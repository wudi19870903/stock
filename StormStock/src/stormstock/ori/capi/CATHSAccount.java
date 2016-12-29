package stormstock.ori.capi;

import java.lang.reflect.Field;
import java.util.Formatter;
import java.util.Properties;
 
public class CATHSAccount {
	
	static{
		// add libpath
		String yourPath = "lib";
		System.setProperty("java.library.path", yourPath);
		Field sysPath = null;
		try {
			sysPath = ClassLoader.class.getDeclaredField("sys_paths");
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sysPath.setAccessible( true );
		try {
			sysPath.set( null, null );
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// load c++ API dll
		String libraryName = "AutoStockTransaction_x86";
		Properties properties = System.getProperties();
		String jvmName = properties.getProperty("java.vm.name");
		if(jvmName.contains("64"))
		{
			libraryName = "AutoStockTransaction_x64";
		}
		try {
			sysPath = ClassLoader.class.getDeclaredField("sys_paths");
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("System.loadLibrary: " + libraryName);
		System.loadLibrary(libraryName);
    }
	
	// TongHuaShun Initialize
	// 0 : success
	public static native int initialize();
	
	public static native float getAvailableMoney();
	
	public static native float getAllMoney();
	
	public static native float getAllStockMarketValue();
	
	// 0 : success to commit buy order
	public static native int buyStock(String stockId, int amount, float price);
	
	// 0 : success to commit sell order
	public static native int sellStock(String stockId, int amount, float price);
}
