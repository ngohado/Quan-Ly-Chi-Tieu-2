package hado.database;

/**
 * Lớp cung cấp các họ ,tên và họ tên đầy đủ cùng với thông tin chi tiết của thành
 * viên trong 1 tuần nào đó
 * @author Hado
 *
 */
public class MemberInfo {
	
	private String lastName ="";
	private String firstName ="";
	private String fullName ="";
	private String id = "" ;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private MemberDetail memberDtail = new MemberDetail() ;
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public MemberDetail getMemberDtail() {
		return memberDtail;
	}
	public void setMemberDtail(MemberDetail memberDtail) {
		this.memberDtail = memberDtail;
	}
}
