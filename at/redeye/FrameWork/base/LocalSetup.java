/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import at.redeye.FrameWork.base.bindtypes.DBConfig;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.transaction.Transaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author martin
 */
public class LocalSetup extends Setup {
    
    String config_name;
    String config_path;
    String config_file;
    String app_name;
    Properties props;
    Root root;    
    HashMap<String,DBConfig> global_config = null;
    
    public LocalSetup( Root root, String app_name )
    {                
        this.app_name = app_name;
        this.root = root;
        config_name = app_name + ".properties";
        
        config_path = System.getProperty("user.home");
        
        if( !is_win_system() )
        {
           config_name = "." + config_name;    
        }
        
        config_file = config_path + File.separator + config_name;  
        check();
                
    }
    
    private void check()
    {
        if( props == null )
        {
            loadProps();
        }
            
    }
    
    private boolean checkGlobal()
    {
        if( global_config == null ) {
            
            if( loadGlobalProps() ) {
                return true;
            }
            
            return false;
        }
        
        return true;
    }
    
    public boolean loadProps()
    {
        props = new Properties();
        
        try {
            FileInputStream in = new FileInputStream(config_file);
            props.load(in);
            in.close();
            
        } catch( FileNotFoundException e ) {
            
            return true;
            
        } catch(IOException ioe) {
        
            System.err.println("Unhandled exception:");
            ioe.printStackTrace();
            return false;
        }         
        
        return true;
    }        

    public boolean saveProps()
    {       
        try {
            FileOutputStream out = new FileOutputStream(config_file);
            props.store(out, "nix");
            out.close();
            
        } catch(IOException ioe) {
        
            System.err.println("Unhandled exception:");
            ioe.printStackTrace();
            return false;
        }               
         
        return true;
    }
    
    public boolean loadGlobalProps()
    {
        final DBConnection conn = root.getDBConnection();
        
        if( conn == null )
            return false;
        
        AutoLogger al = new AutoLogger("LocalSetup")
        {

            @Override
            public void do_stuff() throws Exception {
                
                result = new Boolean(false);
                
                Transaction trans = conn.getNewTransaction();
        
                Vector<DBStrukt> all = trans.fetchTable(new DBConfig() );
                
                for( int i = 0; i < all.size(); i++ )
                {
                    DBConfig c = (DBConfig)all.get(i);
                    
                    if( global_config == null )
                        global_config = new HashMap<String,DBConfig>();
                    
                    global_config.put(c.getConfigName(), c);
                }                                
                                
                conn.closeTransaction(trans);
                
                if( global_config == null )
                {
                    // Noch kein Eintrag in der DB vorhanden...
                    global_config = new HashMap<String,DBConfig>();
                }
                
                result = new Boolean(true);
            }
            
        };
                
        return (Boolean)al.result;
    }
    
    public boolean saveGlobalProps()
    {
        if( global_config == null )
            return false;
        
        final DBConnection conn = root.getDBConnection();
        
        if( conn == null )
            return false;
        
        AutoLogger al = new AutoLogger("LocalSetup")
        {

            @Override
            public void do_stuff() throws Exception {
                result = new Boolean(false);
                                
                Transaction trans = conn.getNewTransaction();
                                                
                Set<String> keys = global_config.keySet();
                
                for( String key : keys )
                {                                       
                    DBConfig c = global_config.get(key);
                    
                    insertOrUpdateValue(trans, c);
                }
                
                trans.commit();                  
                conn.closeTransaction(trans);
                
                result = new Boolean(true);
            }

            private void insertOrUpdateValue(Transaction trans, DBConfig c) throws Exception 
            {
                DBConfig c_db = new DBConfig();
                
                c_db.name.loadFromString(c.getConfigName());
                
                if( trans.fetchTableWithPrimkey(c_db) == true )
                {
                    if( c.differs(c_db) ) {
                        c.hist.setAeHist(root.getUserName());
                        trans.updateValues(c);
                    }
                } else {
                    c.hist.setAnHist(root.getUserName());
                    trans.insertValues(c);
                }                    
            }
        };
        
        return (Boolean)al.result;
    }
    
    @Override
    public String getLocalConfig(String key, String default_value) {
        check();
        return props.getProperty(key, default_value);
    }

    @Override
    public String getConfig(String key, String default_value) {
        
        if( !checkGlobal() )
            return default_value;
        
        DBConfig c = global_config.get(key);
        
        if( c != null )
            return c.getConfigValue();
        
        c = GlobalConfigDefinitions.get(key);
        
        if( c != null )
        {
            global_config.put(key, c);
            return c.getConfigValue();
        }
        
        c = new DBConfig( key, default_value );
        global_config.put(key, c);
        
        return default_value;
    }    

    @Override
    public void setLocalConfig(String key, String value, boolean only_if_not_exists) {
        
        check();
        
        if( props.getProperty(key) == null && only_if_not_exists )
        {
            props.setProperty(key, value);
            
        } else if( !only_if_not_exists ) {
            
            props.setProperty(key, value);
        }
    }

    @Override
    public void setLocalConfig( String key, String value ) {
        setLocalConfig( key, value, false );
    }

    @Override
    public void setConfig(String key, String value, boolean if_not_exists) {
        
        if( !checkGlobal() ) {
            return;
        }
            
        
        DBConfig c = global_config.get(key);
                
        if( c != null && if_not_exists == true )
            return;
            
        if( c == null )
        {
            c = GlobalConfigDefinitions.get(key);
            
            if( c != null )
            {
                c.setConfigValue(value);
                global_config.put(key, c);
                return;
            }
            
            c = new DBConfig(key,value);
            global_config.put(key, c);
            return;
            
        } else {
            
            c.setConfigValue(value);
        }
    }

    @Override
    public void setConfig(String key, String value) {
        setConfig(key,value,false);
    }

    public void saveConfig()
    {
        saveProps();
        saveGlobalProps();
    }        
}
