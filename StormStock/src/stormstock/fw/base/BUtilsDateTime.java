package stormstock.fw.base;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.protobuf.TextFormat.ParseException;

public class BUtilsDateTime {
	/*
	 *  ת�����ڶ���Date���ַ���
	 *  ����  "yyyy-MM-dd"
	 */
	static public String GetDateStr(Date cDate)
	{
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(cDate);
	}
	
	/*
	 *  ת�����ڶ���Date���ַ���
	 *  ����  "HH:mm:ss"
	 */
	static public String GetTimeStr(Date cDate)
	{
		SimpleDateFormat sdf =new SimpleDateFormat("HH:mm:ss");
		return sdf.format(cDate);
	}
	
	/*
	 *  ת�����ڶ���Date���ַ���
	 *  ����  "yyyy-MM-dd HH:mm:ss"
	 */
	static public String GetDateTimeStr(Date cDate)
	{
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(cDate);
	}
	
	/*
	 * ת�������ַ��������� Date
	 * ���������ַ�������Ϊ "yyyy-MM-dd"
	 * ���������ַ�������Ϊ "yyyy-MM-dd HH:mm:ss"
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
     * ���ָ������ƫ�ƺ�������ַ���
     * ���紫�� "2016-01-06", 4 �򷵻�  "2016-01-10"
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
     * ���ָ��ʱ��ƫ�����ɷ��Ӻ��ʱ���ַ���
     * ���紫�� "12:33:05", 4 �򷵻�  "12:37:05"
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
     * �ȴ�����ʱ��
     * �ȴ���ʱ��󷵻�true
     * ����ʱ�Ѿ���ʱ����false
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
}