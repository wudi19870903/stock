#include "stdafx.h"
#include "Common.h"
#include "TongHuaShun.h"
#include "WinHandle.h"

void Test()
{
	int iRetInit = THSAPI_TongHuaShunInit();
	

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
	//////////////////////////////////////////////////////////////////////////
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

		//////////////////////////////////////////////////////////////////////////
		{
			int iTestTimes_AvailableMoney = 1;
			float fAvailableMoneyFirst = -1;
			for (int i=0; i<iTestTimes_AvailableMoney; i++)
			{
				if (0 == i)
				{
					fAvailableMoneyFirst = THSAPI_GetAvailableMoney();
				}
				else
				{
					int fAvailableMoney = THSAPI_GetAvailableMoney();
					if (fAvailableMoneyFirst != fAvailableMoney)
					{
						printf("[TEST] THSAPI_GetAvailableMoney Error [%.2f]\n", fAvailableMoney);
					}
				}
			}
			printf("[TEST] THSAPI_GetAvailableMoney = %f\n", fAvailableMoneyFirst);
		}

		//////////////////////////////////////////////////////////////////////////
		{
			int iTestTimes_AllMoney = 1;
			float fAllMoneyFirst = -1;
			for (int i=0; i<iTestTimes_AllMoney; i++)
			{
				if (0 == i)
				{
					fAllMoneyFirst = THSAPI_GetTotalAssets();
				}
				else
				{
					float fAllMoney = THSAPI_GetTotalAssets();
					if (fAllMoneyFirst != fAllMoney)
					{
						printf("[TEST] THSAPI_GetTotalAssets Error [%.2f]\n", fAllMoney);
					}
				}
			}
			printf("[TEST] THSAPI_GetTotalAssets = %f\n", fAllMoneyFirst);
		}


		//////////////////////////////////////////////////////////////////////////
		{
			int iTestTimes_StockMarketValue = 1;
			float fStockMarketValueFirst = -1;
			for (int i=0; i<iTestTimes_StockMarketValue; i++)
			{
				if (0 == i)
				{
					fStockMarketValueFirst = THSAPI_GetAllStockMarketValue();
				}
				else
				{
					float fStockMarketValue = THSAPI_GetAllStockMarketValue();
					if (fStockMarketValueFirst != fStockMarketValue)
					{
						printf("[TEST] THSAPI_GetStockMarketValue Error [%.2f]\n", fStockMarketValue);
					}
				}
			}
			printf("[TEST] THSAPI_GetStockMarketValue = %f\n", fStockMarketValueFirst);
		}

		//////////////////////////////////////////////////////////////////////////
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

		//////////////////////////////////////////////////////////////////////////
		{
			int iTestTimes_Sell = 0;
			int iSellRetFirst = -1;
			for (int i=0; i<iTestTimes_Sell; i++)
			{
				if (0 == i)
				{
					iSellRetFirst = THSAPI_SellStock("601988",100,4.01);
				}
				else
				{
					int iSellRet = THSAPI_SellStock("601988",100,4.01);
					if (iSellRetFirst != iSellRet)
					{
						printf("[TEST] THSAPI_SellStock Error [%d]\n", iSellRet);
					}
				}
			}
			printf("[TEST] THSAPI_SellStock = %d\n", iSellRetFirst);
		}

		//////////////////////////////////////////////////////////////////////////
		{
			int iTestTimes_HoldStock = 1;
			bool bHoldStockRetFirst = false;
			std::list<HoldStock> cResultList;
			for (int i=0; i<iTestTimes_HoldStock; i++)
			{
				if (0 == i)
				{
					bHoldStockRetFirst = THSAPI_GetHoldStock(cResultList);
				}
				else
				{
					bool bHoldStockRet = THSAPI_GetHoldStock(cResultList);
					if (bHoldStockRetFirst != bHoldStockRet)
					{
						printf("[TEST] THSAPI_GetHoldStock Error [%d]\n", bHoldStockRet);
					}
				}
			}
			printf("[TEST] THSAPI_GetHoldStock list size %d\n", cResultList.size());
			std::list<HoldStock>::iterator it;
			for (it = cResultList.begin(); it != cResultList.end(); it++)
			{
				HoldStock cHoldStock = *it;
				printf("[%s %d %d %.3f %.3f]\n",
					cHoldStock.stockID.c_str(),
					cHoldStock.totalAmount,
					cHoldStock.availableAmount,
					cHoldStock.refPrimeCostPrice,
					cHoldStock.curPrice);
			}
		}



	}
	//////////////////////////////////////////////////////////////////////////
	printf("### Main End\n");

	system("pause");

	return 0;
}
