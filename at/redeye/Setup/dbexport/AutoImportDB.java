/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Setup.dbexport;

import at.redeye.FrameWork.base.BaseModuleLauncher;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.utilities.DownloadUrl;
import at.redeye.FrameWork.utilities.MD5Calc;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.Setup.ConfigCheck.CheckConfigBase;
import at.redeye.Setup.ConfigCheck.Checks.CreatedAlreadyAUser;
import at.redeye.Setup.ConfigCheck.Checks.HaveDbConnection;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class AutoImportDB
{
    public static final String AUTOIMPORTDB = "AUTOIMPORTDB";
    public static final String AUTOIMPORTONCHANGE = "AUTOIMPORTONCHANGE";
    public static final String OLD_IMPORT_DB_MD5_SUM = "OLD_IMPORT_DB_MD5_SUM";

    BaseModuleLauncher module_launcher;
    Root root;
    String download_url;
    File temp_dir;
    CheckConfigBase config;
    private String md5sum_newfile;

    public static Logger logger = Logger.getLogger(AutoImportDB.class);

    public AutoImportDB(Root root, BaseModuleLauncher module_launcher)
    {
        this.module_launcher = module_launcher;
        this.root = root;
    }

    protected boolean shouldAutoImportDB(CheckConfigBase config)
    {  
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

        MD5Calc calc = new MD5Calc();

        md5sum_newfile = calc.calcCheckSum(temp_dir);

        return true;
    }

    public boolean importDB()
    {
        if( !root.loadDBConnectionFromSetup() )
        {
            return false;
        }

        if( DoDBImport.importDBSilent(root,temp_dir.getPath()) )
        {
            root.getSetup().setLocalConfig(OLD_IMPORT_DB_MD5_SUM, md5sum_newfile);
            return true;
        }

        return false;
    }

    public boolean shouldAutoImportDB() {

        download_url = module_launcher.getStartupParam( AUTOIMPORTDB );

        if( download_url == null || download_url.trim().isEmpty() )
            return false;

        if( !root.loadDBConnectionFromSetup() )
        {
            return true;
        }

        if( shouldDownloadDB() )
        {
            String old_md5 = root.getSetup().getLocalConfig(OLD_IMPORT_DB_MD5_SUM,null);

            if( old_md5 != null && md5sum_newfile != null )
            {
                logger.info("MD5 old " + old_md5 + " new File " + md5sum_newfile);

                if( !old_md5.equalsIgnoreCase(md5sum_newfile) )
                    return true;

            } else if( old_md5 == null ) {
                return true;
            }

        }

        if( config == null )
        {
            config = new CheckConfigBase(root);

            config.addCheck(new HaveDbConnection(root));
            config.addCheck(new CreatedAlreadyAUser((root)));
        }

        return shouldAutoImportDB(config);
    }

    public boolean shouldDownloadDB()
    {
        return StringUtils.isYes(module_launcher.getStartupParam(AUTOIMPORTONCHANGE));
    }

}
