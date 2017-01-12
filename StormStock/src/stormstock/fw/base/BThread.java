package stormstock.fw.base;

abstract public class BThread {
	public BThread()
	{
		m_thread = new InThread(this);
	}
	
	// ����ʱ ��ֹͣ�߳�
	@Override
	public void finalize()
	{
		this.stopThread();
	}
	
	abstract public void run();
		
	public boolean checkQuit()
	{
		return m_thread.checkQuit();
	}
	
	public boolean Wait()
	{
		return m_thread.Wait();
	}
	
	public boolean Notify()
	{
		return m_thread.Notify();
	}
	
	public boolean startThread()
	{
		return m_thread.startThread();
	}
	
	public boolean stopThread()
	{
		return m_thread.stopThread();
	}
	
	public boolean checkRunning()
	{
		return m_thread.checkRunning();
	}
	
	public static void sleep(int msec)
	{
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		};
	}
	
	private InThread m_thread;
	
	private class InThread extends Thread
	{
		public InThread(BThread cBThread)
		{
			m_cBThreadRef = cBThread;
			m_bQuit = false;
			m_bRunning = false;
			m_cBWaitObj = new BWaitObj();
		}
		@Override
        public void run()
        {
			m_cBThreadRef.run();
			m_bRunning = false;
        }
		boolean checkQuit()
		{
			return m_bQuit;
		}
		boolean checkRunning()
		{
			return m_bRunning;
		}
		boolean Wait()
		{
			m_cBWaitObj.Wait();
			return true;
		}
		boolean Notify()
		{
			m_cBWaitObj.Notify();
			return true;
		}
		boolean startThread()
		{
			m_bRunning = true;
			super.start();
			return true;
		}
		boolean stopThread()
		{
			if(!m_bQuit)
			{
				m_bQuit = true;
				m_cBWaitObj.Notify();
				try {
					super.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
		private BThread m_cBThreadRef;
		private boolean m_bQuit;
		private boolean m_bRunning;
		private BWaitObj m_cBWaitObj;
	}
}
