package com.ats.shivstore.model.indent;

public class RejectRemarkList {
	
	private int indDetailId;
	private String rejectRemark1;
	private String rejectRemark2;
	
	 
	public int getIndDetailId() {
		return indDetailId;
	}
	public void setIndDetailId(int indDetailId) {
		this.indDetailId = indDetailId;
	}
	public String getRejectRemark1() {
		return rejectRemark1;
	}
	public void setRejectRemark1(String rejectRemark1) {
		this.rejectRemark1 = rejectRemark1;
	}
	public String getRejectRemark2() {
		return rejectRemark2;
	}
	public void setRejectRemark2(String rejectRemark2) {
		this.rejectRemark2 = rejectRemark2;
	}
	@Override
	public String toString() {
		return "RejectRemarkList [indDetailId=" + indDetailId + ", rejectRemark1=" + rejectRemark1 + ", rejectRemark2="
				+ rejectRemark2 + "]";
	}
	
	

}
