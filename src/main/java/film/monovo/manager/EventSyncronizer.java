package film.monovo.manager;

import film.monovo.email.EmailFolder;
import film.monovo.manager.event.Event;
import film.monovo.util.BerlinerTime;
import javafx.application.Platform;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EventSyncronizer {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final RootManager managers;

    public EventSyncronizer(RootManager managers){
        this.managers = managers;
        scheduler.scheduleWithFixedDelay(this::refresh, 0, 5, TimeUnit.MINUTES);

    }

    private void refresh() {
        //Platform.runLater(this::refreshFolder);
        refreshFolder();
    }

    private void refreshFolder() {

        System.out.println("Sync started at " + BerlinerTime.toTimeString(BerlinerTime.nowUnixTimeMilli()));

        var newEvents = managers.emailManager.refreshAllFolder();
        if(newEvents.isEmpty()) {
            System.out.println("Sync Ended at " + BerlinerTime.toTimeString(BerlinerTime.nowUnixTimeMilli()));
            return;
        }
        this.managers.evnetManager.injectNewEventsWithoutUpdate(newEvents);

        for(Event e: newEvents) {
            managers.evnetManager.enrich(e);
        }
        Platform.runLater(managers.evnetManager.gui::updateEventList);
        System.out.println("Sync Ended at " +BerlinerTime.toTimeString(BerlinerTime.nowUnixTimeMilli()));
//		injectToEventChains(newInboxEvents);
//		if(!managers.config.emailConfig.inboundHost.contains("gmail")) {
//			var newSpamEvents = managers.emailManager.refreshFolder(EmailFolder.Spam);
//			injectToEventChains(newSpamEvents);
//		}
    }

}
