/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.desktoplauncher;

import at.redeye.FrameWork.base.Setup;
import at.redeye.FrameWork.base.dll_cache.DLLExtractor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.jimmc.jshortcut.JShellLink;

/**
 *
 * @author martin
 */
public class JShortcutDLL implements DLLExtractor
{
    public static final String LIB_NAME_BASE = "jshortcut_";
    public static final String PROPERTY_NAME = "JSHORTCUT_HOME";

    public String getPropertyNameForDllDir() {
        return PROPERTY_NAME;
    }

    public void extractDlls() throws IOException
    {
        String envdir = System.getProperty(PROPERTY_NAME);

        // dient dazu das die lib geladen wird und die Resource auch zur Verfügung steht.
        JShellLink.class.getName();

        for( String lib : getNames() )
        {
            InputStream source = this.getClass().getResourceAsStream("/" + lib);

            File tempFile = new File( envdir + "/"  + lib );

            FileOutputStream fout = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int read = 0;
            while (read >= 0) {
                fout.write(buffer, 0, read);
                read = source.read(buffer);
            }
            fout.flush();
            fout.close();
            source.close();
        }
    }

    public List<String> getNames() {

        List<String> res = new ArrayList<String>();

       if (Setup.is_win_system())
        {
            String libname = "jshortcut_" + System.getProperty("os.arch") + ".dll";

            res.add(libname);
        }

        return res;
    }
}
