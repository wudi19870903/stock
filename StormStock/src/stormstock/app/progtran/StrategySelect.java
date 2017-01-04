package stormstock.app.progtran;
import stormstock.fw.tran.Context;
import stormstock.fw.tran.IStrategySelect;

public class StrategySelect extends IStrategySelect {

	@Override
	public void strategy_select(Context ctx, SelectResult out_sr) {
		// ��������ֵλ��250������
		float EigenPriceLocLong = 0;
		// ��60�վ���ƫ��ٷֱ�
		float MADeviation60 = 0;
		// ��250�վ���ƫ��ٷֱ�
		float MADeviation250 = 0;
		if(MADeviation60 < -0.1 && MADeviation250 < -0.06 
				&& EigenPriceLocLong < 0.4 && EigenPriceLocLong > 0.1) {
			out_sr.bSelect = true;
			//out_sr.fPriority = (float) Math.random();
			//ANLLog.outputLog("    stock %s %s %s %.2f EigenSample1(%.3f) EigenSample2(%.3f)\n", in_stock.id, in_stock.curBaseInfo.name, in_stock.GetLastDate(), in_stock.GetLastPrice(),EigenSample1,EigenSample2);
		}
	}

}
