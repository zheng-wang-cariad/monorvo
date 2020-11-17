package film.monovo.manager.event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import film.monovo.manager.FileManager;

public class EventChain {
	public final Event event;
	public final ArrayList<Event> events;
	public Long lastUpdated;
	public boolean isDeleted;
	
	public EventChain(Event event) {
		this.event = event;
		events = new ArrayList<Event>();
		lastUpdated = event.timestamp;
		events.add(event);
		isDeleted = false;
		FileManager.persistChain(toDto(this));
	}
	
	public EventChain(Event event, List<Event> events, boolean isDeleted) {
		this.event = event;
		this.events = new ArrayList<>();
		this.events.addAll(events);
		this.isDeleted = isDeleted;
		long time = 0;
		for(Event e : events) {
			if(e.timestamp > time) time = e.timestamp;
		}
		this.lastUpdated = time;
	}
	
	public void addEvent(Event event) {
		if(events.stream().anyMatch( e-> e.uid == event.uid)) return;
		this.lastUpdated = event.timestamp;
		events.add(event);
		FileManager.persistChain(toDto(this));
	}
	
	private EventChainDto toDto(EventChain chain) {
		return new EventChainDto(chain.event.getNormalizedUid(), chain.events.stream().map (e -> e.getNormalizedUid()).collect(Collectors.toList()), chain.isDeleted);
	}

	public EventChainDto toDto() {
		return toDto(this);
	}
}
