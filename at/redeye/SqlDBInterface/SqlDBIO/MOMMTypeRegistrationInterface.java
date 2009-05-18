package at.redeye.SqlDBInterface.SqlDBIO;

import java.io.IOException;
import java.util.HashMap;

import at.redeye.SqlDBInterface.SqlDBIO.impl.MOMMColumnAttribute;
import at.redeye.SqlDBInterface.SqlDBIO.impl.MOMMDBDataType;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;

public interface MOMMTypeRegistrationInterface {

	/**
	 * 
	 * @param filename
	 * @throws IOException
	 * @throws UnsupportedDBDataTypeException
	 * @throws WrongBindFileFormatException
	 */
	public void registerTableBindings(String filename) throws IOException,
			UnsupportedDBDataTypeException, WrongBindFileFormatException;

	/**
	 * 
	 * @param data
	 *            Usage: <br>
	 *            HashMap (String tablename, <br>
	 *            &nbsp;&nbsp;&nbsp;&nbsp;HashMap(String column_name,
	 *            MOMMColumnAttribute column_attributes) ) <br>
	 * @throws UnsupportedDBDataTypeException
	 * @throws WrongBindFileFormatException
	 */
	public void registerTableBindings(
			HashMap<String, HashMap<String, MOMMColumnAttribute>> data)
			throws UnsupportedDBDataTypeException, WrongBindFileFormatException;

	/**
	 * 
	 * @param ident
	 *            The type identifier.
	 * @return The associated database type (MOMMDBDataType)
	 * @throws UnsupportedDBDataTypeException
	 */
	public MOMMDBDataType getRealDBType(String ident)
			throws UnsupportedDBDataTypeException;

	/**
	 * 
	 * @return The whole registration of table bindings
	 */
	public HashMap<String, HashMap<String, MOMMColumnAttribute>> getAllRegisteredTables();

	/**
	 * 
	 * @param tablename
	 * @return The table bindings for the given table
	 */
	public HashMap<String, MOMMColumnAttribute> getRegisteredTableByString(
			String tablename);
}
