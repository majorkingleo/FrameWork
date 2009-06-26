/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.bindtypes;

import java.util.Date;

/**
 *
 * @author martin
 */
public class DBHistory extends DBStrukt {
    public DBDateTime  an_zeit = new DBDateTime( "anzeit", "Anlegezeit" );
    public DBDateTime  lo_zeit = new DBDateTime( "lozeit", "L\u00f6schzeit" );
    public DBDateTime  ae_zeit = new DBDateTime( "aezeit", "\u00c4nderungszeit" );
    public DBString  an_user = new DBString( "anuser", "Anlegebenutzer", 30 );
    public DBString  lo_user = new DBString( "louser", "L\u00f6schbenutzer", 30 );
    public DBString  ae_user = new DBString( "aeuser", "\u00c4nderungsbenutzer", 30 );
    
    public DBHistory( String name )
    {
        super( name, "Geschichte" );
        
        create();
    }
    
    public DBHistory( String name, String title )
    {
        super( name, title );
        
        create();
    }
    
    private void create()
    {
        add( an_zeit );
        add( an_user );
        add( ae_zeit );
        add( ae_user );
        add( lo_zeit );
        add( lo_user );        
    }

    @Override
    public DBHistory getNewOne() {
        return new DBHistory( strukt_name );
    }   
    
    public void setAnHist( String user )
    {
        an_zeit.loadFromCopy(new Date( System.currentTimeMillis() ));
        an_user.loadFromCopy(user);
        
        // gleich mitsetzten
        setAeHist(user);
    }
    
    public void setAeHist( String user )
    {
        ae_zeit.loadFromCopy(new Date( System.currentTimeMillis() ));
        ae_user.loadFromCopy(user);
    }
    
    public void setLoHist( String user )
    {
        lo_zeit.loadFromCopy(new Date( System.currentTimeMillis() ));
        lo_user.loadFromCopy(user);
    }
}
