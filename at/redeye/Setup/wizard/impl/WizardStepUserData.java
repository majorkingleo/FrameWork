/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.Setup.wizard.impl;

import java.awt.Dimension;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.wizards.WizardAction;
import at.redeye.FrameWork.base.wizards.WizardWindowInterface;
import at.redeye.FrameWork.base.wizards.impl.Wizard;
import at.redeye.FrameWork.base.wizards.impl.WizardBaseWindow;
import at.redeye.UserManagement.impl.AdminDlg;

/**
 *
 * @author Mario
 */
public class WizardStepUserData extends WizardBaseWindow implements WizardWindowInterface {

	private static final long serialVersionUID = 1L;
	private AdminDlg dlg = null;
    private Wizard parentWizard = null;

    public WizardStepUserData(Root root, Wizard parent) {

        super(root, "Benutzerdaten");
        this.parentWizard = parent;
        dlg = new AdminDlg(root, parent);

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
        super.close();
    }

    public void onInit() {
        setGuestContent();
        super.setVisible(true);
    }

    @Override
    protected void setGuestContent() {

        super.panelGuestContent.add(dlg.getRootPane());
        super.panelGuestContent.setPreferredSize(new Dimension(300, 200));
        super.panelGuestContent.updateUI();
        dlg.feed_table();
    }

    @Override
    protected String getHelptext() {
        return "\n In diesem Setup-Abschnitt können Sie Ihre Benutzerdaten einpflegen.";
    }

    @Override
    protected Wizard getParentWizard() {
        return parentWizard;
    }
}
