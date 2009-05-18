/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.redeye.FrameWork.base.transaction.MSSQLTransaction;
import at.redeye.FrameWork.base.transaction.MySQLTransaction;
import at.redeye.FrameWork.base.transaction.OracleTransaction;
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

	public DBConnection(ConnectionDefinition definition) {
		open(definition);
	}

	public boolean open(ConnectionDefinition definition) {
		try {
			if (!close()) {
				return false;
			}
			this.definition = definition;

			Transaction trans;

			switch (definition.getDBMSType()) {
			case DB_MSSQL:
				trans = new MSSQLTransaction(definition);
				break;
			case DB_MYSQL:
				trans = new MySQLTransaction(definition);
				break;
			case DB_ORACLE:
				trans = new OracleTransaction(definition);
				break;
			default:
				Logger.getLogger(DBConnection.class.getName()).log(
						Level.SEVERE, "Unsupported DBMS!");
				return false;
			}

			if (!trans.isOpen())
				return false;

			transactions.add(trans);

			return true;
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
		return false;
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

			Transaction trans;

			switch (definition.getDBMSType()) {
			case DB_MSSQL:
				trans = new MSSQLTransaction(definition);
				break;
			case DB_MYSQL:
				trans = new MySQLTransaction(definition);
				break;
			case DB_ORACLE:
				trans = new OracleTransaction(definition);
				break;
			default:
				Logger.getLogger(DBConnection.class.getName()).log(
						Level.SEVERE, "Unsupported DBMS!");
				return null;
			}

			if (!trans.isOpen()) {
				return null;
			}
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

			trans.close();

			if (trans.isOpen())
				return false;

		} catch (SQLException ex) {
			Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE,
					null, ex);
			return false;
		}

		transactions.remove(trans);

		return true;
	}
}
