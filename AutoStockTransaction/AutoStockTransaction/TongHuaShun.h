#include "Common.h"

#include "windows.h"
#include "commctrl.h"

int THSAPI_TongHuaShunInit();

// ��ȡ�˻����ý��
float THSAPI_GetAvailableMoney();
// ��ȡ�˻����ʲ�
float THSAPI_GetAllMoney();
// ��ȡ�˻���Ʊ����ֵ
float THSAPI_GetAllStockMarketValue();
// ��ȡ�ֹ��б�
struct HoldStock
{
	std::string stockID;
	int totalAmount;
	int availableAmount;
	float refPrimeCostPrice;
	float curPrice;
};
bool THSAPI_GetHoldStock(std::list<HoldStock> & resultList);
// ��ȡ����ί���б�
struct CommissionOrder
{
};
bool THSAPI_GetCommissionOrderList(std::list<CommissionOrder> & resultList);
// �����Ʊ�ӿ�
int THSAPI_BuyStock(const char* stockId, const int buyAmount, const float price);
// ������Ʊ�ӿ�
int THSAPI_SellStock(const char* stockId, const int sellAmount, const float price);

int THSAPI_GetHoldStockList();

