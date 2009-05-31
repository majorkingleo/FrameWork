package at.redeye.UserManagement.impl;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import at.redeye.FrameWork.base.BaseDialog;

public class ExtKeyListener implements KeyListener {

	private BaseDialog	dlg;
    
	
	
	public ExtKeyListener(LoginDlg dlg) {
		super();
		this.dlg = dlg;
	}

    public ExtKeyListener(PwdEditDlg dlg) {
		super();
		this.dlg = dlg;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (dlg instanceof LoginDlg) {
                LoginDlg mydlg = (LoginDlg)dlg;
                mydlg.buttonOKActionPerformed();
            } else if (dlg instanceof PwdEditDlg) {
                PwdEditDlg pwddlg = (PwdEditDlg)dlg;
                pwddlg.buttonOKActionPerformed();

            }
			
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			dlg.close();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}
