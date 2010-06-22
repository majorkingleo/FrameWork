/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import at.redeye.FrameWork.base.BindVarInterface.FlagCheckboxPair;
import at.redeye.FrameWork.base.BindVarInterface.Pair;
import at.redeye.FrameWork.base.BindVarInterface.TextDBStringPair;
import at.redeye.FrameWork.base.BindVarInterface.TextStringPair;
import at.redeye.FrameWork.base.bindtypes.DBFlagInteger;
import at.redeye.FrameWork.base.bindtypes.DBValue;
import java.util.Collection;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author martin
 */
public class BindVarBase implements BindVarInterface
{
    public Vector<Pair> pairs = new Vector<Pair>();
    
    public void bindVar(JTextField jtext, StringBuffer var) {
        pairs.add(new TextStringPair(jtext, var));
    }

    public void bindVar(JPasswordField jtext, StringBuffer var) {
        pairs.add(new TextStringPair(jtext, var));
    }

    public void bindVar(JTextField jtext, DBValue var) {
        pairs.add(new TextDBStringPair(jtext, var));
    }
    
    public void bindVar(JCheckBox jCDefault, DBFlagInteger _default)
    {
        pairs.add(new FlagCheckboxPair( jCDefault, _default ));
    }

    public void var_to_gui() {
        for (Pair pair : pairs) {
            pair.var_to_gui();
        }
    }

    public void gui_to_var() {
        for (Pair pair : pairs) {
            pair.gui_to_var();
        }
    }

    public void var_to_gui(DBValue val) {

        for (Pair pair : pairs) {

            if( pair.get_second() == val )
            {
                System.out.println( "var_to_gui for "  + val.getName() );
                pair.var_to_gui();
                break;
            }
        }
    }

    public Collection<Pair> getBindVarPairs() {
        return pairs;
    }

    public void addBindVarPair( Pair pair )
    {
        pairs.add(pair);
    }
}
