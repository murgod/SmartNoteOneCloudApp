package io.webApp.springbootstarter.attachments;

/**
 * MetaData class object for attachments
 * 
 * @author satishkumaranbalagan
 *
 */
public class metaData {
	private String fileName;
	private String fileDownloadUri;
	private String fileType;
	private long size;

	/**
	 * MetaData object constructor
	 * 
	 * @param fileName        name of file in String
	 * @param fileDownloadUri URL of file in String
	 * @param fileType        file type in String
	 * @param size            size of file in long
	 */
	public metaData(String fileName, String fileDownloadUri, String fileType, long size) {
		this.fileName = fileName;
		this.fileDownloadUri = fileDownloadUri;
		this.fileType = fileType;
		this.size = size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString() To print the MetaData object
	 */
	@Override
	public String toString() {
		return "[\"fileName\":\"" + fileName + ", \"fileDownloadUri\":\"" + fileDownloadUri + ", \"fileType\":\""
				+ fileType + ", \"size\":\"" + size + "]";
	}

}
