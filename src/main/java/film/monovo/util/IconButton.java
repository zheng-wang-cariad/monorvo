package film.monovo.util;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;

public class IconButton {
    public static void setIconOrText(Button b, String classPath, String text) {
        setIconOrText(b, classPath, text, 30);
    }

    public static void setIconOrText(Button b, String classPath, String text, int size) {
        try{
            Image imageSync = new Image(new FileInputStream(ResourceUtils.getFile(classPath)));
            ImageView syncIcon = new ImageView(imageSync);
            syncIcon.setFitHeight(size);
            syncIcon.setPreserveRatio(true);
            b.setGraphic(syncIcon);
        } catch (Exception e) {
            b.setText(text);
        }
    }
}
