/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.desktoplauncher;

import at.redeye.FrameWork.base.Setup;
import at.redeye.FrameWork.utilities.CopyFile;
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
public class DesktopLauncher
{
    String app_name;
    String web_start_url;
    String icon_name_ico;
    String icon_name_png;
    String jnlp_name;

    protected static Logger logger = Logger.getLogger(CreateDesktopIcon.class.getName());

    public DesktopLauncher( String app_name, String web_start_url,String icon_name_ico, String icon_name_png )
    {
        this.app_name = app_name;
        this.web_start_url = web_start_url;
        this.icon_name_ico = icon_name_ico;
        this.icon_name_png = icon_name_png;
        this.jnlp_name = Setup.getAppConfigFile(app_name, "launch.jnlp");
    }

    public DesktopLauncher( String app_name, String web_start_url )
    {
        this.app_name = app_name;
        this.web_start_url = web_start_url;
        this.jnlp_name = Setup.getAppConfigFile(app_name, "launch.jnlp");
    }

    public static boolean canCreateDesktopIcon()
    {
       return CreateDesktopIcon.isDesktopIconCreatingSupportedByOs();
    }

    public boolean createDesktopIcon()
    {

        CreateDesktopIcon manager = null;

        if( icon_name_ico != null &&
            icon_name_png != null )
        {
             manager = CreateDesktopIcon.getInstance(icon_name_png, icon_name_ico, app_name, jnlp_name);
        }
        else
        {
            manager = CreateDesktopIcon.getInstance(app_name, jnlp_name);
        }

        if( !has_jnlp() )
        {
            if( !download_jnlp() )
                return false;
        }

        if( !manager.createIcon() )
            return false;

        return true;
    }

    private boolean has_jnlp()
    {
        File file = new File( jnlp_name );

        return file.exists();
    }

    public boolean download_jnlp()
    {
        URL url;
        
        try
        {        
            url = new URL( web_start_url );
        }
        catch( MalformedURLException ex )
        {
            logger.error("invalid Url: " + web_start_url );
            logger.error(ex);
            return false;
        }

        File file = null;
        OutputStream out = null;
        BufferedInputStream bis = null;
        InputStream stream = null;

        boolean failed = true;

        try
        {
            file = File.createTempFile( "launch-", ".part" );

            stream = url.openStream();

            out = new FileOutputStream(file);

            bis = new BufferedInputStream( stream );

            byte[] buf = new byte[1024 * 4];
            int len;

            while ((len = bis.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            File old_one = new File( jnlp_name );

            boolean success = true;

            if( old_one.exists() )
            {
              if( !old_one.delete() )
              {
                  logger.error("cannot delete file " + jnlp_name );
                  success = false;
              }
            }

            if( success )
            {
                if( !file.renameTo(old_one) )
                {
                    logger.error("renaming from " + file.getAbsolutePath() + " to " + jnlp_name + " failed!");
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
}
