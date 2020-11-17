package film.monovo.graphic;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import java.awt.Rectangle;

import org.springframework.util.ResourceUtils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class FilmStyle {
	public final static int pHeightMM = 25;
	public final static int fHeaderWidthMM = 30;
	public final static int fTailWidthMM = 30;
	public final static int fTotalHeightMM = 42;
	public final static int fHeaderUpperOffsetMM = 3;
	public final static int fHeaderLowerOffsetMM = 2;
	public final static int pDelimiterWidthMM = 1;
	public final static int hOuterMarginMM = 2;
	public final static int hInnerMarginMM = 1;
	public final static int hWidthMM = 2;
	public final static int hHeightMM = 3;
	public final static int hBeltHeightMM = hOuterMarginMM + hInnerMarginMM + hHeightMM;
	public final static int fBeltHeightMM = hBeltHeightMM * 2 + pHeightMM;

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
			return resize(ImageIO.read(ResourceUtils.getFile("classpath:FilmHeader.jpg")), totalHeight);
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

//    public final static int getTotalLength(List<Image> imgs) {
//		double imageLength = imgs.stream().map(it -> {
//			return it.getWidth();
//		}).reduce((a, b) -> {
//			return a + b;
//		}).get();
//		int imageTotalLength = (int) imageLength;
//		return imageTotalLength + +photoDelimiterWidth * (imgs.size() - 1) + headerWidth + tailerWidth;
//	}

//    public final static int getTotalLengthMM(List<Image> imgs) {
//		return Resolution.toMillimeter(getTotalLength(imgs));
//	}
//
//    private final static void drawBelt(List<Image> imgs, Canvas canvas, int totalLength) {
//		var gc = canvas.getGraphicsContext2D();
//		gc.drawImage(filmHeader, 0, 0);
//		gc.setFill(Color.BLACK);
//		gc.fillRect(headerWidth, beltVerticalOffset, totalLength - headerWidth, beltHeight);
//	}
//
//    private final static void drawHoles(int width, Canvas canvas) {
//		var gc = canvas.getGraphicsContext2D();
//		var startX = headerWidth + holeWidth;
//		var vOffset1 = Resolution.toPixel(fHeaderUpperOffsetMM + hOuterMarginMM);
//		var vOffset2 = vOffset1 + holeHeight + photoHeight + Resolution.toPixel(hInnerMarginMM * 2);
//		var endPointX = startX + holeWidth;
//		while (startX <= width) {
//			gc.setFill(Color.WHITE);
//			gc.fillRect(startX, vOffset1, holeWidth, holeHeight);
//			gc.fillRect(startX, vOffset2, holeWidth, holeHeight);
//			startX += holeWidth * 2;
//			endPointX += holeWidth * 2;
//		}
//	}

//    private final static void drawPhotos(Canvas canvas, List<Image> img) {
//		var gc = canvas.getGraphicsContext2D();
//		var startX = headerWidth + holeWidth;
//		var isFirst = true;
//		for (Image it : img) {
//			if (isFirst) {
//				isFirst = false;
//			} else {
//				startX += photoDelimiterWidth;
//			}
//			var adjustWidth = (int)(photoHeight / it.getHeight()) * it.getWidth();
//			
//			gc.drawImage(it, startX, photoVerticalOffset, adjustWidth, photoHeight);
//			startX += it.getWidth();
//		}
//	}
	


	public final static BufferedImage build(List<Image> imgs) {
//		var width = getTotalLength(imgs);
//		var canvas = new Canvas(width, totalHeight);
//		var gc = canvas.getGraphicsContext2D();
//		gc.setFill(Color.WHITE);
//		gc.fillRect(0, 0, width, totalHeight);
//
//		drawBelt(imgs, canvas, width);
//		drawHoles(width, canvas);
//		drawPhotos(canvas, imgs);
//		System.out.println(width);
//		System.out.println(totalHeight);
//
//		var result = new WritableImage(width, totalHeight);
//		canvas.get
//		return result;
    	
		
		var images = imgs.stream().map( it -> { return normalize(it);})
			.filter(it -> { return it != null; })
    		.collect(Collectors.toList());

    	var width = getTotalLength(images);
    	var img = new BufferedImage(width, totalHeight, BufferedImage.TYPE_INT_RGB);
    	var canvas = (Graphics2D) img.getGraphics();
    	canvas.setColor(Color.WHITE);
    	canvas.fill(new Rectangle(0, 0, width, totalHeight));
//drawHeader(canvas);
    	drawBelt(images, canvas, width);
    	drawHoles(width, canvas);
    	drawPhotos(canvas, images);
    	return img;
	}

	private static void drawBelt(List<BufferedImage> photos, Graphics2D canvas, int totalLength) {
		canvas.setColor(Color.BLACK);
		canvas.fill(new Rectangle(0, beltVerticalOffset, totalLength, beltHeight));
		canvas.drawImage(filmHeader, 0, 0, null);
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
		var endPointX = startX + holeWidth;
		while (startX <= width) {
			canvas.setColor(Color.WHITE);
			canvas.fill(new Rectangle(startX, vOffset1, holeWidth, holeHeight));
			canvas.fill(new Rectangle(startX, vOffset2, holeWidth, holeHeight));
			startX += holeWidth * 2;
			endPointX += holeWidth * 2;
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
	
	private static BufferedImage normalize(Image image) {
		 var img = SwingFXUtils.fromFXImage(image, null);
		 if(img == null) return null;
		 return resize(img, photoHeight);
	}
  
	private static BufferedImage resize(BufferedImage originalImage, int height) {
 		var width = height  * originalImage.getWidth() / originalImage.getHeight();
		var resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		var g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();

		g.setComposite(AlphaComposite.Src);

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		return resizedImage;
	}
}
