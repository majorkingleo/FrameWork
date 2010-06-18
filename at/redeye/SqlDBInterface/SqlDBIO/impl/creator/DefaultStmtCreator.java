
package at.redeye.SqlDBInterface.SqlDBIO.impl.creator;

import at.redeye.SqlDBInterface.SqlDBIO.TypeRegistrationInterface;


/**
 * @author Mario Mattl
 *
 */
public class DefaultStmtCreator extends AbstractStmtCreator {


	protected DefaultStmtCreator(TypeRegistrationInterface treg) {
		// TODO Auto-generated constructor stub
		super(treg);
	}

	@Override
	public String markColumnName(String columnName) {
		// TODO Auto-generated method stub
		return columnName;
	}

	@Override
	public String markTableName (String tableName) {
		// TODO Auto-generated method stub
		return tableName;
	}


}
