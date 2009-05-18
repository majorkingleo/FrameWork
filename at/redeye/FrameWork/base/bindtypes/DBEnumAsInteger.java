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
public class DBEnumAsInteger extends DBValue {

    public static abstract class EnumAsIntegerHandler
    {
        public abstract int getMaxSize();
        public abstract boolean setValue( String val );
        public abstract boolean setValue( Integer val );
        public abstract Integer getValue();        
        public abstract String getValueAsString();        
        public abstract EnumAsIntegerHandler getNewOne();
        public abstract Vector<String> getPossibleValues();  
    }
    
    public EnumAsIntegerHandler handler; 
    
    public DBEnumAsInteger( String name, EnumAsIntegerHandler enumval )
    {
        super( name );
        handler = enumval;
    }
    
    public DBEnumAsInteger( String name, String title, EnumAsIntegerHandler enumval )
    {
        super( name, title );
        handler = enumval;
    }
    
    @Override
    public MOMMDBDataType getDBType() {
        return MOMMDBDataType.DB_TYPE_INTEGER;
    }

    @Override
    public void loadFromDB(Object obj) {
        handler.setValue((Integer)obj);
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
       handler.setValue((Integer)obj);
    }

    @Override
    public Object getValue() {
        return handler.getValue();
    }

    @Override
    public DBValue getCopy() {
        DBEnumAsInteger copy = new DBEnumAsInteger(name, handler.getNewOne() );
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
        return handler.getValueAsString();
    }
    
    public Vector<String> getPossibleValues()
    {
        return handler.getPossibleValues();
    }
}
