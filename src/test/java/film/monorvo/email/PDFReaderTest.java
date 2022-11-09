package film.monorvo.email;


import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class PDFReaderTest {
    private EmailReader reader = new EmailReader();

    @Test
    public void readerPDFs() {
        var time = Instant.now().getEpochSecond();

        var localTime =  LocalDateTime.ofInstant(Instant.ofEpochMilli(time * 1000),
                TimeZone.getDefault().toZoneId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println(localTime.format(formatter));
    }
}