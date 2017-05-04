// (c) 2014 uchicom
package com.uchicom.pdfv;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.text.SimpleDateFormat;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;

/**
 * プロパティー表示ダイアログ
 * @author shigeki
 *
 */
public class PropertyDialog extends JDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public PropertyDialog(JFrame owner, PDDocument document) {
		super(owner, "ファイルのプロパティ");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		initComponents(document);
	}
	private void initComponents(PDDocument document) {
//		setAlwaysOnTop(true);
		setModal(true);
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("メタ", new JScrollPane(createMetaPanel(createTable(document.getDocumentInformation()))));
		tabbedPane.add("XMP", new JScrollPane(createXmpPanel(document.getDocumentCatalog())));
		getContentPane().add(tabbedPane);
	}
	Format dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public JPanel createMetaPanel(String[][] table) {

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.BELOW_BASELINE_LEADING;
		constraints.ipadx = 10;
		constraints.ipady = 10;
		for (int i = 0; i < table.length; i++) {
			constraints.gridy = i;
			constraints.gridx = 0;
			JLabel label = new JLabel(table[i][0]);
			label.setVerticalAlignment(JLabel.TOP);
			label.setFont(label.getFont().deriveFont(Font.PLAIN));
			panel.add(label, constraints);
			constraints.gridx = 1;
			JTextArea textField = new JTextArea(table[i][1]);
			textField.setEditable(false);
//			textField.setBackground(Color.lightGray);
			textField.setFont(label.getFont());
			textField.setOpaque(false);
			panel.add(textField, constraints);
		}
		return panel;
	}
	public String[][] createTable(PDDocumentInformation di) {
		String[][] table = new String[9][];
		int i = 0;
		table[i++] = new String[]{"作成者", di.getCreator()};
		table[i++] = new String[]{"著者", di.getAuthor()};
		table[i++] = new String[]{"キーワード", di.getKeywords()};
		table[i++] = new String[]{"プロデューサー", di.getProducer()};
		table[i++] = new String[]{"サブジェクト", di.getSubject()};
		table[i++] = new String[]{"タイトル", di.getTitle()};
		table[i++] = new String[]{"トラップ", di.getTrapped()};
		table[i++] = new String[]{"作成日", dateFormat.format(di.getCreationDate().getTime())};
		table[i++] = new String[]{"更新日", dateFormat.format(di.getModificationDate().getTime())};

		return table;
	}
	public JPanel createXmpPanel(PDDocumentCatalog dc) {
		JPanel panel = new JPanel();
		PDMetadata metadata = dc.getMetadata();
		if (metadata != null) {
			StringBuffer strBuff = new StringBuffer();
			//to read the XML metadata
			try {
				InputStream is = metadata.createInputStream();
				byte[] bytes = new byte[1024 * 400];
				int length = is.read(bytes);
				while (length > 0) {
					strBuff.append(new String(bytes, 0, length));
					length = is.read(bytes);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JTextArea textArea = new JTextArea(strBuff.toString().replaceAll(" xmlns:", "\r\n xmlns:").replaceAll("><", ">\r\n<"));
			textArea.setEditable(false);
			textArea.setOpaque(false);
			panel.add(textArea);
		}
		return panel;
	}
}
