/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Setup.dbexport;

import at.redeye.FrameWork.base.BaseModuleLauncher;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.utilities.DownloadUrl;
import at.redeye.Setup.ConfigCheck.CheckConfigBase;
import at.redeye.Setup.ConfigCheck.Checks.CreatedAlreadyAUser;
import at.redeye.Setup.ConfigCheck.Checks.HaveDbConnection;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author martin
 */
public class AutoImportDB
{
    public static final String AUTOIMPORTDB = "AUTOIMPORTDB";

    BaseModuleLauncher module_launcher;
    Root root;
    String download_url;
    File temp_dir;
    CheckConfigBase config;

    public AutoImportDB(Root root, BaseModuleLauncher module_launcher)
    {
        this.module_launcher = module_launcher;
        this.root = root;
    }

    protected boolean shouldAutoImportDB(CheckConfigBase config)
    {
        download_url = module_launcher.getStartupParam( AUTOIMPORTDB );

        if( download_url == null || download_url.trim().isEmpty() )
            return false;        

        if( config.shouldPopUpWizard() )
            return true;

        return false;
    }

    public boolean downloadDB() throws IOException
    {
        temp_dir = File.createTempFile("autoimport", "db");

        temp_dir.delete();

        if( !DownloadUrl.downloadUrl(download_url, temp_dir.getPath() ) )
            return false;

        return true;
    }

    public boolean importDB()
    {
        if( !root.loadDBConnectionFromSetup() )
        {
            return false;
        }

        return DoDBImport.importDBSilent(root,temp_dir.getPath());
    }

    public boolean shouldAutoImportDB() {

        if( !root.loadDBConnectionFromSetup() )
        {
            return true;
        }

        if( config == null )
        {
            config = new CheckConfigBase(root);

            config.addCheck(new HaveDbConnection(root));
            config.addCheck(new CreatedAlreadyAUser((root)));
        }

        return shouldAutoImportDB(config);
    }

}
