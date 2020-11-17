package film.monovo.graphic;

import film.monovo.manager.FileManager;
import film.monovo.manager.order.Order;
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
    private final static int PAPER_WIDTH_MM = 690;
    private final static int PAPER_LENGTH_MM = 330;
    private final static int GAP_MM = 10;
    public final int totalWidth = Resolution.toPixel(PAPER_WIDTH_MM);
    public final int totalLenght = Resolution.toPixel(PAPER_LENGTH_MM);
    public final int gap = Resolution.toPixel(GAP_MM);
    public final int yAdjust = Resolution.toPixel(1);
    public Optional<String> filePath = Optional.empty();

    public final BufferedImage img = new BufferedImage(totalWidth, totalLenght, BufferedImage.TYPE_INT_RGB);

    public FilmMerger(List<Order> orders) {
        this.orders = orders;
        this.draw();
    }

    private void draw() {
        try {
            var canvas = (Graphics2D) img.getGraphics();
            canvas.setColor(Color.WHITE);
            canvas.fill(new Rectangle(0, 0, totalWidth, totalLenght));
            var x = 60;
            var yGap = gap;
            for (Order order : orders) {
                var toMerge = ImageIO.read(new File(FileManager.getMergedFilePath(order.id)));
                canvas.drawImage(toMerge, x, yGap, null);
                drawReferenceLine(canvas, x, yGap, toMerge.getWidth());
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

    private void drawReferenceLine(Graphics2D canvas, int x, int y, int imageWidth) {
        canvas.setColor(Color.black);
        var xOffset = Resolution.toPixel(2);

        canvas.fill(new Rectangle(xOffset, y + FilmStyle.beltVerticalOffset + yAdjust - 3, 30, 3));
        canvas.fill(new Rectangle(x + xOffset + imageWidth, y + FilmStyle.beltVerticalOffset + yAdjust - 3, 80, 3));
        canvas.fill(new Rectangle(xOffset, y + FilmStyle.beltVerticalOffset + FilmStyle.beltHeight + 3 - yAdjust, 30, 3));
        canvas.fill(new Rectangle(x + xOffset + imageWidth, y + FilmStyle.beltVerticalOffset + FilmStyle.beltHeight + 3 - yAdjust, 80, 3));

    }

}