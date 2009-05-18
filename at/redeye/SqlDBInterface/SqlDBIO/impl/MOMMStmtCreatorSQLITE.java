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
    public String markColumnName( String columnName )
    {
        return "`" + columnName.toLowerCase() + "`";
    }
}
