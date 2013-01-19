/*
 * AdminDlg.java
 *
 * Created on 2. Januar 2009, 16:50
 */
package at.redeye.UserManagement.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.tablemanipulator.TableManipulator;
import at.redeye.FrameWork.base.wizards.WizardClientActionInterface;
import at.redeye.FrameWork.utilities.MD5Calc;
import at.redeye.FrameWork.widgets.helpwindow.HelpWin;
import at.redeye.SqlDBInterface.SqlDBIO.TypeRegistrationInterface;
import at.redeye.SqlDBInterface.SqlDBIO.impl.ColumnAttribute;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.UserManagement.UserManagementInterface;
import at.redeye.UserManagement.bindtypes.DBPb;

/**
 * 
 * @author Mario Mattl
 */
public class AdminDlg extends BaseDialog {

	private TableManipulator tm;
	private static final long serialVersionUID = 1L;
	private final static String menuTitle = "Administration Benutzerdaten";
	private List<DBStrukt> pbEntries;
	private List<DBStrukt> oldPbs;
	private UserManagementInterface um;
	private WizardClientActionInterface wizardAction = null;
	public static final String UM_ID_SEQ = "UM_ID_SEQ";

	/**
	 * Creates new form AdminDlg
	 * 
	 * @param root
	 *            Root class
	 */

	public AdminDlg(Root root, WizardClientActionInterface wizardAction) {
		super(root, menuTitle);
		this.wizardAction = wizardAction;
		initComponents();

		initCommon();
	}

	public AdminDlg(Root root) {
		super(root, menuTitle);
		initComponents();

		initCommon();
	}

	private void initCommon() {
		DBPb pb = new DBPb();
		oldPbs = new Vector<DBStrukt>();
		um = new UserDataHandling(root);
		tm = new TableManipulator(root, jTable1, pb);

		tm.hide(pb.id);
		tm.hide(pb.hist.an_user);
		tm.hide(pb.hist.an_zeit);
		tm.hide(pb.hist.lo_user);
		tm.hide(pb.hist.lo_zeit);

		if (root.getUserPermissionLevel() == UserManagementInterface.UM_PERMISSIONLEVEL_ADMIN) {
			tm.setEditable(pb.locked);
			tm.setEditable(pb.surname);
			tm.setEditable(pb.title);
			tm.setEditable(pb.plevel);
			tm.setEditable(pb.pwd);
			tm.setEditable(pb.login);
			tm.setEditable(pb.name);
		} else {
			tm.hide(pb.pwd);
			tm.hide(pb.locked);
		}

		tm.prepareTable();
		feed_table();
		tm.autoResize();

		registerHelpWin(new Runnable() {

			public void run() {
				invokeDialog(new HelpWin(root,
						"/at/redeye/UserManagement/resources/Help/", "AdminDlg"));
			}
		});
	}

	public void feed_table() {

		try {
			tm.clear();
			oldPbs.clear();
			if (root.getUserPermissionLevel() == UserManagementInterface.UM_PERMISSIONLEVEL_ADMIN) {
				pbEntries = um.getAllUserData();
				for (int index = 0; index < pbEntries.size(); index++) {
					DBStrukt strukt = pbEntries.get(index);
					oldPbs.add(strukt.getCopy());
				}
			} else {
				pbEntries = new Vector<DBStrukt>();
				DBPb pb = new DBPb();
				pb.id.loadFromCopy(root.getUserId());
				pbEntries.add(um.getUserData(pb));
			}
			tm.addAll(pbEntries);

		} catch (SQLException ex) {
			logger.error(ex);
		} catch (TableBindingNotRegisteredException ex) {
			logger.error(ex);
		} catch (UnsupportedDBDataTypeException ex) {
			logger.error(ex);
		} catch (WrongBindFileFormatException ex) {
			logger.error(ex);
		} catch (CloneNotSupportedException ex) {
			logger.error(ex);
		} catch (IOException ex) {
			logger.error(ex);
		}
	}

	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		jTable1 = new javax.swing.JTable();
		jLabel1 = new javax.swing.JLabel();
		buttonOK = new javax.swing.JButton();
		buttonNewUser = new javax.swing.JButton();
		buttonCancel = new javax.swing.JButton();
		buttonRead = new javax.swing.JButton();
		jBHelp = new javax.swing.JButton();
		buttonChangePwd = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		jTable1.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null, null },
						{ null, null, null, null }, { null, null, null, null },
						{ null, null, null, null } }, new String[] { "Title 1",
						"Title 2", "Title 3", "Title 4" }));
		jScrollPane1.setViewportView(jTable1);

		jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24));
		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel1.setText("Administration Benutzerstammdaten");

		buttonOK.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/button_ok.gif"))); // NOI18N
		buttonOK.setText("Speichern");
		buttonOK.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				buttonOKActionPerformed(evt);
			}
		});
		if (root.getUserPermissionLevel() != UserManagementInterface.UM_PERMISSIONLEVEL_ADMIN) {
			buttonOK.setEnabled(false);
		}

		buttonNewUser.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/bookmark.png"))); // NOI18N
		buttonNewUser.setText("Neu");
		buttonNewUser.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				buttonNewUserActionPerformed(evt);
			}
		});
		if (root.getUserPermissionLevel() != UserManagementInterface.UM_PERMISSIONLEVEL_ADMIN) {
			buttonNewUser.setEnabled(false);
		}

		buttonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/fileclose.gif"))); // NOI18N
		buttonCancel.setText("Schließen");
		buttonCancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				buttonCancelActionPerformed(evt);
			}
		});

		buttonRead.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/reload.png"))); // NOI18N
		buttonRead.setText("Aktualisieren");
		buttonRead.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				buttonReadActionPerformed(evt);
			}
		});

		jBHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/help.png"))); // NOI18N
		jBHelp.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBHelpActionPerformed(evt);
			}
		});

		buttonChangePwd
				.setIcon(new javax.swing.ImageIcon(getClass().getResource(
						"/at/redeye/FrameWork/base/resources/icons/unlock.png"))); // NOI18N
		buttonChangePwd.setText("Passwort ändern");
		buttonChangePwd.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				buttonChangePwdActionPerformed(evt);
			}
		});

		if (wizardAction != null) {
			buttonCancel.setEnabled(false);
			buttonCancel.setVisible(false);
		}
		if (wizardAction != null) {
			jBHelp.setVisible(false);
			jBHelp.setEnabled(false);
		}
		if (root.getUserPermissionLevel() == UserManagementInterface.UM_PERMISSIONLEVEL_ADMIN) {
			buttonChangePwd.setEnabled(false);
		}

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addContainerGap()
																.addComponent(
																		jScrollPane1,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		845,
																		Short.MAX_VALUE))
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addComponent(
																		jLabel1,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		807,
																		Short.MAX_VALUE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(
																		jBHelp,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		32,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(
														layout.createSequentialGroup()
																.addContainerGap()
																.addComponent(
																		buttonOK)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		buttonNewUser)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		buttonChangePwd)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																		143,
																		Short.MAX_VALUE)
																.addComponent(
																		buttonRead)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		buttonCancel)))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jBHelp)
												.addComponent(
														jLabel1,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														55,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(15, 15, 15)
								.addComponent(jScrollPane1,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										317, Short.MAX_VALUE)
								.addGap(18, 18, 18)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(buttonOK)
												.addComponent(buttonNewUser)
												.addComponent(buttonCancel)
												.addComponent(buttonRead)
												.addComponent(buttonChangePwd))
								.addContainerGap()));

		pack();
	}//</editor-fold>//GEN-END:initComponents

	private void buttonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOKActionPerformed

		Set<Integer> editedRows = tm.getEditedRows();

		TypeRegistrationInterface regi = getTransaction().getTypeRegistration();
		MD5Calc md5 = new MD5Calc("MD5");

		for (Integer i : editedRows) {

			DBPb pb = (DBPb) pbEntries.get(i);

			if (!regi.getAllRegisteredTables().containsKey(pb.getName())) {
				HashMap<String, ColumnAttribute> colls = pb.getHashMap();
				HashMap<String, HashMap<String, ColumnAttribute>> table = new HashMap<String, HashMap<String, ColumnAttribute>>();

				table.put(pb.getName(), colls);

				try {
					regi.registerTableBindings(table);

				} catch (UnsupportedDBDataTypeException e) {

					logger.error(e);
				} catch (WrongBindFileFormatException e) {

					logger.error(e);
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Edited row id: " + pb.id + " [" + pb.name + " "
						+ pb.surname + "]");
			}

			DBPb oldpb = (DBPb) oldPbs.get(i);

			logger.error(oldpb.pwd + " / " + pb.pwd);
			if (oldpb != null
					&& !pb.pwd.toString().equals(oldpb.pwd.toString())) {

				String encPwd = md5.calcChecksum(pb.pwd.toString());
				if (logger.isDebugEnabled()) {
					logger.debug("PWD changed [ " + encPwd + " vs. "
							+ oldpb.pwd.toString() + " -> start encryption");
				}
				pb.pwd.loadFromCopy(encPwd);
			}

			pb.hist.setAeHist(root.getUserName());

			try {
				getTransaction().updateValues(pb);
				getTransaction().commit();
			} catch (SQLException sqlex) {
				logger.error(getTransaction().getSql());
				logger.error(sqlex);
			} catch (UnsupportedDBDataTypeException ex) {
				logger.error(ex);
			} catch (WrongBindFileFormatException ex) {
				logger.error(ex);
			} catch (TableBindingNotRegisteredException ex) {
				logger.error(ex);
			} catch (IOException ex) {
				logger.error(ex);
			}

		}
		feed_table();
	}//GEN-LAST:event_buttonOKActionPerformed

	private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
		// TODO add your handling code here:
		close();
	}//GEN-LAST:event_buttonCancelActionPerformed

	private void buttonReadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonReadActionPerformed
		// TODO add your handling code here:
		feed_table();
	}//GEN-LAST:event_buttonReadActionPerformed

	private void jBHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBHelpActionPerformed

		callHelpWin();

	}//GEN-LAST:event_jBHelpActionPerformed

	private void buttonChangePwdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChangePwdActionPerformed

		invokeDialog(new PwdEditDlg(root, (DBPb) pbEntries.get(0)));

	}//GEN-LAST:event_buttonChangePwdActionPerformed

	private void buttonNewUserActionPerformed(java.awt.event.ActionEvent evt) {

		try {
			int nextVal = getNewSequenceValue(UM_ID_SEQ);

			DBPb newPb = new DBPb();

			newPb.id.loadFromCopy(new Integer(nextVal));
			newPb.plevel.loadFromCopy(new Integer(
					UserManagementInterface.UM_PERMISSIONLEVEL_NORMAL));
			newPb.locked.loadFromCopy(new Integer(
					UserManagementInterface.UM_ACCOUNT_LOCKED));

			newPb.hist.setAnHist(root.getUserName());

			getTransaction().insertValues(newPb);

			tm.setEditable(newPb.name);
			tm.setEditable(newPb.login);
			tm.add(newPb);

			if (pbEntries == null)
				pbEntries = new Vector<DBStrukt>();

			pbEntries.add(newPb);
			oldPbs.add(newPb.getCopy());

		} catch (Exception ex) {
			logger.error("Exception caught", ex);
		}

	}

	@Override
	public boolean canClose() {
		int ret = checkSave(tm);

		if (ret == 1) {
			buttonOKActionPerformed(null);
		} else if (ret == -1) {
			return false;
		}
		return true;
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton buttonCancel;
	private javax.swing.JButton buttonChangePwd;
	private javax.swing.JButton buttonNewUser;
	private javax.swing.JButton buttonOK;
	private javax.swing.JButton buttonRead;
	private javax.swing.JButton jBHelp;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTable jTable1;
	// End of variables declaration//GEN-END:variables
}
