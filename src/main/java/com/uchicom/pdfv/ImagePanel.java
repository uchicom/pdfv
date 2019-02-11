// (c) 2014 uchicom
package com.uchicom.pdfv;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * pdfをロードして、画像のストリーム形式で保存する。
 * 
 * @author Shigeki Uchiyama
 *
 */
public class ImagePanel extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BufferedImage[] images;

	private Image currentImage;
	private double base = 1.5;
	private int ratio;
	private int currentPage;

	public void addRatio(int d) {
		this.ratio += d;
		setScaledImage();
	}

	public int getRatio() {
		return ratio;
	}

	public double getRatioLabel() {
		return 100 * Math.pow(base, ratio);
	}

	public void setImages(BufferedImage[] images) {
		this.images = images;
		setScaledImage();
		repaint();
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
		if (images != null && images[currentPage] != null) {
			setPreferredSize(new Dimension(images[currentPage].getWidth(), images[currentPage].getHeight()));
			setScaledImage();
		}
		repaint();
	}

	public int getCurrentPage() {
		return currentPage;
	}
	
	public void setScaledImage() {
		int width = (int) (images[currentPage].getWidth() * Math.pow(base, ratio));
		int height = (int) (images[currentPage].getHeight() * Math.pow(base, ratio));
		currentImage = images[currentPage].getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
	}

	public void update(Graphics g) {
//        paint(g);
	}

	@Override
	public void paint(Graphics go) {
		if (currentImage != null) {
			go.drawImage(currentImage, 0, 0, this);
		}
	}

}
