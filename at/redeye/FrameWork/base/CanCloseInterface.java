/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

/**
 *
 * @author martin
 */
public interface CanCloseInterface 
{
    public boolean isEdited();    
    public int checkSave();
    public void saveData();
}
