/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.FrameWork.autodialog.dbstrukt.impl;

import java.awt.Font;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.apache.log4j.Logger;

import at.redeye.FrameWork.base.BasePanel;
import at.redeye.FrameWork.base.bindtypes.DBDateTime;
import at.redeye.FrameWork.base.bindtypes.DBDouble;
import at.redeye.FrameWork.base.bindtypes.DBEnum;
import at.redeye.FrameWork.base.bindtypes.DBFlagInteger;
import at.redeye.FrameWork.base.bindtypes.DBFlagJaNein;
import at.redeye.FrameWork.base.bindtypes.DBInteger;
import at.redeye.FrameWork.base.bindtypes.DBString;
import at.redeye.FrameWork.base.bindtypes.DBValue;
import at.redeye.FrameWork.widgets.DBFilterComboBox;
import at.redeye.FrameWork.widgets.datetime.IDateTimeComponent;

/**
 * Creates a new {@link BasePanel} consisting of a label and a input-component
 * for a specific {@link DBValue}
 * 
 * @author Mario Mattl
 */
public class DBValueTypePanel extends BasePanel {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger
			.getLogger(DBValueTypePanel.class.getSimpleName());

	private final static int defStringEditLen = Short.MAX_VALUE;
	private final static int defNumberEditLen = 50;
	private final static int defDropDownEditLen = 80;
	private final static int defDateEditLen = 200;

	private DBValue dbValue;
	private final static Font textFont = new Font("Tahoma", Font.PLAIN, 12);
	private GroupLayout layout;
	private GroupLayout.ParallelGroup horizGroup;
	private GroupLayout.SequentialGroup vertGroup;
	private PanelRegistry wReg;

	public DBValueTypePanel(PanelRegistry wReg, DBValue dbValue) {
		this.wReg = wReg;
		this.dbValue = dbValue;
	}

	public void renderLayout() {
		initComponent();
	}

	private void initComponent() {

		logger.trace("Component: " + dbValue.getName());

		if (dbValue.isPrimaryKey()) {
			generateLabelAndComboBox(dbValue, null, defStringEditLen);
		} else {

			// Ask for user component
			if (wReg.customGuiComponentSet(dbValue)) {
				logger.debug("User want a custom component for "
						+ dbValue.getName() + " => try it");
				generateLabelAndUserComponent(dbValue);
			} else {

				if (dbValue instanceof DBString) {
					DBString dbString = (DBString) dbValue;
					generateLabelAndTextField(dbString);
				} else if (dbValue instanceof DBInteger) {
					DBInteger dbInt = (DBInteger) dbValue;
					generateLabelAndNumericField(dbInt);
				} else if (dbValue instanceof DBDouble) {
					DBDouble dbDouble = (DBDouble) dbValue;
					generateLabelAndNumericField(dbDouble);
				} else if (dbValue instanceof DBDateTime) {
					DBDateTime dbDateTime = (DBDateTime) dbValue;
					generateLabelAndDateTimeComponent(dbDateTime);
				} else if (dbValue instanceof DBFlagInteger) {
					DBFlagInteger dbFlag = (DBFlagInteger) dbValue;
					generateLabelAndCheckBox(dbFlag);
				} else if (dbValue instanceof DBFlagJaNein) {
					DBFlagJaNein dbJaNein = (DBFlagJaNein) dbValue;
					generateLabelAndComboBox(dbJaNein, dbJaNein
							.getLocalizedPossibleValues().toArray(),
							defDropDownEditLen);
				} else if (dbValue instanceof DBEnum) {
					DBEnum dbEnum = (DBEnum) dbValue;
					generateLabelAndComboBox(dbEnum, dbEnum
							.getLocalizedPossibleValues().toArray(),
							defDropDownEditLen);
				}
			}
		}
		this.setName(dbValue.getName());
		wReg.setPanel(this);
	}

	private <T extends DBValue> void generateLabelAndComboBox(T value,
			Object[] content, int size) {

		JLabel label = new JLabel(value.getTitle());
		label.setFont(textFont);

		DBFilterComboBox combo = new DBFilterComboBox(value.getName());
		combo.setFont(textFont);
		combo.setEditable(true);
		combo.setName(value.getName());
		if (content != null) {
			for (Object object : content)
				combo.addItem(object);
		}

		renderVerticalInputPair(new SimpleEntry<JComponent, JComponent>(label,
				combo), size);

		bindVar(combo, value);
	}

	private <T extends DBValue> void generateLabelAndCheckBox(T value) {

		JLabel label = new JLabel(value.getTitle());
		label.setFont(textFont);

		JCheckBox checkbox = new JCheckBox(value.getName());
		checkbox.setFont(textFont);
		checkbox.setName(value.getName());

		renderVerticalInputPair(new SimpleEntry<JComponent, JComponent>(label,
				checkbox), defStringEditLen);
		bindVar(checkbox, (DBFlagInteger) value);

	}

	private <T extends DBValue> void generateLabelAndTextField(T value) {

		JLabel label = new JLabel(value.getTitle());
		label.setFont(textFont);

		JTextField textField = new JTextField();
		textField.setFont(textFont);
		textField.setName(value.getName());

		renderVerticalInputPair(new SimpleEntry<JComponent, JComponent>(label,
				textField), defStringEditLen);
		bindVar(textField, value);

	}

	private <T extends DBValue> void generateLabelAndNumericField(T value) {

		JLabel label = new JLabel(value.getTitle());
		label.setFont(textFont);

		JTextField textField = new JTextField();
		textField.setFont(textFont);
		textField.setName(value.getName());

		renderVerticalInputPair(new SimpleEntry<JComponent, JComponent>(label,
				textField), defNumberEditLen);
		bindVar(textField, value);
	}

	private <T extends DBValue> void generateLabelAndDateTimeComponent(T value) {

		JLabel label = new JLabel(value.getTitle());
		label.setFont(textFont);

		IDateTimeComponent dtComp = wReg.getDateTimeComponent();
		dtComp.setFont(textFont);
		dtComp.setName(value.getName());

		JComponent c;
		if (dtComp instanceof JComponent) {
			c = (JComponent) dtComp;
		} else {
			throw new RuntimeException(dtComp.getName()
					+ " cannot be used as DateTime-component!");
		}

		renderVerticalInputPair(new SimpleEntry<JComponent, JComponent>(label,
				c), defDateEditLen);
		bindVar(dtComp, (DBDateTime) value);
	}

	private <T extends DBValue> void generateLabelAndUserComponent(T value) {

		JLabel label = new JLabel(value.getTitle());
		label.setFont(textFont);

		JComponent c = wReg.getGuiComponent4DBValue(value);
		c.setFont(textFont);
		c.setName(value.getName());
		renderVerticalInputPair(new SimpleEntry<JComponent, JComponent>(label,
				c), defNumberEditLen);
		if (c instanceof JCheckBox) {
			DBFlagInteger flagInt = new DBFlagInteger(value.getName(),
					value.getTitle());
			flagInt.loadFromString(value.toString());
			bindVar((JCheckBox) c, flagInt);
		}

	}

	private void renderVerticalInputPair(Entry<JComponent, JComponent> pair,
			int maxEditFieldLen) {

		final JComponent a = pair.getKey();
		final JComponent b = pair.getValue();

		layout = new GroupLayout(this);
		setLayout(layout);

		horizGroup = layout.createParallelGroup();
		vertGroup = layout.createSequentialGroup();
		layout.setHorizontalGroup(horizGroup);
		layout.setVerticalGroup(vertGroup);

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addPreferredGap(ComponentPlacement.UNRELATED, 5, 15)
				.addComponent(a, 10, 30, 150)
				.addPreferredGap(ComponentPlacement.RELATED, 5, 15)
				.addComponent(b, 10, 50, maxEditFieldLen)
				.addPreferredGap(ComponentPlacement.RELATED, 5, 15));

		layout.setVerticalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(a).addComponent(b));

	}
}
