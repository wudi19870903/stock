#include "stdafx.h"

#include "stormstock_ori_capi_CATHSAccount.h"

#include "../TongHuaShun.h"
#include <string>
using namespace std;

static string jstringTostring(JNIEnv* env, jstring jstr)
{
	string rtnString;
	char* rtn = NULL;
	jclass clsstring = env->FindClass("java/lang/String");
	jstring strencode = env->NewStringUTF("utf-8");
	jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
	jbyteArray barr= (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);
	jsize alen = env->GetArrayLength(barr);
	jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
	if (alen > 0)
	{
		rtn = new char[alen + 1];
		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
		rtnString = string(rtn);
		delete [] rtn;
		rtn = NULL;
	}
	env->ReleaseByteArrayElements(barr, ba, 0);

	return rtnString;
}

/*
* Class:     stormstock_ori_capi_CATHSAccount
* Method:    initialize
* Signature: ()I
*/
JNIEXPORT jint JNICALL Java_stormstock_ori_capi_CATHSAccount_initialize
(JNIEnv *, jclass)
{
	//DFileLog::GetInstance()->Clear();
	DFileLog::GetInstance()->EnableSaveLog(false);

	int err = 0;
	err = THSAPI_TongHuaShunInit();
	return err;
}

/*
* Class:     stormstock_ori_capi_CATHSAccount
* Method:    getAvailableMoney
* Signature: ()Lstormstock/ori/capi/CATHSAccount/ResultAvailableMoney;
*/
JNIEXPORT jobject JNICALL Java_stormstock_ori_capi_CATHSAccount_getAvailableMoney
(JNIEnv * env, jclass)
{
	TESTLOG("Java_stormstock_ori_capi_CATHSAccount_getAvailableMoney\n");

	int err = 0;
	float availableMoney = 0.0f;
	err = THSAPI_GetAvailableMoney(availableMoney);
	TESTLOG("   THSAPI_GetAvailableMoney err(%d) AvailableMoney(%f)\n",err,availableMoney);

	jclass jcls_ResultAvailableMoney = env->FindClass("stormstock/ori/capi/CATHSAccount$ResultAvailableMoney");
	if (NULL == jcls_ResultAvailableMoney)
	{
		TESTLOG("   jcls_ResultAvailableMoney ERROR\n");
	}
	
	jmethodID mid_ResultAvailableMoney_init = env->GetMethodID(jcls_ResultAvailableMoney, "<init>", "()V");
	if (NULL == mid_ResultAvailableMoney_init)
	{
		TESTLOG("   mid_ResultAvailableMoney_init ERROR\n");
	}

	jobject jobj_ResultAvailableMoney = env->NewObject(jcls_ResultAvailableMoney, mid_ResultAvailableMoney_init);
	if (NULL == jobj_ResultAvailableMoney)
	{
		TESTLOG("   jobj_ResultAvailableMoney ERROR\n");
	}

	jfieldID fid_error = env->GetFieldID(jcls_ResultAvailableMoney, "error", "I");  
	if (NULL == fid_error)
	{
		TESTLOG("   fid_error ERROR\n");
	}
	env->SetIntField(jobj_ResultAvailableMoney, fid_error, (int)err); 


	jfieldID fid_availableMoney = env->GetFieldID(jcls_ResultAvailableMoney, "availableMoney", "F");  
	if (NULL == fid_availableMoney)
	{
		TESTLOG("   fid_availableMoney ERROR\n");
	}
	env->SetFloatField(jobj_ResultAvailableMoney, fid_availableMoney, (float)availableMoney); 

	return jobj_ResultAvailableMoney;
}

/*
* Class:     stormstock_ori_capi_CATHSAccount
* Method:    getTotalAssets
* Signature: ()Lstormstock/ori/capi/CATHSAccount/ResultTotalAssets;
*/
JNIEXPORT jobject JNICALL Java_stormstock_ori_capi_CATHSAccount_getTotalAssets
(JNIEnv * env, jclass)
{
	TESTLOG("Java_stormstock_ori_capi_CATHSAccount_getTotalAssets\n");

	int err = 0;
	float totalAssets = 0.0f;
	err = THSAPI_GetTotalAssets(totalAssets);

	TESTLOG("   THSAPI_GetTotalAssets err(%d) TotalAssets(%f)\n",err,totalAssets);

	jclass jcls_ResultTotalAssets = env->FindClass("stormstock/ori/capi/CATHSAccount$ResultTotalAssets");
	if (NULL == jcls_ResultTotalAssets)
	{
		TESTLOG("   jcls_ResultTotalAssets ERROR\n");
	}

	jmethodID mid_ResultTotalAssets_init = env->GetMethodID(jcls_ResultTotalAssets, "<init>", "()V");
	if (NULL == mid_ResultTotalAssets_init)
	{
		TESTLOG("   mid_ResultTotalAssets_init ERROR\n");
	}

	jobject jobj_ResultTotalAssets = env->NewObject(jcls_ResultTotalAssets, mid_ResultTotalAssets_init);
	if (NULL == jobj_ResultTotalAssets)
	{
		TESTLOG("   jobj_ResultTotalAssets ERROR\n");
	}

	jfieldID fid_error = env->GetFieldID(jcls_ResultTotalAssets, "error", "I");  
	if (NULL == fid_error)
	{
		TESTLOG("   fid_error ERROR\n");
	}
	env->SetIntField(jobj_ResultTotalAssets, fid_error, (int)err); 


	jfieldID fid_totalAssets = env->GetFieldID(jcls_ResultTotalAssets, "totalAssets", "F");  
	if (NULL == fid_totalAssets)
	{
		TESTLOG("   fid_totalAssets ERROR\n");
	}
	env->SetFloatField(jobj_ResultTotalAssets, fid_totalAssets, (float)totalAssets); 

	return jobj_ResultTotalAssets;
}

/*
* Class:     stormstock_ori_capi_CATHSAccount
* Method:    getAllStockMarketValue
* Signature: ()Lstormstock/ori/capi/CATHSAccount/ResultAllStockMarketValue;
*/
JNIEXPORT jobject JNICALL Java_stormstock_ori_capi_CATHSAccount_getAllStockMarketValue
(JNIEnv * env, jclass)
{
	TESTLOG("Java_stormstock_ori_capi_CATHSAccount_getAllStockMarketValue\n");

	int err = 0;
	float allStockMarketValue = 0.0f;
	err = THSAPI_GetAllStockMarketValue(allStockMarketValue);

	TESTLOG("   THSAPI_GetAllStockMarketValue err(%d) AllStockMarketValue(%f)\n",err,allStockMarketValue);

	jclass jcls_ResultAllStockMarketValue = env->FindClass("stormstock/ori/capi/CATHSAccount$ResultAllStockMarketValue");
	if (NULL == jcls_ResultAllStockMarketValue)
	{
		TESTLOG("   jcls_ResultAllStockMarketValue ERROR\n");
	}

	jmethodID mid_ResultAllStockMarketValue_init = env->GetMethodID(jcls_ResultAllStockMarketValue, "<init>", "()V");
	if (NULL == mid_ResultAllStockMarketValue_init)
	{
		TESTLOG("   mid_ResultAllStockMarketValue_init ERROR\n");
	}

	jobject jobj_ResultAllStockMarketValue = env->NewObject(jcls_ResultAllStockMarketValue, mid_ResultAllStockMarketValue_init);
	if (NULL == jobj_ResultAllStockMarketValue)
	{
		TESTLOG("   jobj_ResultAllStockMarketValue ERROR\n");
	}

	jfieldID fid_error = env->GetFieldID(jcls_ResultAllStockMarketValue, "error", "I");  
	if (NULL == fid_error)
	{
		TESTLOG("   fid_error ERROR\n");
	}
	env->SetIntField(jobj_ResultAllStockMarketValue, fid_error, (int)err); 


	jfieldID fid_allStockMarketValue = env->GetFieldID(jcls_ResultAllStockMarketValue, "allStockMarketValue", "F");  
	if (NULL == fid_allStockMarketValue)
	{
		TESTLOG("   fid_allStockMarketValue ERROR\n");
	}
	env->SetFloatField(jobj_ResultAllStockMarketValue, fid_allStockMarketValue, (float)allStockMarketValue); 

	return jobj_ResultAllStockMarketValue;
}


/*
* Class:     stormstock_ori_capi_CATHSAccount
* Method:    getHoldStockList
* Signature: ()Lstormstock/ori/capi/CATHSAccount/ResultHoldStockList;
*/
JNIEXPORT jobject JNICALL Java_stormstock_ori_capi_CATHSAccount_getHoldStockList
(JNIEnv * env, jclass)
{
	TESTLOG("Java_stormstock_ori_capi_CATHSAccount_getHoldStockList\n");

	int err = 0;
	std::list<HoldStock> cResultList;
	err = THSAPI_GetHoldStockList(cResultList);

	TESTLOG("   THSAPI_GetHoldStockList err(%d) cResultList size(%d)\n",err,cResultList.size());

	// 构建对象

	jclass jcls_ResultHoldStockList = env->FindClass("stormstock/ori/capi/CATHSAccount$ResultHoldStockList");
	if (NULL == jcls_ResultHoldStockList)
	{
		TESTLOG("   jcls_ResultHoldStockList ERROR\n");
	}

	jmethodID mid_ResultHoldStockList_init = env->GetMethodID(jcls_ResultHoldStockList, "<init>", "()V");
	if (NULL == mid_ResultHoldStockList_init)
	{
		TESTLOG("   mid_ResultHoldStockList_init ERROR\n");
	}

	jobject jobj_ResultHoldStockList = env->NewObject(jcls_ResultHoldStockList, mid_ResultHoldStockList_init);
	if (NULL == jobj_ResultHoldStockList)
	{
		TESTLOG("   jobj_ResultHoldStockList ERROR\n");
	}

	// 填充

	jfieldID fid_error = env->GetFieldID(jcls_ResultHoldStockList, "error", "I");  
	if (NULL == fid_error)
	{
		TESTLOG("   fid_error ERROR\n");
	}
	env->SetIntField(jobj_ResultHoldStockList, fid_error, (int)err); 

	jclass jcls_HoldStock = env->FindClass("stormstock/ori/capi/CATHSAccount$HoldStock");
	if (NULL == jcls_HoldStock)
	{
		TESTLOG("   jcls_HoldStock ERROR\n");
	}
	jmethodID mid_HoldStock_init = env->GetMethodID(jcls_HoldStock, "<init>", "()V");
	if (NULL == mid_HoldStock_init)
	{
		TESTLOG("   mid_HoldStock_init ERROR\n");
	}
	jclass jcls_ArrayList = env->FindClass("Ljava/util/ArrayList;");
	if (NULL == jcls_ArrayList)
	{
		TESTLOG("   jcls_ArrayList ERROR\n");
	}
	jmethodID mid_ArrayList_init = env->GetMethodID(jcls_ArrayList, "<init>", "()V");
	if (NULL == mid_ArrayList_init)
	{
		TESTLOG("   mid_ArrayList_init ERROR\n");
	}
	jobject jobj_ArrayList = env->NewObject(jcls_ArrayList, mid_ArrayList_init);
	if (NULL == jobj_ArrayList)
	{
		TESTLOG("   jobj_ArrayList ERROR\n");
	}
	jmethodID mid_ArrayList_add = env->GetMethodID(jcls_ArrayList, "add", "(Ljava/lang/Object;)Z");
	if (NULL == mid_ArrayList_add)
	{
		TESTLOG("   mid_ArrayList_add ERROR\n");
	}
	std::list<HoldStock>::iterator it;
	for (it = cResultList.begin(); it != cResultList.end(); it++)
	{
		HoldStock cHoldStock = *it;
		jobject jobj_HoldStock = env->NewObject(jcls_HoldStock, mid_HoldStock_init);
		if (NULL == jobj_HoldStock)
		{
			TESTLOG("   jobj_HoldStock ERROR\n");
		}

		jfieldID fid_stockID = env->GetFieldID(jcls_HoldStock, "stockID", "Ljava/lang/String;");
		if (NULL == fid_stockID)
		{
			TESTLOG("   fid_stockID ERROR\n");
		}
		jstring jstr_stockID = env->NewStringUTF(cHoldStock.stockID.c_str());
		env->SetObjectField(jobj_HoldStock, fid_stockID, jstr_stockID);

		jfieldID fid_totalAmount = env->GetFieldID(jcls_HoldStock, "totalAmount", "I");
		if (NULL == fid_totalAmount)
		{
			TESTLOG("   fid_totalAmount ERROR\n");
		}
		jint jint_totalAmount = cHoldStock.totalAmount;
		env->SetIntField(jobj_HoldStock, fid_totalAmount, jint_totalAmount);

		jfieldID fid_availableAmount = env->GetFieldID(jcls_HoldStock, "availableAmount", "I");
		if (NULL == fid_availableAmount)
		{
			TESTLOG("   fid_availableAmount ERROR\n");
		}
		jint jint_availableAmount = cHoldStock.availableAmount;
		env->SetIntField(jobj_HoldStock, fid_availableAmount, jint_availableAmount);

		jfieldID fid_refProfitLoss = env->GetFieldID(jcls_HoldStock, "refProfitLoss", "F");
		if (NULL == fid_refProfitLoss)
		{
			TESTLOG("   fid_refProfitLoss ERROR\n");
		}
		jfloat jfloat_refProfitLoss = cHoldStock.refProfitLoss;
		env->SetFloatField(jobj_HoldStock, fid_refProfitLoss, jfloat_refProfitLoss);

		jfieldID fid_refPrimeCostPrice = env->GetFieldID(jcls_HoldStock, "refPrimeCostPrice", "F");
		if (NULL == fid_refPrimeCostPrice)
		{
			TESTLOG("   fid_refPrimeCostPrice ERROR\n");
		}
		jfloat jfloat_refPrimeCostPrice = cHoldStock.refPrimeCostPrice;
		env->SetFloatField(jobj_HoldStock, fid_refPrimeCostPrice, jfloat_refPrimeCostPrice);

		jfieldID fid_curPrice = env->GetFieldID(jcls_HoldStock, "curPrice", "F");
		if (NULL == fid_curPrice)
		{
			TESTLOG("   fid_curPrice ERROR\n");
		}
		jfloat jfloat_curPrice = cHoldStock.curPrice;
		env->SetFloatField(jobj_HoldStock, fid_curPrice, jfloat_curPrice);

		env->CallBooleanMethod(jobj_ArrayList, mid_ArrayList_add, jobj_HoldStock);
	}
	jfieldID fid_resultList = env->GetFieldID(jcls_ResultHoldStockList, "resultList", "Ljava/util/List;");  
	if (NULL == fid_resultList)
	{
		TESTLOG("   fid_resultList ERROR\n");
	}
	env->SetObjectField(jobj_ResultHoldStockList, fid_resultList, jobj_ArrayList);

	return jobj_ResultHoldStockList;
}

/*
* Class:     stormstock_ori_capi_CATHSAccount
* Method:    getCommissionOrderList
* Signature: ()Lstormstock/ori/capi/CATHSAccount/ResultCommissionOrderList;
*/
JNIEXPORT jobject JNICALL Java_stormstock_ori_capi_CATHSAccount_getCommissionOrderList
(JNIEnv * env, jclass)
{
	TESTLOG("Java_stormstock_ori_capi_CATHSAccount_getCommissionOrderList\n");

	int err = 0;
	std::list<CommissionOrder> cResultList;
	err = THSAPI_GetCommissionOrderList(cResultList);

	TESTLOG("   THSAPI_GetCommissionOrderList err(%d) cResultList size(%d)\n",err,cResultList.size());

	// 构建对象

	jclass jcls_ResultCommissionOrderList = env->FindClass("stormstock/ori/capi/CATHSAccount$ResultCommissionOrderList");
	if (NULL == jcls_ResultCommissionOrderList)
	{
		TESTLOG("   jcls_ResultCommissionOrderList ERROR\n");
	}

	jmethodID mid_ResultCommissionOrderList_init = env->GetMethodID(jcls_ResultCommissionOrderList, "<init>", "()V");
	if (NULL == mid_ResultCommissionOrderList_init)
	{
		TESTLOG("   mid_ResultCommissionOrderList_init ERROR\n");
	}

	jobject jobj_ResultCommissionOrderList = env->NewObject(jcls_ResultCommissionOrderList, mid_ResultCommissionOrderList_init);
	if (NULL == jobj_ResultCommissionOrderList)
	{
		TESTLOG("   jobj_ResultCommissionOrderList ERROR\n");
	}

	// 填充

	jfieldID fid_error = env->GetFieldID(jcls_ResultCommissionOrderList, "error", "I");  
	if (NULL == fid_error)
	{
		TESTLOG("   fid_error ERROR\n");
	}
	env->SetIntField(jobj_ResultCommissionOrderList, fid_error, (int)err); 

	jclass jcls_CommissionOrder = env->FindClass("stormstock/ori/capi/CATHSAccount$CommissionOrder");
	if (NULL == jcls_CommissionOrder)
	{
		TESTLOG("   jcls_CommissionOrder ERROR\n");
	}
	jmethodID mid_CommissionOrder_init = env->GetMethodID(jcls_CommissionOrder, "<init>", "()V");
	if (NULL == mid_CommissionOrder_init)
	{
		TESTLOG("   mid_CommissionOrder_init ERROR\n");
	}
	jclass jcls_ArrayList = env->FindClass("Ljava/util/ArrayList;");
	if (NULL == jcls_ArrayList)
	{
		TESTLOG("   jcls_ArrayList ERROR\n");
	}
	jmethodID mid_ArrayList_init = env->GetMethodID(jcls_ArrayList, "<init>", "()V");
	if (NULL == mid_ArrayList_init)
	{
		TESTLOG("   mid_ArrayList_init ERROR\n");
	}
	jobject jobj_ArrayList = env->NewObject(jcls_ArrayList, mid_ArrayList_init);
	if (NULL == jobj_ArrayList)
	{
		TESTLOG("   jobj_ArrayList ERROR\n");
	}
	jmethodID mid_ArrayList_add = env->GetMethodID(jcls_ArrayList, "add", "(Ljava/lang/Object;)Z");
	if (NULL == mid_ArrayList_add)
	{
		TESTLOG("   mid_ArrayList_add ERROR\n");
	}
	std::list<CommissionOrder>::iterator it;
	for (it = cResultList.begin(); it != cResultList.end(); it++)
	{
		CommissionOrder cCommissionOrder = *it;
		jobject jobj_CommissionOrder = env->NewObject(jcls_CommissionOrder, mid_CommissionOrder_init);
		if (NULL == jobj_CommissionOrder)
		{
			TESTLOG("   jobj_CommissionOrder ERROR\n");
		}

		jfieldID fid_time = env->GetFieldID(jcls_CommissionOrder, "time", "Ljava/lang/String;");
		if (NULL == fid_time)
		{
			TESTLOG("   fid_time ERROR\n");
		}
		jstring jstr_time = env->NewStringUTF(cCommissionOrder.time.c_str());
		env->SetObjectField(jobj_CommissionOrder, fid_time, jstr_time);

		jfieldID fid_stockID = env->GetFieldID(jcls_CommissionOrder, "stockID", "Ljava/lang/String;");
		if (NULL == fid_stockID)
		{
			TESTLOG("   fid_stockID ERROR\n");
		}
		jstring jstr_stockID = env->NewStringUTF(cCommissionOrder.stockID.c_str());
		env->SetObjectField(jobj_CommissionOrder, fid_stockID, jstr_stockID);

		jfieldID fid_tranAct = env->GetFieldID(jcls_CommissionOrder, "tranAct", "Lstormstock/ori/capi/CATHSAccount$TRANACT;");
		if (NULL == fid_tranAct)
		{
			TESTLOG("   fid_tranAct ERROR\n");
		}
		jclass jcls_TRANACT = env->FindClass("stormstock/ori/capi/CATHSAccount$TRANACT");
		jfieldID fid_BUY = env->GetStaticFieldID(jcls_TRANACT, "BUY", "Lstormstock/ori/capi/CATHSAccount$TRANACT;");
		jfieldID fid_SELL = env->GetStaticFieldID(jcls_TRANACT, "SELL", "Lstormstock/ori/capi/CATHSAccount$TRANACT;");
		if (NULL == fid_BUY || NULL == fid_SELL)
		{
			TESTLOG("   fid_BUY fid_SELL ERROR\n");
		}
		jobject jobj_BUY = env->GetStaticObjectField(jcls_TRANACT, fid_BUY);
		jobject jobj_SELL = env->GetStaticObjectField(jcls_TRANACT, fid_SELL);
		if (NULL == jobj_BUY || NULL == jobj_SELL)
		{
			TESTLOG("   jobj_BUY jobj_SELL ERROR\n");
		}
		jobject jobj_tranAct = NULL;
		if (TRANACT_BUY == cCommissionOrder.tranAct)
		{
			jobj_tranAct = jobj_BUY;
		}
		else if (TRANACT_SELL == cCommissionOrder.tranAct)
		{
			jobj_tranAct = jobj_SELL;
		}
		env->SetObjectField(jobj_CommissionOrder, fid_tranAct, jobj_tranAct);

		jfieldID fid_commissionAmount = env->GetFieldID(jcls_CommissionOrder, "commissionAmount", "I");
		if (NULL == fid_commissionAmount)
		{
			TESTLOG("   fid_commissionAmount ERROR\n");
		}
		jint jint_commissionAmount = cCommissionOrder.commissionAmount;
		env->SetIntField(jobj_CommissionOrder, fid_commissionAmount, jint_commissionAmount);

		jfieldID fid_commissionPrice = env->GetFieldID(jcls_CommissionOrder, "commissionPrice", "F");
		if (NULL == fid_commissionPrice)
		{
			TESTLOG("   fid_commissionPrice ERROR\n");
		}
		jfloat jfloat_commissionPrice = cCommissionOrder.commissionPrice;
		env->SetFloatField(jobj_CommissionOrder, fid_commissionPrice, jfloat_commissionPrice);

		jfieldID fid_dealAmount = env->GetFieldID(jcls_CommissionOrder, "dealAmount", "I");
		if (NULL == fid_dealAmount)
		{
			TESTLOG("   fid_dealAmount ERROR\n");
		}
		jint jint_dealAmount = cCommissionOrder.dealAmount;
		env->SetIntField(jobj_CommissionOrder, fid_dealAmount, jint_dealAmount);

		jfieldID fid_dealPrice = env->GetFieldID(jcls_CommissionOrder, "dealPrice", "F");
		if (NULL == fid_dealPrice)
		{
			TESTLOG("   fid_dealPrice ERROR\n");
		}
		jfloat jfloat_dealPrice = cCommissionOrder.dealPrice;
		env->SetFloatField(jobj_CommissionOrder, fid_dealPrice, jfloat_dealPrice);

		env->CallBooleanMethod(jobj_ArrayList, mid_ArrayList_add, jobj_CommissionOrder);
	}
	jfieldID fid_resultList = env->GetFieldID(jcls_ResultCommissionOrderList, "resultList", "Ljava/util/List;");
	if (NULL == fid_resultList)
	{
		TESTLOG("   fid_resultList ERROR\n");
	}
	env->SetObjectField(jobj_ResultCommissionOrderList, fid_resultList, jobj_ArrayList);

	return jobj_ResultCommissionOrderList;
}


/*
* Class:     stormstock_ori_capi_CATHSAccount
* Method:    getDealOrderList
* Signature: ()Lstormstock/ori/capi/CATHSAccount/ResultDealOrderList;
*/
JNIEXPORT jobject JNICALL Java_stormstock_ori_capi_CATHSAccount_getDealOrderList
(JNIEnv * env, jclass)
{
	TESTLOG("Java_stormstock_ori_capi_CATHSAccount_getCommissionOrderList\n");

	int err = 0;
	std::list<DealOrder> cResultList;
	err = THSAPI_GetDealOrderList(cResultList);

	TESTLOG("   THSAPI_GetDealOrderList err(%d) cResultList size(%d)\n",err,cResultList.size());

	// 构建对象

	jclass jcls_ResultDealOrderList = env->FindClass("stormstock/ori/capi/CATHSAccount$ResultDealOrderList");
	if (NULL == jcls_ResultDealOrderList)
	{
		TESTLOG("   jcls_ResultDealOrderList ERROR\n");
	}

	jmethodID mid_ResultDealOrderList_init = env->GetMethodID(jcls_ResultDealOrderList, "<init>", "()V");
	if (NULL == mid_ResultDealOrderList_init)
	{
		TESTLOG("   mid_ResultDealOrderList_init ERROR\n");
	}

	jobject jobj_ResultDealOrderList = env->NewObject(jcls_ResultDealOrderList, mid_ResultDealOrderList_init);
	if (NULL == jobj_ResultDealOrderList)
	{
		TESTLOG("   jobj_ResultDealOrderList ERROR\n");
	}

	// 填充

	jfieldID fid_error = env->GetFieldID(jcls_ResultDealOrderList, "error", "I");  
	if (NULL == fid_error)
	{
		TESTLOG("   fid_error ERROR\n");
	}
	env->SetIntField(jobj_ResultDealOrderList, fid_error, (int)err); 

	jclass jcls_DealOrder = env->FindClass("stormstock/ori/capi/CATHSAccount$DealOrder");
	if (NULL == jcls_DealOrder)
	{
		TESTLOG("   jcls_DealOrder ERROR\n");
	}
	jmethodID mid_DealOrder_init = env->GetMethodID(jcls_DealOrder, "<init>", "()V");
	if (NULL == mid_DealOrder_init)
	{
		TESTLOG("   mid_DealOrder_init ERROR\n");
	}
	jclass jcls_ArrayList = env->FindClass("Ljava/util/ArrayList;");
	if (NULL == jcls_ArrayList)
	{
		TESTLOG("   jcls_ArrayList ERROR\n");
	}
	jmethodID mid_ArrayList_init = env->GetMethodID(jcls_ArrayList, "<init>", "()V");
	if (NULL == mid_ArrayList_init)
	{
		TESTLOG("   mid_ArrayList_init ERROR\n");
	}
	jobject jobj_ArrayList = env->NewObject(jcls_ArrayList, mid_ArrayList_init);
	if (NULL == jobj_ArrayList)
	{
		TESTLOG("   jobj_ArrayList ERROR\n");
	}
	jmethodID mid_ArrayList_add = env->GetMethodID(jcls_ArrayList, "add", "(Ljava/lang/Object;)Z");
	if (NULL == mid_ArrayList_add)
	{
		TESTLOG("   mid_ArrayList_add ERROR\n");
	}
	std::list<DealOrder>::iterator it;
	for (it = cResultList.begin(); it != cResultList.end(); it++)
	{
		DealOrder cDealOrder = *it;
		jobject jobj_DealOrder = env->NewObject(jcls_DealOrder, mid_DealOrder_init);
		if (NULL == jobj_DealOrder)
		{
			TESTLOG("   jobj_DealOrder ERROR\n");
		}

		jfieldID fid_time = env->GetFieldID(jcls_DealOrder, "time", "Ljava/lang/String;");
		if (NULL == fid_time)
		{
			TESTLOG("   fid_time ERROR\n");
		}
		jstring jstr_time = env->NewStringUTF(cDealOrder.time.c_str());
		env->SetObjectField(jobj_DealOrder, fid_time, jstr_time);

		jfieldID fid_stockID = env->GetFieldID(jcls_DealOrder, "stockID", "Ljava/lang/String;");
		if (NULL == fid_stockID)
		{
			TESTLOG("   fid_stockID ERROR\n");
		}
		jstring jstr_stockID = env->NewStringUTF(cDealOrder.stockID.c_str());
		env->SetObjectField(jobj_DealOrder, fid_stockID, jstr_stockID);

		jfieldID fid_tranAct = env->GetFieldID(jcls_DealOrder, "tranAct", "Lstormstock/ori/capi/CATHSAccount$TRANACT;");
		if (NULL == fid_tranAct)
		{
			TESTLOG("   fid_tranAct ERROR\n");
		}
		jclass jcls_TRANACT = env->FindClass("stormstock/ori/capi/CATHSAccount$TRANACT");
		jfieldID fid_BUY = env->GetStaticFieldID(jcls_TRANACT, "BUY", "Lstormstock/ori/capi/CATHSAccount$TRANACT;");
		jfieldID fid_SELL = env->GetStaticFieldID(jcls_TRANACT, "SELL", "Lstormstock/ori/capi/CATHSAccount$TRANACT;");
		if (NULL == fid_BUY || NULL == fid_SELL)
		{
			TESTLOG("   fid_BUY fid_SELL ERROR\n");
		}
		jobject jobj_BUY = env->GetStaticObjectField(jcls_TRANACT, fid_BUY);
		jobject jobj_SELL = env->GetStaticObjectField(jcls_TRANACT, fid_SELL);
		if (NULL == jobj_BUY || NULL == jobj_SELL)
		{
			TESTLOG("   jobj_BUY jobj_SELL ERROR\n");
		}
		jobject jobj_tranAct = NULL;
		if (TRANACT_BUY == cDealOrder.tranAct)
		{
			jobj_tranAct = jobj_BUY;
		}
		else if (TRANACT_SELL == cDealOrder.tranAct)
		{
			jobj_tranAct = jobj_SELL;
		}
		env->SetObjectField(jobj_DealOrder, fid_tranAct, jobj_tranAct);

		jfieldID fid_dealAmount = env->GetFieldID(jcls_DealOrder, "dealAmount", "I");
		if (NULL == fid_dealAmount)
		{
			TESTLOG("   fid_dealAmount ERROR\n");
		}
		jint jint_dealAmount = cDealOrder.dealAmount;
		env->SetIntField(jobj_DealOrder, fid_dealAmount, jint_dealAmount);

		jfieldID fid_dealPrice = env->GetFieldID(jcls_DealOrder, "dealPrice", "F");
		if (NULL == fid_dealPrice)
		{
			TESTLOG("   fid_dealPrice ERROR\n");
		}
		jfloat jfloat_dealPrice = cDealOrder.dealPrice;
		env->SetFloatField(jobj_DealOrder, fid_dealPrice, jfloat_dealPrice);

		env->CallBooleanMethod(jobj_ArrayList, mid_ArrayList_add, jobj_DealOrder);
	}
	jfieldID fid_resultList = env->GetFieldID(jcls_ResultDealOrderList, "resultList", "Ljava/util/List;");
	if (NULL == fid_resultList)
	{
		TESTLOG("   fid_resultList ERROR\n");
	}
	env->SetObjectField(jobj_ResultDealOrderList, fid_resultList, jobj_ArrayList);

	return jobj_ResultDealOrderList;
}


/*
* Class:     stormstock_ori_capi_CATHSAccount
* Method:    buyStock
* Signature: (Ljava/lang/String;IF)I
*/
JNIEXPORT jint JNICALL Java_stormstock_ori_capi_CATHSAccount_buyStock
(JNIEnv * env, jclass, jstring stockId, jint amount, jfloat price)
{
	int rtn = 0;
	string sStockId = jstringTostring(env, stockId);
	int iBuyAmount = amount;
	float fPrice = price;
	rtn = THSAPI_BuyStock(sStockId.c_str(), iBuyAmount, fPrice);
	return rtn;
}


/*
* Class:     stormstock_ori_capi_CATHSAccount
* Method:    sellStock
* Signature: (Ljava/lang/String;IF)I
*/
JNIEXPORT jint JNICALL Java_stormstock_ori_capi_CATHSAccount_sellStock
(JNIEnv * env, jclass, jstring stockId, jint amount, jfloat price)
{
	int rtn = 0;
	string sStockId = jstringTostring(env, stockId);
	int iSellAmount = amount;
	float fPrice = price;
	rtn = THSAPI_SellStock(sStockId.c_str(), iSellAmount, fPrice);
	return rtn;
}