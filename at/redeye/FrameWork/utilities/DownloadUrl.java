/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.utilities;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class DownloadUrl
{
    protected static Logger logger = Logger.getLogger(DownloadUrl.class.getName());

    URL from;
    File to;

    public DownloadUrl( URL url, File file )
    {
        from = url;
        to = file;
    }

    boolean download()
    {
        File file = null;
        OutputStream out = null;
        BufferedInputStream bis = null;
        InputStream stream = null;

        boolean failed = true;

        try
        {
            file = File.createTempFile( to.getName(), ".part" );

            stream = from.openStream();

            out = new FileOutputStream(file);

            bis = new BufferedInputStream( stream );

            byte[] buf = new byte[1024 * 4];
            int len;

            while ((len = bis.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            File old_one = to;

            boolean success = true;

            if( old_one.exists() )
            {
              if( !old_one.delete() )
              {
                  logger.error("cannot delete file " + to.toString() );
                  success = false;
              }
            }

            if( success )
            {
                if( !file.renameTo(old_one) )
                {
                    logger.error("renaming from " + file.getAbsolutePath() + " to " + to + " failed!");
                    logger.error("trying copying");

                    if( !CopyFile.copy(file, old_one) )
                    {
                        logger.error("Cannot copy file");
                        success = false;
                    }

                    file.delete();
                }
            }

            if( success )
                failed = false;

        } catch( IOException ex ) {

            logger.error(ex);

        } finally {
            try {
                stream.close();
                out.close();
                bis.close();
            } catch( IOException ex ) {
                logger.error(ex);
            }
        }

        return !failed;
    }

    public static boolean downloadUrl( String url, String target )
    {
        URL from = null;

        try
        {
             from = new URL( url );

        } catch( MalformedURLException ex ) {
            System.err.println(ex);
            logger.error(ex);

            return false;
        }

        DownloadUrl durl = new DownloadUrl( from, new File(target) );

        return durl.download();
    }
}
