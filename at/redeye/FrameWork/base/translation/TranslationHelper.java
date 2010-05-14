/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.translation;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.BaseDialogBase;
import at.redeye.FrameWork.base.BaseDialogBaseHelper;
import at.redeye.FrameWork.base.Root;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JFrame;
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

     class OpenTransDialog implements Runnable
     {
         public void run() {
             new TranslationDialog(root, (JFrame)base_dlg, base_dlg.getClass().getName()).setVisible(true);
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

    private void switchTranslation( String new_trans ) throws FileNotFoundException, IOException
    {
        String dir = TranslationDialog.getTranslationsDir(root);

        String base_name = dir + "/" + base_dlg.getClass().getName();
        String prop = ".properties";

        String extra = "_";

        if( new_trans.isEmpty() )
            extra = "";

        File dir_exact = new File( base_name + extra + new_trans + prop );

        Properties props = new Properties();

        if( dir_exact.isFile() )
        {
            FileInputStream in = new FileInputStream(dir_exact);
            props.load(in);
            in.close();
        }

        if( extract_strings == null )
            extract_strings = new ExtractStrings(base_dlg.getContainer());

        HashMap<String,Vector<JComponent>> all = extract_strings.getComponents();

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

    }

    private static void assign( JComponent comp, String value )
    {
        ExtractStrings.assign( comp, value );
    }
}
