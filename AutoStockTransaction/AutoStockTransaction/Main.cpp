#include "stdafx.h"
#include "Common.h"
#include "TongHuaShun.h"
#include "WinHandle.h"

void Test()
{
	int iRetInit = THSAPI_TongHuaShunInit();

	std::list<CommissionOrder> cResultList;
	int iHoldStockRet = THSAPI_GetCommissionOrderList(cResultList);
	

	//HWND hZhangDieXianZhi = Find_ZhangDieXianZhi();
	//if (NULL != hZhangDieXianZhi)
	//{
	//	int iClose = CloseZhangDieXianZhi_Cancel();
	//	iClose = 10;
	//}

	//int iFlag =0;
	//string sId;
	//int iAmount = 0;
	//float fPrice = 0.0f;
	//HWND hWeiTuoQueRen = Find_WeiTuoQueRen(iFlag, sId, iAmount,fPrice);
	//if (NULL != hWeiTuoQueRen)
	//{
	//	int iClose = CloseWeiTuoQueRen_Cancel();
	//	iClose = 10;
	//}

	HWND hTijiaoShiBai = Find_TijiaoShiBai();
	if (NULL != hTijiaoShiBai)
	{
		int iClose = CloseTijiaoShiBai();
		iClose = 10;
	}

	//HWND hTijiaoChengGong = Find_TijiaoChengGong();
	//if (NULL != hTijiaoChengGong)
	//{
	//	int iClose = CloseTijiaoChengGong();
	//	iClose = 10;
	//}
}

int _tmain(int argc, _TCHAR* argv[])
{
	//Test();
	//return 0;
	//////////////////////////////////////////////////////////////////////////
	DFileLog::GetInstance()->Clear();
	DFileLog::GetInstance()->EnableSaveLog(true);

	printf("### Main Begin\n");

	int iInitRetFirst = -1;
	////////////////////////////////////////////////////////////////////////// -> test THSAPI_TongHuaShunInit
	{
		int iTestTimes_Init = 1;
		for (int i=0; i<iTestTimes_Init; i++)
		{
			if (0 == i)
			{
				iInitRetFirst = THSAPI_TongHuaShunInit();
			}
			else
			{
				int iInitRet = THSAPI_TongHuaShunInit();
				if (iInitRetFirst != iInitRet)
				{
					printf("[TEST] THSAPI_TongHuaShunInit Error [%d]\n", iInitRet);
				}
			}
		}
		printf("[TEST] THSAPI_TongHuaShunInit = %d\n", iInitRetFirst);
	}

	if (iInitRetFirst>=0)
	{

		////////////////////////////////////////////////////////////////////////// -> test THSAPI_GetAvailableMoney
		{
			int iTestTimes_AvailableMoney = 1;
			float fAvailableMoneyFirst = -1;
			for (int i=0; i<iTestTimes_AvailableMoney; i++)
			{
				if (0 == i)
				{
					THSAPI_GetAvailableMoney(fAvailableMoneyFirst);
				}
				else
				{
					float fAvailableMoney = -1;
					THSAPI_GetAvailableMoney(fAvailableMoney);
					if (fAvailableMoneyFirst != fAvailableMoney)
					{
						printf("[TEST] THSAPI_GetAvailableMoney Error [%.2f]\n", fAvailableMoney);
					}
				}
			}
			printf("[TEST] THSAPI_GetAvailableMoney = %f\n", fAvailableMoneyFirst);
		}

		////////////////////////////////////////////////////////////////////////// -> test THSAPI_GetTotalAssets
		{
			int iTestTimes_TotalAssets = 1;
			float fTotalAssetsFirst = -1;
			for (int i=0; i<iTestTimes_TotalAssets; i++)
			{
				if (0 == i)
				{
					THSAPI_GetTotalAssets(fTotalAssetsFirst);
				}
				else
				{
					float fTotalAssets = -1;
					THSAPI_GetTotalAssets(fTotalAssets);
					if (fTotalAssetsFirst != fTotalAssets)
					{
						printf("[TEST] THSAPI_GetTotalAssets Error [%.2f]\n", fTotalAssets);
					}
				}
			}
			printf("[TEST] THSAPI_GetTotalAssets = %f\n", fTotalAssetsFirst);
		}


		////////////////////////////////////////////////////////////////////////// -> test THSAPI_GetAllStockMarketValue
		{
			int iTestTimes_StockMarketValue = 1;
			float fStockMarketValueFirst = -1;
			for (int i=0; i<iTestTimes_StockMarketValue; i++)
			{
				if (0 == i)
				{
					THSAPI_GetAllStockMarketValue(fStockMarketValueFirst);
				}
				else
				{
					float fStockMarketValue = -1;
					THSAPI_GetAllStockMarketValue(fStockMarketValue);
					if (fStockMarketValueFirst != fStockMarketValue)
					{
						printf("[TEST] THSAPI_GetStockMarketValue Error [%.2f]\n", fStockMarketValue);
					}
				}
			}
			printf("[TEST] THSAPI_GetStockMarketValue = %f\n", fStockMarketValueFirst);
		}

		////////////////////////////////////////////////////////////////////////// -> test THSAPI_BuyStock
		{
			int iTestTimes_Buy = 0;
			int iBuyRetFirst = -1;
			for (int i=0; i<iTestTimes_Buy; i++)
			{
				if (0 == i)
				{
					iBuyRetFirst = THSAPI_BuyStock("601988",100,1.00);
				}
				else
				{
					int iBuyRet = THSAPI_BuyStock("601988",100,1.00);
					if (iBuyRetFirst != iBuyRet)
					{
						printf("[TEST] THSAPI_BuyStock Error [%d]\n", iBuyRet);
					}
				}
			}
			printf("[TEST] THSAPI_BuyStock = %d\n", iBuyRetFirst);
		}

		////////////////////////////////////////////////////////////////////////// -> test THSAPI_SellStock
		{
			int iTestTimes_Sell = 0;
			int iSellRetFirst = -1;
			for (int i=0; i<iTestTimes_Sell; i++)
			{
				if (0 == i)
				{
					iSellRetFirst = THSAPI_SellStock("601988",100,4.01f);
				}
				else
				{
					int iSellRet = THSAPI_SellStock("601988",100,4.01f);
					if (iSellRetFirst != iSellRet)
					{
						printf("[TEST] THSAPI_SellStock Error [%d]\n", iSellRet);
					}
				}
			}
			printf("[TEST] THSAPI_SellStock = %d\n", iSellRetFirst);
		}

		////////////////////////////////////////////////////////////////////////// -> test THSAPI_GetHoldStockList
		{
			int iTestTimes_HoldStock = 1;
			int iHoldStockRetFirst = -1;
			std::list<HoldStock> cResultList;
			for (int i=0; i<iTestTimes_HoldStock; i++)
			{
				if (0 == i)
				{
					iHoldStockRetFirst = THSAPI_GetHoldStockList(cResultList);
				}
				else
				{
					int iHoldStockRet = THSAPI_GetHoldStockList(cResultList);
					if (iHoldStockRetFirst != iHoldStockRet)
					{
						printf("[TEST] THSAPI_GetHoldStockList Error [%d]\n", iHoldStockRet);
					}
				}
			}
			printf("[TEST] THSAPI_GetHoldStockList list size %d\n", cResultList.size());
			std::list<HoldStock>::iterator it;
			for (it = cResultList.begin(); it != cResultList.end(); it++)
			{
				HoldStock cHoldStock = *it;
				printf("    {%s %d %d %.3f %.3f %.3f}\n",
					cHoldStock.stockID.c_str(),
					cHoldStock.totalAmount,
					cHoldStock.availableAmount,
					cHoldStock.refProfitLoss,
					cHoldStock.refPrimeCostPrice,
					cHoldStock.curPrice);
			}
		}

		////////////////////////////////////////////////////////////////////////// -> test THSAPI_GetCommissionOrderList
		{
			int iTestTimes_CommissionOrder = 1;
			int iCommissionOrderRetFirst = -1;
			std::list<CommissionOrder> cResultList;
			for (int i=0; i<iTestTimes_CommissionOrder; i++)
			{
				if (0 == i)
				{
					iCommissionOrderRetFirst = THSAPI_GetCommissionOrderList(cResultList);
				}
				else
				{
					int iCommissionOrderRet = THSAPI_GetCommissionOrderList(cResultList);
					if (iCommissionOrderRetFirst != iCommissionOrderRet)
					{
						printf("[TEST] THSAPI_GetCommissionOrderList Error [%d]\n", iCommissionOrderRet);
					}
				}
			}
			printf("[TEST] THSAPI_GetCommissionOrderList list size %d\n", cResultList.size());
			std::list<CommissionOrder>::iterator it;
			for (it = cResultList.begin(); it != cResultList.end(); it++)
			{
				CommissionOrder cCommissionOrder = *it;
				printf("    {%s %s %d %d %.3f %d %.3f}\n",
					cCommissionOrder.time.c_str(),
					cCommissionOrder.stockID.c_str(),
					cCommissionOrder.tranAct,
					cCommissionOrder.commissionAmount,
					cCommissionOrder.commissionPrice,
					cCommissionOrder.dealAmount,
					cCommissionOrder.dealPrice);
			}
		}

		////////////////////////////////////////////////////////////////////////// -> test THSAPI_GetDealOrderList
		{
			int iTestTimes_DealOrder = 1;
			int iDealOrderRetFirst = false;
			std::list<DealOrder> cResultList;
			for (int i=0; i<iTestTimes_DealOrder; i++)
			{
				if (0 == i)
				{
					iDealOrderRetFirst = THSAPI_GetDealOrderList(cResultList);
				}
				else
				{
					int iDealOrderRet = THSAPI_GetDealOrderList(cResultList);
					if (iDealOrderRetFirst != iDealOrderRet)
					{
						printf("[TEST] THSAPI_GetDealOrderList Error [%d]\n", iDealOrderRet);
					}
				}
			}
			printf("[TEST] THSAPI_GetDealOrderList list size %d\n", cResultList.size());
			std::list<DealOrder>::iterator it;
			for (it = cResultList.begin(); it != cResultList.end(); it++)
			{
				DealOrder cDealOrder = *it;
				printf("    {%s %s %d %d %.3f}\n",
					cDealOrder.time.c_str(),
					cDealOrder.stockID.c_str(),
					cDealOrder.tranAct,
					cDealOrder.dealAmount,
					cDealOrder.dealPrice);
			}
		}

	}
	//////////////////////////////////////////////////////////////////////////
	printf("### Main End\n");

	system("pause");

	return 0;
}
