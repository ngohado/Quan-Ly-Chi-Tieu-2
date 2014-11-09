package hado.dangki;

import hado.quanlychitieu.R;

import java.util.ArrayList;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Class xử lý dữ liệu thông tin đăng nhập của người dùng và gửi lại Server sau
 * đó nhận lại result từ Server xem có đăng kí thành công hay không
 * 
 * @author Hado
 * 
 */
public class DangKiActivity extends Activity {
	EditText edRoomName;
	EditText edUserName;
	EditText edEmail;
	EditText edPassword;
	EditText edPasswordAgain;
	EditText edDescribe;
	
	TextView tvBack ;
	
	Spinner spMode;
	ArrayList<String> arrList = new ArrayList<String>();
	ArrayAdapter<String> adapter = null ;
	String modeSelected = "" ; 

	Button btRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_dangki);
		disableProgress();;
		
		getWigetsId();
		
		tvBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		btRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isConected()){
					if(checkInformationOffline()){
						String strRoomName = edRoomName.getText().toString().trim();
						String strUserName = edUserName.getText().toString().trim();
						String strEmail = edEmail.getText().toString().trim();
						String strPW = edPassword.getText().toString().trim();
						String strDescribe = edDescribe.getText().toString().trim();
						
						new DangKiAsyncTask(DangKiActivity.this).execute(strRoomName,strUserName,strPW,strEmail,strDescribe,modeSelected);
					}
				} else {
					showToast(getString(R.string.fail_khongcoketnoimang));
				}

			}
		});
		/*Xử lý khi chọn các item trong Spiner*/
		spMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				modeSelected = arrList.get(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				modeSelected = getString(R.string.spiner_congkhai);
			}
		});
	}

	/**
	 * Phương thức gán địa chỉ ID cho các widgets
	 */
	private void getWigetsId() {
		edRoomName = (EditText) findViewById(R.id.ed_tenphong);
		edUserName = (EditText) findViewById(R.id.ed_tendangnhap);
		edEmail = (EditText) findViewById(R.id.ed_dkemail);
		edPassword = (EditText) findViewById(R.id.ed_matkhau);
		edPasswordAgain = (EditText) findViewById(R.id.ed_matkhau_again);
		edDescribe = (EditText) findViewById(R.id.ed_mota);

		tvBack = (TextView) findViewById(R.id.hado_back);
		
		spMode = (Spinner) findViewById(R.id.sp_chedo);
		arrList.add(getString(R.string.spiner_congkhai));
		arrList.add(getString(R.string.spiner_bimat));
		adapter = new ArrayAdapter<String>(DangKiActivity.this, android.R.layout.simple_spinner_item, arrList);
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spMode.setAdapter(adapter);

		btRegister = (Button) findViewById(R.id.bt_dangki);
		edRoomName.requestFocus();
	}

	/**
	 * Phương thức kiểm tra xem thông tin đã điền đầy đủ và hợp lệ chưa ?
	 * 
	 * @return
	 */
	private boolean checkInformationOffline() {
		String strRoomName = edRoomName.getText().toString().trim();
		String strUserName = edUserName.getText().toString().trim();
		String strEmail = edEmail.getText().toString().trim();
		String strPW = edPassword.getText().toString().trim();
		String strPWA = edPasswordAgain.getText().toString().trim();
		/*Kiểm tra xem đã đầy đủ thông tin chưa nếu đủ rồi thì kiểm tra tiếp*/
		if (strRoomName.equalsIgnoreCase("")
				|| strUserName.equalsIgnoreCase("")
				|| strEmail.equalsIgnoreCase("") || strPW.equalsIgnoreCase("")
				|| strPWA.equalsIgnoreCase("")) {
			showToast(getString(R.string.fail_khong_day_du_thong_tin)); // Thông báo người dùng điền thiếu thông tin
		} else {
			/*Kiểm tra xem Email đã đúng định dạng chưa nếu đúng rồi thì tiếp tục*/
			if(!isEmailValid(strEmail)){
				showToast(getString(R.string.fail_saidinhdangEmail));
			} else {
				/*Kiểm tra tên đăng nhập và mật khẩu có đủ 6 kí tự k ? Nếu đủ thì kiểm tra tiếp*/
				if( (strUserName.length() < 6) || (strPW.length() < 6) ){
					showToast(getString(R.string.fail_du_ki_tu));
				} else {
					/*Kiểm tra xem 2 phần nhập mật khẩu có trùng nhau không ? Nếu giống trả về true .Tiếp tục kiểm tra Online*/
					if(!strPW.equalsIgnoreCase(strPWA)){
						showToast(getString(R.string.fail_nhaplaimatkhau_khongchinhxac));
					} else {
						return true ;
					}
				}
			}
		}
		return false;
	}
	public boolean isConected(){
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo netwInfo = connMgr.getActiveNetworkInfo();
		if (netwInfo != null && netwInfo.isConnected()) {
			return true;
		} else
			return false;
	}
	/**
	 * Phương thức hiển thị thông báo cho người dung lên Toast
	 */
	public void showToast(String str){
		Toast.makeText(DangKiActivity.this, str, Toast.LENGTH_LONG).show();
	}
	/**
	 * Phương thức kiểm tra xem mục Email người dùng có đúng dạng Email không ?
	 */
	public boolean isEmailValid(CharSequence email){
		
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ;
	}
	
	public void disableProgress(){
		findViewById(R.id.display_progress).setVisibility(View.GONE);
	}
}
