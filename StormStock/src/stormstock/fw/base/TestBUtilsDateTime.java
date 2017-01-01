package stormstock.fw.base;

import java.util.Date;

public class TestBUtilsDateTime {
	
	public static void main(String[] args) {
		BLog.output("TEST", "TestBUtilsDateTime begin\n");
		
		String testdate = BUtilsDateTime.getDateStrForSpecifiedDateOffsetD("2016-01-31", 2);
		BLog.output("TEST", "testdate = %s\n", testdate);
		
		String testtime = BUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM("12:22:34", 2);
		BLog.output("TEST", "testtime = %s\n", testtime);
		
		Date curDate = new Date();
		String curTimeStr = BUtilsDateTime.GetTimeStr(curDate);
		String curDateStr = BUtilsDateTime.GetDateStr(curDate);
		String beforeTimeStr = BUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(curTimeStr, -2);
		String afterTimeStr = BUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(curTimeStr, 2);
		BLog.output("TEST", "curDateStr %s beforeTimeStr = %s\n", curDateStr, beforeTimeStr);
		BLog.output("TEST", "curDateStr %s afterTimeStr = %s\n", curDateStr, afterTimeStr);
		boolean bwaitbefore = BUtilsDateTime.waitDateTime(curDateStr, beforeTimeStr);
		BLog.output("TEST", "waitDateTime beforeTimeStr = %b\n", bwaitbefore);
		boolean bwaitafter = BUtilsDateTime.waitDateTime(curDateStr, afterTimeStr);
		BLog.output("TEST", "waitDateTime bwaitafter = %b\n", bwaitafter);

		BLog.output("TEST", "TestBUtilsDateTime end\n");
	}
}
