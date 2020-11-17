package film.monovo.email;

import java.util.function.Function;

import javax.mail.Folder;
import javax.mail.Session;

import film.monovo.config.AppConfig;
import film.monovo.config.EmailConfig;

public class Reader {
	private final AppConfig appConfig = new AppConfig();
	private final EmailConfig config = appConfig.emailConfig;
	private final Boolean enableDebug = false;
	
	public <T> T execReadOnly(EmailFolder emailFolder, Function<Folder, T> func) {
		return exec(emailFolder, Folder.READ_ONLY, func);
	}
	
	private <T> T exec(EmailFolder emailFolder, int status, Function<Folder, T> func) {
		try {
	        var session = Session.getDefaultInstance(config.getInboundProperties(), null);
	        session.setDebug(enableDebug);
	        var store = session.getStore(config.protocal);
	        store.connect(config.inboundHost, config.username, config.password);
	        Folder folder = store.getFolder(emailFolder.toString());
	        folder.open(status);
	        return func.apply(folder);
	    } catch (Exception e) {
	    	e.printStackTrace();
	        throw new RuntimeException(e);
	    }
	}
}
