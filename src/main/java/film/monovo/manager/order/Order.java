package film.monovo.manager.order;

import java.util.ArrayList;

import film.monovo.manager.FileManager;

public class Order {
	public final String id;
	public final long timestamp;
	public final String orderFolder;
	public OrderStatus status;
	public final ArrayList<OrderLog> log;
	public final Customer customer;
	public String batchFilePath = null;
	public String lieferscheinPath = null;

	public Order(String id, long timestamp, OrderStatus status, Customer customer) {
		this.id = id;
		this.timestamp = timestamp;
		this.orderFolder = FileManager.ensureOrderFolder(id);
		this.status = status;
		log = new ArrayList<>();
		log.add(new OrderLog(status));
		this.customer = customer;
	}
}
