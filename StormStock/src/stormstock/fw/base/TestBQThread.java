package stormstock.fw.base;

import stormstock.fw.base.BQThread.BQThreadRequest;

public class TestBQThread {
	
	public static class TestRequest extends BQThreadRequest
	{
		public TestRequest(int index)
		{
			m_index = index;
		}
		@Override
		public void doAction() {
			BLog.output("TEST", "TestRequest doAction! m_index [%d]\n", m_index);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		public int m_index;
	}

	public static void main(String[] args) {
		BLog.output("TEST", "TestBQThread Begin\n");
		
		BQThread cBQThread = new BQThread();
		
		cBQThread.startThread();
		
		for(int i=0; i<100; i++)
		{
			cBQThread.postRequest(new TestRequest(i));
		}
		
		cBQThread.stopThread();
		
		BLog.output("TEST", "TestBQThread End\n");
	}
}
