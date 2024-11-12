// (c) 2014 uchicom
package com.uchicom.pdfv.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import com.uchicom.pdfv.Constants;
import com.uchicom.pdfv.ViewFrame;
import com.uchicom.pdfv.util.ResourceUtil;

/**
 *
 * @author Shigeki Uchiyama
 *
 */
public class FirstAction extends AbstractAction {
	/** シリアルID */
	private static final long serialVersionUID = 1L;
	private ViewFrame component;

	public FirstAction(ViewFrame component) {
		putValue(NAME, ResourceUtil.getString(Constants.ACTION_NAME_FIRST));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0));
		this.component = component;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		PDDocument document = null;
		try {
			document = Loader.loadPDF(component.getCurrentFile());
//			component.setCurrentPage(0);
//			PDFRenderer pdfRenderer = new PDFRenderer(document);
//			int pageCount = document.getNumberOfPages();
//			// note that the page number parameter is zero based
//			BufferedImage image = pdfRenderer.renderImage(component.getCurrentPage());
//			BufferedImage image2 = pdfRenderer.renderImage(component.getCurrentPage() + 1);
//			BufferedImage image3 = pdfRenderer.renderImage(component.getCurrentPage() + 2);
//			component.setImages(new BufferedImage[] { image, image2, image3 });
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
