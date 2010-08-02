/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.desktoplauncher;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.Setup;
import at.redeye.FrameWork.utilities.DownloadUrl;
import java.io.File;
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
    String icon_name_gif;
    String jnlp_name;
    String app_title;
    Root   root;

    public static Logger logger = Logger.getLogger(CreateDesktopIcon.class.getName());

    public DesktopLauncher(Root root)
    {

        this.app_name = root.getAppName();
        this.web_start_url = root.getWebStartUrl();
        this.jnlp_name = Setup.getAppConfigFile(app_name, "launch.jnlp");
        this.app_title = root.getAppTitle();
        this.root = root;

        if( app_title == null )
            this.app_title = this.app_name;

        root.addDllExtractorToCache(new JShortcutDLL());
    }

    public static boolean canCreateDesktopIcon()
    {
       return CreateDesktopIcon.isDesktopIconCreatingSupportedByOs();
    }

    public CreateDesktopIcon getInstanceForCreateDesktopIcon( String app_name, String jnlp_name, String app_title )
    {
        return CreateDesktopIcon.getInstance(app_name, jnlp_name, app_title);
    }

    public CreateDesktopIcon getInstanceForCreateDesktopIcon( String png, String ico, String gif, String app_name, String jnlp_name, String app_title )
    {
        return CreateDesktopIcon.getInstance(png, ico, gif,app_name, jnlp_name, app_title);
    }

    public boolean createDesktopIcon()
    {

        CreateDesktopIcon manager = null;

        if( icon_name_ico != null &&
            icon_name_png != null )
        {
             manager = getInstanceForCreateDesktopIcon(icon_name_png, icon_name_ico, icon_name_gif, app_name, jnlp_name, app_title);
        }
        else
        {
            manager = getInstanceForCreateDesktopIcon(app_name, jnlp_name, app_title);
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

    public boolean has_jnlp()
    {
        File file = new File( jnlp_name );

        return file.exists();
    }

    public boolean download_jnlp()
    {
        return DownloadUrl.downloadUrl(web_start_url, jnlp_name);
    }

    public String getJnlpName()
    {
        return jnlp_name;
    }

    public String getAppName()
    {
        return app_name;
    }
}
