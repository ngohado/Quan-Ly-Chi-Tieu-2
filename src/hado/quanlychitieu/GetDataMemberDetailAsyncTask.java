package hado.quanlychitieu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import hado.config.WebServiceAPIConfig;
import hado.database.MemberDetail;
import hado.database.MyDatabase;

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
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class GetDataMemberDetailAsyncTask extends AsyncTask<Void, Void, Void>{
	Activity contextfa ;
	String COLUM_WEEK = "WEEK";
	String COLUM_MONEYBOUGHT = "MONEYBOUGHT";
	String COLUM_MONEYUSED = "MONEYUSED";
	/**
	 * Phương thức contructor gán activity
	 * @param a
	 */
	public GetDataMemberDetailAsyncTask(Activity a) {
		contextfa = a;
	}
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		/*Khai báo máy con vào gán link vào httpPost*/
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(WebServiceAPIConfig.httpAddress);
		
		/*Lấy file SharedPreferences để get username và password*/
		SharedPreferences pre = contextfa.getSharedPreferences("data_status",
				Context.MODE_PRIVATE);
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
		return null;
	}

}
