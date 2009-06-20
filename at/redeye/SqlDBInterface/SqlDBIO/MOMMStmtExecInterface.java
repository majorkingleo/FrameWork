package at.redeye.SqlDBInterface.SqlDBIO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import at.redeye.SqlDBInterface.SqlDBIO.impl.MOMMDBDataType;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;


/**
 * 
 * @author Mario Mattl
 * 
 */
public interface MOMMStmtExecInterface {
	
	
	public final static String SQLIF_STD_DATE_FORMAT = "yyyy-MM-dd";
	public final static String SQLIF_STD_TIME_FORMAT = "HH:mm:ss";

	/**
	 * 
	 * @param tablenames
	 * @param whereStmt
	 * @return The selected rows
	 * @throws SQLException
	 * @throws UnsupportedDBDataTypeException
	 * @throws TableBindingNotRegisteredException
	 */
	public Vector<HashMap<String, Object>> fetchTableValue(String[] tablenames,
			String whereStmt) throws SQLException,
			UnsupportedDBDataTypeException, TableBindingNotRegisteredException;

	/**
	 * 
	 * @param tablename
	 * @param primaryKeyData
	 * @return
	 * @throws SQLException
	 * @throws UnsupportedDBDataTypeException
	 * @throws TableBindingNotRegisteredException
	 */
	public HashMap<String, Object> fetchTableValue(String tablename,
			HashMap<String, Object> primaryKeyData) throws SQLException,
			UnsupportedDBDataTypeException, TableBindingNotRegisteredException;

	/**
	 * 
	 * @param stmt
	 * @param typelist
	 * @return The selected rows
	 * @throws SQLException
	 * @throws UnsupportedDBDataTypeException
	 */
	public Vector<Vector<?>> fetchColumnValue(String stmt,
			Vector<MOMMDBDataType> typelist) throws SQLException,
			UnsupportedDBDataTypeException;
	/**
	 * 
	 * Method is similar to a "toString()" overload.
	 * 
	 * @param rs
	 *            Database result set:<br>
	 *            A Vector (rows) of HashMaps (columns)
	 * @return A String for displaying all rows
	 */
	public String printFetchTableOutput(Vector<HashMap<String, Object>> rs);
	
	/**
	 * 
	 * @param rs
	 *            The selected row.
	 * @return The printable output (similar to a toString overload)
	 */
	public String printFetchTableOutput(HashMap<String, Object> rs);
	
	

	/**
	 * 
	 * @param stmt
	 *            The insert Statement. The user is fully responsible for giving
	 *            a valid statement.
	 * @return Number of inserted elements
	 * @throws SQLException, IOException
	 *             In case of SQL errors
	 */
	public int updateValues(String stmt) throws SQLException;

	/**
	 * 
	 * @param stmt
	 *            The update Statement. The user is fully responsible for giving
	 *            a valid statement.
	 * @return Number of inserted elements
	 * @throws SQLException, IOException
	 *             In case of SQL errors
	 */
	public int insertValues(String stmt) throws SQLException;

	/**
	 * 
	 * @param tablename
	 *            The table's name
	 * @param values
	 *            Columns and associated values
	 * @param whereStmt
	 *            Optional, but if not specified all PrimaryKey elements must
	 *            exist <br>
	 *            in the given map "values".
	 * @return Number of updated elements
	 * @throws SQLException
	 *             If data is invalid or missing or in case of SQL errors
	 * @throws TableBindingNotRegisteredException 
	 * @throws IOException
	 */
	public int updateTableValues(String tablename,
			HashMap<String, Object> values, String whereStmt)
			throws SQLException, TableBindingNotRegisteredException, IOException;

	/**
	 * 
	 * @param tablename
	 *            The table's name
	 * @param values
	 *            Columns and associated values
	 * @return Number of inserted elements
	 * @throws SQLException
	 *             If data is invalid or missing or in case of SQL errors
	 * @throws IOException
	 */
	public int insertTableValues(String tablename,
			HashMap<String, Object> values) throws SQLException, IOException;
	
	/**
	 * 
	 * @return The previously executed statement.
	 */
	public String getLastStmt ();
}
