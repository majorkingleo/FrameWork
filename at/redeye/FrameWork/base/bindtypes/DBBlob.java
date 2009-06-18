/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.bindtypes;

import at.redeye.FrameWork.base.bindtypes.DBValue;
import at.redeye.SqlDBInterface.SqlDBIO.impl.MOMMDBDataType;
import com.mysql.jdbc.Blob;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author martin
 */
public class DBBlob extends DBValue {

    Blob value;
    
    public DBBlob(String name)
    {
        super(name);
    }
    
    @Override
    public MOMMDBDataType getDBType() {
        return MOMMDBDataType.DB_TYPE_BLOB;
    }

    @Override
    public void loadFromDB(Object obj) {
       value = (Blob)obj;
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
        try {
            Blob blob = (Blob) obj;
            value.setBytes(0, blob.getBytes(0, 0));
        } catch (SQLException ex) {
            Logger.getLogger(DBBlob.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Object getValue() {
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
