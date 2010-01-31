/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.HotlineHours;

import at.redeye.FrameWork.base.prm.PrmDefaultChecksInterface;
import at.redeye.FrameWork.base.prm.bindtypes.DBConfig;
import at.redeye.FrameWork.base.prm.impl.GlobalConfigDefinitions;
import at.redeye.FrameWork.base.prm.impl.LocalConfigDefinitions;
import at.redeye.FrameWork.base.prm.impl.PrmDefaultCheckSuite;
import org.apache.log4j.Level;

/**
 *
 * @author martin
 */
public class AppConfigDefinitions {
                          
    public static DBConfig DoLogging = new DBConfig("Log-Meldungen Schreiben", "NEIN", "Sollen Logmeldungn in einer LogDatei mitgeschrieben werden.", new PrmDefaultCheckSuite(PrmDefaultChecksInterface.PRM_IS_TRUE_FALSE));
    public static DBConfig LoggingDir = new DBConfig("Log-Verzeichnis", "", "Verzeichnis in das die Logdateien geschrieben werden sollen.");
    private static String [] validLevels = {"MML", Level.DEBUG.toString(), Level.TRACE.toString(), Level.ALL.toString(), Level.INFO.toString()};
    public static DBConfig LoggingLevel = new DBConfig("Log-Level", "DEBUG", "Schwellwert für die Informationen in der Logdatei.", new PrmDefaultCheckSuite(PrmDefaultChecksInterface.PRM_HAS_VALUE), validLevels);
    
    public static void registerDefinitions()
    {       
       
       addLocal(DoLogging);
       addLocal(LoggingDir);
       addLocal(LoggingLevel);
       
       GlobalConfigDefinitions.add_help_path("/at/redeye/HMPC/resources/Help/Params/");
       LocalConfigDefinitions.add_help_path("/at/redeye/HMPC/resources/Help/Params/");
    }
    
    
    static void add( String name, String value, String descr )
    {
        GlobalConfigDefinitions.add(new DBConfig(name,value,descr));
    }
    
    static void add( DBConfig c )
    {
        GlobalConfigDefinitions.add(c);
    }        

    
    static void addLocal( DBConfig c )
    {
        LocalConfigDefinitions.add(c);
    }        

}
