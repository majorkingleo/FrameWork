/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.dbmanager.DBBindtypeManager;
import at.redeye.FrameWork.base.dbmanager.DBManager;
import at.redeye.FrameWork.base.dbmanager.impl.DatabaseManager;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.ConnectionDefinition;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MOMMSupportedDBMSTypes;
import at.redeye.UserManagement.UserManagementInterface;
import at.redeye.UserManagement.bindtypes.DBPb;
import java.util.Vector;
import javax.swing.JFrame;

/**
 *
 * @author martin
 */
public class LocalRoot extends Root {

    protected LocalSetup setup;
    protected DBConnection db_connection;    
    protected DBPb userEntry=null;
    protected DBManager dbmanager=null;
    protected Vector<JFrame> dialogs = new Vector<JFrame>();
    protected boolean appExitAllowed = true;
    
    public LocalRoot( String app_name )
    {
        setup = new LocalSetup( this, app_name );      
        dbmanager=new DatabaseManager();
    }
    
    @Override
    public Setup getSetup() {
        return setup;
    }

    @Override
    public boolean saveSetup() {
        if( setup.saveProps() )
            return setup.saveGlobalProps();
        
        return false;
    }      

    public void setDBConnection( DBConnection con )
    {
        if( db_connection != null )
            db_connection.close();
        
        db_connection = con;
    }
    
    public DBConnection getDBConnection()
    {
        return db_connection;
    } 
    
    public void closeDBConnection()
    {
        setDBConnection(null);
    }
    
    public boolean loadDBConnectionFromSetup()
    {
        String database = setup.getLocalConfig(Setup.DBDatabase, "");
        String host = setup.getLocalConfig(Setup.DBHost, "");
        String user = setup.getLocalConfig(Setup.DBUser, "");
        String passwd = setup.getLocalConfig(Setup.DBPasswd, "");
        MOMMSupportedDBMSTypes dbtype = MOMMSupportedDBMSTypes.valueOf(setup.getLocalConfig(Setup.DBType, MOMMSupportedDBMSTypes.DB_MYSQL.toString()));
        String instance = setup.getLocalConfig(Setup.DBInstance, "");
        String sport = setup.getLocalConfig(Setup.DBPort, "0");
        
        int port = 0;
        
        if( !sport.isEmpty() )
            port = Integer.parseInt(sport);
        
        if( dbtype == MOMMSupportedDBMSTypes.DB_ORACLE )
            database = instance;
        
         ConnectionDefinition connparams = new ConnectionDefinition(
                    host,
                    port,
                    user,
                    passwd,                    
                    database,
                    dbtype
                    );
         
         DBConnection con = new DBConnection();
         
         if( con.open(connparams) )
         {
             setDBConnection( con );                          
             return true;
         }
         
         return false;
    }
    
    @Override
    public void informWindowOpened( JFrame dlg )
    {
        dialogs.add(dlg);
    }
    
    @Override
    public void informWindowClosed( JFrame dlg )
    {
        dialogs.remove(dlg);
        
        if( dialogs.size() <= 0 )
        {
            if( appExitAllowed )
            {
                System.out.println("All Windows closed, normal exit" );
                appExit();
            }
        }
    }

    @Override
    public void closeAllWindowsNoAppExit()
    {
        appExitAllowed = false;
        closeAllWindowsExceptThisOne(null);
        appExitAllowed = true;
    }

    @Override
    public void closeAllWindowsExceptThisOne( JFrame dlg )
    {
        Vector<JFrame> dlgs = new Vector<JFrame>();
        dlgs.addAll(dialogs);

        for( JFrame frame : dlgs )
        {
            if( frame != dlg )
            {
                if( frame instanceof BaseDialog )
                {
                    BaseDialog base_dialog = (BaseDialog) frame;
                    base_dialog.closeNoAppExit();
                }
                else
                {
                    frame.dispose();
                    dialogs.remove(frame);
                }
            }
        }
    }
    
    @Override
    public void appExit()
    {
        saveSetup();
        closeDBConnection();        
        System.exit(0);
    }
    
    @Override
    public void setAktivUser( DBStrukt pb )
    {
        if( DBPb.class.isInstance(pb) )
        {
            userEntry = (DBPb) pb;
        }
    }
    
    @Override
    public String getLogin()
    {
        if( userEntry == null )
            return "";
        
        return userEntry.login.toString();
    }
    
    @Override
    public String getUserName()
    {
        if( userEntry == null )
            return "";
        
        return userEntry.getUserName();
    }
    
    @Override
    public int getUserPermissionLevel()
    {
        if( userEntry == null )
            return UserManagementInterface.UM_PERMISSIONLEVEL_ADMIN;
        
        return (Integer)userEntry.plevel.getValue();
    }
            
    @Override
    public DBBindtypeManager getBindtypeManager()
    {
        return (DBBindtypeManager) dbmanager;
    }        
    
    @Override
    public DBManager getDBManager()
    {
        return dbmanager;
    }        
    
    @Override
    public int getUserId()
    {
        if( userEntry == null )
            return 0;
        
        return (Integer)userEntry.id.getValue();
    }
}
