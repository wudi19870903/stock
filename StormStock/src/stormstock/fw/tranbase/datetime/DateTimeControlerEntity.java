package stormstock.fw.tranbase.datetime;

import java.text.SimpleDateFormat;
import java.util.Date;

import stormstock.fw.base.BLog;
import stormstock.fw.base.BThread;
import stormstock.fw.base.BUtilsDateTime;
import stormstock.fw.tranbase.datetime.TranDateTimeContorlerPublicDef.DATETIMEMODE;

public class DateTimeControlerEntity {
	
	public DateTimeControlerEntity(DATETIMEMODE eMode)
	{
		m_dateTimeMode = eMode;
		if(DATETIMEMODE.REAL == m_dateTimeMode) 
		{
			m_realDateTimeControlThread = new RealDateTimeControlThread(this);
			m_realDateTimeControlThread.startThread();
		}
		else
		{
			m_realDateTimeControlThread = null;
		}
	}
	
	public String getCurDate()
	{
		return m_curDate;
	}
	
	public String getCurTime()
	{
		return m_curTime;
	}
	
	public void setCurDate(String date)
	{
		m_curDate = date;
	}
	
	public void setCurTime(String time)
	{
		m_curTime = time;
	}
	
    /*
     * 等待日期时间
     * 等待到时间后返回true 
     * 调用时已经超时返回false
     */
	public boolean waitDateTime(String date, String time)
	{
		if(DATETIMEMODE.REAL == m_dateTimeMode) 
		{
			BLog.output("DATETIME", "real datetime waitting DateTime (%s %s)... \n", date, time);
			String waitDateTimeStr = date + " " + time;
			String curDateTimeStr = getCurDate() + " " + getCurTime();
	    	{
	    		if(curDateTimeStr.compareTo(waitDateTimeStr) > 0) 
	    		{
	    			BLog.output("DATETIME", "real datetime waitting DateTime (%s %s) return false\n", date, time);
	    			return false;
	    		}
	    	}
			while(true)
	    	{
				curDateTimeStr = getCurDate() + " " + getCurTime();
	    		
	    		if(curDateTimeStr.compareTo(waitDateTimeStr) > 0) 
	    		{
	    			BLog.output("DATETIME", "real datetime waitting DateTime (%s %s) return true\n", date, time);
	    			return true;
	    		}
	  
	    		try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    	}
		}
		else
		{
			setCurDate(date);
			setCurTime(time);
			BLog.output("DATETIME", "mock datetime waitting DateTime (%s %s) return true!\n", date, time);
			return true;
		}
	}
	
	private DATETIMEMODE m_dateTimeMode;
	private RealDateTimeControlThread m_realDateTimeControlThread;
	
	private String m_curDate;
	private String m_curTime;
}
