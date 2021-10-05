package film.monovo.manager;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.UIDFolder;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.gson.Gson;
import com.sun.mail.util.BASE64DecoderStream;

import film.monovo.config.EmailConfig;
import film.monovo.email.EmailFolder;
import film.monovo.email.Reader;
import film.monovo.manager.event.Event;
import film.monovo.manager.event.EventContent;
import film.monovo.manager.event.EventType;
import film.monovo.util.Compressor;
import org.apache.commons.io.IOUtils;


public class EmailManager {
	public final HashMap<String, Double> lastRead = readLastRead();
	public final Gson g = new Gson();
	public final Reader reader = new Reader();
	public final static EventContent INVALID_IMAGE = new EventContent("", EventType.IMAGE);
	private final EmailConfig config;
	ExecutorService executor = Executors.newFixedThreadPool(20);

	public EmailManager(EmailConfig config) {
		this.config = config;
	}

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

	public List<Event> refreshAllFolder() {
		var newInboxEvents = this.refreshFolder(EmailFolder.INBOX);
		if(!config.inboundHost.contains("gmail")) {
			var newSpamEvents = this.refreshFolder(EmailFolder.Spam);
			var list = new ArrayList<Event>();
			list.addAll(newInboxEvents);
			list.addAll(newInboxEvents);
			return list;
		} else {
			return newInboxEvents;
		}

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
				e.printStackTrace();
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
				//readMimeMultipart((MimeMultipart) content, folderName, list, uid);
				readMimeMultipartConcurrent((MimeMultipart) content, folderName, list, uid);
			} else if (content instanceof String) {
				list.add(new EventContent((String) content, EventType.TXT));
			} else {
				System.out.println(content.getClass().getName());
			}
		} catch (Exception e) {}
		return list;
	}

	private void readMimeMultipartConcurrent(MimeMultipart content, EmailFolder folderName, ArrayList<EventContent> list, long uid) {
		var futureList = new ArrayList<Future<EventContent>>();
		readMimeMultipartConcurrent(content, folderName, list, futureList, uid);
		for(Future<EventContent> c: futureList) {
			try {
				list.add(c.get());
			} catch (Exception e) {
				list.add(new EventContent("", EventType.UNKNOWN));
			}
		}
		System.out.println("all image presisted");
	}

	private void readMimeMultipart(MimeMultipart content, EmailFolder folderName, ArrayList<EventContent> list,
								   long uid) {
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

					list.add(readImage(part, folderName, uid));
				} else if (part.getContentType().toLowerCase().contains("multipart")) {
					readMimeMultipart((MimeMultipart) part.getContent(), folderName, list, uid);
				} else if (part.getContentType().toLowerCase().contains("zip")) {
					list.addAll(readZip(part, folderName, uid));
				} else {
					list.add(new EventContent(part.getContentType(), EventType.UNKNOWN));
				}
			}
		} catch (Exception e) {
			list.add(new EventContent("", EventType.UNKNOWN));
		}
	}

	private void readMimeMultipartConcurrent(MimeMultipart content, EmailFolder folderName, ArrayList<EventContent> list,
											 ArrayList<Future<EventContent>> futurelist, long uid) {
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
					var r = new ImageReader(part, folderName, uid);
					futurelist.add(executor.submit(r));
				} else if (part.getContentType().toLowerCase().contains("multipart")) {
					readMimeMultipartConcurrent((MimeMultipart) part.getContent(), folderName, list, futurelist, uid);
				} else if (part.getContentType().toLowerCase().contains("zip")) {
					list.addAll(readZip(part, folderName, uid));
				} else {
					list.add(new EventContent(part.getContentType(), EventType.UNKNOWN));
				}
			}
		} catch (Exception e) {
			list.add(new EventContent("", EventType.UNKNOWN));
		}
	}

	private List<EventContent> readZip(BodyPart part, EmailFolder folderName, long uid) {
		try{
			String destDir = FileManager.createZipSubFolder(Long.toString(uid));
			var encode = part.getHeader("Content-Transfer-Encoding")[0];
			if (encode != null && encode.toLowerCase().contains("base64")) {
				byte[] bytes = IOUtils.toByteArray((BASE64DecoderStream) part.getContent());
				var zipFileStream = FileManager.persistZipFile(bytes, destDir, uid);
				Compressor.extract(new File(destDir), zipFileStream);
				return readAllImage(destDir);
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List<EventContent> readAllImage(String destDir) {
		var folder = new File(destDir);
		var list = new ArrayList<EventContent>();
		for (File f : folder.listFiles()){
			var file = f;
			var name = file.getName().toLowerCase();
			if(name.endsWith("jpeg")) {
				File f1 = new File(f.getAbsolutePath().substring(0, f.getAbsolutePath().length() - 4) + "jpg");
				f.renameTo(f1);
				file = f1;
			}
			name = file.getName().toLowerCase();
			if(name.endsWith("jpg") || name.endsWith("png")){
				list.add(new EventContent(file.getAbsolutePath(), EventType.IMAGE));
			}
		}
		return list;
	}

	protected EventContent readImage(BodyPart part, EmailFolder folderName, long uid) {
		try {
			var fileName = readImageFileName(part);

			var encode = part.getHeader("Content-Transfer-Encoding")[0];
			if (encode != null && encode.toLowerCase().contains("base64")) {
				System.out.println("persist image " + fileName);
				System.out.println(part.getContent().getClass().getName());
				byte[] bytes = IOUtils.toByteArray((BASE64DecoderStream) part.getContent());
				var filePersisted = persist(fileName, uid, bytes);
				System.out.println("image " + fileName + " persisted");
				return new EventContent(filePersisted, EventType.IMAGE);
			}
			return INVALID_IMAGE;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private synchronized String persist(String fileName, long uid, byte[] bytes)  {
		var f = resolve(fileName, uid);
		return FileManager.persistImage(f, bytes, uid);
	}

	private String resolve(String fileName, long uid) {
		var result = fileName;
		if(fileName.toLowerCase().endsWith("jpeg")){
			result = fileName.substring(0, fileName.length() - 4) + "jpg";
		}

		System.out.println("Original fileName: " + fileName);
		if(FileManager.isImageExist(result, uid)) {
			result = UUID.randomUUID().toString() + "_" +result;
		}
		return result;
	}

	private String readImageFileName(BodyPart part) {
		try {
			var strs = part.getContentType().split(";");
			String name = null;
			for (String s : strs) {
				if (s.trim().startsWith("name")) {
					name = s;
					break;
				}
			}

			if (name == null) {
				if (part.isMimeType("image/png")) {
					return UUID.randomUUID().toString() + ".png";
				} else {
					return UUID.randomUUID().toString() + ".jpg";
				}
			} else {
				return name.split("=")[1].replace("\"", "");
			}
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private class ImageReader implements Callable<EventContent> {

		private final BodyPart part;
		private final EmailFolder folderName;
		private final long uid;

		public ImageReader(BodyPart part, EmailFolder folderName, long uid) {
			this.part = part;
			this.folderName = folderName;
			this.uid = uid;
		}


		@Override
		public EventContent call() {
			return readImage(part, folderName, uid);
		}
	}
}
