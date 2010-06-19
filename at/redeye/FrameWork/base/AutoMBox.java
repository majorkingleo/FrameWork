/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import org.apache.log4j.Logger;

import at.redeye.FrameWork.utilities.StringUtils;

import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author martin
 */
public abstract class AutoMBox
{
    protected Logger logger;    
    protected Exception thrown_ex = null;
    protected boolean failed = true;
    protected boolean do_mbox = true;
    public boolean logical_failure = false;
    
    public AutoMBox( String className, boolean do_mbox )
    {
        logger = Logger.getLogger(className);
        this.do_mbox = do_mbox;
        invoke();
    }
    
    public AutoMBox( String className )
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
            logger.error("Exception: " + ex.getMessage());
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
        } catch (Exception ex) {
            logger.error("Exception: " + ex.toString() + "\n" + ex.getLocalizedMessage() );
            logger.error(StringUtils.exceptionToString(ex));
            
            thrown_ex = ex;            
        }
        
        if (thrown_ex != null) {
            if (do_mbox) {
                JOptionPane.showMessageDialog(null,
                        StringUtils.autoLineBreak(
                        "Es ist ein Fehler aufgetreten: " +
                        thrown_ex.getLocalizedMessage()),
                        "Error",
                        JOptionPane.OK_OPTION);
            }
        }
    }
    
    public abstract void do_stuff() throws Exception;
    
    public boolean isFailed()
    {
        if( failed || logical_failure )
            return true;

        return false;
    }
}
