
package at.redeye.FrameWork.base.imagestorage;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/* ImageFileView.java is used by FileChooserDemo2.java. */
public class ImageFileView extends FileView {

    public String getName(File f) {
        return null; //let the L&F FileView figure this out
    }

    public String getDescription(File f) {
        return null; //let the L&F FileView figure this out
    }

    public Boolean isTraversable(File f) {
        return null; //let the L&F FileView figure this out
    }

    public String getTypeDescription(File f) {
        String extension = ImageUtils.getExtension(f);
        String type = null;

        if (extension != null) {
            if (extension.equals(ImageUtils.jpeg) ||
                extension.equals(ImageUtils.jpg)) {
                type = "JPEG Image";
            } else if (extension.equals(ImageUtils.gif)){
                type = "GIF Image";
            } else if (extension.equals(ImageUtils.tiff) ||
                       extension.equals(ImageUtils.tif)) {
                type = "TIFF Image";
            } else if (extension.equals(ImageUtils.png)){
                type = "PNG Image";
            }
        }
        return type;
    }

    public Icon getIcon(File f) {
        String extension = ImageUtils.getExtension(f);
        Icon icon = null;

        if (extension != null) {
            if (extension.equals(ImageUtils.jpeg) ||
                extension.equals(ImageUtils.jpg)) {
                icon = ImageUtils.loadImageIcon(f.getAbsolutePath());
            } else if (extension.equals(ImageUtils.gif)) {
                icon = ImageUtils.loadImageIcon(f.getAbsolutePath());;
            } else if (extension.equals(ImageUtils.tiff) ||
                       extension.equals(ImageUtils.tif)) {
                icon = ImageUtils.loadImageIcon(f.getAbsolutePath());;
            } else if (extension.equals(ImageUtils.png)) {                
                icon = ImageUtils.loadImageIcon(f.getAbsolutePath());;
            }
        }
        return icon;
    }
}
