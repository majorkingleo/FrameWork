/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.Setup.wizard.impl;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.wizards.WizardAction;
import at.redeye.FrameWork.base.wizards.impl.Wizard;
import at.redeye.FrameWork.base.wizards.impl.WizardBaseWindow;

/**
 *
 * @author Mario
 */
public class WizardStepWelcome extends WizardBaseWindow {

    
	private static final long serialVersionUID = 1L;
	private WizardPanelWelcome welcome;
    private Wizard parentWizard = null;

    public WizardStepWelcome(Root root, Wizard parent) {
        super(root, "Begrüßung");
        this.parentWizard = parent;
        welcome = new WizardPanelWelcome();
    }

    @Override
    protected void setGuestContent() {
        super.panelGuestContent.add(welcome);
    }

    @Override
    protected String getHelptext() {
        return "\n Der Setup Wizard ermöglich Ihnen die initiale Parametrierung \n "
                + "Ihrer RedEye Labs Anwendung.";
    }

    public boolean allowJumpNextWindow() {
        return true;
    }

    public boolean allowJumpPrevWindow() {
        return false;
    }

    public boolean allowJumpToEnd() {
        return true;
    }

    public boolean allowCloseBeforeEnd() {
        return true;
    }

    public void onClose(WizardAction current_action) {
        super.close();
    }

    public void onInit() {

        setGuestContent();
        super.setVisible(true);
    }

    @Override
    protected Wizard getParentWizard() {
        return parentWizard;
    }
}
