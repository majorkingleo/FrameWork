/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.dbmanager.DBBindtypeManager;
import at.redeye.FrameWork.base.dbmanager.DBManager;
import at.redeye.UserManagement.UserManagementInterface;

/**
 *
 * @author martin
 */
public abstract class Root {

    public abstract Setup getSetup();
    
    public abstract boolean saveSetup();
    
    public abstract void setDBConnection( DBConnection con );
    public abstract DBConnection getDBConnection();
    public abstract boolean loadDBConnectionFromSetup();
    
    public void informWindowOpened() {}
    public void informWindowClosed() {}
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
            
}
