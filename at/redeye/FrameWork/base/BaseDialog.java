/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.FrameWork.base;

import at.redeye.FrameWork.base.bindtypes.DBString;
import at.redeye.FrameWork.base.bindtypes.DBValue;
import at.redeye.FrameWork.base.tablemanipulator.TableManipulator;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MOMMSupportedDBMSTypes;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;

import at.redeye.UserManagement.impl.ExtKeyListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class BaseDialog extends javax.swing.JFrame  {
               
    private static final long serialVersionUID = 1L;
    protected Root root;
    private Transaction transaction = null;
    protected String title;
    private DBConnection con = null;
    private static Logger logger = Logger.getLogger(BaseDialog.class.getName());
    private Transaction seq_transaction = null;
    boolean edited = false;
    
    protected BaseDialog() {}
    
    public BaseDialog( Root root, String title )
    {
        this.root = root;                        
        this.title = title;
        setTitle( title );
        
        root.informWindowOpened();
        
        if (logger.isDebugEnabled()) {
        	logger.debug(title);
        }
        
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
           public void windowClosing(java.awt.event.WindowEvent e) {
                if( canClose() )
                    close();
           }
        });
                                
        
        int x = Integer.parseInt( root.getSetup().getLocalConfig(title.concat(Setup.WindowX), "300") );  
        int y = Integer.parseInt( root.getSetup().getLocalConfig(title.concat(Setup.WindowY), "300") );                          
        
        this.setBounds(x, y, 0, 0);                 
        this.addKeyListener(new ExtKeyListener(this));
        
        loadStuff();
    }
    
    static public abstract class Pair
    {
        public abstract void gui_to_var();
        public abstract void var_to_gui();        
    };
    
    static class TextStringPair extends Pair
    {
        JTextField textfield;
        StringBuffer value;
        
        public TextStringPair( JTextField textfield, StringBuffer value )
        {
            this.textfield = textfield;
            this.value = value;
        }
        
        public void gui_to_var()
        {
            value.delete(0, value.length());
            value.append(textfield.getText());
        }
        
        public void var_to_gui()
        {
            textfield.setText(value.toString());
        }
    }
    
    static class TextDBStringPair extends Pair
    {
        JTextField textfield;
        DBValue value;
        
        public TextDBStringPair( JTextField textfield, DBValue value )
        {
            this.textfield = textfield;
            this.value = value;
        }
        
        public void gui_to_var()
        {            
            value.loadFromString(textfield.getText());
        }
        
        public void var_to_gui()
        {
            textfield.setText(value.toString());
        }
    }
    
    public Vector<Pair> pairs = new Vector<Pair>();
    
    public void bindVar( JTextField jtext, StringBuffer var )
    {
        pairs.add( new TextStringPair( jtext, var ) );
    }        
    
    public void bindVar(JTextField jtext, DBValue var) {
         pairs.add( new TextDBStringPair( jtext, var ) );
    }
    
    public void var_to_gui()
    {        
        for( Pair pair : pairs )
        {
            pair.var_to_gui();
        }
    }
    
    public void gui_to_var()
    {
        for( Pair pair : pairs )
        {
            pair.gui_to_var();
        }
    }
    
    public Transaction getTransaction()
    {
        if( con == null )
            con = root.getDBConnection();
        
        if( con.hashCode() != root.getDBConnection().hashCode() )        
        {
            con = root.getDBConnection();
            transaction = null;            
        }
        
        if( transaction != null )
        {
            try {

                if (!transaction.isOpen()) {
                    root.getDBConnection().closeTransaction(transaction);
                    transaction = null;
                }
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(BaseDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if( transaction != null )        
            return transaction;
        
        if( root.getDBConnection() == null )
            return null;
        
        transaction = root.getDBConnection().getNewTransaction();
               
        return transaction;
    }
    
    public Transaction getNewTransaction()
    {                
        if( root.getDBConnection() == null )
            return null;
        
        Transaction trans = root.getDBConnection().getNewTransaction();

        
        return trans;
    }
    
    public void closeTransaction( Transaction tran ) throws SQLException
    {
        if( root.getDBConnection() == null )
            return;
        
        if( tran == null )
            return;
        
        tran.rollback();
        
        root.getDBConnection().closeTransaction(tran);
    }
    
    public void close()
    {
        root.getSetup().setLocalConfig(title.concat(Setup.WindowX), Integer.toString(this.getX()));
        root.getSetup().setLocalConfig(title.concat(Setup.WindowY), Integer.toString(this.getY()));        
        
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
        root.informWindowClosed();
        this.dispose();
    }
    
    /**
     * 
     * @param seqName
     * @return
     * @throws java.sql.SQLException
     * @throws at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException
     * @throws IOException 
     */
    public int getNewSequenceValue(String seqName) throws SQLException, UnsupportedDBDataTypeException, WrongBindFileFormatException, TableBindingNotRegisteredException, IOException
    {
        if( getTransaction().getDBMSType() == MOMMSupportedDBMSTypes.DB_SQLITE )
        {
            int value = getTransaction().getNewSequenceValue(seqName,1234567);
            return value;
        }
        else
        {
            if( seq_transaction == null )
                seq_transaction = getNewTransaction();
         
            int value = seq_transaction.getNewSequenceValue(seqName,1234567);
        
            seq_transaction.commit();
        
            return value;
        }
    }
    
    /*
     * @return 1 on Save Data
     *         0 on Don't Save
     *        -1 on Cancel
     */ 
    public int checkSave()
    {
        Object[] options = {"Daten Speichern", "Änderungen verwerfen", "Abbrechen"};

        int n = JOptionPane.showOptionDialog(null,
                "Sie haben Daten verändert, möchten Sie diese vorher Speichern?",
                getTitle(), JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (n) 
        {
            case 0:
                return 1;        
            case 1:
                return 0;                
            default:
                return -1;
        }
    }
    
    public int checkSave( TableManipulator tm )
    {
        tm.stopEditing();
        
        if( tm.getEditedRows().isEmpty() && edited == false )
        {            
            return 0;
        }
        else
        {
            int ret = checkSave();
                                    
            if( ret == -1 )
            {
                return -1;
            }
            else if( ret == 0 )
            {         
                return 0;
            }            
            else
            {
                return 1;
            }                
        }
    }        
    
    
    protected boolean canClose() {
        return true;
    }
    
    public void setEdited()
    {
        setEdited(true);
    }
    
    public boolean isEdited()
    {
        return edited;
    }
    
    public void setEdited( boolean val )
    {
        edited = val;
    }
    
    public void clearEdited()
    {
        setEdited(false);
    }

    /* should be removed later */
    private void loadStuff() 
    {
        StringUtils.set_defaultAutoLineLenght(Integer.valueOf(root.getSetup().getLocalConfig(FrameWorkConfigDefinitions.DefaultAutoLineBreakWidth)));
    }
    
    protected boolean checkAnyAndSingleSelection( JTable table )
    {
        if (table.getSelectedRowCount() <= 0) {
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
}
