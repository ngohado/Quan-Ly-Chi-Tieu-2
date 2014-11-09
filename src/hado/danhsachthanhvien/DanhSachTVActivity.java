package hado.danhsachthanhvien;

import hado.database.MemberDetail;
import hado.database.MemberInfo;
import hado.database.MyDatabase;
import hado.quanlychitieu.R;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Lớp hiển thị danh sách thành viên theo tuần 
 * @author Hado
 *
 */
public class DanhSachTVActivity extends Activity{
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
	
	MyDatabase db = new MyDatabase(DanhSachTVActivity.this);
	
	String week ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_dsthanhvien);
		getWidgetsId();
		getDataFromDatabase();
//		listenClick();
	}
	
	/**
	 * Phương thức gán Id vào các widgets
	 */
	public void getWidgetsId(){
		tvRoomName = (TextView) findViewById(R.id.ds_tenphong);
		tvWeek = (TextView) findViewById(R.id.ds_tuan);
		
		lvMember = (ExpandableListView) findViewById(R.id.lv_dsthanhvien);
		
		
		
		edAddNew = (EditText) findViewById(R.id.ed_add_new_member);
		
		imgAdd = (ImageView) findViewById(R.id.bt_add_person_finish);
		imgDel = (ImageView) findViewById(R.id.img_delmember);
		imgFix = (ImageView) findViewById(R.id.img_fixmember);
	}
	/**
	 * Lấy dữ liệu từ Database theo tuần truyền vào
	 */
	public void getDataFromDatabase(){
		Intent i = getIntent();
		Bundle bundle = i.getBundleExtra("DATA");
		String request = bundle.getString("DATA");
		
		/*
		 * Nếu activity này được gọi từ Menu thì sẽ cho add mới thành viên
		 * vì nếu gọi từ menu sẽ vào tuần mới nhất 
		 */
		if(!request.equalsIgnoreCase("MENU")){
			edAddNew.setEnabled(false);
		}
		week = bundle.getString("WEEK");
		/*Lấy dữ liệu cho danh sách thành viên từ database theo tuần " week " */
		try {
			db.open();
			arrMemberDetail = db.getDataMemberDetail(week);
			db.close();
			adapter = new ExpandableListAdapter(DanhSachTVActivity.this, arrMemberDetail);
			lvMember.setAdapter(adapter);
		} catch (Exception e) {
			showToast(getString(R.string.fail_coloixayra));
		}
		tvWeek.setText("  "+week);
	}
	public void listenClick(){
		imgAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*Kiểm tra xem đã đủ thông tin chưa*/
				
				if(isConnected()){
					if(edAddNew.getText().toString().trim().equalsIgnoreCase("")){
						showToast(getString(R.string.fail_khong_day_du_thong_tin));
					} else {
						doAddNewMember();
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
	public void doAddNewMember(){
		MemberInfo memNew = new MemberInfo();
		String name[] = edAddNew.getText().toString().trim().split(" ");
		String lastName = "";
		for(int i = 0 ; i<(name.length-1) ; i++){
			lastName += name[i]+" ";
		}
		memNew.setFirstName(name[name.length-1]);
		memNew.setLastName(lastName);
		memNew.setFullName(edAddNew.getText().toString().trim());
		edAddNew.setText("");
		
	}
	
	/**
	 * Phương thức kiểm tra máy có đang kết nối Internet hay không ,trả về giá
	 * trị kiểu Boolean
	 */
	public Boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo netwInfo = connMgr.getActiveNetworkInfo();
		if (netwInfo != null && netwInfo.isConnected()) {
			return true;
		} else
			return false;
	}
	/**
	 * Phương thức hiển thị thông báo lên Dialog
	 * @param title
	 * @param msg
	 * @param icon
	 */
	public void myDialog(String title ,String msg ,int icon){
		AlertDialog.Builder dialog = new AlertDialog.Builder(DanhSachTVActivity.this);
		dialog.setTitle(title);
		dialog.setMessage(msg);
		dialog.setIcon(icon);
		
		dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		
		dialog.create().show();
	}
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
		Toast.makeText(DanhSachTVActivity.this, str, Toast.LENGTH_LONG).show();
	}
}
