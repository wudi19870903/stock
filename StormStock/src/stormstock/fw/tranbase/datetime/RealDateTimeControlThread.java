package stormstock.fw.tranbase.datetime;

import stormstock.fw.base.BThread;
import stormstock.fw.base.BUtilsDateTime;
import stormstock.fw.base.BQThread.BQThreadRequest;

public class RealDateTimeControlThread extends BThread {
	
	public RealDateTimeControlThread(DateTimeControlerEntity cDateTimeControlerEntity)
	{
		m_dateTimeControlerEntity = cDateTimeControlerEntity;
	}

	@Override
	public void run() {
		while(!this.checkQuit())
		{
			String sysCurDateStr = BUtilsDateTime.GetCurDateStr();
			String sysCurTimeStr = BUtilsDateTime.GetCurTimeStr();
			
			if(null != m_dateTimeControlerEntity)
			{
				m_dateTimeControlerEntity.setCurDate(sysCurDateStr);
				m_dateTimeControlerEntity.setCurTime(sysCurTimeStr);
			}
			BThread.sleep(1000);
		}
	}

	private DateTimeControlerEntity m_dateTimeControlerEntity;
}
