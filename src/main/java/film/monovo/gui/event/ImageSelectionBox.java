package film.monovo.gui.event;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import film.monovo.manager.FileManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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

import javax.imageio.ImageIO;

public class ImageSelectionBox extends VBox{
	
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
			loadImageToBox(filePath);

		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
