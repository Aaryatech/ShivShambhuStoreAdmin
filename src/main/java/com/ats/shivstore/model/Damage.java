package com.ats.shivstore.model;
 

public class Damage {
	 
	private int damageId; 
	private String date; 
	private int itemId; 
	private String itemName;
	private float qty; 
	private float value; 
	private String reason; 
	private int delStatus;
	private String damageNo; 
	
	public int getDamageId() {
		return damageId;
	}
	public void setDamageId(int damageId) {
		this.damageId = damageId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public float getQty() {
		return qty;
	}
	public void setQty(float qty) {
		this.qty = qty;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public int getDelStatus() {
		return delStatus;
	}
	public void setDelStatus(int delStatus) {
		this.delStatus = delStatus;
	}
	
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public String getDamageNo() {
		return damageNo;
	}
	public void setDamageNo(String damageNo) {
		this.damageNo = damageNo;
	}
	@Override
	public String toString() {
		return "Damage [damageId=" + damageId + ", date=" + date + ", itemId=" + itemId + ", itemName=" + itemName
				+ ", qty=" + qty + ", value=" + value + ", reason=" + reason + ", delStatus=" + delStatus
				+ ", damageNo=" + damageNo + "]";
	}
	
	

}
