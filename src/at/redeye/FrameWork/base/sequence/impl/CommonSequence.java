/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.sequence.impl;

import java.io.IOException;
import java.sql.SQLException;

import at.redeye.FrameWork.base.sequence.Sequence;
import at.redeye.FrameWork.base.sequence.bindtypes.DBSequences;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;

/**
 *
 * @author martin
 */
public class CommonSequence implements Sequence {

    /* implements a sequence by using a the 
     * SEQUENCES table. This should work for
     * all databases.
     */ 
    /**
     * 
     * @param seqName
     * @param trans new Single Transaction not used for other stuff
     * @return next value
     * @throws java.sql.SQLException
     * @throws at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException
     * @throws IOException 
     */
    @Override
    public int getNewSequenceValue( String seqName, Transaction trans ) throws SQLException, UnsupportedDBDataTypeException, WrongBindFileFormatException, TableBindingNotRegisteredException, IOException
    {
    	return getNewSequenceValues(seqName, 1, trans);
    }
      
    /**
     *
     * @param seqName
     * @param trans new Single Transaction not used for other stuff
     * @return next value
     * @throws java.sql.SQLException
     * @throws at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException
     * @throws IOException
     */
    @Override
    public int getNewSequenceValues( String seqName, int number, Transaction trans ) throws SQLException, UnsupportedDBDataTypeException, WrongBindFileFormatException, TableBindingNotRegisteredException, IOException
    {
        if( number < 1 )
            throw new SQLException("invalid number " + number + " must be greater or equal than 1");

    	int currValue = 1;

        DBSequences seq = new DBSequences();

        seq.name.loadFromString(seqName);

        if( !trans.fetchTableWithPrimkey(seq) ) {

            seq.value.loadFromCopy(new Integer(currValue+number));
            trans.insertValues(seq);
            return currValue;
        } else {
            currValue = (Integer)seq.value.getValue();
            seq.value.loadFromCopy(new Integer(currValue+number));
            trans.updateValues(seq);
        }

        return currValue;
    }
}
