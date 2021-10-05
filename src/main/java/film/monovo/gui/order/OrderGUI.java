package film.monovo.gui.order;

import film.monovo.manager.OrderManager;
import film.monovo.manager.order.Order;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class OrderGUI extends BorderPane {

	private final OrderSelectionBox orderSelectionBox = new OrderSelectionBox(this);
	protected OrderInfo orderInfo;
	private final BatchInfo batchInfo = new BatchInfo(this);
	private final HBox buttons = new HBox();
//	private final ListView<OrderHeader> orderList = new ListView<>();
//	private final HashMap<Order, OrderHeader> headers = new HashMap<>();
//	public final ObservableList<OrderHeader> orderHeaders = FXCollections.observableArrayList();
	protected final Stage stage;

	public OrderGUI(Stage stage) {
		this.stage = stage;
		this.setLeft(orderSelectionBox);
		this.setRight(batchInfo);
		this.setBottom(buttons);

	}

	public void update(OrderManager orderManager) {
		var activeOrders = orderManager.getOrders();
		this.getChildren().remove(this.getCenter());
		this.orderSelectionBox.updateOrders(activeOrders, this);
		if (this.orderSelectionBox.getOrderNumber() > 0) {
			orderInfo = new OrderInfo(this.orderSelectionBox.getOrderHeader(0).order, this, orderManager);
			this.setCenter(orderInfo);
		}
	}

	public void update(Order order) {
		this.orderSelectionBox.updateOrder(order);
		orderInfo.refresh(order);
	}

	protected void updateOrderStatus(Order order) {
		this.orderSelectionBox.checkOrderHeader(order);
	}

	public void addToBatch(OrderHeader orderHeader) {
		this.batchInfo.addNewOrderHeader(orderHeader);
	}

	public void removeFromBatch(OrderHeader orderHeader) {
		this.batchInfo.removeOrderHeader(orderHeader);
	}
}
