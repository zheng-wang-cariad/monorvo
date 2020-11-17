package film.monovo.manager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import com.google.gson.Gson;

import film.monovo.config.AppConfig;
import film.monovo.manager.event.Event;
import film.monovo.manager.event.EventChainDto;
import film.monovo.manager.order.Order;

import javax.imageio.ImageIO;

public class FileManager {
	private static final Gson g = new Gson();
	private final static AppConfig config = new AppConfig();

	public static final void persistLastRead(HashMap<String, Double> map) {
		try {
			Files.writeString(Paths.get(config.paths.lastReadFilePath), g.toJson(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static HashMap<String, Double> readLastRead() {
		try {
			var str = Files.readString(Paths.get(config.paths.lastReadFilePath));
			return g.fromJson(str, new HashMap<String, Double>().getClass());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new HashMap<>();
		}
	}

	public static void persistEvents(List<Event> events) {
		events.stream().forEach(e->persistEvent(e));
	}
	
	public static void persistEvent(Event event) {
		try {
			Files.writeString(getEventPath(event), g.toJson(event));	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static Path getEventPath(Event event) {
		return Paths.get(String.format(config.paths.eventFileFormatterString, event.getNormalizedUid()));
	}
	
	private static <T> ArrayList<T> readAllFiles(String fileFolderName, String prefix, String surfix, Class<T> clazz) {
		var list = new ArrayList<T>();
		try {
			File f = new File(fileFolderName);
			for(File fileEntry: f.listFiles()) {
				if (fileEntry.isFile() && fileEntry.getName().startsWith(prefix) && fileEntry.getName().endsWith(surfix)) {
					var str = Files.readString(fileEntry.toPath());
					var event = g.fromJson(str, clazz);
					list.add(event);
				}
			}
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static HashMap<Long, Event> readAllEvents() {
		var map = new HashMap<Long, Event>();
		readAllFiles(config.paths.eventFileFolder, "event", "json", Event.class).stream().forEach(e-> map.put(e.getNormalizedUid(), e));
		return map;
	}

	public static void persistChain(EventChainDto eventChainDto) {
		try {
			Files.writeString(getChainPath(eventChainDto), g.toJson(eventChainDto));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Path getChainPath(EventChainDto eventChainDto) {
		return Paths.get(String.format(config.paths.chainFileFormatterString, eventChainDto.uid));
	}
	
	public static List<EventChainDto> readAllEventChainDtos() {
		return readAllFiles(config.paths.eventFileFolder, "chain", "json", EventChainDto.class);
	}

	public static String persistTempHtmlFile(String str) {
		 var filePath = String.format(config.paths.tempHTMLFileFormatterString, UUID.randomUUID().toString());
		 try {
				Files.writeString(Paths.get(filePath), str);
				return filePath;
			} catch (IOException e) {
				return filePath;
			}
	}

	public static String persistImage(String fileName, byte[] bytes) {
		var file = config.paths.tempImageFileFolder + fileName;
		ensureFile(file);
		try (FileOutputStream fos = new FileOutputStream(file)){
			fos.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	public static String persistImage(String fileName, BufferedImage image) throws IOException {
		var filePath = config.paths.tempImageFileFolder + fileName;
		ensureFile(filePath);
		ImageIO.write(image, "jpg", new File(filePath));
		return filePath;
	}

	public static File ensureFile(String path) {
		var file = new File(path);
		if(file.exists()) {
			return file;
		} else {
			try {
				file.createNewFile();
				return file;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public static String ensureOrderFolder(String id) {
		var directoryPath = String.format(config.paths.orderFolderFormatterString, id);
		try {
            Path path = Paths.get(directoryPath);
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
		return directoryPath;
	}

	public static void persist(Order order) {
		var path = config.paths.orderFileFolder + "order-" + order.id + ".json";
		try {
			Files.writeString(Paths.get(path), g.toJson(order));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static List<Order> readAllOrders() {
		return readAllFiles(config.paths.orderFileFolder, "order", "json", Order.class);
	}

	public static int getImageNumber(String id) {
		var directoryPath = String.format(config.paths.orderFolderFormatterString, id);
		try {
			int numberOfImage = 0;
			File f = new File(directoryPath);
			for(File fileEntry: f.listFiles()) {
				if (fileEntry.isFile() && !fileEntry.getName().equals("merged.jpg") && fileEntry.getName().endsWith("jpg")) {
					numberOfImage ++;
				}
			}
			return numberOfImage;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static List<FileInputStream> readAllImage(String id) {
		var directoryPath = String.format(config.paths.orderFolderFormatterString, id);
		File f = new File(directoryPath);
		var list = new ArrayList<FileInputStream>();
		for(File fileEntry: f.listFiles()) {
			if (fileEntry.isFile() && !fileEntry.getName().equals("merged.jpg") && fileEntry.getName().endsWith("jpg")) {
				try {
					list.add(new FileInputStream(fileEntry.getAbsolutePath()));
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		return list;

	}

	public static String getMergedFilePath(String orderId) {
		return String.format(config.paths.orderFolderFormatterString, orderId) + "merged.jpg";
	}

	public static Collection<? extends String> readAllImageFilePath(String id) {
		var directoryPath = String.format(config.paths.orderFolderFormatterString, id);
		File f = new File(directoryPath);
		var list = new ArrayList<String>();
		for(File fileEntry: f.listFiles()) {
			if (fileEntry.isFile() && !fileEntry.getName().equals("merged.jpg") && fileEntry.getName().endsWith("jpg")) {
				try {
					list.add(fileEntry.getName());
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public static void copyTempImageFileToOrderFolder(String orderId, String fileName) {
		var origin = new File(fileName);
		var orderFile = String.format(config.paths.orderFolderFormatterString, orderId) + origin.getName();
		var targetFile = ensureFile(orderFile);
		try {
			Files.copy(origin.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
