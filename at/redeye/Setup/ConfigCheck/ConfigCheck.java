/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Setup.ConfigCheck;

import at.redeye.FrameWork.base.Root;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public abstract class ConfigCheck
{
    protected static Logger logger = Logger.getLogger(ConfigCheck.class.getName());

    protected Root root;
    protected String check_name;

    public ConfigCheck(Root root, String name) {
        this.root = root;
        check_name = name;
    }

    public abstract boolean doIHaveRequiredFeature();

    public String getName() {
        return check_name;
    }
}
