/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.sequence.impl;

import at.redeye.FrameWork.base.sequence.Sequence;
import at.redeye.FrameWork.base.sequence.bindtypes.DBSequences;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.SqlDBInterface.SqlDBIO.impl.MOMMDBDataType;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;

import java.sql.SQLException;
import java.util.Vector;

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
     */
    public int getNewSequenceValue( String seqName, Transaction trans ) throws SQLException, UnsupportedDBDataTypeException, WrongBindFileFormatException, TableBindingNotRegisteredException
    {
    	int currValue = 1;
        
        /*
        
	Vector<MOMMDBDataType> args = new Vector<MOMMDBDataType>();
	args.add(MOMMDBDataType.DB_TYPE_INTEGER);
	Vector<Vector<?>> res;

	String selectStmt = "select NEXTVAL from SEQUENCES where NAME='"+ seqName +"'";

	res = trans.fetchColumnValue(selectStmt, args);
                        
	if( res.size() == 0 )
        {
            currValue = 1;
            String sql = "insert into SEQUENCES (NAME,NEXTVAL) VALUES('" + seqName + "','2')";
            trans.updateValues(sql);
         } else {                        
            currValue = (Integer) res.get(0).get(0);

            String updateStmt = "update SEQUENCES set NEXTVAL='" 
                               + Integer.toString(currValue + 1) +
                                 "' where NAME='" + seqName +"'";
            trans.updateValues(updateStmt);
         }        
	*/
        
        DBSequences seq = new DBSequences();
        
        seq.name.loadFromString(seqName);
        
        if( !trans.fetchTableWithPrimkey(seq) ) {
            
            seq.value.loadFromCopy(new Integer(currValue+1));
            trans.insertValues(seq);
            return currValue;
        } else {
            currValue = (Integer)seq.value.getValue();
            seq.value.loadFromCopy(new Integer(currValue+1));
            trans.updateValues(seq);
        }
        
        return currValue;
    }
      
    
}
