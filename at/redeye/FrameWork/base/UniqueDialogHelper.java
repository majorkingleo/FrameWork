/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import java.util.HashMap;

/**
 * Simple class that holds a list of open dialogs that should only be opened once
 * @author martin
 */
public class UniqueDialogHelper
{
    HashMap<String,BaseDialog> dialogs = new HashMap<String,BaseDialog>();

    public BaseDialog invokeUniqueDialog( final BaseDialog dialog )
    {
        BaseDialog d = dialogs.get(dialog.getUniqueIdentifier());

        if( d == null )
        {
            dialogs.put(dialog.getUniqueIdentifier(), dialog);

            dialog.registerOnCloseListener(new Runnable() {

                public void run() {
                    dialogs.remove(dialog.getUniqueIdentifier());
                }
            });

            return dialog;

        } else {
            dialog.close();
        }

        return d;
    }
}
