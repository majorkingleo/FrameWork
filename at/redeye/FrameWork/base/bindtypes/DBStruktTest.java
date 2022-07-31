/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.FrameWork.base.bindtypes;

import java.util.ArrayList;
import at.redeye.FrameWork.base.bindtypes.DBStruktTestCases.DBMoreComplex;
import java.util.Map.Entry;
import at.redeye.FrameWork.base.bindtypes.DBStruktTestCases.DBSimple;
import at.redeye.FrameWork.base.sequence.bindtypes.DBSequences;
import at.redeye.SqlDBInterface.SqlDBIO.impl.ColumnAttribute;
import at.redeye.SqlDBInterface.SqlDBIO.impl.DBDataType;
import at.redeye.UserManagement.bindtypes.DBPb;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author martin
 */
public class DBStruktTest {
    
    public DBStruktTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of add method, of class DBStrukt.
     */
    @Test
    public void testAdd_DBValue() {
        
        System.out.println("add");
        DBValue value = new DBInteger("id2");
        DBStrukt instance = new DBSimple();
        instance.add(value);
                
        assertEquals(instance.elements_with_version.size(), 2);
    }

    /**
     * Test of remove method, of class DBStrukt.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        
        DBSimple instance = new DBSimple();
        instance.remove(instance.id);
                
        assertEquals(instance.elements_with_version.size(), 0);
              
    }

    /**
     * Test of add method, of class DBStrukt.
     */
    @Test
    public void testAdd_DBValue_Integer() {
        System.out.println("add");
        DBValue value = new DBString("test",10);
        Integer version = 2;
        DBStrukt instance = new DBSimple();
        instance.add(value, version);
        instance.setVersion(version);

        // We got one entry for version 2
        assertEquals(1,instance.getHashMapForVersion(version).size());
        
        assertEquals(1,instance.getHashMapForVersion(1).size());
    }

    /**
     * Test of add method, of class DBStrukt.
     */
    @Test
    public void testAdd_DBStrukt() {
        System.out.println("add");
        DBStrukt substrukt = new DBHistory("HIST");
        DBStrukt instance = new DBSimple();
        instance.add(substrukt);
        
        assertEquals(1, instance.countSubStrukts());
    }

    /**
     * Test of consume method, of class DBStrukt.
     */
    @Test
    public void testConsume_HashMap() {
        System.out.println("consume");
        
        {
            System.out.println("consume DBSimple");
            
            HashMap<String, Object> map = new HashMap();
            DBSimple instance = new DBSimple();            
            map.put("id", 1);                                          
            instance.consume(map);
            
            DBSimple expResult = new DBSimple();
            expResult.id.loadFromCopy(1);
            
            compareStrukts( expResult, instance );
        }
        
        {
            System.out.println("consume DBSimple mixed Case Letter");
            
            HashMap<String, Object> map = new HashMap();
            DBSimple instance = new DBSimple();            
            map.put("ID", 1);                                          
            instance.consume(map);
            
            DBSimple expResult = new DBSimple();
            expResult.id.loadFromCopy(1);
            
            compareStrukts( expResult, instance );
        }        
        
        {
            System.out.println("consume DBMoreComplex");
            
            HashMap<String, Object> map = new HashMap();
            DBMoreComplex instance = new DBMoreComplex();            
            map.put("id", 1);                                                      
            map.put("hist_id", 2);                     
            
            
            DBMoreComplex expResult = new DBMoreComplex();
            expResult.id.loadFromCopy(1);
            expResult.hist.setAeHist("test");
            expResult.hist_id.loadFromCopy(2);
            
            map.put("HIST_ae_zeit", expResult.hist.ae_zeit.getValue());
            map.put("HIST_ae_user", expResult.hist.ae_user.getValue());
            
            instance.consume(map);
                        
            compareStrukts( expResult, instance );
        }        
    }
    
/**
     * Test of consume method, of class DBStrukt.
     */
    @Test
    public void testConsumeFast_HashMap() {
        System.out.println("consumeFast");
        
        {
            System.out.println("consumeFast DBSimple");
            
            HashMap<String, Object> map = new HashMap();
            DBSimple instance = new DBSimple();            
            map.put("id", 1);                                          
            instance.consume(map);
            
            DBSimple expResult = new DBSimple();
            expResult.id.loadFromCopy(1);
            
            compareStrukts( expResult, instance );
        }
        
        {
            System.out.println("consumeFast DBSimple mixed Case Letter");
            
            HashMap<String, Object> map = new HashMap();
            DBSimple instance = new DBSimple();            
            map.put("ID", 1);                                          
            instance.consume(map);
            
            DBSimple expResult = new DBSimple();
            expResult.id.loadFromCopy(1);
            
            compareStrukts( expResult, instance );
        }        
        
        {
            System.out.println("consumeFast DBMoreComplex");
            
            HashMap<String, Object> map = new HashMap();
            DBMoreComplex instance = new DBMoreComplex();            
            map.put("id", 1);                                                      
            map.put("hist_id", 2);                     
            
            
            DBMoreComplex expResult = new DBMoreComplex();
            expResult.id.loadFromCopy(1);
            expResult.hist.setAeHist("test");
            expResult.hist_id.loadFromCopy(2);
            
            map.put("HIST_ae_zeit", expResult.hist.ae_zeit.getValue());
            map.put("HIST_ae_user", expResult.hist.ae_user.getValue());
            
            instance.consume(map);
                        
            compareStrukts( expResult, instance );
        }        
    }    
    
    private void compareStrukts( DBStrukt expStrukt, DBStrukt result )
    {
        assertEquals(expStrukt.getName(), result.getName());
        
        compareHashMaps(expStrukt.getHashMapAndData(), result.getHashMapAndData());
    }

    /**
     * Test of consume method, of class DBStrukt.
     */
    @Test
    public void testConsume_HashMap_String() {
        System.out.println("consume");
        /*
        HashMap<String, Object> map = null;
        String prefix = "";
        DBStrukt instance = null;
        instance.consume(map, prefix);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
         * 
         */
    }

    /**
     * Test of getName method, of class DBStrukt.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        
        DBStrukt instance = new DBSimple();
        String expResult = DBSimple.NAME;
        String result = instance.getName();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getValue method, of class DBStrukt.
     */
    @Test
    public void testGetValue_int() {
        System.out.println("getValue");
        
        int idx = 0;
        DBSimple instance = new DBSimple();
        DBValue expResult = instance.id;
        DBValue result = instance.getValue(idx);
        assertEquals(expResult, result);
       
    }

    /**
     * Test of getValue method, of class DBStrukt.
     */
    @Test
    public void testGetValue_DBValue() {
        System.out.println("getValue");
        
        DBSimple instance = new DBSimple();
        DBValue val = instance.id;        
        DBValue expResult = instance.id;
        DBValue result = instance.getValue(val);
        assertEquals(expResult.getValue(), result.getValue());
        
    }

    /**
     * Test of getValue method, of class DBStrukt.
     */
    @Test
    public void testGetValue_String() {
        System.out.println("getValue");
        
        DBSimple instance = new DBSimple();          
        DBValue expResult = instance.id;        
        
        String name = instance.id.getName();
        
        DBValue result = instance.getValue(name);
        assertEquals(expResult, result);        
    }

    /**
     * Test of countValues method, of class DBStrukt.
     */
    @Test
    public void testCountValues() {
        System.out.println("countValues");
        DBStrukt instance = new DBSimple();
        int expResult = 1;
        int result = instance.countValues();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of countSubStrukts method, of class DBStrukt.
     */
    @Test
    public void testCountSubStrukts() {
        System.out.println("countSubStrukts");
        DBStrukt instance = new DBPb();
        int expResult = 1;
        int result = instance.countSubStrukts();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getSubStrukt method, of class DBStrukt.
     */
    @Test
    public void testGetSubStrukt() {
        System.out.println("getSubStrukt");                
        int idx = 0;
        DBPb instance = new DBPb();
        DBStrukt expResult = instance.hist;
        DBStrukt result = instance.getSubStrukt(idx);
        assertEquals(expResult.getName(), result.getName());
        
    }

    /**
     * Test of getHashMap method, of class DBStrukt.
     */
    @Test
    public void testGetHashMap_0args() {
        System.out.println("getHashMap");
        
        {
            System.out.println("getHashMap DBSimple");
            DBSimple instance = new DBSimple();
            HashMap<String, ColumnAttribute> expResult = new HashMap();
            expResult.put(instance.id.getName(), new ColumnAttribute(DBDataType.DB_TYPE_INTEGER));
            HashMap<String, ColumnAttribute> result = instance.getHashMap();
            compareHashMaps(result, expResult);
        }

        {
            System.out.println("getHashMap DBSequences");
            DBSequences instance = new DBSequences();
            HashMap<String, ColumnAttribute> expResult = new HashMap();
            expResult.put(instance.name.getName(), new ColumnAttribute(true,DBDataType.DB_TYPE_STRING,instance.name.max_len));
            expResult.put(instance.value.getName(), new ColumnAttribute(DBDataType.DB_TYPE_INTEGER));
            HashMap<String, ColumnAttribute> result = instance.getHashMap();
            compareHashMaps(result, expResult);
        }
        
        {
            System.out.println("getHashMap DBPb");
            DBPb instance = new DBPb();
            HashMap<String, ColumnAttribute> expResult = new HashMap();
            expResult.put(instance.name.getName(), new ColumnAttribute(DBDataType.DB_TYPE_STRING, instance.name.max_len));
            expResult.put(instance.id.getName(), new ColumnAttribute(true,DBDataType.DB_TYPE_INTEGER));
            expResult.put(instance.locked.getName(), new ColumnAttribute(DBDataType.DB_TYPE_INTEGER));
            expResult.put(instance.login.getName(), new ColumnAttribute(DBDataType.DB_TYPE_STRING, instance.login.max_len));
            expResult.put(instance.plevel.getName(), new ColumnAttribute(DBDataType.DB_TYPE_INTEGER));
            expResult.put(instance.pwd.getName(), new ColumnAttribute(DBDataType.DB_TYPE_STRING, instance.pwd.max_len));
            expResult.put(instance.surname.getName(), new ColumnAttribute(DBDataType.DB_TYPE_STRING, instance.surname.max_len));
            expResult.put(instance.title.getName(), new ColumnAttribute(DBDataType.DB_TYPE_STRING,instance.title.max_len));
            
            String prefix = instance.hist.getName() + "_";
            
            expResult.put(prefix + instance.hist.ae_user.getName(), new ColumnAttribute(DBDataType.DB_TYPE_STRING,instance.hist.ae_user.max_len));
            expResult.put(prefix + instance.hist.an_user.getName(), new ColumnAttribute(DBDataType.DB_TYPE_STRING,instance.hist.an_user.max_len));
            expResult.put(prefix + instance.hist.lo_user.getName(), new ColumnAttribute(DBDataType.DB_TYPE_STRING,instance.hist.lo_user.max_len));
            expResult.put(prefix + instance.hist.an_zeit.getName(), new ColumnAttribute(DBDataType.DB_TYPE_DATETIME));
            expResult.put(prefix + instance.hist.ae_zeit.getName(), new ColumnAttribute(DBDataType.DB_TYPE_DATETIME));
            expResult.put(prefix + instance.hist.lo_zeit.getName(), new ColumnAttribute(DBDataType.DB_TYPE_DATETIME));
            
            
            
            HashMap<String, ColumnAttribute> result = instance.getHashMap();
            compareHashMaps(result, expResult);
        }        
    }
    
    private <T> void compareHashMaps( HashMap<String, T> result,  HashMap<String, T> expResult )
    {        
        if( expResult.size() != result.size() )
        {
            fail(" size not equal epxtected: " + expResult.size() + " but is " + result.size() );
        }
        
        assertEquals(expResult.size(), result.size());
        
        for( Entry<String, T> e_res : result.entrySet() )
        {
            System.out.println(" => " + e_res.getKey());
            
            T col_exp = expResult.get(e_res.getKey());
            
            if( col_exp == null ) {
                fail(e_res.getKey() + " not found in expected results" );
            }
            
            assertEquals(col_exp, e_res.getValue());                        
        }        
    }

    /**
     * Test of getHashMap method, of class DBStrukt.
     */
    @Test
    public void testGetHashMap_String() {
        System.out.println("getHashMap");
        
                
        {
            System.out.println("getHashMap DBSimple");
            DBSimple instance = new DBSimple();
            HashMap<String, ColumnAttribute> expResult = new HashMap();
            String prefix = "dummy_";
            expResult.put(prefix + instance.id.getName(), new ColumnAttribute(DBDataType.DB_TYPE_INTEGER));
            HashMap<String, ColumnAttribute> result = instance.getHashMap(prefix);
            compareHashMaps(result, expResult);
        }        
    }

    /**
     * Test of VersionExists method, of class DBStrukt.
     */
    @Test
    public void testVersionExists() {
        System.out.println("VersionExists");
        
        DBSimple instance = new DBSimple();
        
        DBValue testVal = new DBInteger("test");
        instance.add(testVal, 2);
        instance.setVersion(2);                
        
        DBValue val = testVal;
        Integer Version = 2;        
        boolean expResult = true;
        boolean result = instance.VersionExists(val, Version);
        assertEquals(expResult, result);                        
    }

    /**
     * Test of getHashMapForVersion method, of class DBStrukt.
     */
    @Test
    public void testGetHashMapForVersion() {
        System.out.println("getHashMapForVersion");
        
        {
            System.out.println("getHashMapForVersion DBSimple");
            DBSimple instance = new DBSimple();
            instance.add( new DBInteger("test"),2);
            instance.setVersion(2);
            
            HashMap<String, ColumnAttribute> expResult = new HashMap();
            expResult.put("test", new ColumnAttribute(DBDataType.DB_TYPE_INTEGER));
            
            HashMap<String, ColumnAttribute> result = instance.getHashMapForVersion(2);
            compareHashMaps(result, expResult);
        }
        
    }

    /**
     * Test of getHashMap method, of class DBStrukt.
     */
    @Test
    public void testGetHashMap_String_Integer() {
        System.out.println("getHashMap");
        
        {
            System.out.println("getHashMap DBSimple");
            DBSimple instance = new DBSimple();
            instance.add( new DBInteger("test"),2);
            instance.setVersion(2);
            
            String prefix = "dummy_";
            
            HashMap<String, ColumnAttribute> expResult = new HashMap();
            expResult.put(prefix + "test", new ColumnAttribute(DBDataType.DB_TYPE_INTEGER));
            
            HashMap<String, ColumnAttribute> result = instance.getHashMap(prefix, 2);
            compareHashMaps(result, expResult);
        }        
        
    }

    /**
     * Test of getHashMapAndData method, of class DBStrukt.
     */
    @Test
    public void testGetHashMapAndData_0args() {
        System.out.println("getHashMapAndData");
        
        {
            System.out.println("getHashgetHashMapAndData DBSimple");
            DBSimple instance = new DBSimple();
            instance.id.loadFromCopy(1);
            
            HashMap<String, Object> expResult = new HashMap();
            expResult.put(instance.id.getName(), 1);
            
            HashMap<String, Object> result = instance.getHashMapAndData();
            compareHashMaps(result, expResult);
        }
                
    }

    /**
     * Test of getHashMapAndData method, of class DBStrukt.
     */
    @Test
    public void testGetHashMapAndData_String() {
        System.out.println("getHashMapAndData");
        
        
        {
            System.out.println("getHashgetHashMapAndData DBSimple");
            DBSimple instance = new DBSimple();
            instance.id.loadFromCopy(1);
            
            String prefix = "dummy_";
            
            HashMap<String, Object> expResult = new HashMap();
            expResult.put(prefix + instance.id.getName(), 1);
            
            HashMap<String, Object> result = instance.getHashMapAndData(prefix);
            compareHashMaps(result, expResult);
        }        
              
    }

    /**
     * Test of getAllValues method, of class DBStrukt.
     */
    @Test
    public void testGetAllValues() {
        System.out.println("getAllValues");
        
        DBSimple instance = new DBSimple();
        instance.id.loadFromCopy(42);
        
        ArrayList<DBValue> expResult = new ArrayList();
        DBInteger id = new DBInteger("id");
        id.loadFromCopy(42);
        expResult.add(id);
        
        ArrayList<DBValue> result = instance.getAllValues();
        
        assertEquals(expResult.size(), result.size());
        
        for( int i = 0; i < result.size(); i++ )
        {
            assertEquals(expResult.get(i).getValue(), result.get(i).getValue());
        }

    }

    /**
     * Test of getAllNames method, of class DBStrukt.
     */
    @Test
    public void testGetAllNames_0args() {
        System.out.println("getAllNames");
        
        
        DBStrukt instance = new DBSimple();
        ArrayList<String> expResult = new ArrayList();
        expResult.add("id");
        
        ArrayList<String> result = instance.getAllNames();
        assertEquals(expResult, result);

    }

    /**
     * Test of getAllNames method, of class DBStrukt.
     */
    @Test
    public void testGetAllNames_String() {
        System.out.println("getAllNames");
        
        DBStrukt instance = new DBSimple();
        ArrayList<String> expResult = new ArrayList();
        expResult.add("hugoid");
        
        ArrayList<String> result = instance.getAllNames("hugo");
        assertEquals(expResult, result);        
    }

    /**
     * Test of getNewOne method, of class DBStrukt.
     */
    @Test
    public void testGetNewOne() {
        System.out.println("getNewOne");
        DBStrukt instance = new DBSimple();                        
        DBStrukt result = instance.getNewOne();
        assertEquals(instance.getTitle(), result.getTitle());
        assertNotSame(instance, result);
    }

    /**
     * Test of getTitle method, of class DBStrukt.
     */
    @Test
    public void testGetTitle() {
        System.out.println("getTitle");
        DBStrukt instance = new DBSimple();
        String expResult = DBSimple.TITLE;
        String result = instance.getTitle();
        assertEquals(expResult, result);       
    }

    /**
     * Test of loadFromCopy method, of class DBStrukt.
     */
    @Test
    public void testLoadFromCopy() {
        System.out.println("loadFromCopy");
        
        DBSimple simple = new DBSimple();
        simple.id.loadFromCopy(42);
        
        DBSimple simple2 = new DBSimple();
        simple2.loadFromCopy(simple);
        
        assertEquals(simple2.id.getValue(), simple.id.getValue());
        assertNotSame(simple2, simple);
    }

    /**
     * Test of getCopy method, of class DBStrukt.
     */
    @Test
    public void testGetCopy() {
        System.out.println("getCopy");
        
        DBSimple instance = new DBSimple();
        instance.id.loadFromCopy(42);
        
        DBSimple expResult = (DBSimple) instance.getCopy();
        
        assertEquals(expResult.id.getValue(), instance.id.getValue());
        assertNotSame(expResult, instance);
    }

    /**
     * Test of setVersion method, of class DBStrukt.
     */
    @Test
    public void testSetVersion() {
        System.out.println("setVersion");
        
        DBStrukt instance = new DBSimple();
        instance.setVersion(2);
                
        assertEquals((int)2, (int)instance.getVersion());
    }

    /**
     * Test of getVersion method, of class DBStrukt.
     */
    @Test
    public void testGetVersion() {
        System.out.println("getVersion");       
        
        assertEquals((int)1, (int)(new DBSimple().getVersion()));
    }
}
