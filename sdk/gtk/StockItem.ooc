use gtk;

class StockItem {
	
	GtkStockItem item;

	//new(String stockId, String label, GdkModifierType modifier, guint keyval, String translationDomain) {
	new(String stockId, String label, Int modifier, guint keyval, String translationDomain) {
		
		item.stock_id = stockId;
		item.label = label;
		item.modifier = modifier;
		item.keyval = keyval;
		item.translation_domain = translationDomain;
		
	}
	
	new(String stockId, String label) {
		
		item.stock_id = stockId;
		item.label = label;
		item.modifier = 0;
		item.keyval = 0;
		item.translation_domain = null;
		
	}
	
	func getStockItem -> GtkStockItem* {
		
		return &item;
		
	}

}

