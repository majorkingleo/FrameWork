/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.redeye.FrameWork.base.transaction.DerbyTransaction;
import at.redeye.FrameWork.base.transaction.MSSQLTransaction;
import at.redeye.FrameWork.base.transaction.MySQLTransaction;
import at.redeye.FrameWork.base.transaction.OracleTransaction;
import at.redeye.FrameWork.base.transaction.SqLiteTransaction;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.ConnectionDefinition;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MissingConnectionParamException;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.UnSupportedDatabaseException;

/**
 * 
 * @author martin
 */
public class DBConnection {

	protected ConnectionDefinition definition;
	protected Vector<Transaction> transactions = new Vector<Transaction>();

	public DBConnection() {
		// nix
	}

	public DBConnection(ConnectionDefinition definition)
			throws ClassNotFoundException, SQLException,
			MissingConnectionParamException, UnSupportedDatabaseException {
		open(definition);
	}

	public boolean open(ConnectionDefinition definition)
			throws ClassNotFoundException, SQLException,
			MissingConnectionParamException, UnSupportedDatabaseException {

		if (!close()) {
			return false;
		}
		this.definition = definition;

		Transaction trans;
		boolean is_new_transaction = true;

		switch (definition.getDBMSType()) {
		case DB_MSSQL:
			trans = new MSSQLTransaction(definition);
			break;
		case DB_MYSQL:
			trans = new MySQLTransaction(definition);
			break;
		case DB_JAVADB:
			trans = new DerbyTransaction(definition);
			break;
		case DB_ORACLE:
			trans = new OracleTransaction(definition);
			break;
		case DB_SQLITE:

			// Irgendwie funktioniert das nicht gscheit mit
			// SQLite. Deswegen geben wir nun immer nur eine
			// Transaktion zurück.
			// Alle müssen die selbe verwenden.

			if (transactions.size() > 0) {
				trans = transactions.firstElement();
				is_new_transaction = false;
			} else {
				trans = new SqLiteTransaction(definition);
			}
			break;
		default:
			Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE,
					"Unsupported DBMS!");
			return false;
		}

		if (!trans.isOpen())
			return false;

		if (is_new_transaction)
			transactions.add(trans);

		return true;

	}

	public boolean close() {
		if (definition == null || transactions == null)
			return true;

		boolean allClosed = true;

		for (Transaction trans : transactions) {
			try {
				trans.close();
				if (trans.isOpen()) {
					allClosed = false;
				}
			} catch (SQLException ex) {
				Logger.getLogger(DBConnection.class.getName()).log(
						Level.SEVERE, null, ex);
			}
			// System.out.println("HALLO");
			// connections.remove(con);
		}

		transactions.clear();

		return allClosed;
	}

	public Transaction getDefaultTransaction() {
		if (transactions != null && transactions.size() > 0)
			return transactions.firstElement();

		return null;
	}

	public Transaction getNewTransaction() {

		try {

			boolean is_new_transaction = true;
			Transaction trans;

			switch (definition.getDBMSType()) {
			case DB_MSSQL:
				trans = new MSSQLTransaction(definition);
				break;
			case DB_JAVADB:
				trans = new DerbyTransaction(definition);
				break;
			case DB_MYSQL:
				trans = new MySQLTransaction(definition);
				break;
			case DB_ORACLE:
				trans = new OracleTransaction(definition);
				break;
			case DB_SQLITE:
				// Irgendwie funktioniert das nicht gscheit mit
				// SQLite. Deswegen geben wir nun immer nur eine
				// Transaktion zurück.
				// Alle müssen die selbe verwenden.

				if (transactions.size() > 0) {
					trans = transactions.firstElement();
					is_new_transaction = false;
				} else {
					trans = new SqLiteTransaction(definition);
				}
				break;
			default:
				Logger.getLogger(DBConnection.class.getName()).log(
						Level.SEVERE, "Unsupported DBMS!");
				return null;
			}

			if (!trans.isOpen()) {
				return null;
			}
			Logger.getLogger(DBConnection.class.getName()).log(Level.INFO,
					"Transaction Opened: " + trans.hashCode());

			if (is_new_transaction)
				transactions.add(trans);

			return trans;

		} catch (ClassNotFoundException ex) {
			Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (UnSupportedDatabaseException ex) {
			Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (SQLException ex) {
			Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (MissingConnectionParamException ex) {
			Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return null;
	}

	public boolean closeTransaction(Transaction trans) {
		int idx = transactions.indexOf(trans);
		if (idx < 0)
			return false;

		try {

			if (trans.isOpen())
				trans.close();

			if (trans.isOpen())
				return false;

		} catch (SQLException ex) {
			Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE,
					null, ex);
			return false;
		}

		transactions.remove(trans);

		Logger.getLogger(DBConnection.class.getName()).log(Level.INFO,
				"Transaction Closed: " + trans.hashCode());

		return true;
	}
}
