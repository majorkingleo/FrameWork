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
    
    /*
     * returns true if all registered tables have the correct version      
     * */
    public boolean check_table_versions();
    public boolean check_table_versions_with_message( int Permissionlevel );
}
