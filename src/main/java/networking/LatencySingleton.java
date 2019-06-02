package networking;

public class LatencySingleton {
	
	private static LatencySingleton latency;
	public int milliSeconds;
	
	private LatencySingleton() {
		milliSeconds = 50;
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
