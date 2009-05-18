/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.widgets.helpwindow;

import at.redeye.FrameWork.utilities.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;

/**
 *
 * @author martin
 */
public class HelpFileLoader {       
        
    
    public static int findImgTag( StringBuilder s, int start )
    {
        int pos;
        
        do
        {        
            pos = s.indexOf( "img", start );
          
            // System.out.println("pos:"  + pos);
            
            if( pos <= 0 ) {
                // weil bei start 0 getht sich keine <img aus
                return -1;
            }
            
            int rpos = StringUtils.skip_spaces_reverse( s, pos-1 );
            
            if( rpos < 0 ) {
                start = pos + 3;
                continue;
            }
            
            if( s.charAt(rpos) != '<' ) {
                start = pos + 3;
                continue;
            }
            
            if( s.length() <= pos + 3 )
                return -1;
            
            if( StringUtils.is_space(s.charAt(pos+3) ) )
                return pos;
        } while( pos < (s.length() - 1 ));
        
        return -1;
    }        
          
    public static String replace_src( StringBuilder s )
    {                
        Vector<String> res = StringUtils.split_str( s, "=");
        
        int i = 0; 
                        
        StringBuilder ret = new StringBuilder();
        
        for( String part : res )
        {                           
            i++;
            
            if( i == 1 ) {
                ret.append(part);
                continue;
            }
            
            ret.append("=");
            
            String src = StringUtils.strip( new StringBuilder(part), " \t\r\n\"" );

            URL resource = Class.class.getResource(src);
            
            String imgsrc = null;
            
            if( resource != null )
            {
                imgsrc = resource.toString();
            }
            else
            {
                System.out.println( "Cannot load Image: " + src );
            }
                
            
            if( imgsrc != null )
            {
                ret.append("\"");
                ret.append(imgsrc);
                ret.append("\"");
            }
        }
        
        return ret.toString();
    }
    
    public static StringBuilder prepareImages( StringBuilder s )
    {
        int start=0;
        
        while( (start = findImgTag(s,start)) >= 0 )
        {                                 
            // System.out.println("HERE");
            
           int end = s.indexOf( ">", start );
           
           if( end < 0 )
               break;
           
           String res = replace_src( new StringBuilder( s.subSequence(start, end) ) );
                                     
           s.replace(start, end, res);                      
           
           start += res.length();
        }
        
        return s;
    }
    

    public String loadHelp( String Base, String ModuleName ) throws IOException
    {
        String resource  = Base  + "/" + ModuleName + ".html";
        
        resource = resource.replaceAll("//", "/");
        
        InputStream stream = getClass().getResourceAsStream( resource );

        System.out.println("Loading resource:" + resource );
        
        if (stream == null) {
            System.out.println("Failed loading resource:" + resource );
            return "";
        }
        int len;

        StringBuilder res = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
  
        
        while( reader.ready() )
        {
            String line = reader.readLine();
            res.append(line);
        }
        
        reader.close();
        stream.close();

        res = prepareImages(res);
        
        return res.toString();
    }
    
    
    public static void main( String argv[] )
    {
        HelpFileLoader hfl = new HelpFileLoader();
        
        try
        {        
            System.out.println( hfl.loadHelp( "/at/momm/Application/resources/Help/", "MainWin" ) );
        } catch( IOException ex ) {
            System.out.println(ex);
            ex.printStackTrace();
        }
    }
}
