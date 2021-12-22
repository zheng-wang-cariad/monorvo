package film.monorvo.graphic;

import film.monorvo.barcode.Code128;
import film.monorvo.manager.FileManager;
import film.monorvo.manager.order.Order;
import javafx.scene.control.Alert;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FilmMerger {
    private List<Order> orders;
    private final static int PAPER_WIDTH_MM_A3PLUS = 690;
    private final static int PAPER_LENGTH_MM_A3PLUS = 329;
    private final static int PAPER_WIDTH_MM_A3 = 420;
    private final static int PAPER_LENGTH_MM_A3 = 297;
    private final static int GAP_MM = 5;
    private final static int MARGIN_MM_A3PLUS = 18;
    private final static int MARGIN_MM_A3 = 10;
    private final static int FRAME_INDENTATION = 10;

    public final int indentation = Resolution.toPixel(FRAME_INDENTATION);
    private final int totalWidthA3Plus = Resolution.toPixel(PAPER_WIDTH_MM_A3PLUS);
    private final int totalLengthA3Plus = Resolution.toPixel(PAPER_LENGTH_MM_A3PLUS);
    private final int totalLengthA3 = Resolution.toPixel(PAPER_LENGTH_MM_A3);
    private final int totalWidthA3 = Resolution.toPixel(PAPER_WIDTH_MM_A3);
    public final int gap = Resolution.toPixel(GAP_MM);
    public final int marginA3Plus = Resolution.toPixel(MARGIN_MM_A3PLUS);
    public final int marginA3 = Resolution.toPixel(MARGIN_MM_A3);
    public final int yAdjust = Resolution.toPixel(1);
    public Optional<String> filePath = Optional.empty();

    public BufferedImage img;



    public FilmMerger(List<Order> orders, Paper paper) {
        this.orders = orders;
        this.draw(paper);
    }

    private void draw(Paper paper){
        switch (paper){
            case A3: drawA3();
                break;
            case A3PLUS:
                drawA3plus();
                break;
        }
    }

    private void drawA3() {
        try {
            img = new BufferedImage(totalWidthA3, totalLengthA3, BufferedImage.TYPE_INT_RGB);
            var canvas = (Graphics2D) img.getGraphics();
            canvas.setColor(Color.WHITE);
            canvas.fill(new Rectangle(0, 0, totalWidthA3, totalLengthA3));
            var yGap = marginA3Plus;
            for (Order order : orders) {
                var toMerge = ImageIO.read(new File(FileManager.getMergedFilePath(order.id)));
                canvas.drawImage(toMerge, indentation, yGap, null);
                drawReferenceLine(canvas, indentation, yGap, toMerge.getWidth());
                //drawOrderId(canvas, toMerge.getWidth(), yGap, order.id);
                drawOrderBarcode(canvas, toMerge.getWidth(), yGap, order.id);
                yGap += gap + toMerge.getHeight();
            }
            var path = "deletable" + UUID.randomUUID().toString() + ".jpg";
            this.filePath = Optional.of(FileManager.persistImage(path, this.img));
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("failed to merge images");
            alert.showAndWait();
        }
    }

    private void drawA3plus() {
        try {
            img = new BufferedImage(totalWidthA3Plus, totalLengthA3Plus, BufferedImage.TYPE_INT_RGB);
            var canvas = (Graphics2D) img.getGraphics();
            canvas.setColor(Color.WHITE);
            canvas.fill(new Rectangle(0, 0, totalWidthA3Plus, totalLengthA3Plus));
            var yGap = marginA3Plus;
            for (Order order : orders) {
                var toMerge = ImageIO.read(new File(FileManager.getMergedFilePath(order.id)));
                canvas.drawImage(toMerge, indentation, yGap, null);
                drawReferenceLine(canvas, indentation, yGap, toMerge.getWidth());
                //drawOrderId(canvas, toMerge.getWidth(), yGap, order.id);
                drawOrderBarcode(canvas, toMerge.getWidth(), yGap, order.id);
                yGap += gap + toMerge.getHeight();
            }
            var path = "deletable" + UUID.randomUUID().toString() + ".jpg";
            this.filePath = Optional.of(FileManager.persistImage(path, this.img));
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("failed to merge images");
            alert.showAndWait();
        }
    }

//    private void drawOrderId(Graphics2D canvas, int x, int y, String id) {
//        Font font = new Font(null, Font.PLAIN, 80);
//        AffineTransform affineTransform = new AffineTransform();
//        affineTransform.rotate(Math.toRadians(90), 0, 0);
//        Font rotatedFont = font.deriveFont(affineTransform);
//        canvas.setFont(rotatedFont);
//        canvas.setColor(Color.RED);
//        canvas.drawString(id, x + 300, y + 100);
//    }

    private void drawOrderBarcode(Graphics2D canvas, int x, int y, String id) {
        var image = Code128.createBarcode(id);
        canvas.drawImage(image, x + 290, y + 200, null);
    }

    private void drawReferenceLine(Graphics2D canvas, int x, int y, int imageWidth) {
        canvas.setColor(Color.black);
        var xOffset = Resolution.toPixel(2);

        canvas.fill(new Rectangle(xOffset, y + FilmStyle.beltVerticalOffset + yAdjust - 3, 120, 3));
        canvas.fill(new Rectangle(x + xOffset + imageWidth, y + FilmStyle.beltVerticalOffset + yAdjust - 3, 80, 3));
        canvas.fill(new Rectangle(xOffset, y + FilmStyle.beltVerticalOffset + FilmStyle.beltHeight + 3 - yAdjust, 120, 3));
        canvas.fill(new Rectangle(x + xOffset + imageWidth, y + FilmStyle.beltVerticalOffset + FilmStyle.beltHeight + 3 - yAdjust, 80, 3));

    }

}