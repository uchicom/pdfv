// (c) 2014 uchicom
package com.uchicom.pdfv;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.uchicom.pdfv.action.AboutAction;
import com.uchicom.pdfv.action.FirstAction;
import com.uchicom.pdfv.action.HelpAction;
import com.uchicom.pdfv.action.LastAction;
import com.uchicom.pdfv.action.LeftAction;
import com.uchicom.pdfv.action.OpenAction;
import com.uchicom.pdfv.action.PropertyAction;
import com.uchicom.pdfv.action.RightAction;
import com.uchicom.pdfv.action.SaveAction;
import com.uchicom.pdfv.util.ResourceUtil;

/**
 *
 * @author Shigeki Uchiyama
 *
 */
public class ViewFrame extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public ViewFrame() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		initComponents();
	}
	ImagePanel panel;
	JSlider slider;
	private void initComponents() {
		setTitle(ResourceUtil.getString(Constants.APPLICATION_TITLE) + " "
				+ ResourceUtil.getString(Constants.APPLICATION_VERSION));
		setJMenuBar(createJMenuBar());
		panel = new ImagePanel();
		panel.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if ((e.getModifiers() &  ActionEvent.CTRL_MASK) != 0) {
					//拡大縮小（明日はこの拡大縮小の中心動作を実装する。）
					if (panel.getRatio() > Constants.MIN_RATIO && e.getWheelRotation() < 0 ||
							panel.getRatio() < Constants.MAX_RATIO && e.getWheelRotation() > 0) {
						//拡大縮小
						panel.addRatio(e.getWheelRotation());
						//拡大縮小ラベル設定
						panel.repaint();
						ResourceUtil.debug(e);
					}
				}
			}

		});
		slider = new JSlider(0, 100, 0);
//		slider.setInverted(true);
		slider.setPaintTicks(true);
		slider.setMinorTickSpacing(1);
		slider.setMajorTickSpacing(10);
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				// PDFドキュメントをロード
				PDDocument document = null;
				try {
					document = PDDocument.load(ViewFrame.this.getCurrentFile());
					int newPage = slider.getValue();
					if (newPage < document.getNumberOfPages() - 1) {
						ViewFrame.this.setCurrentPage(newPage);
						// ページのリストから最初の1ページを取得する
						PDFRenderer pdfRenderer = new PDFRenderer(document);
						int pageCount = document.getNumberOfPages();
						// note that the page number parameter is zero based
						BufferedImage image = pdfRenderer.renderImage(ViewFrame.this.getCurrentPage());
						BufferedImage image2 = pdfRenderer.renderImage(ViewFrame.this.getCurrentPage() + 1);
						BufferedImage image3 = pdfRenderer.renderImage(ViewFrame.this.getCurrentPage() + 2);
						ViewFrame.this.setImages(new BufferedImage[]{image, image2, image3});
					}
					ViewFrame.this.setOffset(slider.getValue() % 1);


				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(ViewFrame.this, e1.getMessage());
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

		});
//		slider.setPaintLabels(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(panel), BorderLayout.CENTER);
		getContentPane().add(slider, BorderLayout.SOUTH);
		setPreferredSize(new Dimension(100, 100));
	}

	private JMenuBar createJMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu(ResourceUtil.getString(Constants.MENU_NAME_FILE));
		JMenuItem menuItem = new JMenuItem(new OpenAction(this));
		menu.add(menuItem);
		menuItem = new JMenuItem(new SaveAction(this));
		menu.add(menuItem);
		menuItem = new JMenuItem(new PropertyAction(this));
		menu.add(menuItem);
		menuBar.add(menu);
		menu = new JMenu(ResourceUtil.getString(Constants.MENU_NAME_DISP));
		JMenu childMenu = new JMenu(ResourceUtil.getString(Constants.MENU_NAME_MOVE));
		menuItem = new JMenuItem(new FirstAction(this));
		childMenu.add(menuItem);
		menuItem = new JMenuItem(new LeftAction(this));
		childMenu.add(menuItem);
		menuItem = new JMenuItem(new RightAction(this));
		childMenu.add(menuItem);
		menuItem = new JMenuItem(new LastAction(this));
		childMenu.add(menuItem);
		menu.add(childMenu);
		menuBar.add(menu);


		menu = new JMenu(ResourceUtil.getString(Constants.MENU_NAME_HELP));
		menuItem = new JMenuItem(new HelpAction());
		menu.add(menuItem);
		menuItem = new JMenuItem(new AboutAction());
		menu.add(menuItem);
		menuBar.add(menu);
		return menuBar;
	}

	public void setImages(BufferedImage[] images) {
		panel.setImage(images);
		panel.repaint();
	}
	File currentFile;
	public void setCurrentFile(File file) {
		this.currentFile = file;
	}
	public File getCurrentFile() {
		return currentFile;
	}
	int currentPage;
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setOffset(int offset) {
		panel.setOffset(offset);
		panel.repaint();
	}
	public void setSize(int size) {
		slider.setMaximum(size - 1);
	}

}
