/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.FrameWork.base.transaction;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;

import org.joda.time.DateMidnight;

import at.redeye.FrameWork.base.bindtypes.DBDateTime;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.bindtypes.DBValue;
import at.redeye.FrameWork.base.sequence.Sequence;
import at.redeye.FrameWork.base.sequence.impl.CommonSequence;
import at.redeye.SqlDBInterface.SqlDBConnection.DbConnectionInterface;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.ConnectionDefinition;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.DBConnector;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MissingConnectionParamException;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.SupportedDBMSTypes;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.UnSupportedDatabaseException;
import at.redeye.SqlDBInterface.SqlDBIO.StmtExecInterface;
import at.redeye.SqlDBInterface.SqlDBIO.TypeRegistrationInterface;
import at.redeye.SqlDBInterface.SqlDBIO.impl.ColumnAttribute;
import at.redeye.SqlDBInterface.SqlDBIO.impl.DBDataType;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TypeRegistration;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.executor.DefaultStmtExecuter;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;

/**
 * 
 * @author martin
 */
public abstract class Transaction {

        protected static final Logger logger = Logger.getLogger(Transaction.class.getName());
    
	protected DbConnectionInterface iface;
	protected StmtExecInterface executer;
	protected Connection conn;
	protected ConnectionDefinition definition;
	protected TypeRegistrationInterface regi;
	protected Sequence sequence;

	protected Transaction() {
	}

	public Transaction(ConnectionDefinition def) throws ClassNotFoundException,
			SQLException, MissingConnectionParamException,
			UnSupportedDatabaseException {
		definition = def;
		iface = new DBConnector(definition);

		conn = iface.connectToDatabase();

		executer = (StmtExecInterface) new DefaultStmtExecuter(conn,
				definition.getDBMSType());

		regi = new TypeRegistration(definition.getDBMSType());

		switch (definition.getDBMSType()) {
		case DB_ORACLE: /* fall through */
			// maybe we use Oracle sequences in future
		default:
			sequence = new CommonSequence();
			break;
		}
	}

	public boolean isOpen() throws SQLException {
		if (conn == null) {
			return false;
		}
		return !conn.isClosed();
	}

	public boolean close() throws SQLException {
		if (iface == null || conn == null) {
			return false;
		}
		iface.disconnectDatabase(conn);
		return conn.isClosed();
	}

	public void commit() throws SQLException {
		conn.commit();
	}

	public void rollback() throws SQLException {
		conn.rollback();
	}

	public List<List<?>> fetchColumnValue(String stmt, List<DBDataType> typelist)
			throws SQLException, UnsupportedDBDataTypeException {
		return executer.fetchColumnValue(stmt, typelist);
	}
        
	public List<List<?>> fetchColumnValue(String stmt, DBDataType ... typelist)
			throws SQLException, UnsupportedDBDataTypeException {
            
                ArrayList<DBDataType> list = new ArrayList();
                
                for( DBDataType type : typelist )
                    list.add(type);
            
		return executer.fetchColumnValue(stmt, list);
	}        

	public List<?> fetchOneColumnValue(String stmt, DBDataType type )
			throws SQLException, UnsupportedDBDataTypeException {
            
                ArrayList<DBDataType> list = new ArrayList();
                
                list.add(type);
                
                List<List<?>> ergliste = executer.fetchColumnValue(stmt, list);
                
                ArrayList res = new ArrayList();
                
                for( List<?> e : ergliste )
                {           
                    Object o = e.get(0);
                    res.add(o);
                }
                
                return res;
	}                
        
	public List<DBStrukt> fetchTable(DBStrukt binddesc) throws SQLException,
			TableBindingNotRegisteredException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException {
		return fetchTable(binddesc, "");
	}

	protected void registerTable(DBStrukt binddesc)
			throws UnsupportedDBDataTypeException, WrongBindFileFormatException {

		if (!regi.getAllRegisteredTables().containsKey(binddesc.getName())) {
			HashMap<String, ColumnAttribute> colls = binddesc.getHashMap();
			HashMap<String, HashMap<String, ColumnAttribute>> table = new HashMap<String, HashMap<String, ColumnAttribute>>();

			table.put(binddesc.getName(), colls);

			regi.registerTableBindings(table);
		}
	}

	public List<DBStrukt> fetchTable(DBStrukt binddesc, String where)
			throws SQLException, TableBindingNotRegisteredException,
			UnsupportedDBDataTypeException, WrongBindFileFormatException {

		registerTable(binddesc);

		String tablenames[] = { binddesc.getName() };

		List<HashMap<String, Object>> result = executer.fetchTableValue(
				tablenames, where);

		List<DBStrukt> res = new ArrayList<DBStrukt>();

		for (int i = 0; i < result.size(); i++) {
			DBStrukt strukt = binddesc.getNewOne();
			strukt.consumeFast(result.get(i));
			res.add(strukt);
		}

		return res;
	}

        /**
         * 
         * @param <T>
         * @param binddesc
         * @return an empty list if nothing was found
         * @throws SQLException
         * @throws TableBindingNotRegisteredException
         * @throws UnsupportedDBDataTypeException
         * @throws WrongBindFileFormatException 
         */
	public <T extends DBStrukt> List<T> fetchTable2(T binddesc)
			throws SQLException, TableBindingNotRegisteredException,
			UnsupportedDBDataTypeException, WrongBindFileFormatException {
		return fetchTable2(binddesc, "");
	}

        /**
         * 
         * @param <T>
         * @param binddesc
         * @param where
         * @return an empty list if nothing was found
         * @throws SQLException
         * @throws TableBindingNotRegisteredException
         * @throws UnsupportedDBDataTypeException
         * @throws WrongBindFileFormatException 
         */
	public <T extends DBStrukt> List<T> fetchTable2(T binddesc, String where)
			throws SQLException, TableBindingNotRegisteredException,
			UnsupportedDBDataTypeException, WrongBindFileFormatException {

		registerTable(binddesc);

		String tablenames[] = { binddesc.getName() };

		List<HashMap<String, Object>> result = executer.fetchTableValue(
				tablenames, where);

		List<T> res = new ArrayList<T>(result.size());

		for (int i = 0; i < result.size(); i++) {
			DBStrukt strukt = binddesc.getNewOne();
			strukt.consumeFast(result.get(i));
			res.add((T) strukt);
		}

		return res;
	}

	public <T extends DBStrukt> ArrayList<T> fetchTableList(T binddesc)
			throws SQLException, TableBindingNotRegisteredException,
			UnsupportedDBDataTypeException, WrongBindFileFormatException {
		return fetchTableList(binddesc, "");
	}

	public <T extends DBStrukt> ArrayList<T> fetchTableList(T binddesc,
			String where) throws SQLException,
			TableBindingNotRegisteredException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException {

		registerTable(binddesc);

		String tablenames[] = { binddesc.getName() };

		List<HashMap<String, Object>> result = executer.fetchTableValue(
				tablenames, where);

		ArrayList<T> res = new ArrayList<T>(result.size());

		for (int i = 0; i < result.size(); i++) {
			DBStrukt strukt = binddesc.getNewOne();
			strukt.consumeFast(result.get(i));
			res.add(i, (T) strukt);
		}

		return res;
	}

	public String getSql() {
		return executer.getLastStmt();
	}

	public int updateValues(String stmt) throws SQLException {
		return executer.updateValues(stmt);
	}

	public int updateValues(DBStrukt binddesc) throws SQLException,
			UnsupportedDBDataTypeException, WrongBindFileFormatException,
			TableBindingNotRegisteredException, IOException {
		return updateValues(binddesc, null);
	}

	public int updateValues(DBStrukt binddesc, String where)
			throws SQLException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException, TableBindingNotRegisteredException,
			IOException {
		registerTable(binddesc);

		HashMap<String, Object> data = binddesc.getHashMapAndData();
		return executer.updateTableValues(binddesc.getName(), data, where);
	}

	public boolean fetchTableWithPrimkey(DBStrukt binddesc)
			throws UnsupportedDBDataTypeException,
			WrongBindFileFormatException, SQLException,
			TableBindingNotRegisteredException, IOException {
		registerTable(binddesc);

		HashMap<String, Object> data = binddesc.getHashMapAndData();
		HashMap<String, Object> res = executer.fetchTableValue(
				binddesc.getName(), data);

		if (res == null)
			return false;

		binddesc.consumeFast(res);

		return true;
	}
        
        /**
         * Deletes a entry using the binddescs primary key
         * @param binddesc
         * @return true on success, false when not found
         * @throws UnsupportedDBDataTypeException
         * @throws WrongBindFileFormatException
         * @throws SQLException
         * @throws TableBindingNotRegisteredException
         * @throws IOException 
         */
	public boolean deleteWithPrimaryKey(DBStrukt binddesc)
			throws UnsupportedDBDataTypeException,
			WrongBindFileFormatException, SQLException,
			TableBindingNotRegisteredException, IOException {
		registerTable(binddesc);

		String sql = "delete from " + markTable(binddesc) + " where ";
                
                 HashMap<String, ColumnAttribute> data = binddesc.getHashMap();
                 
                 int count = 0;
                 
                 for( Entry<String,ColumnAttribute> e : data.entrySet() )
                 {                     
                     if( e.getValue().isPrimaryKey() )
                     {
                         if( count > 0 ) {
                             sql += ", ";
                         }
                     
                        sql += markColumn(e.getKey()) + " = ";
                        
                        switch( e.getValue().getDatatype() )
                        {
                            case DB_TYPE_DOUBLE:
                            case DB_TYPE_FLOAT:
                            case DB_TYPE_INTEGER:
                            case DB_TYPE_LONG:
                            case DB_TYPE_BIT:
                                sql += binddesc.getValue(e.getKey());
                                break;
                                
                            default:
                                sql += "'" + binddesc.getValue(e.getKey()) + "'";
                                break;
                        }
                                          
                        count++;
                     }
                 }
                 
                logger.debug(sql);
                 
                if( updateValues(sql) == 1 )
                {
                    return true;
                }

		return false;
	}        

	public int insertValues(DBStrukt binddesc)
			throws UnsupportedDBDataTypeException,
			WrongBindFileFormatException, SQLException, IOException {
		registerTable(binddesc);
		HashMap<String, Object> data = binddesc.getHashMapAndData();
		return executer.insertTableValues(binddesc.getName(), data);
	}

	public StmtExecInterface getStmtExecInterface() {
		return executer;
	}

	public TypeRegistrationInterface getTypeRegistration() {
		return regi;
	}

	public String markTable(String in) {
		return executer.getStmtCreator().markTableName(in);
	}

	public String markTable(DBStrukt table) {
		return executer.getStmtCreator().markTableName(table.getName());
	}

	public String markColumn(String in) {
		return executer.getStmtCreator().markColumnName(in);
	}

	public String markColumn(DBValue val) {
		return executer.getStmtCreator().markColumnName(val.getName());
	}
        
        /**
         * Erzeugt TABELLE.name paar zb: PB.login, korrekt hervorgehoben für das DBMS
         * @param table
         * @param column
         * @return 
         */
        public String markColumn( DBStrukt table, DBValue column ) {
            return markTable( table ) + "." + markColumn( column );
        }

	public abstract String getDayStmt(String column, DBDateTime day);

	public abstract String getDayStmt(String column, String dayStr);

	public abstract String getDayStmt(String string, Date toDate);

	public abstract String getDayStmt(String string, DateMidnight day);

        public abstract String getDayStmt(String string, LocalDate day);

	public String getDayStmt(DBDateTime to, DateMidnight dateMidnight) {
		return getDayStmt(to.getName(), dateMidnight);
	}

	public String getDayStmt(DBDateTime to, LocalDate date) {
		return getDayStmt(to.getName(), date);
	}

	public abstract String getPeriodStmt(String column, DBDateTime begin,
			DBDateTime end);

	public abstract String getPeriodStmt(String column, String beginStr,
			String endStr);

	public abstract String getPeriodStmt(String string, DateMidnight dm_from,
			DateMidnight dm_to);

	public abstract String getPeriodStmt(String string, LocalDate dm_from,
			LocalDate dm_to);

        public String getPeriodStmt(DBValue column, DateMidnight dm_from,
			DateMidnight dm_to)
        {
            return getPeriodStmt( column.getName(), dm_from, dm_to);
        }

        /**
         * 
         * @param column
         * @param dm_from
         * @param dm_to
         * @return
         */
        public String getPeriodStmt(DBValue column, LocalDate dm_from,
			LocalDate dm_to)
        {
            return getPeriodStmt( column.getName(), dm_from, dm_to);
        }

	/**
	 * @return: column1 &gt;= date && column2 &lt; date
	 */
	public abstract String getPeriodStmt(String column1, String column2,
			DateMidnight date);

	/**
	 * @return: column1 &gt;= date && column2 &lt; date
	 */        
	public abstract String getPeriodStmt(String column1, String column2,
			LocalDate date);

	/**
	 * @return: column1 &gt;= date && column2 &lt; date
	 */        
	public String getPeriodStmt(DBValue column1, DBValue column2,
			DateMidnight dm) {
		return getPeriodStmt(column1.getName(), column2.getName(), dm);
	}
        
	/**         
	 * @return: column1 &gt;= date && column2 &lt; date
	 */
	public String getPeriodStmt(DBValue column1, DBValue column2,
			LocalDate dm) {
		return getPeriodStmt(column1.getName(), column2.getName(), dm);
	}

	public SupportedDBMSTypes getDBMSType() {
		return definition.getDBMSType();
	}

	public int getNewSequenceValue(String seqName, int magic)
			throws SQLException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException, TableBindingNotRegisteredException,
			IOException {
		if (magic != 1234567) {
			logger.error("Unqualified access to Sequence Value!");
		}

		return sequence.getNewSequenceValue(seqName, this);
	}

	public int getNewSequenceValues(String seqName, int number, int magic)
			throws SQLException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException, TableBindingNotRegisteredException,
			IOException {
		if (magic != 1234567) {
			logger.error("Unqualified access to Sequence Value!");
		}

		return sequence.getNewSequenceValues(seqName, number, this);
	}

	public abstract String getGUIFilterWhereStmt(
			Vector<? extends JComponent> fromFilter,
			Vector<? extends JComponent> toFilter);

	public abstract String getHigherDate(String column, DateMidnight dm_from);

	public abstract String getLowerDate(String column, DateMidnight dm_from);

	public String getHigherDate(DBDateTime column, DateMidnight dm_from) {
		return getHigherDate(column.getName(), dm_from);
	}

	public String getLowerDate(DBDateTime column, DateMidnight dm_from) {
		return getLowerDate(column.getName(), dm_from);
	}

	public abstract String getHigherDateExl(String column, DateMidnight dm_from);

	public abstract String getLowerDateExl(String column, DateMidnight dm_from);

	public String getHigherDateExl(DBDateTime column, DateMidnight dm_from) {
		return getHigherDateExl(column.getName(), dm_from);
	}

	public String getLowerDateExl(DBDateTime column, DateMidnight dm_from) {
		return getLowerDateExl(column.getName(), dm_from);
	}

        public abstract String getHigherDate(String column, LocalDate dm_from);

	public abstract String getLowerDate(String column, LocalDate dm_from);

	public String getHigherDate(DBDateTime column, LocalDate dm_from) {
		return getHigherDate(column.getName(), dm_from);
	}

	public String getLowerDate(DBDateTime column, LocalDate dm_from) {
		return getLowerDate(column.getName(), dm_from);
	}

	public abstract String getHigherDateExl(String column, LocalDate dm_from);

	public abstract String getLowerDateExl(String column, LocalDate dm_from);

	public String getHigherDateExl(DBDateTime column, LocalDate dm_from) {
		return getHigherDateExl(column.getName(), dm_from);
	}

	public String getLowerDateExl(DBDateTime column, LocalDate dm_from) {
		return getLowerDateExl(column.getName(), dm_from);
	}

}
