package com.ats.shivstore.model.indent;

import java.util.Date;


public class GetIndent {

	private int indMId;
	
	private String indMNo;
	
	private String indMDate;

	private int indMType;
	
	private String indRemark;
	
	private String indIsdev;
	
	private int indIsmonthly;

	private String catDesc;
	
	private int indMStatus;
	
	//
	
	private int achdId;
	
	private String accHeadDesc;
	
	private int catId;
	
	private String deptCode; 
	private String subDeptCode;
	private String apprvRemark1; 
	private String apprvRemark2;
	
	public int getCatId() {
		return catId;
	}

	public void setCatId(int catId) {
		this.catId = catId;
	}

	public String getAccHeadDesc() {
		return accHeadDesc;
	}

	public void setAccHeadDesc(String accHeadDesc) {
		this.accHeadDesc = accHeadDesc;
	}

	public int getIndMId() {
		return indMId;
	}

	public String getIndMNo() {
		return indMNo;
	}

	

	public int getIndMType() {
		return indMType;
	}

	public String getIndIsdev() {
		return indIsdev;
	}

	public int getIndIsmonthly() {
		return indIsmonthly;
	}

	public String getCatDesc() {
		return catDesc;
	}

	public int getIndMStatus() {
		return indMStatus;
	}

	public void setIndMId(int indMId) {
		this.indMId = indMId;
	}

	public void setIndMNo(String indMNo) {
		this.indMNo = indMNo;
	}

	
	public String getIndMDate() {
		return indMDate;
	}

	public void setIndMDate(String indMDate) {
		this.indMDate = indMDate;
	}

	public void setIndMType(int indMType) {
		this.indMType = indMType;
	}

	public void setIndIsdev(String indIsdev) {
		this.indIsdev = indIsdev;
	}

	public void setIndIsmonthly(int indIsmonthly) {
		this.indIsmonthly = indIsmonthly;
	}

	public void setCatDesc(String catDesc) {
		this.catDesc = catDesc;
	}

	public void setIndMStatus(int indMStatus) {
		this.indMStatus = indMStatus;
	}

	public int getAchdId() {
		return achdId;
	}

	public void setAchdId(int achdId) {
		this.achdId = achdId;
	}
	private int deptId;
	private int subDeptId;



	public int getDeptId() {
		return deptId;
	}

	public int getSubDeptId() {
		return subDeptId;
	}

	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}

	public void setSubDeptId(int subDeptId) {
		this.subDeptId = subDeptId;
	}

	public String getIndRemark() {
		return indRemark;
	}

	public void setIndRemark(String indRemark) {
		this.indRemark = indRemark;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public String getSubDeptCode() {
		return subDeptCode;
	}

	public void setSubDeptCode(String subDeptCode) {
		this.subDeptCode = subDeptCode;
	}

	public String getApprvRemark1() {
		return apprvRemark1;
	}

	public void setApprvRemark1(String apprvRemark1) {
		this.apprvRemark1 = apprvRemark1;
	}

	public String getApprvRemark2() {
		return apprvRemark2;
	}

	public void setApprvRemark2(String apprvRemark2) {
		this.apprvRemark2 = apprvRemark2;
	}

	@Override
	public String toString() {
		return "GetIndent [indMId=" + indMId + ", indMNo=" + indMNo + ", indMDate=" + indMDate + ", indMType="
				+ indMType + ", indRemark=" + indRemark + ", indIsdev=" + indIsdev + ", indIsmonthly=" + indIsmonthly
				+ ", catDesc=" + catDesc + ", indMStatus=" + indMStatus + ", achdId=" + achdId + ", accHeadDesc="
				+ accHeadDesc + ", catId=" + catId + ", deptCode=" + deptCode + ", subDeptCode=" + subDeptCode
				+ ", apprvRemark1=" + apprvRemark1 + ", apprvRemark2=" + apprvRemark2 + ", deptId=" + deptId
				+ ", subDeptId=" + subDeptId + "]";
	}
	
}
