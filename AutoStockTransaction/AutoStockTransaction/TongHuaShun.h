#include "Common.h"

#include "windows.h"
#include "commctrl.h"

//////////////////////////////////////////////////////////////////////

// 交易动作定义
enum TRANACT
{
	TRANACT_BUY,
	TRANACT_SELL,
};

// 持股定义
struct HoldStock
{
	std::string stockID; // 股票ID
	int totalAmount; // 总数量
	int availableAmount; // 可卖数量
	float refProfitLoss; // 参考盈亏
	float refPrimeCostPrice; // 参考成本价
	float curPrice; // 当前价
};

// 当日委托单定义
struct CommissionOrder
{
	std::string time; // 时间
	std::string stockID; // 股票ID
	TRANACT tranAct; // 交易动作
	int commissionAmount; // 委托数量
	float commissionPrice; // 委托价格
	int dealAmount; // 成交数量
	float dealPrice; // 成交价格
};

// 当日成交单定义
struct DealOrder
{
	std::string time; // 时间
	std::string stockID; // 股票ID
	TRANACT tranAct; // 交易动作
	int dealAmount; // 成交数量
	float dealPrice; // 成交价格
};


/////////////////////////////////////////////////////////////////////////

// 同花顺外挂初始化
int THSAPI_TongHuaShunInit();

// 获取账户可用金额
int THSAPI_GetAvailableMoney(float & availableMoney);

// 获取账户总资产
int THSAPI_GetTotalAssets(float & totalAssets);

// 获取账户股票总市值
int THSAPI_GetAllStockMarketValue(float & allStockMarketValue);

// 获取持股列表
int THSAPI_GetHoldStockList(std::list<HoldStock> & resultList);

// 获取当日委托列表
int THSAPI_GetCommissionOrderList(std::list<CommissionOrder> & resultList);

// 获取当日成交单列表
int THSAPI_GetDealOrderList(std::list<DealOrder> & resultList);

// 买入委托下单接口
int THSAPI_BuyStock(const char* stockId, const int buyAmount, const float price);

// 卖出委托下单接口
int THSAPI_SellStock(const char* stockId, const int sellAmount, const float price);

