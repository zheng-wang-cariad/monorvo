package film.monovo.gui.order;

import film.monovo.manager.FileManager;
import film.monovo.manager.OrderManager;
import film.monovo.manager.order.Order;
import film.monovo.manager.order.OrderStatus;
import film.monovo.util.BerlinerTime;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OrderInfo extends VBox{
	
	private Order order;
	
	private final Label id = new Label();
	private final TextField street = new TextField();
	private final TextField houseNumber = new TextField();
	private final TextField name = new TextField();
	private final TextField additional = new TextField();
	private final TextField city = new TextField();
	private final TextField postCode = new TextField();
	private final TextField country = new TextField();
	private final TextField email = new TextField();
	private final GridPane grid = new GridPane();
	private final Label timestamp = new Label("");
	private final CheckBox enableEdition = new CheckBox("Enable Modification");
	private final Button saveOrder = new Button("save");
	private final Label statusLabel = new Label("");
	private final Label totalImages = new Label("");
	private final OrderManager orderManager;
	protected final OrderGUI gui;
	private ComboBox<String> combo;
	
	public OrderInfo(Order order, OrderGUI gui, OrderManager orderManager) {
		this.orderManager = orderManager;
		this.gui = gui;
		this.order = order;

		createOrderInfoPane();
		this.getChildren().add(grid);
		this.getChildren().add(enableEdition);
		this.getChildren().add(saveOrder);
		this.getChildren().add(createImageBox());
		createStatusController();
		
		setEditable(false);
		refresh(order);
		setupSaves();
	}

	private void createStatusController() {
		var box = new HBox();
		this.getChildren().add(box);
		box.getChildren().add(new Label("status:  "));
		ObservableList<String> list = FXCollections.observableArrayList();
		for(OrderStatus o :OrderStatus.values()) {
			list.add(o.name());
		}
		this.combo = new ComboBox<>(list);
		combo.setOnAction(e -> {
			if(this.order != null && ! combo.getValue().equals(this.order.status.name())){
				var newStatus = OrderStatus.valueOf((String) combo.getValue());
				this.setOrderStatus(newStatus);
			}
		});
		combo.getSelectionModel().select(this.order.status.name());
		box.getChildren().add(combo);
	}
	
	private HBox createImageBox() {
		var box = new HBox();
		int numberOfImage = FileManager.getImageNumber(order.id);
		totalImages.setText("total " + numberOfImage + " images");
		box.getChildren().add(totalImages);
		Button viewImage = new Button("View Images");
		box.getChildren().add(viewImage);
		viewImage.setOnAction(b -> {
			ImageViewerDialog dialog = new ImageViewerDialog(order.id, this);
			dialog.showAndWait();
			gui.updateOrderStatus(order);
		});
		return box;
	}
	
	private void setupSaves() {
		enableEdition.setSelected(false);
		saveOrder.setDisable(true);
		enableEdition.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		        setEditable(newValue);
		    	saveOrder.setDisable(! newValue);
		    }
		});
		
		saveOrder.setOnAction(b -> {
			this.saveOrderValue(order);
			FileManager.persist(order);
			setEditable(false);
			saveOrder.setDisable(true);
			enableEdition.setSelected(false);
		});
	}

	public void createOrderInfoPane() {
		grid.add(new Label("id: "), 1, 1);
		grid.add(id, 2, 1);
		
		grid.add(new Label("order started: "), 1, 2);
		grid.add(timestamp, 2, 2);
		
		grid.add(new Label("name:"), 1, 3);
		this.add(name, 2, 3, 400);
		
		grid.add(new Label("Street/No:"), 1, 4);
		this.add(street, 2, 4, 400);
		this.add(houseNumber, 3, 4, 60);
		
		grid.add(new Label("additional address:"), 1, 5);
		this.add(additional, 2, 5, 400);
		
		grid.add(new Label("City/PostCode:"), 1, 6);
		this.add(postCode, 2, 6, 100);
		this.add(city, 3, 6, 300);
		
		grid.add(new Label("Country:"), 1, 7);
		this.add(country, 2, 7, 100);
		
		grid.add(new Label("email:"), 1, 8);
		this.add(email, 2, 8, 400);
	}
	
	protected void refresh(Order order) {
		this.order = order;
		this.id.setText(order.id);
		this.street.setText(order.customer.streetName);
		this.houseNumber.setText(order.customer.houseNumber);
		this.name.setText(order.customer.name);
		this.additional.setText(order.customer.addressAdditional);
		this.city.setText(order.customer.city);
		this.postCode.setText(order.customer.postCode);
		this.country.setText(order.customer.country);
		this.email.setText(order.customer.email);
		this.timestamp.setText(BerlinerTime.toTimeString(order.timestamp));
		this.combo.getSelectionModel().select(order.status.name());
		int numberOfImage = FileManager.getImageNumber(order.id);
		totalImages.setText("total " + numberOfImage + " images");
	}
	
	private void saveOrderValue(Order order) {
		order.customer.streetName = this.street.getText();
		order.customer.houseNumber = this.houseNumber.getText();
		order.customer.name = this.name.getText();
		order.customer.addressAdditional = this.additional.getText();
		order.customer.city = this.city.getText();
		order.customer.postCode = this.postCode.getText();
		order.customer.country = this.country.getText();
		order.customer.email = this.email.getText();
		FileManager.persist(order);
	}
	
	private void add(Control c, int x, int y, int width) {
		c.setPrefWidth(width);
		grid.add(c, x, y);
	}
	
	private void setEditable(boolean enabled) {
		
		this.street.setEditable(enabled);
		this.houseNumber.setEditable(enabled);
		this.name.setEditable(enabled);
		this.additional.setEditable(enabled);
		this.city.setEditable(enabled);
		this.postCode.setEditable(enabled);
		this.country.setEditable(enabled);
		this.email.setEditable(enabled);
	}

	public void setOrderStatus(OrderStatus status) {
		this.orderManager.updateOrderStatus(this.order, status);
		gui.updateOrderStatus(this.order);
	}
	
	
}
