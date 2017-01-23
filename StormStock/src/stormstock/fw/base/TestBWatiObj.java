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
				Thread.sleep(700);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			m_WaitObj.Notify();
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
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
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BLog.output("TEST", "BWaitObj.Wait ...1\n");
		cBWaitObj.Wait(Long.MAX_VALUE);
		BLog.output("TEST", "BWaitObj.Wait ...1 Return\n");
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BLog.output("TEST", "BWaitObj.Wait ...2\n");
		cBWaitObj.Wait(Long.MAX_VALUE);
		BLog.output("TEST", "BWaitObj.Wait ...2 Return\n");
	}
}
