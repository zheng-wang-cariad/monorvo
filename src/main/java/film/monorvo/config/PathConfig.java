package film.monorvo.config;

public class PathConfig {
	public  String emailCredentialPath;
	public  String lastReadFilePath;
	public  String eventFileFormatterString;
	public  String chainFileFormatterString;
	public  String eventFileFolder;
	public  String tempHTMLFileFormatterString;
	public  String tempImageFileFolder;
	public  String orderFolderFormatterString;
	public  String orderFileFolder;
	
	public PathConfig(String emailCredentialPath, String lastReadFilePath, String eventFileFormatterString, String chainFileFormatterString, String eventFileFolder,
			String tempHTMLFileFormatterString, String tempImageFileFolder, String orderFolderFormatterString,
			String orderFileFolder) {
		this.emailCredentialPath = emailCredentialPath;
		this.lastReadFilePath = lastReadFilePath;
		this.eventFileFormatterString = eventFileFormatterString;
		this.chainFileFormatterString = chainFileFormatterString;
		this.eventFileFolder = eventFileFolder;
		this.tempHTMLFileFormatterString = tempHTMLFileFormatterString;
		this.tempImageFileFolder = tempImageFileFolder;
		this.orderFolderFormatterString = orderFolderFormatterString;
		this.orderFileFolder = orderFileFolder;
	}
}
