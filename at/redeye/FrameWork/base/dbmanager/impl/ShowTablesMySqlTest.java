/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.FrameWork.base.dbmanager.impl;

import at.redeye.FrameWork.base.transaction.Transaction;
import java.util.Collection;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author martin
 */
public class ShowTablesMySqlTest {
    
    public ShowTablesMySqlTest() {
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
     * Test of isVersionNewerThan method, of class ShowTablesMySql.
     */
    @Test
    public void testIsVersionNewerThan() {
        System.out.println("isVersionNewerThan");
        
        
        boolean result = ShowTablesMySql.isVersionNewerThan("", 5, 0, 3);
        assertEquals(false, result);
       
        result = ShowTablesMySql.isVersionNewerThan("5.5.25-log", 5, 0, 3);
        assertEquals(true, result);
        
        result = ShowTablesMySql.isVersionNewerThan("5.5.5", 5, 0, 3);
        assertEquals(true, result);
        
        result = ShowTablesMySql.isVersionNewerThan("xxxx", 5, 0, 3);
        assertEquals(false, result);
        
        result = ShowTablesMySql.isVersionNewerThan("5.0.1", 5, 0, 3);
        assertEquals(false, result);        
    }

}
