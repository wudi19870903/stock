package stormstock.fw.tran;

import stormstock.fw.stockdata.Stock;

abstract public class IEigen {
	abstract public Object calc(Stock stock, Object... args);
}
