// (C) 2014 uchicom
package com.uchicom.pdfv.ui;

import com.uchicom.ui.ImagePanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * pdfをロードして、画像のストリーム形式で保存する。
 *
 * @author Shigeki Uchiyama
 */
public class PdfImagePanel extends ImagePanel {

  /** */
  private static final long serialVersionUID = 1L;

  PDFRenderer renderer;

  private double base = 1.5;
  private int ratio;
  private int currentPage = -1;
  private BufferedImage currentImage;
  private PDPage pdPage;

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

  public void setCurrentPage(int currentPage, PDPage pdPage) {
    if (this.currentPage == currentPage) {
      return;
    }
    this.currentPage = currentPage;
    this.pdPage = pdPage;
    setCurrentImage();
    setScaledImage();
    repaint();
  }

  public int getCurrentPage() {
    return currentPage;
  }

  void setCurrentImage() {
    try {
      synchronized (renderer) {
        var widthScale = getWidth() / getWidth(pdPage);
        var heightScale = getHeight() / getHeight(pdPage);
        if (widthScale < heightScale) {
          currentImage = renderer.renderImage(currentPage, widthScale);
        } else {
          currentImage = renderer.renderImage(currentPage, heightScale);
        }

        // System.out.println("Loaded page " + (currentPage + 1));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  void setScaledImage() {
    int width = (int) (currentImage.getWidth() * Math.pow(base, ratio));
    int height = (int) (currentImage.getHeight() * Math.pow(base, ratio));
    setImage(currentImage.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING));
    // System.out.println("Scaled to " + width + "x" + height);
  }

  @Override
  public void paint(Graphics g) {
    // 背景色を塗りつぶし
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, getWidth(), getHeight());
    if (currentImage == null) {
      return;
    }
    super.paint(g);
  }

  float getWidthMagnification(PDPage pdPage) {
    var cropBox = pdPage.getCropBox();
    return pdPage.getRotation() % 180 == 0
        ? cropBox.getHeight() / cropBox.getWidth()
        : cropBox.getWidth() / cropBox.getHeight();
  }

  float getWidth(PDPage pdPage) {
    var cropBox = pdPage.getCropBox();
    return pdPage.getRotation() % 180 == 0 ? cropBox.getWidth() : cropBox.getHeight();
  }

  float getHeight(PDPage pdPage) {
    var cropBox = pdPage.getCropBox();
    return pdPage.getRotation() % 180 == 0 ? cropBox.getHeight() : cropBox.getWidth();
  }
}
