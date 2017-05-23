// (c) 2014 uchicom
package com.uchicom.pdfv;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.apache.pdfbox.io.MemoryUsageSetting;
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
import com.uchicom.ui.FileOpener;

/**
 *
 * @author Shigeki Uchiyama
 *
 */
public class ViewFrame extends JFrame implements FileOpener {

	private ImagePanel panel;
	private JSlider slider;

	public ViewFrame() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		initComponents();
	}
	private void initComponents() {
		setTitle(ResourceUtil.getString(Constants.APPLICATION_TITLE) + " "
				+ ResourceUtil.getString(Constants.APPLICATION_VERSION));


		setJMenuBar(createJMenuBar());
		panel = new ImagePanel();
		FileOpener.installDragAndDrop(panel, this);
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
				int newPage = slider.getValue();
				panel.setCurrentPage(newPage);
			}

		});
//		slider.setPaintLabels(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(panel), BorderLayout.CENTER);
		getContentPane().add(slider, BorderLayout.SOUTH);
		setPreferredSize(new Dimension(100, 100));
		pack();
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

//	public void setImages(BufferedImage[] images) {
//		panel.setImage(images);
//		panel.repaint();
//	}
//	public void setRenderer(PDFRenderer renderer) {
//		panel.setRenderer(renderer);
//	}
	File currentFile;
	public void setCurrentFile(File file) {
		this.currentFile = file;
	}
	public File getCurrentFile() {
		return currentFile;
	}
	public void setSize(int size) {
		slider.setMaximum(size - 1);
	}

	public void open() {
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
		fileChooser.showOpenDialog(this);
		File file = fileChooser.getSelectedFile();
		if (file != null) {
			setCurrentFile(file);
			try {
				open(file);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				e.printStackTrace();
			}
		}
	}
	/* (非 Javadoc)
	 * @see com.uchicom.ui.FileOpener#open(java.io.File)
	 */
	@Override
	public void open(File file) throws IOException {
		// PDFドキュメントをロード
		Thread thread = new Thread(() -> {
			try {
				PDDocument document = PDDocument.load(file, MemoryUsageSetting.setupTempFileOnly());

				// ページのリストから最初の1ページを取得する
				PDFRenderer renderer = new PDFRenderer(document);
				int max = document.getNumberOfPages();
				setSize(max);
				BufferedImage[] images = new BufferedImage[max];
				panel.setImages(images);
				for (int i = 0; i < max; i++) {
					System.out.println("loading:" + i);
					images[i] = renderer.renderImage(i);
					System.out.println("loaded:" + i);
				}
				panel.setCurrentPage(panel.getCurrentPage());
				renderer = null;
				document.close();
				document = null;
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, e1.getMessage());
			}

			System.gc();
		});
		thread.setDaemon(true);
		thread.start();
	}
	/* (非 Javadoc)
	 * @see com.uchicom.ui.FileOpener#open(java.util.List)
	 */
	@Override
	public void open(List<File> fileList) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		if (fileList.size() > 0) {
			try {
				open(fileList.get(0));
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				e.printStackTrace();
			}
		}
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

}
