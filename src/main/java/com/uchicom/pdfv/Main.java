// (C) 2014 uchicom
package com.uchicom.pdfv;

import javax.swing.SwingUtilities;

/**
 * @author Shigeki Uchiyama
 */
public class Main {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> {
          ViewFrame frame = new ViewFrame();
          frame.setVisible(true);
        });
  }
}
