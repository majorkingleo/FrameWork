/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Setup.dbexport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.dbmanager.DBBindtypeManager;
import at.redeye.FrameWork.base.dbmanager.impl.DatabaseManager;
import at.redeye.FrameWork.base.dbmanager.impl.bindtypes.DBTableVersion;
import at.redeye.FrameWork.base.transaction.DerbyTransaction;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.DeleteDir;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.FrameWork.utilities.Zip;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.ConnectionDefinition;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MissingConnectionParamException;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.SupportedDBMSTypes;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.UnSupportedDatabaseException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;

/**
 * 
 * @author martin
 */
public class DatabaseExport {
	public static Logger logger = Logger.getLogger(DatabaseExport.class
			.getName());

	public static class CannotCreateTempDatabase extends Exception {
		public CannotCreateTempDatabase(String message) {
			super(message);
		}

		public CannotCreateTempDatabase() {
			super();
		}
	}

	Root root;
	String target_file_name;
	Transaction trans_temp;
	Transaction trans_source;
	File temp_db_dir;
	ProgressListener listener = null;

	/**
	 * exports a databse to a zip file, by using a derby database. Always call
	 * close when finished, or an error appeared. This cleans up the tempdir
	 * stuff.
	 * 
	 * @param root
	 * @param target_file_name
	 */
	public DatabaseExport(Root root, String target_file_name) {
		this.target_file_name = target_file_name;
		this.root = root;
	}

	public void setProgressListener(ProgressListener progress_listener) {
		listener = progress_listener;
	}

	protected void fireEvent(String event) {
		if (listener != null)
			listener.setStage(event);
	}

	public void doExport() throws IOException, CannotCreateTempDatabase,
			ClassNotFoundException, SQLException,
			MissingConnectionParamException, UnSupportedDatabaseException,
			TableBindingNotRegisteredException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException {
		trans_source = root.getDBConnection().getNewTransaction();

		fireEvent("erzeuge temporäre Datenbank");

		trans_temp = createTempDatabase();
		DBBindtypeManager bindtype_manager = root.getBindtypeManager();

		Vector<DBStrukt> tables = bindtype_manager.getRegisteredTables();

		if (listener != null)
			listener.setOverallCounter(tables.size());

		DatabaseManager manager_temp = new DatabaseManager(trans_temp);

		for (DBStrukt table : tables) {
			manager_temp.register(table);
		}

		fireEvent("richte Datenbank ein");

		if (manager_temp.autocreate() == false) {
			throw new CannotCreateTempDatabase("Autocreating database failed!");
		}

		DBTableVersion table_version = new DBTableVersion();

		int count = 0;

		for (DBStrukt table : tables) {
			fireEvent("lese Tabelle " + table.getName());

			if (table.getName().equals(table_version.getName()))
				continue;

			logger.info("fetching table " + table.getName());
			final List<DBStrukt> res = trans_source.fetchTable(table);

			logger.info("fetched " + res.size() + " values");

			final String exp_format = "Exportiere Tabelle %s (%d von %d)";

			fireEvent(String.format(exp_format, table.getName(), 0, res.size()));

			int row_counter = 0;

			for (DBStrukt res_table : res) {
				int result = 0;
				row_counter++;

				try {
					result = insertValues(res_table);
				} catch (SQLException ex) {
					logger.error(StringUtils.exceptionToString(ex));
					logger.error("Last Sql:  " + trans_temp.getSql());
					throw ex;
				}

				if (result != 1) {
					logger.info("result of insert is: " + result);
					throw new CannotCreateTempDatabase("result of insert is: "
							+ result);
				}

				if (row_counter % 100 == 0) {
					if (row_counter % 1000 == 0) {
						// commits sind relativ teuer, daher nur alle 1000 mal
						trans_temp.commit();
					}

					logger.info("counter: " + row_counter);
					fireEvent(String.format(exp_format, table.getName(),
							row_counter, res.size()));
				}

				if (listener != null && !listener.canContinue())
					break;
			}

			trans_temp.commit();

			if (listener != null && !listener.canContinue())
				break;

			if (listener != null)
				listener.setCounter(++count);
		}

		trans_temp.commit();
		trans_temp.close();

		trans_temp = null;

		fireEvent("Komprimieren der Datenbank");

		try {
			Zip.zip(temp_db_dir, target_file_name);
		} catch (FileNotFoundException ex) {
			/**
			 * Irgendwie kann das bei Derby passieren, dass die Datenbank
			 * bereits geschlossen ist, aber im nachhinein noch die log Dateien
			 * weggelöscht werden. Deswegen hier der 2. Versuch.
			 */

			logger.error(ex);
			logger.error("retrying to export");

			Zip.zip(temp_db_dir, target_file_name);
		}

	}

	private Transaction createTempDatabase() throws IOException,
			CannotCreateTempDatabase, ClassNotFoundException, SQLException,
			MissingConnectionParamException, UnSupportedDatabaseException {
		temp_db_dir = File.createTempFile("tempdb", ".db");

		if (temp_db_dir == null)
			throw new CannotCreateTempDatabase("Cannot create temp directory");

		logger.info("creating temp database: " + temp_db_dir);

		temp_db_dir.delete();

		if (!root.getBindtypeManager().is_dbms_driver_loaded(
				SupportedDBMSTypes.DB_JAVADB))
			throw new CannotCreateTempDatabase(
					"Missing Derby DB Driver, which is required for exporting databases!");

		ConnectionDefinition connparams = new ConnectionDefinition("", 0, "",
				"", temp_db_dir.getAbsolutePath(), SupportedDBMSTypes.DB_JAVADB);

		Transaction t = new DerbyTransaction(connparams);

		return t;
	}

	/**
	 * clean temporary stuff up
	 * 
	 * @return true on success
	 */
	boolean close() {
		try {

			if (trans_temp != null)
				trans_temp.close();

			trans_temp = null;

			root.getDBConnection().closeTransaction(trans_source);
			trans_source = null;

		} catch (SQLException ex) {
			logger.error(StringUtils.exceptionToString(ex));
			return false;
		}

		if (temp_db_dir != null) {
			if (temp_db_dir.exists()) {
				logger.info("removing directory " + temp_db_dir);
				if (!DeleteDir.deleteDirectory(temp_db_dir)) {
					logger.error("couldn't remove temporary Directory: "
							+ temp_db_dir);
					return false;
				}
			}
			temp_db_dir = null;
		}

		return true;
	}

	public int insertValues(DBStrukt strukt)
			throws UnsupportedDBDataTypeException,
			WrongBindFileFormatException, SQLException, IOException {
		return trans_temp.insertValues(strukt);
	}
}
