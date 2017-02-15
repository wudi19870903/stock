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
	// DFileLog::GetInstance()->Clear();
	DFileLog::GetInstance()->EnableSaveLog(false);

	printf("### Main Begin\n");

	int err = -1;
	////////////////////////////////////////////////////////////////////////// -> test THSAPI_TongHuaShunInit
	{
		for (int i=0; i<1; i++)
		{
			err = THSAPI_TongHuaShunInit();
			printf("[TEST] THSAPI_TongHuaShunInit Error[%d]\n", err);
		}
	}

	if (err >=0)
	{
		////////////////////////////////////////////////////////////////////////// -> test THSAPI_GetAvailableMoney
		for (int i=0; i<1; i++)
		{
			float fAvailableMoney = 0;
			err = THSAPI_GetAvailableMoney(fAvailableMoney);
			printf("[TEST] THSAPI_GetAvailableMoney Error[%d] AvailableMoney[%f]\n", err, fAvailableMoney);
		}

		////////////////////////////////////////////////////////////////////////// -> test THSAPI_GetTotalAssets
		for (int i=0; i<1; i++)
		{
			float fTotalAssetsFirst = 0;
			err = THSAPI_GetTotalAssets(fTotalAssetsFirst);
			printf("[TEST] THSAPI_GetTotalAssets Error[%d] TotalAssets[%f]\n", err, fTotalAssetsFirst);
		}

		////////////////////////////////////////////////////////////////////////// -> test THSAPI_GetAllStockMarketValue
		for (int i=0; i<1; i++)
		{
			float fStockMarketValue = 0;
			err = THSAPI_GetAllStockMarketValue(fStockMarketValue);
			printf("[TEST] THSAPI_GetStockMarketValue Error[%d] AllStockMarketValue[%f]\n", err, fStockMarketValue);
		}

		////////////////////////////////////////////////////////////////////////// -> test THSAPI_BuyStock
		for (int i=0; i<0; i++)
		{
			err = THSAPI_BuyStock("601988",100,1.00);
			printf("[TEST] THSAPI_BuyStock Error[%d]\n", err);
		}

		////////////////////////////////////////////////////////////////////////// -> test THSAPI_SellStock
		for (int i = 0; i<0; i++)
		{
			err = THSAPI_SellStock("601988", 100, 4.01f);
			printf("[TEST] THSAPI_SellStock Error[%d]\n", err);
		}

		////////////////////////////////////////////////////////////////////////// -> test THSAPI_GetHoldStockList
		{
			std::list<HoldStock> cResultList;
			for (int i=0; i<1; i++)
			{
				err = THSAPI_GetHoldStockList(cResultList);
				printf("[TEST] THSAPI_GetHoldStockList Error[%d]\n", err);
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
			std::list<CommissionOrder> cResultList;
			for (int i=0; i<1; i++)
			{
				err = THSAPI_GetCommissionOrderList(cResultList);
				printf("[TEST] THSAPI_GetCommissionOrderList Error[%d]\n", err);
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
			std::list<DealOrder> cResultList;
			for (int i=0; i<1; i++)
			{
				err = THSAPI_GetDealOrderList(cResultList);
				printf("[TEST] THSAPI_GetDealOrderList Error[%d]\n", err);
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
