package film.monovo.manager.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.Message;
import javax.mail.MessagingException;

import film.monovo.email.EmailFolder;
import film.monovo.email.EmailUtils;

public class Event {
	public final String from;
	public final String subject;
	public final long uid;
	public final EmailFolder folder;
	public final String threadId;
	public Boolean enriched;
	public List<EventContent> contents;
	public final long timestamp;
	
	public Event(Message message, Long uid, String folderName) throws MessagingException {
		this.from = EmailUtils.getFrom(message);
		this.subject = message.getSubject();
		this.uid = uid;
		this.folder = EmailFolder.valueOf(folderName);
		this.threadId = EmailUtils.getThreadId(message);
		this.enriched = false;
		this.contents = new ArrayList<>();
		this.timestamp = message.getSentDate().getTime();
	}
	
	public Optional<EventContent> getEventContent(EventType type) {
		return this.contents.stream().filter( it-> it.type == type).findFirst();
	}
	
	public List<EventContent> getAllContentByType(EventType type) {
		return this.contents.stream().filter(it -> it.type == type).collect(Collectors.toList());
	}
	
	public long getNormalizedUid() {
		return folder == EmailFolder.INBOX? uid : -1 * uid;
	}
}
