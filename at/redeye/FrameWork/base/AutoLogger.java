/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import at.redeye.FrameWork.utilities.StringUtils;
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
    private boolean failed = true;
    protected boolean logical_failure = false;
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
            logger.error("Exception: " + ex );
            logger.error(StringUtils.exceptionToString(ex));
            thrown_ex = ex;            
        } catch (TableBindingNotRegisteredException ex) {
            logger.error("Exception: " + ex.toString());
            logger.error(StringUtils.exceptionToString(ex));
            thrown_ex = ex;
        } catch (UnsupportedDBDataTypeException ex) {
            logger.error("Exception: " + ex.toString());
            logger.error(StringUtils.exceptionToString(ex));
            thrown_ex = ex;
        } catch (WrongBindFileFormatException ex) {
            logger.error("Exception: " + ex.toString());
            logger.error(StringUtils.exceptionToString(ex));
            thrown_ex = ex;            
        } catch (CloneNotSupportedException ex) {
            logger.error("Exception: " + ex.toString());
            logger.error(StringUtils.exceptionToString(ex));
            thrown_ex = ex;            
        } catch ( Exception ex ) {
            logger.error("Exception: " + ex.toString());
            logger.error(StringUtils.exceptionToString(ex));
            thrown_ex = ex;            
        }
    }
            
    public abstract void do_stuff() throws Exception;
    
    public boolean isFailed()
    {
        if( failed || logical_failure )
            return true;
        
        return false;
    }
    
    protected void setFailed()
    {
        logical_failure = true;
    }
    
    protected void clearFailed()
    {
        logical_failure = false;
    }
}
