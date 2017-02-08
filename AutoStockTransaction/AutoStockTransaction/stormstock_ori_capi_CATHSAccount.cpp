#include "stdafx.h"
#include "stormstock_ori_capi_CATHSAccount.h"
#include "TongHuaShun.h"
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
* Class:     stormstock_capi_CATHSAccount
* Method:    initialize
* Signature: ()I
*/
JNIEXPORT jint JNICALL Java_stormstock_ori_capi_CATHSAccount_initialize
(JNIEnv *, jclass)
{
	int rtn = 0;
	rtn = THSAPI_TongHuaShunInit();
	return rtn;
}

/*
* Class:     stormstock_capi_CATHSAccount
* Method:    getAvailableMoney
* Signature: ()F
*/
JNIEXPORT jfloat JNICALL Java_stormstock_ori_capi_CATHSAccount_getAvailableMoney
(JNIEnv *, jclass)
{
	float availableMoney = 0.0f;
	availableMoney = THSAPI_GetAvailableMoney();
	return availableMoney;
}

/*
* Class:     stormstock_capi_CATHSAccount
* Method:    getAllMoney
* Signature: ()F
*/
JNIEXPORT jfloat JNICALL Java_stormstock_ori_capi_CATHSAccount_getAllMoney
(JNIEnv *, jclass)
{
	float allMoney = 0.0f;
	allMoney = THSAPI_GetTotalAssets();
	return allMoney;
}

/*
* Class:     stormstock_capi_CATHSAccount
* Method:    getAllStockMarketValue
* Signature: ()F
*/
JNIEXPORT jfloat JNICALL Java_stormstock_ori_capi_CATHSAccount_getAllStockMarketValue
(JNIEnv *, jclass)
{
	float allStockMarketValue = 0.0f;
	allStockMarketValue = THSAPI_GetAllStockMarketValue();
	return allStockMarketValue;
}

/*
* Class:     stormstock_capi_CATHSAccount
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
* Class:     stormstock_capi_CATHSAccount
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