package xk.crawler.UnitTest;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import xk.crawler.simpleCrawler.ShopCrawler;

public class ShopCrawlerTest {
	ShopCrawler sc = new ShopCrawler();
	
	@Test
	public void testInit() {
		sc.init();
		sc.status();
	}
	
	@Test
	public void testParseAndStore() {
		String url = "http://shop.kongfz.com/book/15406";
		sc.init();
		try {
			Document doc = Jsoup.connect(url).timeout(0).get();
			System.out.println(doc.location());
			sc.parseAndStore(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
