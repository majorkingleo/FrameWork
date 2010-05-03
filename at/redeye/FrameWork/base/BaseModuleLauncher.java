/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import at.redeye.FrameWork.base.desktoplauncher.DesktopLauncher;
import at.redeye.FrameWork.base.prm.bindtypes.DBConfig;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.FrameWork.widgets.StartupWindow;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProxySelector;
import java.net.URL;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.sun.java.swing.plaf.motif.MotifLookAndFeel;

/**
 * 
 * @author martin
 */
public abstract class BaseModuleLauncher {
	public ProxySelector proxy = null;
	public StartupWindow splash = null;
	public static Logger logger = Logger.getRootLogger();
	public Root root;
	public String[] args;

	public BaseModuleLauncher() {
		// Proxyeinstellungen von Java Ausschalten, sonst versucht sich
		// der oracle Treiber über den Proxy zu DB zu verbinden.
		proxy = ProxySelector.getDefault();
		ProxySelector.setDefault(null);
	}

	public BaseModuleLauncher(String[] args) {
		// Proxyeinstellungen von Java Ausschalten, sonst versucht sich
		// der oracle Treiber über den Proxy zu DB zu verbinden.
		proxy = ProxySelector.getDefault();
		ProxySelector.setDefault(null);
		this.args = args;
	}

	public String getWebStartUrl() {
		return getWebStartUrl(null);
	}

	public String getWebStartUrl(String default_url) {
		String value = getStartupParam("wsu", "webstarturl", "WEBSTARTURL",
				default_url);

		if (value != null && !value.trim().isEmpty()) {
			URL arg_url;

			try {
				arg_url = new URL(value);

				System.out.println("webstarturl: " + value + " is a valid url");

				return value;
			} catch (MalformedURLException ex) {
				System.err.println("invalid url specified: " + value);
				System.err.println(ex);
			}
		}

		return null;
	}

	public String getStartupParam(String shortname, String longname,
			String envname) {
		return getStartupParam(shortname, longname, envname, null);
	}

	public String getStartupParam(String shortname, String longname,
			String envname, String default_url) {
		String url = null;

		if (args != null) {
			boolean next = false;

			for (String arg : args) {
				if (next) {
					return arg;
				} else if (arg.equalsIgnoreCase("-" + shortname)
						|| arg.equalsIgnoreCase("-" + longname)) {
					next = true;
				}
			}
		}

		if (url == null)
			url = System.getProperty(envname.toUpperCase());

		if (url == null || url.trim().isEmpty()) {
			return default_url;
		}

		return url;
	}

	public void updateJnlp() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				if (DesktopLauncher.canCreateDesktopIcon()) {
					if (proxy != null)
						ProxySelector.setDefault(proxy);

					DesktopLauncher launcher = new DesktopLauncher(root
							.getAppName(), root.getWebStartUrl(), root
							.getAppTitle());

					if (launcher.download_jnlp())
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

		String filename = logFileDir + (logFileDir.isEmpty() ? "" : "/")
				+ "log.OS-" + System.getProperty("user.name", "unknown-user")
				+ ".txt";

		System.out.println("Filename: " + filename);

		logger.setLevel(Level.toLevel(logFileLevel));
		logger.addAppender(consoleAppender);

		if (loggingEnabled.equalsIgnoreCase("ja")
				|| loggingEnabled.equalsIgnoreCase("yes")
				|| loggingEnabled.equalsIgnoreCase("true")) {

			try {

				RollingFileAppender fileAppender = new RollingFileAppender(
						layout, filename);
				fileAppender.setAppend(true);
				fileAppender.setMaxFileSize("3MB");
				fileAppender.setName(RollingFileAppender.class.getSimpleName());

				logger.addAppender(fileAppender);

			} catch (IOException e) {
				JOptionPane
						.showMessageDialog(
								null,
								StringUtils
										.autoLineBreak("Das Logger konnte nicht korrekt initialisiert werden!"),
								"User Management", JOptionPane.WARNING_MESSAGE);

			}
		}

	}

	public void checkTableVersions() {
		new AutoLogger(BaseModuleLauncher.class.getCanonicalName()) {

			@Override
			public void do_stuff() throws Exception {

				Transaction trans = root.getDBConnection()
						.getDefaultTransaction();

				if (trans.isOpen()) {
					root.getBindtypeManager().setTransaction(trans);
					root.getBindtypeManager()
							.check_table_versions_with_message(
									root.getUserPermissionLevel());
				}
			}
		};
	}

	public abstract String getVersion();

	public void setSetupParam(String value, DBConfig config,
			boolean if_not_exist) {
		if (value == null)
			return;

		if (value.trim().isEmpty())
			return;

		root.getSetup().setLocalConfig(config.getConfigName(), value,
				if_not_exist);
	}

	public void setCommonLoggingLevel() {
		String do_logging = getStartupParam("dl", "do-logging", "LOGGING");
		String level = getStartupParam("ll", "logging-level", "LOGGING_LEVEL");
		String dir = getStartupParam("ld", "logging-dir", "LOGGING_DIR");
		String force_logging = getStartupParam("fl", "force-logging",
				"FORCE_LOGGING");

		String enable_logging_on_new_version = getStartupParam("",
				"enable-logging-on-new_version",
				"ENABLE_LOGGING_ON_NEW_VERSION");

		if (StringUtils.isYes(enable_logging_on_new_version)) {
			String version = root.getSetup().getLocalConfig(
					BaseAppConfigDefinitions.Version);

			if (version == null || !version.equalsIgnoreCase(getVersion())) {
				if (!StringUtils.isYes(do_logging))
					do_logging = "true";

				if (!StringUtils.isYes(force_logging))
					force_logging = "true";
			}
		}

		root.getSetup().setLocalConfig(
				BaseAppConfigDefinitions.Version.getConfigName(), getVersion());

		if (dir != null && dir.equalsIgnoreCase("APPHOME")) {
			dir = Setup.getAppConfigDir(root.getAppName() + "/log");
		}

		boolean force = false;

		if (StringUtils.isYes(force_logging)) {
			force = true;
		}

		setSetupParam(do_logging, BaseAppConfigDefinitions.DoLogging, force);
		setSetupParam(level, BaseAppConfigDefinitions.LoggingLevel, force);
		setSetupParam(dir, BaseAppConfigDefinitions.LoggingDir, force);

		root.getSetup().saveConfig();

		configureLogging();
	}

	public boolean splashEnabled() {
		if (StringUtils.isYes(getStartupParam(null, "nosplash", "NOSPLASH"))) {
			return false;
		}

		return true;
	}

	/**
	 * This method sets the LookAndFeel which the user has parameterized. It may
	 * be called after the PrmInit was done, but it has to be done before the UI
	 * starts.
	 */
	public void setLookAndFeel(Root root) {

		String config = root.getSetup().getLocalConfig(
				FrameWorkConfigDefinitions.LookAndFeel);

		logger.debug("Found LookAndFeel PRM value: <" + config + ">");

		try {
			UIManager.setLookAndFeel(getLookAndFeelStrByName(config));
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		} catch (InstantiationException e) {
			logger.error(e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
		} catch (UnsupportedLookAndFeelException e) {
			logger.error(e.getMessage());
		}
	}

	private String getLookAndFeelStrByName(String name) {

		if (name.equalsIgnoreCase("motif")) {
			return "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
		} else if (name.equalsIgnoreCase("metal")) {
			return "javax.swing.plaf.metal.MetalLookAndFeel";
        } else if (name.equalsIgnoreCase("nimbus")) {
            return "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
		} else {
			return UIManager.getSystemLookAndFeelClassName();
		}
	}

}
