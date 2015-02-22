package xk.crawler.UnitTest;
import org.jsoup.nodes.Document;
import org.junit.Test;

import xk.crawler.simpleCrawler.MyCrawler;

public class MyCrawlerTest {
	MyCrawler mcr = new MyCrawler();
	
	@Test
	public void testCrawler() throws InterruptedException {
		mcr.init();
		mcr.status();
		for (int i = 0; i < 300; ++i) {
			Document doc = mcr.getNextPage();
			Thread.sleep(1000);
			System.out.println(i + " " + doc.location());
		}
	}
}
