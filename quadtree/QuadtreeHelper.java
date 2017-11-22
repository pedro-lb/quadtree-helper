package quadtree;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import java.awt.image.BufferedImage;
import quadtree.models.ImageSection;

/**
 *
 * @author Pedro Bini
 */
public class QuadtreeHelper {

    public static ImagePlus CompressImage(ImagePlus image) {
        ImageConverter conv = new ImageConverter(image);
        conv.convertToGray8();
        image.updateAndDraw();
        
        return GetImageFromQuadtree(image, GetQuadtree(image));
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

    public static ImageSection GetQuadtree(ImagePlus image) {
        ImagePlus Result = image.duplicate();

        ByteProcessor bp = (ByteProcessor) Result.getProcessor();
        bp.autoThreshold();

        ImageSection Quadtree = RecursiveSplit(new ImageSection(Result));
        return Quadtree;
    }

    public static ImagePlus GetImageFromQuadtree(ImagePlus image, ImageSection Quadtree) {
        ImagePlus Result = RecursiveClear(image, Quadtree);
        return Result;
    }

    private static ImagePlus RecursiveClear(ImagePlus image, ImageSection Quadtree) {
        int Height = image.getHeight();
        int Width = image.getWidth();

        if (Quadtree == null || Quadtree.Image == null || IsBlank(Quadtree.Image)) {
            image = DrawByReference(image, null, 0, Width, 0, Height);

        } else if (Height > 1 && Width > 1) {
            ImageSection ImageSection = Split(image);

            if (ImageSection != null) {
                if (ImageSection.UpperLeftSection != null) {
                    ImagePlus UpperLeftSection = RecursiveClear(ImageSection.UpperLeftSection.Image, Quadtree.UpperLeftSection);
                    image = DrawByReference(image, UpperLeftSection, 0, Width / 2, 0, Height / 2);
                }

                if (ImageSection.UpperRightSection != null) {
                    ImagePlus UpperRightSection = RecursiveClear(ImageSection.UpperRightSection.Image, Quadtree.UpperRightSection);
                    image = DrawByReference(image, UpperRightSection, Width / 2, Width, 0, Height / 2);
                }

                if (ImageSection.LowerLeftSection != null) {
                    ImagePlus LowerLeftSection = RecursiveClear(ImageSection.LowerLeftSection.Image, Quadtree.LowerLeftSection);
                    image = DrawByReference(image, LowerLeftSection, 0, Width / 2, Height / 2, Height);
                }

                if (ImageSection.LowerRightSection != null) {
                    ImagePlus LowerRightSection = RecursiveClear(ImageSection.LowerRightSection.Image, Quadtree.LowerRightSection);
                    image = DrawByReference(image, LowerRightSection, Width / 2, Width, Height / 2, Height);
                }
            }
        }

        image.updateAndDraw();
        return image;
    }

    private static ImagePlus DrawByReference(ImagePlus image, ImagePlus reference, int initialWidth, int finalWidth, int initialHeight, int finalHeight) {
        ImageProcessor ip = image.getProcessor();
        int X = 0, Y = 0;

        if (reference != null) {
            ImageProcessor ipReference = reference.getProcessor();
            for (int i = initialWidth; i < finalWidth; i++) {
                for (int j = initialHeight; j < finalHeight; j++) {
                    int Pixel = ipReference.getPixel(X, Y);
                    if (Pixel == 255) {
                        ip.putPixel(i, j, Pixel);
                    }
                    Y++;
                }
                Y = 0;
                X++;

            }
        } else {
            for (int i = initialWidth; i < finalWidth; i++) {
                for (int j = initialHeight; j < finalHeight; j++) {
                    ip.putPixel(i, j, 255);
                }
            }
        }

        image.updateAndDraw();
        return image;
    }

    private static ImageSection RecursiveSplit(ImageSection image) {
        if (image != null) {

            image = Split(image.Image);
            image.UpperLeftSection = RecursiveSplit(image.UpperLeftSection);
            image.UpperRightSection = RecursiveSplit(image.UpperRightSection);
            image.LowerLeftSection = RecursiveSplit(image.LowerLeftSection);
            image.LowerRightSection = RecursiveSplit(image.LowerRightSection);

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
}
