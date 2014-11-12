package hado.danhsachthanhvien;

import hado.database.MemberDetail;
import hado.database.MemberInfo;
import hado.database.MyDatabase;
import hado.quanlychitieu.R;

import java.util.ArrayList;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DanhSachTVFragment extends Fragment {
	TextView tvRoomName ;
	TextView tvWeek ;
	
	ExpandableListView lvMember ;
	public static ExpandableListAdapter adapter = null ;
	ArrayList<MemberInfo> arrMemberInfo = new ArrayList<MemberInfo>();
	ArrayList<MemberDetail> arrMemberDetail = new ArrayList<MemberDetail>();
	
	EditText edAddNew ;
	
	ImageView imgAdd ;
	ImageView imgFix ;
	ImageView imgDel ;
	
	MyDatabase db ;
	
	String week ;
	
	View rootView ;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.layout_dsthanhvien, container, false);
		
		getWidgetsId();
		getDataFromDatabase();
		
		listenClick();
		
		return rootView;
	}
	
	/**
	 * Phương thức gán id vào các widgets
	 */
	public void getWidgetsId(){
		tvRoomName = (TextView) rootView.findViewById(R.id.ds_tenphong);
		tvWeek = (TextView) rootView.findViewById(R.id.ds_tuan);
		
		lvMember = (ExpandableListView) rootView.findViewById(R.id.lv_dsthanhvien);
		
		
		
		edAddNew = (EditText) rootView.findViewById(R.id.ed_add_new_member);
		
		imgAdd = (ImageView) rootView.findViewById(R.id.bt_add_person_finish);
		imgDel = (ImageView) rootView.findViewById(R.id.img_delmember);
		imgFix = (ImageView) rootView.findViewById(R.id.img_fixmember);
		
		db = new MyDatabase(getActivity());
	}
	
	/**
	 * Phương thức lấy thông tin danh sách thành viên trong Database
	 */
	public void getDataFromDatabase(){
		String week = "Tuần 1" ;
		/*Lấy dữ liệu cho danh sách thành viên từ database theo tuần " week " */
		try {
			db.open();
			arrMemberDetail = db.getDataMemberDetail(week);
			db.close();
			adapter = new ExpandableListAdapter(getActivity(), arrMemberDetail);
			lvMember.setAdapter(adapter);
		} catch (Exception e) {
			showToast(getString(R.string.fail_coloixayra));
		}
		tvWeek.setText("  "+week);
	}
	
	/**
	 * phương thức xử lý khi click vào thêm thành viên mới
	 */
	public void listenClick(){
		
		imgAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*Kiểm tra xem đã đủ thông tin chưa*/
				String fullName = edAddNew.getText().toString().trim() ;
				if(isConnected()){
					if(fullName.equalsIgnoreCase("")){
						showToast(getString(R.string.fail_khong_day_du_thong_tin));
					} else {
						if(!isCoincidenceName(fullName)){ // Không bị trùng
							doAddNewMember(fullName);
						}
					}
				} else {
					showToast(getString(R.string.fail_khongcoketnoimang));
				}
				
			}
		});
	}
	
	/**
	 * Phương thức add new thành viên
	 */
	public void doAddNewMember(String fullname){
		
		MemberInfo memNew = new MemberInfo();
		String name[] = fullname.split(" ");
		String lastName = "";
		for(int i = 0 ; i<(name.length-1) ; i++){
			lastName += name[i]+" ";
		}
		memNew.setFirstName(name[name.length-1]);
		memNew.setLastName(lastName);
		memNew.setFullName(edAddNew.getText().toString().trim());
		edAddNew.setText("");
		DanhSachTVAsyncTask threadAdd = new DanhSachTVAsyncTask(getActivity());
		threadAdd.execute(memNew);
		if(threadAdd.getStatus() == AsyncTask.Status.RUNNING){
			getActivity().findViewById(R.id.display_progress_listmem).setVisibility(View.VISIBLE);
		}
		
	}
	
	
	/**
	 * Phương thức kiểm tra máy có đang kết nối Internet hay không ,trả về giá
	 * trị kiểu Boolean
	 */
	public Boolean isConnected() {
		try {
			ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
			NetworkInfo netwInfo = connMgr.getActiveNetworkInfo();
			if (netwInfo != null && netwInfo.isConnected()) {
				return true;
			} else
				return false;
		} catch (Exception e) {
			return false ;
		}
		
	}
	
	/**
	 * Phương thức kiểm tra thành viên thêm vào có bị trùng trong Database hay không ?
	 * @param fullName
	 * @return
	 */
	public boolean isCoincidenceName(String fullName){
		for(int i = 0 ; i<arrMemberInfo.size() ; i++){
			if(fullName.equalsIgnoreCase(arrMemberInfo.get(i).getFullName().trim())){
				return true ;
			}
		}
		return false ;
	}
	
	/**
	 * Phương thức show thông tin truyền vào str lên Toast 
	 * @param str
	 */
	public void showToast(String str) {
		Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getActivity().findViewById(R.id.display_progress_listmem).setVisibility(View.GONE);
	}
}
