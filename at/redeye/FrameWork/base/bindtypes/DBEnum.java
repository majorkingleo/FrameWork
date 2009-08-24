/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.bindtypes;

import at.redeye.SqlDBInterface.SqlDBIO.impl.MOMMDBDataType;

import java.util.Vector;

/**
 *
 * @author martin
 */
public class DBEnum extends DBValue {

    public static abstract class EnumHandler
    {
        public abstract int getMaxSize();
        public abstract boolean setValue( String val );
        public abstract String getValue();
        public abstract EnumHandler getNewOne();
        public abstract Vector<String> getPossibleValues();  
    }
    
    public EnumHandler handler; 
    
    public DBEnum( String name, EnumHandler enumval )
    {
        super( name );
        handler = enumval;
    }
    
    public DBEnum( String name, String title, EnumHandler enumval )
    {
        super( name, title );
        handler = enumval;
    }
    
    @Override
    public MOMMDBDataType getDBType() {
        return MOMMDBDataType.DB_TYPE_STRING;
    }

    @Override
    public void loadFromDB(Object obj) {
        handler.setValue((String)obj);
    }

    @Override
    public void loadFromString(String s) {
        handler.setValue(s);
    }

    @Override
    public boolean acceptString(String s) {
       return handler.setValue(s);
    }

    @Override
    public void loadFromCopy(Object obj) {
       handler = handler.getNewOne();
       handler.setValue((String)obj);
    }

    @Override
    public String getValue() {
        return handler.getValue();
    }

    @Override
    public DBValue getCopy() {
        DBEnum copy = new DBEnum(name, handler.getNewOne() );
        copy.handler.setValue(handler.getValue());
        return copy;
    }

    public int getMaxLen()
    {
        return handler.getMaxSize();
    }
    
    @Override
    public String toString()
    {
        return handler.getValue();
    }
    
    public Vector<String> getPossibleValues()
    {
        return handler.getPossibleValues();
    }
}
