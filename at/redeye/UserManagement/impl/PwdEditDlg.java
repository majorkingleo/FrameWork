/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PwdEditDlg.java
 *
 * Created on 16.03.2009, 19:54:45
 */

package at.redeye.UserManagement.impl;

import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.utilities.MD5Calc;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.FrameWork.widgets.helpwindow.HelpWin;
import at.redeye.UserManagement.bindtypes.DBPb;

import javax.swing.JOptionPane;

/**
 * 
 * @author Sabrina und Mario
 */
public class PwdEditDlg extends BaseDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final static String menuTitle = "Passwort ändern";

	private DBPb pb_;

        private static String MESSAGE_TITLE;
        private static String MESSAGE_WRONG_CURRENT_PASSWD;
        private static String MESSAGE_PASSWD_TOSHORT;
        private static String MESSAGE_WRONG_SECOND_PASSWD;

	/** Creates new form PwdEditDlg */
	public PwdEditDlg(final Root root, DBPb pb) {
		super(root, menuTitle);
		pb_ = pb;
		initComponents();

            registerHelpWin(new Runnable() {

                public void run() {

                    new HelpWin(root, "/at/redeye/UserManagement/resources/Help/",
                            "PwdEditDlg").setVisible(true);
                }
            });

            if( MESSAGE_TITLE == null )
            {
                MESSAGE_TITLE = MlM( "Fehler" );
                MESSAGE_WRONG_CURRENT_PASSWD = MlM( "Das eingegebene, aktuelle Passwort ist nicht korrekt!" );
                MESSAGE_PASSWD_TOSHORT = MlM( "Das neue Passwort muss mindestens fünf Zeichen enthalten!" );
                MESSAGE_WRONG_SECOND_PASSWD = MlM( "Das eingegebene, neue Passwort "
							+ "unterscheidet sich von der Wiederholung!" );
            }
	}

    //<editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        buttonHelp = new javax.swing.JButton();
        buttonOK = new javax.swing.JButton();
        buttonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOKActionPerformed();
            }
        });
        buttonCancel = new javax.swing.JButton();
        labelOldPwd = new javax.swing.JLabel();
        labelNewPwd = new javax.swing.JLabel();
        labelCtlPwd = new javax.swing.JLabel();
        fieldOldPwd = new javax.swing.JPasswordField();
        fieldNewPwd = new javax.swing.JPasswordField();
        fieldCtlPwd = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("    Passwort ändern");

        buttonHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/help.png"))); // NOI18N
        buttonHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonHelpActionPerformed(evt);
            }
        });

        buttonOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/button_ok.gif"))); // NOI18N
        buttonOK.setText("Speichern");

        buttonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/fileclose.gif"))); // NOI18N
        buttonCancel.setText("Schließen");
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        labelOldPwd.setFont(new java.awt.Font("Tahoma", 0, 14));
        labelOldPwd.setText("Altes Passwort");

        labelNewPwd.setFont(new java.awt.Font("Tahoma", 0, 14));
        labelNewPwd.setText("Neues Passwort");

        labelCtlPwd.setFont(new java.awt.Font("Tahoma", 0, 14));
        labelCtlPwd.setText("Wiederholung");

        fieldOldPwd.setFont(new java.awt.Font("Tahoma", 0, 14));
        fieldOldPwd.addKeyListener(new ExtKeyListener(this));

        fieldNewPwd.setFont(new java.awt.Font("Tahoma", 0, 14));
        fieldNewPwd.addKeyListener(new ExtKeyListener(this));

        fieldCtlPwd.setFont(new java.awt.Font("Tahoma", 0, 14));
        fieldCtlPwd.addKeyListener(new ExtKeyListener(this));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonHelp, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelOldPwd, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                            .addComponent(labelNewPwd, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                            .addComponent(labelCtlPwd, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                            .addComponent(buttonOK))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fieldCtlPwd, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addComponent(fieldOldPwd, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addComponent(fieldNewPwd, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addComponent(buttonCancel, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonHelp)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelOldPwd)
                    .addComponent(fieldOldPwd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fieldNewPwd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelNewPwd))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCtlPwd)
                    .addComponent(fieldCtlPwd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCancel)
                    .addComponent(buttonOK))
                .addContainerGap())
        );

        pack();
    }//</editor-fold>//GEN-END:initComponents

	private void buttonHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonHelpActionPerformed
		

                callHelpWin();
	}//GEN-LAST:event_buttonHelpActionPerformed

	private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
		// TODO add your handling code here:
		close();
	}//GEN-LAST:event_buttonCancelActionPerformed

	protected void buttonOKActionPerformed() {

		MD5Calc md5 = new MD5Calc("MD5");
		String encPwd = md5.calcChecksum(new String(fieldOldPwd.getPassword()));
		if (!encPwd.equals(pb_.pwd.toString())) {
			JOptionPane
					.showMessageDialog(
							this,
							StringUtils
									.autoLineBreak(MESSAGE_WRONG_CURRENT_PASSWD),
							MESSAGE_TITLE, JOptionPane.OK_OPTION);
			return;
		}

		String newPwd = new String(fieldNewPwd.getPassword());
		if (newPwd == null || newPwd.length() < 5) {
			JOptionPane
					.showMessageDialog(
							this,
							StringUtils
									.autoLineBreak(MESSAGE_PASSWD_TOSHORT),
							MESSAGE_TITLE, JOptionPane.OK_OPTION);
			return;
		}

		encPwd = md5.calcChecksum(newPwd);
		String encPwdCtl = md5.calcChecksum(new String(fieldCtlPwd
				.getPassword()));
		if (!encPwd.equals(encPwdCtl)) {
			JOptionPane.showMessageDialog(this, StringUtils
					.autoLineBreak(MESSAGE_WRONG_SECOND_PASSWD),
					MESSAGE_TITLE, JOptionPane.OK_OPTION);
			return;
		}
		pb_.pwd.loadFromCopy(encPwd);
		pb_.hist.setAeHist(pb_.getUserName());

		new AutoMBox(PwdEditDlg.class.getSimpleName()) {
			public void do_stuff() throws Exception {

				getTransaction().updateValues(pb_);
				getTransaction().commit();
			}
		};
		close();
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonHelp;
    private javax.swing.JButton buttonOK;
    private javax.swing.JPasswordField fieldCtlPwd;
    private javax.swing.JPasswordField fieldNewPwd;
    private javax.swing.JPasswordField fieldOldPwd;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel labelCtlPwd;
    private javax.swing.JLabel labelNewPwd;
    private javax.swing.JLabel labelOldPwd;
    // End of variables declaration//GEN-END:variables

}
