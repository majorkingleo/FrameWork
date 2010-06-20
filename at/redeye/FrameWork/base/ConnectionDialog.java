/*
 * ConnectionDialog.java
 *
 * Created on 5. November 2008, 23:47
 */

package at.redeye.FrameWork.base;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

import java.sql.SQLException;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import at.redeye.FrameWork.base.dbmanager.DBBindtypeManager;
import at.redeye.FrameWork.base.transaction.DerbyTransaction;
import at.redeye.FrameWork.base.transaction.MSSQLTransaction;
import at.redeye.FrameWork.base.transaction.MySQLTransaction;
import at.redeye.FrameWork.base.transaction.OracleTransaction;
import at.redeye.FrameWork.base.transaction.SqLiteTransaction;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.base.wizards.WizardAction;
import at.redeye.FrameWork.base.wizards.WizardClientActionInterface;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.FrameWork.widgets.HideContentWhenDisabled;
import at.redeye.FrameWork.widgets.HideContentWhenDisabledPasswd;
import at.redeye.Setup.ConfigCheck.CheckConfigBase;
import at.redeye.Setup.ConfigCheck.Checks.HaveDbConnection;
import at.redeye.SqlDBInterface.SqlDBConnection.DbConnectionInterface;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.ConnectionDefinition;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.DBConnector;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.SupportedDBMSTypes;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MissingConnectionParamException;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.UnSupportedDatabaseException;

/**
 *
 * @author  martin
 */
public class ConnectionDialog extends BaseDialog {
    
    private static final long serialVersionUID = 1L;
    
    StringBuffer DBHost = new StringBuffer();
    SupportedDBMSTypes DBType;
    StringBuffer DBUser = new StringBuffer();
    StringBuffer DBPasswd = new StringBuffer();
    StringBuffer DBDatabase = new StringBuffer();
    StringBuffer DBInstance = new StringBuffer();
    StringBuffer DBPort = new StringBuffer();
    DBBindtypeManager bindtypeManager = null;
    private WizardClientActionInterface wizardAction = null;
    
    /** Creates new form ConnectionDialog */
    public ConnectionDialog( Root root, WizardClientActionInterface wizardAction) {
        super( root , "Datenbankverbindung");
        this.wizardAction = wizardAction;

        initComponents();

        initCommon();
    }

    public ConnectionDialog( Root root ) {
        super( root , "Datenbankverbindung");
  
        initComponents();

        initCommon();
    }

    private void initCommon()
    {
        setBindtypeManager( root.getBindtypeManager() );

        /* Das ganze dient nur dazu, damit nichts vorbelegt wird,
         * was in der Combobox dann nicht zur auswahl steht, und dann
         * werden die Spalten nicht richtig ein, bzw ausgeblendet.
         */
        SupportedDBMSTypes tt[] = SupportedDBMSTypes.values();

        SupportedDBMSTypes default_type = SupportedDBMSTypes.DB_MYSQL;

        for( SupportedDBMSTypes t2 : tt )
        {
            if( bindtypeManager.is_dbms_driver_loaded(t2) )
            {
                default_type = t2;
                break;
            }
        }

        String passwd =  root.getSetup().getLocalConfig(Setup.DBPasswd, "" );

        passwd = EncryptedDBPasswd.tryDecryptDBPassword(passwd, root.getAppName());

        DBHost.append( EncryptedDBPasswd.tryDecryptDBPassword(root.getSetup().getLocalConfig(Setup.DBHost, "localhost" ) , root.getAppName()) );
        DBUser.append( EncryptedDBPasswd.tryDecryptDBPassword(root.getSetup().getLocalConfig(Setup.DBUser, "" ), root.getAppName()) );
        DBPasswd.append( passwd );
        DBDatabase.append( EncryptedDBPasswd.tryDecryptDBPassword(root.getSetup().getLocalConfig(Setup.DBDatabase, "" ), root.getAppName() ));
        DBInstance.append( EncryptedDBPasswd.tryDecryptDBPassword(root.getSetup().getLocalConfig(Setup.DBInstance, "" ), root.getAppName() ));
        DBPort.append( EncryptedDBPasswd.tryDecryptDBPassword(root.getSetup().getLocalConfig(Setup.DBPort, "" ), root.getAppName() ));
        DBType = SupportedDBMSTypes.valueOf(root.getSetup().getLocalConfig(Setup.DBType, default_type.toString() ) );
              
        bindVar(JTHost, DBHost);
        bindVar(JCType, DBType);
        bindVar(JTUser, DBUser);
        bindVar(JTPasswd, DBPasswd);
        bindVar(JTDatabase, DBDatabase);
        bindVar(JTPort, DBPort);
        bindVar(JTInstance, DBInstance);        

        if( DBType == SupportedDBMSTypes.DB_JAVADB )
        {
            if( DBDatabase.toString().trim().isEmpty() )
            {
                 DBDatabase.setLength(0);
                 DBDatabase.append("APPHOME/db");
            }
        }

        JCType.addActionListener(new ActionListener() {

            boolean initial = true;

            public void actionPerformed(ActionEvent e) {

                if( wizardAction != null )
                {
                    if( initial )
                        initial = false;
                    else
                        wizardAction.applyAction(WizardAction.WIZARD_ACTION_NEXT, false);
                }

                JTDatabase.setEditable(true);
                JTHost.setEditable(true);
                JTPort.setEditable(true);
                JTUser.setEditable(true);
                JTPasswd.setEditable(true);

                logger.info("selectd item: " + JCType.getSelectedItem());

                if( JCType.getSelectedItem() == SupportedDBMSTypes.DB_ORACLE )
                {
                    logger.info("Oracle");
                    JTDatabase.setEditable(false);
                    JTInstance.setEditable(true);

                } else if( JCType.getSelectedItem() == SupportedDBMSTypes.DB_SQLITE ||
                           JCType.getSelectedItem() == SupportedDBMSTypes.DB_JAVADB ) {

                    logger.info("file database");
                    JTDatabase.setEditable(true);
                    JTInstance.setEditable(false);
                    JTHost.setEditable(false);
                    JTPort.setEditable(false);
                    JTUser.setEditable(false);
                    JTPasswd.setEditable(false);

                    if( JCType.getSelectedItem() == SupportedDBMSTypes.DB_JAVADB  )
                    {
                        if( JTDatabase.getText().isEmpty() )
                           JTDatabase.setText("APPHOME/db");
                    }

                } else {
                    logger.info("something else");
                    JTInstance.setEditable(false);
                }
            }

        });

        var_to_gui();
    }

    void bindVar( JComboBox box, SupportedDBMSTypes  t )
    {
        box.removeAllItems();                
        
        SupportedDBMSTypes tt[] = SupportedDBMSTypes.values();
        
        for( SupportedDBMSTypes t2 : tt )
        {
            if( bindtypeManager.is_dbms_driver_loaded(t2) )
                box.addItem(t2);
        }
        
        class TypePair extends Pair
        {
            public void var_to_gui()
            {
                JCType.setSelectedItem(DBType);                
            }
            
            public void gui_to_var()
            {
                DBType = (SupportedDBMSTypes)JCType.getSelectedItem();
            }

            @Override
            public Object get_first() {
                return JCType;
            }

            @Override
            public Object get_second() {
                return DBType;
            }
        }
        
        helper.bind_vars.pairs.add(new TypePair() );
    }
    
    ConnectionDefinition getDefinition()
    {
        String instance;
            
        if( DBType == SupportedDBMSTypes.DB_ORACLE )
            instance = DBInstance.toString();
        else if( DBType == SupportedDBMSTypes.DB_JAVADB )
        {
            instance = DBDatabase.toString();

            if( instance.startsWith("APPHOME") )
            {
                instance = instance.replace("APPHOME", Setup.getAppConfigDir(root.getAppName()) );
            }
        } else
            instance = DBDatabase.toString();
            
        int port = 0;
            
        if( !DBPort.toString().isEmpty() )
            port = Integer.parseInt(DBPort.toString());
            
        ConnectionDefinition connparams = new ConnectionDefinition(
               DBHost.toString(),
               port,
               DBUser.toString(),
               DBPasswd.toString(),                    
               instance,
               DBType
               );
            
        return connparams;
    }
    
    Connection try_connect()
    {
        try
        {        
            gui_to_var();
            System.out.println(DBHost + " " + DBType +  " " + DBUser + " " + DBPasswd + " " + DBDatabase );

            if( DBType == null )
                return null;                               
            
            ConnectionDefinition connparams = getDefinition();
            
            DbConnectionInterface connint = new DBConnector(connparams);

            Connection my_db_conn = connint.connectToDatabase();

            if( !my_db_conn.isClosed() )
                return my_db_conn;
        
        } catch( NoClassDefFoundError e ) {
            System.out.println("FAILED");
            e.printStackTrace();
        } catch( ClassNotFoundException e ) {
            System.out.println("FAILED");
            e.printStackTrace();
        } catch( Exception e ) {
            System.out.println("FAILED");
            e.printStackTrace();
        }                        
        
       return null;
    }
            
            
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
   
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        JBSave = new javax.swing.JButton();
        JBTest = new javax.swing.JButton();
        JBClose = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        JLHost = new javax.swing.JLabel();
        JLUser = new javax.swing.JLabel();
        JLPasswd = new javax.swing.JLabel();
        JLDatabase = new javax.swing.JLabel();
        JTHost = new HideContentWhenDisabled();
        JTUser = new HideContentWhenDisabled();
        JTDatabase = new HideContentWhenDisabled();
        JCType = new javax.swing.JComboBox();
        JLInstance = new javax.swing.JLabel();
        JTInstance = new HideContentWhenDisabled();
        JLPort = new javax.swing.JLabel();
        JTPort = new HideContentWhenDisabled();
        JBManage = new javax.swing.JButton();
        JTPasswd = new HideContentWhenDisabledPasswd();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        JBSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/button_ok.gif"))); // NOI18N
        JBSave.setText("Speichern");
        JBSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JBSaveActionPerformed(evt);
            }
        });

        JBTest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/wizard.gif"))); // NOI18N
        JBTest.setText("Test");
        JBTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JBTestActionPerformed(evt);
            }
        });

        JBClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/fileclose.gif"))); // NOI18N
        JBClose.setText("Schließen");
        JBClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JBCloseActionPerformed(evt);
            }
        });

        jLabel1.setText("Typ");

        JLHost.setText("Host");

        JLUser.setText("Benutzername");

        JLPasswd.setText("Passwort");

        JLDatabase.setText("Datenbank");

        JTHost.setText("jTextField2");

        JTUser.setText("jTextField3");

        JTDatabase.setText("jTextField5");

        JCType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        JLInstance.setText("Instanz");

        JTInstance.setText("jTextField1");

        JLPort.setText("Port");

        JTPort.setText("jTextField1");

        JBManage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/bookmark.png"))); // NOI18N
        JBManage.setText("Einrichten");
        JBManage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JBManageActionPerformed(evt);
            }
        });

        JTPasswd.setText("jPasswordField1");

        if (wizardAction != null) {
            JBClose.setEnabled(false);
            JBClose.setVisible(false);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(JBSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JBTest)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JBManage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                        .addComponent(JBClose))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(JLHost)
                            .addComponent(JLPort)
                            .addComponent(JLPasswd)
                            .addComponent(JLUser)
                            .addComponent(JLDatabase)
                            .addComponent(JLInstance))
                        .addGap(38, 38, 38)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(JTInstance, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                            .addComponent(JTDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                            .addComponent(JTUser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                            .addComponent(JTPort, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                            .addComponent(JTHost, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                            .addComponent(JCType, 0, 385, Short.MAX_VALUE)
                            .addComponent(JTPasswd, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(JCType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JLHost)
                    .addComponent(JTHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JLPort)
                    .addComponent(JTPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JLUser)
                    .addComponent(JTUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JLPasswd)
                    .addComponent(JTPasswd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JLDatabase)
                    .addComponent(JTDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JLInstance)
                    .addComponent(JTInstance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JBSave)
                    .addComponent(JBClose)
                    .addComponent(JBTest)
                    .addComponent(JBManage))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private String encrypt( StringBuffer buf )
{
    String s = buf.toString();

    if( s.isEmpty() )
        return s;

    if( StringUtils.isYes( root.getSetup().getLocalConfig(Setup.EncryptAllDBSettings, "false")) )
    {
        String encoded = EncryptedDBPasswd.encryptDBPassword(buf.toString(), root.getAppName());

        if( encoded == null )
            return s;
        else
            return encoded;
    }
    else
    {
        return buf.toString();
    }
}

private void JBSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBSaveActionPerformed

    Connection con = try_connect();
    
    if( con == null )
    {
        JOptionPane.showMessageDialog(null, 
            "Fehler beim Herstellen der Verbindung", 
            "Error",
            JOptionPane.OK_OPTION);
    }
    else
    {
        root.getSetup().setLocalConfig(Setup.DBDatabase, encrypt(DBDatabase));
        root.getSetup().setLocalConfig(Setup.DBHost, encrypt(DBHost));
        root.getSetup().setLocalConfig(Setup.DBUser, encrypt(DBUser));

        String passwd = DBPasswd.toString();

        String enc_passwd = EncryptedDBPasswd.encryptDBPassword(passwd, root.getAppName());

        if( enc_passwd == null )
        {
            JOptionPane.showMessageDialog(null,
            "Das Datenbankpasswort konnte nicht verschlüsselt werden!",
            "Error",
            JOptionPane.OK_OPTION);
            return;
        }

        root.getSetup().setLocalConfig(Setup.DBPasswd, enc_passwd );
        root.getSetup().setLocalConfig(Setup.DBPort, encrypt(DBPort));
        root.getSetup().setLocalConfig(Setup.DBInstance, encrypt(DBInstance));
        root.getSetup().setLocalConfig(Setup.DBType, DBType.toString());
        
        if( !root.loadDBConnectionFromSetup() )
        {
            JOptionPane.showMessageDialog(null, 
            "Fehler beim Herstellen der Verbindung vom Setup", 
            "Error",
            JOptionPane.OK_OPTION);
            return;
        }
        root.saveSetup();

        CheckConfigBase check_config = new CheckConfigBase(root);

        check_config.addCheck(new HaveDbConnection((root)));

        if( check_config.shouldPopUpWizard() )
        {
            if( setupDatabase(false) )
                root.loadDBConnectionFromSetup();
        }

        if( wizardAction != null )
        {
            wizardAction.applyAction(WizardAction.WIZARD_ACTION_NEXT, true);
        }
    }
    
}//GEN-LAST:event_JBSaveActionPerformed

private boolean setupDatabase( boolean success_message )
{
    Connection con = try_connect();

    if( con == null )
    {
        JOptionPane.showMessageDialog(null,
            "Fehler beim Herstellen der Verbindung",
            "Error",
            JOptionPane.OK_OPTION);
        return false;
    }
    else
    {
        Transaction t = null;

        try {
            con.close();

            ConnectionDefinition connparams = getDefinition();

			switch (connparams.getDBMSType()) {
			case DB_MSSQL:
				t = new MSSQLTransaction(connparams);
				break;
			case DB_MYSQL:
				t = new MySQLTransaction(connparams);
				break;
			case DB_ORACLE:
				t = new OracleTransaction(connparams);
				break;
			case DB_JAVADB:
				t = new DerbyTransaction(connparams);
				break;
            case DB_SQLITE:
                t = new SqLiteTransaction(connparams);
                break;
			default:
				logger.error("Unsupported DBMS!");
				return false;
			}

            bindtypeManager.setTransaction(t);


            if( bindtypeManager.can_support_db() == false )
            {
                JOptionPane.showMessageDialog(null,
                	StringUtils.autoLineBreak(
                        "Die Datenbank erfüllt nicht die notwendigen Voraussetzungen " +
                        "um dieses Programm ausführen zu können."),
                        "Fehler",
                        JOptionPane.OK_OPTION);
            }
            else
            {
                boolean successful_created = bindtypeManager.autocreate();

                if( !successful_created )
                {
                    t.rollback();
                } else {
                    t.commit();

                    if (success_message) {
                        JOptionPane.showMessageDialog(null,
                                StringUtils.autoLineBreak(
                                "Die Datenbank konnte erfolgreich eingerichtet werden."),
                                "Erfolg",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    return true;
                }
            }
        } catch (ClassNotFoundException ex) {
            logger.error(StringUtils.exceptionToString(ex));
        } catch (SQLException ex) {
            logger.error(StringUtils.exceptionToString(ex));
        } catch (MissingConnectionParamException ex) {
            logger.error(StringUtils.exceptionToString(ex));
        } catch (UnSupportedDatabaseException ex) {
            logger.error(StringUtils.exceptionToString(ex));
        } finally {

            if( t != null )
            {
                try {
                    t.close();
                } catch (SQLException ex) {
                     logger.error(StringUtils.exceptionToString(ex));
                }
            }
        }

        JOptionPane.showMessageDialog(null,
            "Fehler beim Einrichten der Datenbank",
            "Fehler",
            JOptionPane.OK_OPTION);
        return false;
    }
}

private void JBCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBCloseActionPerformed
     
        close();
}//GEN-LAST:event_JBCloseActionPerformed

private void JBTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBTestActionPerformed

    Connection con = try_connect();
    
    if( con == null )
    {
        JOptionPane.showMessageDialog(null, 
            "Fehler beim Herstellen der Verbindung", 
            "Error",
            JOptionPane.WARNING_MESSAGE);
    } else {
        
        JOptionPane.showMessageDialog(null, 
            "Die Verbindung konnte hergestellt werden.", 
            "Erfolg",
            JOptionPane.INFORMATION_MESSAGE);
    }
}//GEN-LAST:event_JBTestActionPerformed

private void JBManageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBManageActionPerformed
    
    setupDatabase(true);
}//GEN-LAST:event_JBManageActionPerformed


public void setBindtypeManager( DBBindtypeManager bindtypeManager )
{
    this.bindtypeManager = bindtypeManager;
    
    if( this.bindtypeManager != null )
        JBManage.setEnabled(true);
    else
        JBManage.setEnabled(false);
}


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton JBClose;
    private javax.swing.JButton JBManage;
    protected javax.swing.JButton JBSave;
    private javax.swing.JButton JBTest;
    private javax.swing.JComboBox JCType;
    private javax.swing.JLabel JLDatabase;
    private javax.swing.JLabel JLHost;
    private javax.swing.JLabel JLInstance;
    private javax.swing.JLabel JLPasswd;
    private javax.swing.JLabel JLPort;
    private javax.swing.JLabel JLUser;
    private javax.swing.JTextField JTDatabase;
    private javax.swing.JTextField JTHost;
    private javax.swing.JTextField JTInstance;
    private javax.swing.JPasswordField JTPasswd;
    private javax.swing.JTextField JTPort;
    private javax.swing.JTextField JTUser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables

}
