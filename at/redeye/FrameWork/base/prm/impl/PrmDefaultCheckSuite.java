/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.FrameWork.base.prm.impl;

import at.redeye.FrameWork.base.prm.PrmDefaultChecksInterface;

/**
 *
 * @author mmattl
 */
public class PrmDefaultCheckSuite implements PrmDefaultChecksInterface {

    private long checks2Execute = 0x0;

    public PrmDefaultCheckSuite(long checks2Execute) {

        this.checks2Execute = checks2Execute;

    }

    private static boolean passesDouble(PrmActionEvent event) {

        try {
            Double.parseDouble(event.getNewPrmValue().toString());
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    private static boolean passesBit(PrmActionEvent event) {

        try {
            Boolean.parseBoolean(event.getNewPrmValue().toString());
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    private static boolean passesLong(PrmActionEvent event) {

        try {
            Long.parseLong(event.getNewPrmValue().toString());
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    private static boolean passesJaNein(PrmActionEvent event) {

        String[] validStr = {"ja", "nein", "true", "false", "yes", "no"};

        for (int idx = 0; idx < validStr.length; idx++) {

            if (event.getNewPrmValue().toString().equalsIgnoreCase(validStr[idx])) {
                return true;
            }

        }
        return false;

    }

    private static boolean passesHasAValueEqual(PrmActionEvent event) {

        String[] values = event.getPossibleVals();
        for (int idx = 0; idx < values.length; idx++) {
            System.out.println("Checking "+values[idx]+ " / "+ event.getNewPrmValue().toString());
            if (values[idx].equals(event.getNewPrmValue().toString())) {
                return true;
            }
        }

        return false;

    }

    public boolean doChecks(PrmActionEvent event) {


        if ((checks2Execute & PRM_IS_DOUBLE) != 0) {
            System.out.println("DOUBLE JA");
            if (!passesDouble(event)) {
                return false;
            }
        }

        if ((checks2Execute & PRM_IS_LONG) != 0) {
            System.out.println("LONG JA");
            if (!passesLong(event)) {
                return false;
            }
        }

        if ((checks2Execute & PRM_IS_BIT) != 0) {
            System.out.println("BIT JA");
            if (!passesBit(event)) {
                return false;
            }
        }

        if ((checks2Execute & PRM_IS_TRUE_FALSE) != 0) {
            System.out.println("TRUE/FALSE JA");
            if (!passesJaNein(event)) {
                return false;
            }
        }

        if ((checks2Execute & PRM_HAS_VALUE) != 0) {
            System.out.println("USER VALUE JA");
            if (!passesHasAValueEqual(event)) {
                return false;
            }
        }
        return true;
    }
}
