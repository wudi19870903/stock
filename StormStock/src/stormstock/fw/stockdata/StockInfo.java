package stormstock.fw.stockdata;

public class StockInfo {
	public String name;
	public float price; // Ԫ
	public float allMarketValue; // ��
	public float circulatedMarketValue; // ��
	public float peRatio;
	public StockInfo()
	{
		name = "";
		price = 0.0f;
		allMarketValue = 0.0f;
		circulatedMarketValue = 0.0f;
		peRatio = 0.0f;
	}
	public void CopyFrom(StockInfo cCopyFromObj)
	{
		name = cCopyFromObj.name;
		price = cCopyFromObj.price;
		allMarketValue = cCopyFromObj.allMarketValue;
		circulatedMarketValue = cCopyFromObj.circulatedMarketValue;
		peRatio = cCopyFromObj.peRatio;
	}
}
