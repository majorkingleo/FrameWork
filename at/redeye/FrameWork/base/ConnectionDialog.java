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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import at.redeye.FrameWork.base.dbmanager.DBBindtypeManager;
import at.redeye.FrameWork.base.transaction.DerbyTransaction;
import at.redeye.FrameWork.base.transaction.MSSQLTransaction;
import at.redeye.FrameWork.base.transaction.MySQLTransaction;
import at.redeye.FrameWork.base.transaction.OracleTransaction;
import at.redeye.FrameWork.base.transaction.SqLiteTransaction;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.SqlDBInterface.SqlDBConnection.MOMMDbConnectionInterface;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.ConnectionDefinition;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MOMMDBConnector;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MOMMSupportedDBMSTypes;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MissingConnectionParamException;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.UnSupportedDatabaseException;

/**
 *
 * @author  martin
 */
public class ConnectionDialog extends BaseDialog {
    
    private static final long serialVersionUID = 1L;
    
    StringBuffer DBHost = new StringBuffer();
    MOMMSupportedDBMSTypes DBType;
    StringBuffer DBUser = new StringBuffer();
    StringBuffer DBPasswd = new StringBuffer();
    StringBuffer DBDatabase = new StringBuffer();
    StringBuffer DBInstance = new StringBuffer();
    StringBuffer DBPort = new StringBuffer();
    DBBindtypeManager bindtypeManager = null;
    
    /** Creates new form ConnectionDialog */
    public ConnectionDialog( Root root ) {
        super( root , "Datenbankverbindung");
        
        initComponents();
                
        DBHost.append( root.getSetup().getLocalConfig(Setup.DBHost, "localhost" ) );
        DBUser.append( root.getSetup().getLocalConfig(Setup.DBUser, "" ) );
        DBPasswd.append( root.getSetup().getLocalConfig(Setup.DBPasswd, "" ) );
        DBDatabase.append( root.getSetup().getLocalConfig(Setup.DBDatabase, "" ) );
        DBInstance.append( root.getSetup().getLocalConfig(Setup.DBInstance, "" ) );
        DBPort.append( root.getSetup().getLocalConfig(Setup.DBPort, "" ) );
        DBType = MOMMSupportedDBMSTypes.valueOf(root.getSetup().getLocalConfig(Setup.DBType, MOMMSupportedDBMSTypes.DB_MYSQL.toString() ) );
        
        bindVar(JTHost, DBHost);
        bindVar(JCType, DBType);
        bindVar(JTUser, DBUser);
        bindVar(JTPasswd, DBPasswd);
        bindVar(JTDatabase, DBDatabase);
        bindVar(JTPort, DBPort);
        bindVar(JTInstance, DBInstance);
        
        setBindtypeManager( root.getBindtypeManager() );
        
        JCType.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                                                
                JTDatabase.setVisible(true);                                
                JTInstance.setEditable(false);                
                JTHost.setEditable(true);                
                JTPort.setEditable(true);                
                JTUser.setEditable(true);                
                JTPasswd.setEditable(true);
                    
                if( JCType.getSelectedItem() == MOMMSupportedDBMSTypes.DB_ORACLE )
                {                    
                    JTDatabase.setEditable(false);                        
                    JTInstance.setEditable(true);
                    
                } else if( JCType.getSelectedItem() == MOMMSupportedDBMSTypes.DB_SQLITE ) {
                                        
                    JTDatabase.setEditable(true);                    
                    JTInstance.setEditable(false);                    
                    JTHost.setEditable(false);                    
                    JTPort.setEditable(false);                    
                    JTUser.setEditable(false);                    
                    JTPasswd.setEditable(false);
                }
                                
            }
            
        });
        
        var_to_gui();
    }

    void bindVar( JComboBox box, MOMMSupportedDBMSTypes  t )
    {
        box.removeAllItems();                
        
        MOMMSupportedDBMSTypes tt[] = MOMMSupportedDBMSTypes.values();
        
        for( MOMMSupportedDBMSTypes t2 : tt )
        {
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
                DBType = (MOMMSupportedDBMSTypes)JCType.getSelectedItem();
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
        
        bind_vars.pairs.add(new TypePair() );                
    }
    
    ConnectionDefinition getDefinition()
    {
        String instance;
            
        if( DBType == MOMMSupportedDBMSTypes.DB_ORACLE )
            instance = DBInstance.toString();
        else
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
            
            MOMMDbConnectionInterface connint = new MOMMDBConnector(connparams);

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
        JTHost = new javax.swing.JTextField();
        JTUser = new javax.swing.JTextField();
        JTPasswd = new javax.swing.JTextField();
        JTDatabase = new javax.swing.JTextField();
        JCType = new javax.swing.JComboBox();
        JLInstance = new javax.swing.JLabel();
        JTInstance = new javax.swing.JTextField();
        JLPort = new javax.swing.JLabel();
        JTPort = new javax.swing.JTextField();
        JBManage = new javax.swing.JButton();

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

        JTPasswd.setText("jTextField4");

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 81, Short.MAX_VALUE)
                        .addComponent(JBClose))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
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
                            .addComponent(JTInstance, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                            .addComponent(JTDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                            .addComponent(JTUser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                            .addComponent(JTPort, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                            .addComponent(JTHost, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                            .addComponent(JCType, 0, 416, Short.MAX_VALUE)
                            .addComponent(JTPasswd, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE))))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
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
        root.getSetup().setLocalConfig(Setup.DBDatabase, DBDatabase.toString());
        root.getSetup().setLocalConfig(Setup.DBHost, DBHost.toString());
        root.getSetup().setLocalConfig(Setup.DBUser, DBUser.toString());
        root.getSetup().setLocalConfig(Setup.DBPasswd, DBPasswd.toString());
        root.getSetup().setLocalConfig(Setup.DBPort, DBPort.toString());
        root.getSetup().setLocalConfig(Setup.DBInstance, DBInstance.toString());
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
    }
    
}//GEN-LAST:event_JBSaveActionPerformed

private void JBCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBCloseActionPerformed
     
        close();
}//GEN-LAST:event_JBCloseActionPerformed

private void JBTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBTestActionPerformed
// TODO add your handling code here:
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
// TODO add your handling code here:
    
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
				Logger.getLogger(DBConnection.class.getName()).log(
						Level.SEVERE, "Unsupported DBMS!");
				return;
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
                boolean success = bindtypeManager.autocreate();

                if( !success )
                {
                    t.rollback();                    
                } else {                
                    t.commit();                    

                    JOptionPane.showMessageDialog(null,
                            StringUtils.autoLineBreak(
                            "Die Datenbank konnte erfolgreich eingerichtet werden."),
                            "Erfolg",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnectionDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MissingConnectionParamException ex) {
            Logger.getLogger(ConnectionDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnSupportedDatabaseException ex) {
            Logger.getLogger(ConnectionDialog.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            
            if( t != null )
            {
                try {
                    t.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ConnectionDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        JOptionPane.showMessageDialog(null, 
            "Fehler beim Einrichten der Datenbank", 
            "Fehler",
            JOptionPane.OK_OPTION);            
    }
    
}//GEN-LAST:event_JBManageActionPerformed


public void setBindtypeManager( DBBindtypeManager bindtypeManager )
{
    this.bindtypeManager = bindtypeManager;
    
    if( this.bindtypeManager != null )
        JBManage.setVisible(true);
    else
        JBManage.setVisible(false);
}


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton JBClose;
    private javax.swing.JButton JBManage;
    private javax.swing.JButton JBSave;
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
    private javax.swing.JTextField JTPasswd;
    private javax.swing.JTextField JTPort;
    private javax.swing.JTextField JTUser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables

}
