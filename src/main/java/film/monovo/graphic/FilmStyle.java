package film.monovo.graphic;

import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilmStyle {
	public final static int pHeightMM = 25;
	public final static int fHeaderWidthMM = 30;
	public final static int fTailWidthMM = 30;
	public final static int fTotalHeightMM = 42;
	public final static double fHeaderUpperOffsetMM = 3;
	public final static int fHeaderLowerOffsetMM = 2;
	public final static int pDelimiterWidthMM = 1;
	public final static double hOuterMarginMM = 1.5;
	public final static int hInnerMarginMM = 1;
	public final static int hWidthMM = 2;
	public final static int hHeightMM = 3;
	public final static double hBeltHeightMM = hOuterMarginMM + hInnerMarginMM + hHeightMM;
	public final static double fBeltHeightMM = hBeltHeightMM * 2 + pHeightMM;

	public final static int photoHeight = Resolution.toPixel(pHeightMM);
	public final static int photoDelimiterWidth = Resolution.toPixel(pDelimiterWidthMM);
	public final static int photoVerticalOffset = Resolution.toPixel(fHeaderUpperOffsetMM + hBeltHeightMM);
	public final static int headerWidth = Resolution.toPixel(fHeaderWidthMM);
	public final static int tailerWidth = Resolution.toPixel(fTailWidthMM);
	public final static int totalHeight = Resolution.toPixel(fTotalHeightMM);
	public final static int beltVerticalOffset = Resolution.toPixel(fHeaderUpperOffsetMM);
	public final static int beltHeight = Resolution.toPixel(fBeltHeightMM);
	public final static int holeWidth = Resolution.toPixel(hWidthMM);
	public final static int holeHeight = Resolution.toPixel(hHeightMM);

	public final static BufferedImage filmHeader = getFilmHeader();

	public final static BufferedImage getFilmHeader() {
		try {
			//return new Image(new FileInputStream(ResourceUtils.getFile("classpath:FilmHeader.jpg")));
			return resizeTry(ImageIO.read(ResourceUtils.getFile("classpath:HeaderBox.jpg")), totalHeight);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public final static int getPhotoFrameHeight() {
		return photoHeight;
	}

	public final static int getTotalFrameHeight() {
		return totalHeight;
	}

	public final static BufferedImage build(List<String> imgs) {
		List<BufferedImage> images = getBufferedImages(imgs);

		var width = getTotalLength(images);
    	var img = new BufferedImage(width, totalHeight, BufferedImage.TYPE_INT_RGB);


    	var canvas = (Graphics2D) img.getGraphics();
    	canvas.setColor(Color.WHITE);
    	canvas.fill(new Rectangle(0, 0, width, totalHeight));

    	drawBelt(images, canvas, width);
    	drawHoles(width, canvas);
    	drawPhotos(canvas, images);
    	return img;
	}

	private static List<BufferedImage> getBufferedImages(List<String> imgs) {
		var result = new ArrayList<BufferedImage>();
		for(String path: imgs) {
			try {
				var f = new File(path);
				var image = resizeTry(ImageIO.read(f), photoHeight);
				result.add(image);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private static void drawBelt(List<BufferedImage> photos, Graphics2D canvas, int totalLength) {
		canvas.setColor(Color.BLACK);
		canvas.drawImage(filmHeader, 0, 0, null);
		canvas.fill(new Rectangle(filmHeader.getWidth() - 30, beltVerticalOffset, totalLength, beltHeight));
	}
	
	private static int getTotalLength(List<BufferedImage> normalizedPhotos) {
		var photosWidth = 0;
		for (BufferedImage it : normalizedPhotos) {
			photosWidth += it.getWidth();
		}
		return photosWidth + photoDelimiterWidth * (normalizedPhotos.size() - 1) + headerWidth + tailerWidth;
	}

	private static int getTotalLengthMM(List<BufferedImage> normalizedPhotos) {
		return Resolution.toMillimeter(getTotalLength(normalizedPhotos));
	}
	
	private static void drawHoles(int width, Graphics2D canvas) {
		var startX = headerWidth + holeWidth;
		var vOffset1 = Resolution.toPixel(fHeaderUpperOffsetMM + hOuterMarginMM);
		var vOffset2 = vOffset1 + holeHeight + photoHeight + Resolution.toPixel(hInnerMarginMM * 2);
		//var endPointX = startX + holeWidth;
		while (startX <= width) {
			canvas.setColor(Color.WHITE);
			canvas.fill(new Rectangle(startX, vOffset1, holeWidth, holeHeight));
			canvas.fill(new Rectangle(startX, vOffset2, holeWidth, holeHeight));
			startX += holeWidth * 2;
			//endPointX += holeWidth * 2;
		}
	}
	
	private static void drawPhotos(Graphics2D canvas, List<BufferedImage> photos) {
        var startX = headerWidth + holeWidth;
        var isFirst = true;
        for(BufferedImage it: photos){
            if(isFirst) {
                isFirst = false;
            } else {
                startX += photoDelimiterWidth;
            }
            canvas.drawImage(it, startX, photoVerticalOffset, null);
            startX += it.getWidth();
        }
    }
	
//	private static BufferedImage resize(BufferedImage originalImage, int height) {
// 		var width = height  * originalImage.getWidth() / originalImage.getHeight();
//		var resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//		var g = resizedImage.createGraphics();
//		g.drawImage(originalImage, 0, 0, width, height, null);
//		g.dispose();
//
//		g.setComposite(AlphaComposite.Src);
//
//		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//		return resizedImage;
//	}

	private static BufferedImage resizeTry(BufferedImage sourceImage, int height) {
		double ratio = (double) sourceImage.getWidth()/sourceImage.getHeight();
		var width = (int)(ratio * height + 0.4);
		if(sourceImage.getHeight() == height && sourceImage.getWidth() == width) return sourceImage;
		Image scaled = sourceImage.getScaledInstance(width, height, 4);
		BufferedImage bufferedScaled = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufferedScaled.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.drawImage(scaled, 0, 0, width, height, null);
		return bufferedScaled;
	}
}
