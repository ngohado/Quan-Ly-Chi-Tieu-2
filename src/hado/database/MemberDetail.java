package hado.database;

/**
 * Lớp cung cấp thông tin chi tiết của mỗi thành viên về số tiền đã bỏ ra,
 * số tiền đã sử dụng ,số tiền phải trả và số tiền được nhận trong 1 tuần
 * nào đó
 * @author Hado
 *
 */
public class MemberDetail {
	private float moneyBought = 0;
	private float moneyUsed = 0;
	private float pay = 0;
	private float receive = 0;
	private String week ;
	private String fullName ="";
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public float getMoneyBought() {
		return moneyBought;
	}
	public void setMoneyBought(float moneyBought) {
		this.moneyBought = moneyBought;
	}
	public float getMoneyUsed() {
		return moneyUsed;
	}
	public void setMoneyUsed(float moneyUsed) {
		this.moneyUsed = moneyUsed;
	}
	public float getPay() {		
		return pay;
	}
	public void setPay(float pay) {
		this.pay = pay;
	}
	public float getReceive() {
		return receive;
	}
	public void setReceive(float receive) {
		this.receive = receive;
	}
}
