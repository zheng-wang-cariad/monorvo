package film.monovo.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class BerlinerTime {
	public final static ZoneId zone = ZoneId.of("Europe/Berlin");
	public final static ZoneOffset zoneOffset = zone.getRules().getOffset(LocalDateTime.now());
	public final static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
	public final static DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-mm-yyyy");
	public final static SimpleDateFormat formatter2 = new SimpleDateFormat("d-MM-yyyy hh.mm.ss aa");
		
	public final static LocalDateTime toLocalDateTime(long unixTimeMilli) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(unixTimeMilli), zone);
	}

	public final static long nowTimeMilli() { return LocalDateTime.now().toInstant(zoneOffset).toEpochMilli();}
	public final static long toUnixTimeMilli(LocalDateTime t) { return t.toInstant(zoneOffset).toEpochMilli();}
	public final static String toTimeString(LocalDateTime t) { return t.format(formatter);}
	public final static String toTimeStringSecond(LocalDateTime t) { return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(t);}
	public final static String toTimeStringSecond(long unixTimeMilli) { return toTimeStringSecond(toLocalDateTime(unixTimeMilli)); }
	public final static String toTimeString(long unixTimeMilli) { return toLocalDateTime(unixTimeMilli).format(formatter);}
	public final static long nowUnixTimeMilli() { return toUnixTimeMilli(LocalDateTime.now());}
//	public final static long fromString(String str) {
//		return toUnixTimeMilli(LocalDate.from(formatter1.parse(str)));
//	}
//	
//	public final static boolean testString(String str) {
//		try {
//			fromString(str);
//			return true;
//		} catch(Exception e) {
//			return false;
//		}
//	}

}
