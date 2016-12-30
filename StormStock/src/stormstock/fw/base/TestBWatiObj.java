package stormstock.fw.base;

public class TestBWatiObj {
	public static class TestThread extends Thread
	{
		public  TestThread(BWaitObj waitObj)
		{
			m_WaitObj = waitObj;
		}
		
		@Override
		public void run()
		{
			BLog.output("TEST", "TestThread run begin\n");
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			m_WaitObj.Notify();
			BLog.output("TEST", "TestThread run end\n");
		}
		
		private BWaitObj m_WaitObj;
	}
	
	public static void main(String[] args) {
		
		BWaitObj cBWaitObj = new BWaitObj();
		
		TestThread cThread = new TestThread(cBWaitObj);
		cThread.start();
		BLog.output("TEST", "BWaitObj.Wait ...\n");
		cBWaitObj.Wait();
		BLog.output("TEST", "BWaitObj.Wait Return\n");
	}
}
