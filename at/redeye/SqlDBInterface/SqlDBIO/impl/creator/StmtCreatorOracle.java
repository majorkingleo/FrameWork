/**
 * 
 */
package at.redeye.SqlDBInterface.SqlDBIO.impl.creator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

	@Override
	public String buildInsertStmtForTable(String table,
			HashMap<String, Object> values) {

                boundColumns.clear();
		StringBuilder str = new StringBuilder();

		str.append("insert into ");
		str.append(markTableName(table)).append(" (");

                boolean added_entry = false;

		Set<String> keys = values.keySet();
		Iterator<String> iter = keys.iterator();

		while (iter.hasNext()) {
			String key = iter.next();
			Object ele = values.get(key);
			if (ele instanceof String || ele instanceof Date) {
				if (ele.toString().isEmpty()) {
					continue;
				}
			}

                    if (added_entry) {
                        str.append(" , ");
                        added_entry = false;
                    }

                    str.append(markColumnName(key));
                    added_entry = true;

		}

		str.append(")");
		str.append(" values (");
		iter = keys.iterator();

                added_entry = false;

		while (iter.hasNext()) {
			String key = iter.next();
			Object ele = values.get(key);                        
			if (ele instanceof String || ele instanceof Date) {
				if (ele.toString().isEmpty()) {                                         
					continue;
				} 
			}

                    if (added_entry) {
                        str.append(" , ");
                        added_entry = false;
                    }

                    str.append("?");
                    boundColumns.add(key);
                    added_entry = true;

		}
		str.append(")");
		return str.toString();
	}

	@Override
	public String buildUpdateStmtForTable(String table,
			HashMap<String, Object> values, String whereStmt)
			throws SQLException, TableBindingNotRegisteredException {

		List<String> pkColumns = new ArrayList<String>();

		// reset columns of recent statement
		boundColumns.clear();
		StringBuilder str = new StringBuilder();

		str.append("update ");
		str.append(markTableName(table)).append(" SET ");

		Set<String> keys = values.keySet();
		Iterator<String> iter = keys.iterator();

		while (iter.hasNext()) {

			String key = iter.next();

			if (logger.isTraceEnabled()) {
				logger.trace("--> " + key);
			}
			str.append(markTableName(table)).append(".")
					.append(markColumnName(key));

			// Oracle: It is not allowed to set to null, 
			// so we don't bind empty columns.
			Object tester = values.get(key);
			if (tester instanceof String && ((String) tester).isEmpty()) {
				str.append("=' '");
			} else if (tester instanceof Date && tester.toString().isEmpty()) {
				str.append("='").append(toDateString(new Date(0))).append("'"); // reset date
			} else {
				boundColumns.add(key);
				str.append("=?");
			}

			if (iter.hasNext() == true) {
				str.append(" , ");
			}
		}
		if (whereStmt != null && whereStmt.isEmpty() == false) {
			str.append(" ").append(whereStmt);
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Searching table: " + table);
			}

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
				if (logger.isTraceEnabled()) {
					logger.trace(key + " -> IsPK: " + attr.isPrimaryKey());
				}
				if (attr.isPrimaryKey() == true) {
					pkColumns.add(key);
					boundColumns.add(key);
				}
			}
			if (pkColumns.isEmpty()) {
				throw new SQLException(
						"Update impossible:\nNo whereStmt given and no PrimaryKey columns found!");
			}
			str.append(" where ");

			for (int index = 0; index < pkColumns.size(); index++) {
				str.append(markTableName(table)).append(".")
						.append(markColumnName(pkColumns.get(index)))
						.append("=?");

				if (index < (pkColumns.size() - 1)) {
					str.append(" and ");
				}
			}
		}
		return str.toString();
	}
}
