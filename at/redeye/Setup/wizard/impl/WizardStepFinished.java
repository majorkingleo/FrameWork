/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.Setup.wizard.impl;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.wizards.impl.Wizard;
import at.redeye.FrameWork.base.wizards.impl.WizardBaseWindow;

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
    }

    @Override
    protected void setGuestContent() {
        super.panelGuestContent.add(finish);
    }

    @Override
    protected String getHelptext() {
        return "\n Abschluss des Wizards.\n Ihre Anwendung ist nun startbereit!";
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

    public void onClose() {
        super.close();
    }

    public void onInit() {
        setGuestContent();
        super.setVisible(true);
    }
}
