package xk.crawler.simpleCrawler;

import org.jsoup.nodes.Document;

public interface ICrawler {
	public boolean isEmpty();
	public Document getNextPage();
	public void init();
	public void status();
	public void parseAndStore(Document doc);
}
