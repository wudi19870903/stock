package stormstock.analysis;

public class ANLStockKData {
	public float open;
	public float close;
	public float high;
	public float low;
	public float volume;
	public String datetime; // eg: "2008-01-02 15:00:00"
	public int minspan; // eg: 5 - 5分钟k线  60*4=240 日线 
}
