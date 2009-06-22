package at.redeye.SqlDBInterface.SqlDBIO.impl;

import at.redeye.SqlDBInterface.SqlDBIO.MOMMTypeRegistrationInterface;

/**
 *
 * @author martin
 */
public class MOMMStmtCreatorSQLITE extends MOMMAbstractStmtCreator  {

    protected MOMMStmtCreatorSQLITE(MOMMTypeRegistrationInterface treg_) {
		super(treg_);
		// TODO Auto-generated constructor stub
	}
    
    @Override
    protected String markColumnName( String columnName )
    {
        return "`" + columnName.toLowerCase() + "`";                
    }
    
    @Override
    protected String markTableName( String tableName )
    {
        return "`" + tableName.toUpperCase() + "`";
    }
    
    @Override
    protected String markTableAndColumnNameForUpdate( String table, String column )
    {
        return markColumnName(column);
    }
    
    
}
