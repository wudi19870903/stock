package stormstock.fw.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import stormstock.app.common.RunAutoRealTimeTransection.SellStockItem;
import stormstock.fw.base.BAutoSync.BSyncObj;
import stormstock.fw.base.BQThread.BQThreadRequest;
import stormstock.ori.stockdata.CommonDef.DayKData;

public class BLog {
	
	public static void start()
	{
		s_strLogDirName = BPath.getOutputDir();
		s_strLogName = "default.log";
		
		s_strConfig = "config";
		s_strLogConfigName = "log_config.xml";
		
		reloadConfig();
		
		if(null == s_qThread)
		{
			s_qThread = new BQThread();
			s_qThread.startThread();
		}
		if(null == s_configMonitorThread)
		{
			s_configMonitorThread = new LogConfigMonitorThread();
			s_configMonitorThread.startMonitor();
		}
	}
	
	public static void stop()
	{
		if(null != s_configMonitorThread)
		{
			s_configMonitorThread.stopMonitor();
			s_configMonitorThread = null;
		}
		if(null != s_qThread)
		{
			s_qThread.stopThread();
			s_qThread = null;
		}
	}

	public static void config_setTag(String tag, boolean enable)
	{
		s_syncObjForTagMap.Lock();
		s_tagMap.put(tag, enable);
		s_syncObjForTagMap.UnLock();
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
	public static void reloadConfig()
	{
		//outputConsole("BLog.reloadConfig \n");
		String xmlStr = "";
		String configFileFullName = s_strConfig + "\\" + s_strLogConfigName;
		File cfile=new File(configFileFullName);
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(cfile));
	        int fileLen = (int)cfile.length();
	        char[] chars = new char[fileLen];
	        reader.read(chars);
	        xmlStr = String.valueOf(chars);
//			String tempString = "";
//			while ((tempString = reader.readLine()) != null) {
//				xmlStr = xmlStr + tempString + "\n";
//	        }
			reader.close();
			//fmt.format("XML:\n" + xmlStr);
			if(xmlStr.length()<=0)
				return;
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    StringReader sr = new StringReader(xmlStr);
		    InputSource is = new InputSource(sr);
		    Document doc = builder.parse(is);
		    Element rootElement = doc.getDocumentElement();
		    
		    // 检查返回数据有效性
		    if(!rootElement.getTagName().contains("config")) 
		    	return;
	
		    NodeList tag_contents = rootElement.getElementsByTagName("tag");
	        int lenList = tag_contents.getLength();
	        for (int i = 0; i < lenList; i++) {
	        	Node tag_content = tag_contents.item(i);
	        	String tag_name = ((Element)tag_content).getAttribute("name");
	        	String tag_output = ((Element)tag_content).getAttribute("output");
	        	//outputConsole("name:%s tag_output:%s \n", tag_name, tag_output);
	        	
	        	int output_flg = Integer.parseInt(tag_output);
	        	if(output_flg == 0)
	        	{
	        		config_setTag(tag_name, false);
	        	}
	        	if(output_flg == 1)
	        	{
	        		config_setTag(tag_name, true);
	        	}
	        }
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			return;
		}
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

		s_syncObjForTagMap.Lock();
		if(null != target && "" != target && !s_tagMap.containsKey(target))
		{
			s_tagMap.put(target, false);
		}
		
		if(!s_tagMap.containsKey(target) || s_tagMap.get(target) == false)
		{
			s_syncObjForTagMap.UnLock();
			return;
		}
		s_syncObjForTagMap.UnLock();

		
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
	static private String s_strLogDirName = null;
	static private String s_strLogName = "default.log";
	static private String s_strConfig = "config";
	static private String s_strLogConfigName = "log_config.xml";
	
	static private Map<String, Boolean> s_tagMap = new HashMap<String, Boolean>() {
		{
			put("TEST", true);
			put("ERROR", true);
			put("WARNING", true);
		}
	};
	static private BSyncObj s_syncObjForTagMap = new BSyncObj();
	
	/*
	 * --------------------------------------------------------------------------------------
	 * log request, thread
	 */
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
	
	/*
	 * --------------------------------------------------------------------------------------
	 * log config monitor, thread
	 */
	private static class LogConfigMonitorThread extends BThread
	{
		public void startMonitor()
		{
			super.startThread();
		}
		public void stopMonitor()
		{
			try {
				if(null != m_WatchService)
				{
					m_WatchService.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			super.stopThread();
		}
		@Override
		public void run() {
			try {
				m_WatchService = FileSystems.getDefault().newWatchService();
				Paths.get(BLog.s_strConfig).register(m_WatchService,   
		                StandardWatchEventKinds.ENTRY_CREATE,  
		                StandardWatchEventKinds.ENTRY_DELETE,  
		                StandardWatchEventKinds.ENTRY_MODIFY);  
				while(!super.checkQuit())
				{
		            WatchKey key=m_WatchService.take();  
		            for(WatchEvent<?> event:key.pollEvents())  
		            {  
		                //System.out.println(event.context()+"发生了"+event.kind()+"事件");  
		                if(event.context().toString().equals(BLog.s_strLogConfigName))
		                {
		                	BLog.reloadConfig();
		                }
		            }  
		            if(!key.reset())  
		            {  
		                break;  
		            }  
					super.Wait(100);
				}
			} catch (Exception e) {
				if(!e.getClass().getSimpleName().equals("ClosedWatchServiceException"))
				{
					e.printStackTrace();
				}
			} 
		}
		private WatchService m_WatchService;
	}
	
	static private LogConfigMonitorThread s_configMonitorThread = null;
}
