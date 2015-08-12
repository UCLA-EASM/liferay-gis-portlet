package edu.ucla.macroscope.gis;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.liferay.portlet.documentlibrary.model.DLFileEntry;


public class WordCloudResult {

	private DLFileEntry content;
	//private TreeMap<String,Integer> words;
	private String words[];
	private String filecount;
	private String frequency[];
	
	/*public FirstLinesResult(DLFileEntry content, String line) {
		super();
		this.content = content;
		this.line = line;
	}*/
	
	public WordCloudResult(DLFileEntry content, TreeMap<String,Integer> tree_words,String count) {
		super();
		this.content = content;
		this.words = new String[tree_words.size()];
		this.frequency = new String[tree_words.size()];
		int i=0;
		for(Entry<String, Integer> entry : tree_words.entrySet()) {
			  String key = entry.getKey();
			  Integer value = entry.getValue();
			  this.words[i]=new String(key);
			  this.frequency[i++]=new String(Integer.toString(value));
			  //System.out.println(key + " => " + value);
			}
		//this.words = words;
		this.filecount = count;
	}
	
	public DLFileEntry getContent() {
		return content;
	}
	public String[] getWordArray() {
		
		return words;
	}
	public String getFileCount() {
		return filecount;
	}
	public String[] getFreqCount() {
		return frequency;
	}
	
}
