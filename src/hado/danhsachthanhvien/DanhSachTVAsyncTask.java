package hado.danhsachthanhvien;

import hado.config.WebServiceAPIConfig;
import hado.database.MemberInfo;
import hado.database.MyDatabase;
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
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

/**
 * Lớp tiến trình thực hiện việc gửi thông tin của thành viên mới lên Server để kiểm tra 
 * @author Hado
 *
 */
public class DanhSachTVAsyncTask extends AsyncTask<MemberInfo, String, Void>{
	ArrayList<MemberInfo> arrMemberInfo = new ArrayList<MemberInfo>();
	Activity contextfa ;
	
	/**
	 * Lớp contructor lấy context từ lớp DanhSachTVActivity để cập nhật giao diện
	 * @param a
	 */
	public DanhSachTVAsyncTask(Activity a) {
		// TODO Auto-generated constructor stub
		this.contextfa = a ;
	}

	@Override
	protected Void doInBackground(MemberInfo... params) {
		// TODO Auto-generated method stub
		MemberInfo memberInfo = new MemberInfo();
		memberInfo = params[0];
		/*Khai báo máy con*/
		HttpClient httpClient = new DefaultHttpClient();
		/*Khai báo httpPost và gán đường link để gửi dữ liệu đến server*/
		HttpPost httpPost = new HttpPost(WebServiceAPIConfig.httpAddress);
		
		/*Tạo list dữ liệu cho httpPost*/
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		SharedPreferences pre = contextfa.getSharedPreferences("data_status", Context.MODE_PRIVATE);
		
		nameValuePair.add(new BasicNameValuePair("task", "addmember"));
		nameValuePair.add(new BasicNameValuePair("username", pre.getString("username", "")));
		nameValuePair.add(new BasicNameValuePair("password", pre.getString("password", "")));
		nameValuePair.add(new BasicNameValuePair("firstname",memberInfo.getFirstName() ));
		nameValuePair.add(new BasicNameValuePair("lastname", memberInfo.getLastName() ));
		nameValuePair.add(new BasicNameValuePair("fullname", memberInfo.getFullName() ));
		
		try {
			/*Gán list dữ liệu vào httpPost*/
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
			/*Khai báo httpResponse để yêu cầu gửi httpPost và nhận lại kết quả từ Server*/
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			/*Nhận dữ liệu từ Server*/
			InputStream inputStream = httpResponse.getEntity().getContent();
			
			if(inputStream != null){
				BufferedReader bufferedR = new BufferedReader(new InputStreamReader(inputStream));
				String line ="";
				String textJson = "";
				while ((line = bufferedR.readLine()) != null){
					textJson += line ;
				}
				inputStream.close();
				
				JSONObject jsonO = new JSONObject(textJson);
				String result = jsonO.getString("result");
				
				/*Nếu giá trị result trả về là true thì có nghĩa là không có thành viên nào trùng tên
				 * và đã update vào Server sau đó mở Database để cập nhật lại dữ liệu*/
				if(result.equalsIgnoreCase("true")){					
					refreshData(memberInfo);
					publishProgress(contextfa.getString(R.string.them_thanh_vien_thanh_cong));
				} else {
					publishProgress(contextfa.getString(R.string.fail_coloixayra));
				}
			}
		} catch (Exception e) {
			
		}
		return null;
	}
	public void refreshData(MemberInfo memberInfo){	
			MyDatabase db = new MyDatabase(contextfa);
			try {
				
				db.open();
				db.creatSimpleMember(memberInfo);
				db.close();
				
			} catch (Exception e) {
				publishProgress(contextfa.getString(R.string.fail_coloixayra));
			}
		
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		Toast.makeText(contextfa, values[0], Toast.LENGTH_SHORT).show();
		
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		contextfa.findViewById(R.id.display_progress_listmem).setVisibility(View.GONE);
	}
}
