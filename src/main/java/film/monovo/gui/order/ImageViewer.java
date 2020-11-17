package film.monovo.gui.order;


import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ImageViewer extends VBox {
	
	public final Image originalImage;
	public final ImageViewerDialog parent;
	
	public ImageViewer(Image image, ImageViewerDialog parent) {
		this.parent = parent;
		this.originalImage = image;
		this.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		this.setSpacing(3);
		var view = new ImageView(image);
		view.setPreserveRatio(true);
		view.setFitHeight(200);
		this.getChildren().add(view);
		var box = new HBox();
		var moveLeft = new Button("<");
		var moveRight = new Button(">");
		var remove = new Button("x");
		box.getChildren().add(moveLeft);
		box.getChildren().add(moveRight);
		box.getChildren().add(remove);
		this.getChildren().add(box);
		
		
		moveLeft.setOnAction( b -> {parent.moveLeft(this);});
		moveRight.setOnAction(b -> {parent.moveRight(this);});
		remove.setOnAction(b -> {parent.remove(this);});
	
	}

}
