package edu.ucla.macroscope.gis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;


/**
 * Portlet implementation class GisPortlet
 */
public class GisPortlet extends MVCPortlet {
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
			response.setRenderParameter("filecounts"+i, results.get(i).getFileCount());
		}
		
		response.setRenderParameter("resultSize", Integer.toString(results.size()));
		response.setRenderParameter("jspPage", "/html/gis/results.jsp");
		
		try {
			Set<String> locations = classifyResults(results);
		} catch(Exception e) {
			
		}
	}

	private Set<String> classifyResults(List<FirstLinesResult> results) throws Exception {
		String serializedClassifier = "classifiers/chinese.misc.distsim.crf.ser.gz";
		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);
		Set<String> locations = new HashSet<String>();
		String samples[] = new String[results.size()];
		for(int i=0; i<results.size(); i++) {
			samples[i] = results.get(i).getContent().toString();
			String output = classifier.classifyToString(samples[i]);
			String[] words = output.split(" ");
			for(int j=0;j<words.length;j++) {
				if(words[j].contains("GPE")) {
					locations.add(words[j].substring(0, words[j].length()-4));
				}
			}
		}
		return locations;
	}
}
