/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.utilities.calendar;

import java.util.Collection;
import java.util.Vector;
import org.joda.time.DateMidnight;

import at.redeye.FrameWork.utilities.calendar.Holidays.HolidayInfo;

/**
 *
 * @author Administrator
 */
public class AustrianHolidays extends BaseHolidays implements Holidays {
    
    static String myCountryCode = "AT";
    
    public AustrianHolidays()
    {
        super(myCountryCode);
    }

    public Collection<HolidayInfo> getHolidays(int year) {
        
        // siehe http://de.wikipedia.org/wiki/Feiertage_in_Österreich
        
        Vector<HolidayInfo> res = new Vector<HolidayInfo>();                                               
        
        res.add( create( year, 1, 1, false, true, "Neujahrstag" ) );
        res.add( create( year, 1, 6, false, true, "Dreik\u00f6nigstag" ) );
        res.add( create( year, 5, 1, false, true, "Tag der Arbeit" ) );        
        res.add( create( year, 8, 15, false, true, "Maria Himmelfahrt" ) );
        res.add( create( year, 10, 26, false, true, "Nationalfeiertag" ) );
        res.add( create( year, 11, 1, false, true, "Allerheiligen" ) );
        res.add( create( year, 11, 2, false, false, "Allerseelen" ) );
        res.add( create( year, 12, 8, false, false, "Maria Empf\u00f6ngnis" ) );
        res.add( create( year, 12, 25, false, true, "1. Weihnachtsfeiertag" ) );
        res.add( create( year, 12, 26, false, true, "2. Weihnachtsfeiertag" ) );        
        
        /* TODO, die restlichen fixen Feiertage eintragen */
                                
        DateMidnight easter = getEaster( year );
                
        res.add( create( easter, true, true, "Ostersonntag" ) );
        
        DateMidnight ostermontag = easter.plusDays(1);
        res.add( create( ostermontag, true, true, "Ostermontag"));
        
        DateMidnight gruendonnerstag = easter.minusDays(3);
        res.add( create( gruendonnerstag, true, false, "Gründonnerstag"));
        
        DateMidnight karfreitag = easter.minusDays(2);
        res.add( create( karfreitag, true, false, "Karfreitag"));
        
        DateMidnight christihimmelfahrt = easter.plusDays(39);
        res.add( create( christihimmelfahrt, true, true, "Christi Himmelfahrt"));
        
        DateMidnight pfingsten = easter.plusDays(49);
        res.add( create( pfingsten, true, true, "Pfingsten"));
        
        DateMidnight pfingstmontag = easter.plusDays(50);
        res.add( create( pfingstmontag, true, true, "Pfingstmontag"));           
        
        DateMidnight fronleichnam = easter.plusDays(60);
        res.add( create( fronleichnam, true, true, "Fronleichnam"));
        
        DateMidnight aschermittwoch = easter.minusDays(46);
        res.add( create( aschermittwoch, true, false, "Aschermittwoch"));
        
        DateMidnight faschingdienstag = easter.minusDays(47);
        res.add( create( faschingdienstag, true, false, "Faschingdienstag"));
        
        DateMidnight rosenmontag = easter.minusDays(48);
        res.add( create( rosenmontag, true, false, "Rosenmontag"));                
        
        res.add( create( getEuropeanSummerTimeBegin(year), true, false, "Sommerzeit Beginn" ));
        res.add( create( getEuropeanSummerTimeEnd(year), true, false, "Ende Sommerzeit" ));
        
        return res;        
    }

    public String getPrimaryCountryCode() {
        return myCountryCode;
    }

}
