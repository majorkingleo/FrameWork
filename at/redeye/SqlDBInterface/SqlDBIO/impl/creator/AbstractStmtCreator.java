package at.redeye.SqlDBInterface.SqlDBIO.impl.creator;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
public abstract class AbstractStmtCreator implements StmtCreatorInterface {

	protected static Logger logger = Logger.getLogger(AbstractStmtCreator.class
			.getSimpleName());

	protected TypeRegistrationInterface registration;

	List<String> boundColumns = new ArrayList<String>();

	public AbstractStmtCreator(TypeRegistrationInterface registration) {
		this.registration = registration;
	}

	protected String markTableAndColumnNameForUpdate(String table, String column) {
		return markTableName(table) + "." + markColumnName(column);
	}

	public String buildStmtForTable(String[] tablenames, String whereStmt,
			HashMap<String, ColumnAttribute> columnNames) {

		// reset columns of recent statement
		boundColumns.clear();

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
		logger.trace("simple select");
		return str.toString();

	}

	public String buildStmtForTable(String tablename,
			HashMap<String, Object> values) throws SQLException,
			TableBindingNotRegisteredException {

		// reset columns of recent statement
		boundColumns.clear();

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

		iter = keys.iterator();
		ColumnAttribute attr = null;
		while (iter.hasNext()) {
			String key = iter.next();
			attr = columnNames.get(key);

			if (logger.isTraceEnabled())
				logger.trace(key + " -> IsPK: " + attr.isPrimaryKey());

			if (attr.isPrimaryKey() == true) {
				boundColumns.add(key);
			}
		}
		if (boundColumns.size() == 0) {
			throw new SQLException(
					"Select is impossible:\nNo PrimaryKey columns found!");
		}

		String[] tokens;
		String currcol;

		for (int index = 0; index < boundColumns.size(); index++) {
			currcol = boundColumns.get(index);
			if (currcol.contains(".")) {
				tokens = currcol.split("\\."); // "." has to be escaped!
				str.append(markColumnName(tokens[1]) + "= ?");
			} else {
				str.append(markColumnName(currcol) + "= ?");
			}

		}
		logger.trace("PK select");
		return str.toString();
	}

	public String buildInsertStmtForTable(String table,
			HashMap<String, Object> values) {

		// reset columns of recent statement
		boundColumns.clear();

		StringBuilder str = new StringBuilder();

		str.append("insert into ");
		str.append(markTableName(table) + " (");

		Set<String> keys = values.keySet();
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			str.append(markColumnName(key));
			boundColumns.add(key);
			if (iter.hasNext() == true) {
				str.append(" , ");
			}
		}
		str.append(")");
		str.append(" values (");
		iter = keys.iterator();

		while (iter.hasNext()) {

			iter.next();
			str.append("?");

			if (iter.hasNext() == true) {
				str.append(" , ");
			}
		}
		str.append(")");
		return str.toString();
	}

	public String buildUpdateStmtForTable(String table,
			HashMap<String, Object> values, String whereStmt)
			throws SQLException, TableBindingNotRegisteredException {

		Vector<String> pkColumns = new Vector<String>();

		// reset columns of recent statement
		boundColumns.clear();

		StringBuilder str = new StringBuilder();

		str.append("update ");
		str.append(markTableName(table) + " SET ");

		Set<String> keys = values.keySet();
		Iterator<String> iter = keys.iterator();

		while (iter.hasNext()) {
			String key = iter.next();

			if (logger.isTraceEnabled())
				logger.trace("--> " + key);

			str.append(markTableAndColumnNameForUpdate(table, key));
			str.append("=?");
			boundColumns.add(key);
			if (iter.hasNext() == true) {
				str.append(" , ");
			}
		}
		if (whereStmt != null && whereStmt.isEmpty() == false) {
			str.append(" " + whereStmt);
		} else {

			if (logger.isTraceEnabled())
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

				if (logger.isTraceEnabled())
					logger.trace(key + " -> IsPK: " + attr.isPrimaryKey());

				if (attr.isPrimaryKey() == true) {
					pkColumns.add(key);
					boundColumns.add(key);
				}
			}
			if (pkColumns.size() == 0) {
				throw new SQLException(
						"Update impossible:\nNo whereStmt given and no PrimaryKey columns found!");
			}
			str.append(" where ");

			for (int index = 0; index < pkColumns.size(); index++) {

				str.append(markTableName(table) + "."
						+ markColumnName(pkColumns.get(index)) + "=?");

				if (index < (pkColumns.size() - 1)) {
					str.append(" and ");
				}
			}
		}
		return str.toString();
	}

	public String toDateString(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat(
				StmtExecInterface.SQLIF_STD_DATE_FORMAT + " "
						+ StmtExecInterface.SQLIF_STD_TIME_FORMAT);
		return sdf.format(date);

	}

	public List<String> getCols2Handle() {
		return boundColumns;
	}

	public abstract String markTableName(String tableName);

	public abstract String markColumnName(String columnName);

}
