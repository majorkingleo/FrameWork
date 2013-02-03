/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ExportDialog.java
 *
 * Created on 14.06.2010, 21:33:25
 */

package at.redeye.Setup.dbexport;

import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialogDialog;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.utilities.StringUtils;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author martin
 */
public class ImportDialog extends BaseDialogDialog implements ProgressListener {

    /** Creates new form ExportDialog */

    Thread importer;
    boolean do_abort = false;
    DatabaseImport db_import;
    boolean success = false;
    Runnable finnished_listener = null;

    public ImportDialog( Root root ) {
        super( root, "Datenbankimport" );
        initComponents();

        setBaseLanguage("de");

        doImport();
    }

    public void setFinishedListener( Runnable listener )
    {
        finnished_listener = listener;
    }

    private void doImport()
    {
        JFileChooser fc = new JFileChooser();

        int ret = fc.showOpenDialog(this);

        if( ret == JFileChooser.CANCEL_OPTION ||
            ret == JFileChooser.ERROR_OPTION )
        {
            close_later();
            return;
        }

        File file = fc.getSelectedFile();

        if( file == null )
        {
            close_later();
            return;
        }

        db_import = new DatabaseImport(root, file.getPath() );

        db_import.setProgressListener(this);

        final BaseDialogDialog dialog = this;

        importer = new Thread() {

            @Override
            public void run()
            {
                AutoMBox mb = new AutoMBox(ImportDialog.class.getName())
                {

                    @Override
                    public void do_stuff() throws Exception {

                        db_import.doImport();

                        db_import.close();

                        success = true;
                        JOptionPane.showMessageDialog(dialog, MlM("Die Datenbank wurde erfolgreich importiert!"));
                    }
                };

                close();

                if( finnished_listener != null )
                    finnished_listener.run();
            }

        };

        importer.start();       
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jProgress = new javax.swing.JProgressBar();
        jButtonCancel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLTable = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jButtonCancel.setText("Abbrechen");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jLabel1.setText("Datenbankimport:");

        jLTable.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLTable, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                    .addComponent(jProgress, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonCancel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLTable, javax.swing.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonCancel)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed

        if( importer != null )
        {
            do_abort = true;

            try {
                importer.join(10000);
                logger.info("waited 10 seconds, thread didn't died, killing it.");

                db_import.close();
                importer.join();
                
                importer = null;
                db_import = null;
            } catch( InterruptedException ex ) {
                logger.error(ex,ex);
            }
        }

    }//GEN-LAST:event_jButtonCancelActionPerformed
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JLabel jLTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JProgressBar jProgress;
    // End of variables declaration//GEN-END:variables

    @Override
    synchronized public void setStage(String stage) {
        jLTable.setText(stage);
    }

    @Override
    synchronized public void setOverallCounter(int count) {
        jProgress.setMaximum(count);
    }

    @Override
    synchronized public void setCounter(int val) {
        jProgress.setValue(val);
    }

    @Override
    synchronized public boolean canContinue() {
        return !do_abort;
    }

    private void close_later() {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                close();
                
                if( finnished_listener != null )
                    finnished_listener.run();
            }
        });
    }

}
