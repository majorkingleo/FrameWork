/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import at.redeye.FrameWork.base.desktoplauncher.DesktopLauncher;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.FrameWork.widgets.StartupWindow;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProxySelector;
import java.net.URL;
import javax.swing.JOptionPane;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 *
 * @author martin
 */
public class BaseModuleLauncher
{
    public ProxySelector proxy = null;
    public StartupWindow splash = null;
    public static Logger logger = Logger.getRootLogger();
    public Root root;
    public String[] args;

    public BaseModuleLauncher()
    {
        // Proxyeinstellungen von Java Ausschalten, sonst versucht sich
        // der oracle Treiber über den Proxy zu DB zu verbinden.
        proxy = ProxySelector.getDefault();
        ProxySelector.setDefault(null);
    }

    public BaseModuleLauncher( String[] args )
    {
        // Proxyeinstellungen von Java Ausschalten, sonst versucht sich
        // der oracle Treiber über den Proxy zu DB zu verbinden.
        proxy = ProxySelector.getDefault();
        ProxySelector.setDefault(null);
        this.args = args;
    }

    public String getWebStartUrl()
    {
        return getWebStartUrl(null);
    }

    public String getWebStartUrl( String default_url )
    {
        String value = getStartupParam("wsu", "webstarturl", "WEBSTARTURL", default_url);

        if( value != null && !value.trim().isEmpty() )
        {
            URL arg_url;

            try {
                arg_url = new URL(value);

                System.out.println("webstarturl: " + value + " is a vali url");

                return value;
            } catch (MalformedURLException ex) {
                System.err.println("invalid url specified: " + value);
                System.err.println(ex);
            }
        }
        
        return null;
    }

    public String getStartupParam(String shortname, String longname, String envname )
    {
        return getStartupParam( shortname,  longname,  envname, null);
    }

    public String getStartupParam( String shortname, String longname, String envname, String default_url )
    {
        String url = null;

        if( args != null )
        {
            boolean next = false;

            for( String arg : args )
            {
                if( next )
                {
                    return arg;
                }
                else if( arg.equalsIgnoreCase("-" + shortname) ||
                    arg.equalsIgnoreCase("-" + longname ) )
                {
                    next = true;
                }
            }
        }

        if( url == null )
            url = System.getProperty(envname.toUpperCase());

        if( url == null || url.trim().isEmpty() )
        {
            return default_url;
        }

        return url;
    }

    public void updateJnlp()
    {
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                if( DesktopLauncher.canCreateDesktopIcon() )
                {
                    if( proxy != null )
                        ProxySelector.setDefault(proxy);

                    DesktopLauncher launcher = new DesktopLauncher(root.getAppName(),
                            root.getWebStartUrl() , root.getAppTitle() );

                    if( launcher.download_jnlp() )
                        logger.info("updated jnlp");
                    else
                        logger.error("failed updating jnlp");

                    ProxySelector.setDefault(null);
                }
            }
        };

        thread.start();
    }

    public void configureLogging() {

        PatternLayout layout = new PatternLayout(
                "%d{ISO8601} %-5p (%F:%L): %m%n");
        ConsoleAppender consoleAppender = new ConsoleAppender(layout);

        String logFileDir = root.getSetup().getLocalConfig(
                BaseAppConfigDefinitions.LoggingDir);
        System.out.println("logFileDir: " + logFileDir);
        String logFileLevel = root.getSetup().getLocalConfig(
                BaseAppConfigDefinitions.LoggingLevel);
        String loggingEnabled = root.getSetup().getLocalConfig(
                BaseAppConfigDefinitions.DoLogging);

        String filename = logFileDir + (logFileDir.isEmpty() ? "" : "/") + "log.OS-" + System.getProperty("user.name", "unknown-user") + ".txt";

        System.out.println("Filename: " + filename);

        logger.setLevel(Level.toLevel(logFileLevel));
        logger.addAppender(consoleAppender);

        if (loggingEnabled.equalsIgnoreCase("ja") ||
                loggingEnabled.equalsIgnoreCase("yes") ||
                loggingEnabled.equalsIgnoreCase("true")) {

            try {

                RollingFileAppender fileAppender = new RollingFileAppender(
                        layout, filename);
                fileAppender.setAppend(true);
                fileAppender.setMaxFileSize("3MB");
                fileAppender.setName(RollingFileAppender.class.getSimpleName());

                logger.addAppender(fileAppender);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        null,
                        StringUtils.autoLineBreak("Das Logger konnte nicht korrekt initialisiert werden!"),
                        "User Management", JOptionPane.WARNING_MESSAGE);

            }
        }

    }

     public void checkTableVersions() {
        new AutoLogger(BaseModuleLauncher.class.getCanonicalName()) {

            @Override
            public void do_stuff() throws Exception {

                Transaction trans = root.getDBConnection().getDefaultTransaction();

                if (trans.isOpen()) {
                    root.getBindtypeManager().setTransaction(trans);
                    root.getBindtypeManager().check_table_versions_with_message(root.getUserPermissionLevel());
                }
            }
        };
    }

}
