package at.redeye.SqlDBInterface.SqlDBConnection.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.sqlite.JDBC;

import at.redeye.SqlDBInterface.SqlDBConnection.DbConnectionInterface;

/**
 * @author Mario Mattl
 */
public abstract class AbstractDBConnector implements
		DbConnectionInterface {

	private ConnectionDefinition conndef_;

	private static Logger logger = Logger
			.getLogger(AbstractDBConnector.class.getSimpleName());

	public AbstractDBConnector(ConnectionDefinition conn) {
		conndef_ = conn;
	}

	public Connection connectToDatabase() throws ClassNotFoundException,
			UnSupportedDatabaseException, SQLException,
			MissingConnectionParamException {

		StringBuilder str = new StringBuilder();

		switch (conndef_.getDBMSType()) {
		case DB_MSSQL:
			if (conndef_.getUsername().isEmpty()) {
				throw new MissingConnectionParamException(
						"The username must be specified!");
			}
			if (conndef_.getPwd().isEmpty()) {
				throw new MissingConnectionParamException(
						"The password must be specified!");
			}
			if (conndef_.getInstance().isEmpty()) {
				throw new MissingConnectionParamException(
						"The database must be specified!");
			}
			DriverManager
					.registerDriver(new net.sourceforge.jtds.jdbc.Driver());
			str.append("jdbc:jtds:sqlserver://");
			str.append((conndef_.getHostname().isEmpty() ? "localhost"
					: conndef_.getHostname()));
			str.append(":"
					+ (conndef_.getPort() == 0 ? 1433 : conndef_.getPort()));
			str.append("/" + conndef_.getInstance());
			break;

		case DB_MYSQL:
			if (conndef_.getUsername().isEmpty()) {
				throw new MissingConnectionParamException(
						"The username must be specified!");
			}
			if (conndef_.getInstance().isEmpty()) {
				throw new MissingConnectionParamException(
						"The database must be specified!");
			}
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			str.append("jdbc:mysql://");
			str.append((conndef_.getHostname().isEmpty() ? "localhost"
					: conndef_.getHostname()));
			str.append(":"
					+ (conndef_.getPort() == 0 ? 3306 : conndef_.getPort()));
			str.append("/" + conndef_.getInstance());
			str.append("?zeroDateTimeBehavior=convertToNull");
			break;

		case DB_ORACLE:
			if (conndef_.getUsername().isEmpty()) {
				throw new MissingConnectionParamException(
						"The username must be specified!");
			}

			if (conndef_.getPwd().isEmpty()) {
				throw new MissingConnectionParamException(
						"The password must be specified!");
			}

			if (conndef_.getInstance().isEmpty()) {
				throw new MissingConnectionParamException(
						"The instance must be specified!");
			}
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
			str.append("jdbc:oracle:thin:@//");
			str.append((conndef_.getHostname().isEmpty() ? "localhost"
					: conndef_.getHostname()));
			str.append(":"
					+ (conndef_.getPort() == 0 ? 1521 : conndef_.getPort()));
			str.append("/" + conndef_.getInstance());
			break;

		case DB_SQLITE:
			if (conndef_.getInstance().isEmpty()) {
				throw new MissingConnectionParamException(
						"The database-file must be specified!");
			}
			DriverManager.registerDriver(new JDBC());
			str.append("jdbc:sqlite:");
			str.append(conndef_.getInstance());
			break;
		
		case DB_JAVADB:
			if (conndef_.getInstance().isEmpty()) {
				throw new MissingConnectionParamException(
						"The database-file must be specified!");
			}
			DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
			str.append("jdbc:derby:");

            /* MOB: in der Instanz ist der DB Name.
             * zb /home/martin/foobar
             * aber durch das voraussetzten des hostnamens, oder localhost
             * wird daraus localhost/home/martin/foo
             * dadruch wird dann ein entsprechendes Verzeichnis
             * ausgehend vom aktuellen Verzeichnis verwendet, was nicht gerade
             * dass ist, wass man vielleicht möchte :-)
             */
            /*
			str.append((conndef_.getHostname().isEmpty() ? "localhost"
					: conndef_.getHostname()));
             * */
			str.append(conndef_.getInstance());
			str.append(";create=true");
			break;

		default:
			throw new UnSupportedDatabaseException();
		}

		Connection conn = DriverManager.getConnection(str.toString(), conndef_
				.getUsername(), conndef_.getPwd());
		
		logger.finest("connected");

		conn.setAutoCommit(false);
		if (conndef_.getDBMSType() != SupportedDBMSTypes.DB_SQLITE) {
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		}

		if (conndef_.getDBMSType() == SupportedDBMSTypes.DB_ORACLE) {
			conn.prepareStatement(
				"ALTER SESSION SET NLS_DATE_FORMAT = 'yyyy-mm-dd hh24:mi:ss'")
				.execute();
		} else if (conndef_.getDBMSType() == SupportedDBMSTypes.DB_MSSQL) {
			conn.prepareStatement("SET LANGUAGE US_ENGLISH").execute();
			conn.prepareStatement("SET DATEFORMAT ymd").execute();
		}

		return conn;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see DBConnection.DbConnectionInterface#disconnectDatabase()
	 */
	public void disconnectDatabase(Connection conn) throws SQLException {
		conn.rollback();
		conn.close();
		// TODO Auto-generated method stub

	}

}
