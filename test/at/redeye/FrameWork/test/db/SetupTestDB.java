/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.test.db;

import at.redeye.FrameWork.base.BaseModuleLauncher;
import at.redeye.FrameWork.base.FrameWorkConfigDefinitions;
import at.redeye.FrameWork.base.LocalRoot;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.Setup;
import at.redeye.FrameWork.base.dbmanager.DBBindtypeManager;
import at.redeye.FrameWork.base.prm.bindtypes.DBConfig;
import at.redeye.FrameWork.base.sequence.bindtypes.DBSequences;
import at.redeye.FrameWork.base.transaction.MySQLTransaction;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.SqlDBInterface.SqlDBConnection.DbConnectionInterface;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.ConnectionDefinition;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.DBConnector;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MissingConnectionParamException;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.SupportedDBMSTypes;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.UnSupportedDatabaseException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.UserManagement.bindtypes.DBPb;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author martin
 */
public class SetupTestDB extends BaseModuleLauncher implements SetupTestDBInterface
{
    public SetupTestDB()
    {
        root = new LocalRoot("FRAMEWORK-TEST","FRAMEWORK-TEST", false, false);

        this.configureLogging();
    }

    @Override
    public void invoke() throws ClassNotFoundException, UnSupportedDatabaseException, SQLException, MissingConnectionParamException
    {
        FrameWorkConfigDefinitions.registerDefinitions();
        root.getBindtypeManager().register(new DBPb());
        root.getBindtypeManager().register(new DBSequences());
        root.getBindtypeManager().register(new DBConfig());

        autocreateInternalDB();
    }

    private boolean autocreateInternalDB() throws ClassNotFoundException, UnSupportedDatabaseException, SQLException, MissingConnectionParamException
    {
        String db_name = "test";

        ConnectionDefinition connparams = new ConnectionDefinition(
               "localhost",
               0,
               "root",
               "kingleo",
               db_name,
               SupportedDBMSTypes.DB_MYSQL
               );

        DbConnectionInterface connint = new DBConnector(connparams);

        Connection my_db_conn = connint.connectToDatabase();

        if( my_db_conn.isClosed() )
        {
            logger.error("Erzeugen der Datenbank " + db_name + " nicht mögglich");
            return false;
        }

        Transaction t = new MySQLTransaction(connparams);

        DBBindtypeManager bindtypeManager = root.getBindtypeManager();

        bindtypeManager.setTransaction(t);

        if( bindtypeManager.autocreate() )
        {
             logger.info("Datenbank erfolgreich eingerichtet");
             t.commit();
        }
        else
        {
             logger.error( "Fehler beim Einrichten der Datenbank!" );
             t.rollback();
        }

        t.close();
        my_db_conn.close();

        root.getSetup().setLocalConfig(Setup.DBDatabase, db_name);
        root.getSetup().setLocalConfig(Setup.DBHost, "localhost");
        root.getSetup().setLocalConfig(Setup.DBUser, "root");
        root.getSetup().setLocalConfig(Setup.DBPasswd, "kingleo");
        root.getSetup().setLocalConfig(Setup.DBPort, "");
        root.getSetup().setLocalConfig(Setup.DBInstance, "test");
        root.getSetup().setLocalConfig(Setup.DBType, SupportedDBMSTypes.DB_MYSQL.toString());

         if( !root.loadDBConnectionFromSetup() )
         {
             logger.error("Fehler beim Laden der Datenbankverbindung vom Setup");
             return false;
         }

        root.saveSetup();

        return true;
    }

    @Override
    public String getVersion() {
        return "0.1";
    }

    @Override
    public Root getRoot() {
        return root;
    }

    @Override
    public void close() {
        root.getDBConnection().close();
    }

    @Override
    public DBPb getDB4User(String login_name) throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException
    {
        Transaction trans = root.getDBConnection().getDefaultTransaction();

        DBPb pb = new DBPb();

        List<DBPb> pbs = trans.fetchTable2(pb, "where " + trans.markColumn(pb.login) + "='" + login_name + "'");

        if( pbs.isEmpty() )
            return null;

        return pbs.get(0);
    }

}
