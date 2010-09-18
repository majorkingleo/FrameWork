/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;

import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.dbmanager.DBBindtypeManager;
import at.redeye.FrameWork.base.dbmanager.DBManager;
import at.redeye.FrameWork.base.dbmanager.impl.DatabaseManager;
import at.redeye.FrameWork.base.dll_cache.DLLCache;
import at.redeye.FrameWork.base.dll_cache.DLLExtractor;
import at.redeye.FrameWork.base.proxy.AutoProxyHandler;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.FrameWork.Plugin.Plugin;
import at.redeye.FrameWork.base.translation.MLHelper;
import at.redeye.FrameWork.utilities.calendar.CalendarFactory;
import at.redeye.FrameWork.utilities.calendar.Holidays;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.ConnectionDefinition;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MissingConnectionParamException;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.SupportedDBMSTypes;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.UnSupportedDatabaseException;
import at.redeye.UserManagement.UserManagementInterface;
import at.redeye.UserManagement.bindtypes.DBPb;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 
 * @author martin
 */
public class LocalRoot extends Root {

	protected LocalSetup setup;
	protected DBConnection db_connection;
	protected DBPb userEntry = null;
	protected DBManager dbmanager = null;
	protected Vector<BaseDialogBase> dialogs = new Vector<BaseDialogBase>();
	protected boolean appExitAllowed = true;
	private static Logger logger = Logger.getLogger(LocalRoot.class);
	EncryptedDBPasswd enc;
	DelayedLoader loader_encryption;
	DelayedProxyLoader loader_proxy;
	AutoProxyHandler proxy_handler;
        DLLCache dll_cache;
        ArrayList<Plugin> plugins;
        Holidays holidays;
        MLHelper ml_helper;

	public class DelayedLoader extends Thread {

            public DelayedLoader()
            {
                setName(DelayedLoader.class.getCanonicalName());
            }

            @Override
            public void run() {
                enc = new EncryptedDBPasswd(getAppName());
            }
	}

	public class DelayedProxyLoader extends Thread {
		Root root;

		DelayedProxyLoader(Root root) {
			this.root = root;

                        this.setName(DelayedProxyLoader.class.getCanonicalName());
		}

		@Override
		public void run() {
                    long start = System.currentTimeMillis();
                    proxy_handler = new AutoProxyHandler(root);

                    System.out.println(" proxy laoding: " + (System.currentTimeMillis() - start));
		}
	}

	public LocalRoot(String app_name) {
		super(app_name);

		init();
	}

	public LocalRoot(String app_name, String title) {
		super(app_name, title);

		init();
	}

	private void init() {

            dll_cache = new DLLCache(this);

            loader_encryption = new DelayedLoader();
            loader_encryption.start();

            setup = new LocalSetup(this, app_name);

            loader_proxy = new DelayedProxyLoader(this);
            loader_proxy.start();

            dbmanager = new DatabaseManager();
	}

	@Override
	public Setup getSetup() {
		return setup;
	}

	@Override
	public boolean saveSetup() {

                if( ml_helper != null )
                    ml_helper.saveMissingProps();

		if (setup.saveProps())
			return setup.saveGlobalProps();
                    
		return false;
	}

	@Override
	public void setDBConnection(DBConnection con) {
		if (db_connection != null)
			db_connection.close();

		db_connection = con;
	}

	@Override
	public DBConnection getDBConnection() {
		return db_connection;
	}

	public void closeDBConnection() {
		setDBConnection(null);
	}

	private String decryptPasswd(String passwd) {
		if (passwd == null || passwd.isEmpty())
			return passwd;

		if ((passwd.length() % 4) != 0)
			return passwd;

		try {
			if (enc == null) {
				long start = System.currentTimeMillis();

				loader_encryption.join();

				System.out.println("                       waited for encoder "
						+ (System.currentTimeMillis() - start));
			}

		} catch (InterruptedException ex) {
			logger.error(StringUtils.exceptionToString(ex));
		}

		return enc.tryDecryptDBPassword(passwd);
	}

	@Override
	public boolean loadDBConnectionFromSetup() {

		String database = setup.getLocalConfig(Setup.DBDatabase, "");
		String host = setup.getLocalConfig(Setup.DBHost, "");
		String user = setup.getLocalConfig(Setup.DBUser, "");
		String passwd = setup.getLocalConfig(Setup.DBPasswd, "");
		SupportedDBMSTypes dbtype = SupportedDBMSTypes.valueOf(setup
				.getLocalConfig(Setup.DBType,
						SupportedDBMSTypes.DB_MYSQL.toString()));
		String instance = setup.getLocalConfig(Setup.DBInstance, "");
		String sport = setup.getLocalConfig(Setup.DBPort, "0");

		int port = 0;

		if (!sport.isEmpty()) {
			try {
				port = Integer.parseInt(sport);
			} catch (NumberFormatException ex) {
				logger.error("invalid database port: " + sport);
				return false;
			}
		}

		if (dbtype == SupportedDBMSTypes.DB_ORACLE)
			database = instance;
		else if (dbtype == SupportedDBMSTypes.DB_JAVADB) {
			if (database.startsWith("APPHOME")) {
				database = database.replace("APPHOME",
						Setup.getAppConfigDir(app_name));
			}
		}

		sport = decryptPasswd(sport);
		instance = decryptPasswd(instance);
		database = decryptPasswd(database);
		host = decryptPasswd(host);
		user = decryptPasswd(user);
		passwd = decryptPasswd(passwd);

		ConnectionDefinition connparams = new ConnectionDefinition(host, port,
				user, passwd, database, dbtype);

		DBConnection con = new DBConnection();

		waitUntilNetworkIsReady();

		try {
			if (con.open(connparams)) {
				setDBConnection(con);
				return true;
			}
		} catch (ClassNotFoundException e) {
			logger.error(StringUtils.exceptionToString(e));
		} catch (SQLException e) {
			logger.error(StringUtils.exceptionToString(e));
		} catch (MissingConnectionParamException e) {
			logger.error(StringUtils.exceptionToString(e));
		} catch (UnSupportedDatabaseException e) {
			logger.error(StringUtils.exceptionToString(e));
		}

		return false;
	}

	@Override
	public void informWindowOpened(BaseDialogBase dlg) {
		dialogs.add(dlg);
	}

	@Override
	public void informWindowClosed(BaseDialogBase dlg) {
		dialogs.remove(dlg);

		if (dialogs.size() <= 0) {
			if (appExitAllowed) {
				System.out.println("All Windows closed, normal exit");
				appExit();
			}
		}
	}

	@Override
	public void closeAllWindowsNoAppExit() {
		appExitAllowed = false;
		closeAllWindowsExceptThisOne(null);
		appExitAllowed = true;
	}

	@Override
	public void closeAllWindowsExceptThisOne(BaseDialogBase dlg) {
		Vector<BaseDialogBase> dlgs = new Vector<BaseDialogBase>();
		dlgs.addAll(dialogs);

		for (BaseDialogBase frame : dlgs) {
			if (frame != dlg)
				frame.closeNoAppExit();
		}
	}

	@Override
	public void appExit() {
		saveSetup();
		closeDBConnection();
		System.exit(0);
	}

	@Override
	public void setAktivUser(DBStrukt pb) {
		if (DBPb.class.isInstance(pb)) {
			userEntry = (DBPb) pb;
		}
	}

	@Override
	public String getLogin() {
		if (userEntry == null)
			return "";

		return userEntry.login.toString();
	}

	@Override
	public String getUserName() {
		if (userEntry == null)
			return "";

		return userEntry.getUserName();
	}

	@Override
	public int getUserPermissionLevel() {
		if (userEntry == null)
			return UserManagementInterface.UM_PERMISSIONLEVEL_ADMIN;

		return (Integer) userEntry.plevel.getValue();
	}

	@Override
	public DBBindtypeManager getBindtypeManager() {
		return (DBBindtypeManager) dbmanager;
	}

	@Override
	public DBManager getDBManager() {
		return dbmanager;
	}

	@Override
	public int getUserId() {
		if (userEntry == null) {
			logger.warn("userEntry is null returning default user id 0");
			return 0;
		}

		return (Integer) userEntry.id.getValue();
	}

	@Override
	public void noProxyFor(String address) {
		waitUntilNetworkIsReady();

		proxy_handler.exludeFromProxy(address);
	}

	@Override
	public void waitUntilNetworkIsReady() {
		if (proxy_handler == null && loader_proxy != null) {
                    try {
                        long start = System.currentTimeMillis();
                        loader_proxy.join();
                        System.out.println("                       waited for proxy "
                                + (System.currentTimeMillis() - start));

                    } catch (InterruptedException ex) {
                        logger.error(StringUtils.exceptionToString(ex));
                    }
		}

		loader_proxy = null;
	}

    @Override
    public void addDllExtractorToCache( DLLExtractor extractor )
    {
        dll_cache.addDllExtractor(extractor);
        dll_cache.initEnv();
    }

    @Override
    public void updateDllCache()
    {
        dll_cache.update();
    }

    @Override
    public void registerPlugin( Plugin plugin )
    {
        if( plugins == null )
            plugins = new ArrayList<Plugin>();
        else
        {
            for( Plugin p : plugins )
            {
                // already registered
                if( p.getName().equals(plugin.getName()) )
                    return;
            }
        }

        if( plugin.isAvailable() )
        {
            plugins.add(plugin);
            plugin.initPlugin(this);
        }
    }

    @Override
    public List<Plugin> getRegisteredPlugins()
    {
        return plugins;
    }

    @Override
    public Plugin getPlugin( String name )
    {
        for( Plugin plugin : plugins )
        {
            if( plugin.getName().equals(name) )
            {
                if( !plugin.isAvailable() )
                    return null;

                return plugin;
            }
        }

        return null;
    }

    @Override
    public Holidays getHolidays() 
    {
        if( holidays == null )
            holidays = CalendarFactory.getDefaultHolidays();

        return holidays;
    }

    @Override
    public void setHolidays( Holidays  holidays )
    {
        this.holidays = holidays;
    }

    public String MlM( String message )
    {
        if( ml_helper == null )
            ml_helper = new MLHelper( this );

        return ml_helper.MlM(message);
    }

    /**
     * load a MlM file for a spacific class
     * @param obj
     * @param impl_locale the locale the class was originaly implemented
     * eg "de" for german
     */
    public void loadMlM4Class( Object obj, String impl_locale )
    {
        if( ml_helper == null )
            ml_helper = new MLHelper( this );


        ml_helper.autoLoadFile4Class(obj, Locale.getDefault().toString(), impl_locale);
    }

    /**
     * load a MlM file for a spacific class
     * as implementation language the value of base_language is used
     * @param obj
     */
    public void loadMlM4Class( Object obj )
    {
        if( ml_helper == null )
            ml_helper = new MLHelper( this );

        ml_helper.autoLoadFile4Class(obj, Locale.getDefault().toString(), base_language);
    }
}
