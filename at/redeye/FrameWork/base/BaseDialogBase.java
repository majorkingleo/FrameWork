/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import at.redeye.FrameWork.base.tablemanipulator.TableManipulator;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

/**
 *
 * @author martin
 */
public interface BaseDialogBase
{
    public void close();
    public boolean canClose();

    public void setTitle(String title);

    public String getTitle();

    /**
     * @param requester The calling object (can be used to implement different behavoir for eg
     *        saving size of window and table. Can be null
     *
     * @return a Dialog identifier for saving some data, eg:
     * width an height of the dialog.
     * The default behavior is retuning the dialog title.
     * This function should be overloaded if some instances of dialogs
     * should all have the same eg size but it's no possible, because
     * each one has a different title.
     */

    public String getUniqueDialogIdentifier(Object requester);

    public void addWindowListener(WindowListener windowListener);

    public int getWidth();

    public int getHeight();

    public void setBounds(int x, int y, int i, int i0);

    public boolean openWithLastWidthAndHeight();

    public void setPreferredSize(Dimension dimension);

    /**
     * Registers a listener for a F1, ESC, or somthing global keypressed Event
     * @param to_listen_Key Keyboard Key
     * @param runnable      Method to call
     */
    public void registerActionKeyListener(KeyStroke keyStroke, Runnable runnable);

    public Container getContainer();

    public void doAutoRefresh();

    public void setCursor(Cursor predefinedCursor);

    public void adjustScrollingSpeed( JScrollPane scroll_panel );

    public void setVisible(boolean b);

    public void toFront();

    public void invokeDialog( JFrame frame );

    public void invokeDialog( BaseDialogBase dlg );

    public void invokeDialogModal( BaseDialogDialog dlg );

    public void invokeDialogUnique( BaseDialogBase dialog );

    public void registerOnCloseListener(Runnable runnable);

    public void deregisterOnCloseListener( Runnable runnable );

    public boolean closeSubdialogsOnClose();

    public void setEdited();

    public boolean isEdited();

    public void setEdited(boolean val);

    public void clearEdited();

    /**
     * Checks, if data within the table have been change, asks the
     * user what sould be done (save it, don't save it, or cancel current operation
     * @param tm TableManipulator object
     * @return
     *   1 when the data should by saved <br/>
     *   0 on saving should be done <br/>
     *  -1 cancel current operation <br/>
     *
     */
    public int checkSave(TableManipulator tm);

    /**
     * @return 1 on Save Data  <br/>
     *         0 on Don't Save <br/>
     *        -1 on Cancel <br/>
     */
    public int checkSave();

    /**
     * @return The Transaction object for this dialog
     * This Transaction object will be automatically closed, on closing this this
     * dialog. The Transaction object will be only created once in the lifetime
     * of the dialog. So caching the Transaction object is not required.
     * <b>Can return null, in case of no database connection.</b>
     */
    public Transaction getTransaction();

    /**
     * @return A new Transaction object, of the current database connection
     * This Transactino won't be closed on dialog closing event automatically
     * You have to close each allocated Transaction object yourself by calling
     * <b>closeTransaction()</b>
     *
     * The Transaction object will by destroyed atomatically on appliaction shutdown
     */
     public Transaction getNewTransaction();

    /**
     * closes a given Transaction object. Rollback is done automatically.
     * @param tran a valid Transaction object
     * @throws SQLException if rollback fails
     */
    public void closeTransaction(Transaction tran) throws SQLException;

/**
     * Ermittelt den nächsten Wert für eine gegebene Sequenz
     * @param seqName
     * @return den nächsten Wert der Sequenz
     * @throws java.sql.SQLException
     * @throws at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException
     * @throws WrongBindFileFormatException
     * @throws TableBindingNotRegisteredException
     * @throws IOException
     */
    public int getNewSequenceValue(String seqName) throws SQLException, UnsupportedDBDataTypeException, WrongBindFileFormatException, TableBindingNotRegisteredException, IOException;

    public void dispose();

    public int getX();

    public int getY();

    public void closeNoAppExit();

    public void setBindVarsChanged( boolean state );

    public void setBindVars( BindVarInterface bind_vars );

    /**
    * language the dialog is programmed in
    * if not set, the settings from Root.getBaseLangague() are used     
    */
    public void setBaseLanguage( String language );

    /**
    * @return language the dialog is programmed in
    * if not set, the settings from Root.getBaseLangague() are used
    */    
    public String getBaseLanguage();

    /**
     * @param message native langauge message
     * @return translated message, if available
     */
    public String MlM( String message );
}
