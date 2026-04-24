/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.test.db.data;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.DefaultInsertOrUpdater;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.test.db.SetupTestDB;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.UserManagement.bindtypes.DBPb;
import at.redeye.UserManagement.impl.UserDataHandling;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author martin
 */
public class CreateCommonData {

    Root root;
    Integer normal_job_type_id = null;
    Integer holiday_job_type_id = null;

    private CreateCommonData(Root root) {
        this.root = root;
    }

    private DBPb create_userdata() throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException, IOException
    {
        Transaction trans = root.getDBConnection().getDefaultTransaction();

        DBPb pb = new DBPb();

        List<DBPb> pbs = trans.fetchTable2(pb, "where " + trans.markColumn(pb.login) + "=" + "'martin.test'");

        if( !pbs.isEmpty() )
        {
            pb = pbs.get(0);
        }

        pb.locked.loadFromString("NEIN");
        pb.login.loadFromString("martin.test");
        pb.name.loadFromString("Martin");
        pb.surname.loadFromString("Schema 1 38.5");
        pb.plevel.loadFromCopy(3);
        pb.title.loadFromCopy("Ing");
        pb.pwd.loadFromString(UserDataHandling.getEncryptedPwd("test"));

        DefaultInsertOrUpdater.insertOrUpdateValuesWithPrimKey(trans, pb);

        trans.commit();

        return pb;
    }

    
     public static void main( String[] args )
    {
        new AutoLogger(CreateCommonData.class.getName()) {

            @Override
            public void do_stuff() throws Exception {
                SetupTestDB setup = new SetupTestDB();


                setup.invoke();

                final CreateCommonData data_inserter = new CreateCommonData(setup.root);
                DBPb pb = data_inserter.create_userdata();

            }
        };

    }
  
}
