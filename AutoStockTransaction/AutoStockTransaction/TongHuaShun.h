#include "Common.h"

#include "windows.h"
#include "commctrl.h"

//////////////////////////////////////////////////////////////////////

// ���׶�������
enum TRANACT
{
	BUY,
	SELL,
};

// ����ί�е�����
struct CommissionOrder
{
	std::string date; // ����
	std::string time; // ʱ��
	std::string stockID; // ��ƱID
	TRANACT tranAct; // ���׶���
	int commissionAmount; // ί������
	float commissionPrice; // ί�м۸�
	int dealAmount; // �ɽ�����
	float dealPrice; // �ɽ��۸�
};

// �ֹɶ���
struct HoldStock
{
	std::string stockID; // ��ƱID
	int totalAmount; // ������
	int availableAmount; // ��������
	float refPrimeCostPrice; // �ο��ɱ���
	float curPrice; // ��ǰ��
};

// ���ճɽ�������
struct DealOrder
{
	std::string date; // ����
	std::string time; // ʱ��
	std::string stockID; // ��ƱID
	TRANACT tranAct; // ���׶���
	int dealAmount; // �ɽ�����
	float dealPrice; // �ɽ��۸�
};


/////////////////////////////////////////////////////////////////////////

// ͬ��˳��ҳ�ʼ��
int THSAPI_TongHuaShunInit();

// ��ȡ�˻����ý��
float THSAPI_GetAvailableMoney();

// ��ȡ�˻����ʲ�
float THSAPI_GetTotalAssets();

// ��ȡ�˻���Ʊ����ֵ
float THSAPI_GetAllStockMarketValue();

// ��ȡ����ί���б�
bool THSAPI_GetCommissionOrderList(std::list<CommissionOrder> & resultList);

// ��ȡ�ֹ��б�
bool THSAPI_GetHoldStock(std::list<HoldStock> & resultList);

// ��ȡ���ս���б�
bool THSAPI_GetDealOrderList(std::list<DealOrder> & resultList);

// �����Ʊ�ӿ�
int THSAPI_BuyStock(const char* stockId, const int buyAmount, const float price);

// ������Ʊ�ӿ�
int THSAPI_SellStock(const char* stockId, const int sellAmount, const float price);

