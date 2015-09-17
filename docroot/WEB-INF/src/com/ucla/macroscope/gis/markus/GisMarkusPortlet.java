package com.ucla.macroscope.gis.markus;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import edu.ucla.macroscope.gis.FirstLinesResult;

/**
 * Portlet implementation class GisMarkusPortlet
 */
public class GisMarkusPortlet extends MVCPortlet {
	
	public void parseHtmlforLocations(ActionRequest request, ActionResponse response) 
			throws InvalidParameterException, PortalException, SystemException, SQLException, IOException, PortletException {
		
		ArrayList<Long> selectedDocumentIds = new ArrayList<Long>();
		
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
		
		for (Long documentId : selectedDocumentIds) {
//			InputStream result = null;
			DLFileEntry document = DLFileEntryLocalServiceUtil.getDLFileEntry(documentId);
			
//			result = DLFileEntryLocalServiceUtil.getFileAsStream(document.getUserId(), document
//                    .getFolderId(), document.getName(), document.getVersion());
//			
			Long userId = document.getUserId();
			String version = document.getVersion();
//			String latestfileVersion = document.getLatestFileVersion(true).toString();
//			document.getLatestFileVersion(true).getFileVersionId();
//			Long userId = document.getLatestFileVersion(true).getUserId();
			File docFile = DLFileEntryLocalServiceUtil.getFile(userId, documentId, version, false);
			
			Document htmlDoc = Jsoup.parse(docFile, "UTF-8");
			Elements es = htmlDoc.getElementsByAttributeValue("type", "placeName");
			System.out.println(es.text());
			List<String> locations = new ArrayList<String>();
			for(Element e: es) {
				locations.add(e.text());
			}
			System.out.println(locations);
		}
	}
	
	public void loadFirstLines(ActionRequest request, ActionResponse response) 
			throws InvalidParameterException, PortalException, SystemException, SQLException, IOException, PortletException {
		
		ArrayList<Long> selectedDocumentIds = new ArrayList<Long>();
		String wordCount = "0";

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
				firstLine = possibleFirstLines.split("[\\r\\n]+")[0];
			} else {
				firstLine = "<Empty file>";
			}
			wordCount = Long.toString(document.getSize());

			results.add(new FirstLinesResult(document, firstLine, wordCount));
		}

		for(int i=0; i< results.size();i++) {
			response.setRenderParameter("result"+i, results.get(i).getLine());
			response.setRenderParameter("title"+i, results.get(i).getContent().getTitle());
			response.setRenderParameter("wordCount"+i, results.get(i).getWordCount());
		}
		
		Set<String> locations = new HashSet<String>();
//		try {
//			locations = classifyResults(results);
//		} catch(Exception e) {
//			
//		}
		String[] locationList = locations.toArray(new String[locations.size()]);
		for(int i=0; i<locations.size();i++) {
			response.setRenderParameter("locations", locationList[i]);
		}
		response.setRenderParameter("locationsSize", Integer.toString(locationList.length));
		response.setRenderParameter("resultSize", Integer.toString(results.size()));
		response.setRenderParameter("jspPage", "/html/gis/results.jsp");
		
	}

}
