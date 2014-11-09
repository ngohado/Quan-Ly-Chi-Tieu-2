package hado.quanlychitieu;

import hado.dangki.DangKiActivity;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Button btLogin_index ;
	Button btLogin ;
	Button btRegister ;
	
	EditText edUsername ;
	EditText edPw ;
	
	TextView tvForgotPw ;
	TextView tvBack ;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findViewById(R.id.layout_dangki_dangnhap).setVisibility(View.GONE);
		findViewById(R.id.layout_dangnhap).setVisibility(View.GONE);
		findViewById(R.id.display_progress_index).setVisibility(View.GONE);
		
		getWidgetsId();
		
		if(isFirst()){ // isFirst = true
			try {
				findViewById(R.id.layout_dangki_dangnhap).setVisibility(View.VISIBLE);
				layoutLoginRegister();
			} catch (Exception e) {
				showToast("0");
			}
		} else{ //isFirst = false
			if(isConnected()){ //có kết nối
				loadDataFromServer();
				
			}else{ // không có kết nối mạng .chuyển về menu
				showToast(R.string.load_old_data);
			}
		}
				
	}
	
	/**
	 * Lấy id cho các widgets
	 */
	public void getWidgetsId(){
		btLogin = (Button) findViewById(R.id.bt_dangnhap_login);
		btLogin_index = (Button) findViewById(R.id.bt_dangnhap1);
		btRegister = (Button) findViewById(R.id.bt_dangki);
		
		edUsername = (EditText) findViewById(R.id.ed_tendangnhap_login);
		edPw = (EditText) findViewById(R.id.ed_matkhau_login);
		
		tvForgotPw = (TextView) findViewById(R.id.tv_forgotpw);
		tvBack = (TextView) findViewById(R.id.tv_back_login);
	}
	
	/**
	 * Chuyển đến Layout gồm 2 button Đăng Nhập và Đăng kí và xử lý 2 button
	 */
	public void layoutLoginRegister(){
		/**/
		btLogin_index.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				findViewById(R.id.layout_dangki_dangnhap).setVisibility(View.GONE);
				findViewById(R.id.layout_dangnhap).setVisibility(View.VISIBLE);
				layoutLogin();
			}
		});
		
		/*Chuyển đến Activity Đăng ký*/
		btRegister.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent openRegisterActivity = new Intent(MainActivity.this, DangKiActivity.class);
				startActivity(openRegisterActivity);
				
			}
		});
	}
	
	/**
	 * Chuyển đến layout đăng nhập và xử lý quá trình đăng nhập 
	 */
	public void layoutLogin(){
		
		btLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isConnected()) {
					String strUserName = edUsername.getText().toString().trim();
					String strPassWord = edPw.getText().toString().trim();
					if (strUserName.equalsIgnoreCase("")
							|| strPassWord.equalsIgnoreCase("")) {
						showToast(R.string.fail_khong_day_du_thong_tin);
					} else {
						try {
							sendRequestForReuslt(strUserName, strPassWord);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					showToast(R.string.fail_khongcoketnoimang);
				}
			}
		});
		
		tvBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				findViewById(R.id.layout_dangki_dangnhap).setVisibility(View.VISIBLE);
				findViewById(R.id.layout_dangnhap).setVisibility(View.GONE);
			}
		});
				
	}
	/**
	 * Phương thức gửi đi dữ liệu Username và Password về Server để kiểm tra xem
	 * có đúng không
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public void sendRequestForReuslt(String uname, String passw) throws InterruptedException, ExecutionException {
		DangNhapAsyncTask asyncTask = new DangNhapAsyncTask(MainActivity.this);
		asyncTask.execute(uname,passw);
		if(asyncTask.getStatus() == AsyncTask.Status.RUNNING){
			showToast(R.string.loading_string);
			findViewById(R.id.display_progress_index).setVisibility(View.VISIBLE);
		}
	}
	/**
	 * Load tất cả dữ liệu vào database từ server
	 */
	public void loadDataFromServer(){
		
		GetDataAsyncTask getData = new GetDataAsyncTask(MainActivity.this);
		getData.execute();
		if(getData.getStatus() == AsyncTask.Status.RUNNING){
			showToast(R.string.loading_string);
			findViewById(R.id.display_progress_index).setVisibility(View.VISIBLE);
		}
		
	}
	
	/**
	 * Phương thức kiểm tra xem đã kết nối mạng hay chưa ?
	 * @return
	 */
	public boolean isConnected(){
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo netwInfo = connMgr.getActiveNetworkInfo();
		if (netwInfo != null && netwInfo.isConnected()) {
			return true;
		} else
			return false;
	}
	
	/**
	 * Xem có phải lần đầu tên đăng nhập vào không ?
	 * @return
	 */
	public boolean isFirst(){
		SharedPreferences sPre = this.getSharedPreferences("data_status", MODE_PRIVATE);
		int theFirst = sPre.getInt("theFirst", 1);
		if(theFirst == 0){
			return false ;
		}
		return true ;
	}
	
	/**
	 * Hiện thị thông báo cho người dùng lên Toast
	 * @param id
	 */
	public void showToast(int id) {
		String str = getString(id);
		Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
	}
	public void showToast(String str) {
		Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
