/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.ml.bindtypes;

import at.redeye.FrameWork.base.bindtypes.DBInteger;
import at.redeye.FrameWork.base.bindtypes.DBString;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;

/**
 *
 * @author martin
 */
public class DBLang extends DBStrukt {

    public DBInteger Idx =    new DBInteger( "Idx" );
    public DBString  System = new DBString(  "System", 10 );
    public DBString  Type =   new DBString(  "Type", 10 );
    public DBString  Orig =   new DBString(  "Orig", 100 );
    public DBString  Trans =  new DBString(  "Trans", 100 );
    
    public DBLang()
    {
        super( "LANG" );
        
        add( Idx );
        add( System );
        add( Type );
        add( Orig );
        add( Trans );
    }
    
    @Override
    public DBStrukt getNewOne() {
        return new DBLang();
    }

}
