/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.dbmanager.test;

import at.redeye.FrameWork.base.LocalRoot;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.dbmanager.DBManager;
import at.redeye.FrameWork.base.dbmanager.impl.BaseCreateSql;
import at.redeye.FrameWork.base.dbmanager.impl.CreateSqlMySql;
import at.redeye.FrameWork.base.dbmanager.impl.DatabaseManager;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.UserManagement.bindtypes.DBPb;

import java.sql.SQLException;
import java.util.Collection;
import org.apache.log4j.BasicConfigurator;

/**
 *
 * @author Administrator
 */
public class TestApp {

    public static void main(String[] args) {
        
        Root root = new LocalRoot("");
        
        BasicConfigurator.configure();
	try {
            root.loadDBConnectionFromSetup();
	} catch (NoClassDefFoundError ex) {
            System.out.println(ex);
	}
        
        Transaction trans = root.getDBConnection().getNewTransaction();
        
        BaseCreateSql creator = new CreateSqlMySql() {};
        
        String sql = creator.createSqlforTable(new DBPb());
        
        System.out.println(sql);        
        
        DBManager dbm = new DatabaseManager( trans );
        try {
            
            Collection<String> tables = dbm.getTables();
            
            for( String s : tables )
                System.out.println( "Table: " + s );
            
            System.out.println( "return code: " + dbm.createTable(new DBPb()) );
            trans.commit();
            trans.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println(ex);
        }
    }
}
