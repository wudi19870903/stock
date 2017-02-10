package stormstock.ori.capi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Properties;

/*
 * http://blog.csdn.net/qinjuning/article/details/7607214
 * http://blog.csdn.net/buleriver/article/details/26577895
 * 
 * 签名查看：
 * http://blog.csdn.net/qq_17387361/article/details/52701481
 * 在bin下，执行 javap -s stormstock.ori.capi.CATHSAccount
 * 
 * jni的C++文件生成
 * 在代码src下,执行 javah stormstock.ori.capi.CATHSAccount
 */
 
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
	
	/*
	 * **************************************************************************
	 * public class define
	 */
	
	/*
	 * 可用资金结果
	 */
	public static class ResultAvailableMoney
	{
		public ResultAvailableMoney()
		{
			error = -1;
			availableMoney = 0.0f;
		}
		public int error;
		public float availableMoney;
	}
	
	/*
	 * 总资产结果
	 */
	public static class ResultTotalAssets
	{
		public ResultTotalAssets()
		{
			error = -1;
			totalAssets = 0.0f;
		}
		public int error;
		public float totalAssets;
	}
	
	/*
	 * 持股总市值结果
	 */
	public static class ResultAllStockMarketValue
	{
		public ResultAllStockMarketValue()
		{
			error = -1;
			allStockMarketValue = 0.0f;
		}
		public int error;
		public float allStockMarketValue;
	}
	
	/*
	 * 交易动作枚举
	 */
	public enum TRANACT 
	{
		BUY,
		SELL,
	}
	
	/*
	 * 当日持股定义
	 */
	public static class HoldStock 
	{
		public String stockID; // 股票ID
		public int totalAmount; // 持有总量（股）
		public int availableAmount; // 可卖数量
		public float refProfitLoss; // 参考盈亏
		public float refPrimeCostPrice; // 参考成本价
		public float curPrice; // 当前价
	}
	
	/*
	 * 当日股票委托单定义
	 */
	public static class CommissionOrder 
	{
		public String time;
		public String stockID;
		public TRANACT tranAct;
		public int commissionAmount; // 委托数量
		public float commissionPrice; // 委托价格
		public int dealAmount; // 成交数量
		public float dealPrice; // 成交价格
	}
	
	/*
	 * 当日成交单定义
	 */
	public static class DealOrder 
	{
		public String time;
		public String stockID;        // 股票ID
		public TRANACT tranAct;       // 交易动作
		public int dealAmount; // 成交数量
		public float dealPrice; // 成交价格
	}
	
	/*
	 * 持股列表结果
	 */
	public static class ResultHoldStockList
	{
		public ResultHoldStockList()
		{
			error = -1;
			resultList = null;
		}
		public int error;
		public List<HoldStock> resultList;
	}
	
	/*
	 * 当日委托列表结果
	 */
	public static class ResultCommissionOrderList
	{
		public ResultCommissionOrderList()
		{
			error = -1;
			resultList = null;
		}
		public int error;
		public List<CommissionOrder> resultList;
	}
	
	/*
	 * 当日成交列表结果
	 */
	public static class ResultDealOrderList
	{
		public ResultDealOrderList()
		{
			error = -1;
			resultList = null;
		}
		public int error;
		public List<DealOrder> resultList;
	}
	
	/*
	 * **************************************************************************
	 * public IF define
	 */
	
	/*
	 * TongHuaShun Initialize
	 * 同花顺初始化
	 * return ErrorCode
	 * 0 成功
	 */
	public static native int initialize();
	
	/*
	 * getAvailableMoney
	 * 获取可用资金
	 */
	public static native ResultAvailableMoney getAvailableMoney();
	
	/*
	 * getTotalAssets
	 * 获取总资产
	 */
	public static native ResultTotalAssets getTotalAssets();
	
	/*
	 * getAllStockMarketValue
	 * 获取股票总市值
	 */
	public static native ResultAllStockMarketValue getAllStockMarketValue();
	
	/*
	 * getHoldStockList
	 * 获取持股列表
	 */
	public static native ResultHoldStockList getHoldStockList();

	/*
	 * getCommissionOrderList
	 * 获取当日委托列表
	 */
	public static native ResultCommissionOrderList getCommissionOrderList();

	/*
	 * getDealOrderList
	 * 获取当日成交列表
	 */
	public static native ResultDealOrderList getDealOrderList();
	
	/*
	 * buyStock
	 * 委托买入下单
	 * return ErrorCode
	 * 0 成功
	 */
	public static native int buyStock(String stockId, int amount, float price);
	
	/*
	 * buyStock
	 * 委托卖出下单
	 * return ErrorCode
	 * 0 成功
	 */
	public static native int sellStock(String stockId, int amount, float price);
}
