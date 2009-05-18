/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;

import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public abstract class AutoLogger {
    
    protected Logger logger;    
    protected Exception thrown_ex = null;
    protected boolean failed = true;
    public Object result = null;
    
    public AutoLogger( String className )
    {
        logger = Logger.getLogger(className);
        
        invoke();
    }
    
    private void invoke()
    {
        try {
            do_stuff();
            failed = false;
        } catch (SQLException ex) {               
            logger.error("Exception: " + ex.toString());
            thrown_ex = ex;
            ex.printStackTrace();
        } catch (TableBindingNotRegisteredException ex) {
            logger.error("Exception: " + ex.toString());
            thrown_ex = ex;
            ex.printStackTrace();
        } catch (UnsupportedDBDataTypeException ex) {
            logger.error("Exception: " + ex.toString());
            thrown_ex = ex;
            ex.printStackTrace();
        } catch (WrongBindFileFormatException ex) {
            logger.error("Exception: " + ex.toString());
            thrown_ex = ex;
            ex.printStackTrace();
        } catch (CloneNotSupportedException ex) {
            logger.error("Exception: " + ex.toString());
            thrown_ex = ex;
            ex.printStackTrace();
        } catch ( Exception ex ) {
            logger.error("Exception: " + ex.toString());
            thrown_ex = ex;
            ex.printStackTrace();
        }
    }
            
    public abstract void do_stuff() throws Exception;
    
    public boolean isFailed()
    {
        return failed;
    }
}
