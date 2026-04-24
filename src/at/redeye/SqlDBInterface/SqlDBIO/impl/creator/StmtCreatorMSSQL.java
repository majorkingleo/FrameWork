/**
 * 
 */
package at.redeye.SqlDBInterface.SqlDBIO.impl.creator;


import at.redeye.SqlDBInterface.SqlDBIO.TypeRegistrationInterface;

/**
 * @author Mario Mattl
 * 
 */
public class StmtCreatorMSSQL extends AbstractStmtCreator {

	protected StmtCreatorMSSQL(TypeRegistrationInterface treg_) {
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
