/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.SqlDBInterface.SqlDBIO.impl;

import at.redeye.SqlDBInterface.SqlDBIO.MOMMTypeRegistrationInterface;

/**
 *
 * @author martin
 */
public class MOMMStmtCreatorMYSQL extends MOMMAbstractStmtCreator  {

    protected MOMMStmtCreatorMYSQL(MOMMTypeRegistrationInterface treg_) {
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
