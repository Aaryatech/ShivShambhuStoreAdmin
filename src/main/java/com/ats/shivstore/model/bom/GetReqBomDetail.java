package com.ats.shivstore.model.bom;


public class GetReqBomDetail {
	/*
	 * SELECT
	 * t_req_bom_detail.bom_req_detail_id,t_req_bom_detail.bom_req_id,t_req_bom_detail.
	 * rm_id,t_req_bom_detail.auto_rm_req_qty,
	 * t_req_bom_detail.rm_req_qty,t_req_bom_detail.rm_issue_qty,t_req_bom_detail.
	 * status,
	 * m_item.item_code,m_item.item_desc,m_item.item_uom,m_item.cat_id,m_item.grp_id
	 * ,m_item.sub_grp_id,m_uom_store.uom as rm_uom_name FROM
	 * t_req_bom_detail,m_item,m_uom_store WHERE t_req_bom_detail.bom_req_id=20 AND
	 * t_req_bom_detail.rm_id=m_item.item_id AND m_uom_store.uom_id=m_item.item_uom2
	 * AND m_item.is_used=1 AND t_req_bom_detail.del_status=1 AND
	 * m_uom_store.is_used=1
	 */
	
	private int bomReqDetailId;
	
	private int bomReqId;
	private int rmId;//rm Id ie m_item.itemId
	private float autoRmReqQty;
	private float rmReqQty;
	private float rmIssueQty;
	private int status;
	private String itemCode;//rm Code
	private String itemDesc;// rm Name
	
	private String itemUom;
	
	private int catId;
	private int grpId;
	private int subGrpId;
	
	private String rmUomName;

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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemDesc() {
		return itemDesc;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	public String getItemUom() {
		return itemUom;
	}

	public void setItemUom(String itemUom) {
		this.itemUom = itemUom;
	}

	public int getCatId() {
		return catId;
	}

	public void setCatId(int catId) {
		this.catId = catId;
	}

	public int getGrpId() {
		return grpId;
	}

	public void setGrpId(int grpId) {
		this.grpId = grpId;
	}

	public int getSubGrpId() {
		return subGrpId;
	}

	public void setSubGrpId(int subGrpId) {
		this.subGrpId = subGrpId;
	}

	public String getRmUomName() {
		return rmUomName;
	}

	public void setRmUomName(String rmUomName) {
		this.rmUomName = rmUomName;
	}

	@Override
	public String toString() {
		return "GetReqBomDetail [bomReqDetailId=" + bomReqDetailId + ", bomReqId=" + bomReqId + ", rmId=" + rmId
				+ ", autoRmReqQty=" + autoRmReqQty + ", rmReqQty=" + rmReqQty + ", rmIssueQty=" + rmIssueQty
				+ ", status=" + status + ", itemCode=" + itemCode + ", itemDesc=" + itemDesc + ", itemUom=" + itemUom
				+ ", catId=" + catId + ", grpId=" + grpId + ", subGrpId=" + subGrpId + ", rmUomName=" + rmUomName + "]";
	}
	
}
