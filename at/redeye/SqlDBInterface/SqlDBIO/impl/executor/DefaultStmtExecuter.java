package at.redeye.SqlDBInterface.SqlDBIO.impl.executor;

import java.sql.Connection;

import at.redeye.SqlDBInterface.SqlDBConnection.impl.SupportedDBMSTypes;


public class DefaultStmtExecuter extends AbstractStmtExecuter {

	public DefaultStmtExecuter(Connection conn, SupportedDBMSTypes dbtype) {
		super(conn, dbtype);
		// TODO Auto-generated constructor stub
	}

}
