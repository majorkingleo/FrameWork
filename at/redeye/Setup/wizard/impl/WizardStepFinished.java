/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.Setup.wizard.impl;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.wizards.WizardAction;
import at.redeye.FrameWork.base.wizards.impl.Wizard;
import at.redeye.FrameWork.base.wizards.impl.WizardBaseWindow;
import at.redeye.FrameWork.base.wizards.impl.WizardListener.WizardStatus;

/**
 *
 * @author Mario
 */
public class WizardStepFinished extends WizardBaseWindow {

    
	private static final long serialVersionUID = 1L;
	private WizardPanelFinished finish = null;
    private Wizard parentWizard = null;

    public WizardStepFinished(Root root, Wizard parent) {
        super(root, "Abschluss");
        this.parentWizard = parent;
        finish = new WizardPanelFinished();
        setBaseLanguage("de");
    }

    @Override
    protected void setGuestContent() {
        super.panelGuestContent.add(finish);
    }

    @Override
    protected String getHelptext() {
        return "\n " + MlM("Abschluss des Wizards.\n Ihre Anwendung ist nun startbereit!");
    }

    @Override
    protected Wizard getParentWizard() {
        return parentWizard;
    }

    public boolean allowJumpNextWindow() {
        return true;
    }

    public boolean allowJumpPrevWindow() {
        return true;
    }

    public boolean allowJumpToEnd() {
        return true;
    }

    public boolean allowCloseBeforeEnd() {
        return true;
    }

    public void onClose(WizardAction current_action) {

        switch(current_action)
        {
            case WIZARD_ACTION_CLOSE:
            case WIZARD_ACTION_FINISH:
                parentWizard.setWizardStatus(WizardStatus.CLOSED);
                break;
        }        
        super.close();
    }

    public void onInit() {
        setGuestContent();
        super.setVisible(true);
    }
}
