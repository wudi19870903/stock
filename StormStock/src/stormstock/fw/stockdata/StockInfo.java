package stormstock.fw.stockdata;

public class StockInfo {

	public StockInfo()
	{
		ID = "";
		name = "";
		price = 0.0f;
		allMarketValue = 0.0f;
		circulatedMarketValue = 0.0f;
		peRatio = 0.0f;
	}
	
	public void CopyFrom(StockInfo cCopyFromObj)
	{
		if(null != cCopyFromObj)
		{
			ID = cCopyFromObj.ID;
			name = cCopyFromObj.name;
			price = cCopyFromObj.price;
			allMarketValue = cCopyFromObj.allMarketValue;
			circulatedMarketValue = cCopyFromObj.circulatedMarketValue;
			peRatio = cCopyFromObj.peRatio;
		}
	}
	
	public String ID;
	public String name;
	public float price; // дЊ
	public float allMarketValue; // вк
	public float circulatedMarketValue; // вк
	public float peRatio;
}
