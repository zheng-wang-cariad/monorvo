package film.monorvo.email;

import java.util.UUID;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;


public class EmailUtils {
	public static final String THREAD_ID_NAME = "Thread-Index"; 
	
	public static final String getThreadId(Message message) {
		try {
			var idx = message.getHeader(THREAD_ID_NAME);
			return idx == null ? UUID.randomUUID().toString() : message.getHeader(THREAD_ID_NAME)[0];
		} catch (MessagingException e) {
			return UUID.randomUUID().toString();
		}
    }
	
	public static String getFrom(Message message){
		try {
			var from = message.getFrom();
			if(from != null) {
	            StringBuilder sb = new StringBuilder();
	            for(Address addr: from) {
	            	if(sb.length() != 0) sb.append("; ");
	            	sb.append(addr.toString());
	            	
	            }
	            return sb.toString();
			}
		} catch (MessagingException e) {}
        return "unknown";
    }
}
