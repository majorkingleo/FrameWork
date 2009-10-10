package at.redeye.FrameWork.base.prm.bindtypes;

import at.redeye.FrameWork.base.bindtypes.*;
import at.redeye.FrameWork.base.prm.PrmAttachInterface;
import at.redeye.FrameWork.base.prm.PrmListener;
import at.redeye.FrameWork.base.prm.impl.PrmActionEvent;
import java.util.Vector;


/**
 *
 * @author martin
 */
public class DBConfig extends DBStrukt implements PrmAttachInterface
{    
    public static String TABLENAME = "CONFIG";

    private Vector <PrmListener> prmListeners = new Vector<PrmListener>();
    private String oldValue = "";

    public DBString  name  = new DBString("name", "Name", 100 );
    public DBString  value = new DBString( "value", "Wert", 100 );
    public DBString  descr  = new DBString( "description", "Beschreibung", 250 );
    public DBHistory hist  = new DBHistory("hist");
    
    public DBConfig()
    {
        super(TABLENAME);         
        
        register();
    }                
    
    public DBConfig( String name, String value, String descr )
    {
        super(TABLENAME);
        
        register();
        
        this.name.loadFromString(name);
        this.value.loadFromString(value);
        this.descr.loadFromString(descr);
    }

    public DBConfig( String name, String value )
    {
        super(TABLENAME);
        
        register();
        
        this.name.loadFromString(name);
        this.value.loadFromString(value);        
    }

    
    public boolean differs(DBConfig c_db) {
        if( getConfigName().equals(c_db.getConfigName() ) == false )
            return true;
        if( getConfigValue().equals(c_db.getConfigValue()) == false )
            return true;
        
        return false;
    }
    
    private void register()
    {
        add( name );
        add( value );
        add( descr );
        add( hist );
        
        name.setAsPrimaryKey();        
    }
    
    public String getConfigName()
    {
        return name.getValue().toString();
    }
    
    public String getConfigValue()
    {
        return value.getValue().toString();
    }
    
    public void setConfigValue( String val )
    {
        oldValue = new String (value.toString());
        value.loadFromString(val);
    }             
    
    @Override
    public DBStrukt getNewOne() {
        return new DBConfig();
    }

    public void addPrmListener(PrmListener listener) {
        prmListeners.add(listener);
    }

    public void removePrmListener(PrmListener listener) {
        prmListeners.remove(listener);
    }

    public void updateListeners(PrmActionEvent prmActionEvent) {

        System.out.println ("PRM " + name.toString()+ 
                " has " + prmListeners.size() + " listener(s) registered!");

        for (PrmListener currentListener : prmListeners) {
            currentListener.onChange(prmActionEvent);
        }
    }

    public String getOldValue() {
        return oldValue;
    }

    
}
