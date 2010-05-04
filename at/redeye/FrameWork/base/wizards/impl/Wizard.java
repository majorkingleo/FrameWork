/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.FrameWork.base.wizards.impl;

import java.awt.Rectangle;
import java.util.Vector;

import at.redeye.FrameWork.base.wizards.WizardAction;
import at.redeye.FrameWork.base.wizards.WizardAttachInterface;
import at.redeye.FrameWork.base.wizards.WizardClientActionInterface;
import at.redeye.FrameWork.base.wizards.WizardProperties;
import at.redeye.FrameWork.base.wizards.impl.WizardListener.WizardStatus;

/**
 * 
 * @author Mario
 * 
 */
public class Wizard implements WizardClientActionInterface,
		WizardAttachInterface {

	private Vector<WizardListener> allWizardListeners = new Vector<WizardListener>();
	private Vector<WizardBaseWindow> allWindows = new Vector<WizardBaseWindow>();
	private WizardProperties props = null;
	

	int currentWindow = 0;

	public Wizard(WizardProperties props) {
		this.props = props;
	}

	public void start() {
		if (allWindows.size() > 0) {
			WizardBaseWindow window = allWindows.get(0);

			window.controlButtons();
			prepareLegendText(window);
			window.onInit();
		}
	}

	public void addWindow(WizardBaseWindow window) {

		delegateProps(window);
		allWindows.add(window);
	}

	private void delegateProps(WizardBaseWindow window) {

		window.setButtonCancelText(props.getButtonCancelText());
		window.setButtonFinishText(props.getButtonJumpEndText());
		window.setButtonNextText(props.getButtonNextText());
		window.setButtonPrevText(props.getButtonPrevText());
		window.setAreaMenuTreeTitle(props.getLegendAreaTitle());

	}

	protected void handleUpdate() {

		WizardBaseWindow window = allWindows.get(currentWindow);
		Rectangle bounds = window.getBounds();

		switch (window.getRecentAction()) {

		case WIZARD_ACTION_NEXT:
			window.onClose();
			currentWindow++;
			window = allWindows.get(currentWindow);
			if (currentWindow == (allWindows.size() - 1)) {
				applyAction(WizardAction.WIZARD_ACTION_NEXT, false);
			}
			window.setBounds(bounds);
			prepareLegendText(window);
			window.controlButtons();
			window.onInit();

			break;
			
		case WIZARD_ACTION_PREV:
			
			window.onClose();
			currentWindow--;
			window = allWindows.get(currentWindow);
			if (currentWindow == 0) {
				applyAction(WizardAction.WIZARD_ACTION_PREV, false);
			}
			window.setBounds(bounds);
			prepareLegendText(window);
			window.controlButtons();
			window.onInit();
			break;
			
		case WIZARD_ACTION_FINISH: 
			
			window.onClose();
			break;
			
		case WIZARD_ACTION_CLOSE: 
			
			window.onClose();
			break;

		}
		
		
	}

	private void prepareLegendText(WizardBaseWindow window) {

		StringBuilder legend = new StringBuilder();
		for (int idx = 0; idx < allWindows.size(); idx++) {

			if (idx == currentWindow) {
				legend.append("=> ");
			} else {
				legend.append("   ");
			}

			legend.append((idx + 1) + ". " + allWindows.get(idx).getTitle()
					+ "\n");

		}
		for (int idx = 0; idx < allWindows.size(); idx++) {
			window = allWindows.get(idx);
			window.textAreaMenuTree.setText(legend.toString());

		}
	}
	
	
	public void setWizardStatus(WizardStatus currentWizardStatus) {
		updateWizardListeners(currentWizardStatus);	
	}
	
	@Override
	public void applyAction(WizardAction wizardAction, boolean isGranted) {

		WizardBaseWindow window = allWindows.get(currentWindow);
		window.applyActionRule(wizardAction, isGranted);
		window.controlButtons();
	}

	@Override
	public void addWizardListener(WizardListener listener) {
		allWizardListeners.add(listener);
	}

	@Override
	public void removeWizardListener(WizardListener listener) {
		allWizardListeners.remove(listener);

	}

	@Override
	public void updateWizardListeners(WizardStatus currentWizardStatus) {
		
		System.out.println("UPDATE WIZARD LISTENER");
		for (WizardListener currentListener : allWizardListeners) {
			currentListener.onStateChange(currentWizardStatus);
		}

	}

	
}
