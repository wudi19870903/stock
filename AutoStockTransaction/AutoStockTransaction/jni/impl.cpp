#include "../stdafx.h"

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
	//DFileLog::GetInstance()->EnableSaveLog(true);

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
(JNIEnv *, jclass)
{
	jobject jobj;
	return jobj;
}

/*
* Class:     stormstock_ori_capi_CATHSAccount
* Method:    getCommissionOrderList
* Signature: ()Lstormstock/ori/capi/CATHSAccount/ResultCommissionOrderList;
*/
JNIEXPORT jobject JNICALL Java_stormstock_ori_capi_CATHSAccount_getCommissionOrderList
(JNIEnv *, jclass)
{
	jobject jobj;
	return jobj;
}


/*
* Class:     stormstock_ori_capi_CATHSAccount
* Method:    getDealOrderList
* Signature: ()Lstormstock/ori/capi/CATHSAccount/ResultDealOrderList;
*/
JNIEXPORT jobject JNICALL Java_stormstock_ori_capi_CATHSAccount_getDealOrderList
(JNIEnv *, jclass)
{
	jobject jobj;
	return jobj;
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