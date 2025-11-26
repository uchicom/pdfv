// (C) 2025 uchicom
package com.uchicom.pdfv.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class XorImageIcon extends ImageIcon {

  public XorImageIcon(Image image) {
    super(image);
  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {

    g.setXORMode(new Color(0, 0, 255, 10));

    // 画像を描画
    g.drawImage(getImage(), x, y, getImageObserver());
  }
}
