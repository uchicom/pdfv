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
public class OpenAction extends AbstractAction {
  /** シリアルID */
  private static final long serialVersionUID = 1L;

  private ViewFrame component;

  public OpenAction(ViewFrame component) {
    putValue(NAME, ResourceUtil.getString(Constants.ACTION_NAME_OPEN));
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
    this.component = component;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    component.open();
  }
}
