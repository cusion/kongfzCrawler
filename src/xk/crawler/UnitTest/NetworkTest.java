package xk.crawler.UnitTest;

import java.io.IOException;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import xk.crawler.utils.NetworkUtil;

public class NetworkTest {
	NetworkUtil net = new NetworkUtil();
	
	@Test
	public void testExecuteCmd() {
		String cmd = "dir";
		System.out.println(net.executeCmd(cmd));
	}
	
	@Test
	public void testReconnect() {
		if (net.reconnect()) {
			System.out.println("SUCCESS");
		} else {
			System.out.println("EROOR");
		}
	}
	
	@Test
	public void testConnection() {
		int recTime = 0;
		int maxRecTime = 60*1000, step = 1000, timeOut = 10*1000;
		int cnt = 0, reconnectCnt = 0;
		
		while (true) {
			try {
				Document doc = Jsoup.connect("http://www.baidu.com/#wd="+cnt).timeout(timeOut).get();
				//System.out.println(cnt + " " + doc.title());
				++cnt;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Error Connect to target URL whilt cnt = " + cnt);
				//e.printStackTrace();
				recTime = 0;
				Random r = new Random();
				while (recTime < maxRecTime) {
					recTime += r.nextInt(step);
					recTime = recTime % maxRecTime;
					System.out.println("Trying to reconnect " + reconnectCnt + " sleep time " + recTime);
					try {
						Thread.sleep(recTime);
					} catch (InterruptedException se) {
						// TODO Auto-generated catch block
						System.out.println("Thread sleep error!");
						se.printStackTrace();
					}
					if (net.reconnect()) {
						break;
					}
				}
			}
		}
	}
}
