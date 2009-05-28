/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import at.redeye.FrameWork.base.bindtypes.DBConfig;


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
    
    public static boolean is_win_system()
    {
        return System.getProperty("os.name").matches(".*[Ww][Ii][Nn].*");
    }

    public String getConfig(DBConfig config) {
        return getConfig( config.getConfigName(), config.getConfigValue() );
    }
    
    public String getLocalConfig(DBConfig config) {
        return getLocalConfig( config.getConfigName(), new String(config.getConfigValue()) );
    }
    
    public abstract String getLocalConfig( String key, String default_value );
    
    public abstract String getConfig( String key, String default_value );
    
    public abstract void setLocalConfig( String key, String value, boolean if_not_exists );
    
    public abstract void setLocalConfig( String key, String value );
        
    public abstract void setConfig( String key, String value, boolean if_not_exists );
    
    public abstract void setConfig( String key, String value );
    
    public void saveConfig() {}
}
