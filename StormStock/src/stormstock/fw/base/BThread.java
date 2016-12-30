package stormstock.fw.base;

abstract public class BThread {
	public BThread()
	{
		m_thread = new InThread(this);
	}
	
	abstract public void run();
		
	public boolean checkQuit()
	{
		return m_thread.checkQuit();
	}
	
	public boolean Wait()
	{
		return m_thread.waiting();
	}
	
	public boolean startThread()
	{
		return m_thread.startThread();
	}
	
	public boolean stopThread()
	{
		return m_thread.stopThread();
	}
	
	private InThread m_thread;
	
	private class InThread extends Thread
	{
		public InThread(BThread cBThread)
		{
			m_cBThreadRef = cBThread;
			m_bQuit = false;
			m_cBWaitObj = new BWaitObj();
		}
		@Override
        public void run()
        {
			m_cBThreadRef.run();
        }
		boolean checkQuit()
		{
			return m_bQuit;
		}
		boolean waiting()
		{
			m_cBWaitObj.Wait();
			return true;
		}
		boolean startThread()
		{
			super.start();
			return true;
		}
		boolean stopThread()
		{
			m_bQuit = true;
			m_cBWaitObj.Notify();
			return true;
		}
		private BThread m_cBThreadRef;
		private boolean m_bQuit;
		private BWaitObj m_cBWaitObj;
	}
}
