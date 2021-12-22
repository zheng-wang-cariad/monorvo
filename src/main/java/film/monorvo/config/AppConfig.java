package film.monorvo.config;


import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;

public class AppConfig {
	//public final String configPath = "/Users/kolinsky.hexad/Documents/monovo_storage/meta/config.json";
	public final String configPath = "/Users/kolinsky.hexad/Documents/shop/meta/config.json";
	public final Gson g = new Gson();
	public final PathConfig paths;
	public final EmailConfig emailConfig;
	

	public AppConfig() {
		try {
			String str = Files.readString(Paths.get(configPath));
			this.paths = g.fromJson(str, PathConfig.class);
			str = Files.readString(Paths.get(paths.emailCredentialPath));
			this.emailConfig = g.fromJson(str, EmailConfig.class);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
}
