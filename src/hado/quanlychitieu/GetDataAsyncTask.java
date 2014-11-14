package hado.quanlychitieu;

import hado.config.WebServiceAPIConfig;
import hado.database.Invoice;
import hado.database.MemberDetail;
import hado.database.MemberInfo;
import hado.database.MyDatabase;
import hado.menu.MenuActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

public class GetDataAsyncTask extends AsyncTask<Void, String, Void> {
	String COLUM_ID = "ID";
	String COLUM_WEEK = "WEEK";
	String COLUM_MEMBERBOUGHT = "MEMBERBOUGHT";
	String COLUM_TIMEBOUGHT = "TIMEBOUGHT";
	String COLUM_MONEY = "MONEY";
	String COLUM_MEMBERLQ = "MEMBERLQ";
	String COLUM_DESCRIPTION = "DESCRIPTION";
	String COLUM_FIRSTNAME = "FIRSTNAME";
	String COLUM_LASTNAME = "LASTNAME";
	String COLUM_FULLNAME = "FULLNAME";
	String COLUM_MONEYBOUGHT = "MONEYBOUGHT";
	String COLUM_MONEYUSED = "MONEYUSED";
	
	Activity contextfa ;
	
	/**
	 * Phương thức contructor gán activity
	 * @param a
	 */
	public GetDataAsyncTask(Activity a) {
		contextfa = a;
	}
	@Override
	protected Void doInBackground(Void... params) {
		
		/*Khai báo máy con vào gán link vào httpPost*/
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(WebServiceAPIConfig.httpAddress);
		
		/*Lấy file SharedPreferences để get username và password*/
		SharedPreferences pre = contextfa.getSharedPreferences("data_status",
				Context.MODE_PRIVATE);
		
		/*Get dữ liệu memberDetail*/
		try {
			/*Gán yêu cầu task ,username và passwowrd vào namevaluepair để tí gán vào httpPost gửi lên webService*/
			List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair("task", "memberdetail"));
			nameValuePair.add(new BasicNameValuePair("username", pre.getString(
					"username", "")));
			nameValuePair.add(new BasicNameValuePair("password", pre.getString(
					"password", "")));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
			
			/*Dùng httpResponse nhận dữ liệu khi dùng máy con httpClient gửi lên Server*/
			HttpResponse httpResponse = httpClient.execute(httpPost);

			/*Get dữ liệu theo kiểu inputStream*/
			InputStream inputStream = httpResponse.getEntity().getContent();
			
			
			if (inputStream != null) { // inputStream có dữ liệu
				
				/*Đọc dữ liệu vào kiểu Buffere*/
				BufferedReader bufferedR = new BufferedReader(
						new InputStreamReader(inputStream));
				
				/*Đọc từng dòng trong bufferedR cho đến hết cho vào dạng Text*/
				String line = "";
				String textJson = "";
				while ((line = bufferedR.readLine()) != null) {
					textJson += line;
				}
//				SharedPreferences p = contextfa.getSharedPreferences("test2", Context.MODE_PRIVATE);
//				SharedPreferences.Editor editor = p.edit();
//				editor.putString("t", textJson);
//				editor.commit();
				/*Gán đoạn text dữ liệu Json vào Object Json để đọc dữ liệu trong*/
				JSONObject jsonO = new JSONObject(textJson);
				
				/*Lấy dữ liệu trả về trong trường result <true or false>*/
				String result = jsonO.getString("result");
				
				/*Nếu true thì get dữ liệu cho vào Database*/
				if (result.equalsIgnoreCase("true")) {
					/*Đọc theo thứ tự ...để hiểu xuất file sharedPreferences để đọc*/
					int i = 0;					
					try {
						/*Khai báo list memberdetail để gửi vào database*/
						ArrayList<MemberDetail> arr = new ArrayList<MemberDetail>();
						while (!jsonO.isNull(""+i)) {
							
							JSONObject c = jsonO.getJSONObject("" + i);
							MemberDetail b = new MemberDetail();
							b.setWeek(c.getString(COLUM_WEEK));
							b.setFullName(c.getString("MEMBER"));
							float moneyB = Float.parseFloat(c.getString(COLUM_MONEYBOUGHT));
							float moneyU = Float.parseFloat(c.getString(COLUM_MONEYUSED));
							b.setMoneyBought(moneyB);
							b.setMoneyUsed(moneyU);
					
							arr.add(b);

							i++;
						}
						
						/*add list memberdetai vào database*/
						try {
							MyDatabase db = new MyDatabase(contextfa);
							db.open();
							db.creatDataMemberDetail(arr);
							db.close();
						} catch (Exception e) {
							
						}

					} catch (Exception e) {
						
					}

				} else {
					
				}
			}
		} catch (Exception e) {
			
		}
		
		/*
		 * Get dữ liệu chi tiết hóa đơn (invoice) từ server và ghi vào database
		 * các comment tương tự như trên
		 */
		try {
			List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair("task", "invoice"));
			nameValuePair.add(new BasicNameValuePair("username", pre.getString("username", "")));
			nameValuePair.add(new BasicNameValuePair("password", pre.getString("password", "")));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			InputStream inputStream = httpResponse.getEntity().getContent();
			
			if(inputStream != null){
				BufferedReader bufferedR = new BufferedReader(new InputStreamReader(inputStream));
				String line = "";
				String textJson = "";
				
				while((line = bufferedR.readLine()) != null){
					textJson += line ;
				}
//				SharedPreferences p = contextfa.getSharedPreferences("test", Context.MODE_PRIVATE);
//				SharedPreferences.Editor editor = p.edit();
//				editor.putString("t", textJson);
//				editor.commit();
				JSONObject jsonO = new JSONObject(textJson);
				String result = jsonO.getString("result");
				
				if(result.equalsIgnoreCase("true")){
					int i = 0 ;
					ArrayList<Invoice> arr = new ArrayList<Invoice>();
					try {
						
						while(!jsonO.isNull(""+i)){
							Invoice invoice = new Invoice();
							JSONObject a = jsonO.getJSONObject(""+i);
							invoice.setWeek(a.getString(COLUM_WEEK));
							invoice.setMemberBought(a.getString(COLUM_MEMBERBOUGHT));
							float money = Float.parseFloat(a.getString(COLUM_MONEY));
							invoice.setMoney(money);
							invoice.setMemberLQ(a.getString(COLUM_MEMBERLQ));
							invoice.setDescription(a.getString(COLUM_DESCRIPTION));
							try {
								invoice.setTimeBought(handlingTime(a.getJSONObject(COLUM_TIMEBOUGHT).getString("date")));
							} catch (Exception e) {
								
							}
							arr.add(invoice);
							i++;
						}
						
						try {
							MyDatabase db = new MyDatabase(contextfa);
							db.open();
							db.creatDataInvoice(arr);
							db.close();
						} catch (Exception e) {
							
						}
					} catch (Exception e) {
						
					}
					
				}
			} 
		} catch (Exception e) {
			
		}
		
		/*
		 * Get dữ liệu member từ server rồi gán vào database
		 * comment tương tự phần trên
		 */
		try {
			List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair("task", "member"));
			nameValuePair.add(new BasicNameValuePair("username", pre.getString("username", "")));
			nameValuePair.add(new BasicNameValuePair("password", pre.getString("password", "")));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			InputStream inputStream = httpResponse.getEntity().getContent();
			if(inputStream != null){
				BufferedReader bufferedR = new BufferedReader(
						new InputStreamReader(inputStream));
				String line = "";
				String textJson = "";
				while ((line = bufferedR.readLine()) != null) {
					textJson += line ;
				}
//				
//				SharedPreferences p = contextfa.getSharedPreferences("test3", Context.MODE_PRIVATE);
//				SharedPreferences.Editor editor = p.edit();
//				editor.putString("t", textJson);
//				editor.commit();
				JSONObject jsonO = new JSONObject(textJson);
				String result = jsonO.getString("result");
				if(result.equalsIgnoreCase("true")){
					int i = 0 ;
					try {
						ArrayList<MemberInfo> arr = new ArrayList<MemberInfo>();
						while(!jsonO.isNull(""+i)){
							JSONObject a = jsonO.getJSONObject(""+i);
							MemberInfo mem = new MemberInfo();
							String id = a.getString(COLUM_ID);
							String firstName = a.getString(COLUM_FIRSTNAME);
							String lastName = a.getString(COLUM_LASTNAME);
							mem.setId(id);
							mem.setFirstName(firstName);
							mem.setLastName(lastName);
							String fullName = lastName + " " + firstName ;
							mem.setFullName(fullName);
							
							arr.add(mem);
							i++;
						}
						
						try {
							MyDatabase db = new MyDatabase(contextfa);
							db.open();
							db.creatDataMember(arr);
							db.close();
						} catch (Exception e) {
							
						}
						
					} catch (Exception e) {
						
					}
				}
			}
		} catch (Exception e) {
			
		}
		
		/**
		 * Lấy dữ liệu tuần 
		 */
		try {
			List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair("task", "getweek"));
			nameValuePair.add(new BasicNameValuePair("username", pre.getString("username", "")));
			nameValuePair.add(new BasicNameValuePair("password", pre.getString("password", "")));
			
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			InputStream inputStream = httpResponse.getEntity().getContent();
			BufferedReader bufferedR = new BufferedReader(new InputStreamReader(inputStream));
			String line = "" ;
			String textJson = "" ;
			while((line = bufferedR.readLine()) != null ){
				textJson += line ;
			}
			
			JSONObject jsonO = new JSONObject(textJson);
			String result = jsonO.getString("result");
			if(result.equalsIgnoreCase("true")){
				int i = 0 ;
				try {
					ArrayList<String> week = new ArrayList<String>() ;
					ArrayList<String> idWeek = new ArrayList<String>();
					while(!jsonO.isNull(""+i)){
						JSONObject a = jsonO.getJSONObject(""+i);
						week.add(a.getString("week"));
						idWeek.add(a.getString("id"));
						
						i++ ;
					}
					
					try {
						MyDatabase db = new MyDatabase(contextfa);
						db.open();
						db.creatDataWeek(week, idWeek);
						db.close();
					} catch (Exception e) {
						
					}
				} catch (Exception e) {
					
				}
			}
		} catch (Exception e) {
			
		}
		return null;
	}
	
	/**
	 * Phương thức được truyền vào thời gian mua (time bought) và xử lý trả về
	 * để gán vào timeBought trong class Invoice
	 * @param inp
	 * @return
	 */
	public String handlingTime(String inp){
		String time = "" ;
		String arr[] = inp.split(" ");
		String date[] = arr[0].split("-");		
		StringTokenizer a = new StringTokenizer(arr[1], ":");
		String hour = a.nextElement().toString();
		hour += ":"+a.nextElement().toString();
		time = hour+"  "+date[2]+"-"+date[1]+"-"+date[0] ;
		return time ;
		
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		Toast.makeText(contextfa, values[0], Toast.LENGTH_SHORT).show();
	}
	/**
	 * Phương thức khi thực hiện xong sẽ chuyển tắt activity và chuyển về Menu
	 */
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		contextfa.findViewById(R.id.display_progress_index).setVisibility(View.GONE);
		Intent i = new Intent(contextfa, MenuActivity.class);
		contextfa.finish();
		contextfa.startActivity(i);
	}

}
