package stormstock.fw.tranbase.datetime;

import stormstock.fw.base.BUtilsDateTime;
import stormstock.fw.tranbase.datetime.TranDateTimeContorlerPublicDef.DATETIMEMODE;

/*
 * 全局交易日期时间控制器
 * 所有交易相关模块调用此接口来获取当前时间
 * 此时间由流程控制器控制 
 */
public class TranDateTimeContorlerIF {
	
	public static boolean initialize(DATETIMEMODE eMode)
	{
		s_dateTimeControlerEntity = new DateTimeControlerEntity(eMode);
		return true;
	}
	
	/*
	 * realtime模式
	 * 	等待日期时间成功，返回true
	 * 	等待失败，返回false，比如等待的时间已经过期
	 * historymock模式
	 * 	直接返回true
	 */
	public static boolean waitDateTime(String date, String time)
	{
		return s_dateTimeControlerEntity.waitDateTime(date, time);
	}
	
	public static String getCurDate()
	{
		return s_dateTimeControlerEntity.getCurDate();
	}
	
	public static String getCurTime()
	{
		return s_dateTimeControlerEntity.getCurTime();
	}
	
	private static DateTimeControlerEntity s_dateTimeControlerEntity;
}
