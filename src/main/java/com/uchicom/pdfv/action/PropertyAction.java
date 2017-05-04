// (c) 2014 uchicom
package com.uchicom.pdfv.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.uchicom.pdfv.Constants;
import com.uchicom.pdfv.PropertyDialog;
import com.uchicom.pdfv.ViewFrame;
import com.uchicom.pdfv.util.ResourceUtil;

/**
 *
 * @author Shigeki Uchiyama
 *
 */
public class PropertyAction extends AbstractAction {
	/** シリアルID */
	private static final long serialVersionUID = 1L;
	private ViewFrame component;

	public PropertyAction(ViewFrame component) {
		putValue(NAME, ResourceUtil.getString(Constants.ACTION_NAME_PROPERTY));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK));
		this.component = component;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// PDFドキュメントをロード
		if (component.getCurrentFile() != null) {
			PDDocument document = null;
			try {
				document = PDDocument.load(component.getCurrentFile());
//				PDDocumentInformation di = document.getDocumentInformation();
				PropertyDialog dialog = new PropertyDialog(component, document);

				dialog.pack();
				dialog.setVisible(true);

			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(component, e1.getMessage());
			} finally {
				if (document != null) {
					try {
						document.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						document = null;
					}
				}
			}
		}
	}

}
