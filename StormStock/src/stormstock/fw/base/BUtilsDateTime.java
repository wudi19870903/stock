package stormstock.fw.base;

import java.text.SimpleDateFormat;
import java.util.Date;

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
}
