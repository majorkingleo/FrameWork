/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.desktoplauncher;

import net.jimmc.jshortcut.JShellLink;

/**
 *
 * @author martin
 */
public class CreateDesktopIconWin extends CreateDesktopIcon
{
    public CreateDesktopIconWin( String png, String ico, String app_name, String url )
    {
        super( png, ico, app_name, url );
    }

    @Override
    public boolean createIcon()
    {
        JShellLink link = new JShellLink();

        String icon_name = export_icon(icon_ico);

        if( icon_name == null )
            return false;

        logger.info("created Icon: " + icon_name);

        link.setIconLocation(icon_name);
        link.setName(app_name);
        link.setWorkingDirectory(System.getProperty("user.home"));
        link.setFolder(JShellLink.getDirectory("desktop"));
        link.setPath("javaws");
        link.setArguments("\"" + app_url + "\"");

        link.save();

        return true;
    }

}
