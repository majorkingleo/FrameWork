/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.dbmanager.impl;

import at.redeye.SqlDBInterface.SqlDBIO.impl.MOMMColumnAttribute;

/**
 *
 * @author martin
 */
public class CreateSqlMySql extends BaseCreateSql {
    /* nothing todo */
    
    @Override
    protected String addStorageInfo()
    {
        return " ENGINE='InnoDB' DEFAULT CHARSET='utf8' COLLATE='utf8_general_ci'";
    }

	@Override
	public String markColumn(String col) {
		
		return "`"+col+"`";
	}
    
    @Override
    protected String createSqlForRow( MOMMColumnAttribute attr ) {
        
        switch( attr.getDatatype() )
        {
            case DB_TYPE_STRING: return "VARCHAR(" + attr.getWidth() + ")";
            case DB_TYPE_DATETIME: return "DATETIME";
            case DB_TYPE_DATE: return "DATE";            
            
            case DB_TYPE_FLOAT:
            case DB_TYPE_DOUBLE:
                return "double default 0";
            
            case DB_TYPE_LONG:             
            case DB_TYPE_INTEGER:
            case DB_TYPE_BOOLEAN:
            case DB_TYPE_BIT:
            case DB_TYPE_SHORT:
                return "int default '0'";
                
            case DB_TYPE_BLOB:
                return "MEDIUMBLOB";
        }
        
        return null;
    }
}
