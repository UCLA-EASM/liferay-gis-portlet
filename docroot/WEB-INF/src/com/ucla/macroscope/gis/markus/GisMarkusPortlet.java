package com.ucla.macroscope.gis.markus;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
		
		List<String> locations = new ArrayList<String>();
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
			for(Element e: es) {
				locations.add(e.text());
			}
			System.out.println(locations);
		}
		
		for(int i=0 ; i< locations.size(); i++) {
			response.setRenderParameter("location"+ i, locations.get(i));
		}
		response.setRenderParameter("locationsResultSize", Integer.toString(locations.size()));
		response.setRenderParameter("jspPage", "/html/gismarkus/locationsResult.jsp");
	}
}
