/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.dbmanager;

import at.redeye.FrameWork.base.transaction.Transaction;

import java.sql.SQLException;
import java.util.Collection;

/**
 *
 * @author Administrator
 */
public interface ShowTables {

    public Collection<String> showTables( Transaction trans ) throws SQLException;
    public boolean db_supports_all_requested_features( Transaction trans ) throws SQLException;
}
