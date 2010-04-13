/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.desktoplauncher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author martin
 */
public class CreateDesktopIconKDE extends CreateDesktopIcon
{

    public CreateDesktopIconKDE( String png, String ico, String gif, String app_name, String url, String app_title )
    {
        super( png, ico, gif, app_name, url, app_title );
    }

    @Override
    public boolean createIcon()
    {
        String desktop_dir = System.getProperty("user.home") + File.separator + "Desktop";

        File file = new File( desktop_dir );

        if( !file.isDirectory() )
        {
            logger.error("Directory " + file.getAbsolutePath() + " not existing. Seems not to be a KDE Desktop");
            return false;
        }

        boolean use_gif = true;

        String icon_name;

        if( use_gif )
            icon_name = export_icon(icon_gif);
        else
            icon_name = export_icon(icon_png);

        if( icon_name == null )
            return false;

        String ini_file_name = desktop_dir + File.separator + app_name + ".desktop";

        File ini_file = new File(ini_file_name);

        if( ini_file.exists() )
        {
            logger.info("Datei " + ini_file_name + " already exists. nothing todo");
            return true;
        }

        try
        {
            FileOutputStream fout = new FileOutputStream( ini_file );

            StringBuffer out = new StringBuffer();

            out.append("[Desktop Entry]\n");

            out.append("Icon=");
            out.append(icon_name);
            out.append("\n");

            out.append("Exec=");
            out.append("javaws '");
            out.append(app_url);
            out.append("'\n");

            out.append("Type=Application\nTerminal=false\n");

            out.append("Name=");
            out.append(app_title);
            out.append("\n");

            fout.write(out.toString().getBytes());

            fout.close();

        } catch( IOException ex ) {
            logger.error(ex);
            return false;
        }

        return true;
    }

}
