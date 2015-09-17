package edu.ucla.macroscope.gis;

import com.liferay.portlet.documentlibrary.model.DLFileEntry;

public class FirstLinesResult {
	private DLFileEntry content;
	private String line;
	private String wordCount;
	
	/*public FirstLinesResult(DLFileEntry content, String line) {
		super();
		this.content = content;
		this.line = line;
	}*/
	
	public FirstLinesResult(DLFileEntry content, String line,String count) {
		super();
		this.content = content;
		this.line = line;
		this.wordCount = count;
	}
	
	public DLFileEntry getContent() {
		return content;
	}
	public String getLine() {
		return line;
	}
	public String getWordCount() {
		return wordCount;
	}
	
}