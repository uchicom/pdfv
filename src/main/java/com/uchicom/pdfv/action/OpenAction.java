// (c) 2014 uchicom
package com.uchicom.pdfv.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.uchicom.pdfv.Constants;
import com.uchicom.pdfv.ViewFrame;
import com.uchicom.pdfv.util.ResourceUtil;

/**
 *
 * @author Shigeki Uchiyama
 *
 */
public class OpenAction extends AbstractAction {
	/** シリアルID */
	private static final long serialVersionUID = 1L;
	private ViewFrame component;
	public OpenAction(ViewFrame component) {
		putValue(NAME, ResourceUtil.getString(Constants.ACTION_NAME_OPEN));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		this.component = component;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.canRead()) {
					if (f.isDirectory()) {
						return true;
					}
					if (f.isFile() && f.getName().matches(".*\\.[pP][dD][fF]$")) {
						return true;
					}
				}
				return false;
			}

			@Override
			public String getDescription() {
				return "*.pdf";
			}

		});
		fileChooser.showOpenDialog(component);
		File file = fileChooser.getSelectedFile();
		if (file != null) {
			component.setCurrentFile(file);
			// PDFドキュメントをロード
			PDDocument document = null;
			try {
				document = PDDocument.load(file);
				component.setSize(document.getNumberOfPages());
				// ページのリストから最初の1ページを取得する
				PDFRenderer renderer = new PDFRenderer(document);
				int pageCount = document.getNumberOfPages();
				// note that the page number parameter is zero based
				BufferedImage image = renderer.renderImageWithDPI(component.getCurrentPage(), 300, ImageType.ARGB);
				component.setImages(new BufferedImage[]{image});
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
