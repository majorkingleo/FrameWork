/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.FrameWork.test;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBDateTime;
import at.redeye.FrameWork.base.bindtypes.DBFlagInteger;
import at.redeye.FrameWork.base.bindtypes.DBHistory;
import at.redeye.FrameWork.base.bindtypes.DBInteger;
import at.redeye.FrameWork.base.bindtypes.DBString;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.test.db.SetupTestDB;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class PerfTestTransaction {
    
    Logger logger = Logger.getLogger(PerfTestTransaction.class);
    
    public static class DBTest extends DBStrukt
    {
        DBInteger     id = new DBInteger("id");
        DBString      user = new DBString("user",255);
        DBFlagInteger locked = new DBFlagInteger("locked");
        DBHistory     hist = new DBHistory("hist");
        DBInteger     group = new DBInteger("group");
        
        public DBTest()
        {
            super( "TEST" );
            
            add(id);
            add(user);
            add(locked);            
            add(hist);            
            add(group, 2);
            
            setVersion(2);
        }
        
        
        @Override
        public DBStrukt getNewOne() {
            return new DBTest();
        }        
    }
    
    Transaction trans;
    Root root;
    
    public PerfTestTransaction( Transaction trans, Root root )
    {
        this.trans = trans;                
        this.root = root;
    }            
    
    void createTestData() throws SQLException, UnsupportedDBDataTypeException, WrongBindFileFormatException, IOException
    {
        root.getBindtypeManager().setTransaction(trans);
       
        DBTest test = new DBTest();
        
        if( root.getDBManager().tableExists(test.getName()))
            root.getDBManager().drop_table(new DBTest());     
        
        root.getBindtypeManager().register(new DBTest());        
        root.getBindtypeManager().autocreate();
                                
         for( int i = 0; i < 10000; i++ )
         {
             test.id.loadFromCopy(i);
             test.user.loadFromString(root.getUserName());
             test.group.loadFromCopy(i);
             test.hist.setAnHist(root.getUserName());
             
             trans.insertValues(test);
         }                        
    }
    
    void readTestData() throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException
    {
        long now = System.currentTimeMillis();        
        
        List<DBTest> res = trans.fetchTable2(new DBTest());
        
        long duration = System.currentTimeMillis() - now;
        
        System.out.println("fetched " + res.size() + " data in " + duration + "ms ");
    }
    
    public static void main(String args[]) {
        new AutoLogger(PerfTestTransaction.class.getName()) {

            @Override
            public void do_stuff() throws Exception {
                SetupTestDB setup_test_db = new SetupTestDB();
                setup_test_db.invoke();
                Root root = setup_test_db.getRoot();
                Transaction trans = root.getDBConnection().getDefaultTransaction();
                                
                
                PerfTestTransaction test = new PerfTestTransaction(trans,root);                                
                
     //           test.createTestData();
                trans.commit();
                
                test.readTestData();                
                trans.commit();
            }
        };
    }
}
