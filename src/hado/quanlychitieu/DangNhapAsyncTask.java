package hado.quanlychitieu;

import hado.config.WebServiceAPIConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
import android.widget.TextView;
import android.widget.Toast;

/**
 * Lớp xử lý việc gửi Username và Password đến Server và nhận dữ liệu trả về
 * trên 1 tiến trình khác
 * 
 * @author Hado
 * 
 */
public class DangNhapAsyncTask extends AsyncTask<String, String, Boolean> {

	Activity contextfa ;
	/**
	 * Khai báo 1 Activity contextfa và gán trong Contructor để cập nhật diện
	 * 
	 * @param a
	 */
	public DangNhapAsyncTask(Activity a) {
		contextfa = a;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		/*
		 * Lấy dữ liệu username và password được truyền vào khi gọi đến lớp
		 * HttpAsyncTask
		 */
		String paramUserName = params[0];
		String paramPassword = params[1];

		/* Khai báo 1 máy con */
		HttpClient httpClilent = new DefaultHttpClient();

		HttpPost httpPost = new HttpPost(WebServiceAPIConfig.httpAddress);

		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("task", "login"));
		nameValuePair.add(new BasicNameValuePair("username", paramUserName));
		nameValuePair.add(new BasicNameValuePair("password", paramPassword));
		
		try {
			
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

			HttpResponse httpResponse = httpClilent.execute(httpPost);

			/* Lấy dữ liệu trả về từ Server */
			InputStream inputStream = httpResponse.getEntity().getContent();

			/*
			 * Chuyển dữ liệu kiểu inputstream sang kiểu String .Nếu khác Null
			 * thì tiếp tục ,nếu bằng Null thì thông báo với người dùng lên
			 * Toast
			 */
			if (inputStream != null) {
				BufferedReader bufferedR = new BufferedReader(
						new InputStreamReader(inputStream));
				String line = "";
				String textJson = "";
				
				/* Đọc lần lượt tất cả các dòng rồi gán vào textJson */
				while ((line = bufferedR.readLine()) != null) {
					textJson += line;
				}
				inputStream.close();

				/*
				 * Lấy kết quả trả về từ Server theo kiểu dữ liệu JSon và kiểm
				 * tra kết quả là True hay False
				 */
				JSONObject jsonO = new JSONObject(textJson);				
				String result = jsonO.getString("result");
				
				/*
				 * So sánh kết quả trả về .Nếu là True thì đóng Activity đăng
				 * nhập và trở về Menu ,nếu sai thì thông báo với người dùng lên
				 * Toast
				 */
				if (result.equals("true")) {
					try {
						SharedPreferences pre = contextfa.getSharedPreferences("data_status", Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = pre.edit();
						editor.putInt("theFirst", 0);
						editor.putString("username", paramUserName);
						editor.putString("password", paramPassword);
						editor.commit();
						
					} catch (Exception e) {
						// TODO: handle exception
					}
															
					return true ;
				} else {
					return false ;
				}

			} else {
				return false ;
			}

		} catch (Exception e) {
			return false ;
		}

	}

	/**
	 * Phương thức với đầu vào là true thì lấy dữ liệu từ server vào database 
	 * nếu sai thì thông báo lên toast và tắt layout progress
	 */
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(result){ // true
			TextView tv = (TextView) contextfa.findViewById(R.id.loading_dangnhap);
			tv.setText(contextfa.getString(R.string.dang_tai_du_lieu));
			new GetDataAsyncTask(contextfa).execute();
			onCancelled();
		}else{ // false
			
			showToastShort(contextfa.getString(R.string.fail_sai_dangnhap));
			contextfa.findViewById(R.id.display_progress_index).setVisibility(View.GONE);
		}
	}
	
	/**
	 * Phương thức dùng để hiển thị thông tin cho người dùng lên Toast
	 * 
	 * @param str
	 */
	public void showToastShort(String str) {
		Toast.makeText(contextfa, str, Toast.LENGTH_LONG).show();
	}
}
