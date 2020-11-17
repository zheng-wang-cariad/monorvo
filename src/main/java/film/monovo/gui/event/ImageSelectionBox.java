package film.monovo.gui.event;

import java.io.FileInputStream;

import film.monovo.manager.FileManager;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ImageSelectionBox extends VBox{
	
	public final String filePath;
	public final CheckBox checkbox = new CheckBox();
	
	public ImageSelectionBox(String filePath, boolean checked) {
		this.filePath = filePath;
		this.setSpacing(3);

		checkbox.setSelected(checked);
		try {
			FileManager.ensureFile(filePath);
			var input = new FileInputStream(filePath);
			var image = new Image(input);
			var view = new ImageView(image);
			view.setPreserveRatio(true);
			view.setFitHeight(200);
			this.getChildren().add(view);
			
			var box = new HBox();
			box.getChildren().add(checkbox);
			box.getChildren().add(new Label("added to order"));
			
			this.getChildren().add(box);
			
			this.setBorder(new Border(new BorderStroke(Color.BLACK,
					  BorderStrokeStyle.SOLID,
					  CornerRadii.EMPTY, new BorderWidths(1))));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
