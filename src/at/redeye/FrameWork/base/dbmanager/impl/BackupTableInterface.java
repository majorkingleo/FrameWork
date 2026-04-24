/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.dbmanager.impl;

/**
 *
 * @author martin
 */
public interface BackupTableInterface 
{    
    public String createSqlForBackup( String table, String target_name );
    public String createSqlDropTable( String table );
}
