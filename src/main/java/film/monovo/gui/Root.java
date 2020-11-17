package film.monovo.gui;



import film.monovo.manager.RootManager;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class Root {
	public final RootManager managers;
	
	
	public final TabPane tabs;
	protected final Tab eventTab;
	protected final Tab orderTab;
	
	public Root(Stage stage) {
		managers = new RootManager(stage);
		tabs = new TabPane();	
		
		eventTab = new Tab("Event Management", managers.evnetManager.pane);
		orderTab = new Tab("Order Management", managers.orderManager.gui);
		eventTab.setClosable(false);
		orderTab.setClosable(false);
		tabs.getTabs().addAll(eventTab);
		tabs.getTabs().addAll(orderTab);
	}

	
}
