package film.monovo.gui.order;

import film.monovo.graphic.FilmStyle;
import film.monovo.manager.FileManager;
import film.monovo.manager.order.OrderStatus;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
		var orderImages = new OrderedImages(images);
		for(String path: orderImages.sortedPath) {
			try {
				image.add(new ImageViewer(path, this, orderImages.isSorted));
			} catch (FileNotFoundException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText(e.getMessage());
				alert.showAndWait();
			}
		}
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
			var imgs = this.image.stream().map( it -> it.path).collect(Collectors.toList());
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


	private class OrderedImages {
		public boolean isSorted;
		public List<String> sortedPath;

		public OrderedImages(List<String> paths) {
			try {
				var indexedPathes = new ArrayList<IndexedPath>();
				for (String path : paths) {
					var f = new File(path);
					var numberInString = f.getName().split("\\.")[0].replaceAll("[^0-9]", "");
					var orderNumber = Integer.parseInt(numberInString);
					indexedPathes.add(new IndexedPath(orderNumber, path));
				}
				var sorted = indexedPathes.stream().sorted()
						.map(it -> it.path)
						.collect(Collectors.toList());
				this.isSorted = true;
				this.sortedPath = sorted;
			} catch(Exception e) {
				this.isSorted = false;
				this.sortedPath = paths;
			}
		}
	}

	public class IndexedPath implements Comparable<IndexedPath>{
		public int orderNumber;
		public String path;

		public IndexedPath(int orderNumber, String path) {
			this.orderNumber = orderNumber;
			this.path = path;
		}

		public int getOrderNumber() {
			return orderNumber;
		}

		@Override
		public int compareTo(IndexedPath o) {
			return Integer.compare(this.orderNumber, o.orderNumber);
		}
	}
}
