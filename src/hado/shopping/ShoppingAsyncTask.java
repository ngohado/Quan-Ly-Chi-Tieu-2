package hado.shopping;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import hado.config.WebServiceAPIConfig;
import hado.database.Invoice;
import hado.database.MyDatabase;
import hado.quanlychitieu.R;

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
import android.view.View;
import android.widget.Toast;

/**
 * Phương thức gửi chi tiết mua sắm về server
 * @author Hado
 *
 */
public class ShoppingAsyncTask extends AsyncTask<Invoice, Void, Boolean>{
	Activity contextfa ;
	public ShoppingAsyncTask(Activity a) {
		contextfa = a ;
	}
	@Override
	protected Boolean doInBackground(Invoice... params) {
		/*Khai báo máy con vào gán link vào httpPost*/
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(WebServiceAPIConfig.httpAddress);
		
		/*Lấy file SharedPreferences để get username và password*/
		SharedPreferences pre = contextfa.getSharedPreferences("data_status",
				Context.MODE_PRIVATE);
		
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("task", "newshopping"));
		nameValuePair.add(new BasicNameValuePair("username", pre.getString("username", "")));
		nameValuePair.add(new BasicNameValuePair("password", pre.getString("password", "")));
		nameValuePair.add(new BasicNameValuePair("week", params[0].getWeek()));
		nameValuePair.add(new BasicNameValuePair("memberbought", params[0].getMemberBought()));
		nameValuePair.add(new BasicNameValuePair("timebought", params[0].getTimeBought()));
		nameValuePair.add(new BasicNameValuePair("money",params[0].getMoney()+""));
		nameValuePair.add(new BasicNameValuePair("memberlq", params[0].getMemberLQ()));
		nameValuePair.add(new BasicNameValuePair("description", params[0].getDescription()));
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			InputStream inputStream = httpResponse.getEntity().getContent();
			BufferedReader bufferedR = new BufferedReader(new InputStreamReader(inputStream));
			
			String line = "";
			String textJson = "";
			while((line = bufferedR.readLine()) != null ){
				textJson += line ;
			}
			
			JSONObject jsonO = new JSONObject(textJson);
			String result = jsonO.getString("result");
			
			if(result.equalsIgnoreCase("true")){
				insertToDatabase(params[0]);
				return true ;
			} else {
				return false ;
			}
		} catch (Exception e) {
			
		}
		return false;
	}

	/**
	 * Thêm hóa đơn vào database nếu thành công
	 * @param in
	 */
	public void insertToDatabase(Invoice in){
		MyDatabase db = new MyDatabase(contextfa);
		try {
			db.open();
			db.creatSimpleInvoice(in);
			db.close();
		} catch (Exception e) {
			
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(result){
			showToast(R.string.them_thanh_vien_thanh_cong);
		} else {
			showToast(R.string.fail_coloixayra);
		}
		contextfa.findViewById(R.id.display_progress_addnew).setVisibility(View.GONE);
		onCancelled();
	}
	
	/**
	 * Show toast
	 * @param id
	 */
	public void showToast(int id) {
		String str = contextfa.getString(id);
		Toast.makeText(contextfa, str, Toast.LENGTH_SHORT).show();
	}
}
