/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.FrameWork.base.prm.impl;


/**
 *
 * @author mmattl
 */
public class PrmDefaultCheckSuite {

    public static boolean passesNumeric(String prmToCheck, PrmActionEvent event) {

        if (!isMine(prmToCheck, event)) {
            return true;
        }

        try {
            Double.parseDouble(event.getNewPrmValue().toString());
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    public static boolean passesJaNein(String prmToCheck, PrmActionEvent event) {

        String [] validStr = {"ja", "nein", "true", "false", "yes", "no"};

        if (!isMine(prmToCheck, event)) {
            return true;
        }

        for (int idx = 0; idx < validStr.length; idx++) {

            if (event.getNewPrmValue().toString().equalsIgnoreCase(validStr[idx])) {
                return true;
            }

        }
        return false;

    }

    public static boolean passesHasAValueEqual(String prmToCheck, PrmActionEvent event, String values[]) {

        if (!isMine(prmToCheck, event)) {
            return true;
        }

        for (int idx = 0; idx < values.length; idx++) {
            if (values[idx].equals(event.getNewPrmValue().toString())) {
                return true;
            }
        }
  
        return false;

    }

    private static boolean isMine(String prmToCheck, PrmActionEvent event) {
        if (prmToCheck.equals(event.getParameterName().toString())) {
            return true;
        }
        return false;
    }


}
