package quadtree.models;

import ij.ImagePlus;
import java.util.ArrayList;
import quadtree.QuadtreeHelper;

/**
 *
 * This class represents a section of an image, as used in the separation method
 * by QuadtreeHelper class.
 *
 * @author Pedro Bini
 */
public class ImageSection {

    public ImageSection(ImagePlus image) {
        UpperLeftSection = null;
        UpperRightSection = null;
        LowerLeftSection = null;
        LowerRightSection = null;
        Image = image;
    }

    public ImagePlus Image;

    public ImageSection UpperLeftSection;
    public ImageSection UpperRightSection;
    public ImageSection LowerLeftSection;
    public ImageSection LowerRightSection;
    
    public void ShowQuadtree(){
        if (UpperLeftSection != null) {
            UpperLeftSection.ShowQuadtree();
        }
        
        if (UpperRightSection != null) {
            UpperRightSection.ShowQuadtree();
        }
        
        if (LowerLeftSection != null) {
            LowerLeftSection.ShowQuadtree();
        }
        
        if (LowerRightSection != null) {
            LowerRightSection.ShowQuadtree();
        }
        
        Image.show();
    }

    public ImagePlus getImage() {
        return Image;
    }

    public ImageSection getUpperLeftSection() {
        return UpperLeftSection;
    }

    public ImageSection getUpperRightSection() {
        return UpperRightSection;
    }

    public ImageSection getLowerLeftSection() {
        return LowerLeftSection;
    }

    public ImageSection getLowerRightSection() {
        return LowerRightSection;
    }

    
    
    public void setImage(ImagePlus Image) {
        this.Image = Image;
    }

    public void setUpperLeftSection(ImageSection UpperLeftSection, boolean IgnoreBlank) {
        if (IgnoreBlank && QuadtreeHelper.IsBlank(UpperLeftSection.Image)) {
            return;
        }

        this.UpperLeftSection = UpperLeftSection;
    }

    public void setUpperRightSection(ImageSection UpperRightSection, boolean IgnoreBlank) {
        if (IgnoreBlank && QuadtreeHelper.IsBlank(UpperRightSection.Image)) {
            return;
        }

        this.UpperRightSection = UpperRightSection;
    }

    public void setLowerLeftSection(ImageSection LowerLeftSection, boolean IgnoreBlank) {
        if (IgnoreBlank && QuadtreeHelper.IsBlank(LowerLeftSection.Image)) {
            return;
        }

        this.LowerLeftSection = LowerLeftSection;
    }

    public void setLowerRightSection(ImageSection LowerRightSection, boolean IgnoreBlank) {
        if (IgnoreBlank && QuadtreeHelper.IsBlank(LowerRightSection.Image)) {
            return;
        }

        this.LowerRightSection = LowerRightSection;
    }

}
