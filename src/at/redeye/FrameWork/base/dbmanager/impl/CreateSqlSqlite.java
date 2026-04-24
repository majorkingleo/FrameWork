/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.dbmanager.impl;

import at.redeye.SqlDBInterface.SqlDBIO.impl.ColumnAttribute;
import at.redeye.SqlDBInterface.SqlDBIO.impl.DBDataType;
import java.util.Collection;

/**
 *
 * @author Administrator
 */
public class CreateSqlSqlite extends BaseCreateSql {

	@Override
	protected String addStorageInfo() {
		
		return "";
	}

	@Override
	public String markColumn(String col) {
		
		return "`"+col+"`";
	}

	@Override
	protected String createSqlForRow(ColumnAttribute attr) {
        
        String extra = "";
        
        if( attr.isPrimaryKey() )
            extra = " PRIMARY KEY ";
        
        switch( attr.getDatatype() )
        {
            case DB_TYPE_STRING: return "VARCHAR(" + attr.getWidth() + ")" + extra;
            case DB_TYPE_DATETIME: return "DATETIME" + extra;
            case DB_TYPE_DATE: return "DATE" + extra;            
           
            case DB_TYPE_FLOAT:
            case DB_TYPE_DOUBLE:
            	return "double default 0" + extra;
            case DB_TYPE_INTEGER:
            case DB_TYPE_LONG:
            case DB_TYPE_BOOLEAN:
            case DB_TYPE_BIT:
            case DB_TYPE_SHORT:
                return "int default '0'" + extra;
        }
        
        return null;
    }
    
    @Override
    protected String createPrimKeys( String table, Collection<String> primKeys )
    {
        // nothing todo, is implemented in createSqlForRow
        // since sqlite does not supprt that stuff :(
        return "";
    }

    @Override
    protected String appendNotNullIfSupportedbyNewRows(ColumnAttribute attr) {

        if( attr.getDatatype() == DBDataType.DB_TYPE_STRING )
        {
            return " default "+ getDefaultValueVarChar(attr.getWidth()) + " NOT NULL ";
        }

        return " NOT NULL";
    }

}
