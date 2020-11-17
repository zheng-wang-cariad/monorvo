package film.monovo.gui.order;

import film.monovo.graphic.FilmMerger;
import film.monovo.graphic.Resolution;
import film.monovo.manager.FileManager;
import film.monovo.manager.order.Order;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

public class BatchInfo extends VBox {
    private final ArrayList<OrderHeader> batch = new ArrayList<>();
    private final HashMap<OrderHeader, BufferedImage> images = new HashMap<>();
    private final Label total = new Label("0 / 6 orders in batch");
    private final int max = 6;
    private final HBox buttons = new HBox();
    private final Stage stage;

    public BatchInfo(OrderGUI gui) {
        this.stage = gui.stage;
        this.getChildren().add(total);
        initButtons();
    }

    private void initButtons() {
        var merge = new Button("build");
        var clear = new Button("clear");

        buttons.getChildren().add(merge);
        buttons.getChildren().add(clear);

        merge.setOnAction(action -> {
            var order = batch.stream().map (it -> it.order ).collect(Collectors.toList());
            FilmMerger merger = new FilmMerger(order);
            if(merger.filePath.isPresent()) {
                try {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    var input = new FileInputStream(merger.filePath.get());
                    var image = new Image(input);
                    var view = new ImageView(image);
                    var sp = new ScrollPane(view);
                    alert.getDialogPane().setContent(sp);
                    alert.setWidth(1000);
                    alert.setHeight(800);
                    ButtonType save = new ButtonType("save");
                    ButtonType cancel = new ButtonType("cancel");
                    alert.getButtonTypes().setAll(save, cancel);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == save){
                        FileChooser c = new FileChooser();
                        c.getExtensionFilters().add(new FileChooser.ExtensionFilter("picture doc(*.jpg)", "*.jpg"));
                        File selectedFile = c.showSaveDialog(this.stage);
                        new File (merger.filePath.get()).renameTo(selectedFile);
                    } else if (result.get() == cancel) {
                        //DO NOTHING
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }

        });

        clear.setOnAction(action -> {
            this.getChildren().clear();
            this.getChildren().add(total);
            this.images.clear();
            this.batch.clear();
        });
    }

    public void removeOrderHeader(OrderHeader orderHeader) {
        this.batch.remove(orderHeader);
        this.images.remove(orderHeader);
        this.getChildren().clear();
        this.getChildren().add(total);
        for (OrderHeader o: batch) {
            this.getChildren().add(createBox(o));
        }

        if(!batch.isEmpty()) this.getChildren().add(buttons);
        this.total.setText(this.batch.size() + "/" + max + " in batch");
    }

    public void addNewOrderHeader(OrderHeader orderHeader) {
        try {
            if(batch.size() >= 6) {
                throw new RuntimeException("batch is full");
            }

            batch.add(orderHeader);
            var b = createBox(orderHeader);
            if(b == null) return;
            this.getChildren().remove(buttons);
            this.getChildren().add(b);
            this.getChildren().add(buttons);

            this.total.setText(this.batch.size() + "/" + max + " in batch");


        } catch (Exception e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private HBox createBox(OrderHeader orderHeader) {
        var box = new HBox();
        var image = getMergedImage(orderHeader.order);
        if(image == null) return null;
        this.images.put(orderHeader, image);
        box.getChildren().add(new Label("Size: " + Resolution.toMillimeter(image.getWidth()) +
                "mm x " + Resolution.toMillimeter(image.getHeight()) + "mm"));
        var button = new Button("remove");
        box.getChildren().add(button);
        button.setOnAction(action -> {
            orderHeader.check.setSelected(false);
            this.getChildren().remove(box);
            removeOrderHeader(orderHeader);
        });
        return box;
    }

    private BufferedImage getMergedImage(Order order) {
        try {
            var path = FileManager.getMergedFilePath(order.id);
            return ImageIO.read(new File(path));
        } catch(Exception e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Picture can not be read");
            alert.showAndWait();
            return null;
        }
    }
}
