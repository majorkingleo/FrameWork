package at.redeye.SqlDBInterface.SqlDBIO.impl.creator;

import at.redeye.SqlDBInterface.SqlDBIO.TypeRegistrationInterface;

/**
 *
 * @author martin
 */
public class StmtCreatorSQLITE extends AbstractStmtCreator  {

    protected StmtCreatorSQLITE(TypeRegistrationInterface treg_) {
		super(treg_);
		// TODO Auto-generated constructor stub
	}
    
    @Override
	public String markColumnName( String columnName )
    {
        return "`" + columnName.toLowerCase() + "`";                
    }
    
    @Override
	public String markTableName( String tableName )
    {
        return "`" + tableName.toUpperCase() + "`";
    }
    
    @Override
    protected String markTableAndColumnNameForUpdate( String table, String column )
    {
        return markColumnName(column);
    }
    
    
}
