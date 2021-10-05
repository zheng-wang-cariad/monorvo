package film.monovo.gui.event;

import film.monovo.manager.FileManager;
import film.monovo.manager.event.EventChain;
import film.monovo.util.IconButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class EventView extends VBox {
    private final ListView<EventHeader> eventList = new ListView<EventHeader>();
    public final ObservableList<EventHeader> events = FXCollections.observableArrayList();
    private final EventGUI gui;
    private final CheckBox checkBox = new CheckBox("include deleted");
    public TextField search = new TextField();
    public EventView(EventGUI gui) {
        this.gui = gui;

        var searchBox = new HBox();
        var searchButton = new Button();
        IconButton.setIconOrText(searchButton, "classpath:search.png", "search");
        searchButton.setOnAction(action -> updateList());
        searchBox.getChildren().addAll(search, searchButton);

        this.getChildren().addAll(searchBox, checkBox, eventList);
        eventList.setEditable(false);
        eventList.setItems(events);
        eventList.setOnMouseClicked( e-> {
            //selectIndex(eventList.getSelectionModel().getSelectedIndex());
            gui.selectIndex(eventList.getSelectionModel().getSelectedIndex());
        });
    }

    public EventHeader selectedItem() {
        return eventList.getSelectionModel().getSelectedItem();
    }

    public void updateList(){
        if(events.size() > 0) {
            events.clear();
            List<EventChain> list = gui.manager.getAllEventChains();
            list.stream().filter(this::showDeletedItem)
                    .filter(it -> it.containsStr(this.search.getText()))
                    .forEach( it -> events.add(new EventHeader(it, gui)));
            if(events.size() > 0) {
                gui.selectIndex(0);
            }
        }
    }

    public int updateList(List<EventChain> list){
        if(events.size() > 0) {
            var selectedChain = selectedItem().chain;
            events.clear();
            list.stream().filter(this::showDeletedItem)
                    .forEach(it -> events.add(new EventHeader(it, gui)));
            for(int i = 0; i < events.size(); i ++) {
                if (events.get(i).chain.event.getNormalizedUid() == selectedChain.event.getNormalizedUid()) {
                    return i;
                }
            }
        } else {
            list.stream().filter(this::showDeletedItem)
                    .forEach(it -> events.add(new EventHeader(it, gui)));
        }
        return 0;
    }

    public EventChain selectIndex(int index) {
        if(events.size() > index && index >= 0) {
            eventList.getSelectionModel().select(index);
            return eventList.getSelectionModel().getSelectedItem().chain;
        }
        return null;
    }

    public void deleteEvent(EventHeader eventHeader) {
        eventHeader.chain.isDeleted = true;
        this.events.remove(eventHeader);
        FileManager.persistChain(eventHeader.chain.toDto());
    }

    private boolean showDeletedItem(EventChain chain){
        return this.checkBox.isSelected() || !chain.isDeleted;
    }

    public void updateCurrentEventCreated() {
        var header =  this.eventList.getSelectionModel().getSelectedItem();
        header.chain.isImported = true;
        header.update();
        FileManager.persistChain(header.chain.toDto());
    }
}
