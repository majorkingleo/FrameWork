package at.redeye.SqlDBInterface.SqlDBIO.impl;

import java.sql.Connection;

import at.redeye.SqlDBInterface.SqlDBConnection.impl.MOMMSupportedDBMSTypes;


public class MOMMDefaultStmtExecuter extends MOMMAbstractStmtExecuter {

	public MOMMDefaultStmtExecuter(Connection conn, MOMMSupportedDBMSTypes dbtype) {
		super(conn, dbtype);
		// TODO Auto-generated constructor stub
	}

}
