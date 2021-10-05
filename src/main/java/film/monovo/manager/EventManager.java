package film.monovo.manager;

import java.util.*;
import java.util.stream.Collectors;

import film.monovo.email.EmailFolder;
import film.monovo.gui.event.EventGUI;
import film.monovo.manager.event.Event;
import film.monovo.manager.event.EventChain;
import film.monovo.manager.event.EventChainDto;
import javafx.scene.layout.BorderPane;

public class EventManager {
	public final RootManager managers;
	public final BorderPane pane = new BorderPane();
	protected final EventGUI gui;
	private final HashMap<Long, Event> events;
	private HashMap<String, EventChain> chains = new HashMap<String, EventChain>();

	public EventManager(RootManager managers) {
		this.managers = managers;

		events = FileManager.readAllEvents();
		chains = readAllChains();
		this.gui = new EventGUI(this);
	}

	public List<EventChain> getAllEventChains() {
		return new ArrayList<>(this.chains.values());
	}

	public void injectNewEvents(List<Event> newEvents) {
		injectToEventChains(newEvents);
		this.gui.updateEventList();
	}

	public void injectNewEventsWithoutUpdate(List<Event> newEvents) {
		injectToEventChains(newEvents);
	}

	public List<EventChain> getSortedEventChain() {
//		var newInboxEvents = managers.emailManager.refreshFolder(EmailFolder.INBOX);
//
//		injectToEventChains(newInboxEvents);
//		if(!managers.config.emailConfig.inboundHost.contains("gmail")) {
//			var newSpamEvents = managers.emailManager.refreshFolder(EmailFolder.Spam);
//			injectToEventChains(newSpamEvents);
//		}

		ArrayList<EventChain> toSort = new ArrayList<>(chains.values());
		toSort.sort((a, b) -> {
			var c = a.lastUpdated - b.lastUpdated;
			if(c > 0) {
				return -1;
			} else if(c == 0){
				return 0;
			} else return 1;
		});
		return toSort;
	}

	private void injectToEventChains(List<Event> events) {
		events.stream().forEach(e -> {
					if(chains.containsKey(e.threadId)) {
						var chain = chains.get(e.threadId);
						chain.addEvent(e);
						chain.isDeleted = false;
					} else {
						chains.put(e.threadId, new EventChain(e));	
					}
				}
		);
	}
	
	private HashMap<String, EventChain> readAllChains(){
		var map = new HashMap<String, EventChain>();
		var chainsDtos = FileManager.readAllEventChainDtos().stream();
		chainsDtos.forEach(dto -> {
			var chain = toChain(dto);
			map.put(chain.event.threadId, chain);
		});
		return map;
	}
	
	private EventChain toChain(EventChainDto dto) {
		return new EventChain(events.get(dto.uid), dto.uids.stream().map(uid->events.get(uid)).collect(Collectors.toList()), dto.isDeleted, dto.isImported);
	}

	public void enrich(EventChain chain){
		chain.events.forEach(it -> enrich(it));
		
	}
	
	public void enrich(Event event){
		
		if(event.enriched) return;
		try {
			System.out.println("persist event" + event.getNormalizedUid() + " " + event.from);
			event.contents = this.managers.emailManager.readContents(event);
			event.enriched = true;
			FileManager.persistEvent(event);

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
