package film.monovo.config;

import java.util.Properties;

public class EmailConfig {
	public final String protocal;
	public final String inboundHost;
	public final String password;
	public final String username;

	EmailConfig(String protocal, String inboundHost, String username, String password) {
		this.protocal = protocal;
		this.inboundHost = inboundHost;
		this.password = password;
		this.username = username;
	}

	public Properties getInboundProperties() {
		var prop = new Properties();
		prop.put("mail.store.protocol", protocal);
		prop.put("mail.imap.auth", true);
		prop.put("mail.imap.starttls.enable", "true");
		prop.put("mail.imap.host", inboundHost);
		prop.put("mail.imap.port", "993");
		return prop;
	}
}
