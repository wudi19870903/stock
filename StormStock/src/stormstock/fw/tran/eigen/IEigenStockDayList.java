package stormstock.fw.tran.eigen;

import java.util.List;

import stormstock.fw.stockdata.Stock;

abstract public class IEigenStockDayList {
	abstract public Object calc(Stock cStock, Object... args);
}
