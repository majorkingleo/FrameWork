/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.sequence.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import at.redeye.FrameWork.base.sequence.Sequence;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.SqlDBInterface.SqlDBIO.impl.DBDataType;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;

/**
 * 
 * @author martin
 */
public class SequenceOracle implements Sequence {

	public int getNewSequenceValue(String seqName, Transaction trans)
			throws SQLException, UnsupportedDBDataTypeException {
		int currValue = 0;
		List<DBDataType> args = new Vector<DBDataType>();
		args.add(DBDataType.DB_TYPE_INTEGER);
		List<List<?>> res;

		String stmt = "select " + seqName + ".nextval from dual";

		res = trans.fetchColumnValue(stmt, args);
		currValue = (Integer) res.get(0).get(0);

		return currValue;
	}

}
