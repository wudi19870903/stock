package stormstock.fw.tran.strategy;

abstract public class IStrategyCreate {
	// �����µ�����
	public static class CreateResult {
		public CreateResult() {
			bCreate = false;
			fPositionRatio = 0.3f;
			fMaxMoney = 10000*100;
		}
		public boolean bCreate; // ���ֱ�־
		public float fPositionRatio; // �����λ
		public float fMaxMoney; // �����������ֵ���ƣ�
	}
	
	abstract public void strategy_create(StockContext ctx, CreateResult out_sr);
	abstract public int strategy_create_max_count(StockContext ctx, CreateResult out_sr);
}
