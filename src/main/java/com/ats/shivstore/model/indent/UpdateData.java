package com.ats.shivstore.model.indent;

import java.util.List;

public class UpdateData {
	
	private int indId;
	private int sts;
	private String RejectRemark1;
	private String RejectRemark2;
	private List<RejectRemarkList> rejectRemarkList;
	public int getIndId() {
		return indId;
	}
	public void setIndId(int indId) {
		this.indId = indId;
	}
	public int getSts() {
		return sts;
	}
	public void setSts(int sts) {
		this.sts = sts;
	}
	public String getRejectRemark1() {
		return RejectRemark1;
	}
	public void setRejectRemark1(String rejectRemark1) {
		RejectRemark1 = rejectRemark1;
	}
	public String getRejectRemark2() {
		return RejectRemark2;
	}
	public void setRejectRemark2(String rejectRemark2) {
		RejectRemark2 = rejectRemark2;
	}
	public List<RejectRemarkList> getRejectRemarkList() {
		return rejectRemarkList;
	}
	public void setRejectRemarkList(List<RejectRemarkList> rejectRemarkList) {
		this.rejectRemarkList = rejectRemarkList;
	}
	@Override
	public String toString() {
		return "UpdateData [indId=" + indId + ", sts=" + sts + ", RejectRemark1=" + RejectRemark1 + ", RejectRemark2="
				+ RejectRemark2 + ", rejectRemarkList=" + rejectRemarkList + "]";
	}
	

}
