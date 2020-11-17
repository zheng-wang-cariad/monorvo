package film.monovo.manager;

import java.util.Collection;
import java.util.HashMap;

import com.google.gson.Gson;

import film.monovo.exception.UserAwaringException;
import film.monovo.gui.order.OrderGUI;
import film.monovo.manager.order.Order;
import film.monovo.manager.order.OrderLog;
import film.monovo.manager.order.OrderStatus;
import film.monovo.util.BerlinerTime;
import javafx.stage.Stage;

public class OrderManager {
	private Gson g = new Gson();
	private final HashMap<String, Order> orderMap = new HashMap<>();
	public OrderGUI gui;
	
	public OrderManager(Stage stage) {
		this.gui = new OrderGUI(stage);
		FileManager.readAllOrders().forEach(o -> orderMap.put(o.id, o));
		gui.update(this);
	}

	public void addNewOrder(Order order) throws UserAwaringException {
		if(orderMap.containsKey(order.id)) throw new UserAwaringException("Order exists");
		orderMap.put(order.id, order);
		FileManager.persist(order);
		gui.update(this);
	}
	
	public void updateOrderStatus(Order order, OrderStatus status) {
		order.status = status;
		order.log.add(new OrderLog(status));
		FileManager.persist(order);
		gui.update(order);
	}

	public Collection<Order> getOrders() {
		return orderMap.values();
	}

	public boolean hasOrder(String orderId) {
		return orderMap.containsKey(orderId);

	}
}
