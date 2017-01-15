package stormstock.fw.base;

import java.util.Date;

public class TestBUtilsDateTime {
	
	public static void test_all()
	{
		String time1 = "12:22:23";
		String time2 = "13:20:21";
		long diffSec = BUtilsDateTime.subTime(time1,time2);
		BLog.output("TEST", "time1(%s) - time2(%s) = %d s\n", time1,time2,diffSec);
		
		String date1 = "2016-01-01";
		String date2 = "0000-01-01";
		long diffDay = BUtilsDateTime.subDate(date1, date2);
		BLog.output("TEST", "date1(%s) - date2(%s) = %d day\n", date1,date2,diffDay);
		
		
		String testdate = BUtilsDateTime.getDateStrForSpecifiedDateOffsetD("2016-01-31", 2);
		BLog.output("TEST", "testdate = %s\n", testdate);
		
		String testtime = BUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM("12:22:34", 2);
		BLog.output("TEST", "testtime = %s\n", testtime);
		
		String curDateStr = BUtilsDateTime.GetCurDateStr();
		String curTimeStr = BUtilsDateTime.GetCurTimeStr();
		String curDateTimeStr = BUtilsDateTime.GetCurDateTimeStr();
		BLog.output("TEST", "curDate %s curTime %s \n", curDateStr, curTimeStr);
		BLog.output("TEST", "curDateTimeStr %s \n", curDateTimeStr);
		
		String beforeTimeStr = BUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(curTimeStr, -2);
		String afterTimeStr = BUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(curTimeStr, 2);
		BLog.output("TEST", "curDateStr %s beforeTimeStr = %s\n", curDateStr, beforeTimeStr);
		BLog.output("TEST", "curDateStr %s afterTimeStr = %s\n", curDateStr, afterTimeStr);
		boolean bwaitbefore = BUtilsDateTime.waitDateTime(curDateStr, beforeTimeStr);
		BLog.output("TEST", "waitDateTime beforeTimeStr = %b\n", bwaitbefore);
		boolean bwaitafter = BUtilsDateTime.waitDateTime(curDateStr, afterTimeStr);
		BLog.output("TEST", "waitDateTime bwaitafter = %b\n", bwaitafter);
	}
	
	public static void test_getDateStrForSpecifiedDateOffsetD()
	{
		String testdate = BUtilsDateTime.getDateStrForSpecifiedDateOffsetD("2016-01-31", 2);
		BLog.output("TEST", "testdate = %s\n", testdate);
	}
	public static void main(String[] args) {
		BLog.output("TEST", "TestBUtilsDateTime begin\n");
		
		test_getDateStrForSpecifiedDateOffsetD();
	
		BLog.output("TEST", "TestBUtilsDateTime end\n");
	}
}
