package film.monovo.gui.event;

import java.io.File;
import java.net.MalformedURLException;

import film.monovo.manager.event.Event;
import film.monovo.manager.event.EventType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;


public class EventBox extends VBox{
	private final Event event;
	
	public EventBox(Event event) {
		this.event = event;
		
		var subject = new Label(event.subject);
		subject.setFont(new Font("Black", 20));
		
		this.getChildren().add(subject);
		this.getChildren().add(new Label(event.from));
		
		var content = event.getEventContent(EventType.HTML);
		if(content.isPresent()) {
			WebView w = new WebView();
			w.getEngine().load(getURL(content.get().content));
			this.getChildren().add(w);
			
		} else {
			content = event.getEventContent(EventType.TXT);
			if(content.isPresent()) {
				this.getChildren().add(new Label(content.get().content));
			}
		}
		
		var images = event.getAllContentByType(EventType.IMAGE);
		
		if(!images.isEmpty()) {
			var box = new ImageBox();
			images.forEach( i -> box.addImage(i.content) );
			ScrollPane sp = new ScrollPane();
			sp.setContent(box);
			this.getChildren().add(sp);
			
		}
		
		content = event.getEventContent(EventType.UNKNOWN);
		if(content.isPresent()) {
			var warning = new Label("some item can not be read");
			warning.setTextFill(Color.RED);
			this.getChildren().add(warning);
		}
		
	}
	
	private String getURL(String filePath) {
		File f = new File(filePath);
		try {
			return f.toURI().toURL().toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
