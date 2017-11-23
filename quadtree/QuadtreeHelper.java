package quadtree;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import java.awt.image.BufferedImage;
import quadtree.models.ImageSection;

/**
 *
 * @author Pedro Bini
 */
public class QuadtreeHelper {

    public static ImagePlus CompressImage(ImagePlus image, int maxDepth, boolean eightBitMode) {
        if (eightBitMode) {
            image = ConvertToGray8(image);
        }

        return GetImageFromQuadtree(image, GetQuadtree(image, maxDepth, eightBitMode), maxDepth, eightBitMode);
    }

    public static boolean IsBlank(ImagePlus image) {
        if (image == null) {
            return true;
        } else {
            ImageProcessor ip = image.getProcessor();
            for (int i = 0; i < ip.getWidth(); i++) {
                for (int j = 0; j < ip.getHeight(); j++) {
                    if (ip.getPixel(i, j) != 255) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public static ImageSection GetQuadtree(ImagePlus image, int maxDepth, boolean eightBitMode) {
        ImagePlus Result = image.duplicate();

        if (!eightBitMode) {
            Result = ConvertToGray8(Result);
        }

        ByteProcessor bp = (ByteProcessor) Result.getProcessor();
        bp.autoThreshold();

        ImageSection Quadtree = RecursiveSplit(new ImageSection(Result), maxDepth, 0);
        return Quadtree;
    }

    public static ImagePlus GetImageFromQuadtree(ImagePlus image, ImageSection Quadtree, int maxDepth, boolean eightBitMode) {
        ImagePlus Result = RecursiveClear(image, Quadtree, maxDepth, 0, eightBitMode);
        return Result;
    }

    private static ImagePlus RecursiveClear(ImagePlus image, ImageSection Quadtree, int maxDepth, int depth, boolean eightBitMode) {
        if (maxDepth > 0 && depth >= maxDepth) {
            return image;
        }

        int Height = image.getHeight();
        int Width = image.getWidth();
        depth++;

        if (Quadtree == null || Quadtree.Image == null || IsBlank(Quadtree.Image)) {
            image = DrawByReference(image, null, 0, Width, 0, Height, eightBitMode);
        } else if (Height > 1 && Width > 1) {
            ImageSection ImageSection = Split(image);

            if (ImageSection != null) {
                if (ImageSection.UpperLeftSection != null) {
                    ImagePlus UpperLeftSection = RecursiveClear(ImageSection.UpperLeftSection.Image, Quadtree.UpperLeftSection, maxDepth, depth, eightBitMode);
                    image = DrawByReference(image, UpperLeftSection, 0, Width / 2, 0, Height / 2, eightBitMode);
                }

                if (ImageSection.UpperRightSection != null) {
                    ImagePlus UpperRightSection = RecursiveClear(ImageSection.UpperRightSection.Image, Quadtree.UpperRightSection, maxDepth, depth, eightBitMode);
                    image = DrawByReference(image, UpperRightSection, Width / 2, Width, 0, Height / 2, eightBitMode);
                }

                if (ImageSection.LowerLeftSection != null) {
                    ImagePlus LowerLeftSection = RecursiveClear(ImageSection.LowerLeftSection.Image, Quadtree.LowerLeftSection, maxDepth, depth, eightBitMode);
                    image = DrawByReference(image, LowerLeftSection, 0, Width / 2, Height / 2, Height, eightBitMode);
                }

                if (ImageSection.LowerRightSection != null) {
                    ImagePlus LowerRightSection = RecursiveClear(ImageSection.LowerRightSection.Image, Quadtree.LowerRightSection, maxDepth, depth, eightBitMode);
                    image = DrawByReference(image, LowerRightSection, Width / 2, Width, Height / 2, Height, eightBitMode);
                }
            }
        }

        image.updateAndDraw();
        return image;
    }

    private static ImagePlus DrawByReference(ImagePlus image, ImagePlus reference, int initialWidth, int finalWidth, int initialHeight, int finalHeight, boolean eightBitMode) {
        ImageProcessor ip = image.getProcessor();
        int X = 0, Y = 0;

        if (!eightBitMode) {
            ip = (ColorProcessor) ip;
        }

        if (reference != null) {
            ImageProcessor ipReference = reference.getProcessor();
            for (int i = initialWidth; i < finalWidth; i++) {
                for (int j = initialHeight; j < finalHeight; j++) {
                    if (eightBitMode) {
                        int Pixel = ipReference.getPixel(X, Y);
                        if (Pixel == 255) {
                            ip.putPixel(i, j, new int[]{255, 255, 255});
                        }
                    } else {
                        int[] ReferenceRGB = new int[]{0, 0, 0};
                        ipReference.getPixel(i, j, ReferenceRGB);

                        int[] RGB = new int[]{0, 0, 0};
                        ip.getPixel(i, j, RGB);

                        // TODO: Draw the rectangle as an average color of itself.
                        ip.putPixel(i, j, new int[]{(ReferenceRGB[0] + RGB[0]) / 2, (ReferenceRGB[1] + RGB[1]) / 2, (ReferenceRGB[2] + RGB[2]) / 2});
                    }

                    Y++;
                }
                Y = 0;
                X++;
            }
        } else {
            for (int i = initialWidth; i < finalWidth; i++) {
                for (int j = initialHeight; j < finalHeight; j++) {
                    if (eightBitMode) {
                        ip.putPixel(i, j, 255);
                    } else {
                        ip.putPixel(i, j, new int[]{255, 255, 255});
                    }
                }
            }
        }

        image.updateAndDraw();
        return image;
    }

    private static ImageSection RecursiveSplit(ImageSection image, int maxDepth, int depth) {
        if (maxDepth > 0 && depth >= maxDepth) {
            return image;
        }

        if (image != null) {
            image = Split(image.Image);
            depth++;

            image.UpperLeftSection = RecursiveSplit(image.UpperLeftSection, maxDepth, depth);
            image.UpperRightSection = RecursiveSplit(image.UpperRightSection, maxDepth, depth);
            image.LowerLeftSection = RecursiveSplit(image.LowerLeftSection, maxDepth, depth);
            image.LowerRightSection = RecursiveSplit(image.LowerRightSection, maxDepth, depth);
        }

        return image;
    }

    private static ImageSection Split(ImagePlus image) {
        ImageSection Result = new ImageSection(image);
        int Height = image.getHeight();
        int Width = image.getWidth();

        if (Height > 1 && Width > 1) {
            Result.setUpperLeftSection(new ImageSection(GetSplitResult(image, 0, Width / 2, 0, Height / 2)), true);
            Result.setUpperRightSection(new ImageSection(GetSplitResult(image, Width / 2, Width, 0, Height / 2)), true);
            Result.setLowerLeftSection(new ImageSection(GetSplitResult(image, 0, Width / 2, Height / 2, Height)), true);
            Result.setLowerRightSection(new ImageSection(GetSplitResult(image, Width / 2, Width, Height / 2, Height)), true);
        }

        return Result;
    }

    private static ImagePlus GetSplitResult(ImagePlus image, int initialWidth, int finalWidth, int initialHeight, int finalHeight) {
        ImageProcessor ipOriginal = image.duplicate().getProcessor();
        int Height = finalHeight - initialHeight;
        int Width = finalWidth - initialWidth;

        ImageProcessor ip = image.getProcessor();
        ip.setInterpolationMethod(ImageProcessor.BILINEAR);
        ip = ip.resize(Width * 2, Height * 2);

        int CropHeight = ip.getHeight() / 2;
        int CropWidth = ip.getWidth() / 2;

        ip.setRoi(CropWidth, CropHeight, Width, Height);
        ImageProcessor ipCropped = ip.crop();
        int X = 0, Y = 0;

        for (int i = initialWidth; i < finalWidth; i++) {
            for (int j = initialHeight; j < finalHeight; j++) {
                int Pixel = ipOriginal.getPixel(i, j);
                ipCropped.putPixel(X, Y, Pixel);
                Y++;
            }
            Y = 0;
            X++;
        }

        BufferedImage croppedImage = ipCropped.getBufferedImage();
        return new ImagePlus("Result", croppedImage);
    }

    private static ImagePlus ConvertToGray8(ImagePlus image) {
        ImageConverter converter = new ImageConverter(image);
        converter.convertToGray8();
        image.updateAndDraw();
        return image;
    }
}
