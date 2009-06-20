/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.sequence;

import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;

import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * @author martin
 */
public interface Sequence {

    
    /**
     * 
     * @param seqName Name
     * @return next value
     * @throws java.sql.SQLException
     * @throws at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException
     */
    public int getNewSequenceValue( String seqName, Transaction trans ) throws
			UnsupportedDBDataTypeException, WrongBindFileFormatException, SQLException, TableBindingNotRegisteredException, IOException;
    
}
