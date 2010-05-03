/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Setup.wizard.impl;

import at.redeye.FrameWork.base.LocalRoot;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.wizards.WizardProperties;
import at.redeye.FrameWork.base.wizards.impl.Wizard;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * 
 * @author Mario
 */

public class SetupWizard {

	private static Logger logger = Logger.getLogger(SetupWizard.class
			.getSimpleName());

	public static void main(String[] args) {

		BasicConfigurator.configure();
		WizardProperties props = new WizardProperties();
		props.setButtonNextText("Vorwärts");
		Root root = new LocalRoot("RedEye Labs Setup Wizard");
		Wizard wizard = new Wizard(props);
		WizardStepDBSetup dbSetup = new WizardStepDBSetup(root, wizard);
		WizardStepWelcome welcome = new WizardStepWelcome(root, wizard);
		WizardStepUserData user = new WizardStepUserData(root, wizard);
		WizardStepFinished finish = new WizardStepFinished(root, wizard);

		wizard.addWindow(welcome);
		wizard.addWindow(dbSetup);
		wizard.addWindow(user);
		wizard.addWindow(finish);
		logger.trace("Starting wizard");
		wizard.start();

	}

}
