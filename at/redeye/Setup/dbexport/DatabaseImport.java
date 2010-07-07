/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Setup.dbexport;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.dbmanager.DBBindtypeManager;
import at.redeye.FrameWork.base.dbmanager.DBManager;
import at.redeye.FrameWork.base.dbmanager.impl.DatabaseManager;
import at.redeye.FrameWork.base.dbmanager.impl.bindtypes.DBTableVersion;
import at.redeye.FrameWork.base.transaction.DerbyTransaction;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.DeleteDir;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.FrameWork.utilities.UnZip;
import at.redeye.FrameWork.utilities.Zip;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.ConnectionDefinition;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.SupportedDBMSTypes;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MissingConnectionParamException;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.UnSupportedDatabaseException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class DatabaseImport
{
    public static Logger logger = Logger.getLogger(DatabaseImport.class.getName());

    public static class CannotOpenTempDatabase extends Exception
    {
        public CannotOpenTempDatabase( String message )
        {
            super( message );
        }

        public CannotOpenTempDatabase()
        {
            super();
        }
    }

    Root root;
    String source_file_name;
    Transaction trans_temp;
    Transaction trans_target;
    File temp_db_dir;
    ProgressListener listener = null;

    /**
     * exports a databse to a zip file, by using a derby database.
     * Always call close when finished, or an error appeared. This
     * cleans up the tempdir stuff.
     * @param root
     * @param target_file_name
     */
    public DatabaseImport( Root root, String source_file_name )
    {
        this.source_file_name = source_file_name;
        this.root = root;
    }

    public void setProgressListener( ProgressListener progress_listener )
    {
        listener = progress_listener;
    }

    protected void fireEvent( String event )
    {
        if( listener != null )
        {
            logger.info(event);
            listener.setStage(event);
        }
    }

    public void doImport() throws IOException, CannotOpenTempDatabase, ClassNotFoundException, SQLException, MissingConnectionParamException, UnSupportedDatabaseException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException
    {
       temp_db_dir = File.createTempFile("tempdb", ".db");

       temp_db_dir.delete();
       temp_db_dir.mkdir();

       fireEvent("Entpacke Archiv");

       UnZip.unzip(temp_db_dir, source_file_name);

       trans_temp = openTempDatabase();

       trans_target = root.getDBConnection().getNewTransaction();

       fireEvent("Lösche existierende Datenbank");
        // alle bisherigen Tabellen löschen
       DBManager db_manager = root.getDBManager();

       root.getBindtypeManager().setTransaction(trans_target);

       Vector<DBStrukt> tables = root.getBindtypeManager().getRegisteredTables();

       int count = tables.size()*2-1;

        if( listener != null )
           listener.setOverallCounter(count);

       // konrolliere, ob das auch nur annähernd die richtige Datenbank sein kann
       DatabaseManager manager_temp = new DatabaseManager(trans_temp);

       Collection<String> source_tables = manager_temp.getTables();

       for( DBStrukt table : tables )
       {
           boolean found = false;

           for( String source_table : source_tables )
           {
               manager_temp.register(table);
               
               if( table.getName().equals(source_table) )
               {
                   found = true;
                   break;
               }
           }

           if( !found )
           {
               throw new CannotOpenTempDatabase("Tabelle " + table.getName() + " konnte in Quelldatenbank nicht gefunden werden. " +
                       "Import abgebrochen, keine Daten wurden importiert, oder gelöscht");
           }
       }

       count = 0;

       for( DBStrukt table : tables )
       {
           logger.info("Lösche Tabelle " + table.getName() );

           fireEvent("Lösche Tabelle " + table.getName() );

           if( db_manager.tableExists(table.getName()) )
           {
               if (!db_manager.drop_table(table)) {
                   throw new CannotOpenTempDatabase("Tabelle " + table.getName() + " konnte nicht gelöscht werden!");
               }
           }

           if( listener != null )
                listener.setCounter(++count);
       }

       if( !root.getBindtypeManager().autocreate() )
            throw new  CannotOpenTempDatabase("Die Datenbank konnte nicht mehr eingerichtet werden" );

       // wenn das ein alter export ist, dann eventuelle Spalten anlegen, damit wir
       // das graffel importieren können.
       if( !manager_temp.autocreate() )
            throw new  CannotOpenTempDatabase("Die Datenbank konnte nicht geladen werden" );

       DBTableVersion version = new DBTableVersion();

       for( DBStrukt table : tables )
       {
           if( table.getName().equals(version.getName()))
            continue;

           logger.info("Importiere Tabelle " + table.getName() );

           fireEvent("Importiere Tabelle " + table.getName() );

           Vector<DBStrukt> imp_table = trans_temp.fetchTable(table);

           String imp_format = "Importiere Tabelle %s (%d von %d)";

           fireEvent(String.format(imp_format,table.getName(),0,imp_table.size()));

           int row_counter = 0;

           for( DBStrukt it : imp_table )
           {
               trans_target.insertValues(it);               

               row_counter++;

               if( row_counter % 100 == 0 )
               {
                   if( row_counter % 1000 == 0 )
                   {
                    // commits sind relativ teuer, daher nur alle 1000 mal
                    trans_target.commit();
                   }

                   fireEvent(String.format(imp_format,table.getName(),row_counter,imp_table.size()));
               }
           }

           trans_target.commit();

           if( listener != null )
                listener.setCounter(++count);
       }
    }

    private Transaction openTempDatabase() throws IOException, CannotOpenTempDatabase, ClassNotFoundException, SQLException, MissingConnectionParamException, UnSupportedDatabaseException
    {        
        if( !root.getBindtypeManager().is_dbms_driver_loaded(SupportedDBMSTypes.DB_JAVADB) )
            throw new CannotOpenTempDatabase("Missing Derby DB Driver, which is required for exporting databases!");

        ConnectionDefinition connparams = new ConnectionDefinition(
               "",
               0,
               "",
               "",
               temp_db_dir.getAbsolutePath(),
               SupportedDBMSTypes.DB_JAVADB
               );

        Transaction t = new DerbyTransaction(connparams);

        return t;
    }

    /**
     * clean temporary stuff up
     * @return true on success
     */
    boolean close()
    {
        try {

            if( trans_temp != null )
                trans_temp.close();

            trans_temp = null;

            root.getDBConnection().closeTransaction(trans_target);
            trans_target = null;

        } catch( SQLException ex ) {
            logger.error(StringUtils.exceptionToString(ex));
            return false;
        }

        if( temp_db_dir != null )
        {
            if (temp_db_dir.exists())
            {
                logger.info("removing directory " + temp_db_dir);
                if (!DeleteDir.deleteDirectory(temp_db_dir)) {
                    logger.error("couldn't remove temporary Directory: " + temp_db_dir);
                    return false;
                }
            }
            temp_db_dir = null;
        }

        root.getBindtypeManager().setTransaction(null);

        return true;
    }
}
