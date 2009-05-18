/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.FrameWork.base.tablemanipulator;

import at.redeye.FrameWork.base.bindtypes.DBValue;

/**
 *
 * @author martin
 */
public abstract class TableValidator {

    public String formatData(Object data) {
        String result = String.valueOf(data);
        return result;
    }

    public boolean acceptData(String data) {
        return true;
    }

    public boolean loadToValue(DBValue val, String s) {
        return false;
    }

    public boolean wantDoLoadSelf() {
        return false;
    }
}