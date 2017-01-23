package stormstock.fw.base;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stormstock.fw.base.BAutoSync.BSyncObj;
import stormstock.fw.base.BQThread.BQThreadRequest;

public class BLog {
	
	public static void start()
	{
		s_strLogDirName = BPath.getOutputDir();
		s_strLogName = "default.log";
		if(null == s_qThread)
		{
			s_qThread = new BQThread();
			s_qThread.startThread();
		}
	}
	
	public static void stop()
	{
		if(null != s_qThread)
		{
			s_qThread.stopThread();
			s_qThread = null;
		}
	}

	public static void config_setLogDir(String dirName)
	{
		s_strLogDirName = dirName;
	}
	public static void config_setTag(String tag, boolean enable)
	{
		s_tagMap.put(tag, enable);
	}
	public static void config_output()
	{
		outputConsole("BLog.config_output ----------------->>>>>> begin\n");
		for (Map.Entry<String, Boolean> entry : s_tagMap.entrySet()) {
			String tag = entry.getKey();
			Boolean enable = entry.getValue();
			outputConsole("tag[%s] enable[%b]\n", tag, enable);
		}
		outputConsole("BLog.config_output ----------------->>>>>> end\n");
	}
	
	public static void error(String target, String format, Object... args)
	{
		String logstr = String.format(format, args);
		output("ERROR", "(%s) %s", target, logstr);
	}
	
	public static void warning(String target, String format, Object... args)
	{
		String logstr = String.format(format, args);
		output("WARNING", "(%s) %s", target, logstr);
	}
	
	
	public static void output(String target, String format, Object... args)
	{
		if(null != target && "" != target && !s_tagMap.containsKey(target))
		{
			s_tagMap.put(target, false);
		}
		
		if(!s_tagMap.containsKey(target) || s_tagMap.get(target) == false)
		{
				return;
		}
		
		// dir file name check
		if(null == s_strLogDirName) s_strLogDirName = BPath.getOutputDir();
		if(null == s_strLogName) s_strLogName = "default.log";
		
		String logstr = String.format(format, args);
		
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String curDateTimeStr = sdf.format(new Date());
		
		String fullLogStr = String.format("[%s][%10s] %s", curDateTimeStr, target, logstr);
		
		if(null != s_qThread)
		{
			LogOutRequest cLogOutRequest = new LogOutRequest(fullLogStr);
			s_qThread.postRequest(cLogOutRequest);
		}
		else
		{
			BLog.implLogOutput(fullLogStr); // 无log工作线程直接输出
		}
	}
	
	private static void implLogOutput(String logbuf)
	{
		outputConsole(logbuf);
		
		File cDir = new File(s_strLogDirName);  
		if (!cDir.exists()  && !cDir.isDirectory())      
		{       
		    cDir.mkdir();    
		}
		File cfile =new File(s_strLogDirName + "\\" + s_strLogName);
		try
		{
			FileOutputStream cOutputStream = new FileOutputStream(cfile, true);
			cOutputStream.write(logbuf.getBytes());
			cOutputStream.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception:" + e.getMessage()); 
		}
	}
	
	private static void outputConsole(String format, Object... args)
	{
		String logstr = String.format(format, args);
		s_fmt.format("%s", logstr);
	}
	static private Formatter s_fmt = new Formatter(System.out);
	static private String s_strLogName = "default.log";
	static private String s_strLogDirName = null;
	static private Map<String, Boolean> s_tagMap = new HashMap<String, Boolean>() {
		{
			put("TEST", true);
			put("ERROR", true);
			put("WARNING", true);
		}
	};
	
	private static class LogOutRequest extends BQThreadRequest 
	{
		public LogOutRequest(String logbuf)
		{
			m_logbuf = logbuf;
		}
		@Override
		public void doAction() {
			// TODO Auto-generated method stub
			BLog.implLogOutput(m_logbuf); // 无log工作线程直接输出
		}
		private String m_logbuf;
	}
	static private BQThread s_qThread = null;
}
