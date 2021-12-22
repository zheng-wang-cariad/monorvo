package film.monorvo.gui.event;

import java.io.FileInputStream;

import film.monorvo.manager.FileManager;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class ImageBox extends HBox {
	
	public void addImage(String path) {
		try {
			System.out.println("read image" + path);
			FileManager.ensureFile(path);
			var input = new FileInputStream(path);
			var image = new Image(input);
			var view = new ImageView(image);
			view.setPreserveRatio(true);
			view.setFitHeight(200);
			this.getChildren().add(view);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
}


