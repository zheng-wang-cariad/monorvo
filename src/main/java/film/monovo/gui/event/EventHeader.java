package film.monovo.gui.event;

import film.monovo.manager.event.EventChain;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EventHeader extends HBox {
	
	public final EventChain chain;
	
	public EventHeader(EventChain chain, EventGUI gui) {
		this.chain = chain;
		var texts = new VBox();
		var button = new Button("x");
		this.getChildren().addAll(button, texts);
		button.setOnAction(a -> gui.deleteEvent(this));

		texts.getChildren().addAll(new Label(chain.event.subject), new Label(chain.event.from));
	}

}
