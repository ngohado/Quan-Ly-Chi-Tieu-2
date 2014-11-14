package hado.menu;

import java.util.ArrayList;

import hado.database.MemberInfo;
import hado.database.MyDatabase;
import hado.quanlychitieu.R;
import hado.shopping.ShoppingActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeFragment extends Fragment {
	LinearLayout linearLayout ;
	MyDatabase db ;
	Button btShopping ;
	ArrayList<MemberInfo> arrMemberInfo = new ArrayList<MemberInfo>();
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.layout_home, container, false);
		linearLayout = (LinearLayout) v.findViewById(R.id.list_member_home);
		btShopping = (Button) v.findViewById(R.id.bt_open_shopping);
		btShopping.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getActivity(), ShoppingActivity.class);
				startActivity(i);
			}
		});
		db = new MyDatabase(getActivity());
		try {
			db.open();
			arrMemberInfo = db.getDataMember();			
			db.close();
		} catch (Exception e) {
			
		}
		
		for(int i = 0 ; i< arrMemberInfo.size() ; i++){
			TextView tv = new TextView(getActivity());
			tv.setText("• "+arrMemberInfo.get(i).getFullName());
			linearLayout.addView(tv, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		}
		return v;
	}
}
