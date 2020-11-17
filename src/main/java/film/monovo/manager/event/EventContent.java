package film.monovo.manager.event;

public class EventContent {
	public final String content;
	public final EventType type;
	
	public EventContent(String content, EventType type) {
		this.content = content;
		this.type = type;
	}
}
