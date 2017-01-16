package stormstock.ori.stockdata;

public class CommonDef {
	
	/*
	 * 股票简项
	 * id-名称
	 */
	public static class StockSimpleItem
	{
		public StockSimpleItem(){}
		public StockSimpleItem(String in_id, String in_name)
		{
			id = in_id;
			name = in_name;
		}
		public StockSimpleItem(StockSimpleItem cStockSimpleItem)
		{
			name = cStockSimpleItem.name;
			id = cStockSimpleItem.id;
		}
		public String name;
		public String id;
	}
	
	/*
	 * 日内交易明细
	 * 时间-价格-成交量
	 */
	public static class DayDetailItem implements Comparable
	{
		public String time;
		public float price;
		public float volume; 
		@Override
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			DayDetailItem sdto = (DayDetailItem)o;
		    return this.time.compareTo(sdto.time);
		}
	}
	
	/*
	 * 日K数据
	 * 日期-开盘价-收盘价-最低价-最高价-成交量
	 */
	public static class DayKData implements Comparable
	{
		// 2015-09-18
		public String date;
		public float open;
		public float close;
		public float low;
		public float high;
		public float volume;
		@Override
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			DayKData sdto = (DayKData)arg0;
		    return this.date.compareTo(sdto.date);
		}
	}
	
	/*
	 * 分红派息因子
	 * 日期-送股-转送-派息
	 */
	public static class DividendPayout implements Comparable
	{
		public String date;
		public float songGu;
		public float zhuanGu;
		public float paiXi;
		@Override
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			DividendPayout sdto = (DividendPayout)o;
		    return this.date.compareTo(sdto.date);
		}
	}
	
	/*
	 * 股票实时信息
	 * 名字-日期-时间-当前价
	 */
	public static class RealTimeInfo implements Comparable
	{
		public String name;
		public String date;
		public String time;
		public float curPrice;
		
		@Override
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			RealTimeInfo sdto = (RealTimeInfo)o;
			int iCheck1 = this.date.compareTo(sdto.date);
			if(0 == iCheck1)
			{
				int iCheck2 = this.time.compareTo(sdto.time);
				return iCheck2;
			}
			else
			{
				return iCheck1;
			}
		}
	}
	/*
	 * 股票实时信息（更多信息）
	 * 股票实时信息 +　总市值，流通市值，市盈率
	 */
	public static class RealTimeInfoMore extends RealTimeInfo
	{
		public float allMarketValue; //总市值
		public float circulatedMarketValue; // 流通市值
		public float peRatio; //市盈率
	}
	
	
	/*
	 * 股票基本信息
	 * 名字-当前价-总市值-流通市值-市盈率等（将来扩展为行业等）
	 */
	public static class StockBaseInfo
	{
		public String name;
		public float price; // 元
		public float allMarketValue; // 亿
		public float circulatedMarketValue; // 亿
		public float peRatio;
		public StockBaseInfo()
		{
			name = "";
			price = 0.0f;
			allMarketValue = 0.0f;
			circulatedMarketValue = 0.0f;
			peRatio = 0.0f;
		}
		public void CopyFrom(StockBaseInfo cCopyFromObj)
		{
			name = cCopyFromObj.name;
			price = cCopyFromObj.price;
			allMarketValue = cCopyFromObj.allMarketValue;
			circulatedMarketValue = cCopyFromObj.circulatedMarketValue;
			peRatio = cCopyFromObj.peRatio;
		}
	}
}
