use gtk;
import StockItem;

class Stock {

	static func add(StockItem stockItem) {
		
		gtk_stock_add(stockItem.getStockItem(), 1);
		
	}

}
