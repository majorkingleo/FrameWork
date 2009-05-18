package at.redeye.FrameWork.utilities;

import java.util.Date;
import org.joda.time.DateTime;

/**
 *
 * @author martin  
 */
public class HMSTime {

    long millis;
    
    public HMSTime()
    {
        millis = 0;
    }

    public HMSTime(long millis) 
    {
        this.millis = millis;
    }
    
    public HMSTime( DateTime jdt )
    {
        millis = jdt.getMillisOfDay();
    }
    
    public HMSTime( Date date )
    {
        millis = new DateTime( date ).getMillisOfDay();
    }
        
    public String toString( String format )
    {                        
        String fields[] = format.split(":");
        
        StringBuilder res = new StringBuilder();
        
        for( int i = 0; i < fields.length; i++ )
        {
            if( res.length() > 0 )
                res.append(":");
            
            if( fields[i].matches("H+"))
            {
                long hours   = getHours();
        
                if( hours < 10 )
                    res.append("0");
                
                res.append(String.valueOf(hours));
            }
            else if( fields[i].matches("m+") )
            {
                long minutes = getMinutesOfHour();
                
                if( minutes < 10 )
                    res.append("0");
                
                res.append(String.valueOf(minutes));
            }
            else if( fields[i].matches("s+"))
            {
                long seconds = getSecondsOfHour();
                
                if( seconds < 10 )
                    res.append("0");
                
                res.append(String.valueOf(seconds));
            }            
        }
        
        return res.toString();
    }
    
    @Override
    public String toString()
    {
        return toString("HH:mm:ss");
    }
    
    public long getHours()
    {
        return millis / 1000 / 60 / 60;
    }
    
    public long getMinutesOfHour()
    {
        long rest  = ( millis / 1000 ) - getHours() * 60 * 60;
        
        return rest / 60;
    }
    
    public long getSecondsOfHour()
    {
        long rest = ( millis / 1000 ) - getHours() * 60 * 60 - getMinutesOfHour() * 60;
        
        return rest;
    }
    
    public void addMillis( long millis )
    {
        this.millis += millis;
    }
    
    public void addSeconds( long seconds )
    {
        millis += seconds * 1000;
    }
    
    public void addMinutes( long minutes )
    {
        millis += minutes * 60 * 1000;
    }
    
    public void addHours( long hours )
    {
        millis += hours * 60 * 60 * 1000;
    }
}
