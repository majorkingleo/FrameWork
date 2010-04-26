/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.translation;

import java.awt.Component;
import java.awt.Container;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

/**
 *
 * @author martin
 */
public class ExtractStrings
{
    TreeSet<String> strings;
    HashMap<String,Vector<JComponent>> components;

    public ExtractStrings( Container cont )
    {
        strings = new TreeSet<String>();
        components = new HashMap<String,Vector<JComponent>>();

        extractStrings(cont);
    }

    public TreeSet<String> getStrings()
    {
        return strings;
    }

    public HashMap<String,Vector<JComponent>>  getComponents()
    {
        return components;
    }

    private void extractStrings( Container cont )
    {        
        for( Component comp : cont.getComponents() )
        {
//            System.out.println("com:" + comp);

            if( comp instanceof JLabel )
                addString((JLabel)comp);
            else if( comp instanceof JButton )
                addString((JButton)comp);
            else if( comp instanceof JMenu )
                addString((JMenu)comp);
            else if( comp instanceof JMenuItem )
                addString((JMenuItem)comp);
            else if( comp instanceof JRadioButtonMenuItem )
                addString((JRadioButtonMenuItem)comp);
            else if( comp instanceof JCheckBoxMenuItem )
                addString((JCheckBoxMenuItem)comp);
            else
            {
                try {
                    extractStrings((Container) comp);
                } catch (Exception ex) {
                }
            }
        }
    }

    private void addComp( String text, JComponent comp )
    {
        Vector<JComponent> vcomp = components.get(text);

        if( vcomp == null )
        {
            vcomp = new Vector<JComponent>();
            components.put(text, vcomp);
        }

        vcomp.add(comp);
    }

    private void addString( JLabel label )
    {
        strings.add(label.getText());

        addComp(label.getText(),label);
    }

    private void addString( JButton button )
    {
        if( button.getText().isEmpty() )
            return;

        strings.add(button.getText());

        addComp(button.getText(),button);
    }

    private void addString(JMenu menu) {

        if( menu.getText().isEmpty() )
            return;

        strings.add(menu.getText());

        addComp(menu.getText(),menu);

        extractStrings(menu.getPopupMenu());        
    }

    private void addString(JMenuItem menu_item) {

        if( menu_item.getText().isEmpty() )
            return;

        strings.add(menu_item.getText());

        addComp(menu_item.getText(),menu_item);
    }

    public static void assign(JComponent comp, String value) {

        if( comp instanceof JButton )
            ((JButton)comp).setText(value);
        else if( comp instanceof JLabel )
            ((JLabel)comp).setText(value);
        else if( comp instanceof JMenuItem )
            ((JMenuItem)comp).setText(value);
        else if( comp instanceof JMenu )
            ((JMenu)comp).setText(value);

    }
}
