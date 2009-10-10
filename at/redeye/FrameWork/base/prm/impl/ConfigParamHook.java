/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.prm.impl;

import at.redeye.FrameWork.base.FrameWorkConfigDefinitions;
import at.redeye.FrameWork.base.*;
import at.redeye.FrameWork.base.prm.bindtypes.DBConfig;
import at.redeye.FrameWork.widgets.helpwindow.HelpFileLoader;
import at.redeye.FrameWork.widgets.helpwindow.HelpWinHook;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class ConfigParamHook implements HelpWinHook 
{
    String keyword;
    Root root;
    boolean global;
    TreeMap config;
    Collection<String>  search_path;
    private static Logger logger = Logger.getLogger(ConfigParamHook.class.getName());
    String color_even;
    String color_odd;
    String color_title;
    
    public ConfigParamHook( Root root, String keyword, boolean global, Collection<String> search_path )
    {
        this.keyword = keyword;
        this.root = root;
        this.global = global;
        this.search_path = search_path;
       
        if( global )
            config = GlobalConfigDefinitions.entries;
        else
            config = LocalConfigDefinitions.entries;
        
        color_even = root.getSetup().getLocalConfig(FrameWorkConfigDefinitions.HelpParamColorEven);
        color_odd = root.getSetup().getLocalConfig(FrameWorkConfigDefinitions.HelpParamColorOdd);
        color_title = root.getSetup().getLocalConfig(FrameWorkConfigDefinitions.HelpParamColorTitle);
    }
    
    
    public String getKeyword() {
        return keyword;
    }

    public String getText() {
        
        StringBuilder res = new StringBuilder();
        
        Set<String> keys = config.keySet();
                        
        res.append("<table>\n");
        res.append("<tr bgcolor=\"" + color_title + "\"><td><b>Parameter</b></td>\n");
        res.append("<td><b>Wert</b></td>\n");
        res.append("<td><b>Standardwert</b></td>\n");
        res.append("<td><b>Beschreibung</b></td></tr>\n");               
        
        int count = 1;
        
        for( String key : keys )
        {
            String color;
            
            if( count % 2 == 1 )
            {
                color = color_even;
            } else {
                color = color_odd;
            }
            
            count++;
            
            DBConfig c = (DBConfig)config.get(key);
            
            res.append("<tr bgcolor=\"" + color + "\">\n");
            
            res.append("<td><font face=\"Verdana\">\n");
            res.append( key );          
            res.append("</font></td>\n");
            
            res.append("<td>\n");
            res.append("<font face=\"Verdana\">\n");
            
            if( global )
                res.append( root.getSetup().getConfig(c) );
            else
                res.append( root.getSetup().getLocalConfig(c) );
            
            res.append("</font>\n");
            res.append("</td>\n");
            
            res.append("<td>\n");
            res.append("<font face=\"Verdana\">\n");
            res.append( c.getConfigValue());
            res.append("</font>\n");
            res.append("</td>\n");            
                        
            res.append("<td><font face=\"Verdana\">\n");
            res.append( c.descr.toString() );            
            res.append("</font></td>\n");
            
            res.append("</tr>\n");
            
            
            HelpFileLoader hfl = new HelpFileLoader();
            
            String extra = null;
            
            for(String path : search_path)
            {
                try {
                    extra = hfl.loadHelp(path, key);
                } catch (IOException ex) {
                    logger.trace("Hilfemodul: '" + path + "/" + key + ".html' konnte nicht geöffnet werden." );
                }
                
                if( extra != null && !extra.isEmpty() )
                    break;
            }
            
            if( extra != null && extra.isEmpty() == false )
            {
                res.append("<tr>");
                res.append("<td colspan=4 bgcolor=\"" + color + "\">" +
                        "<blockquote>" +
                        "<font face=\"Verdana\">");
                res.append(extra);
                res.append("</blockquote>" +
                        "</font><br/></td> ");
                res.append("</tr>");
            }
        }
        
        res.append("</table>");
        
        return res.toString();
    }

}
