package quadtree;

import ij.ImagePlus;

/**
 *
 * @author Pedro Bini
 */
public class TestUnit {

    public static void main(String[] args) {
        //Test_Lena();        

        //Test_Feynman();
        //Test_PickImage(false);
    }

    public static void Test_PickImage(boolean eightBitMode) {
        ImagePlus image = ij.IJ.openImage();
        if (image != null) {
            ShowQuadtreeResult(image, 6, eightBitMode);
            ShowQuadtreeResult(image, 7, eightBitMode);
            ShowQuadtreeResult(image, 8, eightBitMode);
            ShowQuadtreeResult(image, 9, eightBitMode);
            ShowQuadtreeResult(image, 0, eightBitMode);
            image.setTitle("Original");
            image.show();
        }
    }

    public static void Test_Feynman() {
        ImagePlus image = new ImagePlus("src/quadtree/images/feynman.jpg");
        if (image != null) {
            ShowQuadtreeResult(image, 6, false);
            ShowQuadtreeResult(image, 7, false);
            ShowQuadtreeResult(image, 8, false);
            ShowQuadtreeResult(image, 9, false);
            ShowQuadtreeResult(image, 0, false);
            image.setTitle("Original");
            image.show();
        }
    }

    public static void Test_Lena() {
        ImagePlus image = new ImagePlus("src/quadtree/images/lena_gray.bmp");
        if (image != null) {
            ShowQuadtreeResult(image, 6, true);
            ShowQuadtreeResult(image, 7, true);
            ShowQuadtreeResult(image, 8, true);
            ShowQuadtreeResult(image, 9, true);
            ShowQuadtreeResult(image, 0, true);
            image.setTitle("Original");
            image.show();
        }
    }

    public static void ShowQuadtreeResult(ImagePlus image, int maxDepth, boolean eightBitMode) {
        ImagePlus quadtreeResult = QuadtreeHelper.CompressImage(image.duplicate(), maxDepth, eightBitMode);
        quadtreeResult.setTitle("Quadtree Result - Depth: " + (maxDepth > 0 ? maxDepth : "Max") + (eightBitMode ? " (8-bit)" : ""));
        quadtreeResult.show();
    }
}
