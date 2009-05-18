package at.redeye.SqlDBInterface.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import at.redeye.SqlDBInterface.SqlDBConnection.MOMMDbConnectionInterface;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.ConnectionDefinition;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MOMMDBConnector;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MOMMSupportedDBMSTypes;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MissingConnectionParamException;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.UnSupportedDatabaseException;
import at.redeye.SqlDBInterface.SqlDBIO.MOMMStmtExecInterface;
import at.redeye.SqlDBInterface.SqlDBIO.MOMMTypeRegistrationInterface;
import at.redeye.SqlDBInterface.SqlDBIO.impl.MOMMDBDataType;
import at.redeye.SqlDBInterface.SqlDBIO.impl.MOMMDefaultStmtExecuter;
import at.redeye.SqlDBInterface.SqlDBIO.impl.MOMMTypeRegistration;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;


public class TestApplication {

	/**
	 * @param args
	 * @throws TableBindingNotRegisteredException
	 */
	
	private static Logger logger = Logger.getLogger(TestApplication.class
		      .getSimpleName());
	
	public static void main(String[] args)
			throws TableBindingNotRegisteredException {
		
		
		BasicConfigurator.configure();

		ConnectionDefinition connparams = new ConnectionDefinition
		("", 0,
				"root", 
				"mysql", 
				"test", 
				MOMMSupportedDBMSTypes.DB_MYSQL);

					
		MOMMDbConnectionInterface connint = new MOMMDBConnector(connparams);
		
		MOMMTypeRegistrationInterface regi = new MOMMTypeRegistration (connparams.getDBMSType());

		Vector<Vector<?>> result;
		Vector<HashMap<String, Object>> result2;
		Connection my_db_conn = null;
		try {

			my_db_conn = connint.connectToDatabase();
			logger.info("Connection open: "+ !my_db_conn.isClosed());
			
			
			
			MOMMStmtExecInterface invoker = (MOMMStmtExecInterface) new MOMMDefaultStmtExecuter(
					my_db_conn, connparams.getDBMSType());
			

			String stmt = "select DUMMY.id, DUMMY.name, DUMMY.adress, INFO.text1, INFO.valid, DUMMY.zeit "+
				"from DUMMY, INFO where INFO.id = DUMMY.id";

			Vector<MOMMDBDataType> argslist = new Vector<MOMMDBDataType>();
			argslist.add(MOMMDBDataType.DB_TYPE_INTEGER);
			argslist.add(MOMMDBDataType.DB_TYPE_STRING);
			argslist.add(MOMMDBDataType.DB_TYPE_STRING);
			argslist.add(MOMMDBDataType.DB_TYPE_STRING);
			argslist.add(MOMMDBDataType.DB_TYPE_BIT);
			argslist.add(MOMMDBDataType.DB_TYPE_STRING);
			
//			String stmt = "select persnr, count (*) from p2laz group by persnr";
//			Vector<MOMMDBDataType> argslist = new Vector<MOMMDBDataType>();
//			argslist.add(MOMMDBDataType.DB_TYPE_STRING);
//			argslist.add(MOMMDBDataType.DB_TYPE_LONG);

			result = invoker.fetchColumnValue(stmt, argslist);
			String out = "";
			for (Vector<?> row : result) {
				for (Object object : row) {
					out += object + "\t\t";
				}
				logger.info(out);
				out = "";
			}
			

			logger.info("\n========================== TEST2: =============================\n");

			stmt = "select DUMMY.id, DUMMY.name, DUMMY.adress, INFO.text1, INFO.text2 from INFO, DUMMY where DUMMY.id = INFO.id";

			regi.registerTableBindings("dummy.bind");
			regi.registerTableBindings("info.bind");
			String[] tabnames = { "DUMMY", "INFO" };

			result2 = invoker.fetchTableValue(
					tabnames, "where DUMMY.id = INFO.id");

			logger.info(invoker.printFetchTableOutput(result2));

			logger.info("\n========================== TEST3: ============================\n");

			String[] tabnames2 = { "INFO" };
			result2 = invoker.fetchTableValue(tabnames2, null);
			logger.info(invoker.printFetchTableOutput(result2));
			int anz = 0;
			logger.info("\n========================== TEST4: ============================\n");

			GregorianCalendar cal = new GregorianCalendar();
			

			HashMap <String, Object> values = new HashMap<String, Object>();
			values.put("text1", "STRUCT UPDATE ZOCKT AUCH");
			values.put("text2", cal.getTime().toString());
			values.put("id", new Integer(2));
			anz = invoker.updateTableValues("INFO", values, null);
			logger.info(anz +" rows updated ");
//			anz = invoker.updateValues("update INFO set text1 = '"+cal.getTime().toString()+"'");
			
			logger.info("\n========================== TEST5: ============================\n");
			
			result2 = invoker.fetchTableValue(tabnames2, null);
			logger.info(invoker.printFetchTableOutput(result2));
			
			logger.info("\n========================== TEST6: ============================\n");
//			
//			values.clear();
//			values.put("text1", "INSERT ZOCKT AUCH");
//			values.put("text2", "WUNDERBAR");
//			values.put("valid", new Integer(1));
//			logger.info("Inserted: "+invoker.insertTableValues("INFO", values));
//			my_db_conn.commit();
//			
//			values.put("text2", "MOBBIBÄR");
//			values.put("valid", true);
//			anz = invoker.updateTableValues("INFO", values, "where id = 3");
//			my_db_conn.commit();
//			result2 = invoker.fetchTableValue(tabnames2, null);
//			logger.info(invoker.printFetchTableOutput(result2)+"\n-> "+anz);
			
			result2.clear();
			HashMap<String, Object> record = new HashMap<String, Object>();
			record.put("id", new Integer (2));
			record = invoker.fetchTableValue("INFO", record);
			logger.info(invoker.printFetchTableOutput(record));
			
			logger.info("\n========================== TEST7: ============================\n");
			regi.registerTableBindings("tpa.bind");
//			regi.registerTableBindings("tek.bind");
//			regi.registerTableBindings("tep.bind");
			String[] tabnames3 = {"tpa" };
			result2 = invoker.fetchTableValue(tabnames3, "");
			logger.info(invoker.printFetchTableOutput(result2));
			values.clear();
			values.put("aktpos_feldid", "POOL");
			values.put("tanr", new Integer(1));
			values.put("teid", "ffffffffffffffffff");
			anz = invoker.updateTableValues("TPA", values, "");
			my_db_conn.commit();
			result2 = invoker.fetchTableValue(tabnames3, "");
			logger.info("\n-> " + anz + invoker.printFetchTableOutput(result2));
			
//			anz = invoker.updateValues("update tpa set aktpos_feldid = ziel_feldid");
//			
//			my_db_conn.commit();
//			result2 = invoker.fetchTableValue(tabnames3, "");
//			
//			logger.info(invoker.printFetchTableOutput(result2)+"\n-> "+anz);
		
		} catch (ClassNotFoundException e) {
		
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnSupportedDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedDBDataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongBindFileFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MissingConnectionParamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				connint.disconnectDatabase(my_db_conn);
				logger.info("Connection closed: "+my_db_conn.isClosed());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

}
