package at.redeye.FrameWork.base.wizards.impl;

public interface WizardListener {
	
	public static enum WizardStatus {
		OPENED,
		CLOSED
	}
	
	public void onStateChange (WizardStatus currentWizardStatus);

}
