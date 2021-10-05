package film.monovo.manager;

import film.monovo.config.AppConfig;
import javafx.stage.Stage;

public class RootManager {
	public final AppConfig config;
	public final EventManager evnetManager;
	public final EmailManager emailManager;
	public final OrderManager orderManager;
	private EventSyncronizer sync;
	
	public RootManager(Stage stage) {
		this.config = new AppConfig();
		this.emailManager = new EmailManager(config.emailConfig);
		this.evnetManager = new EventManager(this);
		this.orderManager = new OrderManager(stage);
		this.sync = new EventSyncronizer(this);
		
	}
}
