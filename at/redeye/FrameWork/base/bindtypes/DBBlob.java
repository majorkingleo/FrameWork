/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.bindtypes;

import at.redeye.SqlDBInterface.SqlDBIO.impl.DBDataType;

/**
 *
 * @author martin
 */
public class DBBlob extends DBValue {

    public byte[] value;
    
    public DBBlob(String name)
    {
        super(name);        
        value = new byte[0];
    }
    
    @Override
    public DBDataType getDBType() {
        return DBDataType.DB_TYPE_BLOB;
    }

    @Override
    public void loadFromDB(Object obj) {
        value = ((byte[])obj);
    }

    @Override
    public void loadFromString(String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean acceptString(String s) {
        return false;
    }

    @Override
    public void loadFromCopy(Object obj) {
        byte[] bb = (byte[])obj;
		value = new byte[bb.length];
		
		System.arraycopy(bb, 0, value, 0, bb.length);
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    @Override
    public DBBlob getCopy() 
    {        
        DBBlob blob = new DBBlob(name);
        blob.loadFromCopy(value);
        return blob;
    }

}
