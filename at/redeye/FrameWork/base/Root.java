/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.dbmanager.DBBindtypeManager;
import at.redeye.FrameWork.base.dbmanager.DBManager;
import at.redeye.UserManagement.UserManagementInterface;
import javax.swing.JFrame;

/**
 *
 * @author martin
 */
public abstract class Root {

    String app_name;
    String web_start_url;

    public Root( String app_name )
    {
        this.app_name = app_name;
    }

    public abstract Setup getSetup();
    
    public abstract boolean saveSetup();
    
    public abstract void setDBConnection( DBConnection con );
    public abstract DBConnection getDBConnection();
    public abstract boolean loadDBConnectionFromSetup();
    
    public void informWindowOpened( JFrame dlg ) {}
    public void informWindowClosed( JFrame dlg ) {}
    public void closeAllWindowsExceptThisOne( JFrame dlg ) {}
    public void closeAllWindowsNoAppExit() {}

    public void appExit() {}
    
    public void setAktivUser( DBStrukt pb )
    { 
    
    }
    
    public int getUserPermissionLevel()
    {
        return UserManagementInterface.UM_PERMISSIONLEVEL_ADMIN;
    }
    
    public String getUserName()
    {
        return "";
    }
    
    public String getLogin()
    {
        return "";
    }

    public DBBindtypeManager getBindtypeManager()
    {
        return null;
    }        
    
    public DBManager getDBManager()
    {
        return null;
    }
    
    public int getUserId()
    {
        return 0;
    }

    public String getAppName()
    {
        return app_name;
    }

    public void setWebStartUlr(String url)
    {
        web_start_url = url;
    }

    public String getWebStartUrl()
    {
        return web_start_url;
    }
            
}
