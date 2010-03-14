/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import at.redeye.FrameWork.base.desktoplauncher.DesktopLauncher;
import at.redeye.FrameWork.widgets.StartupWindow;
import java.net.ProxySelector;
import org.apache.log4j.Logger;

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

    public BaseModuleLauncher()
    {
        // Proxyeinstellungen von Java Ausschalten, sonst versucht sich
        // der oracle Treiber über den Proxy zu DB zu verbinden.
        proxy = ProxySelector.getDefault();
        ProxySelector.setDefault(null);
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
                            root.getWebStartUrl() );

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

}
