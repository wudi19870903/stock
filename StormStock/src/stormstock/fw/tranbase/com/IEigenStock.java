package stormstock.fw.tranbase.com;

import java.util.List;

import stormstock.fw.tranbase.stockdata.Stock;

abstract public class IEigenStock {
	abstract public Object calc(Stock cStock, Object... args);
}
