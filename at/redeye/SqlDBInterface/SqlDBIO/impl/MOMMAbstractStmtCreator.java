package at.redeye.SqlDBInterface.SqlDBIO.impl;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import at.redeye.SqlDBInterface.SqlDBIO.MOMMStmtCreatorInterface;
import at.redeye.SqlDBInterface.SqlDBIO.MOMMStmtExecInterface;
import at.redeye.SqlDBInterface.SqlDBIO.MOMMTypeRegistrationInterface;

/**
 * @author Mario Mattl
 * 
 */
public abstract class MOMMAbstractStmtCreator implements
		MOMMStmtCreatorInterface {

	protected static Logger logger = Logger
			.getLogger(MOMMAbstractStmtCreator.class.getSimpleName());

	protected MOMMTypeRegistrationInterface registration_;

	public MOMMAbstractStmtCreator(MOMMTypeRegistrationInterface registration) {
		super();
		this.registration_ = registration;
		logger.setLevel(Level.TRACE);
	}

	protected String markColumnName(String columnName) {
		return columnName;
	}

	protected String markTableName(String table) {
		return table;
	}

	public String buildStmtForTable(String[] tablenames, String whereStmt,
			HashMap<String, MOMMColumnAttribute> columnNames) {

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

		HashMap<String, MOMMColumnAttribute> columnNames = registration_
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
		MOMMColumnAttribute attr = null;
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
			str.append(markTableName(table) + "." + markColumnName(key));
			str.append("=");
			Object ele = values.get(key);
			if (ele instanceof String || ele instanceof Date) {
				str.append("'");
				usestring = true;
			}
			if (ele instanceof Date) {
				str.append(toDateString((Date)ele));
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
	
	public String toDateString(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat(
				MOMMStmtExecInterface.SQLIF_STD_DATE_FORMAT + " "
						+ MOMMStmtExecInterface.SQLIF_STD_TIME_FORMAT);
		return sdf.format(date);

	}

}
