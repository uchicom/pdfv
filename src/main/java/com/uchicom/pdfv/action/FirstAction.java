// (C) 2014 uchicom
package com.uchicom.pdfv.action;

import com.uchicom.pdfv.Constants;
import com.uchicom.pdfv.ViewFrame;
import com.uchicom.pdfv.util.ResourceUtil;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 * 最初のページを表示する.
 *
 * @author Shigeki Uchiyama
 */
public class FirstAction extends AbstractAction {
  /** シリアルID */
  private static final long serialVersionUID = 1L;

  private ViewFrame component;

  public FirstAction(ViewFrame component) {
    putValue(NAME, ResourceUtil.getString(Constants.ACTION_NAME_FIRST));
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0));
    this.component = component;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    component.showFirst();
  }
}
