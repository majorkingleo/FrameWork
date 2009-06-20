package at.redeye.SqlDBInterface.SqlDBIO.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import at.redeye.SqlDBInterface.SqlDBConnection.impl.MOMMSupportedDBMSTypes;
import at.redeye.SqlDBInterface.SqlDBIO.MOMMStmtCreatorInterface;
import at.redeye.SqlDBInterface.SqlDBIO.MOMMStmtExecInterface;
import at.redeye.SqlDBInterface.SqlDBIO.MOMMTypeRegistrationInterface;

public abstract class MOMMAbstractStmtExecuter implements MOMMStmtExecInterface {

	private Connection conn_;

	private MOMMSupportedDBMSTypes dbmstype_;

	private MOMMStmtCreatorInterface stmtCreator_;

	private MOMMTypeRegistrationInterface treg_;

	private static String lastStmt_ = null;

	public MOMMAbstractStmtExecuter(Connection conn,
			MOMMSupportedDBMSTypes dbmstype) {
		super();
		this.conn_ = conn;
		this.dbmstype_ = dbmstype;
		this.treg_ = new MOMMTypeRegistration(dbmstype_);
		switch (dbmstype_) {
		case DB_MYSQL:
			this.stmtCreator_ = new MOMMStmtCreatorMYSQL(treg_);
			break;
		case DB_ORACLE:
			this.stmtCreator_ = new MOMMStmtCreatorOracle(treg_);
			break;
		case DB_SQLITE:
			this.stmtCreator_ = new MOMMStmtCreatorSQLITE(treg_);
			break;
		case DB_MSSQL:
			this.stmtCreator_ = new MOMMStmtCreatorMSSQL(treg_);
			break;

		}

	}

	protected Object processTypeValue(ResultSet rs, MOMMDBDataType sourceType,
			int index) throws UnsupportedDBDataTypeException, SQLException {

		String result = null;

		switch (sourceType) {

		case DB_TYPE_DOUBLE:
			return (new Double(rs.getDouble(index)));

		case DB_TYPE_FLOAT:
			return (new Float(rs.getFloat(index)));

		case DB_TYPE_INTEGER:
			return (new Integer(rs.getInt(index)));

		case DB_TYPE_LONG:
			return (new Long(rs.getLong(index)));

		case DB_TYPE_STRING:

			result = rs.getString(index);
			return (new String(result == null ? "" : result.trim()));

		case DB_TYPE_SHORT:
			return (new Short(rs.getShort(index)));

		case DB_TYPE_BOOLEAN:
		case DB_TYPE_BIT:
			return (new Boolean(rs.getBoolean(index)));

		case DB_TYPE_DATE: // FT
		case DB_TYPE_TIME: // FT
		case DB_TYPE_DATETIME:

			return new Date(rs.getTimestamp(index).getTime());
			
		case DB_TYPE_BLOB:
			byte [] bytes = rs.getBytes(index);
			return bytes;
			
		default:
			throw new UnsupportedDBDataTypeException("DataType "
					+ sourceType.toString() + " is unknown!");
		}

	}

	/**
	 * 
	 * @param stmt
	 *            Statement that shall be executed
	 * @param typelist
	 *            User input of types which are expected to be read.<br>
	 *            The user is responsible for putting the correct type-order.
	 * @return A vector (rows) of vectors (columns)
	 * 
	 */
	public Vector<Vector<?>> fetchColumnValue(String stmt,
			Vector<MOMMDBDataType> typelist) throws SQLException,
			UnsupportedDBDataTypeException {

		Vector<Vector<?>> wholeOutput = new Vector<Vector<?>>();
		Vector<Object> wholeRow = null;

		Statement s = conn_.createStatement();

		ResultSet rs = null;
		setLastStmt(stmt);
		rs = s.executeQuery(stmt);

		while (rs.next()) {
			wholeRow = new Vector<Object>();
			for (int index = 0; index < typelist.size(); index++) {

				wholeRow.add(processTypeValue(rs, typelist.get(index),
						(index + 1)));
			}
			wholeOutput.add(wholeRow);
		}
		rs.close();
		s.close();
		return wholeOutput;
	}

	/**
	 * 
	 * This method reads all columns of the given table. <br>
	 * For binding it uses a Map <br>
	 * consisting of <String tablename, Vector<Object datatype>> <br>
	 * where all type definitions are stored. <br>
	 * <br>
	 * 
	 * @param tablenames
	 *            the table's names <br>
	 * @param whereStmt
	 *            the statement to execute <br>
	 * @throws SQLException
	 *             in case of SQL errors <br>
	 * @throws UnsupportedDBDataTypeException
	 *             if a data-type could not be binded <br>
	 * @throws TableBindingNotRegisteredException
	 *             if requested table has no registered binding <br>
	 */

	public Vector<HashMap<String, Object>> fetchTableValue(String[] tablenames,
			String whereStmt) throws SQLException,
			UnsupportedDBDataTypeException, TableBindingNotRegisteredException {

		Statement s = conn_.createStatement();

		Vector<HashMap<String, Object>> wholeOutput = new Vector<HashMap<String, Object>>();
		HashMap<String, MOMMColumnAttribute> typelist = new HashMap<String, MOMMColumnAttribute>();
		HashMap<String, Object> wholeRow = null;

		HashMap<String, HashMap<String, MOMMColumnAttribute>> registeredTables = treg_
				.getAllRegisteredTables();

		if (tablenames.length < 1)
			throw new TableBindingNotRegisteredException("No tables given!");

		for (int tabindex = 0; tabindex < tablenames.length; tabindex++) {
			String table = "";
			table = tablenames[tabindex].toUpperCase();
			if (!registeredTables.containsKey(table))
				throw new TableBindingNotRegisteredException("Table " + table
						+ " has not been registered yet!");
			typelist.putAll(registeredTables.get(table));
		}

		String stmt = stmtCreator_.buildStmtForTable(tablenames, whereStmt,
				typelist);
		setLastStmt(stmt);
		Set<String> keys = typelist.keySet();

		ResultSet rs = s.executeQuery(stmt);
		while (rs.next()) {

			wholeRow = new HashMap<String, Object>();

			Iterator<String> iter = keys.iterator();
			int counter = 1;
			while (iter.hasNext()) {
				String currkey = iter.next();
				wholeRow.put(currkey, processTypeValue(rs, typelist
						.get(currkey).getDatatype(), counter));
				counter++;

			}
			wholeOutput.add(wholeRow);
		}
		rs.close();
		s.close();
		return wholeOutput;
	}

	@Override
	public HashMap<String, Object> fetchTableValue(String tablename,
			HashMap<String, Object> primaryKeyData) throws SQLException,
			UnsupportedDBDataTypeException, TableBindingNotRegisteredException {

		Statement s = conn_.createStatement();
		String stmt = "";
		ResultSet rs = null;

		HashMap<String, HashMap<String, MOMMColumnAttribute>> registeredTables = treg_
				.getAllRegisteredTables();
		HashMap<String, MOMMColumnAttribute> typelist = null;
		HashMap<String, Object> wholeRow = null;

		if (!registeredTables.containsKey(tablename.toUpperCase()))
			throw new TableBindingNotRegisteredException("Table " + tablename
					+ " has not been registered yet!");

		typelist = registeredTables.get(tablename.toUpperCase());

		Set<String> keys = typelist.keySet();
		stmt = stmtCreator_.buildStmtForTable(tablename, primaryKeyData);
		setLastStmt(stmt);
		rs = s.executeQuery(stmt);
		while (rs.next()) {

			wholeRow = new HashMap<String, Object>();

			Iterator<String> iter = keys.iterator();
			int counter = 1;
			while (iter.hasNext()) {
				String currkey = iter.next();
				wholeRow.put(currkey, processTypeValue(rs, typelist
						.get(currkey).getDatatype(), counter));
				counter++;

			}
		}
		rs.close();

		s.close();
		return wholeRow;
	}

	/**
	 * 
	 * Method is similar to a "toString()" overload.
	 * 
	 * @param rs
	 *            Database result set:<br>
	 *            A Vector (rows) of HashMaps (columns)
	 * @return A String for displaying all rows
	 */
	public String printFetchTableOutput(Vector<HashMap<String, Object>> rs) {

		StringBuilder str = new StringBuilder();
		HashMap<String, Object> row = null;

		str.append("\n(JOINED) COLUMNS:\n");
		str.append("------------------------------------------------\n");

		int index = 0;
		for (; index < rs.size(); index++) {

			row = rs.get(index);
			Set<String> keys = row.keySet();
			Iterator<String> iter = keys.iterator();
			if (index == 0) {
				while (iter.hasNext()) {
					String currkey = iter.next();
					str.append(currkey + "\n");
				}
			}
			str.append("\n");
			iter = keys.iterator();
			while (iter.hasNext()) {
				String currkey = iter.next();
				str.append(row.get(currkey) + "\t");

			}

		}
		str.append("\n\n(" + index + ") READY\n");
		return str.toString();
	}

	/**
	 * 
	 * Method is similar to a "toString()" overload.
	 * 
	 * @param rs
	 *            Database result set:<br>
	 *            HashMap (columns)
	 * @return A String for displaying all rows
	 */
	public String printFetchTableOutput(HashMap<String, Object> rs) {

		StringBuilder str = new StringBuilder();

		str.append("\n(JOINED) COLUMNS:\n");
		str.append("------------------------------------------------\n");

		Set<String> keys = rs.keySet();
		Iterator<String> iter = keys.iterator();

		while (iter.hasNext()) {
			String currkey = iter.next();
			str.append(currkey + "\n");
		}

		str.append("\n");
		iter = keys.iterator();
		while (iter.hasNext()) {
			String currkey = iter.next();
			str.append(rs.get(currkey) + "\t");

		}

		str.append("\n\nREADY\n");
		return str.toString();
	}

	public int insertValues(String stmt) throws SQLException {

		Statement s = conn_.createStatement();
		int rv = 0;

		setLastStmt(stmt);
		rv = s.executeUpdate(stmt);

		s.close();
		return rv;
	}

	public int updateValues(String stmt) throws SQLException {

		Statement s = conn_.createStatement();
		int rv = 0;

		setLastStmt(stmt);
		rv = s.executeUpdate(stmt);

		s.close();
		return rv;
	}

	public int insertTableValues(String tablename,
			HashMap<String, Object> values) throws SQLException, IOException {

		if (tablename.isEmpty()) {
			throw new SQLException("No tablename given");
		}

		PreparedStatement s;
		int rv = 0;
		String stmt = stmtCreator_.buildInsertStmtForTable(tablename, values);
		s = handleDMLStatement(stmt, values);
		setLastStmt(stmt);
		rv = s.executeUpdate();

		s.close();
		return rv;

	}

	public int updateTableValues(String tablename,
			HashMap<String, Object> values, String whereStmt)
			throws SQLException, TableBindingNotRegisteredException, IOException {

		if (tablename.isEmpty()) {
			throw new SQLException("No tablename given");
		}

		PreparedStatement s;
		int rv = 0;

		String stmt = stmtCreator_.buildUpdateStmtForTable(tablename, values,
				whereStmt);
		s = handleDMLStatement(stmt, values);
		setLastStmt(stmt);
		rv = s.executeUpdate();

		s.close();
		return rv;

	}

	/**
	 * @return the lastStmt_
	 */
	public String getLastStmt() {
		return (lastStmt_ == null ? "NOT SET" : lastStmt_);
	}

	/**
	 * @param lastStmt
	 *            the lastStmt to set
	 */
	protected void setLastStmt(String lastStmt) {
		MOMMAbstractStmtExecuter.lastStmt_ = lastStmt;
	}

	protected boolean containsBlob(String stmt) {
		return stmt.contains(MOMMAbstractStmtCreator.BLOB_IDENTIFIER);
	}

	protected java.sql.PreparedStatement handleDMLStatement(String stmt,
			HashMap<String, Object> values) throws SQLException, IOException {

		PreparedStatement ps;
		
		if (containsBlob(stmt)) {
			stmt = stmt.replaceAll(MOMMAbstractStmtCreator.BLOB_IDENTIFIER, "\\?");
			ps = conn_.prepareStatement(stmt);
			Set<String> keys = values.keySet();
			Iterator<String> iter = keys.iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				Object ele = values.get(key);
				if (ele instanceof byte[]) {
					byte[] blobData = (byte[]) ele;
					InputStream is = new ByteArrayInputStream(blobData);
					ps.setBinaryStream(1, is, blobData.length);
					is.close();
				}
			}
		} else {
			ps = conn_.prepareStatement(stmt);
		}
		return ps;
	}

}
