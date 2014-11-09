package hado.danhsachthanhvien;

import hado.database.MemberDetail;
import hado.quanlychitieu.R;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter{
	Activity contextfa ;
	ArrayList<MemberDetail> arrGroup ;
	
	public ExpandableListAdapter(Activity a ,ArrayList<MemberDetail> arr) {
		// TODO Auto-generated constructor stub
		this.contextfa = a ;
		this.arrGroup = arr ;
	}
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null) {
			convertView = contextfa.getLayoutInflater().inflate(R.layout.child_lvmember, null);
		}
		TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_titleChild);
		TextView tvMain = (TextView) convertView.findViewById(R.id.tv_mainChild);
		MemberDetail a = arrGroup.get(groupPosition);
		if(childPosition == 0){
			tvTitle.setText("Tiền bỏ ra");
			tvMain.setText(""+a.getMoneyBought());
		}
		if(childPosition == 1){
			tvTitle.setText("Tiền sử dụng");
			tvMain.setText(""+a.getMoneyUsed());
		}
		if(childPosition == 2){
			tvTitle.setText("Phải đóng");
			tvMain.setText(""+a.getPay());
		}
		if(childPosition == 3){
			tvTitle.setText("Được nhận");
			tvMain.setText(""+a.getReceive());
		}
		return convertView;
	}
	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return 4;
	}
	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return arrGroup.get(groupPosition);
	}
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return arrGroup.size();
	}
	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = contextfa.getLayoutInflater().inflate(R.layout.group_lvmember, null);
		}
		TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_memberName);
		tvTitle.setText(arrGroup.get(groupPosition).getFullName());
		return convertView;
	}
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}
}
