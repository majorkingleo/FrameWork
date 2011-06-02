/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import org.apache.log4j.Logger;

import at.redeye.FrameWork.utilities.StringUtils;

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
        } catch (Exception ex) {
            logger.error("Exception: " + ex.toString() + "\n" + ex.getLocalizedMessage(), ex );                        
            thrown_ex = ex;            
        }
        
        if (thrown_ex != null) {
            if (do_mbox) {

                Root root = Root.getLastRoot();

                JOptionPane.showMessageDialog(null,
                        StringUtils.autoLineBreak(
                        root.MlM("Es ist ein Fehler aufgetreten:") + " " +
                        thrown_ex.getLocalizedMessage()),
                        root.MlM("Error"),
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
