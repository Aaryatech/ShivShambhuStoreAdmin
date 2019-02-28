package com.ats.shivstore.model.bom;

import java.util.List;

public class ReqBomHeader {
	
	private int bomReqId;
	
	private String bomReqDate;
	
	private int productionId;
	
	private String  productionDate ;
	
	private int plantId;
	private int subPlantId;
	
	private int senderUserId;
	private int approvedUserId;
	
	private String  approvedDate ;
	
	private int isManual;
	
	private int status;
	private int delStatus;
	
	private int exInt1;
	private int exInt2;

	private String exVar1;
	private String exVar2;
	
	private int exBool1;
	
	List<ReqBomDetail>  reqBomDetailsList;
	
	public List<ReqBomDetail> getReqBomDetailsList() {
		return reqBomDetailsList;
	}

	public void setReqBomDetailsList(List<ReqBomDetail> reqBomDetailsList) {
		this.reqBomDetailsList = reqBomDetailsList;
	}

	public int getBomReqId() {
		return bomReqId;
	}

	public void setBomReqId(int bomReqId) {
		this.bomReqId = bomReqId;
	}

	public String getBomReqDate() {
		return bomReqDate;
	}

	public void setBomReqDate(String bomReqDate) {
		this.bomReqDate = bomReqDate;
	}

	public int getProductionId() {
		return productionId;
	}

	public void setProductionId(int productionId) {
		this.productionId = productionId;
	}

	public String getProductionDate() {
		return productionDate;
	}

	public void setProductionDate(String productionDate) {
		this.productionDate = productionDate;
	}

	public int getPlantId() {
		return plantId;
	}

	public void setPlantId(int plantId) {
		this.plantId = plantId;
	}

	public int getSubPlantId() {
		return subPlantId;
	}

	public void setSubPlantId(int subPlantId) {
		this.subPlantId = subPlantId;
	}

	public int getSenderUserId() {
		return senderUserId;
	}

	public void setSenderUserId(int senderUserId) {
		this.senderUserId = senderUserId;
	}

	public int getApprovedUserId() {
		return approvedUserId;
	}

	public void setApprovedUserId(int approvedUserId) {
		this.approvedUserId = approvedUserId;
	}

	public String getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(String approvedDate) {
		this.approvedDate = approvedDate;
	}

	public int getIsManual() {
		return isManual;
	}

	public void setIsManual(int isManual) {
		this.isManual = isManual;
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

	public int getExBool1() {
		return exBool1;
	}

	public void setExBool1(int exBool1) {
		this.exBool1 = exBool1;
	}

	@Override
	public String toString() {
		return "ReqBomHeader [bomReqId=" + bomReqId + ", bomReqDate=" + bomReqDate + ", productionId=" + productionId
				+ ", productionDate=" + productionDate + ", plantId=" + plantId + ", subPlantId=" + subPlantId
				+ ", senderUserId=" + senderUserId + ", approvedUserId=" + approvedUserId + ", approvedDate="
				+ approvedDate + ", isManual=" + isManual + ", status=" + status + ", delStatus=" + delStatus
				+ ", exInt1=" + exInt1 + ", exInt2=" + exInt2 + ", exVar1=" + exVar1 + ", exVar2=" + exVar2
				+ ", exBool1=" + exBool1 + ", reqBomDetailsList=" + reqBomDetailsList + "]";
	}
	
	
	
	
}
