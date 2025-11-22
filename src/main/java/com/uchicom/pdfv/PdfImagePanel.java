// (C) 2014 uchicom
package com.uchicom.pdfv;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JComponent;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * pdfをロードして、画像のストリーム形式で保存する。
 *
 * @author Shigeki Uchiyama
 */
public class PdfImagePanel extends JComponent {

  /** */
  private static final long serialVersionUID = 1L;

  PDFRenderer renderer;

  private double base = 1.5;
  private int ratio;
  private int currentPage;

  public void setPDFRenderer(PDFRenderer renderer) {
    this.renderer = renderer;
  }

  public void addRatio(int d) {
    this.ratio -= d;
    setScaledImage();
  }

  public int getRatio() {
    return ratio;
  }

  public double getRatioLabel() {
    return 100 * Math.pow(base, ratio);
  }

  public void setImages(BufferedImage[] images) {
    setScaledImage();
    repaint();
  }

  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
    setScaledImage();
    repaint();
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public void setScaledImage() {
    try {
      synchronized (renderer) {
        BufferedImage image = renderer.renderImageWithDPI(currentPage, 200);
        int width = (int) (image.getWidth() * Math.pow(base, ratio));
        int height = (int) (image.getHeight() * Math.pow(base, ratio));
        setImage(image.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING));
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  Image image;

  public void setImage(Image image) {
    this.image = image;
    setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
  }

  @Override
  public void update(Graphics g) {
    paint(g);
  }

  @Override
  public void paint(Graphics g) {
    // 背景色を塗りつぶし
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, getWidth(), getHeight());
    if (image == null) {
      return;
    }
    // 中央に表示
    int x = 0, y = 0;
    if (image.getWidth(this) < getWidth()) {
      x = (getWidth() - image.getWidth(this)) / 2;
    }
    if (image.getHeight(this) < getHeight()) {
      y = (getHeight() - image.getHeight(this)) / 2;
    }
    // 画像描画
    g.drawImage(image, x, y, this);
  }
}
