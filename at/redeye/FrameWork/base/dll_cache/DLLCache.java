/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.dll_cache;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.Setup;
import at.redeye.FrameWork.utilities.StringUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class DLLCache
{
   private static Logger logger = Logger.getLogger(DLLCache.class.getName());

   String cache_dir;

   // das ist absichtlich ein Vector; eben wegen Sychronized
   ArrayList<DLLExtractor> extractors = new ArrayList<DLLExtractor>();

    public DLLCache(Root root) {
        cache_dir = Setup.getAppConfigDir(root.getAppName() + "/jar/dll_cache");
    }

    synchronized public void initEnv()
    {
        for( DLLExtractor extractor : extractors )
        {
            String env = extractor.getPropertyNameForDllDir();

            System.setProperty(env, cache_dir);
        }
    }

    synchronized public void addDllExtractor( DLLExtractor extractor )
    {
        extractors.add(extractor);
    }

    /**
     * extracts all required dlls
     */
    public void update()
    {
        for( DLLExtractor extractor : extractors )
        {
            for( String dll_name : extractor.getNames() )
            {
                File dll = new File( cache_dir + "/" + dll_name );

                if( !dll.exists() )
                {
                    try
                    {
                        extractor.extractDlls();
                        break;
                    } catch( IOException ex ) {
                        logger.error(StringUtils.exceptionToString(ex));
                    }
                }
            }
        }
    }
}
