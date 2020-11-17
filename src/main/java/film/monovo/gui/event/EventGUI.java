package film.monovo.gui.event;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import film.monovo.exception.UserAwaringException;
import film.monovo.manager.EventManager;
import film.monovo.manager.FileManager;
import film.monovo.manager.event.EventChain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import film.monovo.manager.event.Event;
import film.monovo.manager.order.Order;

public class EventGUI {
	protected final EventManager manager;
	protected final BorderPane layout;
	private VBox eventInfo = new VBox();
	private AllImageBox allImages = new AllImageBox();
	private final ListView<EventHeader> eventList = new ListView<EventHeader>();
	public final ObservableList<EventHeader> events = FXCollections.observableArrayList();


	public EventGUI(EventManager manager) {
		this.manager = manager;
		this.layout = manager.pane;

		initialLayout();
		updateEventList();
		createButtons();

	}
	
	private void createButtons() {
		var box = new HBox();
		box.prefHeight(70);
		var create = new Button("create an order");
		create.setOnAction(v -> {
			Dialog<Order> orderDialog = new OrderDialog();
			var result = orderDialog.showAndWait();
			if(result.isPresent()) createNewOrder(result.get());
		});
		box.getChildren().add(create);

		var importToOrder = new Button("import to an order");
		importToOrder.setOnAction(a -> {
			ImportDialog orderDialog = new ImportDialog(eventList.getSelectionModel().getSelectedItem().chain, this.manager);
			var result = orderDialog.showAndWait();
			if(result.isPresent() && orderDialog.orderId.isPresent()) {
				result.get().entrySet().stream().filter(it -> !it.getValue())
						.forEach(it -> FileManager.copyTempImageFileToOrderFolder(orderDialog.orderId.get(), it.getKey()));
			}
		});
		box.getChildren().add(importToOrder);

		this.layout.setBottom(box);

	}

	private void createNewOrder(Order order) {
		try {
			this.manager.managers.orderManager.addNewOrder(order);
		} catch (UserAwaringException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(e.getMessage());
			alert.showAndWait();
		}
	}

	private void initialLayout() {
		this.layout.setLeft(eventList);
		this.layout.getLeft().prefWidth(300);
		eventList.setEditable(false);
		eventList.setItems(events);
		eventList.setOnMouseClicked( e-> {
			selectIndex(getIndex());
		});
	}

	public void updateEventList() {
		List<EventChain> list = manager.refreshFolder();
		if(events.size() > 0) {
			var selectedChain = eventList.getSelectionModel().getSelectedItem().chain;
			events.clear();
			list.stream().filter(it-> !it.isDeleted)
					.forEach(it -> {
						events.add(new EventHeader(it, this));
						System.out.println(it.isDeleted);
					});
			for(int i = 0; i < list.size(); i ++) {
				if(list.get(i).event.getNormalizedUid() == selectedChain.event.getNormalizedUid()) {
					selectIndex(i);
					break;
				}
			}
		} else {
			list.stream().filter(it-> !it.isDeleted)
					.forEach(it -> {
						events.add(new EventHeader(it, this));
						System.out.println(it.isDeleted);
					});
			selectIndex(0);
		}
	}
	
	public int getIndex() {
		return eventList.getSelectionModel().getSelectedIndex();
	}
	
	public void selectIndex(int index) {
		
		if(events.size() > index && index >= 0) {
			
			eventList.getSelectionModel().select(index);
			var chain = eventList.getSelectionModel().getSelectedItem().chain;
			manager.enrich(chain);
			updateGUI(chain);
			
		}
	}
	
	private void updateGUI(EventChain chain) {
		updateChainInfo(chain);
		updateOrderImages(chain);
	}

	private void updateChainInfo(EventChain chain) {
		
		this.layout.getChildren().remove(this.eventInfo);
		eventInfo = new VBox();
		ScrollPane sp = new ScrollPane();
		sp.setContent(eventInfo);
		//eventInfo.setFillWidth(true);
		this.layout.setCenter(sp);
		
		chain.events.sort((Event a, Event b)-> (int) (a.timestamp - b.timestamp));
		chain.events.stream()
			.forEach ( e -> {
							var eventBox = new EventBox(e);
							eventInfo.getChildren().add(eventBox);
						});
	}
	
	private void updateOrderImages(EventChain chain) {
		System.out.println(12);
		this.layout.getChildren().remove(this.allImages);
		allImages = new AllImageBox();
		ScrollPane sp = new ScrollPane();
		sp.setContent(allImages);
		allImages.loadChain(chain);
		this.layout.setRight(sp);
	}

	public void deleteEvent(EventHeader eventHeader) {
		eventHeader.chain.isDeleted = true;
		this.events.remove(eventHeader);
		FileManager.persistChain(eventHeader.chain.toDto());
	}
}
