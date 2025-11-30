// (C) 2014 uchicom
package com.uchicom.pdfv;

import com.uchicom.pdfv.ui.ViewFrame;
import com.uchicom.util.Parameter;
import javax.swing.SwingUtilities;

/**
 * @author Shigeki Uchiyama
 */
public class Main {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ViewFrame(new Parameter(args)).setVisible(true));
  }
}
