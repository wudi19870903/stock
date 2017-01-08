package stormstock.fw.objmgr;

public class GlobalTranTime {
	
	public static String getTranDate()
	{
		return s_date;
	}
	
	public static String getTranTime()
	{
		return s_time;
	}
	
	public static void setTranDateTime(String date, String time)
	{
		s_date = date;
		s_time = time;
	}
	
	private static String s_date; 
	private static String s_time; 
}
