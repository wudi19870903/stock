package stormstock.fw.base;

import stormstock.fw.base.BAutoSync.BSyncObj;

public class BWaitObj {
	
	public BWaitObj()
	{
		m_sync = new BSyncObj();
		m_waitObj = new Object();
		m_bNotified = false;
	}

	public boolean Wait(long msec)
	{
		try {
			synchronized(m_waitObj)
			{
				if(!m_bNotified)
				{
					m_waitObj.wait(msec);
				}
				m_bNotified = false;
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
			m_bNotified = true;
		}
		return true;
	}
	
	private BSyncObj m_sync;
	private Object m_waitObj;
	private boolean m_bNotified;
}
