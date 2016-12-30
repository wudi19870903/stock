package stormstock.fw.base;

public class BWaitObj {
	
	public BWaitObj()
	{
		m_waitObj = new Object();
	}

	public boolean Wait()
	{
		try {
			synchronized(m_waitObj)
			{
				m_waitObj.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean Notify()
	{
		synchronized(m_waitObj)
		{
			m_waitObj.notify();
		}
		return true;
	}
	
	private Object m_waitObj;
}
