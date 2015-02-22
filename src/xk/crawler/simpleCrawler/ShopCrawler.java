package xk.crawler.simpleCrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import xk.crawler.dao.BookShopBean;
import xk.crawler.utils.DBFactory;
import xk.crawler.utils.FileUtils;

class PropNode {
	private int index;
	private Pattern pattern;
	
	public PropNode(){}
	public PropNode(int index, Pattern pattern){
		this.index = index;
		this.pattern = pattern;
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public Pattern getPattern() {
		return pattern;
	}
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
}

public class ShopCrawler implements ICrawler{
	// some meta info
	String[] extraction = {"bookShopName", "shopOwner", "address", "telephone"};
	String sql = "insert into shop values(?,?,?,?,?)";
	
	private HashMap<String, PropNode> propMap = null;
	private Queue <String> shopURLs = null;
	private Properties prop = null;
	final static int timeOut = 0;
	
	public void init() {
		BufferedReader bfr = FileUtils.getFileInput();
		prop  = FileUtils.getProperties("bookshop.properties");
		
		shopURLs = new LinkedList<String>();
		propMap = new HashMap<String, PropNode>();
		
		// init properties
		for (int i = 0; i < extraction.length; ++i) {
			String[] ss = prop.getProperty(extraction[i]).split(",");
			//System.out.println(ss[0] + " + test + " + ss[1]);
			propMap.put(extraction[i], new PropNode(Integer.parseInt(ss[0]), Pattern.compile(ss[1])));
		}		
		
		String tmpLine = null;
		try {
			while ((tmpLine=bfr.readLine()) != null) {
				shopURLs.add(tmpLine);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("read shop url file error!");
			e.printStackTrace();
		} finally {
			if (bfr != null) {
				try {
					bfr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean isEmpty() {
		return shopURLs.isEmpty();
	}
	
	public Document getNextPage() {
		Document doc = null;
		
		try {
			doc = Jsoup.connect(shopURLs.peek()).timeout(timeOut).get();
			shopURLs.poll();
		} catch (Exception e) {
			System.out.println("Error in get next page!");
			System.out.println("============ Error Message Begin ============");
			System.out.println(e.getLocalizedMessage());
			System.out.println("============ Error Message End =============");
			doc = null;
		}
		return doc;
	}

	@Override
	public void status() {
		System.out.println(shopURLs.peek());
		System.out.println(shopURLs.size());
	}

	@Override
	public void parseAndStore(Document doc) {
		if (doc == null) return;
		Elements shopInfo = doc.getElementById(prop.getProperty("contentID")).select("ul");
		//BookShopBean bs = new BookShopBean();
		String bookShopID = null;
		String[] extractionProps = new String[extraction.length];
		
		// get bookShopID
		Pattern shopIDpat = Pattern.compile("\\d+");
		Matcher mat = shopIDpat.matcher(doc.location());
		mat.find();
		bookShopID = mat.group();
		
		// get extraction properties
		for (int i = 0; i < extraction.length; ++i) {
			int index = propMap.get(extraction[i]).getIndex();
			if (index >= shopInfo.size()) {
				index = shopInfo.size()-1;
			}
			//System.out.println(index + "\n" + shopInfo.get(index).toString() + "\n"+ propMap.get(extraction[i]).getPattern().pattern());
			mat = propMap.get(extraction[i]).getPattern().matcher(shopInfo.get(index).toString());
			
			if (mat.find()) {
				extractionProps[i] = mat.group(1);
				System.out.println(extractionProps[i]);
			} else {
				extractionProps[i] = "";
			}
			
			if (extraction[i].equals("telephone")&&(extractionProps[i].equals("") || 
					extractionProps[i].matches("[\\d１２３４５６７８９０]{2,4}-[\\d１２３４５６７８９０]{1,2}-[\\d１２３４５６７８９０]{1,2}"))) {
				//String recpatStr = "[\\s\\S]+?([\\d１２３４５６７８９０]{11})[\\s\\S]*";
				if (doc.getElementById(prop.getProperty("contentID")).nextElementSibling() == null) {
					continue;
				}
				String recpatStr = "[^\\d]+?([\\d１２３４５６７８９０]{3,4}-[\\d１２３４５６７８９０]{7,8}|[\\d１２３４５６７８９０]{8,11})[^\\d]+";
				Pattern recpat = Pattern.compile(recpatStr);
				Matcher recmat = recpat.matcher(doc.getElementById(prop.getProperty("contentID")).nextElementSibling().toString());
				
				if (recmat.find()) {
					extractionProps[i] = recmat.group(1);
					System.out.println(extractionProps[i]);
				}
			}
		}
		System.out.println("==============================");

		Connection conn = DBFactory.getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bookShopID);
			for (int i = 0; i < extractionProps.length; ++i) {
				pstmt.setString(i+2,extractionProps[i]);
			}
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Execute insert error!");
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
}
