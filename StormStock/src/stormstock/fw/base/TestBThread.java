package stormstock.fw.base;

public class TestBThread {
	
	public static class TestThread extends BThread 
	{
		@Override
		public void run() {
			BLog.output("TEST", "TestThread Run\n");
			while(!checkQuit())
			{
				BLog.output("TEST", "TestThread Running...\n");
				Wait(Long.MAX_VALUE);
			}
		}
		
	}
	public static void main(String[] args) {
		BLog.output("TEST", "Test TestBThread begin\n");
		
		TestThread cThread = new TestThread();
		
		cThread.startThread();
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		cThread.stopThread();
		
		BLog.output("TEST", "Test TestBThread end\n");
	}
}
