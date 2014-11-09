package hado.dangki;

import hado.config.WebServiceAPIConfig;
import hado.quanlychitieu.R;

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
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

/**
 * Lớp xử lý việc gửi thông tin đăng kí lên Server để kiểm tra xem có đăng kí thành công không trên
 * 1 tiến trình khác
 * @author Hado
 *
 */
public class DangKiAsyncTask extends AsyncTask<String, String, Void>{
	Activity contexfa ;
	String error = "" ;
	public DangKiAsyncTask(Activity a) {
		contexfa = a ;
	}
	
	@Override
	protected Void doInBackground(String... params) {
		/*Lấy string username truyền vào*/
		String strRoomName = params[0];
		String strUsername = params[1];
		String strPassword = params[2];
		String strEmail = params[3];
		String strDescribe = params[4];
		String strMode = params[5];
		int intMode ;
		if(strMode.equalsIgnoreCase(contexfa.getString(R.string.spiner_congkhai))){
			intMode = 1 ;
		} else {
			intMode = 0 ;
		}
		
		publishProgress("loading");
		/*Khởi tạo 1 máy con*/
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpPost httpPost = new HttpPost(WebServiceAPIConfig.httpAddress);
		
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("task", "register"));
		nameValuePair.add(new BasicNameValuePair("roomname", strRoomName));
		nameValuePair.add(new BasicNameValuePair("username",strUsername));
		nameValuePair.add(new BasicNameValuePair("password", strPassword));
		nameValuePair.add(new BasicNameValuePair("email", strEmail));
		nameValuePair.add(new BasicNameValuePair("description", strDescribe));
		nameValuePair.add(new BasicNameValuePair("mode", ""+intMode));
		
		try {
			
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			/* Lấy dữ liệu trả về từ Server */
			InputStream inputStream = httpResponse.getEntity().getContent();
			
			/*
			 * Chuyển dữ liệu kiểu inputstream sang kiểu String .Nếu khác Null
			 * thì tiếp tục ,nếu bằng Null thì thông báo với người dùng lên
			 * Toast
			 */
			if(inputStream != null){
				BufferedReader bufferedR = new BufferedReader(new InputStreamReader(inputStream));
				String line = "";
				String textJson = "";
				/* Đọc lần lượt tất cả các dòng rồi gán vào textJson */
				while ((line = bufferedR.readLine()) != null ){
					textJson += line ;
				}
				inputStream.close();
				/*Lấy kết quả trả về từ Server*/
				JSONObject jsonO = new JSONObject(textJson);
				String result = jsonO.getString("result");
				error = jsonO.getString("error");
				publishProgress(result,error);
			} else {
				publishProgress(contexfa.getString(R.string.fail_coloixayra));
			}
			
		} catch (Exception e) {
			publishProgress(contexfa.getString(R.string.fail_coloixayra));
		}
				
		return null;
	}
	/**
	 * Phương thức update giao diện cho Activity
	 */
	@Override
	protected void onProgressUpdate(String... values) {
		/*Nếu result = successful thì thông báo đăng kí thành công và đóng activity đăng ký ,nếu không thì là trùng tên đăng nhập*/
		if(values[0].equalsIgnoreCase("true")){
			contexfa.findViewById(R.id.display_progress).setVisibility(View.GONE);
			showToast(contexfa.getString(R.string.successful_dangki));
			contexfa.finish();
		}
		if(values[0].equalsIgnoreCase("false")){
			showToastShort(error);
			contexfa.findViewById(R.id.display_progress).setVisibility(View.GONE);
		}
		if(values[0].equalsIgnoreCase("loading")){
			showToast(contexfa.getString(R.string.loading_string));
			contexfa.findViewById(R.id.display_progress).setVisibility(View.VISIBLE);
		}
	}
	/**
	 * Phương thức hiển thị thông báo cho người dung lên Toast
	 */
	public void showToast(String str){
		Toast.makeText(contexfa, str, Toast.LENGTH_LONG).show();
	}
	public void showToastShort(String str) {
		Toast.makeText(contexfa, str, Toast.LENGTH_LONG).show();
	}
}
