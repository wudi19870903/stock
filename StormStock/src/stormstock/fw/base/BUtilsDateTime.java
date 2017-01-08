package stormstock.fw.base;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.protobuf.TextFormat.ParseException;

public class BUtilsDateTime {
	/*
	 *  当前日期
	 */
	static public String GetCurDateStr()
	{
		return GetDateStr(new Date());
	}
	
	/*
	 *  当前时间
	 */
	static public String GetCurTimeStr()
	{
		return GetTimeStr(new Date());
	}
	
	/*
	 *  当前日期时间
	 */
	static public String GetCurDateTimeStr()
	{
		return GetDateTimeStr(new Date());
	}
	
	/*
	 *  转换日期对象Date到字符串
	 *  返回  "yyyy-MM-dd"
	 */
	static public String GetDateStr(Date cDate)
	{
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(cDate);
	}
	
	/*
	 *  转换日期对象Date到字符串
	 *  返回  "HH:mm:ss"
	 */
	static public String GetTimeStr(Date cDate)
	{
		SimpleDateFormat sdf =new SimpleDateFormat("HH:mm:ss");
		return sdf.format(cDate);
	}
	
	/*
	 *  转换日期对象Date到字符串
	 *  返回  "yyyy-MM-dd HH:mm:ss"
	 */
	static public String GetDateTimeStr(Date cDate)
	{
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(cDate);
	}
	
	/*
	 * 转换日期字符串到对象 Date
	 * 输入日期字符串可以为 "yyyy-MM-dd"
	 * 输入日期字符串可以为 "yyyy-MM-dd HH:mm:ss"
	 */
	static public Date GetDate(String dateStr)
	{
		SimpleDateFormat sdf = null;
		if(dateStr.length() == "yyyy-MM-dd".length())
		{
			sdf =new SimpleDateFormat("yyyy-MM-dd");
		}
		else
		{
			sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		Date cDate = null;
		try
		{
			cDate = sdf.parse(dateStr);  
		}
		catch (Exception e)  
		{  
			BLog.output("TIME", e.getMessage());  
		}  
		return cDate;
	}
	
	/*
     * 获得指定日期偏移后的日期字符串
     * 例如传入 "2016-01-06", 4 则返回  "2016-01-10"
     */  
    public static String getDateStrForSpecifiedDateOffsetD(String specifiedDate, int offset) {
        Calendar c = Calendar.getInstance();  
        Date date = null;  
        try {  
            date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDate);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        c.setTime(date);  
        int day = c.get(Calendar.DATE);  
        c.set(Calendar.DATE, day + offset);  
  
        String dayNew = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());  
        return dayNew;  
    } 
    
	/*
     * 获得指定时间偏移若干分钟后的时间字符串
     * 例如传入 "12:33:05", 4 则返回  "12:37:05"
     */  
    public static String getTimeStrForSpecifiedTimeOffsetM(String specifiedTime, int offset_m) {
        Calendar c = Calendar.getInstance();  
        Date date = null;  
        try {  
            date = new SimpleDateFormat("HH:mm:ss").parse(specifiedTime);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        c.setTime(date);  
        int minute = c.get(Calendar.MINUTE);  
        c.set(Calendar.MINUTE, minute + offset_m);  
  
        String timeNew = new SimpleDateFormat("HH:mm:ss").format(c.getTime());  
        return timeNew;  
    } 
    
    /*
     * 等待日期时间
     * 等待到时间后返回true
     * 调用时已经超时返回false
     */
    public static boolean waitDateTime(String date, String time)
    {
    	String waitDateTimeStr = date + " " + time;
    	
    	{
        	Date curDate = new Date();
    		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		String curDateTimeStr = sdf.format(curDate);
    		if(curDateTimeStr.compareTo(waitDateTimeStr) > 0) 
    			return false;
    	}
    	
    	while(true)
    	{
    		Date curDate = new Date();
    		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		String curDateTimeStr = sdf.format(curDate);
    		
    		if(curDateTimeStr.compareTo(waitDateTimeStr) > 0) 
    		{
    			return true;
    		}
  
    		try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
    
    /*
     * 时间差（秒）
     */
    public static long subTime(String time1, String time2)
    {  
    	long diffsec = 0;
		try {
			Date date1 = new SimpleDateFormat("HH:mm:ss").parse(time1);
			Date date2 = new SimpleDateFormat("HH:mm:ss").parse(time2);  
	        long diff = date1.getTime() - date2.getTime();
	        diffsec = diff / 1000; 
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		} 
        return diffsec;
    }
    
    /*
     * 日期差（天）
     */
    public static long subDate(String date1, String date2)
    {  
    	long diffsec = 0;
		try {
			Date dateObj1 = new SimpleDateFormat("yyyy-MM-dd").parse(date1);
			Date dateObj2 = new SimpleDateFormat("yyyy-MM-dd").parse(date2);  
	        long diff = dateObj1.getTime() - dateObj2.getTime();
	        diffsec = diff / 1000 / 3600 / 24; 
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		} 
        return diffsec;
    }
}
