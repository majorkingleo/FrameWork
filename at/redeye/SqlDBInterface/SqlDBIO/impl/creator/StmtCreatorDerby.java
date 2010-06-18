package at.redeye.SqlDBInterface.SqlDBIO.impl.creator;

import at.redeye.SqlDBInterface.SqlDBIO.TypeRegistrationInterface;

public class StmtCreatorDerby extends AbstractStmtCreator {

	public StmtCreatorDerby(TypeRegistrationInterface registration) {

		super(registration);
	}

	@Override
	public String markTableName(String tableName) {
		return "\"" + tableName.toUpperCase() + "\"";
	}

	@Override
	public String markColumnName(String columnName) {
		return "\"" + columnName.toLowerCase() + "\"";

	}
	
}
