package stormstock.run.bt;

import java.util.List;

import stormstock.analysis.ANLEigen;
import stormstock.analysis.ANLStock;
import stormstock.analysis.ANLStockDayKData;
import stormstock.analysis.ANLUtils;

public class StrategyPriceDrop  extends ANLEigen {
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
				int indexHigh = ANLUtils.indexHigh(dayklist, iCheckB, iCheckE);
				int indexLow = ANLUtils.indexLow(dayklist, iCheckB, iCheckE);
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
//						outputLog("    Test findLatestXiaCuoRange [%s,%s] ZhenFu(%.3f,%.3f)\n",
//								dayklist.get(iCheckB).date,
//								dayklist.get(iCheckE).date,
//								xiaCuoZhenFu,xiaCuoMinCheck);
					
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
	
	@Override
	public Object calc(ANLStock cANLStock) {
		float param_XiaCuo = 100.0f;
		if(cANLStock.historyData.size() < 10) {
			return param_XiaCuo;
		}
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
