/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.dbmanager;

import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

/**
 *
 * @author Administrator
 */
public interface DBManager {

    public boolean tableExists( String table ) throws SQLException;
    public String getTableVersion( String table ) throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException, CloneNotSupportedException;
    
    public Collection<String> getTables() throws SQLException;
    public boolean backupTable( String origin_name, String backup_name ) throws SQLException;
    public boolean migrateTable( DBStrukt strukt, Integer fromVersion ) throws SQLException;
    public boolean createTable( DBStrukt strukt ) throws SQLException;
    public boolean autoCreateTable( DBStrukt strukt ) throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, CloneNotSupportedException, WrongBindFileFormatException, IOException;
    public boolean db_supports_all_requested_features() throws SQLException;    
}
