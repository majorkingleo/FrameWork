package at.redeye.SqlDBInterface.SqlDBIO.impl.creator;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import at.redeye.SqlDBInterface.SqlDBIO.StmtCreatorInterface;
import at.redeye.SqlDBInterface.SqlDBIO.StmtExecInterface;
import at.redeye.SqlDBInterface.SqlDBIO.TypeRegistrationInterface;
import at.redeye.SqlDBInterface.SqlDBIO.impl.ColumnAttribute;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;

/**
 * @author Mario Mattl
 * 
 */
public abstract class AbstractStmtCreator implements
		StmtCreatorInterface {

	protected static Logger logger = Logger
			.getLogger(AbstractStmtCreator.class.getSimpleName());

	protected TypeRegistrationInterface registration;
	
	/**
	 * Allows to identify a BLOB
	 */
	public static final String BLOB_IDENTIFIER = "###BLOB###";

	public AbstractStmtCreator(TypeRegistrationInterface registration) {
		this.registration = registration;
	}
	
    protected String markTableAndColumnNameForUpdate(String table, String column) {
		return markTableName(table) + "." + markColumnName(column);
	}

	public String buildStmtForTable(String[] tablenames, String whereStmt,
			HashMap<String, ColumnAttribute> columnNames) {

		StringBuilder str = new StringBuilder();

		str.append("select ");
		Set<String> keys = columnNames.keySet();
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext()) {
			str.append(markColumnName(iter.next()));
			if (iter.hasNext() == true) {
				str.append(" , ");
			}
		}
		str.append(" from ");
		for (int index = 0; index < tablenames.length; index++) {
			str.append(markTableName(tablenames[index]).toUpperCase());
			if (index < (tablenames.length - 1)) {
				str.append(" , ");
			}
		}
		if (whereStmt != null && whereStmt.isEmpty() == false) {
			str.append(" " + whereStmt);
		}
		logger.trace("\n-> Created Statement: " + str.toString());
		return str.toString();

	}

	public String buildStmtForTable(String tablename,
			HashMap<String, Object> values) throws SQLException,
			TableBindingNotRegisteredException {

		StringBuilder str = new StringBuilder();
		str.append("select ");

		HashMap<String, ColumnAttribute> columnNames = registration
				.getRegisteredTableByString(tablename);

		Set<String> keys = columnNames.keySet();
		if (keys == null) {
			throw new TableBindingNotRegisteredException("Table " + tablename
					+ " not found in registration!");
		}
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext()) {
			str.append(markColumnName(iter.next()));
			if (iter.hasNext() == true) {
				str.append(" , ");
			}
		}
		str.append(" from " + markTableName(tablename.toUpperCase())
				+ " where ");

		Vector<String> whereCols = new Vector<String>();

		iter = keys.iterator();
		ColumnAttribute attr = null;
		while (iter.hasNext()) {
			String key = iter.next();
			attr = columnNames.get(key);
			logger.trace(key + " -> IsPK: " + attr.isPrimaryKey());
			if (attr.isPrimaryKey() == true) {
				whereCols.add(key);
			}
		}
		if (whereCols.size() == 0) {
			throw new SQLException(
					"Select is impossible:\nNo PrimaryKey columns found!");
		}

		String[] tokens;
		String currcol;
		boolean usestring;
		for (int index = 0; index < whereCols.size(); index++) {
			currcol = whereCols.get(index);
			Object data = null;
			if (currcol.contains(".")) {
				tokens = currcol.split("\\."); // "." has to be escaped!
				data = values.get(tokens[1]);
				str.append(markColumnName(tokens[1]) + "=");
			} else {
				data = values.get(currcol);
				str.append(markColumnName(currcol) + "=");
			}

			if (data == null) {
				throw new SQLException(
						"Select is impossible:\nNo whereStmt given and (a part of) PrimaryKey data is missing!");
			}
			usestring = false;
			if (data instanceof String || data instanceof Date) {
				usestring = true;
				str.append("'");
			}
			if (data instanceof Date) {
				str.append(toDateString((Date) data));
			} else {
				str.append(data);
			}
			if (usestring) {
				str.append("'");
			}
			if (index < (whereCols.size() - 1)) {
				str.append(" and ");
			}
		}
		logger.trace("\n-> Created Statement: " + str.toString());
		return str.toString();
	}

	public String buildInsertStmtForTable(String table,
			HashMap<String, Object> values) {

		StringBuilder str = new StringBuilder();

		str.append("insert into ");
		str.append(markTableName(table) + " (");

		Set<String> keys = values.keySet();
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext()) {
			str.append(markColumnName(iter.next()));
			if (iter.hasNext() == true) {
				str.append(" , ");
			}
		}
		str.append(")");
		str.append(" values (");
		iter = keys.iterator();
		boolean usestring;
		while (iter.hasNext()) {
			usestring = false;
			Object ele = values.get(iter.next());
			if (ele instanceof String || ele instanceof Date) {
				str.append("'");
				usestring = true;
			}

			if (ele instanceof String && ele.toString().isEmpty()) {
				str.append(" ");
			} else if (ele instanceof Date) {
				str.append(toDateString((Date) ele));
			} else if (ele instanceof byte[]) {
				str.append(BLOB_IDENTIFIER);
			} else {
				str.append(ele.toString());
			}
			if (usestring) {
				str.append("'");
			}
			if (iter.hasNext() == true) {
				str.append(" , ");
			}
		}
		str.append(")");

		logger.trace("\n-> Created INSERT-Statement: " + str.toString());

		return str.toString();
	}

	public String buildUpdateStmtForTable(String table,
			HashMap<String, Object> values, String whereStmt)
			throws SQLException, TableBindingNotRegisteredException {

		StringBuilder str = new StringBuilder();

		str.append("update ");
		str.append(markTableName(table) + " SET ");

		Set<String> keys = values.keySet();
		Iterator<String> iter = keys.iterator();
		boolean usestring;
		while (iter.hasNext()) {
			usestring = false;
			String key = iter.next();
			logger.trace("--> " + key);
			str.append(markTableAndColumnNameForUpdate(table,key));
			str.append("=");
			Object ele = values.get(key);
			if (ele instanceof String || ele instanceof Date) {
				str.append("'");
				usestring = true;
			}
			if (ele instanceof Date) {
				str.append(toDateString((Date) ele));
			} else if (ele instanceof byte[]) {
				str.append(BLOB_IDENTIFIER);
			} else {
				str.append(ele);
			}
			if (usestring) {
				str.append("'");
			}
			if (iter.hasNext() == true) {
				str.append(" , ");
			}
		}
		if (whereStmt != null && whereStmt.isEmpty() == false) {
			str.append(" " + whereStmt);
		} else {

			Vector<String> whereCols = new Vector<String>();

			logger.trace("Searching table: " + table);

			HashMap<String, ColumnAttribute> cols = registration
					.getRegisteredTableByString(table);

			keys = cols.keySet();
			if (keys == null) {
				throw new TableBindingNotRegisteredException("Table " + table
						+ " not found in registration!");
			}
			iter = keys.iterator();
			ColumnAttribute attr = null;
			while (iter.hasNext()) {
				String key = iter.next();
				attr = cols.get(key);
				logger.trace(key + " -> IsPK: " + attr.isPrimaryKey());
				if (attr.isPrimaryKey() == true) {
					whereCols.add(key);
				}
			}
			if (whereCols.size() == 0) {
				throw new SQLException(
						"Update impossible:\nNo whereStmt given and no PrimaryKey columns found!");
			}
			str.append(" where ");

			String[] tokens;
			String currcol;

			for (int index = 0; index < whereCols.size(); index++) {
				str.append(markTableName(table) + "."
						+ markColumnName(whereCols.get(index)) + "=");
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
							"Update impossible:\nNo whereStmt given and (a part of) PrimaryKey data is missing!");
				}

				usestring = false;
				if (data instanceof String || data instanceof Date) {
					usestring = true;
					str.append("'");
				}
				if (data instanceof Date) {
					str.append(toDateString((Date) data));
				} else if (data instanceof byte[]) {
					str.append(BLOB_IDENTIFIER);
				} else {
					str.append(data);
				}
				if (usestring) {
					str.append("'");
				}
				if (index < (whereCols.size() - 1)) {
					str.append(" and ");
				}
			}
		}
		logger.trace("\n-> Created UPDATE-Statement: " + str.toString());

		return str.toString();
	}

	public String toDateString(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat(
				StmtExecInterface.SQLIF_STD_DATE_FORMAT + " "
						+ StmtExecInterface.SQLIF_STD_TIME_FORMAT);
		return sdf.format(date);

	}
	
	public abstract String markTableName (String tableName);
	
	public abstract String markColumnName (String columnName);
	

}
