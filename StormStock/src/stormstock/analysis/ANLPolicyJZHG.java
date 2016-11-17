package stormstock.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import stormstock.analysis.ANLPolicy.ANLUserAcc.ANLUserAccStock;
import stormstock.analysis.ANLPolicy.ANLUserStockPool;
import stormstock.analysis.ANLPolicyAve.BounceData;
import stormstock.analysis.ANLPolicyCD.XiaCuoRange;

// 价值回归
public class ANLPolicyJZHG extends ANLPolicy {
	public static ANLPolicyJZHG s_cANLPolicyJZHG = new ANLPolicyJZHG();
	public static void OutLog(String str){s_cANLPolicyJZHG.outputLog(str);}
	
	// 股票分值表，用于给每天的股票打分
	static class ANLPolicyStockCK
	{
		public String stockID;
		
		public float param_PianLiBi; // 偏离比分值
		public float mingci_PianLiBi; // 偏离比名次
		
		public float param_XiaCuo; // 短期下挫分值
		public float mingci_XiaCuo; // 下挫名次
		
		// 价值回归分值   系数(当前价-半年最高)+(当前价-半年最低)+(当前价-15日均)
		// 前3个月吸货分值，    贴重要压力位上穿吃货检查，20 60日均线 ， - 贴重要支撑位下穿反弹
		// 解套分值， 给近5日解套次数，突破次数
		// 洗盘分值， 缩量中阴线次数，直接一次性击穿重要支撑位
		
		static class ZongHeCompare implements Comparator 
		{
			public int compare(Object object1, Object object2) {
				ANLPolicyStockCK ck1 = (ANLPolicyStockCK)object1;
				ANLPolicyStockCK ck2 = (ANLPolicyStockCK)object2;
				float zonghe1 = ck1.mingci_PianLiBi * 5/10.0f + ck1.mingci_XiaCuo * 5/10.0f;
				float zonghe2 = ck2.mingci_PianLiBi * 5/10.0f + ck2.mingci_XiaCuo * 5/10.0f;
				if(zonghe1 <= zonghe2)
				{
					return -1;
				}
				else
				{
					return 1;
				}
			}
		}
		static class PianLiBiCompare implements Comparator 
		{
			public int compare(Object object1, Object object2) {
				ANLPolicyStockCK ck1 = (ANLPolicyStockCK)object1;
				ANLPolicyStockCK ck2 = (ANLPolicyStockCK)object2;
				if(ck1.param_PianLiBi <= ck2.param_PianLiBi)
				{
					return -1;
				}
				else
				{
					return 1;
				}
			}
		}
		static class XiaCuoCompare implements Comparator 
		{
			public int compare(Object object1, Object object2) {
				ANLPolicyStockCK ck1 = (ANLPolicyStockCK)object1;
				ANLPolicyStockCK ck2 = (ANLPolicyStockCK)object2;
				if(ck1.param_XiaCuo <= ck2.param_XiaCuo)
				{
					return -1;
				}
				else
				{
					return 1;
				}
			}
		}
		static public void sortZongHe(List<ANLPolicyStockCK> cList)
		{
			Collections.sort(cList, new ANLPolicyStockCK.PianLiBiCompare()); //偏离比排序
			for(int i=0; i<cList.size(); i++)
			{
				int mingci = i+1;
				ANLPolicyStockCK cANLPolicyStockCK = cList.get(i);
				cANLPolicyStockCK.mingci_PianLiBi = mingci;
			}
			Collections.sort(cList, new ANLPolicyStockCK.XiaCuoCompare()); //偏离比排序
			for(int i=0; i<cList.size(); i++)
			{
				int mingci = i+1;
				ANLPolicyStockCK cANLPolicyStockCK = cList.get(i);
				cANLPolicyStockCK.mingci_XiaCuo = mingci;
			}
			Collections.sort(cList, new ANLPolicyStockCK.ZongHeCompare()); // 最后进行综合排序
		}
	}
	
	// 偏离比分数计算类，计算短期中期长期都处于相对底部的分值
	static class CalPianLiBi
	{
		static float calculate(ANLStock cANLStock)
		{
			if(cANLStock.historyData.size() == 0) 
				return -1.0f;
			String date = cANLStock.historyData.get(cANLStock.historyData.size()-1).date;
			// 长期价格均值作为基准值
			float long_base_price = cANLStock.GetMA(500, date);
			// 当前价格参数
			float cur_price = cANLStock.historyData.get(cANLStock.historyData.size()-1).close;
			float cur_pricePa = (cur_price - long_base_price)/long_base_price;
			
			// 计算短期偏离比
			float short_low_price = cANLStock.GetLow(20, date);
			float short_low_pricePa = (short_low_price - long_base_price)/long_base_price;
			float short_high_price = cANLStock.GetHigh(20, date);
			float short_high_pricePa = (short_high_price - long_base_price)/long_base_price;
			float short_pianlirate = 0.0f;
			if(short_high_pricePa-short_low_pricePa != 0)
				short_pianlirate = (cur_pricePa-short_low_pricePa)/(short_high_pricePa-short_low_pricePa);

			// 计算中期偏离比
			float mid_low_price = cANLStock.GetLow(60, date);
			float mid_low_pricePa = (mid_low_price - long_base_price)/long_base_price;
			float mid_high_price = cANLStock.GetHigh(60, date);
			float mid_high_pricePa = (mid_high_price - long_base_price)/long_base_price;
			float mid_pianlirate = 0.0f;
			if(mid_high_pricePa-mid_low_pricePa != 0)
				mid_pianlirate = (cur_pricePa-mid_low_pricePa)/(mid_high_pricePa-mid_low_pricePa);

			// 计算长期偏离比
			float long_low_price = cANLStock.GetLow(250, date);
			float long_low_pricePa = (long_low_price - long_base_price)/long_base_price;
			float long_high_price = cANLStock.GetHigh(250, date);
			float long_high_pricePa = (long_high_price - long_base_price)/long_base_price;
			float long_pianlirate = 0.0f;
			if(long_high_pricePa-long_low_pricePa != 0)
				long_pianlirate = (cur_pricePa-long_low_pricePa)/(long_high_pricePa-long_low_pricePa);
			
			// 短期均值参数
			float fenshu = short_pianlirate*(3/10.0f) 
					+ mid_pianlirate*(4/10.0f) 
					+ long_pianlirate*(3/10.0f);
			
			String logstr = String.format(" --- Test ---  PianLiBi %s [%.2f %.2f %.2f %.2f] %.2f\n",
					cANLStock.id, 
					fenshu, short_pianlirate,mid_pianlirate,long_pianlirate, 
					cur_price);
			// OutLog(logstr);
			
			return fenshu;
		}
	}
	
	static class DuanQiXiaCuo
	{
		// 确认i日是否是下挫企稳点
		static public class XiaCuoRange
		{
			public int iBeginIndex;
			public int iHighIndex;
			public int iLowIndex;
			public int iEndEndex;
			public float highPrice;
			public float lowPrice;
			public float maxZhenFu()
			{
				return (lowPrice-highPrice)/highPrice;
			}
			public float xiaCuoXieLv()
			{
				return maxZhenFu()/(iLowIndex-iHighIndex);
			}
		}
		// 计算i到j日的最高价格的索引
		static public int indexHigh(List<ANLStockDayKData> dayklist, int i, int j)
		{
			int index = i;
			float high = 0.0f;
			for(int k = i; k<=j; k++ )
			{
				ANLStockDayKData cDayKDataTmp = dayklist.get(k);
				if(cDayKDataTmp.high > high) 
				{
					high = cDayKDataTmp.high;
					index = k;
				}
			}
			return index;
		}
		// 计算i到j日的最低价格的索引
		static public int indexLow(List<ANLStockDayKData> dayklist, int i, int j)
		{
			int index = i;
			float low = 100000.0f;
			for(int k = i; k<=j; k++ )
			{
				ANLStockDayKData cDayKDataTmp = dayklist.get(k);
				if(cDayKDataTmp.low < low) 
				{
					low = cDayKDataTmp.low;
					index = k;
				}
			}
			return index;
		}
		static public XiaCuoRange CheckXiaCuoRange(List<ANLStockDayKData> dayklist, int i)
		{
			String logstr;
			int iCheckE = i;
			// 检查当前天，前6到20天区间满足急跌企稳趋势
			for(int iCheckB = i-6; iCheckB>=i-20; iCheckB--)
			{
				if(iCheckB >= 0)
				{
					// @ 最高点与最低点在区间位置的判断
					boolean bCheckHighLowIndex = false;
					int indexHigh = indexHigh(dayklist, iCheckB, iCheckE);
					int indexLow = indexLow(dayklist, iCheckB, iCheckE);
					if(indexHigh>iCheckB && indexHigh<=(iCheckB+iCheckE)/2
							&& indexLow > (iCheckB+iCheckE)/2 && indexLow < iCheckE)
					{
						bCheckHighLowIndex = true;
					}
					
					// @ 最高点与最低点下挫幅度判断
					boolean bCheckXiaCuo = false;
					float highPrice = dayklist.get(indexHigh).high;
					float lowPrice = dayklist.get(indexLow).low;
					float xiaCuoZhenFu = (lowPrice-highPrice)/highPrice;
					float xiaCuoMinCheck = -1.5f*0.01f*(indexLow-indexHigh);
					if(xiaCuoMinCheck > -0.06f) xiaCuoMinCheck = -0.06f;
					if(xiaCuoZhenFu < xiaCuoMinCheck)
					{
						bCheckXiaCuo = true;
					}
					
					// @ 前区间，后区间价位判断
					boolean bCheck3 = true;
					int cntDay = iCheckE-iCheckB;
					float midPrice = (highPrice+lowPrice)/2;
					for(int c= iCheckB; c<iCheckB + cntDay/3; c++)
					{
						if(dayklist.get(c).low < midPrice)
						{
							bCheck3 = false;
						}
					}
					for(int c= iCheckE; c>iCheckE - cntDay/3; c--)
					{
						if(dayklist.get(c).low > midPrice)
						{
							bCheck3 = false;
						}
					}
					
					if(bCheckHighLowIndex && 
							bCheckXiaCuo && 
							bCheck3)
					{
						logstr = String.format("    Test findLatestXiaCuoRange [%s,%s] ZhenFu(%.3f,%.3f)\n",
								dayklist.get(iCheckB).date,
								dayklist.get(iCheckE).date,
								xiaCuoZhenFu,xiaCuoMinCheck);
						// outputLog(logstr);
						
						XiaCuoRange retXiaCuoRange = new XiaCuoRange();
						retXiaCuoRange.iBeginIndex = iCheckB;
						retXiaCuoRange.iEndEndex = iCheckE;
						retXiaCuoRange.iHighIndex = indexHigh;
						retXiaCuoRange.iLowIndex = indexLow;
						retXiaCuoRange.highPrice = highPrice;
						retXiaCuoRange.lowPrice = lowPrice;
						return retXiaCuoRange;
					}
				}
			}
			return null;
		}
		static float calculate(ANLStock cANLStock)
		{
			float param_XiaCuo = 100.0f;
			String date = cANLStock.historyData.get(cANLStock.historyData.size()-1).date;
			float cur_price = cANLStock.historyData.get(cANLStock.historyData.size()-1).close;
			XiaCuoRange cXiaCuoRange = CheckXiaCuoRange(cANLStock.historyData, cANLStock.historyData.size()-1);
			String logstr = "";
			if(null != cXiaCuoRange)
			{
				param_XiaCuo = cXiaCuoRange.maxZhenFu();
				
				logstr = String.format(" --- Test ---  XiaCuo %s [%.3f %.3f %.3f %.3f %.3f] %.2f\n",
						cANLStock.id, 
						param_XiaCuo, cXiaCuoRange.highPrice, cXiaCuoRange.lowPrice, cXiaCuoRange.maxZhenFu(), cXiaCuoRange.xiaCuoXieLv(),
						cur_price);
			}
			else
			{
				logstr = String.format(" --- Test ---  XiaCuo %s [%.3f  - - - -] %.2f\n",
						cANLStock.id, 
						param_XiaCuo,
						cur_price);
			}
			// OutLog(logstr);
			return param_XiaCuo;
		}
	}
	
	
	//****************************************************************
	// init 初始化参数, 测试系统回调
	public void init()
	{
		String logstr = String.format("init\n");
		// 账户初始化
		cUserAcc.init(100000.0f);
		outputLog(logstr);
	}

	//****************************************************************
	// 所有股票回调筛选函数，测试系统回调，反悔true的被加入考察股票池
	public boolean stock_filter(ANLStock cANLStock)
	{
		// for test
		boolean bEnableTest = false;
		if(bEnableTest)
		{
			List<String> testStockList = Arrays.asList(
					"000001"
					);
			for(int i=0; i< testStockList.size();i++)
			{
				if(cANLStock.id.compareTo(testStockList.get(i)) == 0 )
				{
					return true;
				}
			}
			return false;
		}
		
		// 市值过大过滤掉
		if(cANLStock.curBaseInfo.allMarketValue > 200.0f 
				|| cANLStock.curBaseInfo.circulatedMarketValue > 100.0f)
		{
			return false;
		}
		// 交易历史上市时间较短的过滤掉
		if(cANLStock.historyData.size() < 250*2)
		{
			return false;
		}
		// PE市盈率过大的过滤掉
		if(cANLStock.curBaseInfo.peRatio > 100.0f 
				|| cANLStock.curBaseInfo.peRatio == 0)
		{
			return false;
		}
		// 名字过滤
		if(cANLStock.curBaseInfo.name.contains("S")
				|| cANLStock.curBaseInfo.name.contains("*")
				|| cANLStock.curBaseInfo.name.contains("N"))
		{
			return false;
		}
		
		String logstr = String.format("add userpool %s %s\n", cANLStock.id, cANLStock.curBaseInfo.name);
		outputLog(logstr);
		return true;
	}

	//****************************************************************
	// 每天用户股票池回调，测试系统自动回调，确定买入卖出
	public void check_today(String date, ANLUserStockPool spool)
	{
		String logstr = String.format("check_today %s --------------------------------- >>>\n", date);
		outputLog(logstr);
		
		// ---------创建股票分值表 ---------
		List<ANLPolicyStockCK> stockCKList = new ArrayList<ANLPolicyStockCK>();
		for(int i = 0; i < spool.stockList.size(); i++)
		{
			ANLStock cANLStock = spool.stockList.get(i);
			if(cANLStock.historyData.size() == 0) continue;
			
			ANLPolicyStockCK cANLPolicyStockCK = new ANLPolicyStockCK();
			cANLPolicyStockCK.stockID = cANLStock.id;
			cANLPolicyStockCK.param_PianLiBi = CalPianLiBi.calculate(cANLStock);
			cANLPolicyStockCK.param_XiaCuo = DuanQiXiaCuo.calculate(cANLStock);
			
			stockCKList.add(cANLPolicyStockCK);
		}
		ANLPolicyStockCK.sortZongHe(stockCKList);
		
		// ---------打印可操作股票信息 ---------
		if(stockCKList.size() != 0) 
		{
			for(int i = 0; i < stockCKList.size(); i++)
			{
				ANLPolicyStockCK cANLPolicyStockCK = stockCKList.get(i);
				logstr = String.format("    %s PianLiBi[ %.3f %.1f] XiaCuo[ %.3f %.1f]\n", 
						cANLPolicyStockCK.stockID, 
						cANLPolicyStockCK.param_PianLiBi, cANLPolicyStockCK.mingci_PianLiBi,
						cANLPolicyStockCK.param_XiaCuo, cANLPolicyStockCK.mingci_XiaCuo);
				outputLog(logstr);
				if(i>20) break; // 只打印排序靠前的
			}
		}
			
		// ---------用户操作交易 ---------
		int iMaxHoldCnt = 2; // 最大持股个数
		for(int i = 0; i < cUserAcc.stockList.size(); i++) // 遍历持仓票，进行卖出判断
		{
			ANLUserAccStock cANLUserAccStock = cUserAcc.stockList.get(i);
			float cprice = spool.getStock(cANLUserAccStock.id).GetLastPrice();
			if(cANLUserAccStock.holdDayCnt > 5) // 持有一定时间卖出
			{
				cUserAcc.sellStock(cANLUserAccStock.id, cprice, cANLUserAccStock.totalAmount);
			}
			float shouyi = (cprice - cANLUserAccStock.buyPrices)/cANLUserAccStock.buyPrices;
			if(shouyi > 0.03 || shouyi < -0.03) // 止盈止损卖出
			{
				cUserAcc.sellStock(cANLUserAccStock.id, cprice, cANLUserAccStock.totalAmount);
			}
		}
		int iNeedBuyCnt = iMaxHoldCnt - cUserAcc.stockList.size();
		for(int i = 0; i< iNeedBuyCnt; i++) // 手中持票数量不足时进行买入
		{
			float usedMoney = cUserAcc.money/(iNeedBuyCnt-i);//拿出相应仓位钱
			for(int j=0;j<stockCKList.size();j++)// 遍历可操作票
			{
				ANLPolicyStockCK cANLPolicyStockCK = stockCKList.get(j);
				if(spool.getStock(cANLPolicyStockCK.stockID).GetLastDate().compareTo(date)!=0) //股票最后日期与当前最后日期不同 继续下一个
				{
					continue;
				}
				
				boolean alreayHas = false;
				for(int k = 0; k < cUserAcc.stockList.size(); k++) // 遍历持仓票，判断是否已经持有
				{
					ANLUserAccStock cANLUserAccStock = cUserAcc.stockList.get(k);
					if(cANLPolicyStockCK.stockID.compareTo(cANLUserAccStock.id) == 0)
					{
						alreayHas = true;
						break;
					}
				}
				if(!alreayHas)
				{
					String buy_id = cANLPolicyStockCK.stockID;
					float buy_price = spool.getStock(buy_id).GetLastPrice();
					int buy_amount = (int)(usedMoney/buy_price)/100*100;
					cUserAcc.buyStock(buy_id, buy_price, buy_amount);
					stockCKList.remove(j); // 买入后从列表清除
					break;
				}
			}
		}
		
		return;
	}
	public static void main(String[] args) throws InterruptedException {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		fmt.format("main begin\n");
		s_cANLPolicyJZHG.rmlog();
		s_cANLPolicyJZHG.run("2016-01-01", "2016-12-31");
		fmt.format("main end\n");
	}
}
