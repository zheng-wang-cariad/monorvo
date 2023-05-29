package film.monorvo.graphic;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class JPGRotator {
    public static void rotate(String filePath, boolean b) {
        try {
            var input = ImageIO.read(new File(filePath));
            var height = input.getHeight();
            var width = input.getWidth();
            var target = new BufferedImage(input.getHeight(), input.getWidth(), input.getType());
            var graphics2D = target.createGraphics();
            double theta;
            if (b) {
                theta = (Math.PI * 2) / 4;
                graphics2D.translate((height - width) / 2, (height - width) / 2);
            } else {
                theta = (Math.PI * 2) * 3 / 4;
                graphics2D.translate((width - height) / 2, (width - height) / 2);
            }
            graphics2D.rotate(theta, ((double) height) / 2, ((double) width) / 2);
            graphics2D.drawRenderedImage(input, null);
            ImageIO.write(target, "jpg", new File(filePath));
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
