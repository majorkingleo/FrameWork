/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.imagestorage;

import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author martin
 */
public class ImageFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter 
{

     private static final String[] endings = 
        {
            ".jpg",
            ".jpeg",
            ".png",
            ".gif",
            ".bmp"
        };
    
    public boolean accept(File file) 
    {
        if( file.isDirectory() )
            return true;
        
        if( !file.isFile() )
            return false;
                
        String name = file.getName().toLowerCase();
        
        for( int i = 0; i < endings.length; i++ )
            if( name.endsWith(endings[i]) )
                return true;
        
        return false;
    }

    @Override
    public String getDescription() {
        return "Bilder";
    }

}
