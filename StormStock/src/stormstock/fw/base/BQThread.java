package stormstock.fw.base;

import java.util.ArrayList;
import java.util.List;

import stormstock.fw.base.BAutoSync.BSyncObj;

public class BQThread {
	
	abstract public static class BQThreadRequest
	{
		abstract public void doAction();
	}
	
	public BQThread()
	{
		m_thread = new BQThreadEntity();
	}
	public boolean postRequest(BQThreadRequest cReq)
	{
		if(m_thread.checkRunning())
		{
			return m_thread.postRequest(cReq);
		}
		return false;
	}
	
	public boolean startThread()
	{
		return m_thread.startThread();
	}
	
	public boolean stopThread()
	{
		return m_thread.stopThread();
	}
	
	
	private static class BQThreadEntity extends BThread
	{
		public BQThreadEntity()
		{
			m_requestList = new ArrayList<BQThreadRequest>();
			m_syncObj = new BSyncObj();
		}
		@Override
		public void run() {
			while(!super.checkQuit())
			{
				while(true)
				{
					BQThreadRequest cReq = popRequest();
					if(null == cReq) break;
					cReq.doAction();
				}
				super.Wait(Long.MAX_VALUE);
			}
		}
		public boolean postRequest(BQThreadRequest cReq)
		{
			m_syncObj.Lock();
			m_requestList.add(cReq);
			m_syncObj.UnLock();
			super.Notify();
			return true;
		}
		public BQThreadRequest popRequest()
		{
			BQThreadRequest cReq = null;
			m_syncObj.Lock();
			if(m_requestList.size()>0)
			{
				cReq = m_requestList.get(0);
				m_requestList.remove(0);
			}
			m_syncObj.UnLock();
			return cReq;
		}
		private List<BQThreadRequest> m_requestList;
		private BSyncObj m_syncObj;
	}
	
	private BQThreadEntity m_thread;
}
