package networking;

public class LatencySingleton {
	
	private static LatencySingleton latency;
	public int milliSeconds;
	public String ip;
	
	private LatencySingleton() {
		milliSeconds = 50;
	}
	
	public static void setIP(String ip) {
		if(latency == null) {
			latency = new LatencySingleton();
		}
		latency.ip = ip; 
	}
	
	public static String getIP() {
		if(latency == null) {
			latency = new LatencySingleton();
		}
		return latency.ip;
	}
	
	public static void setLatency(int milliSeconds) {
		if(latency == null) {
			latency = new LatencySingleton();
		}
		latency.milliSeconds = milliSeconds;
	}
	
	public static int getLatency() {
		if(latency == null) {
			latency = new LatencySingleton();
		}
		return latency.milliSeconds;
	}
}
