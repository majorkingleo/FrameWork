package at.redeye.SqlDBInterface.SqlDBIO.impl;

import at.redeye.SqlDBInterface.SqlDBIO.MOMMTypeRegistrationInterface;

public class MOMMStmtCreatorDerby extends MOMMAbstractStmtCreator {

	public MOMMStmtCreatorDerby(MOMMTypeRegistrationInterface registration) {

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
