/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Setup.ConfigCheck.Checks;

import java.util.List;
import java.util.Vector;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.Setup.ConfigCheck.ConfigCheck;
import at.redeye.SqlDBInterface.SqlDBIO.impl.DBDataType;
import at.redeye.UserManagement.bindtypes.DBPb;

/**
 * 
 * @author martin
 */
public class CreatedAlreadyAUser extends ConfigCheck {
	public CreatedAlreadyAUser(Root root) {
		super(root, "is there already a user in the dabase");
	}

	@Override
	public boolean doIHaveRequiredFeature() {
		Transaction trans = root.getDBConnection().getDefaultTransaction();

		Vector<DBDataType> args = new Vector<DBDataType>();

		args.add(DBDataType.DB_TYPE_LONG);

		List<List<?>> res;

		DBPb pb = new DBPb();

		try {
			res = trans.getStmtExecInterface().fetchColumnValue(
					"select count(*) from " + trans.markTable(pb) + " where "
							+ trans.markColumn(pb.locked) + "=0", args);
			trans.rollback();
		} catch (Exception ex) {
			logger.error(StringUtils.exceptionToString(ex));
			return false;
		}

		if (res == null || res.isEmpty()) {
			logger.error("result set is empty??");
			return false;
		}

		Long count = (Long) res.get(0).get(0);

		if (count > 0)
			return true;

		return false;
	}

}
