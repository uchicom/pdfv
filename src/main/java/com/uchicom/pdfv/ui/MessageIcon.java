// (C) 2025 uchicom
package com.uchicom.pdfv.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

public class MessageIcon implements Icon {
  String message;
  Color color;
  float fontSize;
  int width;
  int height;

  public MessageIcon(String message, Color color, float fontSize, int width, int height) {
    this.message = message;
    this.color = color;
    this.fontSize = fontSize;
    this.width = width;
    this.height = height;
  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {
    g.setColor(color);
    g.setFont(g.getFont().deriveFont(fontSize));
    g.drawString(message, x, y + (height + 15) / 2);
  }

  @Override
  public int getIconWidth() {
    return width;
  }

  @Override
  public int getIconHeight() {
    return height;
  }
}
