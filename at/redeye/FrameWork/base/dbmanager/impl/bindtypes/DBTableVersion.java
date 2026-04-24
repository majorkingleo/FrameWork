/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.dbmanager.impl.bindtypes;

import at.redeye.FrameWork.base.bindtypes.DBString;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;

/**
 *
 * @author martin
 */
public class DBTableVersion extends DBStrukt 
{
    public DBString table = new DBString( "Table", 100 );
    public DBString version = new DBString( "Version", 100 );
    
    public DBTableVersion()
    {
        super("TABLEVERSION");
        
        table.setAsPrimaryKey();
        
        add( table );
        add( version );
    }
    
    @Override
    public DBStrukt getNewOne() {
        return new DBTableVersion();
    }

}
