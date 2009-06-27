/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

/**
 *
 * @author Sabrina und Mario
 */
public class DefaultAutoRefresh {

    public static void DefaultAutoRefreshTable(AutoRefreshInterface afif)
    {
        afif.feed_table(true);
    }

}
