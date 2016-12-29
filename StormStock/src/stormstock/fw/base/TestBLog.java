package stormstock.fw.base;

public class TestBLog {
	public static void main(String[] args) {
		
		//BLog.config_setLogDir("testlog");
		BLog.config_setTag("TAG1", true);
		BLog.config_setTag("TAG3", true);
		BLog.config_setTag("TAG4", false);
		
		BLog.output("TAG1", "testlog TAG1 string abcdedf1!\n");
		BLog.output("TAG2", "testlog TAG2 string abcdedf2! %d\n", 25);
		BLog.output("TAG2", "testlog TAG2 string abcdedf3!\n");
		BLog.output("TAG3", "testlog TAG3 string abcdedf4!\n");
		BLog.output("TAG3", "testlog TAG3 string abcdedf4!\n");
		BLog.output("TAG4", "testlog TAG3 string abcdedf4!\n");
	}
}
