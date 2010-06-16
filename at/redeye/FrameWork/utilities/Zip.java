/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.utilities;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author martin
 */
public class Zip
{
    public static void zip(File temp_db_dir, String target_file_name) throws FileNotFoundException, IOException
    {
        zip( temp_db_dir, new File( target_file_name));
    }

    public static void zip( File file_or_dir ) throws FileNotFoundException, IOException
    {
        zip( file_or_dir, new File( file_or_dir.getName() + ".zip" ) );
    }

    public static void zip( File file_or_dir, File zip_name ) throws FileNotFoundException, IOException
    {
        ZipOutputStream z = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zip_name.getAbsolutePath())));

        z.setLevel(9);

        zip( file_or_dir, file_or_dir.getPath(), z);

        z.close();
    }

    protected static void zip( File file_or_dir, String path, ZipOutputStream z) throws FileNotFoundException, IOException
    {
        if( file_or_dir.isDirectory() )
        {
            File files[] =  file_or_dir.listFiles();

            for( File file : files )
            {
                zip( file, path, z );
            }
            return;
        }

        byte[] readBuffer = new byte[2156];
        int bytesIn = 0;

        FileInputStream in = new FileInputStream( file_or_dir );

        String name = file_or_dir.getPath().substring(path.length());

        if( name.startsWith("/") )
            name = name.substring(1);

        name.replace('/', '\\');

        ZipEntry entry = new ZipEntry(name);

        z.putNextEntry(entry);

        while ((bytesIn = in.read(readBuffer)) != -1)
        {
            z.write(readBuffer, 0, bytesIn);
        }

        in.close();
    }

    public static void main( String argv[] )
    {
        try {
            zip(new File("/home/martin/Dropbox"), new File( "/home/martin/ooo3.zip"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Zip.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Zip.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
