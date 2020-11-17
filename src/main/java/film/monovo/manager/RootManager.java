package film.monovo.manager;

import film.monovo.config.AppConfig;
import javafx.stage.Stage;

public class RootManager {
	public final EventManager evnetManager;
	public final EmailManager emailManager;
	public final OrderManager orderManager;
	
	public RootManager(Stage stage) {
		AppConfig config = new AppConfig();
		this.emailManager = new EmailManager();
		this.evnetManager = new EventManager(this);
		this.orderManager = new OrderManager(stage);
		
		
	}
}
