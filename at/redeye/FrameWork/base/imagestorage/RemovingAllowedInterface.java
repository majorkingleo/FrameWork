/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base.imagestorage;

import at.redeye.FrameWork.base.transaction.Transaction;

/**
 *
 * @author martin
 */
public interface RemovingAllowedInterface 
{
    public boolean canRemoveImage( Transaction trans, int id ) throws Exception;
}
