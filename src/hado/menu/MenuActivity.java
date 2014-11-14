package hado.menu;

import hado.quanlychitieu.R;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class MenuActivity extends FragmentActivity implements OnPageChangeListener,OnTabChangeListener{
	
	MyPagerAdapter adapter = null ;
	ViewPager viewPager ;
	
	TabHost tabHost ;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.layout_menu);
		
		getWidgetsId();
		
		createTabs();
		
		adapter = new MyPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		
		viewPager.setOnPageChangeListener(this);
		tabHost.setOnTabChangedListener(this);
		
	}
	
	/**
	 * Gán id vào các widgets
	 */
	public void getWidgetsId(){
		viewPager = (ViewPager) findViewById(R.id.pager);
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
	}
	
	/**
	 * phương thức tạo các tabs
	 */
	public void createTabs(){
		tabHost.setup();
		
		MenuActivity a = MenuActivity.this;
		addCustomTab(R.drawable.tab_home, a, 1, Color.parseColor("#FF4040"));
		addCustomTab(R.drawable.tab_invoice, a, 2, Color.parseColor("#FF4040"));
		addCustomTab(R.drawable.tab_nhatki, a, 3, Color.parseColor("#FF4040"));
		addCustomTab(R.drawable.tab_listmem, a, 4, Color.parseColor("#FF4040"));
		
//		for(int i = 0 ; i < tabHost.getTabWidget().getChildCount() ;i++){
//			tabHost.getTabWidget().getChildAt(i).sets
//		}
	}
	
	/**
	 * 
	 */
	public void addCustomTab(int drawable ,MenuActivity activity ,int i ,int color){
		TabHost.TabSpec spec ;
		spec = tabHost.newTabSpec("Tab "+i);
		tabHost.addTab(spec.setIndicator("", getResources().getDrawable(drawable)).setContent(new MyTabFactory(activity)));
	}
	
	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		int pos = this.tabHost.getCurrentTab();
		this.viewPager.setCurrentItem(pos);
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		int pos = this.viewPager.getCurrentItem();
		this.tabHost.setCurrentTab(pos);
	}
	
	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
