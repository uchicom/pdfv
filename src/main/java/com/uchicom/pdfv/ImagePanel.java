// (c) 2014 uchicom
package com.uchicom.pdfv;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * pdfをロードして、画像のストリーム形式で保存する。
 * @author Shigeki Uchiyama
 *
 */
public class ImagePanel extends Component {

	private BufferedImage[] images;
	private double base = 1.1;
	private int ratio = 0;
	private int currentPage;
	private float scale = 1;

	public void addRatio(int d) {
		this.ratio += d;
	}
	public int getRatio() {
		return ratio;
	}
	public double getRatioLabel() {
		return 100 * Math.pow(base, ratio);
	}
	public void setImages(BufferedImage[] images) {
		this.images = images;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
		if (images != null && images[currentPage] != null) {
			setPreferredSize(new Dimension(images[currentPage].getWidth(), images[currentPage].getHeight()));
		}
		repaint();
	}
	public int getCurrentPage() {
		return currentPage;
	}
    public void update(Graphics g) {
//        paint(g);
    }
	@Override
	public void paint(Graphics go) {
		if (images != null && images[currentPage] != null) {
			System.out.println("paint!" + currentPage);
			go.drawImage(images[currentPage], 0,0, this);
		}
	}

}
