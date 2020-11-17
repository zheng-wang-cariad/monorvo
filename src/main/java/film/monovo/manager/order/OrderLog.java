package film.monovo.manager.order;

import film.monovo.util.BerlinerTime;

public class OrderLog {
	public final Long timestamp;
	public final OrderStatus status;
	
	public OrderLog(OrderStatus status) {
		this.timestamp = BerlinerTime.nowUnixTimeMilli();
		this.status = status;
	}
}
