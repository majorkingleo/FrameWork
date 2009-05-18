/**
 * 
 */
package at.redeye.SqlDBInterface.SqlDBIO.impl;

import at.redeye.SqlDBInterface.SqlDBIO.MOMMTypeRegistrationInterface;

/**
 * @author Mario Mattl
 * 
 */
public class MOMMStmtCreatorMSSQL extends MOMMAbstractStmtCreator {

	protected MOMMStmtCreatorMSSQL(MOMMTypeRegistrationInterface treg_) {
		super(treg_);

	}

	@Override
	public String markColumnName(String columnName) {
		return "[" + columnName.toLowerCase() + "]";
	}

	public String markTableName(String tableName) {

		return "[" + tableName.toUpperCase() + "]";

	}

}
