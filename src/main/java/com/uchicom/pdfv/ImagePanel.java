// (c) 2014 uchicom
package com.uchicom.pdfv;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 *
 * @author Shigeki Uchiyama
 *
 */
public class ImagePanel extends Component {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	BufferedImage[] images;
	double base = 1.1;
	int ratio = 0;
	int[] widths;
	int[] heights;
	public void addRatio(int d) {
		this.ratio += d;
		initSize();
	}
	public int getRatio() {
		return ratio;
	}
	public double getRatioLabel() {
		return 100 * Math.pow(base, ratio);
	}
	public void setImage(BufferedImage[] images) {
		this.images = images;
		initSize();
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	int offset;
	public void initSize() {
		System.out.println(ratio);
		if (images == null) return;
		int width=0;
		int height=0;
		int max = images.length;
		widths = new int[max];
		heights = new int[max];
		for (int iArray = 0; iArray < max; iArray++) {
			BufferedImage image = images[iArray];
			widths[iArray] = (int)(image.getWidth() * Math.pow(base, ratio));
			heights[iArray] = (int)(image.getHeight() * Math.pow(base,  ratio));
			width += widths[iArray];
			height +=  heights[iArray];
		}
		setPreferredSize(new Dimension(width, height));
		setSize(new Dimension(width, height));
		System.out.println(width + ":" + height);

	}
    public void update(Graphics g) {
        paint(g);
    }
	int scale = 1;
	Image bImage = new BufferedImage(2000,1000, BufferedImage.TYPE_3BYTE_BGR);
	@Override
	public void paint(Graphics go) {
		int bX = 0;
		if (images != null && bImage != null) {
			if (offset == 0) {
				Graphics g = bImage.getGraphics();
				g.clearRect(0,  0, 2000,  1000);
				int max = images.length;
				int x = 0;
				for (int iArray = 0; iArray < max; iArray++) {
					BufferedImage image = images[iArray];
					if (iArray == 0) {
						g.drawImage(image, x,0,  x + widths[iArray] - widths[iArray] * offset / scale, heights[iArray],
								image.getWidth() * offset / scale, 0, image.getWidth(), image.getHeight(),
								this);
						x = - widths[iArray] * offset / scale;
					} else {
						g.drawImage(image, x,0,  x + widths[iArray], heights[iArray],
								0, 0, image.getWidth(), image.getHeight(),
								this);
					}
					x += widths[iArray];
				}
			}
		}
		go.drawImage(bImage, 0, 0, 2000 - 2000 * offset / scale, 1000, 2000 * offset / scale, 0, 2000, 1000, this);
	}
}
