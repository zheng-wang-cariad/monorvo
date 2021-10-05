package film.monovo;

import film.monovo.graphic.Resolution;
import film.monovo.gui.Root;
import film.monovo.util.Compressor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();
        var root = new Root(stage);
        var vBox = new VBox(root.tabs);
        var scene = new Scene(vBox, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
//        var width = Resolution.toPixel(297.0);
//        System.out.println(width);
//        var height = Resolution.toPixel(420.0);
//        System.out.println(height);


        launch();
    }

}