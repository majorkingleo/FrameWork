/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Setup.ConfigCheck.Checks;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.Setup.ConfigCheck.ConfigCheck;
import at.redeye.UserManagement.bindtypes.DBPb;
import at.redeye.UserManagement.bindtypes.DBPermissionLevel;
import java.util.Vector;

/**
 *
 * @author martin
 */
public class AdminUserExists extends ConfigCheck
{
    public AdminUserExists(Root root)
    {
        super(root,"is there already a admin user in the database");                
    }

    @Override
    public boolean doIHaveRequiredFeature()
    {
        Transaction trans = root.getDBConnection().getDefaultTransaction();

        DBPb pb = new DBPb();

        try
        {
            Vector<DBPb> res = trans.fetchTable2(pb,
                    "where " + trans.markColumn(pb.locked) + "=0 and " +
                    trans.markColumn(pb.plevel) + "=" + DBPermissionLevel.PERMISSIONLEVEL.Administrator.ordinal() );
            trans.rollback();

            if( res.isEmpty() )
                return false;

            return true;

        } catch( Exception ex ) {
            logger.error(StringUtils.ExceptionToString(ex));
            return false;
        }
    }


}
