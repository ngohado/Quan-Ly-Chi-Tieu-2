package hado.menu;

import android.content.Context;
import android.view.View;
import android.widget.TabHost.TabContentFactory;

public class MyTabFactory implements TabContentFactory {

	Context contextfa ;
	public MyTabFactory(Context c) {
		contextfa = c ;
	}
	
	@Override
	public View createTabContent(String tag) {
		
		View vTab = new View(contextfa);
		vTab.setMinimumHeight(0);
		vTab.setMinimumWidth(0);
		return vTab;
	}

}
