/**
 * 
 */
package at.redeye.SqlDBInterface.SqlDBIO.impl.creator;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import at.redeye.SqlDBInterface.SqlDBIO.TypeRegistrationInterface;
import at.redeye.SqlDBInterface.SqlDBIO.impl.ColumnAttribute;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;

/**
 * @author Mario Mattl
 * 
 */
public class StmtCreatorOracle extends AbstractStmtCreator {

	public StmtCreatorOracle(TypeRegistrationInterface registration) {

		super(registration);
	}

	@Override
	public String markTableName(String tableName) {
		return "\"" + tableName.toUpperCase() + "\"";
	}

	@Override
	public String markColumnName(String columnName) {
		return "\"" + columnName.toLowerCase() + "\"";

	}

	public String buildInsertStmtForTable(String table,
			HashMap<String, Object> values) {

		StringBuilder str = new StringBuilder();

		str.append("insert into ");
		str.append(markTableName(table) + " (");

		Set<String> keys = values.keySet();
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext()) {
			// Oracle: Ignore columns that have empty strings
			String col = iter.next();
			Object tester = values.get(col);
			if ((tester instanceof String || tester instanceof Date)
					&& tester.toString().isEmpty()) {
				continue;
			}
			str.append(markColumnName(col));
			if (iter.hasNext() == true) {
				str.append(" , ");
			}
		}
		str.append(")");
		str.append(" values (");
		iter = keys.iterator();

		while (iter.hasNext()) {
			String key = iter.next();
			Object ele = values.get(key);
			if (ele instanceof String || ele instanceof Date) {
				if (ele.toString().isEmpty()) {
					continue;
				} else {
					str.append("?");
					boundColumns.add(key);
				}
			}

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
			logger.trace("--> " + key);
			str.append(markTableName(table) + "." + markColumnName(key));
			boundColumns.add(key);
			str.append("=?");

			if (iter.hasNext() == true) {
				str.append(" , ");
			}
		}
		if (whereStmt != null && whereStmt.isEmpty() == false) {
			str.append(" " + whereStmt);
		} else {

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

}
