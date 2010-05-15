/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Setup.wizard.impl;

import at.redeye.FrameWork.base.ConnectionDialog;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.wizards.WizardAction;
import at.redeye.FrameWork.base.wizards.WizardWindowInterface;
import at.redeye.FrameWork.base.wizards.impl.Wizard;
import at.redeye.FrameWork.base.wizards.impl.WizardBaseWindow;

/**
 *
 * @author Mario
 */
public class WizardStepDBSetup extends WizardBaseWindow implements WizardWindowInterface {


   
	private static final long serialVersionUID = 1L;
	private ConnectionDialog dlg;
    private Wizard parentWizard = null;


    public WizardStepDBSetup(Root root, Wizard parent) {
        super(root, "Datenbank Setup");
        this.parentWizard = parent;
        dlg = new ConnectionDialog(root, parent);
    }
    
    public boolean allowJumpNextWindow() {
        return false;
    }

    public boolean allowJumpPrevWindow() {
        return true;
    }

    public boolean allowJumpToEnd() {
        return false;
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

    public String getWindowHelp() {
        return "Datenbank einrichten!";
    }


    @Override
    protected String getHelptext() {
        return "\n Datenbank einrichten und Verbindungstest";
    }

    @Override
    protected void setGuestContent() {
       super.panelGuestContent.add (dlg.getContentPane());
    }

    @Override
    protected Wizard getParentWizard() {
        return parentWizard;
    }


}
