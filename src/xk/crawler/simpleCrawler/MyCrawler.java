package xk.crawler.simpleCrawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Stack;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import xk.crawler.utils.DBFactory;
import xk.crawler.utils.FileUtils;

public class MyCrawler implements ICrawler{
	// some meta info
	String[] extraction = {"bookName", "author", "publisher", "date"};
	String sql = "insert into book values(?,?,?,?,?,?)";
	
	// control information
	final static int sleepTime = 1*1000;
	final static int timeOut = 0;
	final static int pageMaximum = 200;
	final static int numPerPage = 50;
	int errorID = 0;
	
	private Stack<String> urlStack = null;
	private String prefix = null;
	private int curPageIndex = 1;
	private int curMaxPageIndex = 0;
	private String curURL = null;
	//private boolean findSubclasses = true;
	private Properties prop = null;
	
	public void init() {
		prop = FileUtils.getProperties("book.properties");
		Document doc = FileUtils.getDocument();
		curPageIndex = 1;
		curMaxPageIndex = 0;
		errorID = 0;
		
		Element metaInfo = doc.getElementById("kongfz");
		prefix = metaInfo.getElementById("search-prefix").ownText();
		
		Elements rootCats = doc.getElementById("kongfz-cats").children();
		urlStack = new Stack<String>();
		
		for (Element cat : rootCats) {
			urlStack.push(prefix + "/product/n000001h194901cat_" + cat.id());
		}
		
		curURL = urlStack.peek();
		//findSubclasses = true;
	}
	
	public boolean isEmpty() {
		return urlStack.isEmpty();
	}
	
	private boolean hasSubClasses(Document doc) throws IOException {
		
		Element conditionBox = doc.getElementById("conditionBox");
		Elements subClasses = conditionBox.children();
		
		if (subClasses.size() > 1) {
			if (subClasses.get(1).attr("param-select").equals("cat_")) {
				return true;
			}
		}
		return false;
	}
	
	/*private boolean hasSubClasses(String url) {
		if (url.charAt(0) == '0') {
			return true;
		} else {
			return false;
		}
	}
	
	public Document getNextPage() {
		Document doc = null;
		try {
			curURL = urlStack.peek();
			// while current url has subclass
			while (hasSubClasses(curURL)) {
				doc = Jsoup.connect(curURL.substring(1)).timeout(timeOut).get();
				urlStack.pop();
				Element conditionBox = doc.getElementById("conditionBox");
				Elements subClasses = conditionBox.children();
				Elements subURLs = subClasses.get(1).select("li");
				for (Element e:subURLs) {
					urlStack.push(prefix+e.childNode(0).attr("href"));
				}
				curURL = urlStack.peek();
				Thread.sleep(sleepTime);
				doc = Jsoup.connect(curURL).timeout(timeOut).get();
			}
		}
		return doc;
	}*/
	
	public Document getNextPage() {
		Document doc = null;
		Element conditionBox = null;
		Elements subClasses = null;
		Elements subURLs = null;
		try {
			curURL = urlStack.peek();
			
			if (curURL.endsWith("label")) {
				curURL = curURL.substring(0, curURL.length()-5);
			} else {
				doc = Jsoup.connect(curURL).timeout(timeOut).get();
				// while current url has subclass
				while (hasSubClasses(doc)) {
					urlStack.pop();
					conditionBox = doc.getElementById("conditionBox");
					subClasses = conditionBox.children();
					subURLs = subClasses.get(1).select("li");          // if it has subclasses, then the index could be larger than 1
					for (Element e:subURLs) {
						urlStack.push(prefix+e.childNode(0).attr("href"));
					}
					curURL = urlStack.peek();
					Thread.sleep(sleepTime);
					doc = Jsoup.connect(curURL).timeout(timeOut).get();
				}

				conditionBox = doc.getElementById("conditionBox");
				subClasses = conditionBox.children();
				
				urlStack.pop();
				for (int i = 1; i < subClasses.size(); ++i) {
					subURLs = subClasses.get(i).select("li");
					for (Element e:subURLs) {
						urlStack.push(prefix+e.childNode(0).attr("href") + "label");      // attach a label to those labeled url
					}
				}
				curPageIndex = 1;
				
				curURL = urlStack.peek();
				curURL = curURL.substring(0, curURL.length()-5);          // recover url
				//findSubclasses = false;
			}
			
			// get the real url with page index
			if (curURL.endsWith("/")) {
				curURL = curURL.substring(0, curURL.length()-1) + "w" + curPageIndex;
			} else {
				curURL = curURL + "w" + curPageIndex;
			}
			
			// get the real page
			doc = Jsoup.connect(curURL).timeout(timeOut).get();
			//System.out.println(curURL);
			
			// update max page index
			if (curMaxPageIndex == 0) {
				Element totalFound = doc.getElementById("b_c_nav").getElementsByClass("red1").first();
				int tmp = Integer.parseInt(totalFound.ownText().trim());
				curMaxPageIndex = tmp/numPerPage + (tmp%numPerPage == 0 ? 0 : 1);
				curMaxPageIndex = curMaxPageIndex > pageMaximum ? pageMaximum : curMaxPageIndex;
			}
			
			//System.out.println(curPageIndex + " " + curMaxPageIndex);
			++curPageIndex;
			if (curPageIndex > curMaxPageIndex) {
				curPageIndex = 1;
				curMaxPageIndex = 0;
				//findSubclasses = true;
				urlStack.pop();
			}
			
		} catch (Exception e){
			System.out.println("Error in get Next webPage!");
			System.out.println("============== Error Message Begin==============");
			System.out.println(e.getMessage());
			System.out.println("============== Error Message End==============");
			if (doc != null) {
				curPageIndex = 1;
				curMaxPageIndex = 0;
				//findSubclasses = true;
				urlStack.pop();
			}
			doc = null;
			//e.printStackTrace();
		}
		return doc;
	}
	
	public void status() {
		System.out.println("Current prefix is : " + prefix);
		System.out.println("Current stack top is : " + urlStack.peek());
		System.out.println("Current content size of the url stack is :" + urlStack.size());
		/*for (String s : urlStack) {
			System.out.println(s);
		}*/
	}
	
	public boolean exists(Connection conn, String id) {
		if (id == null || id.equals("")) {
			return true;
		}
		Statement stmt = null;
		ResultSet rs = null;
		try {
			 stmt = conn.createStatement();
			 rs = stmt.executeQuery("select * from book where bookID = " + id);
			if (rs.first()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return false;
	}

	@Override
	public void parseAndStore(Document doc) {
		Connection conn = DBFactory.getConnection();
		PreparedStatement pstmt = null;
		
		String bookID = null;
		String[] bookProps = new String[extraction.length];
		String bookShopID = null;
		
		try {
			pstmt = conn.prepareStatement(sql);
			Elements books = doc.getElementById(prop.getProperty("contentID")).children();
			for (int bookind = 0; bookind < books.size()-1; ++bookind) {  // the last element is overall pages information
				Element book = books.get(bookind);
				bookID = book.attr(prop.getProperty("bookID"));
				
				if (exists(conn, bookID)) {
					//System.out.println("exists");
					continue;
				}
				
				//System.out.println("bookID " + bookID);
				
				for (int i = 0; i < bookProps.length; ++i) {
					String[] paths = prop.getProperty(extraction[i]).split(",");
					Element tmpEle = book;
					try {
						for (int j = 0; j < paths.length; ++j) {
							tmpEle = tmpEle.child(Integer.parseInt(paths[j]));
						}
						bookProps[i] = tmpEle.ownText();
					} catch (IndexOutOfBoundsException e) {
						bookProps[i] = "";
					}
					
					//System.out.println(extraction[i] + " " + bookProps[i]);
				}
				bookShopID = book.attr(prop.getProperty("bookShopID"));
				
				//System.out.println("bookShopID " + bookShopID);
				//System.out.println("========================================");
				
				pstmt.setString(1, bookID);
				for (int i = 0; i < bookProps.length; ++i) {
					pstmt.setString(i+2, bookProps[i]);
				}
				pstmt.setString(6, bookShopID);
				
				pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("book insert error!");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("==================== Error in Parsing Begin ================");
			System.out.println(doc.toString().lastIndexOf("</html>"));
			System.out.println(doc.location());
			e.printStackTrace();
			System.out.println("==================== Error in Parsing End ==================");
			FileWriter fw = null;
			try {
				fw = new FileWriter(new File("error"+(++errorID)+".log"));
				fw.write(doc.location() + "\n");
				fw.write(doc.toString());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("Error writing error log!");
				e1.printStackTrace();
			} finally {
				if (fw != null) {
					try {
						fw.flush();
						fw.close();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
			}
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
