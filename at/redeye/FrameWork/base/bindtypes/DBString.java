/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.bindtypes;

import at.redeye.SqlDBInterface.SqlDBIO.impl.MOMMDBDataType;

/**
 *
 * @author martin
 */
public class DBString extends DBValue {
    protected String value = new String();   
    protected int max_len;

    public DBString( String name, int max_len )
    {
        super( name );
        this.max_len = max_len;
    } 
    
    public DBString( String name, String title, int max_len )
    {
        super( name, title );
        this.max_len = max_len;
    }         
    
    @Override
    public MOMMDBDataType getDBType() {
        return MOMMDBDataType.DB_TYPE_STRING;
    }

    @Override
    public void loadFromDB(Object obj) {
        value = (String)obj;
    }

    @Override
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString()
    {
        return value;
    }

    @Override
    public void loadFromString(String s) {        
        value = s;        
    }

    @Override
    public boolean acceptString(String s) {
        if( s.length() > max_len )
            return false;
        
        return true;
    }

    @Override
    public DBString getCopy() {
        DBString s = new DBString( name, title, max_len );
        s.value = new String(value);
        return s;
    }

    @Override
    public void loadFromCopy(Object obj) {
        value = new String( (String)obj );
    }
    
    public int getMaxLen()
    {
        return max_len;
    }
}
