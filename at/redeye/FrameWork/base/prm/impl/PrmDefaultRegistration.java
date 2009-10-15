/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.prm.impl;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.prm.PrmListener;
import at.redeye.FrameWork.base.prm.bindtypes.DBConfig;

/**
 *
 * @author mmattl
 */
public class PrmDefaultRegistration {


    public static void attachToPRM (Root root, PrmListener listener,  DBConfig [] parameters) {
        
        DBConfig localPrm = null;
        DBConfig globalPrm = null;

        for (int idx = 0; idx < parameters.length; idx++) {
            localPrm = root.getSetup().getLocalConfig(parameters[idx].getConfigName());
            if (localPrm != null)
                localPrm.addPrmListener(listener);
            globalPrm = root.getSetup().getConfig(parameters[idx].getConfigName());
            if (globalPrm != null)
                globalPrm.addPrmListener(listener);
        }
    }

    public static void detachFromPRM (Root root, PrmListener listener, DBConfig [] parameters) {

        DBConfig localPrm = null;
        DBConfig globalPrm = null;

        for (int idx = 0; idx < parameters.length; idx++) {
            localPrm = root.getSetup().getLocalConfig(parameters[idx].getConfigName());
            if (localPrm != null)
                localPrm.removePrmListener(listener);
            globalPrm = root.getSetup().getConfig(parameters[idx].getConfigName());
            if (globalPrm != null)
                globalPrm.removePrmListener(listener);
        }
    }
}
