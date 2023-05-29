package film.monorvo.gui.order;


import film.monorvo.graphic.JPGRotator;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageViewer extends VBox {
	public final String path;
	public Image originalImage;
	public final ImageViewerDialog parent;
	private final HBox box;
	public ImageView view;
	public TextField input;
	public Integer index;

	public ImageViewer(String path, ImageViewerDialog parent, boolean ordered) throws FileNotFoundException {
		this.path = path;
		this.parent = parent;

		this.originalImage = readImage(path);
		this.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		this.setSpacing(3);
		setupView();
		this.box = setupBox(parent, ordered);
		this.getChildren().add(box);
	}

	private HBox setupBox(ImageViewerDialog parent, boolean ordered) {
		var box = new HBox();
		var moveLeft = new Button("<");
		moveLeft.setDisable(ordered);
		var moveRight = new Button(">");
		moveRight.setDisable(ordered);
		var remove = new Button("x");
		var rotate = new Button("r");
		input = new TextField("");
		input.setDisable(ordered);
		box.getChildren().add(rotate);
		box.getChildren().add(input);
		box.getChildren().add(moveLeft);
		box.getChildren().add(moveRight);
		box.getChildren().add(remove);

		input.textProperty().addListener((observable, oldValue, newValue) -> {parent.adjustOrder(this);});
		rotate.setOnAction( b-> rotate());
		moveLeft.setOnAction( b -> parent.moveLeft(this));
		moveRight.setOnAction(b -> parent.moveRight(this));
		remove.setOnAction(b -> parent.remove(this));
		return box;
	}

	private void setupView() throws FileNotFoundException {
		this.originalImage = readImage(path);
		this.view = new ImageView(originalImage);
		view.setPreserveRatio(true);
		view.setFitHeight(200);
		this.getChildren().add(view);
	}

	private void rotate() {
		try {
			JPGRotator.rotate(path, true);
			resetViewer();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void resetViewer() throws FileNotFoundException {
		this.getChildren().remove(view);
		this.getChildren().remove(box);
		setupView();
		this.getChildren().add(box);
	}

	public Image readImage(String path) throws FileNotFoundException {
			return new Image (new FileInputStream(new File(path)));
	}

}
