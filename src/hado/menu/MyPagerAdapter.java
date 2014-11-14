package hado.menu;

import hado.danhsachthanhvien.DanhSachTVFragment;
import hado.hoadon.InvoiceFragment;
import hado.nhatki.NhatKiFragment;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyPagerAdapter extends FragmentPagerAdapter{
	private List<Fragment> arrFragment ;
	
	public MyPagerAdapter(FragmentManager fm) {
		super(fm);
		arrFragment = new ArrayList<Fragment>();
		arrFragment.add(new HomeFragment());
		arrFragment.add(new InvoiceFragment());
		arrFragment.add(new NhatKiFragment());
		arrFragment.add(new DanhSachTVFragment());
		
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return arrFragment.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrFragment.size();
	}
	
}
