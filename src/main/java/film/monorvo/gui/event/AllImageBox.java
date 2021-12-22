package film.monorvo.gui.event;

import java.util.ArrayList;

import film.monorvo.manager.event.EventChain;
import film.monorvo.manager.event.EventContent;
import film.monorvo.manager.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AllImageBox extends VBox{
	
	private final ArrayList<String> filePath = new ArrayList<>();
	protected final ArrayList<ImageSelectionBox> boxes = new ArrayList<>();
	private Label selectedLabel = new Label();
	
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
		this.getChildren().clear();
		this.boxes.clear();
		for(String f: filePath) {
			var box = new ImageSelectionBox(this, f, false);
			this.boxes.add(box);
			this.getChildren().add(box);
		}
		this.getChildren().add(createSelectAllButton());
		this.getChildren().add(selectedLabel);
		selectedLabel.setText("selected pic: 0");
	}

	private Node createSelectAllButton() {
		var button = new Button("select all");
		button.setOnAction(action -> {
			this.boxes.stream().forEach(it -> it.checkbox.setSelected(true));
			this.selectedLabel.setText("selected pic: " + this.boxes.size());
		});
		return button;
	}

	public void updateLabel() {
		var count  = boxes.stream().filter(it->it.checkbox.isSelected()).count();
		this.selectedLabel.setText("selected pic: " + count);
	}
}
