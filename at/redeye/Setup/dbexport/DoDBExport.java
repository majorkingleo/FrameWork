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
public class DoDBExport {

    public static void exportDB(Root root, String file_name)
    {
        final DatabaseExport export = new DatabaseExport(root, file_name);

        AutoLogger al = new AutoLogger(DoDBExport.class.getName()) {

            @Override
            public void do_stuff() throws Exception {

                export.doExport();

            }
        };

        export.close();
    }

}
