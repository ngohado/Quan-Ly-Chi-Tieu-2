package hado.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Nơi nhập xuất dự liệu trên Database
 * 
 * @author Hado
 * 
 */
public class MyDatabase {
	private static final String TABLE_INVOICE = "INVOICE";
	private static final String TABLE_MEMBER = "MEMBER";
	private static final String TABLE_MEMBERDETAIL = "MEMBERDETAIL";

	private static final String COLUM_STT = "STT";
	private static final String COLUM_WEEK = "WEEK";
	private static final String COLUM_FIRSTNAME = "FIRSTNAME";
	private static final String COLUM_LASTNAME = "LASTNAME";
	private static final String COLUM_FULLNAME = "FULLNAME";
	private static final String COLUM_MEMBERBOUGHT = "MEMBERBOUGHT";
	private static final String COLUM_TIMEBOUGHT = "TIMEBOUGHT";
	private static final String COLUM_MONEY = "MONEY";
	private static final String COLUM_MEMBERLQ = "MEMBERLQ";
	private static final String COLUM_MONEYBOUGHT = "MONEYBOUGHT";
	private static final String COLUM_MONEYUSED = "MONEYUSED";
	private static final String COLUM_DESCRIPTION = "DESCRIPTION";


	private static Context contextfa ;
	private SQLiteDatabase db ;
	private OpenHelper openHelper ;
	/**
	 * Contructor cho Mydatabase ,truyền vào Context
	 * @param c
	 */
	public MyDatabase(Context c) {
		// TODO Auto-generated constructor stub
		contextfa = c;
	}
	/**
	 * Phương thức open dùng để mở hoặc khởi tạo database nếu chưa có và cho phép được
	 * ghi dữ liệu vào Database
	 * @return
	 * @throws SQLiteException
	 */
	public MyDatabase open() throws SQLiteException{
		/*Mở hoặc khởi tạo database*/
		openHelper = new OpenHelper(contextfa);
		/*Cho phép được ghi dữ liệu vào database*/
		db = openHelper.getWritableDatabase();
		
		return this ;
	}
	/**
	 * Phương thức dóng lại database
	 */
	public void close(){
		openHelper.close();
	}
	/*------------------------------Phần hado.danhsachthanhvien--------------------------------------*/
	/**
	 * Phương thức lấy chi thiết thông tin của 1 thành viên theo họ tên đầy đủ và tuần
	 * @param fullName
	 * @param week
	 * @return
	 */
	public ArrayList<MemberDetail> getDataMemberDetail(String week){
		ArrayList<MemberDetail> arr = new ArrayList<MemberDetail>();
		
		
		
		/*Lấy dữ liệu theo các cột trong colums ,và lọc giá trị theo 2 đối truyền vào là fullName và week*/
		String colums[] = new String[]{COLUM_WEEK,COLUM_FULLNAME,COLUM_MONEYBOUGHT,COLUM_MONEYUSED} ;
		Cursor c = db.query(TABLE_MEMBERDETAIL, colums,COLUM_WEEK+"=?", new String[]{week}, null, null, null);
		
		int rowMoneyB = c.getColumnIndex(COLUM_MONEYBOUGHT);
		int rowMoneyU = c.getColumnIndex(COLUM_MONEYUSED);
		int rowName = c.getColumnIndex(COLUM_FULLNAME);
		
		for(c.moveToFirst(); !c.isAfterLast() ; c.moveToNext()){
			MemberDetail memberDetai = new MemberDetail();
			
			String fullName = c.getString(rowName);
			float moneyBought = c.getFloat(rowMoneyB);
			float moneyUsed = c.getFloat(rowMoneyU);
			
			/*Tính toán tiền phải trả và được nhận cho thành viên*/
			if(moneyBought > moneyUsed){
				memberDetai.setPay(0);
				memberDetai.setReceive(moneyBought-moneyUsed);
			}else{
				if(moneyBought < moneyUsed){
					memberDetai.setPay(moneyUsed-moneyBought);
					memberDetai.setReceive(0);
				}else{
					memberDetai.setPay(0);
					memberDetai.setReceive(0);
				}
			}
			memberDetai.setFullName(fullName);
			memberDetai.setMoneyBought(moneyBought);
			memberDetai.setMoneyUsed(moneyUsed);
			
			arr.add(memberDetai);
		}
		
		return arr ;
	}
	
	/**
	 * Phương thức trả về danh sách thông tin của thành viên trong phòng
	 * @return
	 */
	public ArrayList<MemberInfo> getDataMember(){
		ArrayList<MemberInfo> arrMemberInfo = new ArrayList<MemberInfo>();
		/*Lấy dữ tất cả dữ liệu trong bảng thành viên*/
		String columns[] = new String[]{COLUM_FULLNAME} ;
		Cursor c = db.query(TABLE_MEMBER, columns, null, null, null, null, null);
		
		/*Gán ID của các trường trong bảng*/
		int rowFullName = c.getColumnIndex(COLUM_FULLNAME);
		
		/*Duyệt tất cả các phần tử để gán vào trong ArrayList*/
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
			MemberInfo a = new MemberInfo() ;
			
			/*Gán thông tin họ tên của thành viên*/
			String fullName = c.getString(rowFullName) ;
			a.setFullName(fullName);
					
			/*Gán từng Object MemberInfo vào ArrayList*/
			arrMemberInfo.add(a);			
		}
		
		return arrMemberInfo ;
	}
	
	/**
	 *Phương thức thêm 1 thành viên vào trong Database 
	 * @param a
	 */
	public void creatSimpleMember(MemberInfo a){
		ContentValues c = new ContentValues();
		c.put(COLUM_FIRSTNAME, a.getFirstName());
		c.put(COLUM_LASTNAME, a.getLastName());
		c.put(COLUM_FULLNAME, a.getFullName());
		
		db.insert(TABLE_MEMBER, null, c);
	}
	
	/**
	 * Phương thức thêm danh sách member vào trong database
	 * @param arr
	 */
	public void creatDataMember(ArrayList<MemberInfo> arr){
		db.delete(TABLE_MEMBER, null, null);
		for(int i = 0 ; i<arr.size() ;i++){
			MemberInfo a = new MemberInfo();
			a = arr.get(i);
			ContentValues c = new ContentValues();
			c.put(COLUM_FIRSTNAME, a.getFirstName());
			c.put(COLUM_LASTNAME, a.getLastName());
			c.put(COLUM_FULLNAME, a.getFullName());
			
			db.insert(TABLE_MEMBER, null, c);
		}
	}
	
	/**
	 * Phương thức thêm danh sách memberdetail vào database
	 * @param arr
	 */
	public void creatDataMemberDetail(ArrayList<MemberDetail> arr){
		db.delete(TABLE_MEMBERDETAIL, null, null);
		for(int i = 0 ; i< arr.size() ; i++){
			MemberDetail a = new MemberDetail();
			a = arr.get(i);
			ContentValues cv = new ContentValues();
			cv.put(COLUM_WEEK, a.getWeek());
			cv.put(COLUM_FULLNAME, a.getFullName());
			cv.put(COLUM_MONEYBOUGHT, a.getMoneyBought());
			cv.put(COLUM_MONEYUSED, a.getMoneyUsed());
			
			db.insert(TABLE_MEMBERDETAIL, null, cv);
		}
	}
	
	/**
	 * Phương thức thêm danh sách invoice vào database
	 * @param arr
	 */
	public void creatDataInvoice(ArrayList<Invoice> arr){
		db.delete(TABLE_INVOICE, null, null);
		for(int i = 0 ; i<arr.size() ; i++){
			Invoice a = new Invoice();
			a = arr.get(i);
			ContentValues cv = new ContentValues();
			cv.put(COLUM_WEEK, a.getWeek());
			cv.put(COLUM_MEMBERBOUGHT, a.getMemberBought());
			cv.put(COLUM_TIMEBOUGHT, a.getTimeBought());
			cv.put(COLUM_MONEY, a.getMoney());
			cv.put(COLUM_MEMBERLQ, a.getMemberLQ());
			cv.put(COLUM_DESCRIPTION, a.getDescription());
			
			db.insert(TABLE_INVOICE, null, cv);
		}
	}
	
	/**
	 * Phương thức thêm 1 hóa đơn vào trong database
	 * @param a
	 */
	public void creatSimpleInvoice(Invoice a){
		
		ContentValues cv = new ContentValues();
		cv.put(COLUM_WEEK, a.getWeek());
		cv.put(COLUM_MEMBERBOUGHT, a.getMemberBought());
		cv.put(COLUM_TIMEBOUGHT, a.getTimeBought());
		cv.put(COLUM_MONEY, a.getMoney());
		cv.put(COLUM_MEMBERLQ, a.getMemberLQ());
		cv.put(COLUM_DESCRIPTION, a.getDescription());
		
		db.insert(TABLE_INVOICE, null, cv);
	}
	
	/**
	 * trả về số tuần mới nhất (lớn nhất)
	 */
	public String newWeek(){
		String newWeek = "" ;
		String colums[]= new String[]{COLUM_WEEK} ;
		
		Cursor c = db.query(TABLE_MEMBERDETAIL,colums, null, null, null, null, null);
		
		int rowWeek = c.getColumnIndex(COLUM_WEEK);
		c.moveToLast();
		newWeek = c.getString(rowWeek);
		return newWeek ;
	}
	
	/**
	 * Lớp có tác dụng tạo or mở file ,tạo các bảng trong Database
	 * @author Hado
	 *
	 */
	private static class OpenHelper extends SQLiteOpenHelper {

		public OpenHelper(Context context) {
			super(context, "QL", null, 1);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase arg0) {
			// TODO Auto-generated method stub
			arg0.execSQL("CREATE TABLE " + TABLE_MEMBERDETAIL + " ("
					+ COLUM_WEEK + " TEXT, " + COLUM_FULLNAME + " TEXT, "
					+ COLUM_MONEYBOUGHT + " FLOAT, " + COLUM_MONEYUSED
					+ " FLOAT);");
			arg0.execSQL("CREATE TABLE " + TABLE_MEMBER + " (" + COLUM_STT
					+ " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , "
					+ COLUM_FIRSTNAME + " TEXT, " + COLUM_LASTNAME + " TEXT, "
					+ COLUM_FULLNAME + " TEXT);");
			arg0.execSQL("CREATE TABLE " + TABLE_INVOICE + " (" + COLUM_WEEK
					+ " TEXT, " + COLUM_MEMBERBOUGHT + " TEXT, "
					+ COLUM_TIMEBOUGHT + " TEXT, " + COLUM_MONEY
					+ " FLOAT, " + COLUM_MEMBERLQ + " TEXT, "
					+ COLUM_DESCRIPTION + " TEXT);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub

		}
	}
	
	/**
	 * Phương thức show thông tin truyền vào str lên Toast 
	 * @param str
	 */
	public void showToast(String str) {
		Toast.makeText(contextfa, str, Toast.LENGTH_LONG).show();
	}

}
