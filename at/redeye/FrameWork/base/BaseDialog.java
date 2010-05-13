/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.FrameWork.base;

import at.redeye.FrameWork.base.bindtypes.DBFlagInteger;
import at.redeye.FrameWork.base.bindtypes.DBValue;
import at.redeye.FrameWork.base.prm.bindtypes.DBConfig;
import at.redeye.FrameWork.base.tablemanipulator.TableManipulator;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.base.translation.TranslationHelper;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MOMMSupportedDBMSTypes;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import javax.swing.KeyStroke;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class BaseDialog extends javax.swing.JFrame implements BindVarInterface {

    private static final long serialVersionUID = 1L;

    /**
     * reference to the root object
     */
    protected Root root;
    private Transaction transaction = null;

    /**
     * title of the dialog
     */
    protected String title;
    private DBConnection con = null;

    /**
     * reference to the logger object
     */
    protected static Logger logger = Logger.getLogger(BaseDialog.class.getName());
    private Transaction seq_transaction = null;
    protected Timer autoRefreshTimer = new Timer();
    protected TimerTask autoRefreshTask = new TimerTask() {

        @Override
        public void run() {

            doAutoRefresh();
        }
    };

    boolean edited = false;
    protected BindVarBase bind_vars = new BindVarBase();
    protected List<Runnable> onCloseListeners;
    protected CloseSubDialogHelper close_subdialog_helper;

    /**
     * All keys ESC, or F1, F2 listeners are registered in this container
     */
    protected HashMap<KeyStroke,Vector<Runnable>> listen_key_events = null;    
    private JRootPane myrootPane;
    protected Runnable HelpWinRunnable;
    protected UniqueDialogHelper unique_dialog_helper;

    private class ActionKeyListener implements ActionListener
    {
        KeyStroke key;

        public ActionKeyListener( KeyStroke key )
        {
            this.key = key;
        }

        public void actionPerformed(ActionEvent e) {
            if (listen_key_events == null) {
                return;
            }

            Vector<Runnable> functions = listen_key_events.get(key);

            for (Runnable runnable : functions) {
                runnable.run();
            }
        }
      }

    public BaseDialog(Root root, String title) {
        this.root = root;
        this.title = title;
        setTitle(title);

        root.informWindowOpened(this);

        if (logger.isDebugEnabled()) {
            logger.debug(title);
        }

        this.addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {                
                if (canClose()) {
                    close();
                }
            }
        });


        int x = Integer.parseInt(root.getSetup().getLocalConfig(title.concat(Setup.WindowX), "300"));
        int y = Integer.parseInt(root.getSetup().getLocalConfig(title.concat(Setup.WindowY), "300"));
        int w = Integer.parseInt(root.getSetup().getLocalConfig(title.concat(Setup.WindowWidth), "0"));
        int h = Integer.parseInt(root.getSetup().getLocalConfig(title.concat(Setup.WindowHeight), "0"));

        Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        
        if( dim.getWidth() < x + getWidth() )
            x = 300;
        
        if( dim.getHeight() < y + getHeight() )
            y = 300;
        
        if( x < 0 )
            x = 300;
        
        if( y < 0 )
            y = 300;
                
        this.setBounds(x, y, 0, 0);

        if( w > 0 && h > 0 && openWithLastWidthAndHeight() )
        {
            if( x + w > dim.getWidth() )
                w = (int)dim.getWidth() - x;

            if( y + h > dim.getHeight() )
                h = (int)dim.getHeight() - y;

            this.setPreferredSize(new Dimension(w,h));
        }
        //this.addKeyListener(new ExtKeyListener(this));

        registerActionKeyListener(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                new Runnable() {

                    public void run() {
                        if (canClose()) {
                            close();
                        }
                    }
                });

        new TranslationHelper(root,this);

        loadStuff();
    }

    /**
     * Overload this method, if the window shouldn't open with
     * with the last stored with and height.
     * @return true if the size of the dialog should be stored
     */
    protected boolean openWithLastWidthAndHeight()
    {
        return true;
    }

    /**
     * automatically opens the Help Windows, when F1 is pressed
     * @param runnable This runnable should open the Help Window
     */
    public void registerHelpWin( Runnable runnable )
    {
        HelpWinRunnable = runnable;

        registerActionKeyListener(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
                runnable );
    }

    /**
     * opens the registerd Help win by Hand
     */
    public void callHelpWin()
    {
        if( HelpWinRunnable != null )
        {
            setWaitCursor();
            HelpWinRunnable.run();
            setNormalCursor();
        }
    }

    /**
     * Registers a listener for a F1, ESC, or somthing global keypressed Event     
     * @param to_listen_Key Keyboard Key
     * @param runnable      Method to call
     */
    public void registerActionKeyListener( KeyStroke to_listen_Key, Runnable runnable )
    {
        if( listen_key_events == null )
            listen_key_events = new HashMap<KeyStroke,Vector<Runnable>>();

        Vector<Runnable> listeners = listen_key_events.get(to_listen_Key);

        if( listeners == null )
        {
            listeners = new Vector<Runnable>();
            listen_key_events.put(to_listen_Key, listeners);

            registerActionKeyListenerOnRootPane(to_listen_Key);
        }

        listeners.add(runnable);
    }

    private void registerActionKeyListenerOnRootPane(KeyStroke key)
    {
        if( myrootPane == null )
            return;

        myrootPane.registerKeyboardAction(new ActionKeyListener(key), key,JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    @Override
    protected JRootPane createRootPane()
    {
        myrootPane = super.createRootPane();

        // alle im Container beinhalteten listener anhängen

        if( listen_key_events != null )
        {
            for( KeyStroke key : listen_key_events.keySet() )
            {
                myrootPane.registerKeyboardAction(new ActionKeyListener(key), key,JComponent.WHEN_IN_FOCUSED_WINDOW);
            }
        }

        return myrootPane;
    }

    /**     
     * @return The Transaction object for this dialog
     * This Transaction object will be automatically closed, on closing this this
     * dialog. The Transaction object will be only created once in the lifetime
     * of the dialog. So caching the Transaction object is not required.
     * <b>Can return null, in case of no database connection.</b>
     */
    public Transaction getTransaction() {
        if (con == null) {
            con = root.getDBConnection();
        }
        // Here we have to check -> NULL pointer exception if no connection
        // exists, e.g. before inital Setup
        if (con == null) {
            return null;
        }

        if (con.hashCode() != root.getDBConnection().hashCode()) {
            con = root.getDBConnection();
            transaction = null;
        }

        if (transaction != null) {
            try {

                if (!transaction.isOpen()) {
                    root.getDBConnection().closeTransaction(transaction);
                    transaction = null;
                }
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(BaseDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (transaction != null) {
            return transaction;
        }

        if (root.getDBConnection() == null) {
            return null;
        }

        transaction = root.getDBConnection().getNewTransaction();

        return transaction;
    }

    /**
     * @return A new Transaction object, of the current database connection
     * This Transactino won't be closed on dialog closing event automatically
     * You have to close each allocated Transaction object yourself by calling
     * <b>closeTransaction()</b>
     *
     * The Transaction object will by destroyed atomatically on appliaction shutdown
     */
    public Transaction getNewTransaction() {
        if (root.getDBConnection() == null) {
            return null;
        }

        Transaction trans = root.getDBConnection().getNewTransaction();


        return trans;
    }

    /**
     * closes a given Transaction object. Rollback is done automatically.
     * @param tran a valid Transaction object
     * @throws SQLException if rollback fails
     */
    public void closeTransaction(Transaction tran) throws SQLException {
        if (root.getDBConnection() == null) {
            return;
        }

        if (tran == null) {
            return;
        }

        tran.rollback();

        root.getDBConnection().closeTransaction(tran);
    }

    /**
     * closes the current dialog.
     */
    public void close() {
        root.getSetup().setLocalConfig(title.concat(Setup.WindowX), Integer.toString(this.getX()));
        root.getSetup().setLocalConfig(title.concat(Setup.WindowY), Integer.toString(this.getY()));
        root.getSetup().setLocalConfig(title.concat(Setup.WindowWidth), Integer.toString(this.getWidth()));
        root.getSetup().setLocalConfig(title.concat(Setup.WindowHeight), Integer.toString(this.getHeight()));

        try {
            if (transaction != null) {
                transaction.rollback();
                root.getDBConnection().closeTransaction(transaction);
            }
            if (seq_transaction != null) {
                seq_transaction.rollback();
                root.getDBConnection().closeTransaction(seq_transaction);
            }
        } catch (SQLException ex) {
            logger.error(ex);
        }

        root.informWindowClosed(this);

        if( onCloseListeners != null )
        {
            for( Runnable run : onCloseListeners)
                run.run();

            onCloseListeners.clear();
        }

        this.dispose();
    }

    /**
     * Schließt das Fenster, ohne die Appliaktion zu beenden, auch wenn
     * das zu schließende Fenster das Letzte offene ist. Das default
     * Verhalten der Appliaktion ist, dass beim Schließen des letzten offenen
     * Fensters die komplette Applikation geschlossen wird.
     */
    public void closeNoAppExit()
    {
        close();
    }

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
    public int getNewSequenceValue(String seqName) throws SQLException, UnsupportedDBDataTypeException, WrongBindFileFormatException, TableBindingNotRegisteredException, IOException {
        if (getTransaction().getDBMSType() == MOMMSupportedDBMSTypes.DB_SQLITE) {
            int value = getTransaction().getNewSequenceValue(seqName, 1234567);
            return value;
        } else {
            if (seq_transaction == null) {
                seq_transaction = getNewTransaction();
            }

            int value = seq_transaction.getNewSequenceValue(seqName, 1234567);

            seq_transaction.commit();

            return value;
        }
    }

    /**
     * @return 1 on Save Data  <br/>
     *         0 on Don't Save <br/>
     *        -1 on Cancel <br/>
     */
    public int checkSave() {
        Object[] options = {"Daten Speichern", "Änderungen verwerfen", "Abbrechen"};

        int n = JOptionPane.showOptionDialog(null,
                StringUtils.autoLineBreak(
                "Sie haben Daten verändert. "+
                "Möchten Sie die Daten vor dem Verlassen des Dialoges speichern?"),
                getTitle(), JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (n) {
            case 0:
                return 1;
            case 1:
                return 0;
            default:
                return -1;
        }
    }

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
    public int checkSave(TableManipulator tm) {
        tm.stopEditing();

        if (tm.getEditedRows().isEmpty() && edited == false) {
            return 0;
        } else {
            int ret = checkSave();

            if (ret == -1) {
                return -1;
            } else if (ret == 0) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    protected void doAutoRefresh() {
    }

    /**
     * to be overrided by subdialogs
     * @return true if the dialog can be closed
     */
    protected boolean canClose() {
        return true;
    }

    public void setEdited() {
        setEdited(true);
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean val) {
        edited = val;
    }

    public void clearEdited() {
        setEdited(false);
    }

    /* should be removed later */
    private void loadStuff() {
        StringUtils.set_defaultAutoLineLenght(Integer.valueOf(root.getSetup().getLocalConfig(FrameWorkConfigDefinitions.DefaultAutoLineBreakWidth)));
    }

    /**
     * Kontrolliert, ob in der übergebenen Tabelle nur ein Eintrag selektiert
     * wurde. Wurde mehr als ein Eintrag selektiert, bekommt der User eine
     * entsprechende Fehlermeldeung aufgeschalten und der Rückgabewert der Funktion
     * ist false.
     * @param table eine jTable
     * @return  <b>true</b> Wenn nur ein Eintrag selektiert wurde und <b>false</b>, wenn
     * kein, oder mehrere Einträge selektiert wurden. Eine ensprechende Fehlermeldung
     * ist dabei dem User schon aufgeschalten worden.
     */
    protected boolean checkAnyAndSingleSelection(JTable table) {
        if (table.getSelectedRowCount() <= 0) {
            JOptionPane.showMessageDialog(null,
                    StringUtils.autoLineBreak("Bitte wählen Sie einen Eintrag aus."),
                    "Fehler",
                    JOptionPane.OK_OPTION);
            return false;
        }

        if (table.getSelectedRowCount() > 1) {
            JOptionPane.showMessageDialog(null,
                    "Bitte nur einen Eintrag auswählen.",
                    "Fehler",
                    JOptionPane.OK_OPTION);
            return false;
        }

        return true;
    }

    /**
     * in jTextField an einen StringBuffer anbinden
     * @param jtext das Textfeld
     * @param var   der StringBuffer
     *
     * Bei einem Aufruf von var_to_gui(), oder gui_to_var(), wird
     * dann der demenstprechende Inhalt entweder vom GUI zu Variablen,
     * oder umgekehrt übertragen.
     */
    public void bindVar(JTextField jtext, StringBuffer var) {
       bind_vars.bindVar(jtext,var);
    }

    /**
     * in jTextField an einen StringBuffer anbinden
     * @param jtext das Textfeld
     * @param var   der StringBuffer
     *
     * Bei einem Aufruf von var_to_gui(), oder gui_to_var(), wird
     * dann der demenstprechende Inhalt entweder vom GUI zu Variablen,
     * oder umgekehrt übertragen.
     */
    public void bindVar(JPasswordField jtext, StringBuffer var) {
       bind_vars.bindVar(jtext,var);
    }

    /**
     * Ein jTextField an eine DBValue anbinden
     * @param jtext das Textfeld
     * @param var   die Datenbankvariable
     *
     * Bei einem Aufruf von var_to_gui(), oder gui_to_var(), wird
     * dann der demenstprechende Inhalt entweder vom GUI zu Variablen,
     * oder umgekehrt übertragen.
     */
    public void bindVar(JTextField jtext, DBValue var) {
       bind_vars.bindVar(jtext,var);
    }
    
    /**
     * Eine JCheckBox an eine DBFlagInteger Variable anbinden
     * @param jtext die Textbox
     * @param var   die Datebanvariable
     *
     * Bei einem Aufruf von var_to_gui(), oder gui_to_var(), wird
     * dann der demenstprechende Inhalt entweder vom GUI zu Variablen,
     * oder umgekehrt übertragen.
     */
    @Override
    public void bindVar(JCheckBox jtext, DBFlagInteger var) {
       bind_vars.bindVar(jtext,var);
    }

    /**
     * Alle Werte der angebunden Variablen in die entsprechenden GUI Komponenten übertragen
     */
    @Override
    public void var_to_gui() {
        bind_vars.var_to_gui();
    }

    /**
     * Alle Elemnte des GUIs in die angebundenen Datenbankfelder kopieren
     */
    @Override
    public void gui_to_var() {
        bind_vars.gui_to_var();
    }

    /**
     * gibt die <b>root</b> Klasse zurück
     * @return
     */
    public Root getRoot()
    {
        return root;
    }

    /**
     * Setzt den Sanduhr Mauscursor
     */
    public void setWaitCursor()
    {
        setWaitCursor(true);
    }

    /**
     * Setzt den Sanduhr, oder "normale" Mauscursor
     * @param state <b>true</b> für die Sanduhr und <b>false</b> für den nurmalen Cursor
     */
    public void setWaitCursor( boolean state )
    {
        if( state )
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        else
            setCursor(Cursor.getDefaultCursor());
    }

    /**
     * Setzt wieder den "normalen" Mauscursor
     */
    public void setNormalCursor()
    {
        setWaitCursor(false);
    }

    /**
     * Konfiguriert das jScrollpanel entsprechen dem im Setup hinterlegten
     * Geschwindigkeit. Vom User über den Parameter VerticalScrollingSpeed einstellbar.
     * @param scroll_panel
     */
    public void adjustScrollingSpeed( JScrollPane scroll_panel )
    {
        try {
            adjustScrollingSpeed(scroll_panel.getVerticalScrollBar(), BaseAppConfigDefinitions.VerticalScrollingSpeed);
            adjustScrollingSpeed(scroll_panel.getVerticalScrollBar(), BaseAppConfigDefinitions.HorizontalScrollingSpeed);
        } catch( NumberFormatException ex ) {
            logger.error(ex);            
            return;
        }
    }

    private void adjustScrollingSpeed(JScrollBar ScrollBar, DBConfig config)
    {
        String value = root.getSetup().getLocalConfig(config);

        Integer i = Integer.parseInt(value);

        if( i  <=  0 )
        {
            logger.error("invalid scrolling interval: " + i + " using default value: " + config.getConfigValue() );
            i = Integer.parseInt(config.getConfigValue());
        }

        ScrollBar.setUnitIncrement(i);
    }

    /**
     * Little helper function that sets the frame visible and
     * push it to front, by useing the wait cursor.
     * @param frame
     */
    public void invokeDialog( JFrame frame )
    {
        setWaitCursor();
        frame.setVisible(true);
        frame.toFront();
        setNormalCursor();
    }

    /**
     * Little helper function that sets the frame visible and
     * push it to front, by useing the wait cursor.
     * @param frame
     */
    public void invokeDialog( BaseDialog dlg )
    {
        setWaitCursor();
        dlg.setVisible(true);
        dlg.toFront();

        if( close_subdialog_helper == null )
            close_subdialog_helper = new CloseSubDialogHelper(this);

        if( closeSubdialogsOnClose() )
            close_subdialog_helper.closeSubDialog(dlg);

        setNormalCursor();
    }

    public void invokeDialogUnique( BaseDialog dialog )
    {
        setWaitCursor();

        if( unique_dialog_helper == null )
            unique_dialog_helper = new UniqueDialogHelper();

        if( close_subdialog_helper == null )
            close_subdialog_helper = new CloseSubDialogHelper(this);

        BaseDialog d_unique = unique_dialog_helper.invokeUniqueDialog(dialog);
        d_unique.setVisible(true);
        d_unique.toFront();

        if( closeSubdialogsOnClose() )
            close_subdialog_helper.closeSubDialog(dialog);

        setNormalCursor();
    }

    /**
     * @return should return a unique identifier for this dialog,
     * by default it's the Classname + "/" + title
     */
    public String getUniqueIdentifier()
    {
        return this.getClass().getName() + "/" + getTitle();
    }

    public void registerOnCloseListener( Runnable runnable )
    {
        if( runnable == null )
            return;

        if( onCloseListeners == null )
            onCloseListeners = new LinkedList<Runnable>();

        onCloseListeners.add(runnable);
    }

    public void deregisterOnCloseListener( Runnable runnable )
    {
        if( onCloseListeners == null )
            return;

        int index = onCloseListeners.indexOf(runnable);

        if( index == -1 )
            return;

        onCloseListeners.remove(index);
    }

    public boolean closeSubdialogsOnClose()
    {
        return true;
    }
}
