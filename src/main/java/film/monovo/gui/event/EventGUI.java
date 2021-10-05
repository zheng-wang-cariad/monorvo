package film.monovo.gui.event;

import film.monovo.exception.UserAwaringException;
import film.monovo.manager.EventManager;
import film.monovo.manager.FileManager;
import film.monovo.manager.event.Event;
import film.monovo.manager.event.EventChain;
import film.monovo.manager.order.Order;
import film.monovo.util.IconButton;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;

public class EventGUI {
	protected final EventManager manager;
	protected final BorderPane layout;
	private VBox eventInfo = new VBox();
	protected AllImageBox allImages = new AllImageBox();
	//	private final ListView<EventHeader> eventList = new ListView<EventHeader>();
	//	public final ObservableList<EventHeader> events = FXCollections.observableArrayList();
	public EventView eventView = new EventView(this);

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
		box.getChildren().add(initSyncButton());
		box.getChildren().add(initCreateOrderButton());
		box.getChildren().add(initImportPicButton());
		this.layout.setBottom(box);
	}

	private Button initImportPicButton() {
		var importToOrder = new Button();
		IconButton.setIconOrText(importToOrder, "classpath:import.png", "import to an order");
		importToOrder.setOnAction(a -> {
			ImportDialog orderDialog = new ImportDialog(eventView.selectedItem().chain, this.manager, this);
			var result = orderDialog.showAndWait();
			if(result.isPresent() && orderDialog.orderId.isPresent()) {
				result.get().entrySet().stream().filter(Map.Entry::getValue)
						.forEach(it -> FileManager.copyTempImageFileToOrderFolder(orderDialog.orderId.get(), it.getKey()));
				eventView.updateCurrentEventCreated();
			}
		});
		return importToOrder;
	}

	private Button initCreateOrderButton() {
		var create = new Button();
		create.setOnAction(v -> {
			Dialog<Order> orderDialog = new OrderDialog(eventView.selectedItem().chain, this);
			var result = orderDialog.showAndWait();
			result.ifPresent(this::createNewOrder);
		});
		IconButton.setIconOrText(create, "classpath:create.jpg", "Create Order");
		return create;
	}

	private Button initSyncButton() {
		Button sync = new Button();
		sync.setOnAction(action -> {
			this.eventView.search.clear();
			this.updateEventList(true);
		});
		IconButton.setIconOrText(sync, "classpath:sync.jpg", "Sync");
		return sync;
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
		this.layout.setLeft(eventView);
		this.layout.getLeft().prefWidth(300);
	}

	public void updateEventList() {
		updateEventList(false);
	}

	private void updateEventList(boolean forceEnrich) {
		List<EventChain> list = manager.getSortedEventChain();
		var focus = this.eventView.updateList(list);
		selectIndex(focus, forceEnrich);
	}

	private void selectIndex(int index, boolean forceEnrich) {
		var chain = this.eventView.selectIndex(index);

		if(forceEnrich) {
			FileManager.cleanChainFolder(chain.event.uid);
			chain.events.stream().forEach(it -> it.enriched = false);
		}

		if(chain != null) {
			manager.enrich(chain);
			updateGUI(chain);
		}
	}

	public void selectIndex(int index) {
		selectIndex(index, false);
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
		this.layout.getChildren().remove(this.allImages);
		allImages = new AllImageBox();
		ScrollPane sp = new ScrollPane();
		sp.setContent(allImages);
		allImages.loadChain(chain);
		this.layout.setRight(sp);
	}

	public void deleteEvent(EventHeader eventHeader) {
		this.eventView.deleteEvent(eventHeader);
	}
}
