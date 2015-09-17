package edu.ucla.macroscope.gis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
//import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
//import com.liferay.portal.model.PortletPreferences;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class FirstLinesPortlet
 */
public class FirstLinesPortlet extends MVCPortlet {
 
	public void loadFirstLines(ActionRequest request, ActionResponse response) 
			throws InvalidParameterException, PortalException, SystemException, SQLException, IOException, PortletException {
		
		ArrayList<Long> selectedDocumentIds = new ArrayList<Long>();
		String count = "0";

		for (Enumeration<String> parameterNames = request.getParameterNames(); parameterNames.hasMoreElements();) {
			String parameterName = parameterNames.nextElement();
			
			if (!parameterName.startsWith("document-")) {
				continue;
			}
			
			if(ParamUtil.getBoolean(request, parameterName)) {
				// NOTE: Potential bug if document IDs get more complex
				Long documentId = Long.parseLong(parameterName.replaceAll("document-", ""));
				selectedDocumentIds.add(documentId);
			}
		}
		
		if (selectedDocumentIds.isEmpty()) {
			throw new InvalidParameterException("No document IDs selected");
		}
		
		List<FirstLinesResult> results = new ArrayList<FirstLinesResult>();
		
		for (Long documentId : selectedDocumentIds) {;
			DLFileEntry document = DLFileEntryLocalServiceUtil.getDLFileEntry(documentId);
			//BufferedReader br=null;
			//br = new BufferedReader(new FileReader(document.getTitle()));
			//br = new BufferedReader(bew FileReader(document));
			
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			
			InputStream stream = document.getContentStream();
			
			// 4096 because it's unlikely the first line of a text file will have a first line
			// greater than 4K long
			int byteCount = stream.read(buffer, 0, 4096);
			
			String firstLine;
			
			if (byteCount != -1) {
				byteStream.write(buffer, 0, byteCount);
				stream.close();
				byte[] bytes = byteStream.toByteArray();
				
				// Assuming all files will be UTF-8. Not safe for production,
				// and should be tested before a demo
				String possibleFirstLines = new String(bytes, "UTF-8");
				System.out.println("Lines: "+possibleFirstLines);
				firstLine = possibleFirstLines.split("[\\r\\n]+")[0];
			} else {
				firstLine = "<Empty file>";
			}
			firstLine = document.getTreePath();//Integer.toString(document.getReadCount());
			//br.close();
			count = Long.toString(document.getSize());

			results.add(new FirstLinesResult(document, firstLine, count));
			//results.add(new FirstLinesResult(document, firstLine));
		}
		
		//SessionMessages.add(request, "results", results);
		//System.out.println(SessionMessages.get(request, "results"));
		//System.out.println(results.get(0).getLine());
		for(int i=0; i< results.size();i++) {
			response.setRenderParameter("result"+i, results.get(i).getLine());
			response.setRenderParameter("title"+i, results.get(i).getContent().getTitle());
			response.setRenderParameter("filecounts"+i, results.get(i).getWordCount());
		}
		
		response.setRenderParameter("resultSize", Integer.toString(results.size()));
		response.setRenderParameter("jspPage", "/html/maps/results.jsp");
	}
	
	public void tokenize_file(ActionRequest request, ActionResponse response) 
			throws InvalidParameterException, PortalException, SystemException, SQLException, IOException, PortletException {
		
		ArrayList<Long> selectedDocumentIds = new ArrayList<Long>();
		String count = "0";
		HashMap<String,Integer> dict = new HashMap<String,Integer>();
		ValueComparator bvc =  new ValueComparator(dict);
        TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
        GetStopWords stop= new GetStopWords();
        HashSet h = stop.getStopList();
		
		for (Enumeration<String> parameterNames = request.getParameterNames(); parameterNames.hasMoreElements();) {
			String parameterName = parameterNames.nextElement();
			
			if (!parameterName.startsWith("document-")) {
				continue;
			}
			
			if(ParamUtil.getBoolean(request, parameterName)) {
				// NOTE: Potential bug if document IDs get more complex
				Long documentId = Long.parseLong(parameterName.replaceAll("document-", ""));
				selectedDocumentIds.add(documentId);
			}
		}
		
		if (selectedDocumentIds.isEmpty()) {
			throw new InvalidParameterException("No document IDs selected");
		}
		
		List<WordCloudResult> results = new ArrayList<WordCloudResult>();
		
		for (Long documentId : selectedDocumentIds) {;
			DLFileEntry document = DLFileEntryLocalServiceUtil.getDLFileEntry(documentId);
			//BufferedReader br=null;
			//br = new BufferedReader(new FileReader(document.getTitle()));
			//br = new BufferedReader(bew FileReader(document));
			
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[Math.abs((int)document.getSize())];
			
			InputStream stream = document.getContentStream();
			if(stream.read(buffer)==-1){
				System.out.println("Entire file parsed");
			}
			
			stream.close();
			
			String lines = new String(buffer);
			//System.out.println("Line: "+lines);
			//Making the dictionary
			Pattern p = Pattern.compile("[\\w']+");
			Matcher m = p.matcher(lines);
			System.out.println("Matcher starts:");
			dict.put("Test", 1);
			while(m.find()){
				String word = "";
				word=lines.substring(m.start(), m.end());
				System.out.println("Splitting"+word);
				if(dict.containsKey(word)){
					int count1  = dict.get(word);
					count1++;
					dict.put(word, count1);
					//System.out.println("Inside"+word);
				}
				else if(!(h.contains(word)) && !word.matches("[0-9]+") && word.length()>2){
					dict.put(word, 0);
					//System.out.println(word);
				}
				 //System.out.println("out");
			}
			if(dict.size()<3){
				GetStopWords stop2= new GetStopWords(0);
		        HashSet h1 = stop2.getStopList();
				//dict.remove("Test");
				char chinese[]=lines.toCharArray();
				for(char c:chinese){
					String cword = ""+c;
					if(!cword.isEmpty()&&!cword.equals("")&&!cword.equals(" ")&&!h1.contains(cword)){
						if(!dict.containsKey(cword)){
							dict.put(cword, 1);
						}
						else{
							int count1  = dict.get(cword);
							count1++;
							dict.put(cword, count1);
						}
					}
						
				}
				//String s = new String();
			}
			sorted_map.putAll(dict);
			//System.out.println("results: "+sorted_map);
			//System.out.println(lines);
		
			
			//////////////////// Previous Code reused
			String firstLine;
			
			
			count = Long.toString(document.getSize());

			results.add(new WordCloudResult(document, sorted_map, count));
			//results.add(new FirstLinesResult(document, firstLine));
		}
		
		//SessionMessages.add(request, "results", results);
		//System.out.println(SessionMessages.get(request, "results"));
		//System.out.println(results.get(0).getLine());
		for(int i=0; i< results.size();i++) {
			response.setRenderParameter("WordArray"+i, results.get(i).getWordArray());
			response.setRenderParameter("title"+i, results.get(i).getContent().getTitle());
			response.setRenderParameter("frequency_count"+i, results.get(i).getFreqCount());
			response.setRenderParameter("filecounts"+i, results.get(i).getFileCount());
		}
		
		response.setRenderParameter("resultSize", Integer.toString(results.size()));
		response.setRenderParameter("jspPage", "/html/maps/viewcloud.jsp");
	}

	
}
class ValueComparator implements Comparator<String> {

    Map<String, Integer> base;
    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}