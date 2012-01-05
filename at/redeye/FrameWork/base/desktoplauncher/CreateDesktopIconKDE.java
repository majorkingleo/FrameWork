/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.desktoplauncher;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.translation.MLHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author martin
 */
public class CreateDesktopIconKDE extends CreateDesktopIcon
{
    
    MLHelper ml;

    public CreateDesktopIconKDE( Root root, String png, String ico, String gif, String app_name, String url, String app_title )
    {
        super( root, png, ico, gif, app_name, url, app_title );
        
        if( root != null )
        {
            ml = new MLHelper(root);
            ml.autoLoadFile4Class(this, root.getDisplayLanguage(), "en" );
        }
        setCommand( "javaws '" +  app_url + "'" );
    }
    
    @Override
    public boolean createIcon()
    {
        File file = getDesktopDir();

        if( file == null || !file.isDirectory() )
        {
            logger.error("Cannot find a Desktop dir. Seems not to be a KDE Desktop");
            return false;
        }

        boolean use_gif = false;

        String icon_name;

        if( use_gif )
            icon_name = export_icon(icon_gif);
        else
            icon_name = export_icon(icon_png);

        if( icon_name == null )
            return false;

        String ini_file_name = file.toString() + File.separator + app_name + ".desktop";

        File ini_file = new File(ini_file_name);

        if( ini_file.exists() )
        {
            logger.info("Datei " + ini_file_name + " already exists. nothing todo");
            return true;
        }

        try
        {
            FileOutputStream fout = new FileOutputStream( ini_file );

            StringBuilder out = new StringBuilder();
            
            out.append("[Desktop Entry]\n");

            out.append("Icon=");
            out.append(icon_name);
            out.append("\n");

            out.append("Exec=");
            out.append(getCommand());
            out.append("\n");

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

    boolean hasKDE()
    {
        String kde_versions[] = { ".kde", ".kde2", ".kde3", ".kde4" , ".kde5"};
        
        for( String kde_version : kde_versions ) {
            File file = new File(System.getProperty("user.home") + File.separator + kde_version);
        
            if( file.isDirectory() )
                return true;
        }
        
        return false;
    }
    
    File getDesktopDir()
    {
        if( !hasKDE() )
            return null;

        ArrayList<String> desktop_dirs = new ArrayList();

        desktop_dirs.add(System.getProperty("user.home") + File.separator + "Desktop");
        desktop_dirs.add(System.getProperty("user.home") + File.separator + ml.MlM("Desktop"));

        for (String desktop_dir : desktop_dirs) {
            File file = new File(desktop_dir);

            if (file.isDirectory()) {                
                return file;
            }
            else
            {
                logger.error("Directory " + file.getAbsolutePath() + " not existing. Seems not to be a KDE Desktop");
            }
        }
        
        return null;
    }
}
