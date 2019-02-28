package com.ats.shivstore.model.bom;

public class ReqBomDetail {

	private int bomReqDetailId;

	private int bomReqId;;

	private int rmId;// ie rm Item Id

	private String uom;

	private float autoRmReqQty;
	private float rmReqQty;

	private float rmIssueQty;

	private float rejectedQty;
	private float returnQty;

	private String mrnBatch;

	private float mrnItemRate;

	private float itemValue;

	private int status;

	private int delStatus;

	private int exInt1;
	private int exInt2;

	private String exVar1;
	private String exVar2;
	private String exVar3;

	private int exBool1;
	private int exBool2;
	public int getBomReqDetailId() {
		return bomReqDetailId;
	}
	public void setBomReqDetailId(int bomReqDetailId) {
		this.bomReqDetailId = bomReqDetailId;
	}
	public int getBomReqId() {
		return bomReqId;
	}
	public void setBomReqId(int bomReqId) {
		this.bomReqId = bomReqId;
	}
	public int getRmId() {
		return rmId;
	}
	public void setRmId(int rmId) {
		this.rmId = rmId;
	}
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public float getAutoRmReqQty() {
		return autoRmReqQty;
	}
	public void setAutoRmReqQty(float autoRmReqQty) {
		this.autoRmReqQty = autoRmReqQty;
	}
	public float getRmReqQty() {
		return rmReqQty;
	}
	public void setRmReqQty(float rmReqQty) {
		this.rmReqQty = rmReqQty;
	}
	public float getRmIssueQty() {
		return rmIssueQty;
	}
	public void setRmIssueQty(float rmIssueQty) {
		this.rmIssueQty = rmIssueQty;
	}
	public float getRejectedQty() {
		return rejectedQty;
	}
	public void setRejectedQty(float rejectedQty) {
		this.rejectedQty = rejectedQty;
	}
	public float getReturnQty() {
		return returnQty;
	}
	public void setReturnQty(float returnQty) {
		this.returnQty = returnQty;
	}
	public String getMrnBatch() {
		return mrnBatch;
	}
	public void setMrnBatch(String mrnBatch) {
		this.mrnBatch = mrnBatch;
	}
	public float getMrnItemRate() {
		return mrnItemRate;
	}
	public void setMrnItemRate(float mrnItemRate) {
		this.mrnItemRate = mrnItemRate;
	}
	public float getItemValue() {
		return itemValue;
	}
	public void setItemValue(float itemValue) {
		this.itemValue = itemValue;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getDelStatus() {
		return delStatus;
	}
	public void setDelStatus(int delStatus) {
		this.delStatus = delStatus;
	}
	public int getExInt1() {
		return exInt1;
	}
	public void setExInt1(int exInt1) {
		this.exInt1 = exInt1;
	}
	public int getExInt2() {
		return exInt2;
	}
	public void setExInt2(int exInt2) {
		this.exInt2 = exInt2;
	}
	public String getExVar1() {
		return exVar1;
	}
	public void setExVar1(String exVar1) {
		this.exVar1 = exVar1;
	}
	public String getExVar2() {
		return exVar2;
	}
	public void setExVar2(String exVar2) {
		this.exVar2 = exVar2;
	}
	public String getExVar3() {
		return exVar3;
	}
	public void setExVar3(String exVar3) {
		this.exVar3 = exVar3;
	}
	public int getExBool1() {
		return exBool1;
	}
	public void setExBool1(int exBool1) {
		this.exBool1 = exBool1;
	}
	public int getExBool2() {
		return exBool2;
	}
	public void setExBool2(int exBool2) {
		this.exBool2 = exBool2;
	}
	
	
	@Override
	public String toString() {
		return "ReqBomDetail [bomReqDetailId=" + bomReqDetailId + ", bomReqId=" + bomReqId + ", rmId=" + rmId + ", uom="
				+ uom + ", autoRmReqQty=" + autoRmReqQty + ", rmReqQty=" + rmReqQty + ", rmIssueQty=" + rmIssueQty
				+ ", rejectedQty=" + rejectedQty + ", returnQty=" + returnQty + ", mrnBatch=" + mrnBatch
				+ ", mrnItemRate=" + mrnItemRate + ", itemValue=" + itemValue + ", status=" + status + ", delStatus="
				+ delStatus + ", exInt1=" + exInt1 + ", exInt2=" + exInt2 + ", exVar1=" + exVar1 + ", exVar2=" + exVar2
				+ ", exVar3=" + exVar3 + ", exBool1=" + exBool1 + ", exBool2=" + exBool2 + "]";
	}
	
	
	
	

}
