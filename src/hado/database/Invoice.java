package hado.database;

public class Invoice {
	private String week ;
	private String memberBought ="";
	private String timeBought ="";
	private float money ;
	private String memberLQ ="";
	private String description ="";
	
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getMemberBought() {
		return memberBought;
	}
	public void setMemberBought(String memberBought) {
		this.memberBought = memberBought;
	}
	public String getTimeBought() {
		return timeBought;
	}
	public void setTimeBought(String timeBought) {
		this.timeBought = timeBought;
	}
	public float getMoney() {
		return money;
	}
	public void setMoney(float money) {
		this.money = money;
	}
	public String getMemberLQ() {
		return memberLQ;
	}
	public void setMemberLQ(String memberLQ) {
		this.memberLQ = memberLQ;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
