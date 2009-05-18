/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.dbmanager;

import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MOMMSupportedDBMSTypes;

/**
 *
 * @author user
 */
public interface CreateSql {
    
    public String createSqlforTable( DBStrukt strukt, MOMMSupportedDBMSTypes dbtype );
    
}
