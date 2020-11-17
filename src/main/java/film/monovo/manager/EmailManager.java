package film.monovo.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.UIDFolder;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.gson.Gson;
import com.sun.mail.util.BASE64DecoderStream;

import film.monovo.email.EmailFolder;
import film.monovo.email.Reader;
import film.monovo.manager.event.Event;
import film.monovo.manager.event.EventContent;
import film.monovo.manager.event.EventType;
import org.apache.commons.io.IOUtils;


public class EmailManager {
	public final HashMap<String, Double> lastRead = readLastRead();
	public final Gson g = new Gson();
	public final Reader reader = new Reader();
	public final static EventContent INVALID_IMAGE = new EventContent("", EventType.IMAGE);

	private HashMap<String, Double> readLastRead() {
		return FileManager.readLastRead();
	}

	private long getStart(Folder folder) {
		var name = folder.getName();
		if (lastRead.containsKey(name)) {
			return lastRead.get(name).longValue() + 1;
		} else {
			return 1;
		}
	}

	public List<Event> refreshFolder(EmailFolder folder) {
		return reader.execReadOnly(folder, refreshUnreadEmails);
	}

	private Function<Folder, List<Event>> refreshUnreadEmails = (Folder f) -> {
		var eventList = new ArrayList<Event>();
		var uf = (UIDFolder) f;

		try {
			var max = uf.getUIDNext();
			for (long i = getStart(f); i < max; i++) {
				var message = uf.getMessageByUID(i);
				if (message != null)
					eventList.add(new Event(message, i, f.getName()));
			}
			updateLastRead(max - 1, f.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (eventList.size() > 0)
			FileManager.persistEvents(eventList);
		return eventList;

	};

	public List<EventContent> readContents(Event event) {
		return reader.execReadOnly(event.folder, f -> {
			ArrayList<EventContent> list = new ArrayList<>();
			var uf = (UIDFolder) f;
			try {
				var message = uf.getMessageByUID(event.uid);
				readContents((MimeMessage) message, event.uid, event.folder, list);
			} catch (MessagingException e) {
			}
			return list;
		});
	}

	private void updateLastRead(Long value, String foldername) {
		lastRead.put(foldername, value.doubleValue());
		FileManager.persistLastRead(lastRead);
	}

	private List<EventContent> readContents(MimeMessage message, long uid, EmailFolder folderName, ArrayList<EventContent> list) {
		try{
			var content = message.getContent();
			if (content instanceof MimeMultipart) {
				readMimeMultipart((MimeMultipart) content, folderName, list);
			} else if (content instanceof String) {
				list.add(new EventContent((String) content, EventType.TXT));
			} else {
				System.out.println(content.getClass().getName());
			}
		} catch (Exception e) {}
		return list;
	}

	private void readMimeMultipart(MimeMultipart content, EmailFolder folderName, ArrayList<EventContent> list) {
		try {
			for (int i = 0; i < content.getCount(); i++) {
				var part = content.getBodyPart(i);
				if (part.isMimeType("text/html")) {

					var str = (String) part.getContent();
					var filePath = FileManager.persistTempHtmlFile(str);
					list.add(new EventContent(filePath, EventType.HTML));
				} else if (part.isMimeType("text/plain")) {

					list.add(new EventContent((String) part.getContent(), EventType.TXT));
				} else if (part.isMimeType("image/*")) {

					list.add(readImage(part, folderName));
				} else if (part.getContentType().contains("multipart")) {

					readMimeMultipart((MimeMultipart) part.getContent(), folderName, list);
				} else {

					list.add(new EventContent("", EventType.UNKNOWN));
				}
			}
		} catch (Exception e) {
			list.add(new EventContent("", EventType.UNKNOWN));
		}
	}

	private EventContent readImage(BodyPart part, EmailFolder folderName) {
		try {
			var fileName = readImageFileName(part);
			var encode = part.getHeader("Content-Transfer-Encoding")[0];
			System.out.println("image encode" + encode);
			if (encode != null && encode.toLowerCase().contains("base64")) {
				System.out.println("persist image" + fileName);
				System.out.println(part.getContent().getClass().getName());
				byte[] bytes = IOUtils.toByteArray((BASE64DecoderStream) part.getContent());
				var filePersisted = FileManager.persistImage(fileName, bytes);
				return new EventContent(filePersisted, EventType.IMAGE);
			}
			return INVALID_IMAGE;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String readImageFileName(BodyPart part) throws MessagingException {
		var strs = part.getContentType().split(";");
		String name = null;
		for (String s : strs) {
			if (s.trim().startsWith("name")) {
				name = s;
				break;
			}
		}

		if (name == null) {
			return UUID.randomUUID().toString() + ".jpg";
		} else {
			return name.split("=")[1].replace("\"", "");
		}
	}
}
