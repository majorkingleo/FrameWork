package at.redeye.FrameWork.autodialog.dbstrukt.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;

import at.redeye.FrameWork.base.BasePanel;
import at.redeye.FrameWork.base.bindtypes.DBValue;
import at.redeye.FrameWork.widgets.datetime.IDateTimeComponent;
import at.redeye.FrameWork.widgets.datetime.impl.CommonDateTimeComponent;

class PanelRegistry {

	private Map<String, BasePanel> createdWidgets = new HashMap<String, BasePanel>();
	private Map<DBValue, Class<? extends JComponent>> guiComp4DbValues = new HashMap<DBValue, Class<? extends JComponent>>();
	private Class<? extends IDateTimeComponent> dateTimeComponent = CommonDateTimeComponent.class;

	BasePanel getPanelByName(String name) {
		BasePanel panel = createdWidgets.get(name);
		if (panel == null) {
			throw new RuntimeException("Panel >" + name
					+ "< has not been registered");
		}
		return panel;
	}

	void setPanel(BasePanel panel) {
		System.out.println("Adding >" + panel.getName() + "< to registry");
		createdWidgets.put(panel.getName(), panel);
	}

	/**
	 * @param dateTimeComponent
	 *            the dateTimeComponent to set
	 */
	void setDateTimeComponent(
			Class<? extends IDateTimeComponent> dateTimeComponent) {
		this.dateTimeComponent = dateTimeComponent;
	}

	/**
	 * Get a new instance of the currently set DateTimeComponent
	 * 
	 * @return the dateTimeComponent
	 */
	IDateTimeComponent getDateTimeComponent() {
		try {
			return dateTimeComponent.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(
					"Failed to instantiate a new object of DateTime component!\n",
					e);
		}
	}

	void setGuiComponent4DBValue(
			Entry<? extends DBValue, Class<? extends JComponent>> entry) {
		guiComp4DbValues.put(entry.getKey(), entry.getValue());
	}

	boolean customGuiComponentSet(DBValue key) {
		return guiComp4DbValues.containsKey(key);
	}

	JComponent getGuiComponent4DBValue(DBValue key) {
		try {
			Class<? extends JComponent> c = guiComp4DbValues.get(key);
			if (c == null) {
				throw new RuntimeException(
						"Could not find a registered JComponent for DBValue "
								+ key.getName());
			}
			return c.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(
					"Failed to instantiate a new object of JComponent component for DBValue "
							+ key.getName() + "\n", e);
		}
	}

}
