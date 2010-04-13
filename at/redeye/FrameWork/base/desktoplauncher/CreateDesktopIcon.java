/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.desktoplauncher;

import at.redeye.FrameWork.base.Setup;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public abstract class CreateDesktopIcon
{
    String icon_png;
    String icon_ico;
    String icon_gif;
    String app_name;
    String app_url;
    String app_title;

    protected static Logger logger = Logger.getLogger(CreateDesktopIcon.class.getName());

    public CreateDesktopIcon( String icon_png, String icon_ico, String icon_gif, String app_name, String url, String app_title )
    {
        this.icon_png = icon_png;
        this.icon_ico = icon_ico;
        this.icon_gif= icon_gif;
        this.app_name = app_name;
        this.app_url = url;
        this.app_title = app_title;
    }

    abstract public boolean createIcon();

    public static CreateDesktopIcon getInstance( String app_name, String url, String app_title )
    {
        return getInstance("/at/redeye/FrameWork/base/resources/icons/icon.png",
                           "/at/redeye/FrameWork/base/resources/icons/icon.ico",
                           "/at/redeye/FrameWork/base/resources/icons/icon.gif",
                           app_name,
                           url,
                           app_title);
    }

    /*
     *  exports the filename to the user homedirectory
     *  and returns the path to the icon
     */
    protected String export_icon( String icon_name )
    {
        InputStream stream = getClass().getResourceAsStream( icon_name );

        if( stream == null )
        {
            logger.error("Cannot load icon " + icon_name);
            return null;
        }

        String extension = ".png";

        if( icon_name.endsWith(".gif") )
            extension = ".gif";

        if( Setup.is_win_system() )
            extension = ".ico";

        String export_path_name = Setup.getAppConfigFile(app_name, app_name + extension);

        try {

            OutputStream out = new FileOutputStream(export_path_name);

            BufferedInputStream bis = new BufferedInputStream( stream );

            byte[] buf = new byte[1024*4];
            int len;

            while( (len = bis.read(buf) ) > 0 )
            {
                out.write(buf,0,len);
            }

            out.close();
            bis.close();
            stream.close();

        } catch (Exception ex) {

           logger.error(ex);
           export_path_name = null;
        }


        return export_path_name;
    }

    public static CreateDesktopIcon getInstance(  String icon_png, String icon_ico, String icon_gif,
                                                  String app_name, String url, String app_title )
    {
        if( Setup.is_win_system() )
            return new CreateDesktopIconWin(icon_png, icon_ico, icon_gif, app_name, url, app_title);
        else if( Setup.is_linux_system() )
            return new CreateDesktopIconKDE(icon_png, icon_ico, icon_gif, app_name, url, app_title);

        return null;
    }

    public static boolean isDesktopIconCreatingSupportedByOs()
    {
        if( getInstance("foo", "bar", "klo")  != null )
            return true;

        return false;
    }
}
