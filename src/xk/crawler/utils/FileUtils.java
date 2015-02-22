package xk.crawler.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

public class FileUtils {
	// denote where the xml file locates
	final static String xmlFile = "target.xml";
	final static String txtFile = "bookshopURLs.txt";
	
	public static Document getDocument() {
		return getDocument(xmlFile);
	}
	
	public static Document getDocument(String filepath) {
		Document doc = null;
		InputStream is = null;
		try {
			is = new FileInputStream(filepath);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			System.out.println("FileUtils: no such XML file path exists");
			e1.printStackTrace();
			return null;
		}
		try {
			doc = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
		} catch (Exception e) {
			System.out.println("Parse file to XML Document error!");
			e.printStackTrace();
		}
		return doc;
	}
	
	public static BufferedReader getFileInput() {
		return getFileInput(txtFile);
	}
	
	public static BufferedReader getFileInput(String filePath) {
		BufferedReader bfr = null;
		
		try {
			FileInputStream fis = new FileInputStream(filePath);
			bfr = new BufferedReader(new InputStreamReader(fis));
		} catch (Exception e) {
			System.out.println("Input File not found!");
			e.printStackTrace();
		}
		
		return bfr;
	}
	
	public static Properties getProperties(String filePath) {
		Properties prop = null;
		try {
			FileInputStream fis = new FileInputStream(filePath);
			prop = new Properties();
			prop.load(fis);
		} catch (Exception e) {
			System.out.println("Load Property file error!");
			e.printStackTrace();
		}
		return prop;
	}
}
