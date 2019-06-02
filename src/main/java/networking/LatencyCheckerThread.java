package networking;
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  
import java.io.*; 
import java.net.*; 

class LatencyCheckerThread implements Runnable {   
    private InetAddress inet;
    
    public LatencyCheckerThread(String ipAddress){
    	try {
			inet = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }
    
    public void run() {  
    	while(true) {
			try {  
				long start = System.currentTimeMillis();
				if (inet.isReachable(5000)) {
					long end = System.currentTimeMillis();
					int delta = (int) (end - start);
					LatencySingleton.setLatency(delta);
					System.out.println("Current latency: " + delta);
				} else {
					System.out.println("Connection with host server lost");
				}
			} catch (Exception e) { 
				e.printStackTrace(); 
			}  
    	}
    }
}  
