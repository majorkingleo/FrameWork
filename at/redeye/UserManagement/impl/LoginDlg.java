/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LoginDlg.java
 *
 * Created on 21.12.2008, 19:09:57
 */
package at.redeye.UserManagement.impl;

import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.JOptionPane;


import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.Root;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.UserManagement.UserManagementInterface;
import at.redeye.UserManagement.bindtypes.DBPb;

/**
 * 
 * @author Mario Mattl
 */
public class LoginDlg extends BaseDialog {

	private static final long serialVersionUID = 1L;
	/** Creates new form LoginDlg */
	private static final String dlgName = "Login Dialog";
	private UserManagementInterface um;

	/** Creates new form LoginDlg */
	public LoginDlg(Root root, UserManagementInterface um) {
		super(root, dlgName);
		this.um = um;
		initComponents();
	}

    @Override
    public boolean openWithLastWidthAndHeight()
    {
        // ansonsten wird das Loginfenster immer größer. Keine Ahnung warum.
        return false;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        fieldUser = new javax.swing.JTextField();
        fieldUser.addKeyListener(new ExtKeyListener(this));
        jLabel2 = new javax.swing.JLabel();
        fieldPwd = new javax.swing.JPasswordField();
        fieldPwd.addKeyListener(new ExtKeyListener(this));
        buttonOK = new javax.swing.JButton();
        buttonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOKActionPerformed();
            }
        });
        buttonCancel = new javax.swing.JButton();
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {

                close();
                root.appExit();
            }
        });
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Login");

        fieldUser.setFont(new java.awt.Font("Tahoma", 0, 14));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Passwort");

        fieldPwd.setFont(new java.awt.Font("Tahoma", 0, 14));

        buttonOK.setFont(new java.awt.Font("Tahoma", 0, 14));
        buttonOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/button_ok.gif"))); // NOI18N
        buttonOK.setText("OK");
        buttonOK.setActionCommand("buttonOK");
        buttonOK.setPreferredSize(new java.awt.Dimension(120, 32));

        buttonCancel.setFont(new java.awt.Font("Tahoma", 0, 14));
        buttonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/fileclose.gif"))); // NOI18N
        buttonCancel.setText("Schließen");
        buttonCancel.setActionCommand("buttonCancel");
        buttonCancel.setPreferredSize(new java.awt.Dimension(120, 31));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource(um.getLogo())));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(buttonOK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(97, 97, 97)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(fieldUser)
                                        .addComponent(fieldPwd, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)))))
                        .addContainerGap(40, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(90, 90, 90)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(fieldUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(fieldPwd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonOK, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	protected void buttonOKActionPerformed() {
		try {
			DBPb pb = um.checkUserData(fieldUser.getText(), new String(fieldPwd
					.getPassword()), false,this);
			if (pb != null) {
				root.setAktivUser(pb);
				um.updateListeners();
				logger.info("User " + root.getUserName() + " [Level "
						+ root.getUserPermissionLevel()
						+ "] successfully signed on!");
			} else {
				HashMap<String, Object> map = new HashMap<String, Object>();
				pb = new DBPb();
				map.put("name", "Initiales Setup");
				map.put("pwd", "  ---  ");
				map.put("login", "admin");
				map.put("plevel",
						UserManagementInterface.UM_PERMISSIONLEVEL_ADMIN);
				map.put("locked", UserManagementInterface.UM_ACCOUNT_LOCKED);
				pb.consume(map);
				root.setAktivUser(pb);

			}
			close();
		} catch (InvalidLoginException e) {
			logger.error("Falsche Benutzerdaten!");
			JOptionPane.showMessageDialog(this, "Falsche Benutzerdaten!",
					"User Management", JOptionPane.ERROR_MESSAGE);
			return;
		} catch (SQLException e) {
			logger.error("Fehler beim Zugriff auf die Datenbank!\n"
					+ e.getMessage());
			JOptionPane
					.showMessageDialog(this,
							"Fehler beim Zugriff auf die Datenbank!\n"
									+ e.getMessage(), "User Management",
							JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return;
		} catch (UnsupportedDBDataTypeException e) {
			logger.error("Fehler beim Zugriff auf die Datenbank!");
			JOptionPane.showMessageDialog(this,
					"Fehler beim Zugriff auf die Datenbank!",
					"User Management", JOptionPane.ERROR_MESSAGE);
			return;
		} catch (TableBindingNotRegisteredException e) {
			logger.error("Tabellen wurden nicht registiert!");
			JOptionPane.showMessageDialog(this,
					"Tabellen wurden nicht registiert!", "User Management",
					JOptionPane.ERROR_MESSAGE);
			return;
		} catch (WrongBindFileFormatException e) {
			logger.error("Falscher Bindedeskriptor!");
			JOptionPane.showMessageDialog(this, "Falscher Bindedeskriptor!",
					"User Management", JOptionPane.ERROR_MESSAGE);
			return;
		} catch (CloneNotSupportedException e) {
			logger.error("Allgemeiner Fehler beim Überprüfen der Daten!");
			JOptionPane.showMessageDialog(this,
					"Allgemeiner Fehler beim Überprüfen der Daten!",
					"User Management", JOptionPane.ERROR_MESSAGE);
			return;
		} catch (UserLockedException e) {
			logger.error("Der Benutzer ist gesperrt!");
			JOptionPane.showMessageDialog(this, "Der Benutzer ist gesperrt!",
					"User Management", JOptionPane.ERROR_MESSAGE);
			return;
		}

	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonOK;
    private javax.swing.JPasswordField fieldPwd;
    private javax.swing.JTextField fieldUser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}
