/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Setup.ConfigCheck.Checks;

import at.redeye.FrameWork.base.Root;
import at.redeye.Setup.ConfigCheck.ConfigCheck;

/**
 *
 * @author martin
 */
public class InitialRun extends ConfigCheck
{
    public InitialRun(Root root)
    {
        super( root, "is this the initial run");
    }
    
    public boolean doIHaveRequiredFeature()
    {
         return !root.getSetup().initialRun();
    }
}
