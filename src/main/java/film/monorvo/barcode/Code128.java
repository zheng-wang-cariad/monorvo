package film.monorvo.barcode;

import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import java.awt.image.BufferedImage;

public class Code128 {

    public static BufferedImage createBarcode(String value) {
        var bean = new Code128Bean();

        bean.setHeight(10d);
        bean.doQuietZone(false);

        BitmapCanvasProvider provider = new BitmapCanvasProvider(600, BufferedImage.TYPE_BYTE_GRAY, false, 0);
        bean.generateBarcode(provider, value);

        try {
            provider.finish();
            var image = provider.getBufferedImage();
            return rotate(image, false);
        } catch (Exception e) {
            throw new RuntimeException("failed to generate barcode");
        }
    }

    private static BufferedImage rotate(BufferedImage input, boolean b) {
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
            return target;
    }
}
