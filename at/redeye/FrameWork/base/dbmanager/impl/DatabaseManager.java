/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.dbmanager.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.dbmanager.DBBindtypeManager;
import at.redeye.FrameWork.base.dbmanager.DBManager;
import at.redeye.FrameWork.base.dbmanager.ShowTables;
import at.redeye.FrameWork.base.dbmanager.impl.bindtypes.DBTableVersion;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MOMMSupportedDBMSTypes;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;

/**
 * 
 * @author Administrator
 */
public class DatabaseManager implements DBManager, DBBindtypeManager {

	protected Transaction trans = null;
	protected BaseCreateSql createSql = null;
	protected MOMMSupportedDBMSTypes dbmstype = null;
	protected ShowTables showTables = null;
	protected Vector<DBStrukt> tables = new Vector<DBStrukt>();

	public DatabaseManager() {

	}

	public DatabaseManager(Transaction trans) {
		setTransaction(trans);
	}

	public void setTransaction(Transaction trans) {
		this.trans = trans;

		dbmstype = trans.getDBMSType();

		switch (dbmstype) {
		case DB_MYSQL:
			createSql = new CreateSqlMySql();
			break;

		case DB_SQLITE:
			createSql = new CreateSqlSqlite();
			break;

		case DB_MSSQL:
			createSql = new CreateSqlMSSql();
			break;

		case DB_ORACLE:
			createSql = new CreateSqlOracle();
			break;

		default:
			// do nothing
			break;
		}

		switch (dbmstype) {
		case DB_SQLITE:
			showTables = new ShowTablesSqlite();
			break;

		case DB_MYSQL:
			showTables = new ShowTablesMySql();
			break;

		case DB_ORACLE:
			showTables = new ShowTablesOracle();
			break;

		case DB_MSSQL:
			showTables = new ShowTablesMSSql();
			break;

		}
	}

	public boolean tableExists(String table) throws SQLException {
		Collection<String> table_list = showTables.showTables(trans);

		for (String s : table_list)
			if (s.equalsIgnoreCase(table))
				return true;

		return false;
	}

	public String getTableVersion(String table) throws SQLException,
			TableBindingNotRegisteredException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException, CloneNotSupportedException {

		DBTableVersion vers = new DBTableVersion();

		if (!tableExists(vers.getName()))
			return null;

		DBTableVersion binddesc = new DBTableVersion();

		binddesc.table.loadFromString(table);

		Vector<DBStrukt> res = trans.fetchTable(binddesc, "where "
				+ trans.markColumn("Table") + " = '" + table + "'");

		if (res.size() > 0)
			return ((DBTableVersion) res.get(0)).version.toString();

		return null;
	}

	public Collection<String> getTables() throws SQLException {
		return showTables.showTables(trans);
	}

	public boolean backupTable(String origin_name, String backup_name)
			throws SQLException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean migrateTable(String table, String fromVersion,
			String toVersion, String script) throws SQLException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean createTable(DBStrukt strukt) throws SQLException {
		String sql = createSql.createSqlforTable(strukt);

		String[] sqls = sql.split(";");

		boolean done_something = false;

		for (String s : sqls) {
			String s1 = s.trim();

			if (!s1.isEmpty()) {
				if (trans.updateValues(s1) != 0) {
					System.out.println("SqlStatement failed: " + s1);
					return false;
				}

				done_something = true;
			}
		}

		return done_something;
	}

	public boolean autoCreateTable(DBStrukt strukt) throws SQLException,
			TableBindingNotRegisteredException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException, CloneNotSupportedException, IOException {
		String version = getTableVersion(strukt.getName());

		if (version == null || !tableExists(strukt.getName())) {
			/* existiert überhaupt die Version Tabelle? */
			DBTableVersion vers = new DBTableVersion();
			if (!tableExists(vers.getName())) {
				if (!createTable(vers)) {
					return false;
				} else {
					return autoCreateTable(strukt);
				}
			} else {
				if (tableExists(strukt.getName())) {
					return setTableVersion(strukt.getName(), strukt
							.getVersion());
				} else {
					if (!createTable(strukt)) {
						return false;
					} else {
						return setTableVersion(strukt.getName(), strukt
								.getVersion());
					}
				}
			}
		} else {
			String vers = strukt.getVersion();
			if (vers.compareTo(version) == 0) {
				return true;
			} else {
				return false;
			}
		}
	}

	private boolean setTableVersion(String name, String version)
			throws SQLException, TableBindingNotRegisteredException,
			UnsupportedDBDataTypeException, WrongBindFileFormatException,
			CloneNotSupportedException, IOException {

		DBTableVersion vers = new DBTableVersion();

		vers.table.loadFromString(name);

		boolean rv = trans.fetchTableWithPrimkey(vers);

		if (rv == true) {
			
			vers.version.loadFromString(vers.getVersion());
			trans.updateValues(vers);

		} else {
			
			vers.version.loadFromString(vers.getVersion());
			trans.insertValues(vers);
			
		}

		return true;
	}

	public void register(DBStrukt strukt) {
		tables.add(strukt);
	}

	public boolean autocreate() {

		AutoLogger al = new AutoLogger(DatabaseManager.class.getName()) {

			@Override
			public void do_stuff() throws Exception {

				for (final DBStrukt strukt : tables) {
					logger.debug("Creating Table: " + strukt.getName());
					boolean success = autoCreateTable(strukt);
					if (!success) {
						failed = true;
						logger.debug("Failed to create Table: "
								+ strukt.getName());
					}
				}
			}
		};

		if (al.isFailed()) {
			System.out.println("Last Sql: " + trans.getSql());
			return false;
		}

		return true;
	}

    public boolean db_supports_all_requested_features() throws SQLException {
        return showTables.db_supports_all_requested_features(trans);
    }

    public boolean can_support_db() {
        
        AutoLogger al = new AutoLogger("can_support_db" )
        {            
            @Override
            public void do_stuff() throws Exception {
                
                result = new Boolean(false);
                
                if( db_supports_all_requested_features() ) {
                    result = new Boolean(true);
                }            
            }
        };
        
        return (Boolean)al.result;
    }
    
    
}
