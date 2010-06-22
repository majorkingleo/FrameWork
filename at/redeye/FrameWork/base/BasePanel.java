/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import at.redeye.FrameWork.base.bindtypes.DBFlagInteger;
import at.redeye.FrameWork.base.bindtypes.DBValue;
import java.util.Collection;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

/**
 *
 * @author martin
 */
public class BasePanel extends javax.swing.JPanel implements BindVarInterface
{
    protected BindVarBase bind_vars = new BindVarBase();

    @Override
    public void bindVar(JTextField jtext, StringBuffer var) {
       bind_vars.bindVar(jtext,var);
    }

    @Override
    public void var_to_gui() {
        bind_vars.var_to_gui();
    }

    @Override
    public void gui_to_var() {
        bind_vars.gui_to_var();
    }

    @Override
    public void bindVar(JTextField jtext, DBValue var) {
        bind_vars.bindVar(jtext,var);        
    }

    @Override
    public void bindVar(JCheckBox box, DBFlagInteger var) {
        bind_vars.bindVar(box,var);        
    }

    public Collection<Pair> getBindVarPairs() {
        return bind_vars.getBindVarPairs();
    }

    public void addBindVarPair( Pair pair )
    {
        bind_vars.addBindVarPair(pair);
    }

}
