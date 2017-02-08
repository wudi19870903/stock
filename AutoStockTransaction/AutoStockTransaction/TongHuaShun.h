#include "Common.h"

#include "windows.h"
#include "commctrl.h"

int THSAPI_TongHuaShunInit();

// 获取账户可用金额
float THSAPI_GetAvailableMoney();
// 获取账户总资产
float THSAPI_GetAllMoney();
// 获取账户股票总市值
float THSAPI_GetAllStockMarketValue();
// 获取持股列表
struct HoldStock
{
	std::string stockID;
	int totalAmount;
	int availableAmount;
	float refPrimeCostPrice;
	float curPrice;
};
bool THSAPI_GetHoldStock(std::list<HoldStock> & resultList);
// 获取当日委托列表
struct CommissionOrder
{
};
bool THSAPI_GetCommissionOrderList(std::list<CommissionOrder> & resultList);
// 买入股票接口
int THSAPI_BuyStock(const char* stockId, const int buyAmount, const float price);
// 卖出股票接口
int THSAPI_SellStock(const char* stockId, const int sellAmount, const float price);

int THSAPI_GetHoldStockList();

