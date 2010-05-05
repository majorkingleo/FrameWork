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
import java.util.Collection;
import java.util.LinkedList;

/**
 * 
 * @author Mario
 * 
 */
public class Wizard implements WizardClientActionInterface,
		WizardAttachInterface {

	private Collection<WizardListener> allWizardListeners = new LinkedList<WizardListener>();
	private Vector<WizardBaseWindow> allWindows = new Vector<WizardBaseWindow>();
	private WizardProperties props = null;

        private boolean beeing_in_update_state = false;
        private Vector<WizardListener> toRemoveListeners = new Vector<WizardListener>();;

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

                WizardAction action = window.getRecentAction();

		switch (action) {

		case WIZARD_ACTION_NEXT:
			window.onClose(action);
			currentWindow++;
			window = allWindows.get(currentWindow);
			if (currentWindow == (allWindows.size() - 1)) {
				applyAction(WizardAction.WIZARD_ACTION_NEXT, false);
			}
			window.setBounds(bounds);
			prepareLegendText(window);
			window.controlButtons();
			window.onInit();
                        window.toFront();

			break;
			
		case WIZARD_ACTION_PREV:
			
			window.onClose(action);
			currentWindow--;
			window = allWindows.get(currentWindow);
			if (currentWindow == 0) {
				applyAction(WizardAction.WIZARD_ACTION_PREV, false);
			}
			window.setBounds(bounds);
			prepareLegendText(window);
			window.controlButtons();
			window.onInit();
                        window.toFront();
			break;
			
		case WIZARD_ACTION_FINISH: 

                        setWizardStatus(WizardStatus.CLOSED);
			window.onClose(action);
			break;
			
		case WIZARD_ACTION_CLOSE: 

                        setWizardStatus(WizardStatus.CLOSED);
			window.onClose(action);
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
                        
            if( !beeing_in_update_state )
            {
                allWizardListeners.remove(listener);
            }
            else
            {
                toRemoveListeners.add(listener);
            }
	}

	@Override
	public void updateWizardListeners(WizardStatus currentWizardStatus) {

            beeing_in_update_state = true;

            System.out.println("UPDATE WIZARD LISTENER");
            for (WizardListener currentListener : allWizardListeners) {
                if( currentListener.onStateChange(currentWizardStatus) == false )
                {
                    if( !toRemoveListeners.contains(currentListener) )
                        toRemoveListeners.add(currentListener);
                }
            }

            beeing_in_update_state = false;

            /*
             * Das müss ma so machen, weil wenn beim updateEvent einer sich nun
             * deregistrieren will, und removeWizardListener aufruft kommts zu einer
             * ConcurrentModificationException un nix passiert.
             */
            for( WizardListener listener : toRemoveListeners )
            {
                removeWizardListener(listener);
            }

	}

	
}
