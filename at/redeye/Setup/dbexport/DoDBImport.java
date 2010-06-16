/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Setup.dbexport;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.Root;

/**
 *
 * @author martin
 */
public class DoDBImport {

    public static void importDBSilent(Root root, String file_name)
    {
        final DatabaseImport db_import = new DatabaseImport(root, file_name);

        AutoLogger al = new AutoLogger(DoDBImport.class.getName()) {

            @Override
            public void do_stuff() throws Exception {

                db_import.doImport();

            }
        };

        db_import.close();
    }

}
