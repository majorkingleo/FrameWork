/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.Setup.ConfigCheck.Checks;

import at.redeye.FrameWork.base.DBConnection;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.prm.bindtypes.DBConfig;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.Setup.ConfigCheck.ConfigCheck;
import java.sql.SQLException;

/**
 *
 * @author martin
 */
public class HaveDbConnection extends ConfigCheck {

    public HaveDbConnection(Root root) {
        super(root, "do i have a valid database connection");
    }

    @Override
    public boolean doIHaveRequiredFeature() {
        DBConnection con = root.getDBConnection();

        if (con == null) {
            return false;
        }

        Transaction trans = con.getDefaultTransaction();

        if (trans == null) {
            return false;
        }

        try {

            if (!trans.isOpen()) {
                return false;
            }

            DBConfig config = new DBConfig();

            root.getBindtypeManager().setTransaction(trans);

            if (!root.getDBManager().tableExists(config.getName())) {
                return false;
            }
        } catch (SQLException ex) {
            return false;
        }

        return true;
    }
}
