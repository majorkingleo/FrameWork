/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.dbmanager.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.dbmanager.DBBindtypeManager;
import at.redeye.FrameWork.base.dbmanager.DBManager;
import at.redeye.FrameWork.base.dbmanager.ShowTables;
import at.redeye.FrameWork.base.dbmanager.impl.bindtypes.DBTableVersion;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.SupportedDBMSTypes;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.UserManagement.UserManagementInterface;

/**
 * 
 * @author Administrator
 */
public class DatabaseManager implements DBManager, DBBindtypeManager {

	protected Transaction trans = null;
	protected BaseCreateSql createSql = null;
	protected SupportedDBMSTypes dbmstype = null;
	protected ShowTables showTables = null;
	protected Vector<DBStrukt> tables = new Vector<DBStrukt>();

	HashMap<String, String> table_versions;
	Collection<String> table_list;
        Root root;

        String MESSAGE_ADMIN;
        String MESSAGE_USER;
        String MESSAGE_BOTH;
        String MESSAGE_TITLE;

	public DatabaseManager(Root root) {
            this.root = root;

            initCommon();
	}

	public DatabaseManager(Root root, Transaction trans) {
            this.root = root;
            setTransaction(trans);

            initCommon();
	}

        private void initCommon()
        {
            root.loadMlM4Class(this,"de");


            if( MESSAGE_ADMIN == null )
            {
                MESSAGE_ADMIN = root.MlM("Bitte öffnen Sie den Datenbankverbindungsdialog (Programm=>Datenbankeinstellungen) "
                        + "und betätigen Sie den Button \"Einrichten\". Dadurch werden die notwendigen Änderungen "
                        + "an der Datenbank automatisch durchgeführt. Bevor Tabellen manipuliert werden, erstellt "
                        + "die Applikation automatisch Sicherungen der zu manipulierenden Tabellen. "
                        + "Zusätzlich sollten Sie jedoch vorher eine Sicherung der gesammten Datenbank durchführen. "
                        + "Um Datenverlusten vorzubeugen stellen Sie bitte auch sicher, dass währen der Migration "
                        + "kein anderer User mehr auf die Datenbank zugreift");

                MESSAGE_USER = root.MlM("Bitte kontaktieren Sie Ihren Administrator damit dieser eine Aktualisierung der Datenbank "
                        + "durchführen kann. Um den Vorgang zu starten, muß sich ein Benutzer mit Administratorrechten "
                        + "auf dieser Applikation anmelden. Dann erfolgen auch noch weiter Hinweise. "
                        + "Sie können das Programm nun auch noch weiter verwenden, es ist aber nicht mehr "
                        + "sichergestellt das es korrekt funktioniert.");

                MESSAGE_BOTH = root.MlM("Eine oder mehrere Tabellen in der Datenbank weichen von jener Version, die im Programm "
                        + "benötigt werden ab.");

                MESSAGE_TITLE = root.MlM("Warnung");
            }
        }

	private void clearCache() {
		table_versions = null;
		table_list = null;
	}

	public void setTransaction(Transaction trans) {

		clearCache();

		this.trans = trans;

		if (trans == null)
			return;

		dbmstype = trans.getDBMSType();

		switch (dbmstype) {
		case DB_MYSQL:
			createSql = new CreateSqlMySql();
			break;

		case DB_JAVADB:
			createSql = new CreateSqlDerby();
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

		case DB_JAVADB:
			showTables = new ShowTablesDerby();
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

    @Override
	public boolean tableExists(String table) throws SQLException {

		// For the very first time
		if (showTables == null) {
			return false;
		}

		if (table_list == null) {
			table_list = showTables.showTables(trans);
		}

		for (String s : table_list) {
			if (s.equalsIgnoreCase(table)) {
				return true;
			}
		}

		return false;
	}

	public String getTableVersion(String table) throws SQLException,
			TableBindingNotRegisteredException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException, CloneNotSupportedException {

		DBTableVersion vers = new DBTableVersion();

		if (!tableExists(vers.getName()))
			return null;

		if (table_versions == null) {
			table_versions = new HashMap<String, String>();

			List<DBTableVersion> versions = trans
					.fetchTable2(new DBTableVersion());

			for (DBTableVersion version_entry : versions) {
				table_versions.put(
						version_entry.table.getValue().toUpperCase(),
						version_entry.version.getValue());
			}
		}

		/*
		 * old code DBTableVersion binddesc = new DBTableVersion();
		 * 
		 * binddesc.table.loadFromString(table);
		 * 
		 * Vector<DBStrukt> res = trans.fetchTable(binddesc, "where " +
		 * trans.markColumn("Table") + " = '" + table + "'");
		 * 
		 * if (res.size() > 0) return ((DBTableVersion)
		 * res.get(0)).version.toString();
		 */

		return table_versions.get(table.toUpperCase());
	}

	@Override
	public Collection<String> getTables() throws SQLException {
		return showTables.showTables(trans);
	}

	@Override
	public boolean backupTable(String origin_name, String backup_name)
			throws SQLException {
		String sql = createSql.createSqlForBackup(origin_name, backup_name);

		return execSql(sql);
	}

	@Override
	public boolean migrateTable(DBStrukt strukt, Integer fromVersion)
			throws SQLException {
		String table_name = strukt.getName();

		for (int i = 0; i < 50; i++) {
			table_name = strukt.getName() + "_"
					+ String.format("%02d", fromVersion) + "_"
					+ String.format("%02d", i + 1);

			if (tableExists(table_name))
				continue;

			break;
		}

		if (!backupTable(strukt.getName(), table_name))
			return false;

		// new table in the DB add it to the Cache
		if (table_list != null)
			table_list.add(table_name);

		String sql = new String();

		for (int i = fromVersion; i < strukt.getVersion(); i++) {
			sql += createSql.createSqlForNewRows(strukt, i + 1) + ";";
		}

		return execSql(sql);
	}

	protected boolean execSql(String sql) throws SQLException {
		String[] sqls = sql.split(";");

		boolean done_something = false;

		for (String s : sqls) {
			String s1 = s.trim();

			if (!s1.isEmpty()) {
				if (trans.updateValues(s1) < 0) {
					System.out.println("SqlStatement failed: " + s1);
					return false;
				}

				done_something = true;
			}
		}

		return done_something;
	}

	@Override
	public boolean createTable(DBStrukt strukt) throws SQLException {
		table_list = null; // cache leeren
		String sql = createSql.createSqlforTable(strukt);

		return execSql(sql);
	}

	@Override
	public boolean autoCreateTable(DBStrukt strukt) throws SQLException,
			TableBindingNotRegisteredException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException, CloneNotSupportedException,
			IOException {
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
					return setTableVersion(strukt.getName(),
							strukt.getVersion());
				} else {
					if (!createTable(strukt)) {
						return false;
					} else {
						return setTableVersion(strukt.getName(),
								strukt.getVersion());
					}
				}
			}
		} else {
			Integer vers = strukt.getVersion();
			Integer ivers = 0;

			if (version.equals("0.1")) // compatible mode for older apps
				ivers = 1;
			else
				ivers = Integer.parseInt(version);

			if (vers.compareTo(ivers) == 0) {
				return true;
			} else {
				if (!migrateTable(strukt, ivers)) {
					return false;
				} else {
					return setTableVersion(strukt.getName(),
							strukt.getVersion());
				}
			}
		}
	}

	private boolean setTableVersion(String name, Integer version)
			throws SQLException, TableBindingNotRegisteredException,
			UnsupportedDBDataTypeException, WrongBindFileFormatException,
			CloneNotSupportedException, IOException {

		DBTableVersion vers = new DBTableVersion();

		vers.table.loadFromString(name);

		boolean rv = trans.fetchTableWithPrimkey(vers);

		if (rv == true) {

			vers.version.loadFromString(version.toString());
			trans.updateValues(vers);

		} else {

			vers.version.loadFromString(version.toString());
			trans.insertValues(vers);

		}

		if (table_versions != null) {
			table_versions.put(name, version.toString());
		}

		return true;
	}

	public void register(DBStrukt strukt) {

		// doppelte Einträge vermeiden
		// kann passieren, wenn erneut eingelogt wird.
		for (int i = 0; i < tables.size(); i++) {
			if (tables.get(i).getName().equals(strukt.getName())) {
				tables.remove(i);
				break;
			}
		}

		tables.add(strukt);
	}

	/**
	 * automatically creates the entire database
	 * 
	 * @return return true on success, false on failure
	 */
    @Override
	public boolean autocreate() {

		AutoLogger al = new AutoLogger(DatabaseManager.class.getName()) {

			@Override
			public void do_stuff() throws Exception {

				for (final DBStrukt strukt : tables) {
					logger.debug("Creating Table: " + strukt.getName());
					boolean success = autoCreateTable(strukt);
					if (!success) {
						setFailed();
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

    @Override
	public boolean db_supports_all_requested_features() throws SQLException {
		return showTables.db_supports_all_requested_features(trans);
	}

    @Override
	public boolean can_support_db() {

		AutoLogger al = new AutoLogger("can_support_db") {
			@Override
			public void do_stuff() throws Exception {

				result = new Boolean(false);

				if (db_supports_all_requested_features()) {
					result = new Boolean(true);
				}
			}
		};

		return (Boolean) al.result;
	}

    @Override
	public boolean check_table_versions() {
		AutoLogger al = new AutoLogger(DatabaseManager.class.getName()) {

			@Override
			public void do_stuff() throws Exception {

				for (final DBStrukt strukt : tables) {
					logger.debug("Checking Table: " + strukt.getName());

					String sversion = getTableVersion(strukt.getName());

					if (sversion == null) {
						logger.error("Not entry of Table "
								+ strukt.getName()
								+ " or table TABLEVERSION itsself does not exists");
						setFailed();
						break;
					}

					int iversion = 0;

					if (sversion.equals("0.1")) {
						iversion = 1;
					} else {
						iversion = Integer.parseInt(sversion);
					}

					boolean success = false;

					if (iversion == strukt.getVersion()) {
						success = true;
					}

					if (!success) {
						setFailed();
						logger.debug(String
								.format("Table %s has version '%d' (string: %s) in Database and '%d' in Application Code. Updating Table is required.",
										strukt.getName(), iversion, sversion,
										strukt.getVersion()));
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

    @Override
    public boolean check_table_versions_with_message(final int Permissionlevel) {
        if (!check_table_versions()) {


            String msg;

            if (Permissionlevel == UserManagementInterface.UM_PERMISSIONLEVEL_ADMIN) {
                msg = MESSAGE_ADMIN;
            } else {
                msg = MESSAGE_USER;
            }

            JOptionPane.showMessageDialog(
                    null,
                    StringUtils.autoLineBreak(MESSAGE_BOTH + "\n\n" + msg),
                    MESSAGE_TITLE, JOptionPane.WARNING_MESSAGE);

            return false;
        }

        return true;
    }

    @Override
	public boolean is_dbms_driver_loaded(SupportedDBMSTypes dbmstype) {
		String name;

		try {
			switch (dbmstype) {
			case DB_MSSQL:
				name = net.sourceforge.jtds.jdbc.Driver.class.getName();
				break;
			case DB_MYSQL:
				name = com.mysql.jdbc.Driver.class.getName();
				break;
			case DB_ORACLE:
				name = oracle.jdbc.OracleDriver.class.getName();
				break;
			case DB_SQLITE:
				name = org.sqlite.JDBC.class.getName();
				break;
			case DB_JAVADB:
				name = org.apache.derby.jdbc.EmbeddedDriver.class.getName();
				break;
			}
		} catch (NoClassDefFoundError ex) {
			return false;
		}

		return true;
	}

    @Override
	public Vector<DBStrukt> getRegisteredTables() {
		return tables;
	}

    @Override
	public boolean drop_table(DBStrukt strukt) throws SQLException {

		table_list = null; // cache leeren

		return execSql("drop table " + trans.markTable(strukt));
	}

}
