package com.ats.shivstore.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.ats.shivstore.common.Constants;
import com.ats.shivstore.common.DateConvertor;
import com.ats.shivstore.model.GetMrnDetailRej;
import com.ats.shivstore.model.IssueDetail;
import com.ats.shivstore.model.IssueHeader;
import com.ats.shivstore.model.bom.GetReqBomDetail;
import com.ats.shivstore.model.bom.GetReqBomHeader;
import com.ats.shivstore.model.bom.ReqBomDetail;
import com.ats.shivstore.model.bom.ReqBomHeader;
import com.ats.shivstore.model.doc.DocumentBean;
import com.ats.shivstore.model.doc.SubDocument;
import com.ats.shivstore.model.mrn.MrnDetail;
import com.ats.shivstore.model.user.User;
@Controller
public class BomContrller {
	
	RestTemplate rest = new RestTemplate();
	String fromDate, toDate;
	List<GetReqBomHeader> bomHeadList;
	
	@RequestMapping(value = "/showBOMReqests", method = RequestMethod.GET)
	public ModelAndView showBOMReqests(HttpServletRequest request, HttpServletResponse response) {
		
		ModelAndView model = null;
		try {

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			// String fromDate,toDate;

			if (request.getParameter("from_date") == null || request.getParameter("to_date") == null) {
				Date date = new Date();
				DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
				fromDate = df.format(date);
				toDate = df.format(date);
				System.out.println("From Date And :" + fromDate + "ToDATE" + toDate);

				map.add("fromDate", DateConvertor.convertToYMD(fromDate));
				map.add("toDate", DateConvertor.convertToYMD(toDate));

				System.out.println("inside if ");
			} else {
				fromDate = request.getParameter("from_date");
				toDate = request.getParameter("to_date");

				System.out.println("inside Else ");

				System.out.println("fromDate " + fromDate);

				System.out.println("toDate " + toDate);

				map.add("fromDate", DateConvertor.convertToYMD(fromDate));
				map.add("toDate", DateConvertor.convertToYMD(toDate));

			}

			model = new ModelAndView("bom/show_bom_req");
			GetReqBomHeader[] reqHeadArray = rest.postForObject(Constants.url + "/getReqBomHeaders", map, GetReqBomHeader[].class);

			bomHeadList = new ArrayList<GetReqBomHeader>(Arrays.asList(reqHeadArray));

			System.out.println("bomHeadList List using /showBOMReqests   " + bomHeadList.toString());

			model.addObject("bomHeadList", bomHeadList);
			model.addObject("fromDate", fromDate);
			model.addObject("toDate", toDate);
			

		} catch (Exception e) {

			System.err.println("Exception in showBOMReqests BomContrller" + e.getMessage());
			e.printStackTrace();
		}

		return model;	
	}
	GetReqBomHeader bomHeader;
	//getReqBomDetail
	List<GetReqBomDetail> bomDetailList;
	@RequestMapping(value = "/getReqBomDetail/{bomReqId}", method = RequestMethod.GET)
	public ModelAndView getReqBomDetail(HttpServletRequest request, HttpServletResponse response,@PathVariable int bomReqId) {
		
		ModelAndView model = null;
		try {
			model = new ModelAndView("bom/show_bom_detail");
			bomHeader=new GetReqBomHeader();
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			map.add("bomReqId", bomReqId);

			GetReqBomDetail[] reqDetailArray = rest.postForObject(Constants.url + "/getReqBomDetails", map, GetReqBomDetail[].class);

			bomDetailList = new ArrayList<GetReqBomDetail>(Arrays.asList(reqDetailArray));

			System.out.println("bomDetailList List using /showBOMReqests   " + bomDetailList.toString());

			model.addObject("bomDetailList", bomDetailList);
			
			 bomHeader = rest.postForObject(Constants.url + "/getReqBomHeaderByReqId", map, GetReqBomHeader.class);

			model.addObject("bomHeader", bomHeader);
			
			
		} catch (Exception e) {

			System.err.println("Exception in showBOMReqests BomContrller" + e.getMessage());
			e.printStackTrace();
		}

		return model;	
	}
	
	List<GetMrnDetailRej> mrnBatchList = null;
	@RequestMapping(value = "/getMrnDetailBatchForIssue", method = RequestMethod.GET)
	public @ResponseBody List<GetMrnDetailRej> getMrnDetailBatchForIssue(HttpServletRequest request, HttpServletResponse response) {
		
		try {

			int rmId=Integer.parseInt(request.getParameter("rmId"));
			
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			map.add("rmId", rmId);

			GetMrnDetailRej[] batchArray = rest.postForObject(Constants.url + "/getMrnDetailBatchForIssue", map, GetMrnDetailRej[].class);

			 mrnBatchList = new ArrayList<GetMrnDetailRej>(Arrays.asList(batchArray));

			System.out.println("mrnBatchList List using /getMrnDetailBatchForIssue   " + mrnBatchList.toString());
			
		} catch (Exception e) {

			System.err.println("Exception in getMrnDetailBatchForIssue BomContrller" + e.getMessage());
			e.printStackTrace();
		}

		return mrnBatchList;	
	}
	
	//submit auto issue for bom request
	
	//insertIssueFromBomReq
	@RequestMapping(value = "/insertIssueFromBomReq", method = RequestMethod.POST)
	public String insertIssueFromBomReq(HttpServletRequest request, HttpServletResponse response) {
		
		ModelAndView model = null;
		try {
			model = new ModelAndView("bom/show_bom_detail");
			List<ReqBomDetail> updateBomList;
			List<MrnDetail> updateMrnDetail = new ArrayList<MrnDetail>();

			System.out.println("bomDetailList List using /showBOMReqests   " + bomDetailList.toString());
			 List<IssueDetail> issueDetailList=new ArrayList<>();
			 
			 String getBomList = new String();

			 ReqBomHeader bomHeadUpdate=new ReqBomHeader();
			 
			 bomHeadUpdate.setApprovedDate(new SimpleDateFormat("yyyy-MM-dd").format(new  Date()));
			 HttpSession session =request.getSession();
					 
			 User user=(User) session.getAttribute("userInfo");
					 
			 bomHeadUpdate.setApprovedUserId(user.getUserId());
			 bomHeadUpdate.setBomReqDate(DateConvertor.convertToYMD(bomHeader.getBomReqDate()));
			 bomHeadUpdate.setBomReqId(bomHeader.getBomReqId());
			 bomHeadUpdate.setDelStatus(1);
			 bomHeadUpdate.setExVar1("NA");
			 bomHeadUpdate.setExVar2("NA");
			 bomHeadUpdate.setIsManual(bomHeader.getIsManual());
			 bomHeadUpdate.setPlantId(bomHeader.getPlantId());
			 bomHeadUpdate.setProductionDate(DateConvertor.convertToYMD(bomHeader.getProductionDate()));
			 bomHeadUpdate.setProductionId(bomHeader.getProductionId());
			 bomHeadUpdate.setSenderUserId(bomHeader.getSenderUserId());
			 bomHeadUpdate.setStatus(3);
			 bomHeadUpdate.setSubPlantId(bomHeader.getSubPlantId());
			 
						 
			for(int i=0;i<bomDetailList.size();i++) {
				
				float issueQty=Float.parseFloat(request.getParameter("issueQty"+bomDetailList.get(i).getRmId()));	
				int mrnDetailId=Integer.parseInt(request.getParameter("mrnBatch"+bomDetailList.get(i).getRmId()));
				
				String mrnBatchName=request.getParameter("mrnBatchName"+bomDetailList.get(i).getRmId());
				
				System.err.println("Issue Qty " +issueQty + "mrn Batch Id  "+mrnDetailId);
				IssueDetail isueDet=new   IssueDetail();
				isueDet.setItemIssueQty(issueQty);
				isueDet.setItemId(bomDetailList.get(i).getRmId());
				
				isueDet.setItemGroupId(bomDetailList.get(i).getGrpId());
				isueDet.setDeptId(bomHeader.getPlantId());
				isueDet.setSubDeptId(bomHeader.getSubPlantId());
				isueDet.setAccHead(0);
				isueDet.setItemId(bomDetailList.get(i).getRmId());
				isueDet.setItemRequestQty(1);
				isueDet.setItemPendingQty(1);
				
				isueDet.setStatus(0);
				isueDet.setDelStatus(1);
				isueDet.setBatchNo(mrnBatchName);
				isueDet.setMrnDetailId(mrnDetailId);
				
				issueDetailList.add(isueDet);
				
				getBomList = getBomList+","+bomDetailList.get(i).getBomReqDetailId(); 

			}
			 getBomList = getBomList.substring(1, getBomList.length());
			 
			 
			 MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
			 map.add("bomDIdList", getBomList);
			 ReqBomDetail[] bomDArray = rest.postForObject(Constants.url + "/getBomDetListByBomDIdList", map,
					 ReqBomDetail[].class);
			 
			 updateBomList = new ArrayList<ReqBomDetail>(Arrays.asList(bomDArray));
			 

			 IssueHeader issueHeader = new IssueHeader();
			 
			 issueHeader.setAccHead(1);
			 issueHeader.setDeleteStatus(1);
			 issueHeader.setDeptId(bomHeader.getPlantId());
			 issueHeader.setIssueDate(new SimpleDateFormat("yyyy-MM-dd").format(new  Date()));
			 issueHeader.setIssueSlipNo(""+bomHeader.getBomReqId());
			 issueHeader.setItemCategory(0);
			 issueHeader.setStatus(1);
			 issueHeader.setSubDeptId(bomHeader.getSubPlantId());
			 
     		 issueHeader.setIssueDetailList(issueDetailList);
     		 
     		 System.err.println("Issue Table " +issueHeader.toString());

     		 DocumentBean docBean=null;
				try {
					
					map = new LinkedMultiValueMap<String, Object>();
					map.add("docId",6);
					map.add("catId", 1);
					map.add("date", issueHeader.getIssueDate());
					map.add("typeId", 1);
					RestTemplate restTemplate = new RestTemplate();

					 docBean = restTemplate.postForObject(Constants.url + "getDocumentData", map, DocumentBean.class);
					String indMNo=docBean.getSubDocument().getCategoryPrefix()+"";
					int counter=docBean.getSubDocument().getCounter();
					int counterLenth = String.valueOf(counter).length();
					counterLenth =4 - counterLenth;
					StringBuilder code = new StringBuilder(indMNo+"");

					for (int i = 0; i < counterLenth; i++) {
						String j = "0";
						code.append(j);
					}
					code.append(String.valueOf(counter));
					
					 issueHeader.setIssueNo(""+code);
					
					docBean.getSubDocument().setCounter(docBean.getSubDocument().getCounter()+1);
				}catch (Exception e) {
					e.printStackTrace();
					 issueHeader.setIssueNo("1");
				}
				
				
				 String mrnDetailList = new String();

				 for(int i=0 ; i<issueDetailList.size() ; i++)
				 {
					 mrnDetailList = mrnDetailList+","+issueDetailList.get(i).getMrnDetailId(); 
				 }
	  
				 mrnDetailList = mrnDetailList.substring(1, mrnDetailList.length());
				 
				  map = new LinkedMultiValueMap<>();
				 map.add("mrnDetailList", mrnDetailList);
				 MrnDetail[] MrnDetail = rest.postForObject(Constants.url + "/getMrnDetailListByMrnDetailId", map,
						 MrnDetail[].class);
				 
				 updateMrnDetail = new ArrayList<MrnDetail>(Arrays.asList(MrnDetail));
				 
				 for(int j=0 ; j<issueDetailList.size() ; j++)
				 {
					 for(int i=0 ; i<updateMrnDetail.size() ; i++)
					 { 
						 if(updateMrnDetail.get(i).getMrnDetailId()==issueDetailList.get(j).getMrnDetailId())
						 {
							 updateMrnDetail.get(i).setRemainingQty(updateMrnDetail.get(i).getRemainingQty()-issueDetailList.get(j).getItemIssueQty());
							 updateMrnDetail.get(i).setIssueQty(updateMrnDetail.get(i).getIssueQty()+issueDetailList.get(j).getItemIssueQty());
						 }
					 }
					 
					 for( int i=0;i<updateBomList.size();i++) {
						 
						 if(updateBomList.get(i).getRmId()==issueDetailList.get(j).getItemId())
						 {
							 updateBomList.get(i).setRmIssueQty(issueDetailList.get(j).getItemIssueQty());
							 updateBomList.get(i).setStatus(3);
						 }
						 
					 }
				 }
				 bomHeadUpdate.setReqBomDetailsList(updateBomList);
				 
				  IssueHeader res = rest.postForObject(Constants.url + "/saveIssueHeaderAndDetail", issueHeader,
							IssueHeader.class);
					 if(res!=null)
			          {
			        		try {
			        			
			        			SubDocument subDocRes = rest.postForObject(Constants.url + "/saveSubDoc", docBean.getSubDocument(), SubDocument.class);

			        		
			        		}catch (Exception e) {
								e.printStackTrace();
							}
			        		
			        		 MrnDetail[] update = rest.postForObject(Constants.url + "/updateMrnDetailList", updateMrnDetail,
			    					 MrnDetail[].class);
			        		
			        		 ReqBomHeader bomUpdate = rest.postForObject(Constants.url + "/saveBomHeaderDetail", bomHeadUpdate,
			        				 ReqBomHeader.class);
			        		  
			          }
					System.out.println(res); 
		} catch (Exception e) {

			System.err.println("Exception in showBOMReqests BomContrller" + e.getMessage());
			e.printStackTrace();
		}

		return "redirect:/getReqBomDetail/"+22;
	}
	
}
