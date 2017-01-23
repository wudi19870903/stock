package stormstock.fw.base;

import java.io.IOException;
import java.nio.file.FileSystems;  
import java.nio.file.Paths;  
import java.nio.file.StandardWatchEventKinds;  
import java.nio.file.WatchEvent;  
import java.nio.file.WatchKey;  
import java.nio.file.WatchService; 

public class TestBLog {
	public static void main(String[] args) {
		//BLog.config_setLogDir("testlog");
		BLog.config_setTag("TAG1", true);
		BLog.config_setTag("TAG2", true);
		BLog.config_setTag("TAG3", false);
		BLog.start();
		
		BLog.output("TAG1", "testlog TAG1 string abcdedf1!\n");
		BLog.output("TAG2", "testlog TAG2 string abcdedf2! %d\n", 25);
		BLog.output("TAG3", "testlog TAG2 string abcdedf3!\n");
		BLog.output("TAG4", "testlog TAG3 string abcdedf4!\n");
		BLog.output("TAG5", "testlog TAG3 string abcdedf4!\n");
		BLog.output("TAG6", "testlog TAG3 string abcdedf4!\n");
		
		
		for(int i=0; i< 1000; i++)
		{
			BLog.output("TAG1", "testlog TAG1 string abcdedf1!\n");
			BLog.output("TAG2", "testlog TAG2 string abcdedf2! %d\n", 25);
			BLog.output("TAG3", "testlog TAG2 string abcdedf3!\n");
			BLog.output("TAG4", "testlog TAG3 string abcdedf4!\n");
			BLog.output("TAG5", "testlog TAG3 string abcdedf4!\n");
			BLog.output("TAG6", "testlog TAG3 string abcdedf4!\n");
			BThread.sleep(300);
		}
		
		BLog.stop();
	}
}
