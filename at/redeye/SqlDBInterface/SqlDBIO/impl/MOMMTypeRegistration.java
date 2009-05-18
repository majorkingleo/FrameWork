package at.redeye.SqlDBInterface.SqlDBIO.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;

import at.redeye.SqlDBInterface.SqlDBConnection.impl.MOMMSupportedDBMSTypes;
import at.redeye.SqlDBInterface.SqlDBIO.MOMMTypeRegistrationInterface;

public class MOMMTypeRegistration implements MOMMTypeRegistrationInterface {

	private MOMMSupportedDBMSTypes dbmstype_;
	private HashMap<String, MOMMDBDataType> typeMatchTable_;
	private static HashMap<String, HashMap<String, MOMMColumnAttribute>> registeredTables_;

	private static final String SEPERATOR_SIGN = ",";

	private static Logger logger = Logger.getLogger(MOMMTypeRegistration.class
			.getSimpleName());

	public MOMMTypeRegistration(MOMMSupportedDBMSTypes dbmstype) {
		this.dbmstype_ = dbmstype;
		MOMMTypeRegistration.registeredTables_ = new HashMap<String, HashMap<String, MOMMColumnAttribute>>();
		setTypeMatchTable();
	}

	protected void setTypeMatchTable() {

		HashMap<String, MOMMDBDataType> typeMatchTable = new HashMap<String, MOMMDBDataType>();

		switch (dbmstype_) {
		case DB_SQLITE:
		case DB_MSSQL:
		case DB_MYSQL:
		case DB_ORACLE:
			typeMatchTable.put("string", MOMMDBDataType.DB_TYPE_STRING);
			typeMatchTable.put("long", MOMMDBDataType.DB_TYPE_LONG);
			typeMatchTable.put("int", MOMMDBDataType.DB_TYPE_INTEGER);
			typeMatchTable.put("double", MOMMDBDataType.DB_TYPE_DOUBLE);
			typeMatchTable.put("short", MOMMDBDataType.DB_TYPE_SHORT);
			typeMatchTable.put("char", MOMMDBDataType.DB_TYPE_STRING);
			typeMatchTable.put("varchar", MOMMDBDataType.DB_TYPE_STRING);
			typeMatchTable.put("varchar2", MOMMDBDataType.DB_TYPE_STRING);
			typeMatchTable.put("float", MOMMDBDataType.DB_TYPE_FLOAT);
			typeMatchTable.put("date", MOMMDBDataType.DB_TYPE_DATE);
			typeMatchTable.put("datetime", MOMMDBDataType.DB_TYPE_DATETIME);
			typeMatchTable.put("time", MOMMDBDataType.DB_TYPE_TIME);
			typeMatchTable.put("timestamp", MOMMDBDataType.DB_TYPE_LONG);
			typeMatchTable.put("bool", MOMMDBDataType.DB_TYPE_BOOLEAN);
			typeMatchTable.put("bit", MOMMDBDataType.DB_TYPE_BIT);

			break;

		}
		typeMatchTable_ = typeMatchTable;

	}

	/**
	 * 
	 * @param ident
	 *            A string that defines the data type of column
	 * @return MOMMDBDataType: The real DB_TYPE
	 * @throws UnsupportedDBDataTypeException ,
	 *             if type from file cannot be converted
	 */
	public MOMMDBDataType getRealDBType(String ident)
			throws UnsupportedDBDataTypeException {

		MOMMDBDataType dbDataType = typeMatchTable_.get(ident);
		if (dbDataType == null)
			throw new UnsupportedDBDataTypeException("Could not convert type >"
					+ ident + "<");

		return dbDataType;

	}

	public void registerTableBindings(String filename) throws IOException,
			UnsupportedDBDataTypeException, WrongBindFileFormatException {

		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String row = null;
		String ident = null;
		String[] tokens = null;

		int count = 1;
		HashMap<String, MOMMColumnAttribute> parsedTypes = new HashMap<String, MOMMColumnAttribute>();
		while ((row = reader.readLine()) != null) {
			if (count == 1) {
				ident = row.toUpperCase();
				count++;
				continue;
			}
			tokens = row.split(SEPERATOR_SIGN);
			if (tokens.length < 3) {
				reader.close();
				throw new WrongBindFileFormatException(
						"Invalid number of arguments in bind-file "
								+ filename
								+ " !\nFormat has to be:[ColumnName, ColumnType, PrimaryKey (true/false)]");
			}
			if (tokens[0].isEmpty() || tokens[1].isEmpty()
					|| tokens[2].isEmpty()) {
				reader.close();
				throw new WrongBindFileFormatException(
						"File format error in bind-file "
								+ filename
								+ " !\n"
								+ "Format has to be: [ColumnName, ColumnType, PrimaryKey (true/false)]");
			}
			MOMMColumnAttribute colattr = new MOMMColumnAttribute(
					getRealDBType(tokens[1].trim()));

			if (tokens[2].trim().equalsIgnoreCase("TRUE")) {
				colattr.setPrimaryKey(true);
			}
			parsedTypes.put(ident + "." + tokens[0].trim(), colattr);

		}
		registeredTables_.put(ident, parsedTypes);
		logger.info("-> Registered types for table " + ident + " [SOURCE: "
				+ filename + "]\n");
		reader.close();

	}

	@Override
	public void registerTableBindings(
			HashMap<String, HashMap<String, MOMMColumnAttribute>> data)
			throws UnsupportedDBDataTypeException, WrongBindFileFormatException {

		if (data == null || data.size() == 0) {
			throw new WrongBindFileFormatException(
					"Invalid registration data: null or size is zero!");
		}
		Set<String> tables = data.keySet();

		HashMap<String, MOMMColumnAttribute> columns = null;
		for (String currtable : tables) {
			columns = data.get(currtable);
			if (columns == null) {
				throw new WrongBindFileFormatException(
						"Invalid registration data: Map of columntypes is null!");
			}
			registeredTables_.put(currtable.toUpperCase(), columns);
			logger.info("-> Registered types for table " + currtable);
		}

	}

	public HashMap<String, HashMap<String, MOMMColumnAttribute>> getAllRegisteredTables() {

		return registeredTables_;

	}

	public HashMap<String, MOMMColumnAttribute> getRegisteredTableByString(
			String tablename) {

		return registeredTables_.get(tablename);

	}

}
