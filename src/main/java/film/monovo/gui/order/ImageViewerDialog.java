package film.monovo.gui.order;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import film.monovo.graphic.FilmStyle;
import film.monovo.manager.FileManager;
import film.monovo.manager.order.OrderStatus;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ImageViewerDialog extends Dialog<Integer> {
	public VBox box = new VBox();
	public HBox imagesBox = new HBox();
	public ScrollPane sp = new ScrollPane();
	public ScrollPane mergedBuildSp = new ScrollPane();
	public ArrayList<ImageViewer> image = new ArrayList<>();
	public HBox buttons = new HBox();
	public BufferedImage merged;
	public final OrderInfo orderInfo;
	public final String orderId;
	public Button saveAs;
	public Button save;
	
	public ImageViewerDialog(String orderId, OrderInfo orderInfo) {
		this.orderInfo = orderInfo;
		this.orderId = orderId;
		
		var images = FileManager.readAllImage(orderId);
		images.stream().map( it -> { return new Image(it); })
			.forEach(it-> { image.add( new ImageViewer(it, this));});
		for(ImageViewer i: image) {
			imagesBox.getChildren().add(i);
		}
		
		sp.setContent(imagesBox);
		imagesBox.setPrefHeight(280);
		box.getChildren().add(sp);
		box.getChildren().add(buttons);
		
		createMergeButton();
		createSaveAsButton();
		createSaveButton();
		
		this.getDialogPane().setContent(box);
		this.getDialogPane().setPrefSize(1000, 800);
		
		ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().add(buttonTypeOk);
		this.setResultConverter(b -> {
			if(b == buttonTypeOk) {
				return 1;
			}
				return 2;
			}
		);
	}
	
	private void createSaveAsButton() {
		this.saveAs = new Button("Sava As");

		saveAs.setDisable(true);
		buttons.getChildren().add(saveAs);
		saveAs.setOnAction(action -> {
			FileChooser c = new FileChooser();
			c.getExtensionFilters().add(new FileChooser.ExtensionFilter("picture doc(*.jpg)", "*.jpg"));
			File selectedFile = c.showSaveDialog(orderInfo.gui.stage);
			try {
				ImageIO.write(this.merged, "jpg", selectedFile);
			} catch (IOException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Failed to save file");
				alert.showAndWait();
			}
		});
		
	}
	
	private void createSaveButton() {
		save = new Button("Save");
		buttons.getChildren().add(save);
		save.setDisable(true);
		save.setOnAction(action -> {
			var path = FileManager.getMergedFilePath(orderId);
			FileManager.ensureFile(path);
			try {
				ImageIO.write(this.merged, "jpg", new File(path));
				orderInfo.setOrderStatus(OrderStatus.PROCESSED);
			} catch (IOException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Failed to save file");
				alert.showAndWait();
			}
			this.close();
		});
		
	}

	public void createMergeButton() {
		Button b = new Button("merge");
		buttons.getChildren().add(b);
		b.setOnAction(action -> {
			var imgs = this.image.stream().map( it -> { return it.originalImage; }).collect(Collectors.toList());
			this.merged = FilmStyle.build(imgs);
			var imageView = new ImageView(SwingFXUtils.toFXImage(merged, null));
			this.mergedBuildSp.setContent(imageView);
			box.getChildren().clear();
			box.getChildren().add(sp);
			box.getChildren().add(mergedBuildSp);
			box.getChildren().add(buttons);
			this.getDialogPane().setContent(box);
			save.setDisable(false);
			saveAs.setDisable(false);
		});
	}

	public void moveLeft(ImageViewer imageViewer) {
		var i = image.indexOf(imageViewer);
		if(i > 0) {
			image.remove(i);
			image.add(i - 1, imageViewer);
			redraw();
		}
	}

	private void redraw() {
		this.imagesBox.getChildren().clear();
		for(ImageViewer i : image) {
			imagesBox.getChildren().add(i);
		}
	}

	public void moveRight(ImageViewer imageViewer) {
		var i = image.indexOf(imageViewer);
		if(i != image.size() - 1) {
			image.remove(i);
			image.add(i + 1, imageViewer);
			redraw();	
		}
	}

	public void remove(ImageViewer imageViewer) {
		image.remove(imageViewer);
		redraw();
	}
}
