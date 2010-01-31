package at.redeye.HotlineHours;

import org.apache.log4j.Logger;

public class HotlineHoursMain {

	public static Logger logger = Logger.getLogger(HotlineHoursMain.class
			.getSimpleName());

    private static ModuleLauncher ml;

    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ml = new ModuleLauncher();
        ml.invoke();

	}

}
