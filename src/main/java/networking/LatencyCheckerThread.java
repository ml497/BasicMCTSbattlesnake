package networking;
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  
import java.io.*; 
import java.net.*; 

class LatencyCheckerThread implements Runnable {   
    private InetAddress inet;
    
    public LatencyCheckerThread(){
    	
    }
    
    public void run() {  
    	while(true) {
    		try {
	    		if(inet == null) {
	    			if(LatencySingleton.getIP() != null) {
						inet = InetAddress.getByName(LatencySingleton.getIP());
	    			}
	    		}
	    		if(inet != null) { 
					long start = System.currentTimeMillis();
					if (inet.isReachable(5000)) {
						long end = System.currentTimeMillis();
						int delta = (int) (end - start);
						LatencySingleton.setLatency(delta);
						System.out.println("Current latency: " + delta);
					} else {
						System.out.println("Connection with host server lost");
					}
		    	}
	    		Thread.sleep(100);
    		} catch( Exception e) {
    			System.out.println(e.getMessage());
    		}
    	}
    }
}  
