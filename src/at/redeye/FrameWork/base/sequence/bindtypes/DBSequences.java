/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.sequence.bindtypes;

import at.redeye.FrameWork.base.bindtypes.DBInteger;
import at.redeye.FrameWork.base.bindtypes.DBString;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;

/**
 *
 * @author martin
 */
public class DBSequences extends DBStrukt {
    public DBString name = new DBString( "name", 20 );
    public DBInteger value = new DBInteger( "nextval" );

    public DBSequences()
    {
        super( "SEQUENCES" );
        
        add( name );
        add( value );
        
        name.setAsPrimaryKey();
    }
    
    @Override
    public DBStrukt getNewOne() {
        return new DBSequences();
    }
    
}
