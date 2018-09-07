// (c) 2014 uchicom
package com.uchicom.pdfv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
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
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDComboBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
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
import com.uchicom.ui.ResumeFrame;

/**
 *
 * @author Shigeki Uchiyama
 *
 */
public class ViewFrame extends ResumeFrame implements FileOpener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ImagePanel panel;
	private JSlider slider;

	/**
	 * 設定プロパティーファイルの相対パス
	 */
	private static final String CONF_FILE_PATH = "./conf/pdfv.properties";

	public ViewFrame() {
		super(new File(CONF_FILE_PATH), "syo.window");
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
				if ((e.getModifiers() & ActionEvent.CTRL_MASK) != 0) {
					// 拡大縮小（明日はこの拡大縮小の中心動作を実装する。）
					if (panel.getRatio() > Constants.MIN_RATIO && e.getWheelRotation() < 0
							|| panel.getRatio() < Constants.MAX_RATIO && e.getWheelRotation() > 0) {
						// 拡大縮小
						panel.addRatio(e.getWheelRotation());
						// 拡大縮小ラベル設定
						panel.repaint();
						ResourceUtil.debug(e);
					}
				}
			}

		});
		slider = new JSlider(0, 100, 0);
		// slider.setInverted(true);
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
		// slider.setPaintLabels(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(panel), BorderLayout.CENTER);
		getContentPane().add(slider, BorderLayout.SOUTH);
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

	// public void setImages(BufferedImage[] images) {
	// panel.setImage(images);
	// panel.repaint();
	// }
	// public void setRenderer(PDFRenderer renderer) {
	// panel.setRenderer(renderer);
	// }
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

		String current = getString("current");
		if (current != null) {
			fileChooser.setCurrentDirectory(new File(current));
		}
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

	/*
	 * (非 Javadoc)
	 * 
	 * @see com.uchicom.ui.FileOpener#open(java.io.File)
	 */
	@Override
	public void open(File file) throws IOException {
		config.setProperty("current", file.getParentFile().getPath());
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
				PDAcroForm form = document.getDocumentCatalog().getAcroForm();
				Map<PDPage, List<PDField>> map = new HashMap<>();
				if (form != null) {
					for (PDField field : form.getFields()) {
						for (PDAnnotationWidget widget : field.getWidgets()) {
							PDPage page = widget.getPage();
							if (map.containsKey(page)) {
								map.get(page).add(field);
							} else {
								List<PDField> fieldList = new ArrayList<>();
								fieldList.add(field);
								map.put(page, fieldList);
							}
						}
					}
				}
				PDPageTree pageTree = document.getPages();
				int dpi = 300;
				for (int i = 0; i < max; i++) {
					System.out.println("loading:" + i);
					images[i] = renderer.renderImageWithDPI(i, 300);
					PDPage page = pageTree.get(i);
					if (map.containsKey(page)) {
						Graphics g = images[i].getGraphics();
						for (PDField field : map.get(page)) {
							for (PDAnnotationWidget widget : field.getWidgets()) {
								PDRectangle rect = widget.getRectangle();
								if (rect == null) {

								} else {
									int x = Math.round(rect.getLowerLeftX() * dpi / 72);
									int y = Math.round(rect.getUpperRightY() * dpi / 72);
									int width = Math.round(rect.getWidth() * dpi / 72);
									int height = Math.round(rect.getHeight() * dpi / 72);
									g.setColor(Color.BLUE);
									g.drawRect(x, images[i].getHeight() - y, width, height);

									if (field instanceof PDTextField) {
										PDTextField text = (PDTextField) field;
										// maxレングスは入力チェックで利用。
										int length = text.getMaxLen();
										String[] styles = text.getDefaultAppearance().split(" ");
										String fontName = styles[0].substring(1);
										String fontSize = styles[1];
										if ("0".equals(styles[1])) {
											// 後は全体のサイズが収まるようにフォントサイズを計算する。
											int maxFont = width / length;
											if (maxFont > height) {
												maxFont = height;
											}
											g.setFont(new Font(fontName, Font.PLAIN, maxFont));
										} else {
											g.setFont(new Font(fontName, Font.PLAIN,
													Math.round(Float.parseFloat(fontSize) * dpi / 72)));
										}
										Rectangle2D rect2 = g.getFont().getStringBounds(text.getValue(),
												new FontRenderContext(new AffineTransform(), false, false));
										g.drawString(text.getValue(), x + width - (int) rect2.getWidth(),
												images[i].getHeight() - y + height);
									} else if (field instanceof PDComboBox) {
										PDComboBox combo = (PDComboBox) field;
										String[] styles = combo.getDefaultAppearance().split(" ");
										String fontName = styles[0].substring(1);
										String fontSize = styles[1];
										if ("0".equals(styles[1])) {
											g.setFont(new Font(fontName, Font.PLAIN, width));
										} else {
											g.setFont(new Font(fontName, Font.PLAIN,
													Math.round(Float.parseFloat(fontSize) * dpi / 72)));
										}
										Rectangle2D rect2 = g.getFont().getStringBounds(combo.getValue().get(0),
												new FontRenderContext(new AffineTransform(), false, false));
										g.drawString(combo.getValue().get(0), x + width - (int) rect2.getWidth(),
												images[i].getHeight() - y + height);
									}
								}
							}
						}

					}
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

	/*
	 * (非 Javadoc)
	 * 
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
