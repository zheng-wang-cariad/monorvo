package film.monorvo.gui.event;

import film.monorvo.graphic.JPGRotator;
import film.monorvo.manager.FileManager;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageSelectionBox extends VBox{
	public int index = -1;
	public final String filePath;
	public final CheckBox checkbox = new CheckBox();
	private final Button rotateL = new Button("<");
	private final Button rotateR = new Button(">");

	private final AllImageBox parent;
	private final ImageView view = new ImageView();

	public ImageSelectionBox(AllImageBox parent, String filePath, boolean checked) {
		this.parent = parent;
		this.filePath = filePath;
		this.setSpacing(3);

		rotateL.setOnAction(a -> rotate(filePath, false));
		rotateR.setOnAction(a -> rotate(filePath, true));

		checkbox.setSelected(checked);
		checkbox.setOnAction(a -> parent.updateLabel());
		try {
			FileManager.ensureFile(filePath);
			loadImageToBox(filePath);
			this.getChildren().add(view);
			
			var box = new HBox();
			box.getChildren().add(checkbox);
			box.getChildren().add(new Label("added to order"));
			box.getChildren().add(rotateL);
			box.getChildren().add(rotateR);
			this.getChildren().add(box);
			
			this.setBorder(new Border(new BorderStroke(Color.BLACK,
					  BorderStrokeStyle.SOLID,
					  CornerRadii.EMPTY, new BorderWidths(1))));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private ImageView loadImageToBox(String filePath) throws FileNotFoundException {
		var input = new FileInputStream(filePath);
		var image = new Image(input);
		view.setImage(image);
		view.setPreserveRatio(true);
		view.setFitHeight(200);
		return view;
	}

	private void rotate(String filePath, boolean b) {
		try {
			JPGRotator.rotate(filePath, b);
			loadImageToBox(filePath);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
