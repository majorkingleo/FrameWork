package at.redeye.SqlDBInterface.SqlDBIO.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;

import at.redeye.SqlDBInterface.SqlDBConnection.impl.SupportedDBMSTypes;
import at.redeye.SqlDBInterface.SqlDBIO.TypeRegistrationInterface;

public class TypeRegistration implements TypeRegistrationInterface {

	private SupportedDBMSTypes dbmstype_;
	private HashMap<String, DBDataType> typeMatchTable_;
	private static HashMap<String, HashMap<String, ColumnAttribute>> registeredTables_;

	private static final String SEPERATOR_SIGN = ",";

	private static Logger logger = Logger.getLogger(TypeRegistration.class
			.getSimpleName());

	public TypeRegistration(SupportedDBMSTypes dbmstype) {
		this.dbmstype_ = dbmstype;
		TypeRegistration.registeredTables_ = new HashMap<String, HashMap<String, ColumnAttribute>>();
		setTypeMatchTable();
	}

	protected void setTypeMatchTable() {

		HashMap<String, DBDataType> typeMatchTable = new HashMap<String, DBDataType>();

		switch (dbmstype_) {
		case DB_SQLITE:
		case DB_MSSQL:
		case DB_MYSQL:
		case DB_ORACLE:
			typeMatchTable.put("string", DBDataType.DB_TYPE_STRING);
			typeMatchTable.put("long", DBDataType.DB_TYPE_LONG);
			typeMatchTable.put("int", DBDataType.DB_TYPE_INTEGER);
			typeMatchTable.put("double", DBDataType.DB_TYPE_DOUBLE);
			typeMatchTable.put("short", DBDataType.DB_TYPE_SHORT);
			typeMatchTable.put("char", DBDataType.DB_TYPE_STRING);
			typeMatchTable.put("varchar", DBDataType.DB_TYPE_STRING);
			typeMatchTable.put("varchar2", DBDataType.DB_TYPE_STRING);
			typeMatchTable.put("float", DBDataType.DB_TYPE_FLOAT);
			typeMatchTable.put("date", DBDataType.DB_TYPE_DATE);
			typeMatchTable.put("datetime", DBDataType.DB_TYPE_DATETIME);
			typeMatchTable.put("time", DBDataType.DB_TYPE_TIME);
			typeMatchTable.put("timestamp", DBDataType.DB_TYPE_LONG);
			typeMatchTable.put("bool", DBDataType.DB_TYPE_BOOLEAN);
			typeMatchTable.put("bit", DBDataType.DB_TYPE_BIT);

			break;
		default:
			break;

		}
		typeMatchTable_ = typeMatchTable;

	}

	/**
	 * 
	 * @param ident
	 *            A string that defines the data type of column
	 * @return DBDataType: The real DB_TYPE
	 * @throws UnsupportedDBDataTypeException
	 *             , if type from file cannot be converted
	 */
	public DBDataType getRealDBType(String ident)
			throws UnsupportedDBDataTypeException {

		DBDataType dbDataType = typeMatchTable_.get(ident);
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
		HashMap<String, ColumnAttribute> parsedTypes = new HashMap<String, ColumnAttribute>();
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
			ColumnAttribute colattr = new ColumnAttribute(
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
			HashMap<String, HashMap<String, ColumnAttribute>> data)
			throws UnsupportedDBDataTypeException, WrongBindFileFormatException {

		if (data == null || data.size() == 0) {
			throw new WrongBindFileFormatException(
					"Invalid registration data: null or size is zero!");
		}
		Set<String> tables = data.keySet();

		HashMap<String, ColumnAttribute> columns = null;
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

	public HashMap<String, HashMap<String, ColumnAttribute>> getAllRegisteredTables() {

		return registeredTables_;

	}

	public HashMap<String, ColumnAttribute> getRegisteredTableByString(
			String tablename) {

		return registeredTables_.get(tablename);

	}

}
