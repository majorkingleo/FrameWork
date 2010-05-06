/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Setup.ConfigCheck;


import at.redeye.FrameWork.base.Root;
import java.util.Collection;
import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 * 
 * @author martin
 */
public class CheckConfigBase {

        protected static Logger logger = Logger.getLogger(CheckConfigBase.class.getName());
	protected Root root;
        protected Collection<ConfigCheck> checks;


	public CheckConfigBase(Root root) {
		this.root = root;
		checks = new LinkedList<ConfigCheck>();
	}

        public void addCheck(ConfigCheck check )
        {
            checks.add(check);
        }

        public boolean shouldPopUpWizard()
        {
           for (ConfigCheck check : checks)
           {
             if (!check.doIHaveRequiredFeature()) 
             {
                logger.info("check " + check.getName() + " failed.");
                return true;
             }
            }
            return false;
        }	
}
