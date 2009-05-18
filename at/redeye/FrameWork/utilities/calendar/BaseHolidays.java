/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.utilities.calendar;

import at.redeye.FrameWork.utilities.calendar.Holidays.HolidayInfo;

import org.joda.time.DateMidnight;

/**
 *
 * @author martin
 */
public class BaseHolidays {
    
    public String CountryCode = "";
    
    public BaseHolidays( String CountryCode )
    {
        this.CountryCode = CountryCode;
    }
    
    public HolidayInfo create( DateMidnight date, boolean floating, boolean official, String name )
    {
        return new HolidayInfo( date, floating, official, name, CountryCode );                
    }

    public HolidayInfo create( int year, int month, int day, boolean floating, boolean official, String name )
    {
        return new HolidayInfo( year, month, day, floating, official, name, CountryCode );                
    }
    
    public DateMidnight getEaster( int year )
    {
        Easterformular easter_formular = new Easterformular(year);
                
        int day = easter_formular.easterday();
                
        DateMidnight easter;
        
        if( day < 31 )
        {
            return new DateMidnight( year, 3, day );            
        } else {
            return new DateMidnight( year, 4, day - 31 );            
        }
    }
    
    public int getNumberOfCountryCodes()
    {
        return 1;
    }
    
    public DateMidnight getEuropeanSummerTimeBegin( int year )
    {
        return getLastSundayOf( year, 3 );
    }
    
    public DateMidnight getEuropeanSummerTimeEnd( int year )
    {
        return getLastSundayOf( year, 10 );
    }
    
    public DateMidnight getLastSundayOf( int year, int month )
    {
        DateMidnight dm = new DateMidnight( year, month, 31 );
       
        while( true )
        {
            if( dm.getDayOfWeek() == 7 )
                return dm;
            
            dm = dm.minusDays(1);
        }
    }
}
