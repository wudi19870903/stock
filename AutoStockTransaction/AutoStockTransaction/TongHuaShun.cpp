#include "stdafx.h"
#include "TongHuaShun.h"
#include "Common.h"
#include "math.h"

#include "windows.h"
#include "commctrl.h"
#include "WinHandle.h"

static bool s_initFLag = false;
static HWND s_hMainWin = NULL;
static HWND s_hLeftTreeView = NULL;

static HWND s_hZijinGupiaoWin = NULL;
static HWND s_hHoldStockWin = NULL;
static HWND s_hCommissionOrderWin = NULL;
static HWND s_hDealOrderWin = NULL;

static HWND s_hBuyWin = NULL;
static HWND s_hSellWin = NULL;

int THSAPI_TongHuaShunInit()
{
	TESTLOG("THSAPI_TongHuaShunInit#\n");

	// 初始化窗口句柄
	HWND hWnd = FindTongHuaShunMainWin();
	TESTLOG("hWnd = 0x%x\n",hWnd);
	if (NULL == hWnd)
	{
		TESTLOG("THSAPI_TongHuaShunInit# [ERROR] main win error, need 网上股票交易系统5.0!\n");
		return -10;
	}
	s_hMainWin = hWnd;
	setMainWin(s_hMainWin);
	TESTLOG("THSAPI_TongHuaShunInit# search main win ok\n");

	// 初始化左侧TreeView句柄
	HWND hLeftTreeView = findLeftTreeView(hWnd);
	if (NULL == hLeftTreeView)
	{
		TESTLOG("THSAPI_TongHuaShunInit# [ERROR] LeftTreeView win error\n");
		return -20;
	}
	s_hLeftTreeView = hLeftTreeView;
	TESTLOG("THSAPI_TongHuaShunInit# search LeftTreeView ok\n");
	if (selectMasterTreeViewItem(hLeftTreeView, -1)<0)
	{
		TESTLOG("THSAPI_TongHuaShunInit# [ERROR] LeftTreeView win error 2\n");
		return -21;
	}

	// 初始化资金股票主窗口句柄
	HWND hZijinGupiaoWin = findZijinGupiaoWin(hWnd);
	if (NULL == hZijinGupiaoWin)
	{
		TESTLOG("THSAPI_TongHuaShunInit# [ERROR] ZijinGupiao win error\n");
		return -30;
	}
	s_hZijinGupiaoWin = hZijinGupiaoWin;
	TESTLOG("THSAPI_TongHuaShunInit# search ZijinGupiaoWin ok\n");

	// 初始化持股窗口句柄
	HWND hHoldStockWin = findHoldStockWin(hWnd);
	if (NULL == hHoldStockWin)
	{
		TESTLOG("THSAPI_TongHuaShunInit# [ERROR] hHoldStockWin error\n");
		return -31;
	}
	s_hHoldStockWin = hHoldStockWin;
	TESTLOG("THSAPI_TongHuaShunInit# search HoldStockWin ok\n");

	// 初始化当日委托窗口句柄
	HWND hCommissionOrderWin = findCommissionOrderWin(hWnd);
	if (NULL == hCommissionOrderWin)
	{
		TESTLOG("THSAPI_TongHuaShunInit# [ERROR] findCommissionOrderWin error\n");
		return -32;
	}
	s_hCommissionOrderWin = hCommissionOrderWin;
	TESTLOG("THSAPI_TongHuaShunInit# search CommissionOrderWin ok\n");

	////// 初始化当日成交窗口句柄
	HWND hDealOrderWin = findDealOrderWin(hWnd);
	if (NULL == hDealOrderWin)
	{
		TESTLOG("THSAPI_TongHuaShunInit# [ERROR] findDealOrderWin error\n");
		return -33;
	}
	s_hDealOrderWin = hDealOrderWin;
	TESTLOG("THSAPI_TongHuaShunInit# search DealOrderWin ok\n");

	// 初始化买入窗口句柄
	HWND hBuyWin = findBuyWin(hWnd);
	if (NULL == hBuyWin)
	{
		TESTLOG("THSAPI_TongHuaShunInit# [ERROR] Buy win error\n");
		return -40;
	}
	s_hBuyWin = hBuyWin;
	TESTLOG("THSAPI_TongHuaShunInit# search BuyWin ok\n");

	// 初始化卖出窗口句柄
	HWND hSellWin = findSellWin(hWnd);
	if (NULL == hSellWin)
	{
		TESTLOG("THSAPI_TongHuaShunInit# [ERROR] Sell win error\n");
		return -50;
	}
	s_hSellWin = hSellWin;
	TESTLOG("THSAPI_TongHuaShunInit# search SellWin ok\n");

	// 关闭无用窗口
	if (0!=CancelAllMainWin())
	{
		TESTLOG("THSAPI_TongHuaShunInit# [ERROR] CancelAllMainWin error\n");
		return -60;
	}

	Flush_F5();
	Hide_MainWin();

	s_initFLag = true;
	return 0;
}

int THSAPI_GetAvailableMoney(float & availableMoney)
{
	TESTLOG("THSAPI_GetAvailableMoney#\n");
	if (!s_initFLag)
	{
		return -10;
	}
	Flush_F5();
	if (!s_hZijinGupiaoWin)
	{
		TESTLOG("THSAPI_GetAvailableMoney# [ERROR] ZijinGupiaoWin error\n");
		return -20;
	}
	HWND hChild = NULL;
	int index =0;
	for(;;) {
		hChild = FindWindowExW(s_hZijinGupiaoWin, hChild, 0, 0);
		if (hChild == NULL)
			break;
		index++;
		char szTitle[200];
		char szClass[200];
		char szWinText[200];
		GetWindowText(hChild, szTitle, sizeof(szTitle) / sizeof(char));
		GetClassName(hChild, szClass, sizeof(szClass) / sizeof(char));
		GetWindowText(hChild, szWinText, sizeof(szWinText) / sizeof(char));
		if (8 == index)
		{
			for (int i=0; i<500; i++)
			{
				GetWindowText(hChild, szWinText, sizeof(szWinText) / sizeof(char));
				if (0 != strcmp(szWinText, ""))
				{
					TESTLOG("THSAPI_GetAvailableMoney# hWnd = 0x%x szClass[%s] szWinText[%s]\n",hChild, szClass,szWinText);
					float val = 0.0f;
					sscanf_s(szWinText ,"%f", &val);
					availableMoney = val;
					return 0;
				}
				Sleep(10);
			}
			TESTLOG("THSAPI_GetAvailableMoney# [ERROR] THSAPI_GetAvailableMoney\n");
			return -30;
		}
	}
	return -100;
}

int THSAPI_GetTotalAssets(float & totalAssets)
{
	TESTLOG("THSAPI_GetTotalAssets#\n");
	if (!s_initFLag)
	{
		return -10;
	}

	Flush_F5();
	if (!s_hZijinGupiaoWin)
	{
		TESTLOG("THSAPI_GetTotalAssets# [ERROR] ZijinGupiaoWin error\n");
		return -20;
	}
	HWND hChild = NULL;
	int index =0;
	for(;;) {
		hChild = FindWindowExW(s_hZijinGupiaoWin, hChild, 0, 0);
		if (hChild == NULL)
			break;
		index++;
		char szTitle[200];
		char szClass[200];
		char szWinText[200];
		GetWindowText(hChild, szTitle, sizeof(szTitle) / sizeof(char));
		GetClassName(hChild, szClass, sizeof(szClass) / sizeof(char));
		GetWindowText(hChild, szWinText, sizeof(szWinText) / sizeof(char));
		if (14 == index)
		{
			for (int i=0; i<500; i++)
			{
				GetWindowText(hChild, szWinText, sizeof(szWinText) / sizeof(char));
				if (0 != strcmp(szWinText, ""))
				{
					TESTLOG("THSAPI_GetTotalAssets# hWnd = 0x%x szClass[%s] szWinText[%s]\n",hChild, szClass,szWinText);
					float val = 0.0f;
					sscanf_s(szWinText ,"%f", &val);
					totalAssets = val;
					return 0;
				}
				Sleep(10);
			}
			TESTLOG("THSAPI_GetTotalAssets# [ERROR] THSAPI_GetTotalAssets\n");
			return -30;
		}
	}
	return -100;
}

int THSAPI_GetAllStockMarketValue(float & allStockMarketValue)
{
	TESTLOG("THSAPI_GetAllStockMarketValue#\n");
	if (!s_initFLag)
	{
		return -10;
	}

	Flush_F5();
	if (!s_hZijinGupiaoWin)
	{
		TESTLOG("THSAPI_GetAllStockMarketValue# [ERROR] ZijinGupiaoWin error\n");
		return -20;
	}
	HWND hChild = NULL;
	int index =0;
	for(;;) {
		hChild = FindWindowExW(s_hZijinGupiaoWin, hChild, 0, 0);
		if (hChild == NULL)
			break;
		index++;
		char szTitle[200];
		char szClass[200];
		char szWinText[200];
		GetWindowText(hChild, szTitle, sizeof(szTitle) / sizeof(char));
		GetClassName(hChild, szClass, sizeof(szClass) / sizeof(char));
		GetWindowText(hChild, szWinText, sizeof(szWinText) / sizeof(char));
		if (13 == index)
		{
			for (int i=0; i<500; i++)
			{
				GetWindowText(hChild, szWinText, sizeof(szWinText) / sizeof(char));
				if (0 != strcmp(szWinText, ""))
				{
					TESTLOG("THSAPI_GetStockMarketValue# hWnd = 0x%x szClass[%s] szWinText[%s]\n",hChild, szClass,szWinText);
					float val = 0.0f;
					sscanf_s(szWinText ,"%f", &val);
					allStockMarketValue = val;
					return  0;
				}
				Sleep(10);
			}
			TESTLOG("THSAPI_GetStockMarketValue# [ERROR] THSAPI_GetStockMarketValue\n");
			return -30;
		}
	}
	return -100;
}

int THSAPI_GetHoldStockList(std::list<HoldStock> & resultList)
{
	if (!s_initFLag)
	{
		return -10;
	}

	resultList.clear();

	if (s_hHoldStockWin)
	{
		std::string buf;
		bool bCopy = getCtrlVFormWin(s_hHoldStockWin,buf);
		 
		// 解析拷贝数据
		if(bCopy && buf.length()>0)
		{
			//printf("%s", buf.c_str());
			std::string copyStr = DString::replace(buf, "\r", "");
			std::list<std::string> rows =  DString::split(copyStr, "\n");

			std::list<std::string> row0_cols = DString::split(rows.front(), "\t");
			rows.pop_front();

			// 确定列索引
			int iCol_ID = -1;
			int iCol_TotalAmount = -1;
			int iCol_AvailableAmount = -1;
			int iCol_RefProfitLoss = -1;
			int iCol_RefPrimeCostPrice = -1;
			int iCol_CurPrice = -1;
			{
				std::list<std::string>::iterator it;
				int indexCol = 0;
				for (it = row0_cols.begin(); it != row0_cols.end(); it++,indexCol++)
				{
					std::string cell = *it;

					if (0 == cell.compare("证券代码"))
					{
						iCol_ID = indexCol;
					}
					if (0 == cell.compare("股票余额"))
					{
						iCol_TotalAmount = indexCol;
					}
					if (0 == cell.compare("可用余额"))
					{
						iCol_AvailableAmount = indexCol;
					}
					if (0 == cell.compare("参考盈亏"))
					{
						iCol_RefProfitLoss = indexCol;
					}
					if (0 == cell.compare("参考成本价"))
					{
						iCol_RefPrimeCostPrice = indexCol;
					}
					if (0 == cell.compare("市价"))
					{
						iCol_CurPrice = indexCol;
					}
				}
			}

			if (-1==iCol_ID 
				|| -1==iCol_TotalAmount
				|| -1==iCol_AvailableAmount
				|| -1 == iCol_RefProfitLoss
				|| -1==iCol_RefPrimeCostPrice
				|| -1==iCol_CurPrice)
			{
				return -20;
			}

			// 遍历余行
			{
				std::list<std::string>::iterator it_row;
				for (it_row = rows.begin(); it_row != rows.end(); it_row++)
				{
					std::string row = *it_row;
					std::list<std::string> row_cols = DString::split(row, "\t");

					if (row0_cols.size() != row_cols.size())
					{
						return -30; // 列不吻合
					}
					

					HoldStock cHoldStock;

					std::list<std::string>::iterator it_col;
					int indexCol = 0;
					for (it_col = row_cols.begin(); it_col != row_cols.end(); it_col++,indexCol++)
					{
						std::string cell = *it_col;

						if (indexCol == iCol_ID)
						{
							cHoldStock.stockID = cell;
						}
						if (indexCol == iCol_TotalAmount)
						{
							cHoldStock.totalAmount = atoi(cell.c_str());
						}
						if (indexCol == iCol_AvailableAmount)
						{
							cHoldStock.availableAmount = atoi(cell.c_str());
						}
						if (indexCol == iCol_RefProfitLoss)
						{
							cHoldStock.refProfitLoss = (float)atof(cell.c_str());
						}
						if (indexCol == iCol_RefPrimeCostPrice)
						{
							cHoldStock.refPrimeCostPrice = (float)atof(cell.c_str());
						}
						if (indexCol == iCol_CurPrice)
						{
							cHoldStock.curPrice = (float)atof(cell.c_str());
						}
					}

					resultList.push_back(cHoldStock);
				}
			}

			return 0;
		}
	}
	return -100;
}

int THSAPI_GetCommissionOrderList(std::list<CommissionOrder> & resultList)
{
	if (!s_initFLag)
	{
		return -10;
	}

	resultList.clear();

	if (s_hCommissionOrderWin)
	{
		std::string buf;
		bool bCopy = getCtrlVFormWin(s_hCommissionOrderWin,buf);

		// 解析拷贝数据
		if(bCopy && buf.length()>0)
		{
			//printf("%s", buf.c_str());
			std::string copyStr = DString::replace(buf, "\r", "");
			std::list<std::string> rows =  DString::split(copyStr, "\n");

			std::list<std::string> row0_cols = DString::split(rows.front(), "\t");
			rows.pop_front();

			// 确定列索引
			int iCol_time = -1;
			int iCol_stockID = -1;
			int iCol_tranAct = -1;
			int iCol_commissionAmount = -1;
			int iCol_commissionPrice = -1;
			int iCol_dealAmount = -1;
			int iCol_dealPrice = -1;
			{
				std::list<std::string>::iterator it;
				int indexCol = 0;
				for (it = row0_cols.begin(); it != row0_cols.end(); it++,indexCol++)
				{
					std::string cell = *it;
					if (0 == cell.compare("委托时间"))
					{
						iCol_time = indexCol;
					}
					if (0 == cell.compare("证券代码"))
					{
						iCol_stockID = indexCol;
					}
					if (0 == cell.compare("操作"))
					{
						iCol_tranAct = indexCol;
					}
					if (0 == cell.compare("委托数量"))
					{
						iCol_commissionAmount = indexCol;
					}
					if (0 == cell.compare("委托价格"))
					{
						iCol_commissionPrice = indexCol;
					}
					if (0 == cell.compare("成交数量"))
					{
						iCol_dealAmount = indexCol;
					}
					if (0 == cell.compare("成交均价"))
					{
						iCol_dealPrice = indexCol;
					}
				}
			}

			if (-1==iCol_time 
				|| -1==iCol_stockID
				|| -1==iCol_tranAct
				|| -1 == iCol_commissionAmount
				|| -1==iCol_commissionPrice
				|| -1==iCol_dealAmount
				|| -1==iCol_dealPrice)
			{
				return -20;
			}

			// 遍历余行
			{
				std::list<std::string>::iterator it_row;
				for (it_row = rows.begin(); it_row != rows.end(); it_row++)
				{
					std::string row = *it_row;
					std::list<std::string> row_cols = DString::split(row, "\t");

					if (row0_cols.size() != row_cols.size())
					{
						return -30; // 列不吻合
					}

					CommissionOrder cCommissionOrder;

					std::list<std::string>::iterator it_col;
					int indexCol = 0;
					for (it_col = row_cols.begin(); it_col != row_cols.end(); it_col++,indexCol++)
					{
						std::string cell = *it_col;

						if (indexCol == iCol_time)
						{
							cCommissionOrder.time = cell;
						}
						if (indexCol == iCol_stockID)
						{
							cCommissionOrder.stockID = cell;
						}
						if (indexCol == iCol_tranAct)
						{
							if (cell.compare("证券买入") == 0)
							{
								cCommissionOrder.tranAct = TRANACT_BUY;
							}
							if (cell.compare("证券卖出") == 0)
							{
								cCommissionOrder.tranAct = TRANACT_SELL;
							}
						}
						if (indexCol == iCol_commissionAmount)
						{
							cCommissionOrder.commissionAmount = atoi(cell.c_str());
						}
						if (indexCol == iCol_commissionPrice)
						{
							cCommissionOrder.commissionPrice = (float)atof(cell.c_str());
						}
						if (indexCol == iCol_dealAmount)
						{
							cCommissionOrder.dealAmount = atoi(cell.c_str());
						}
						if (indexCol == iCol_dealPrice)
						{
							cCommissionOrder.dealPrice = (float)atof(cell.c_str());
						}
					}

					resultList.push_back(cCommissionOrder);
				}
			}

			return 0;
		}
	}
	return -100;
}

int THSAPI_GetDealOrderList(std::list<DealOrder> & resultList)
{
	if (!s_initFLag)
	{
		return -10;
	}

	resultList.clear();

	if (s_hDealOrderWin)
	{
		std::string buf;
		bool bCopy = getCtrlVFormWin(s_hDealOrderWin,buf);

		// 解析拷贝数据
		if(bCopy && buf.length()>0)
		{
			//printf("%s", buf.c_str());
			std::string copyStr = DString::replace(buf, "\r", "");
			std::list<std::string> rows =  DString::split(copyStr, "\n");

			std::list<std::string> row0_cols = DString::split(rows.front(), "\t");
			rows.pop_front();

			// 确定列索引
			int iCol_time = -1;
			int iCol_stockID = -1;
			int iCol_tranAct = -1;
			int iCol_dealAmount = -1;
			int iCol_dealPrice = -1;
			{
				std::list<std::string>::iterator it;
				int indexCol = 0;
				for (it = row0_cols.begin(); it != row0_cols.end(); it++,indexCol++)
				{
					std::string cell = *it;
					if (0 == cell.compare("成交时间"))
					{
						iCol_time = indexCol;
					}
					if (0 == cell.compare("证券代码"))
					{
						iCol_stockID = indexCol;
					}
					if (0 == cell.compare("操作"))
					{
						iCol_tranAct = indexCol;
					}
					if (0 == cell.compare("成交数量"))
					{
						iCol_dealAmount = indexCol;
					}
					if (0 == cell.compare("成交均价"))
					{
						iCol_dealPrice = indexCol;
					}
				}
			}

			if (-1==iCol_time 
				|| -1==iCol_stockID
				|| -1==iCol_tranAct
				|| -1==iCol_dealAmount
				|| -1==iCol_dealPrice)
			{
				return -20;
			}

			// 遍历余行
			{
				std::list<std::string>::iterator it_row;
				for (it_row = rows.begin(); it_row != rows.end(); it_row++)
				{
					std::string row = *it_row;
					std::list<std::string> row_cols = DString::split(row, "\t");

					if (row0_cols.size() != row_cols.size())
					{
						return -30; // 列不吻合
					}

					DealOrder cDealOrder;

					std::list<std::string>::iterator it_col;
					int indexCol = 0;
					for (it_col = row_cols.begin(); it_col != row_cols.end(); it_col++,indexCol++)
					{
						std::string cell = *it_col;

						if (indexCol == iCol_time)
						{
							cDealOrder.time = cell;
						}
						if (indexCol == iCol_stockID)
						{
							cDealOrder.stockID = cell;
						}
						if (indexCol == iCol_tranAct)
						{
							if (cell.compare("证券买入") == 0)
							{
								cDealOrder.tranAct = TRANACT_BUY; 
							}
							if (cell.compare("证券卖出") == 0)
							{
								cDealOrder.tranAct = TRANACT_SELL;
							}
						}
						if (indexCol == iCol_dealAmount)
						{
							cDealOrder.dealAmount = atoi(cell.c_str());
						}
						if (indexCol == iCol_dealPrice)
						{
							cDealOrder.dealPrice = (float)atof(cell.c_str());
						}
					}

					resultList.push_back(cDealOrder);
				}
			}

			return 0;
		}
	}
	return -100;
}

int THSAPI_BuyStock(const char* stockId, const int buyAmount, const float price)
{
	TESTLOG("THSAPI_BuyStock#\n");
	if (!s_initFLag)
	{
		return -10;
	}

	Flush_F5();
	HWND hStockIDWin = NULL;
	HWND hStockPriceWin = NULL;
	HWND hStockAmountWin = NULL;
	HWND hStockBuyBtnWin = NULL;
	if (NULL == s_hBuyWin)
	{
		TESTLOG("THSAPI_BuyStock# [ERROR] BuyWin error\n");
		return -11;
	}
	if (0!=CancelAllMainWin())
	{
		TESTLOG("THSAPI_BuyStock# [ERROR] CancelAllMainWin error\n");
		return -12;
	}
	HWND hChildL1 = NULL;
	int index =0;
	for(;;) {
		hChildL1 = FindWindowExW(s_hBuyWin, hChildL1, 0, 0);
		if (hChildL1 == NULL)
			break;
		index++;
		char szTitleL1[200];
		char szClassL1[200];
		char szWinTextL1[200];
		GetWindowText(hChildL1, szTitleL1, sizeof(szTitleL1) / sizeof(char));
		GetClassName(hChildL1, szClassL1, sizeof(szClassL1) / sizeof(char));
		GetWindowText(hChildL1, szWinTextL1, sizeof(szWinTextL1) / sizeof(char));
		if (1 == index && 0 == strcmp("Edit",szClassL1))
		{
			hStockIDWin = hChildL1;
			TESTLOG("THSAPI_BuyStock# THSAPI_BuyStock hStockIDWin = 0x%x szClassL1[%s] szWinTextL1[%s]\n",hChildL1, szClassL1,szWinTextL1);
		}
		if (3 == index && 0 == strcmp("Edit",szClassL1))
		{
			hStockPriceWin = hChildL1;
			TESTLOG("THSAPI_BuyStock# THSAPI_BuyStock hStockPriceWin = 0x%x szClassL1[%s] szWinTextL1[%s]\n",hChildL1, szClassL1,szWinTextL1);
		}
		if (5 == index && 0 == strcmp("Edit",szClassL1))
		{
			hStockAmountWin = hChildL1;
			TESTLOG("THSAPI_BuyStock# THSAPI_BuyStock hStockAmountWin = 0x%x szClassL1[%s] szWinTextL1[%s]\n",hChildL1, szClassL1,szWinTextL1);
		}
		if (6 == index && 0 == strcmp("Button",szClassL1))
		{
			hStockBuyBtnWin = hChildL1;
			TESTLOG("THSAPI_BuyStock# THSAPI_BuyStock hStockBuyBtnWin = 0x%x szClassL1[%s] szWinTextL1[%s]\n",hChildL1, szClassL1,szWinTextL1);
		}
		if (index > 6)
		{
			if (!hStockIDWin || !hStockPriceWin || !hStockAmountWin || !hStockBuyBtnWin )
			{
				return -20;
			}
		}
	}
	
	// 选择买入画面
	selectMasterTreeViewItem(s_hLeftTreeView, 0);

	// 填写买入股票号
	for (int i=0;i<100;i++)
	{
		SendMessageA(hStockIDWin, WM_SETTEXT, 0, (LPARAM) stockId);
		Sleep(50);
		char checkstr[200];
		SendMessage(hStockIDWin, WM_GETTEXT, 200, long(checkstr));
		if (0 == strcmp(checkstr,stockId))
		{
			break;
		}
		CancelAllMainWin();
	}

	// 填写买入数量
	char szBuyAmount[200];
	sprintf_s(szBuyAmount, "%d", buyAmount);
	for (int i=0;i<10;i++)
	{
		SendMessageA(hStockAmountWin, WM_SETTEXT, 0, (LPARAM) szBuyAmount);
		Sleep(200);
		char checkstr[200];
		SendMessage(hStockAmountWin, WM_GETTEXT, 200, long(checkstr));
		if (0 == strcmp(checkstr,szBuyAmount))
		{
			break;
		}
		CancelAllMainWin();
	}

	// 填写买入价格
	SendMessageA(hStockPriceWin, WM_SETFOCUS, 0, 0);
	Sleep(10);
	char szPrice[200];
	sprintf_s(szPrice, "%.2f", price);
	for (int i=0;i<100;i++)
	{
		SendMessageA(hStockPriceWin, WM_SETTEXT, 0, (LPARAM) szPrice);
		Sleep(50);
		char checkstr[200];
		SendMessage(hStockPriceWin, WM_GETTEXT, 200, long(checkstr));
		if (0 == strcmp(checkstr,szPrice))
		{
			break;
		}
		CancelAllMainWin();
	}
	SendMessageA(hStockPriceWin, WM_SETFOCUS, 0, 0);

	// 点击买入按钮
	PostMessage(hStockBuyBtnWin,WM_LBUTTONDOWN,0,0);
	Sleep(10);
	PostMessage(hStockBuyBtnWin,WM_LBUTTONUP,0,0);
	for (int i=0;i<500;i++)
	{
		HWND hZhangDieXianZhiCheck = Find_ZhangDieXianZhi();
		int iFlag =0;
		string sId;
		int iAmount = 0;
		float fPrice = 0.0f;
		HWND hWeiTuoQueRenCheck = Find_WeiTuoQueRen(iFlag,sId, iAmount, fPrice);
		if (NULL!=hZhangDieXianZhiCheck || NULL!=hWeiTuoQueRenCheck)
		{
			break;
		}
		HWND hJiaoYiShiBai = Find_TijiaoShiBai();
		if (NULL!= hJiaoYiShiBai)
		{
			CloseTijiaoShiBai();
			return -25;
		}
		Sleep(10);
	}

    // 点击确认下单按钮
	for (int i=0;i<100;i++)
	{
		// 涨跌限制关闭，交易失败
		HWND hZhangDieXianZhi = Find_ZhangDieXianZhi();
		if (hZhangDieXianZhi)
		{
			CloseZhangDieXianZhi_Cancel();
			return -30;
		}
		// 委托确认
		int iFlag =0;
		string sId;
		int iAmount = 0;
		float fPrice = 0.0f;
		HWND hWeiTuoQueRen = Find_WeiTuoQueRen(iFlag,sId, iAmount, fPrice);
		if (hWeiTuoQueRen)
		{
			if (0 == iFlag &&
				0 == strcmp(sId.c_str(),stockId) &&
				iAmount == buyAmount &&
				fabs(fPrice-price) < 0.0001)
			{
				CloseWeiTuoQueRen_OK();
			}
			else
			{
				CloseWeiTuoQueRen_Cancel();
				return -31;
			}
			
			for (int i=0;i<500;i++)
			{
				HWND hTijiaoChengGongCheck = Find_TijiaoChengGong();
				HWND hJiaoYiShiBaiCheck = Find_TijiaoShiBai();
				if (NULL!=hTijiaoChengGongCheck || NULL!=hJiaoYiShiBaiCheck)
				{
					break;
				}
				Sleep(10);
			}

			//委托成功或失败确认
			for (int i=0;i<100;i++)
			{
				//交易成功
				HWND hTijiaoChengGong = Find_TijiaoChengGong();
				if (hTijiaoChengGong)
				{
					CloseTijiaoChengGong();
					return 0;
				}
				else
				{
					//交易成功
					HWND hJiaoYiChengGong = Find_TijiaoChengGong();
					if (hJiaoYiChengGong)
					{
						CloseTijiaoChengGong();
						return 0;
					}
					//交易失败
					HWND hJiaoYiShiBai = Find_TijiaoShiBai();
					if (hJiaoYiShiBai)
					{
						CloseTijiaoShiBai();
						return -40;
					}
				}
				Sleep(10);
			}
			break;
		}
		Sleep(10);
	}

    // 未知错误
	return -100;
}


int THSAPI_SellStock(const char* stockId, const int sellAmount, const float price)
{
	TESTLOG("THSAPI_SellStock#\n");
	if (!s_initFLag)
	{
		return -10;
	}

	Flush_F5();
	HWND hStockIDWin = NULL;
	HWND hStockPriceWin = NULL;
	HWND hStockAmountWin = NULL;
	HWND hStockSellBtnWin = NULL;
	if (NULL == s_hSellWin)
	{
		TESTLOG("THSAPI_SellStock# [ERROR] SellWin error\n");
		return -11;
	}
	if (0!=CancelAllMainWin())
	{
		TESTLOG("THSAPI_SellStock# [ERROR] CancelAllMainWin error\n");
		return -12;
	}
	HWND hChildL1 = NULL;
	int index =0;
	for(;;) {
		hChildL1 = FindWindowExW(s_hSellWin, hChildL1, 0, 0);
		if (hChildL1 == NULL)
			break;
		index++;
		char szTitleL1[200];
		char szClassL1[200];
		char szWinTextL1[200];
		GetWindowText(hChildL1, szTitleL1, sizeof(szTitleL1) / sizeof(char));
		GetClassName(hChildL1, szClassL1, sizeof(szClassL1) / sizeof(char));
		GetWindowText(hChildL1, szWinTextL1, sizeof(szWinTextL1) / sizeof(char));
		if (1 == index && 0 == strcmp("Edit",szClassL1))
		{
			hStockIDWin = hChildL1;
			TESTLOG("THSAPI_SellStock# THSAPI_BuyStock hStockIDWin = 0x%x szClassL1[%s] szWinTextL1[%s]\n",hChildL1, szClassL1,szWinTextL1);
		}
		if (3 == index && 0 == strcmp("Edit",szClassL1))
		{
			hStockPriceWin = hChildL1;
			TESTLOG("THSAPI_SellStock# THSAPI_BuyStock hStockPriceWin = 0x%x szClassL1[%s] szWinTextL1[%s]\n",hChildL1, szClassL1,szWinTextL1);
		}
		if (5 == index && 0 == strcmp("Edit",szClassL1))
		{
			hStockAmountWin = hChildL1;
			TESTLOG("THSAPI_SellStock# THSAPI_BuyStock hStockAmountWin = 0x%x szClassL1[%s] szWinTextL1[%s]\n",hChildL1, szClassL1,szWinTextL1);
		}
		if (6 == index && 0 == strcmp("Button",szClassL1))
		{
			hStockSellBtnWin = hChildL1;
			TESTLOG("THSAPI_SellStock# THSAPI_BuyStock hStockSellBtnWin = 0x%x szClassL1[%s] szWinTextL1[%s]\n",hChildL1, szClassL1,szWinTextL1);
		}
		if (index > 6)
		{
			if (!hStockIDWin || !hStockPriceWin || !hStockAmountWin || !hStockSellBtnWin )
			{
				return -20;
			}
		}
	}

	// 选择卖出画面
	selectMasterTreeViewItem(s_hLeftTreeView, 1);

	// 填写卖出股票号
	for (int i=0;i<100;i++)
	{
		SendMessageA(hStockIDWin, WM_SETTEXT, 0, (LPARAM) stockId);
		Sleep(50);
		char checkstr[200];
		SendMessage(hStockIDWin, WM_GETTEXT, 200, long(checkstr));
		if (0 == strcmp(checkstr,stockId))
		{
			break;
		}
		CancelAllMainWin();
	}

	// 填写卖出数量
	char szSellAmount[200];
	sprintf_s(szSellAmount, "%d", sellAmount);
	for (int i=0;i<10;i++)
	{
		SendMessageA(hStockAmountWin, WM_SETTEXT, 0, (LPARAM) szSellAmount);
		Sleep(200);
		char checkstr[200];
		SendMessage(hStockAmountWin, WM_GETTEXT, 200, long(checkstr));
		if (0 == strcmp(checkstr,szSellAmount))
		{
			break;
		}
		CancelAllMainWin();
	}

	// 填写卖出价格
	SendMessageA(hStockPriceWin, WM_SETFOCUS, 0, 0);
	Sleep(10);
	char szPrice[200];
	sprintf_s(szPrice, "%.2f", price);
	for (int i=0;i<100;i++)
	{
		SendMessageA(hStockPriceWin, WM_SETTEXT, 0, (LPARAM) szPrice);
		Sleep(50);
		char checkstr[200];
		SendMessage(hStockPriceWin, WM_GETTEXT, 200, long(checkstr));
		if (0 == strcmp(checkstr,szPrice))
		{
			break;
		}
		CancelAllMainWin();
	}
	SendMessageA(hStockPriceWin, WM_SETFOCUS, 0, 0);

	// 点击卖出按钮
	PostMessage(hStockSellBtnWin,WM_LBUTTONDOWN,0,0);
	Sleep(10);
	PostMessage(hStockSellBtnWin,WM_LBUTTONUP,0,0);
	for (int i=0;i<500;i++)
	{
		HWND hZhangDieXianZhiCheck = Find_ZhangDieXianZhi();
		int iFlag =0;
		string sId;
		int iAmount = 0;
		float fPrice = 0.0f;
		HWND hWeiTuoQueRenCheck = Find_WeiTuoQueRen(iFlag,sId, iAmount, fPrice);
		if (NULL!=hZhangDieXianZhiCheck || NULL!=hWeiTuoQueRenCheck)
		{
			break;
		}
		HWND hJiaoYiShiBai = Find_TijiaoShiBai();
		if (NULL!= hJiaoYiShiBai)
		{
			CloseTijiaoShiBai();
			return -25;
		}
		Sleep(10);
	}

	// 点击确认下单按钮
	for (int i=0;i<100;i++)
	{
		// 涨跌限制关闭，交易失败
		HWND hZhangDieXianZhi = Find_ZhangDieXianZhi();
		if (hZhangDieXianZhi)
		{
			CloseZhangDieXianZhi_Cancel();
			return -30;
		}

		// 委托确认
		int iFlag =0;
		string sId;
		int iAmount = 0;
		float fPrice = 0.0f;
		HWND hWeiTuoQueRen = Find_WeiTuoQueRen(iFlag,sId,iAmount, fPrice);
		if (hWeiTuoQueRen)
		{
			if (1 == iFlag && 
				0 == strcmp(sId.c_str(),stockId) && 
				iAmount == sellAmount && 
				fabs(fPrice-price) < 0.0001)
			{
				CloseWeiTuoQueRen_OK();
			}
			else
			{
				CloseWeiTuoQueRen_Cancel();
				return -31;
			}

			for (int i=0;i<500;i++)
			{
				HWND hTijiaoChengGongCheck = Find_TijiaoChengGong();
				HWND hJiaoYiShiBaiCheck = Find_TijiaoShiBai();
				if (NULL!=hTijiaoChengGongCheck || NULL!=hJiaoYiShiBaiCheck)
				{
					break;
				}
				Sleep(10);
			}

			//委托成功或失败确认
			for (int i=0;i<100;i++)
			{
				//交易成功
				HWND hTijiaoChengGong = Find_TijiaoChengGong();
				if (hTijiaoChengGong)
				{
					CloseTijiaoChengGong();
					return 0;
				}
				else
				{
					//交易成功
					HWND hJiaoYiChengGong = Find_TijiaoChengGong();
					if (hJiaoYiChengGong)
					{
						CloseTijiaoChengGong();
						return 0;
					}
					//交易失败
					HWND hJiaoYiShiBai = Find_TijiaoShiBai();
					if (hJiaoYiShiBai)
					{
						CloseTijiaoShiBai();
						return -40;
					}
				}
				Sleep(10);
			}
			break;
		}
		Sleep(10);
	}

	// 未知错误
	return -100;
}


