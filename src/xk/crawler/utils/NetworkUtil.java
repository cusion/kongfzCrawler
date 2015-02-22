package xk.crawler.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class NetworkUtil {
	final static String cnn = "VPN";
	final static String uname = "cusion89";
	final static String pswd = "261935";
	final static int maxTimes = 400;
	final static int sleepTime = 10*1000;
	/*
	 * Manipulate Local Network, depend on OS batch script, currently using windows rasdial command
	 * Connect: rasdial connectionName username password
	 * Disconnect: rasdial /disconnect
	 */
	
	public boolean reconnect() {
		return reconnect(cnn, uname, pswd);
	}
	
	public boolean reconnect(String connectionName, String username, String password) {
		return reconnect(cnn, uname, pswd, maxTimes);
	}
	
	public boolean reconnect(String connectionName, String username, String password, int maxtimes) {
		disconnect(connectionName);
		for (int i = 0; i < maxtimes; ++i) {
			if (connect(connectionName, username, password)) {
				return true;
			}
			System.out.println("Retrying to connect...");
			try {
				Thread.sleep(sleepTime*(i+1));
			} catch (InterruptedException e) {
				System.out.println("reconnect thread sleep error!");
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	public boolean connect() {
		return connect(cnn, uname, pswd);
	}
	
	public boolean connect(String connectionName, String username, String password) {
		String vpnCmd = "rasdial " + connectionName + " " + username + " "
						+ password;
		String cmdResponse = executeCmd(vpnCmd);
		System.out.println(cmdResponse);
		
		if (cmdResponse.indexOf(connectionName) == cmdResponse.lastIndexOf(connectionName)) {
			return false;
		} else {
			return true;
		}
	}
	
	public void disconnect() {
		String vpnCmd = "rasdial /disconnect";
		System.out.println(executeCmd(vpnCmd));
	}
	public void disconnect(String connectionName) {
		String vpnCmd = "rasdial " + connectionName + " /disconnect";
		System.out.println(executeCmd(vpnCmd));
	}
	
	public String executeCmd(String strCmd) {
		Process p = null;
		try {
			p = Runtime.getRuntime().exec("cmd /c " + strCmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Get Process Error!");
			e.printStackTrace();
		}

		StringBuilder sbCmd = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String line = null;
		
		try {
			while ((line = br.readLine()) != null) {
				sbCmd.append(line+'\n');
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Read cmd line error!");
			e.printStackTrace();
		}
		
		return sbCmd.toString();
	}
}
