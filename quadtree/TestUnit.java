package quadtree;

import ij.ImagePlus;
import ij.process.ImageConverter;

/**
 *
 * @author Pedro Bini
 */
public class TestUnit {

    public static void main(String[] args) {
        ImagePlus img = ij.IJ.openImage();
        ImagePlus compressed = QuadtreeHelper.CompressImage(img.duplicate());

        img.setTitle("Original");
        compressed.setTitle("Compressed");

        img.show();
        compressed.show();
    }
}
