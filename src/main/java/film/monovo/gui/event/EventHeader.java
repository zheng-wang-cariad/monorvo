package film.monovo.gui.event;

import film.monovo.manager.event.EventChain;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class EventHeader extends HBox {
	
	public final EventChain chain;
	
	public EventHeader(EventChain chain, EventGUI gui) {
		this.chain = chain;
		var texts = new VBox();
		var button = new Button("x");
		this.getChildren().addAll(button, texts);
		button.setOnAction(a -> gui.deleteEvent(this));
		var from = this.chain.event.from.toLowerCase();
		if(this.chain.isImported) {
			if(from.contains("transaction@etsy.com")) {
				this.setBackground(new Background(new BackgroundFill(Color.web("0x0F0FFF"), CornerRadii.EMPTY, Insets.EMPTY)));
			} else {
				this.setBackground(new Background(new BackgroundFill(Color.web("0xFFFF99"), CornerRadii.EMPTY, Insets.EMPTY)));
			}
		} else if(from.contains("transaction@etsy.com")) {
			this.setBackground(new Background(new BackgroundFill(Color.web("0xE5FFCC"), CornerRadii.EMPTY, Insets.EMPTY)));
		} else if (from.contains("conversations@mail.etsy.com")) {
			this.setBackground(new Background(new BackgroundFill(Color.web("0xFFE5CC"), CornerRadii.EMPTY, Insets.EMPTY)));
		} else if (from.contains("noreply@etsy.com")) {
			this.setBackground(new Background(new BackgroundFill(Color.web("0xFFCCE5"), CornerRadii.EMPTY, Insets.EMPTY)));
		}

		texts.getChildren().addAll(new Label(chain.event.subject), new Label(chain.event.from));
	}

	public void update() {
		var from = this.chain.event.from.toLowerCase();
		if(this.chain.isImported) {
			if(from.contains("transaction@etsy.com")) {
				this.setBackground(new Background(new BackgroundFill(Color.web("0x0F0FFF"), CornerRadii.EMPTY, Insets.EMPTY)));
			} else {
				this.setBackground(new Background(new BackgroundFill(Color.web("0xFFFF99"), CornerRadii.EMPTY, Insets.EMPTY)));
			}
		}
	}
}
