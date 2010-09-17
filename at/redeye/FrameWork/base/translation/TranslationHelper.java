/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.translation;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.BaseDialogBase;
import at.redeye.FrameWork.base.BaseDialogBaseHelper;
import at.redeye.FrameWork.base.Root;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author martin
 */
public class TranslationHelper
{
     BaseDialogBase base_dlg;
     Root  root;
     ExtractStrings extract_strings;
     Properties currentProps;

     class OpenTransDialog implements Runnable
     {
         public void run() {
             base_dlg.invokeDialogUnique(
                new TranslationDialog(root, base_dlg.getContainer(), base_dlg.getClass().getName(), extract_strings)
             );
         }
     }

     class SwitchTrans_DE_EN implements Runnable
     {
         String lang_a = "";
         String lang_b = "en";
         String lang_current;

         public void run() {
             if (lang_current != null && lang_b.equals(lang_current)) {
                 lang_current = lang_a;
             } else {
                 lang_current = lang_b;
             }

             new AutoLogger(this.getClass().getName()) {

                 @Override
                 public void do_stuff() throws Exception {
                     switchTranslation(lang_current);
                 }
             };
         }
     }

     public TranslationHelper(Root root, BaseDialogBase base_dlg)
     {
         this.root = root;
         this.base_dlg = base_dlg;

        base_dlg.registerActionKeyListener(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), new OpenTransDialog() );
        base_dlg.registerActionKeyListener(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), new SwitchTrans_DE_EN() );
     }

    public TranslationHelper(Root root, BaseDialogBase base_dlg, BaseDialogBaseHelper helper)
    {
        this.root = root;
        this.base_dlg = base_dlg;

        helper.registerActionKeyListener(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), new OpenTransDialog() );
        helper.registerActionKeyListener(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), new SwitchTrans_DE_EN() );        
    }

    private boolean haveResource( String name )
    {
        System.out.println("testing: " + name);

        URL url = this.getClass().getResource(name);

        if( url != null )
            return true;

        return false;
    }

    private String getAltResourcePath(String resourceName, String subdir)
    {
        int index = resourceName.lastIndexOf('/');

        return resourceName.substring(0,index) + '/' + subdir + resourceName.substring(index);
    }

    private boolean switchTranslation( String new_trans ) throws FileNotFoundException, IOException
    {
        String dir = TranslationDialog.getTranslationsDir(root);        

        String file_name =  "/" + base_dlg.getClass().getName();

        String base_name = dir + file_name;
        String prop = ".properties";

        String extra = "_";

        if( new_trans.isEmpty() )
            extra = "";

        File dir_exact = new File( base_name + extra + new_trans + prop );

        String resource_name = "/" + base_dlg.getClass().getName().replaceAll("\\.", "/") + extra + new_trans + prop;

        String alt1_resource_name = "/" + getAltResourcePath(base_dlg.getClass().getName().replaceAll("\\.", "/"), "translations") + extra + new_trans + prop;
        String alt2_resource_name = "/" + getAltResourcePath(base_dlg.getClass().getName().replaceAll("\\.", "/"), "resources/translations") + extra + new_trans + prop;

        Properties props = new Properties();

        if( dir_exact.isFile() )
        {
            FileInputStream in = new FileInputStream(dir_exact);
            props.load(in);
            in.close();

        } else if( haveResource( resource_name ) ) {

            InputStream in = this.getClass().getResourceAsStream( resource_name );
            props.load( in );
            in.close();

        } else if( haveResource( alt1_resource_name ) ) {

            InputStream in = this.getClass().getResourceAsStream( alt1_resource_name );
            props.load( in );
            in.close();

        } else if( haveResource( alt2_resource_name ) ) {

            InputStream in = this.getClass().getResourceAsStream( alt2_resource_name );
            props.load( in );
            in.close();

        } else {
           return false;
        }

        if( extract_strings == null )
            extract_strings = new ExtractStrings(base_dlg.getContainer());

        HashMap<String,List<JComponent>> all = extract_strings.getComponents();

        Set<String> keys = all.keySet();

        for (String key : keys) {
            String value = props.getProperty(key);

            for (JComponent comp : all.get(key)) {
                if (value != null && !value.isEmpty()) {
                    assign(comp, value);
                } else {
                    assign(comp, key);
                }
            }
        }

        currentProps = props;

        return true;
    }

    private static void assign( JComponent comp, String value )
    {
        ExtractStrings.assign( comp, value );
    }

    public boolean switchTrans(String trans) {
        try {
            return switchTranslation(trans);

        } catch (FileNotFoundException ex) {
            return false;

        } catch (IOException ex) {
            return false;
        }        
    }

    public void autoSwitchToCurrentLocale()
    {
        Locale locale = Locale.getDefault();

        if (locale.toString().equals(base_dlg.getBaseLanguage())) {
            return;
        }

        if (switchTrans(locale.toString())) {
            return;
        }

        String parts[] = locale.toString().split("_");

        if (parts.length == 1 && !root.getDefaultLanguage().equals(base_dlg.getBaseLanguage())) {
            switchTrans(root.getDefaultLanguage());
            return;
        }

        if (switchTrans(parts[0])) {
            return;
        }

        if (!root.getDefaultLanguage().equals(base_dlg.getBaseLanguage())) {
            switchTrans(root.getDefaultLanguage());
        }
    }

    public String MlM( String message )
    {
        if( currentProps == null )
            return message;

        String res =  currentProps.getProperty(message);

        if( res == null && extract_strings != null )
        {
            // damit im Ünbersetztungsdialog der Text aufscheint
            extract_strings.strings.add(message);
        }

        if( res == null )
            res = root.MlM(message);

        if( res == null )
           return message;

        return res;
    }
}
