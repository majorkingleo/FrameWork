/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.desktoplauncher;

import java.io.File;

import net.jimmc.jshortcut.JShellLink;


/**
 *
 * @author martin
 */
public class CreateDesktopIconWin extends CreateDesktopIcon
{
    public CreateDesktopIconWin( String png, String ico, String gif, String app_name, String url, String app_title )
    {
        super( png, ico, gif, app_name, url, app_title );

         setCommand( "javaws " +  app_url + "\"" );
    }
    
    private String findExeOfCommand()
    {
        String exe = getCommand();
        int index = exe.indexOf(" ");
        
        if( index >= 0 )
        {
            exe = exe.substring(0,index) + ".exe";
        }        

        String cmd = null;

        if( ( cmd = commandExists( "c:/windows/system32/" + exe ) ) != null )
        {
            return cmd;
        }
        else if( ( cmd = commandExists( "c:\\windows\\syswow64\\" + exe ) ) != null )
        {
            return cmd;
        }
        else if( ( cmd = commandExists( System.getProperty("java.home") + "\\bin\\" + exe ) ) != null )
        {
            return cmd;
        } 
        
        return exe;
    }

    @Override
    public boolean createIcon()
    {
        JShellLink link = new JShellLink();

        String icon_name = export_icon(icon_ico);

        if( icon_name == null )
            return false;

        //icon_name = icon_name.replaceAll("\\\\", "/");

        logger.error("created Icon: " + icon_name);        

        link.setIconLocation(icon_name);
        link.setName(app_title);
        link.setWorkingDirectory(System.getProperty("user.home"));
        link.setFolder(JShellLink.getDirectory("desktop"));

        link.setPath(findExeOfCommand());

        link.setArguments(getArguments());


        link.save();

        return true;
    }

    private String getArguments()
    {
        String cmd = getCommand();

        int index = cmd.indexOf(" ");

        if( index >= 0 )
        {
            cmd = cmd.substring(index+1);
        }

        return cmd.replaceAll("'", "\"");
    }

    private String commandExists(String string) {

        File fcmd = new File( string );

        if( fcmd.canExecute() ) {
            logger.info("command " + string + " found at " + fcmd.getAbsolutePath() );
            return fcmd.getAbsolutePath();
        }

        logger.info("command " + string + " not found ");

        return null;
    }

}
