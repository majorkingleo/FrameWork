package at.redeye.SqlDBInterface.SqlDBIO.impl.creator;

import at.redeye.SqlDBInterface.SqlDBConnection.impl.SupportedDBMSTypes;
import at.redeye.SqlDBInterface.SqlDBIO.TypeRegistrationInterface;

public class StmtCreatorFactory {

	private TypeRegistrationInterface typeRegistration;

	public StmtCreatorFactory(TypeRegistrationInterface typeRegistration) {
		super();
		this.typeRegistration = typeRegistration;
	}

	public AbstractStmtCreator getStmtCreator(SupportedDBMSTypes dbms) {
		switch (dbms) {
		case DB_MYSQL:
			return new StmtCreatorMYSQL(typeRegistration);

		case DB_MARIADB:
			return new StmtCreatorMYSQL(typeRegistration);                        
                        
		case DB_ORACLE:
			return new StmtCreatorOracle(typeRegistration);

		case DB_SQLITE:
			return new StmtCreatorSQLITE(typeRegistration);

		case DB_MSSQL:
			return new StmtCreatorMSSQL(typeRegistration);

		case DB_JAVADB:
			return new StmtCreatorDerby(typeRegistration);

			// add new here
		default:
			return new DefaultStmtCreator(typeRegistration);

		}
	}
}
