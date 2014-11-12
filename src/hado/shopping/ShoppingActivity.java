package hado.shopping;

import hado.database.Invoice;
import hado.database.MemberInfo;
import hado.database.MyDatabase;
import hado.quanlychitieu.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Lớp hiển thị layout thêm mới và sử lý việc thêm mới mua sắm
 * @author Hado
 *
 */
public class ShoppingActivity extends ActionBarActivity{
	Spinner spMemB;
	ArrayList<String> arrSP = new ArrayList<String>();
	ArrayAdapter<String> adapterSP = null ;
	String memberSelected = "";

	EditText edMoney;
	EditText edDescription;

	public LinearLayout linearLayout;

	Button btAdd;
	
	MyDatabase db = new MyDatabase(ShoppingActivity.this);

	ArrayList<MemberInfo> arrMem = new ArrayList<MemberInfo>();
	
	String week = "" ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_addnew);
		getWidgetsId();
		
		getData();
		
		/*xử lý sự kiện click vào button them moi*/
		btAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!isConnected()){ // not connected
					
					showToast(R.string.fail_khongcoketnoimang);
				} else { // connected
					
					String money = edMoney.getText().toString().trim();
					String description = edDescription.getText().toString().trim();
					String memberLQ = "" ;
					
					int size = linearLayout.getChildCount() ;
					for(int i=0 ; i < size; i++){
						/*Lấy view con của linear layout thứ tự setId() */
						View child =linearLayout.getChildAt(i);
						if(child instanceof CheckBox){ //so sánh xem view child có phải là checkbox hay không (instanceof) 
							
							CheckBox cb = (CheckBox) child;
							
							if(cb.isChecked()){ // xem những checkbox nào được đánh dấu thì thêm vào String
								memberLQ += cb.getText().toString() + "," ;
							}						
						}					
					}
					
					if(memberLQ.isEmpty() || money.isEmpty() || description.isEmpty() ){ // khong day du thong tin
						showToast(R.string.fail_khong_day_du_thong_tin);
					} else { //day du thong tin
						requestTask(money, description, memberLQ);
					}
				}
				
			}
		});
		
		/*xử lý sự kiện khi chọn spinner*/
		spMemB.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				memberSelected = arrSP.get(position) ;
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		}) ;
		
	}
	
	/**
	 * Phương thức gán id vào các widgets 
	 */
	public void getWidgetsId() {
		spMemB = (Spinner) findViewById(R.id.sp_nguoimua);

		edMoney = (EditText) findViewById(R.id.ed_sotien);
		edDescription = (EditText) findViewById(R.id.ed_motathemoi);

		linearLayout = (LinearLayout) findViewById(R.id.checkbox);

		btAdd = (Button) findViewById(R.id.bt_themmuasam);
	}
	
	/**
	 * Phương thức lấy danh sách tên thành viên để gán vào spiner và LinearLayout với các checkbox
	 */
	public void getData() {
		
		/*Lấy dữ liệu từ database vào arrMem*/
		try {
			db.open();
			arrMem = db.getDataMember();
			db.close();

		} catch (Exception e) {

		}
		
		/*Phần thêm các checkbox vào trong linearLayout*/
		for (int i = 0; i < arrMem.size(); i++) {
			CheckBox c = new CheckBox(getApplicationContext());
			String name = arrMem.get(i).getFullName();
			c.setText(name);
			LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			c.setId(i);
			c.setLayoutParams(param);
			c.setTextColor(Color.parseColor("#009966"));
			linearLayout.addView(c);
			
			/*thêm tên vào danh sách hiển thị spiner*/
			arrSP.add(name);			
		}
		
		/*thiết lập danh sách cho spiner*/
		adapterSP = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,arrSP);
		adapterSP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spMemB.setAdapter(adapterSP);
		
		/*Lấy tuần được gửi về*/
		Intent result = getIntent();
		week = result.getStringExtra("WEEK");
	}
	
	public void requestTask(String money ,String description ,String memberLQ){
		Calendar cal = Calendar.getInstance(); // lấy thời gian hiện tại của hệ thống
		SimpleDateFormat sdf = null ;
		
		String strTime1 = "yyyy-MM-dd HH:mm:ss" ;
		String strTime2 = "HH:mm dd-MM-yyyy" ;
		
		sdf = new SimpleDateFormat(strTime2);
		String timeDatabase = sdf.format(cal.getTime());
		
		sdf = new SimpleDateFormat(strTime1);
		String timeServer = sdf.format(cal.getTime());
		
		Invoice invoice = new Invoice();
		invoice.setWeek(week);
		invoice.setMemberBought(memberSelected);
		invoice.setMoney(Float.parseFloat(money));
		invoice.setMemberLQ(memberLQ);
		invoice.setDescription(description);
		invoice.setTimeBought(timeServer);
		
		Invoice invoice1 = new Invoice();
		invoice1.setWeek(week);
		invoice1.setMemberBought(memberSelected);
		invoice1.setMoney(Float.parseFloat(money));
		invoice1.setMemberLQ(memberLQ);
		invoice1.setDescription(description);
		invoice1.setTimeBought(timeDatabase);
		
		try {
			db.open();
			db.creatSimpleInvoice(invoice1);
			db.close();			
		} catch (Exception e) {
			
		}
		
		ShoppingAsyncTask task = new ShoppingAsyncTask(ShoppingActivity.this);
		task.execute(invoice);
		
		if(task.getStatus() == AsyncTask.Status.RUNNING){
			findViewById(R.id.display_progress_addnew).setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Set màu nền, title và button back cho actionbar 
	 */
	public void customActionBar() {
		ActionBar ac = getSupportActionBar();
		ac.setDisplayHomeAsUpEnabled(true);
		ac.setTitle(R.string.quay_lai);
		ColorDrawable color = new ColorDrawable(Color.parseColor("#009966"));
		ac.setBackgroundDrawable(color);

	}

	/**
	 * Xử lý button back của actionbar
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
           
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Show toast
	 * @param id
	 */
	public void showToast(int id) {
		String str = getString(id);
		Toast.makeText(ShoppingActivity.this, str, Toast.LENGTH_LONG).show();
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
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		customActionBar();
	}
	
}
