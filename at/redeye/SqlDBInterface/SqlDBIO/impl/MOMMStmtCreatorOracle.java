/**
 * 
 */
package at.redeye.SqlDBInterface.SqlDBIO.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import at.redeye.SqlDBInterface.SqlDBIO.MOMMTypeRegistrationInterface;

/**
 * @author Mario Mattl
 * 
 */
public class MOMMStmtCreatorOracle extends MOMMAbstractStmtCreator {

	public MOMMStmtCreatorOracle(MOMMTypeRegistrationInterface registration) {

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
			if (tester instanceof String && tester.toString().isEmpty()) {
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
		boolean usestring;
		while (iter.hasNext()) {
			usestring = false;
			Object ele = values.get(iter.next());
			if (ele instanceof String || ele instanceof Date) {
				if (ele.toString().isEmpty()) {
					continue;
				} else {
					usestring = true;
					str.append("'");
				}
			}
			if (ele instanceof Date) {
				str.append(toDateString((Date)ele));
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

		logger.debug("\n-> Created INSERT-Statement: " + str.toString());

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
			str.append(markTableName(table) + "." + markColumnName(key));
			str.append("=");
			Object ele = values.get(key);
			if (ele instanceof String || ele instanceof Date) {
				str.append("'");
				usestring = true;

			}
			if (ele instanceof String && ele.toString().isEmpty()) {
				str.append(" ");
            } else if( ele instanceof Date ) {
                str.append(toDateString((Date)ele));
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
		if (whereStmt != null && whereStmt.isEmpty() == false) {
			str.append(" " + whereStmt);
		} else {

			Vector<String> whereCols = new Vector<String>();

			logger.trace("Searching table: " + table);

			HashMap<String, MOMMColumnAttribute> cols = registration_
					.getRegisteredTableByString(table);

			keys = cols.keySet();
			if (keys == null) {
				throw new TableBindingNotRegisteredException("Table " + table
						+ " not found in registration!");
			}
			iter = keys.iterator();
			MOMMColumnAttribute attr = null;
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
					str.append(toDateString((Date)data));
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



}
