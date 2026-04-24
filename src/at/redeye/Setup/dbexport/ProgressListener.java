/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Setup.dbexport;

/**
 *
 * @author martin
 */
public interface ProgressListener
{
    /**     
     * @param stage a info text to pop into a label
     */
    void setStage( String stage );

    /**
     * sets the number of ticks for a progress bar
     * @param count
     */
    void setOverallCounter( int count );

    /**     
     * @param val current value
     */
    void setCounter( int val );

    boolean canContinue();
}
