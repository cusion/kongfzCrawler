package xk.crawler.utils;

import java.util.Scanner;

import org.jsoup.nodes.Document;

import xk.crawler.simpleCrawler.ICrawler;
import xk.crawler.simpleCrawler.MyCrawler;
import xk.crawler.simpleCrawler.ShopCrawler;
import xk.crawler.utils.NetworkUtil;

public class Main {
	public static void main(String[] args) {
		ICrawler mc = new ShopCrawler();
		//ICrawler mc = new MyCrawler();
		NetworkUtil net = new NetworkUtil();
		mc.init();
		
		int sleepTime = 500;
		int cnt = 0;
		int total = 0;
		Document doc = null;
		int resumePoint = 188800;
		int resumeCnt = 0;
		
		while (!mc.isEmpty()) {
			try {
				Thread.sleep(sleepTime);
				doc = mc.getNextPage();
				if (doc == null) {
					if (net.reconnect()) {
						++cnt;
						System.out.println("Reconnect succecced " + cnt);
						sleepTime += 500;
					} else {
						System.out.println("There must be some errors I could not handle!");
						System.out.println("Please manually correct it and then enter something to continue...");
						Scanner pauseScanner = new Scanner(System.in);
						String tmp = pauseScanner.next();
					}
				} else {
					//int tmp = 0;
					//tmp = doc.getElementById("mainContent").children().size() - 1;
					if (doc != null){
						//System.out.println((++total) + " " + doc.location() + " <---> " + tmp);
						System.out.println((++total) + " " + doc.location());
					}
					/*if (resumeCnt < resumePoint) {
						resumeCnt += tmp;
						continue;
					}*/
					sleepTime = 500;
					mc.parseAndStore(doc);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(doc.location() + " " + e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
		System.out.println("Crawler done!");
	}
}
