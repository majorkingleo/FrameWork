/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.FrameWork.base;

import java.util.Collection;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import at.redeye.FrameWork.base.bindtypes.DBFlagInteger;
import at.redeye.FrameWork.base.bindtypes.DBValue;

/**
 * 
 * @author martin
 */
public interface BindVarInterface {

	static public abstract class Pair {

		public abstract void gui_to_var();

		public abstract void var_to_gui();

		public abstract Object get_first();

		public abstract Object get_second();
	};

	static class TextStringPair extends Pair {

		JTextField textfield;
		StringBuffer value;

		public TextStringPair(JTextField textfield, StringBuffer value) {
			this.textfield = textfield;
			this.value = value;
		}

		public void gui_to_var() {
			value.delete(0, value.length());
			value.append(textfield.getText());
		}

		public void var_to_gui() {
			textfield.setText(value.toString());
		}

		@Override
		public JTextField get_first() {
			return textfield;
		}

		@Override
		public StringBuffer get_second() {
			return value;
		}
	}

	static class ComboStringPair extends Pair {

		JComboBox combo;
		DBValue value;

		public ComboStringPair(JComboBox combo, DBValue value) {
			this.combo = combo;
			this.value = value;
		}

		public void gui_to_var() {
			Object o = combo.getSelectedItem();
			value.loadFromString((o != null ? o.toString() : ""));
		}

		public void var_to_gui() {
			combo.setSelectedItem(value.toString());
		}

		@Override
		public JComboBox get_first() {
			return combo;
		}

		@Override
		public DBValue get_second() {
			return value;
		}
	}

	static class PasswdStringPair extends Pair {
		JPasswordField textfield;
		StringBuffer value;

		public PasswdStringPair(JPasswordField textfield, StringBuffer value) {
			this.textfield = textfield;
			this.value = value;
		}

		public void gui_to_var() {
			value.delete(0, value.length());
			value.append(textfield.getPassword());
		}

		public void var_to_gui() {
			textfield.setText(value.toString());
		}

		@Override
		public JTextField get_first() {
			return textfield;
		}

		@Override
		public StringBuffer get_second() {
			return value;
		}
	}

	static class TextDBStringPair extends Pair {

		JTextField textfield;
		DBValue value;

		public TextDBStringPair(JTextField textfield, DBValue value) {
			this.textfield = textfield;
			this.value = value;
		}

		public void gui_to_var() {
			value.loadFromString(textfield.getText());
		}

		public void var_to_gui() {
			textfield.setText(value.toString());
		}

		@Override
		public JTextField get_first() {
			return textfield;
		}

		@Override
		public DBValue get_second() {
			return value;
		}
	}

	static class FlagCheckboxPair extends Pair {

		JCheckBox checkbox;
		DBFlagInteger value;

		public FlagCheckboxPair(JCheckBox checkbox, DBFlagInteger value) {
			this.checkbox = checkbox;
			this.value = value;
		}

		public void gui_to_var() {
			if (checkbox.isSelected())
				value.loadFromString("X");
			else
				value.loadFromString(" ");
		}

		public void var_to_gui() {
			if (value.getValue() != 0)
				checkbox.setSelected(true);
			else
				checkbox.setSelected(false);
		}

		@Override
		public JCheckBox get_first() {
			return checkbox;
		}

		@Override
		public DBFlagInteger get_second() {
			return value;
		}
	}

	public void bindVar(JTextField jtext, StringBuffer var);

	public void bindVar(JTextField jtext, DBValue var);

	public void bindVar(JComboBox jComboBox, DBValue var);

	public void bindVar(JCheckBox jCDefault, DBFlagInteger _default);

	public void var_to_gui();

	public void gui_to_var();

	Collection<Pair> getBindVarPairs();

	void addBindVarPair(Pair pair);
}
