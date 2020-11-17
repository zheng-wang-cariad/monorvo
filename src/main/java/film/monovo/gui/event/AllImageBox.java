package film.monovo.gui.event;

import java.util.ArrayList;

import film.monovo.manager.event.EventChain;
import film.monovo.manager.event.EventContent;
import film.monovo.manager.event.EventType;
import javafx.scene.layout.VBox;

public class AllImageBox extends VBox{
	
	private final ArrayList<String> filePath = new ArrayList<>();
	
	public AllImageBox() {
		this.setSpacing(10);
	}

	public void loadChain(EventChain chain) {
		chain.events.stream().forEach(e->e.contents.forEach(c -> {
			if(c.type == EventType.IMAGE) addFile(c);
		}));
		refresh();
	}

	private void addFile(EventContent c) {
		if(filePath.contains(c.content)) return;
		filePath.add(c.content);
	}
	
	public void refresh() {
		for(String f: filePath) {
			System.out.println(11);
			this.getChildren().add(new ImageSelectionBox(f, false));
		}
	}
	
	
}
