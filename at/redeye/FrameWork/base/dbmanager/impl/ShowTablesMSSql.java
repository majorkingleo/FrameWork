package at.redeye.FrameWork.base.dbmanager.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.redeye.FrameWork.base.dbmanager.ShowTables;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.SqlDBInterface.SqlDBIO.impl.DBDataType;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;

public class ShowTablesMSSql implements ShowTables {

	@Override
	public Collection<String> showTables(Transaction trans) throws SQLException {
		
		String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES";
		
		Vector<DBDataType> args = new Vector<DBDataType>();
		args.add(DBDataType.DB_TYPE_STRING);
		Vector<Vector<?>> res;

		/*
		 * Eine UnsupportedDBDataTypeException Exception sollte hier ja eher
		 * nicht geworfen werden, weil wir sollten schon wissen, was wir tun.
		 */
		try {
			res = trans.getStmtExecInterface().fetchColumnValue(sql, args);
		} catch (UnsupportedDBDataTypeException ex) {
			System.out.println("XXX: " + ex);
			Logger.getLogger(ShowTablesMSSql.class.getName()).log(
					Level.SEVERE, null, ex);
			return null;
		}

		Vector<String> ret = new Vector<String>();

		for (int i = 0; i < res.size(); i++)
			ret.add((String) res.get(i).get(0));

		return ret;
	}

    public boolean db_supports_all_requested_features(Transaction trans) throws SQLException {
        return true;
    }

}
