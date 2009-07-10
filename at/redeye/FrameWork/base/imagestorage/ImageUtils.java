/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package at.redeye.FrameWork.base.imagestorage;

import java.awt.Image;
import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/* Utils.java is used by FileChooserDemo2.java. */
/* Utils.java is used by FileChooserDemo2.java. */
public class ImageUtils {
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";

    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    public static Icon loadScaledImageIcon(String path, int w, int h) {
        ImageIcon icon = createImageIcon(path);
        Image image = scaleImage(icon.getImage(),w,h);
        icon.setImage(image);
        return icon;
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = ImageUtils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    public static ImageIcon loadScaledImageIcon(String path) {

        try {
            java.net.URL imgURL;

            imgURL = new java.net.URL("file:" + path);

            if (imgURL != null) {
                ImageIcon icon =  new ImageIcon(imgURL);
                Image image = icon.getImage();
                image = scaleImage(image,50,50);
                icon.setImage(image);
                return icon;
            } else {
                System.err.println("Couldn't find file: " + path);
                return null;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(ImageUtils.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
    }
    
    public static ImageIcon loadImageIcon(byte bytes[], String descr) 
    {
        ImageIcon image = new ImageIcon(bytes,descr);
        return image;        
    }

    public static ImageIcon loadScaledImageIcon(byte bytes[], String descr, int width, int height) 
    {
        ImageIcon image = loadImageIcon(bytes,descr);
        image.setImage(scaleImage(image.getImage(),width,height));
        return image;
    }
    
    public static ImageIcon loadScaledImageIcon(byte bytes[], String descr) 
    {
        return loadScaledImageIcon(bytes, descr, 50, 50);
    }
    
    public static Image scaleImage(Image image, int width, int height )
    {
        int image_width = image.getWidth(null);
        int image_height = image.getHeight(null);
        
        int scale_width = width;
        int scale_height = height;
        
        if( image_width < width )
            scale_width = image_width;
        
        if( image_height < height )
            scale_height = image_height;
        
        if( image_height < height &&
            image_width < width )
        {
            // do not scale            
            return image;
        }
        
        double ratio = (double)image_width / (double)image_height;
        
        scale_width = width;
        
        scale_height = (int)((double)width / ratio);
        
        image = image.getScaledInstance(scale_width, scale_height, Image.SCALE_SMOOTH);
            
        return image;
    }
}
