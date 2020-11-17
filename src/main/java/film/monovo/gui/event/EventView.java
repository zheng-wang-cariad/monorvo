package film.monovo.gui.event;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class EventView extends VBox {
    private final ListView<EventHeader> eventList = new ListView<EventHeader>();
    public final ObservableList<EventHeader> events = FXCollections.observableArrayList();

    public EventView() {
        var search = new TextField();
        var checkBox = new CheckBox("include deleted");
        this.getChildren().addAll(search, checkBox, eventList);
    }

    public EventHeader selectedItem() {
        return eventList.getSelectionModel().getSelectedItem();
    }
}
