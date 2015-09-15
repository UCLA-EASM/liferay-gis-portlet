package edu.ucla.macroscope.gis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MarkusDemo {
	public static void main(String[] args) throws Exception {
		File input = new File("/Users/shkapur/Downloads/test_export.html");
		Document doc = Jsoup.parse(input, "UTF-8");
		System.out.println(doc);
		System.out.println();System.out.println();
		
//		Elements links = doc.getElementsByTag("span");
//		System.out.println(links.size());
//		for(Element link: links) {
//			System.out.println(link);
//			Elements e = link.getElementsByAttributeValue("type", "placeName");
//			System.out.println(e.text());
//		}
//		
		Elements es = doc.getElementsByAttributeValue("type", "placeName");
		System.out.println(es.text());
		List<String> locations = new ArrayList<String>();
		for(Element e: es) {
			locations.add(e.text());
		}
		System.out.println(locations);
	}
}
