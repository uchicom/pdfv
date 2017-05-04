// (c) 2014 uchicom
package com.uchicom.pdfv.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.uchicom.pdfv.Constants;
import com.uchicom.pdfv.ViewFrame;
import com.uchicom.pdfv.util.ResourceUtil;

/**
 *
 * @author Shigeki Uchiyama
 *
 */
public class SaveAction extends AbstractAction {
	/** シリアルID */
	private static final long serialVersionUID = 1L;
	private ViewFrame component;
	public SaveAction(ViewFrame component) {
		putValue(NAME, ResourceUtil.getString(Constants.ACTION_NAME_SAVE));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
//		putValue(ACTION_COMMAND_KEY, "Ctrl + S");
		this.component = component;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(null, "OK?");
	}

}
