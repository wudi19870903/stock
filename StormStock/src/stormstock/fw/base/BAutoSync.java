package stormstock.fw.base;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BAutoSync {

	public static class BSyncObj
	{
		private Lock lock;
		public BSyncObj()
		{
			lock = new ReentrantLock();
		}
		public boolean Lock()
		{
			lock.lock(); 
			return true;
		}
		public boolean UnLock()
		{
			lock.unlock(); 
			return true;
		}
	}
	
	private BSyncObj m_cSyncObj;
	
	public BAutoSync(BSyncObj cSyncObj)
	{
		m_cSyncObj = cSyncObj;
		cSyncObj.Lock();
	}
	public void finalize()
	{
		m_cSyncObj.UnLock();
	}
}
