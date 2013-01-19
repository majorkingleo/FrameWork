package at.redeye.SqlDBInterface.SqlDBIO.impl.executor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import at.redeye.SqlDBInterface.SqlDBConnection.impl.SupportedDBMSTypes;
import at.redeye.SqlDBInterface.SqlDBIO.StmtCreatorInterface;
import at.redeye.SqlDBInterface.SqlDBIO.StmtExecInterface;
import at.redeye.SqlDBInterface.SqlDBIO.TypeRegistrationInterface;
import at.redeye.SqlDBInterface.SqlDBIO.impl.ColumnAttribute;
import at.redeye.SqlDBInterface.SqlDBIO.impl.DBDataType;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TypeRegistration;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.creator.StmtCreatorFactory;

public abstract class AbstractStmtExecuter implements StmtExecInterface {

	private Connection conn;

	private StmtCreatorInterface stmtCreator;

	private TypeRegistrationInterface treg;

	private static String lastStmt = null;

	protected static Logger logger = Logger
			.getLogger(AbstractStmtExecuter.class.getSimpleName());

	public AbstractStmtExecuter(Connection conn, SupportedDBMSTypes dbmstype) {
		super();
		this.conn = conn;
		this.treg = new TypeRegistration(dbmstype);
		this.stmtCreator = new StmtCreatorFactory(treg)
				.getStmtCreator(dbmstype);

	}

	protected static Object processTypeValue(ResultSet rs,
			DBDataType sourceType, int index)
			throws UnsupportedDBDataTypeException, SQLException {

		switch (sourceType) {

		case DB_TYPE_DOUBLE:
			return (Double) rs.getDouble(index);

		case DB_TYPE_FLOAT:
			return (Float) rs.getFloat(index);

		case DB_TYPE_INTEGER:
			return (Integer) rs.getInt(index);

		case DB_TYPE_LONG:
			return (Long) rs.getLong(index);

		case DB_TYPE_STRING: {
			String result = rs.getString(index);
			return (result == null ? "" : result.trim());
		}

		case DB_TYPE_SHORT:
			return (Short) rs.getShort(index);

		case DB_TYPE_BOOLEAN:
		case DB_TYPE_BIT:
			return (Boolean) rs.getBoolean(index);

		case DB_TYPE_DATE: // FT
		case DB_TYPE_TIME: // FT
		case DB_TYPE_DATETIME:

			final Timestamp ts = rs.getTimestamp(index);
			return new Date(ts == null ? 0 : ts.getTime());

		case DB_TYPE_BLOB: {
			byte[] bytes = rs.getBytes(index);
			return bytes;
		}

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
	@Override
	public List<List<?>> fetchColumnValue(String stmt, List<DBDataType> typelist)
			throws SQLException, UnsupportedDBDataTypeException {

		long fetchStartTime = System.currentTimeMillis();
		ArrayList<List<?>> wholeOutput = new ArrayList<List<?>>();
		List<Object> wholeRow = null;

		Statement s = conn.createStatement();

		ResultSet rs = null;

		rs = s.executeQuery(stmt);

		while (rs.next()) {
			wholeRow = new ArrayList<Object>();
			for (int index = 0; index < typelist.size(); index++) {

				wholeRow.add(processTypeValue(rs, typelist.get(index),
						(index + 1)));
			}
			wholeOutput.add(wholeRow);
		}
		rs.close();
		s.close();
		setLastStmt(buildTimeSuffix(stmt,
				(System.currentTimeMillis() - fetchStartTime)));
		logger.trace(lastStmt);
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

	public List<HashMap<String, Object>> fetchTableValue(String[] tablenames,
			String whereStmt) throws SQLException,
			UnsupportedDBDataTypeException, TableBindingNotRegisteredException {

		long fetchStartTime = System.currentTimeMillis();
		PreparedStatement s;

		List<HashMap<String, Object>> wholeOutput = new ArrayList<HashMap<String, Object>>();
		HashMap<String, ColumnAttribute> typelist = new HashMap<String, ColumnAttribute>();
		HashMap<String, Object> wholeRow = null;

		HashMap<String, HashMap<String, ColumnAttribute>> registeredTables = treg
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

		String stmt = stmtCreator.buildStmtForTable(tablenames, whereStmt,
				typelist);

		s = conn.prepareStatement(stmt);
		ResultSet rs = s.executeQuery();

		// Set<String> keys = typelist.keySet();

		Set<Entry<String, ColumnAttribute>> keys_and_types = typelist
				.entrySet();

		while (rs.next()) {

			wholeRow = new HashMap<String, Object>();

			// Iterator<String> iter = keys.iterator();
			int counter = 1;
			for (Entry<String, ColumnAttribute> entry : keys_and_types) {

				wholeRow.put(
						entry.getKey(),
						processTypeValue(rs, entry.getValue().getDatatype(),
								counter));
				counter++;

			}
			wholeOutput.add(wholeRow);
		}
		rs.close();
		s.close();

		setLastStmt(buildTimeSuffix(stmt,
				(System.currentTimeMillis() - fetchStartTime)));

		logger.trace(lastStmt);
		return wholeOutput;
	}

	@Override
	public HashMap<String, Object> fetchTableValue(String tablename,
			HashMap<String, Object> primaryKeyData) throws SQLException,
			UnsupportedDBDataTypeException, TableBindingNotRegisteredException,
			IOException {

		PreparedStatement s;
		String stmt = "";
		ResultSet rs = null;
		long fetchStartTime = System.currentTimeMillis();

		HashMap<String, HashMap<String, ColumnAttribute>> registeredTables = treg
				.getAllRegisteredTables();
		HashMap<String, ColumnAttribute> typelist = null;
		HashMap<String, Object> wholeRow = null;

		if (!registeredTables.containsKey(tablename.toUpperCase()))
			throw new TableBindingNotRegisteredException("Table " + tablename
					+ " has not been registered yet!");

		typelist = registeredTables.get(tablename.toUpperCase());

		Set<String> keys = typelist.keySet();
		stmt = stmtCreator.buildStmtForTable(tablename, primaryKeyData);
		s = handleStatement(stmt, primaryKeyData);
		logger.info(s.toString());
		rs = s.executeQuery();
		while (rs.next()) {

			wholeRow = new HashMap<String, Object>();

			Iterator<String> iter = keys.iterator();
			int counter = 1;
			while (iter.hasNext()) {
				String currkey = iter.next();
				wholeRow.put(
						currkey,
						processTypeValue(rs, typelist.get(currkey)
								.getDatatype(), counter));
				counter++;

			}
		}
		rs.close();
		s.close();

		setLastStmt(buildTimeSuffix(stmt,
				(System.currentTimeMillis() - fetchStartTime)));
		if (logger.isTraceEnabled()) {
			logger.trace(lastStmt);
		}
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
	@Override
	public String printFetchTableOutput(List<HashMap<String, Object>> rs) {

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
					str.append(currkey).append("\n");
				}
			}
			str.append("\n");
			iter = keys.iterator();
			while (iter.hasNext()) {
				String currkey = iter.next();
				str.append(row.get(currkey)).append("\t");

			}

		}
		str.append("\n\n(").append(index).append(") READY\n");
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
			str.append(currkey).append("\n");
		}

		str.append("\n");
		iter = keys.iterator();
		while (iter.hasNext()) {
			String currkey = iter.next();
			str.append(rs.get(currkey)).append("\t");

		}

		str.append("\n\nREADY\n");
		return str.toString();
	}

	public int insertValues(String stmt) throws SQLException {

		Statement s = conn.createStatement();
		int rv = 0;

		setLastStmt(stmt);
		rv = s.executeUpdate(stmt);

		s.close();
		return rv;
	}

	public int updateValues(String stmt) throws SQLException {

		Statement s = conn.createStatement();
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

		long fetchStartTime = System.currentTimeMillis();
		PreparedStatement s;
		int rv = 0;
		String stmt = stmtCreator.buildInsertStmtForTable(tablename, values);
		s = handleStatement(stmt, values);

		try {
			rv = s.executeUpdate();
			s.close();

		} catch (SQLException ex) {

			setLastStmt(buildTimeSuffix(stmt,
					(System.currentTimeMillis() - fetchStartTime)));
			logger.error(lastStmt, ex);
			throw ex;
		}

		setLastStmt(buildTimeSuffix(stmt,
				(System.currentTimeMillis() - fetchStartTime)));
		logger.trace(lastStmt);

		return rv;
	}

	public int updateTableValues(String tablename,
			HashMap<String, Object> values, String whereStmt)
			throws SQLException, TableBindingNotRegisteredException,
			IOException {

		if (tablename.isEmpty()) {
			throw new SQLException("No tablename given");
		}
		long fetchStartTime = System.currentTimeMillis();
		PreparedStatement s;
		int rv = 0;

		String stmt = stmtCreator.buildUpdateStmtForTable(tablename, values,
				whereStmt);

		s = handleStatement(stmt, values);

		try {

			rv = s.executeUpdate();
			s.close();

		} catch (SQLException ex) {

			setLastStmt(buildTimeSuffix(stmt,
					(System.currentTimeMillis() - fetchStartTime)));
			logger.error(lastStmt, ex);
			throw ex;
		}

		setLastStmt(buildTimeSuffix(stmt,
				(System.currentTimeMillis() - fetchStartTime)));

		logger.trace(lastStmt);

		return rv;

	}

	/**
	 * @return the lastStmt
	 */
	public String getLastStmt() {
		return (lastStmt == null ? "NOT SET" : lastStmt);
	}

	/**
	 * @param lastStmt
	 *            the lastStmt to set
	 */
	protected void setLastStmt(String lastStmt) {
		AbstractStmtExecuter.lastStmt = lastStmt;
	}

	protected PreparedStatement handleStatement(String stmt,
			HashMap<String, Object> values) throws SQLException, IOException {

		PreparedStatement ps = conn.prepareStatement(stmt);
		List<String> whereCols = stmtCreator.getCols2Handle();

		String currcol;
		String[] tokens;
		for (int index = 0; index < whereCols.size(); index++) {
			currcol = whereCols.get(index);
			Object data = null;
			if (currcol.contains(".")) {
				tokens = currcol.split("\\."); // "." has to be escaped!
				data = values.get(tokens[1]);
			} else {
				data = values.get(currcol);
			}
			if (data == null) {
				throw new SQLException(
						"Select is impossible:\nNo whereStmt given and (a part of) PrimaryKey data is missing! Column: '"
								+ currcol + "' is null");
			}
			setPreparedStatementTypes(ps, index + 1, data);

		}
		return ps;
	}

	public StmtCreatorInterface getStmtCreator() {
		return stmtCreator;
	}

	protected String buildTimeSuffix(String base, long millis) {
		StringBuilder str = new StringBuilder(base);

		str.append(" [ duration: ");
		str.append(millis);
		str.append(" ms ]");

		return str.toString();
	}

	protected void setPreparedStatementTypes(PreparedStatement ps, int index,
			Object data) throws SQLException, IOException {

		if (logger.isTraceEnabled())
			logger.trace("Start index " + index + " / " + data);

		if (data instanceof String) {
			ps.setString(index, (String) data);
		} else if (data instanceof Date) {
			ps.setString(index, (String) stmtCreator.toDateString((Date) data));
		} else if (data instanceof Float) {
			ps.setFloat(index, (Float) data);
		} else if (data instanceof Double) {
			ps.setDouble(index, (Double) data);
		} else if (data instanceof Integer) {
			ps.setInt(index, (Integer) data);
		} else if (data instanceof Long) {
			ps.setLong(index, (Long) data);
		} else if (data instanceof Short) {
			ps.setShort(index, (Short) data);
		} else if (data instanceof Boolean) {
			ps.setBoolean(index, (Boolean) data);
		} else if (data instanceof Byte) {
			ps.setByte(index, (Byte) data);
		} else if (data instanceof byte[]) {
			byte[] blobData = (byte[]) data;
			InputStream is = new ByteArrayInputStream(blobData);
			ps.setBinaryStream(index, is, blobData.length);
			is.close();
		} else {
			throw new SQLException(
					"Unknown data type! Don't know how to handle " + index
							+ " / " + data);
		}

	}

}
