// (C) 2014 uchicom
package com.uchicom.pdfv;

import com.uchicom.pdfv.action.AboutAction;
import com.uchicom.pdfv.action.FirstAction;
import com.uchicom.pdfv.action.HelpAction;
import com.uchicom.pdfv.action.LastAction;
import com.uchicom.pdfv.action.LeftAction;
import com.uchicom.pdfv.action.OpenAction;
import com.uchicom.pdfv.action.PropertyAction;
import com.uchicom.pdfv.action.RightAction;
import com.uchicom.pdfv.action.SaveAction;
import com.uchicom.pdfv.action.SplitSaveAction;
import com.uchicom.pdfv.ui.MessageIcon;
import com.uchicom.pdfv.util.ResourceUtil;
import com.uchicom.ui.FileOpener;
import com.uchicom.ui.ResumeFrame;
import com.uchicom.util.Parameter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * @author Shigeki Uchiyama
 */
public class ViewFrame extends ResumeFrame implements FileOpener {

  /** */
  private static final long serialVersionUID = 1L;

  private PdfImagePanel panel;
  private JSlider slider;
  PDDocument document;
  PDFRenderer renderer;
  JPanel leftPanel;
  JScrollPane leftScrollPane;
  BufferedImage emptyImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
  MessageIcon loadingIcon = new MessageIcon("読込中...", Color.RED, 15F, 64, 64);
  MessageIcon errorIcon = new MessageIcon("Error.", Color.RED, 15F, 64, 64);
  JSplitPane splitPane;

  /** 設定プロパティーファイルの相対パス */
  private static final String CONF_FILE_PATH = "./conf/pdfv.properties";

  public ViewFrame(Parameter parameter) {
    super(new File(CONF_FILE_PATH), "pdfv.window");
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    initComponents(parameter);
  }

  private void initComponents(Parameter parameter) {
    setTitle(
        ResourceUtil.getString(Constants.APPLICATION_TITLE)
            + " "
            + ResourceUtil.getString(Constants.APPLICATION_VERSION));

    setJMenuBar(createJMenuBar());
    panel = new PdfImagePanel();
    // FileOpener.installDragAndDrop(panel, this);
    panel.addMouseWheelListener(
        new MouseWheelListener() {

          @Override
          public void mouseWheelMoved(MouseWheelEvent e) {
            if ((e.getModifiersEx() & java.awt.event.MouseEvent.CTRL_DOWN_MASK) != 0) {
              // 拡大縮小（明日はこの拡大縮小の中心動作を実装する。）
              if (panel.getRatio() > Constants.MIN_RATIO && e.getWheelRotation() > 0
                  || panel.getRatio() < Constants.MAX_RATIO && e.getWheelRotation() < 0) {
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
    slider.addChangeListener(
        new ChangeListener() {

          @Override
          public void stateChanged(ChangeEvent e) {
            int newPage = slider.getValue();
            panel.setCurrentPage(newPage);
          }
        });
    // slider.setPaintLabels(true);
    leftPanel = new JPanel();
    leftPanel.setLayout(new GridBagLayout());

    var viewPanel = new JPanel();
    viewPanel.setLayout(new BorderLayout());
    viewPanel.add(new JScrollPane(panel), BorderLayout.CENTER);
    viewPanel.add(slider, BorderLayout.SOUTH);
    splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    leftScrollPane = new JScrollPane(leftPanel);
    leftScrollPane.getViewport().addChangeListener(e -> loadImage((JViewport) e.getSource()));
    splitPane.setLeftComponent(leftScrollPane);
    splitPane.setRightComponent(viewPanel);
    splitPane.setDividerLocation(200);
    FileOpener.installDragAndDrop(splitPane, this);
    getContentPane().add(splitPane);
    pack();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> close()));
    if (parameter.is("file")) {
      var file = parameter.getFile("file");
      if (!file.exists()) {
        JOptionPane.showMessageDialog(this, "ファイルが見つかりません。" + file.getPath());
        return;
      }
      if (!file.isFile()) {
        JOptionPane.showMessageDialog(this, "ファイルではありません。" + file.getPath());
        return;
      }
      if (!file.canRead()) {
        JOptionPane.showMessageDialog(this, "ファイルが読み取れません。" + file.getPath());
        return;
      }
      try {
        open(file);
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
        e.printStackTrace();
      }
    }
  }

  void loadImage(JViewport viewport) {
    var viewRect = viewport.getViewRect();
    var components = leftPanel.getComponents();

    var count = 0;
    var breakFlag = false;
    for (var i = 0; i < components.length; i++) {
      var component = components[i];
      if (!(component instanceof JRadioButton)) {
        continue;
      }
      var componentRect = component.getBounds();
      var isVisible = viewRect.intersects(componentRect);
      count++;
      var radio = (JRadioButton) component;
      if (!isVisible) {
        radio.setIcon(loadingIcon);
        leftPanel.repaint();
        if (breakFlag) {
          break;
        }
        continue;
      }
      breakFlag = true;
      if (radio.getIcon() != null && radio.getIcon().getIconWidth() == viewRect.width) {
        continue;
      }
      try {
        synchronized (renderer) {
          radio.setIcon(
              new ImageIcon(
                  renderer.renderImage(
                      count - 1, (viewRect.width - 12) / getWidth(document.getPage(count - 1)))));
        }
      } catch (IOException e1) {
        radio.setIcon(errorIcon);
      }
    }
  }

  float getWidth(PDPage pdPage) {
    var cropBox = pdPage.getCropBox();
    return pdPage.getRotation() % 180 == 0 ? cropBox.getWidth() : cropBox.getHeight();
  }

  private JMenuBar createJMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu(ResourceUtil.getString(Constants.MENU_NAME_FILE));
    JMenuItem menuItem = new JMenuItem(new OpenAction(this));
    menu.add(menuItem);
    menuItem = new JMenuItem(new SaveAction(this));
    menu.add(menuItem);
    menuItem = new JMenuItem(new SplitSaveAction(this));
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
    fileChooser.setFileFilter(
        new FileFilter() {

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
    SwingUtilities.invokeLater(() -> load(file));
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

  void load(File file) {

    setCurrentFile(file);
    try {
      close();
      document = Loader.loadPDF(file);

      // ページのリストから最初の1ページを取得する
      renderer = new PDFRenderer(document);
      panel.setPDFRenderer(renderer);
      int max = document.getNumberOfPages();
      leftPanel.removeAll();
      ButtonGroup group = new ButtonGroup();
      var gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.weightx = 1.0;
      gbc.anchor = GridBagConstraints.NORTH;
      gbc.insets.top = 2;
      gbc.insets.bottom = 2;
      gbc.insets.left = 2;
      gbc.insets.right = 2;
      for (int i = 0; i < max; i++) {
        if (i > 0) {
          leftPanel.add(new JToggleButton("分割"), gbc);
          gbc.gridy++;
        }
        final int j = i;
        JRadioButton radio = new JRadioButton(loadingIcon);
        radio.setAction(
            new AbstractAction() {
              @Override
              public void actionPerformed(ActionEvent e) {
                show(j);
              }
            });
        group.add(radio);

        leftPanel.add(radio, gbc);
        gbc.gridy++;
      }
      setSize(max);
      panel.setCurrentPage(panel.getCurrentPage());
      pack();
      loadImage(leftScrollPane.getViewport());

    } catch (IOException e1) {
      e1.printStackTrace();
      JOptionPane.showMessageDialog(this, e1.getMessage());
    }

    System.gc();
  }

  void close() {
    if (document == null) {
      return;
    }
    try {
      document.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    document = null;
  }

  public void showFirst() {
    show(0);
  }

  public void showLast() {
    show(document.getNumberOfPages() - 1);
  }

  public void showNext() {
    int nextPage = panel.getCurrentPage() + 1;
    if (nextPage >= document.getNumberOfPages()) {
      nextPage = document.getNumberOfPages() - 1;
    }
    show(nextPage);
  }

  public void showPrevious() {
    int previousPage = panel.getCurrentPage() - 1;
    if (previousPage < 0) {
      previousPage = 0;
    }
    show(previousPage);
  }

  void show(int page) {
    panel.setCurrentPage(page);
    slider.setValue(page);
  }

  public void save() {
    JOptionPane.showMessageDialog(this, "未実装");
  }

  public void saveSplit() {
    var splitIndexList = new ArrayList<Integer>(document.getNumberOfPages());
    var components = leftPanel.getComponents();
    var count = 0;
    for (var i = 0; i < components.length; i++) {
      if (components[i] instanceof JRadioButton) {
        continue;
      }
      if (components[i] instanceof JToggleButton button) {
        count++;
        if (button.isSelected()) {
          splitIndexList.add(count);
        }
      }
    }
    if (splitIndexList.isEmpty()) {
      JOptionPane.showMessageDialog(this, "分割位置が指定されていません。");
      return;
    }

    var ret = JOptionPane.showConfirmDialog(this, "分割保存しますがよろしいですか？");
    if (ret != JOptionPane.YES_OPTION) {
      return;
    }
    splitIndexList.add(document.getNumberOfPages());
    var startIndex = 0;
    for (var splitIndex : splitIndexList) {
      var splitFileName =
          currentFile
              .getName()
              .replaceAll("\\.pdf$", "_" + (startIndex + 1) + "-" + splitIndex + ".pdf");
      try (var newDocument = new PDDocument()) {
        for (var j = startIndex; j < splitIndex; j++) {
          newDocument.addPage(document.getPage(j));
        }
        newDocument.save(new File(currentFile.getParentFile(), splitFileName));
        newDocument.close();
      } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "分割保存に失敗しました。" + splitFileName);
        return;
      }
      startIndex = splitIndex;
    }
    JOptionPane.showMessageDialog(this, "分割保存が完了しました。");
  }
}
