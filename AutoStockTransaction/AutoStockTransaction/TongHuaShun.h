#include "Common.h"

#include "windows.h"
#include "commctrl.h"

//////////////////////////////////////////////////////////////////////

// 交易动作定义
enum TRANACT
{
	BUY,
	SELL,
};

// 当日委托单定义
struct CommissionOrder
{
	std::string date; // 日期
	std::string time; // 时间
	std::string stockID; // 股票ID
	TRANACT tranAct; // 交易动作
	int commissionAmount; // 委托数量
	float commissionPrice; // 委托价格
	int dealAmount; // 成交数量
	float dealPrice; // 成交价格
};

// 持股定义
struct HoldStock
{
	std::string stockID; // 股票ID
	int totalAmount; // 总数量
	int availableAmount; // 可卖数量
	float refPrimeCostPrice; // 参考成本价
	float curPrice; // 当前价
};

// 当日成交单定义
struct DealOrder
{
	std::string date; // 日期
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
float THSAPI_GetAvailableMoney();

// 获取账户总资产
float THSAPI_GetTotalAssets();

// 获取账户股票总市值
float THSAPI_GetAllStockMarketValue();

// 获取当日委托列表
bool THSAPI_GetCommissionOrderList(std::list<CommissionOrder> & resultList);

// 获取持股列表
bool THSAPI_GetHoldStock(std::list<HoldStock> & resultList);

// 获取当日交割单列表
bool THSAPI_GetDealOrderList(std::list<DealOrder> & resultList);

// 买入股票接口
int THSAPI_BuyStock(const char* stockId, const int buyAmount, const float price);

// 卖出股票接口
int THSAPI_SellStock(const char* stockId, const int sellAmount, const float price);

