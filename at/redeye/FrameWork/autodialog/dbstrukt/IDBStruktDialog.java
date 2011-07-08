package at.redeye.FrameWork.autodialog.dbstrukt;

import java.util.Collection;
import java.util.Map.Entry;

import javax.swing.JComponent;

import at.redeye.FrameWork.base.bindtypes.DBValue;
import at.redeye.FrameWork.widgets.datetime.IDateTimeComponent;

/**
 * 
 * @author Mario
 */
public interface IDBStruktDialog {

	/**
	 * This method starts the dialog rendering. It should be called, after all
	 * (custom) setup is done.
	 */
	public void openDialog();

	/**
	 * Set additional fields in primary key segment of dialog. The common
	 * purpose is to add some fields, those shall describe the primary key.
	 * 
	 * @param fields
	 */
	public void setPrimKeyExplanationFields(Collection<? extends DBValue> fields);

	/**
	 * Set a custom title
	 * 
	 * @param text
	 */
	public void setCustomTitleText(String text);

	/**
	 * Set a custom DateTime component.<br>
	 * It will be used instead of the common string field.
	 * 
	 * @param comp
	 *            The DateTime-component
	 */
	public void setDateTimeGuiComponent(Class<? extends IDateTimeComponent> comp);

	/**
	 * Set a specific Swing GUI component for a DBValue.
	 * 
	 * @param entry
	 *            A pair consisting of a DBValue and the GUI-component that
	 *            shall be used for it.
	 */
	public void setGuiComponent4DBValue(
			Entry<? extends DBValue, Class<? extends JComponent>> entry);

}
