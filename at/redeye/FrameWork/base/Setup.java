/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import at.redeye.FrameWork.base.prm.bindtypes.DBConfig;
import java.io.File;
import org.apache.log4j.Logger;



/**
 *
 * @author martin
 */
public abstract class Setup {
    
    static public String DBType = "DBType";
    static public String DBHost = "DBHost";
    static public String DBInstance = "DBInstance";
    static public String DBPort = "DBPort";
    static public String DBUser = "DBUser";
    static public String DBPasswd = "DBPasswd";
    static public String DBDatabase = "DBDatabase";
    static public String H1IPAddress = "H1IPAddress";
    static public String H1Port = "H1Port";
    static public String H1LTSAP = "H1LTSAP";
    static public String H1RTSAP = "H1RTSAP";
    
    static public String WindowX = "WindowX";
    static public String WindowY = "WindowY";
    static public String WindowWidth = "WindowWidth";
    static public String WindowHeight = "WindowHeight";

    public static Logger logger = Logger.getLogger(Setup.class);
    
    public static boolean is_win_system()
    {
        return System.getProperty("os.name").matches(".*[Ww][Ii][Nn].*");
    }

    public static boolean is_win_7_system()
    {
        return System.getProperty("os.name").matches("Windows 7");
    }

    public static boolean is_linux_system()
    {
        return System.getProperty("os.name").equals("Linux");
    }

    public static String getHiddenUserHomeFileName( String name )
    {
        String config_path = System.getProperty("user.home");

        if( !is_win_system() )
        {
           name = "." + name;
        }

        String config_file = config_path + File.separator + name;

        return config_file;
    }

    public static String getAppConfigDir( String app_name )
    {
        String name = getHiddenUserHomeFileName( app_name );

        File file = new File(name);

        if( !file.exists() )
        {
            if( !file.mkdirs() )
            {
                logger.error("failed createing directory" + name + " !!!");
                name = null;
            }
        }
        return name;
    }

    public static String getAppConfigFile( String app_name, String file_name )
    {
        String dir = getAppConfigDir(app_name);
        String file_abs_name = dir + File.separator + file_name;

        return file_abs_name;
    }

    public String getConfig(DBConfig config) {
        return getConfig( config.getConfigName(), config.getConfigValue() );
    }
    
    public String getLocalConfig(DBConfig config) {
        return getLocalConfig( config.getConfigName(), new String(config.getConfigValue()) );
    }
    
    public abstract String getLocalConfig( String key, String default_value );
    
    public abstract String getConfig( String key, String default_value );

    public abstract DBConfig getConfig( String key);

    public abstract DBConfig getLocalConfig(String key);
    
    public abstract void setLocalConfig( String key, String value, boolean if_not_exists );
    
    public abstract void setLocalConfig( String key, String value );
        
    public abstract void setConfig( String key, String value, boolean if_not_exists );
    
    public abstract void setConfig( String key, String value );
    
    public void saveConfig() {}
    
    public boolean initialRun()
    {
        return false;
    }
}
