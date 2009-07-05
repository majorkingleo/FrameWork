/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import at.redeye.FrameWork.base.bindtypes.DBFlagInteger;
import at.redeye.FrameWork.base.bindtypes.DBValue;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

/**
 *
 * @author martin
 */
public class BasePanel extends javax.swing.JPanel implements BindVarInterface
{
    protected BindVarBase bind_vars = new BindVarBase();

    public void bindVar(JTextField jtext, StringBuffer var) {
       bind_vars.bindVar(jtext,var);
    }

    public void var_to_gui() {
        bind_vars.var_to_gui();
    }

    public void gui_to_var() {
        bind_vars.gui_to_var();
    }

    public void bindVar(JTextField jtext, DBValue var) {
        bind_vars.bindVar(jtext,var);        
    }

    public void bindVar(JCheckBox box, DBFlagInteger var) {
        bind_vars.bindVar(box,var);        
    }

}
