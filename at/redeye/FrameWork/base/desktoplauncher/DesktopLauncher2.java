/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.desktoplauncher;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.Setup;
import at.redeye.FrameWork.utilities.CopyFile;
import at.redeye.FrameWork.utilities.DeleteDir;
import at.redeye.FrameWork.utilities.DownloadUrl;
import at.redeye.FrameWork.utilities.MD5Calc;
import at.redeye.FrameWork.utilities.ParseJNLP;
import at.redeye.FrameWork.utilities.StringUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author martin
 */
public class DesktopLauncher2 extends DesktopLauncher
{
    Root root;
    String jar_dir;
    String jar_lib_dir;
    ParseJNLP parser;
    String jar_update_dir;
    String jar_update_lib_dir;
    String jar_update_dir_real;

    public DesktopLauncher2(Root root)
    {
        super(root);

        this.root = root;

        jar_dir = Setup.getAppConfigDir(getAppName()) + "/jar";
        jar_lib_dir = jar_dir + "/lib";

        jar_update_dir = Setup.getAppConfigDir(getAppName()) + "/jar_update.tmp";
        jar_update_lib_dir = Setup.getAppConfigDir(getAppName()) + "/jar_update.tmp/lib";
        jar_update_dir_real = Setup.getAppConfigDir(getAppName()) + "/jar_update";
    }

    private HashMap<String,String> getMd5Sums( String content )
    {
        HashMap<String,String> online = new HashMap<String,String>();

        String lines[] = content.split("\n");

        for( String line : lines )
        {
            if( line.trim().isEmpty() )
                continue;

            String cont[] = line.split(" ");

            if( cont.length < 2 )
            {
                logger.error("invalid line in md5.txt: " + line);
                continue;
            }

            String md5 = null;
            String jar = null;

            for( int i = 0; i < cont.length; i++ )
            {
                String c = cont[i].trim();

                if( c.isEmpty() )
                    continue;

                if( md5 == null )
                    md5 = c;
                else
                    jar = c;
            }

            if( md5 == null || jar == null )
            {
                logger.error("invalid line in md5.txt: " + line);
                continue;
            }

            logger.info("online md5: " + md5 + " file " + jar);

            online.put(jar, md5);
        }

        return online;
    }

    private HashMap<String,String> getMd5SumsOline(ParseJNLP parser) throws MalformedURLException, IOException
    {
        String app_dir = Setup.getAppConfigDir(getAppName());

        String md5_url = parser.getCodeBase() + "md5.txt";

        URL url = new URL( md5_url );

        String res = DownloadUrl.downloadUrl(url);

        logger.trace(res);

        return getMd5Sums(res);
    }

    public String getMainJarPath()
    {
        if( parser == null )
        {
            new AutoLogger(DesktopLauncher2.class.getName()) {

                @Override
                public void do_stuff() throws Exception {
                    parser = new ParseJNLP( new File(getJnlpName()) );
                }
            };
        }

        if( parser != null )
        {
            return jar_dir + "/" +  parser.getMainJar();
        }

        return null;
    }

    public boolean download_jars() throws ParserConfigurationException, IOException, SAXException
    {
        try {
            if( !download_jars_int() )
            {
              delUpdateDir();
              return false;
            }
        } catch( ParserConfigurationException ex ) {
            delUpdateDir();
            throw ex;
        }  catch( IOException ex ) {
            delUpdateDir();
            throw ex;
        }  catch( SAXException ex ) {
            delUpdateDir();
            throw ex;
        }

        return true;
    }

    public boolean download_jars_int() throws ParserConfigurationException, IOException, SAXException
    {
        parser = new ParseJNLP( new File(getJnlpName()) );

        HashMap<String,String> online = getMd5SumsOline(parser);

        HashMap<String,String> current = getMd5SumsCurrent(parser);

        String main_jar = parser.getMainJar();


        List<String> files = new ArrayList<String>();
        List<String> files_to_copy = new ArrayList<String>();

        boolean something_changed = false;

        for( String jar : parser.getJars() )
        {
            if( online.get(jar) == null )
            {
                logger.error("cannot find jar " + jar + " in online md5sum");
                return false;
            }

            if( current == null ||
                current.get(jar) == null ||
                !current.get(jar).equals(online.get(jar)) )
            {
                if (!something_changed && !manageUpdateDir()) {
                    return false;
                }

                if( current == null )
                    logger.info("jar file " + jar + " does not exist");
                else
                    logger.info("md5 differs at  " + jar + " " +  current.get(jar) + " != " + online.get(jar) );

                String url = parser.getCodeBase() + "/" + jar;
                String target;

                if( main_jar.equals(jar) )
                {
                    target = jar_update_dir + "/" + jar;
                }
                else
                {
                    target = jar_update_lib_dir + "/" + jar;
                }

                files.add(target);

                if( !DownloadUrl.downloadUrl(url, target + ".part") )
                {
                    logger.error("downloading " + jar + " failed");
                    return false;
                }

                something_changed = true;
            } else {
                files_to_copy.add(jar);
            }
        }

        if( !something_changed )
        {
            logger.info("Jar files are up to date");
            return true;
        }

        for( String jar : files_to_copy )
        {
            System.out.println("copy file: " + jar );

            String target;
            String source;

            if (main_jar.equals(jar)) {
                target = jar_update_dir + "/" + jar;
                source = jar_dir + "/" + jar;
            } else {
                target = jar_update_lib_dir + "/" + jar;
                source = jar_lib_dir + "/" + jar;
            }

            if( !CopyFile.copy(new File( source ), new File( target + ".part") ) )
            {
                logger.error("cannot copy file  from " + source + " to " + target );
                return false;
            }

            files.add(target);
        }


        // now rename all files
        for( String jar_file : files )
        {
            File file_part = new File( jar_file + ".part" );
            File file_target = new File( jar_file );
            File file_old = new File( jar_file + ".old" );

            // nothing to update
            if( !file_part.exists() )
                continue;
            
            if( file_old.exists() )
                file_old.delete();

            if( file_target.exists() && !file_target.renameTo(file_old) )
            {
                logger.error("renaming file " + file_target + " to " + file_old + " failed ");
                return false;
            }

            if( !file_part.renameTo(file_target) )
            {
                logger.error("renaming file " + file_part + " to " + file_old + " failed ");
                return false;
            }

            if( file_old.exists() )
                file_old.delete();
        }

        File jfupdate_dir = new File( jar_update_dir );

        if( !jfupdate_dir.renameTo(new File(jar_update_dir_real)) )
        {
            return false;
        }

        if( !Setup.is_win_system() )
        {
            if( !updateDirs() )
            {
                logger.error("updateDirs failed");
                return false;
            }

        } else {

            if( !extractStarter() )
            {
                logger.error("extractStarter failed");
                return false;
            }
        }
        
        logger.info("Jar files are now up to date again");

        return true;
    }

    private HashMap<String, String> getMd5SumsCurrent(ParseJNLP parser) throws FileNotFoundException, IOException
    {
        File f_jar_dir = new File( jar_dir );
        File f_lib_dir = new File( jar_lib_dir );

        if( !f_jar_dir.isDirectory() )
        {
            logger.info(jar_dir + " does not exist" );

            // kein fehler! diese Verzeichnis auch gleich mitanlegen
            f_lib_dir.mkdirs();
            return null;
        }

        if( !f_lib_dir.isDirectory() )
        {
            logger.info(jar_lib_dir + " does not exist" );
            f_jar_dir.mkdirs();
            return null;
        }

        HashMap<String, String> current = new HashMap<String, String>();

        String main_jar = parser.getMainJar();

        MD5Calc md5calc = new MD5Calc();

        for( String jar : parser.getJars() )
        {
            String jar_location;

            if( jar.equals(main_jar) )
            {
                jar_location = jar_dir + "/" + jar;
            }
            else
            {
                jar_location = jar_lib_dir + "/" + jar;
            }

            File file = new File(jar_location);

            if( file.exists() )
            {
                String md5 = md5calc.calcCheckSum(file);

                current.put(jar, md5);
            }
        }

        return current;
    }

    private String getCommand()
    {        
        if( Setup.is_win_system() )
        {
            return "javaw -jar '" + Setup.getAppConfigDir(app_name) + "/RedeyeStarter.jar" + "' " +
                 " '" + getMainJarPath() + "' '" + getJnlpName() + "'";
        }

        return "java -jar '" + getMainJarPath() + "' '" + getJnlpName() + "'";
    }

    @Override
    public CreateDesktopIcon getInstanceForCreateDesktopIcon( String app_name, String jnlp_name, String app_title )
    {
        CreateDesktopIcon gen = super.getInstanceForCreateDesktopIcon(app_name, jnlp_name, app_title);

        gen.setCommand(getCommand());

        return gen;
    }

    @Override
    public CreateDesktopIcon getInstanceForCreateDesktopIcon( String png, String ico, String gif, String app_name, String jnlp_name, String app_title )
    {
        CreateDesktopIcon gen = super.getInstanceForCreateDesktopIcon(png, ico, gif,app_name, jnlp_name, app_title);

        gen.setCommand(getCommand());

        return gen;
    }

    public boolean download_jnlp_only()
    {
        if( !super.download_jnlp() )
           return false;

        return true;
    }

    @Override
    public boolean download_jnlp()
    {
        if( !super.download_jnlp() )
           return false;

        try
        {
            if( !download_jars() )
                return false;
        } catch( Exception ex ) {
            logger.error(StringUtils.exceptionToString(ex));
            return false;
        }

        return true;
    }

    @Override
    public boolean has_jnlp()
    {
       boolean ret = super.has_jnlp();

       if( !ret )
           return false;

       File jar_file = new File(getMainJarPath());

       if( !jar_file.exists() )
           return false;

       return true;
    }

    private boolean manageUpdateDir()
    {
        File udir = new File( jar_update_dir );

        if( udir.exists() )
        {
            if( !DeleteDir.deleteDirectory(udir) )
            {
                logger.error("failed deleting directory: " + udir);
                return false;
            }
        }

         File ulibdir = new File( jar_update_lib_dir );

         if( !ulibdir.exists() )
         {
             if( !ulibdir.mkdirs() )
             {
                 logger.error("cannot create directory " + ulibdir);
                 return false;
             }
         }

        return true;
    }

    private boolean updateDirs()
    {
        File fjar_update_dir = new File( Setup.getAppConfigDir(getAppName()) + "/jar_update");
        File fjar_old_dir = new File(  Setup.getAppConfigDir(getAppName()) + "/jar_old" );
        File fjar_dir = new File( jar_dir );

                // delete old directory
        if( fjar_old_dir.exists() )
        {
            logger.info( "deleting old directory" );

            if( !DeleteDir.deleteDirectory(fjar_old_dir) )
            {
                logger.error("deleting directory " + fjar_old_dir + " failed");
                return false;
            }
        }

        // move new directory to old one
        logger.info( "rename " + fjar_dir + " to " + fjar_old_dir );

        if( !fjar_dir.renameTo(fjar_old_dir)  )
        {
            logger.error( "cannot rename " + fjar_dir + " to " + fjar_old_dir + " updated aborted");
            return false;
        }

        // move update dir directory normal one
        logger.info( "rename " + fjar_update_dir + " to " + fjar_dir);

        if( !fjar_update_dir.renameTo(fjar_dir)  )
        {
            logger.error( "cannot rename " + fjar_update_dir + " to " + fjar_dir);

            // rename old dir back to normal one
            if (!fjar_old_dir.renameTo(fjar_dir)) {
                logger.error("cannot rename " + fjar_old_dir + " to " + fjar_dir);
                return false;
            }
            return false;
        }

        return true;
    }

    private boolean extractStarter()
    {
        InputStream stream = getClass().getResourceAsStream( "/at/redeye/FrameWork/ext_resources/RedeyeStarter.zip" );

        if( stream == null )
        {
            logger.error("Cannot load RedeyeStarter ");
            return false;
        }

        String export_path_name = Setup.getAppConfigFile(app_name, "RedeyeStarter.jar");

        try {

            OutputStream out = new FileOutputStream(export_path_name);

            BufferedInputStream bis = new BufferedInputStream( stream );

            byte[] buf = new byte[1024*4];
            int len;

            while( (len = bis.read(buf) ) > 0 )
            {
                out.write(buf,0,len);
            }

            out.close();
            bis.close();
            stream.close();

        } catch (Exception ex) {

           logger.error(ex);
           export_path_name = null;
        }


        return export_path_name != null;
    }

    @Override
    public boolean createDesktopIcon()
    {
        if( Setup.is_win_system() )
        {
            if( !extractStarter() ) {
                logger.error("extract Starter failed");
                return false;
            }
        }

        return super.createDesktopIcon();
    }

    private void delUpdateDir()
    {
        File fjar_update_dir = new File( Setup.getAppConfigDir(getAppName()) + "/jar_update");

        DeleteDir.deleteDirectory(fjar_update_dir);
    }
}
