/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.dbmanager;

import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MOMMSupportedDBMSTypes;

/**
 *
 * @author martin
 */
public interface DBBindtypeManager {

    public boolean can_support_db();
    public void register( DBStrukt strukt );
    public boolean autocreate();
    public void setTransaction( Transaction trans );
    
    /**
     * returns true if all registered tables have the correct version      
     * */
    public boolean check_table_versions();
    public boolean check_table_versions_with_message( int Permissionlevel );

    /**
     * Checks if the dmstype is currently available. In case of a java webstart apps
     * it can happen that not all drivers are delivered.
     * @param dbmstype
     * @return
     */
    public boolean is_dbms_driver_loaded( MOMMSupportedDBMSTypes dbmstype );
}
