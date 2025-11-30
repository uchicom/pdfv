// (C) 2014 uchicom
package com.uchicom.pdfv.action;

import com.uchicom.pdfv.Constants;
import com.uchicom.pdfv.ui.ViewFrame;
import com.uchicom.pdfv.util.ResourceUtil;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 * @author Shigeki Uchiyama
 */
public class LastAction extends AbstractAction {
  /** シリアルID */
  private static final long serialVersionUID = 1L;

  private ViewFrame component;

  public LastAction(ViewFrame component) {
    putValue(NAME, ResourceUtil.getString(Constants.ACTION_NAME_LAST));
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_END, 0));
    this.component = component;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    component.showLast();
  }
}
