/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.dbmanager;

import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.transaction.Transaction;

/**
 *
 * @author martin
 */
public interface DBBindtypeManager {

    public boolean can_support_db();
    public void register( DBStrukt strukt );
    public boolean autocreate();
    public void setTransaction( Transaction trans );
}
