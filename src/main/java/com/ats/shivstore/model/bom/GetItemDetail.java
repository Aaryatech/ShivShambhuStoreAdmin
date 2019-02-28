package com.ats.shivstore.model.bom;


public class GetItemDetail {

	private int itemDetailId;

	private int fgItemId;
	
	private int rmId;
	private int rmUomId;
	
	private String rmItemCode;
	private String rmName;
	private String rmUomName;
	
	private float rmQty;
	
	private float itemRmQty;

	public float getItemRmQty() {
		return itemRmQty;
	}
	public void setItemRmQty(float itemRmQty) {
		this.itemRmQty = itemRmQty;
	}

	private int	catId;
	private String catDesc;
	
	public int getItemDetailId() {
		return itemDetailId;
	}
	public void setItemDetailId(int itemDetailId) {
		this.itemDetailId = itemDetailId;
	}
	public int getFgItemId() {
		return fgItemId;
	}
	public void setFgItemId(int fgItemId) {
		this.fgItemId = fgItemId;
	}
	public int getRmId() {
		return rmId;
	}
	public void setRmId(int rmId) {
		this.rmId = rmId;
	}
	
	public int getRmUomId() {
		return rmUomId;
	}
	public void setRmUomId(int rmUomId) {
		this.rmUomId = rmUomId;
	}
	public float getRmQty() {
		return rmQty;
	}
	public void setRmQty(float rmQty) {
		this.rmQty = rmQty;
	}
	public String getRmItemCode() {
		return rmItemCode;
	}
	public void setRmItemCode(String rmItemCode) {
		this.rmItemCode = rmItemCode;
	}
	public String getRmName() {
		return rmName;
	}
	public void setRmName(String rmName) {
		this.rmName = rmName;
	}
	public String getRmUomName() {
		return rmUomName;
	}
	public void setRmUomName(String rmUomName) {
		this.rmUomName = rmUomName;
	}
	public int getCatId() {
		return catId;
	}
	public void setCatId(int catId) {
		this.catId = catId;
	}
	public String getCatDesc() {
		return catDesc;
	}
	public void setCatDesc(String catDesc) {
		this.catDesc = catDesc;
	}
	
	@Override
	public String toString() {
		return "GetItemDetail [itemDetailId=" + itemDetailId + ", fgItemId=" + fgItemId + ", rmId=" + rmId
				+ ", rmUomId=" + rmUomId + ", rmItemCode=" + rmItemCode + ", rmName=" + rmName + ", rmUomName="
				+ rmUomName + ", rmQty=" + rmQty + ", itemRmQty=" + itemRmQty + ", catId=" + catId + ", catDesc="
				+ catDesc + "]";
	}
	
	

	
}
