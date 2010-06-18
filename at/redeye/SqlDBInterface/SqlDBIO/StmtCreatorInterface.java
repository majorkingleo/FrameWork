package at.redeye.SqlDBInterface.SqlDBIO;

import java.sql.SQLException;
import java.util.HashMap;

import at.redeye.SqlDBInterface.SqlDBIO.impl.ColumnAttribute;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;


/**
 * 
 * @author Mario Mattl
 * 
 */
public interface StmtCreatorInterface {

	/**
	 * 
	 * @param tablenames
	 *            The tables names
	 * @param whereStmt
	 *            Statement to identify the affected rows. It also has to
	 *            contain join-clauses, <br>
	 *            if more than one table is being selected.
	 * @param columnNames
	 * @return The created statement
	 */
	public String buildStmtForTable(String[] tablenames, String whereStmt,
			HashMap<String, ColumnAttribute> columnNames);

	/**
	 * 
	 * @param tablename
	 *            The tablename
	 * @param values
	 *            Filled Primary Key data for rows that shall be read
	 * @return The created statement
	 * @throws SQLException 
	 * @throws TableBindingNotRegisteredException 
	 */
	public String buildStmtForTable(String tablename,
			HashMap<String, Object> values) throws SQLException, TableBindingNotRegisteredException;

	/**
	 * 
	 * @param table
	 *            The table's name
	 * @param values
	 *            Columns and associated values
	 * @return The created statement.
	 */
	public String buildInsertStmtForTable(String table,
			HashMap<String, Object> values);

	/**
	 * 
	 * @param table
	 *            The table's name
	 * @param values
	 *            Columns and associated values.
	 * @param whereStmt
	 *            Optional, but if not specified all PrimaryKey elements must
	 *            exist <br>
	 *            in the given map "values".
	 * @return The created statement.
	 * @throws SQLException
	 *             If data is invalid or missing.
	 * @throws TableBindingNotRegisteredException 
	 */
	public String buildUpdateStmtForTable(String table,
			HashMap<String, Object> values, String whereStmt)
			throws SQLException, TableBindingNotRegisteredException;
	
	public String markTableName (String tableName);
	
	public String markColumnName (String columnName);

}
