/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.SqlDBInterface.SqlDBIO.impl.creator;

import at.redeye.SqlDBInterface.SqlDBIO.TypeRegistrationInterface;

/**
 *
 * @author martin
 */
public class StmtCreatorMYSQL extends AbstractStmtCreator  {

    protected StmtCreatorMYSQL(TypeRegistrationInterface treg_) {
		super(treg_);
		// TODO Auto-generated constructor stub
	}
    
    @Override
    public String markTableName( String tableName )
    {
        return "`" + tableName.toUpperCase() + "`";
    }
    
    @Override
    public String markColumnName( String columnName )
    {
        return "`" + columnName.toLowerCase() + "`";
    }
}
