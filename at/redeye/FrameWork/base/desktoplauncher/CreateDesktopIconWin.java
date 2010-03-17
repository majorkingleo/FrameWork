/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.desktoplauncher;

import at.redeye.FrameWork.base.Setup;
import at.redeye.FrameWork.utilities.ReadFile;
import java.io.File;
import java.io.IOException;
import net.jimmc.jshortcut.JShellLink;

/**
 *
 * @author martin
 */
public class CreateDesktopIconWin extends CreateDesktopIcon
{
    public CreateDesktopIconWin( String png, String ico, String app_name, String url, String app_title )
    {
        super( png, ico, app_name, url, app_title );
    }

    @Override
    public boolean createIcon()
    {
        JShellLink link = new JShellLink();

        String icon_name = export_icon(icon_ico);

        if( icon_name == null )
            return false;

        logger.error("created Icon: " + icon_name);

        link.setIconLocation(icon_name);
        link.setName(app_title);
        link.setWorkingDirectory(System.getProperty("user.home"));
        link.setFolder(JShellLink.getDirectory("desktop"));

        File ws_exe = new File( "c:/windows/system32/javaws.exe");

        logger.error("exists: " + ws_exe.exists() );
        logger.error("isfile: " + ws_exe.isFile() );
        logger.error("canExec: " + ws_exe.canExecute() );
        logger.error("canread:" +  ws_exe.canRead() );

        boolean can_read = false;
        int bytes = 0;

        try
        {
            byte[] buf = ReadFile.getBytesFromFile(ws_exe);

            bytes = buf.length;

            if( buf.length > 0 )
                can_read = true;
        } catch( IOException ex ) {
            logger.error(ex);
        }

        logger.error("canreally_read: " + can_read + " readed " + bytes + " bytes");
        
        if( !ws_exe.exists() )
        {
            logger.error(ws_exe.getAbsoluteFile() + " not found ");

            if( Setup.is_win_7_system() )
            {
                ws_exe = new File( "c:\\windows\\syswow64\\javaws.exe");
            }

            if( !ws_exe.isFile() )
            {
                logger.error(ws_exe.getAbsoluteFile() + " not found ");

                 ws_exe = new File( System.getProperty("java.home") + "\\bin\\javaws.exe" );
            }
        }

            if( ws_exe.isFile() )
            {
                logger.error("Found javaws at: " + ws_exe.getAbsolutePath());
                link.setPath(ws_exe.getAbsolutePath());
            }
            else
            {
                // hope the best
                logger.error(ws_exe.getAbsoluteFile() + " not found ");
                link.setPath("javaws");
            }

        link.setArguments("\"" + app_url + "\"");

        link.save();

        return true;
    }

}
