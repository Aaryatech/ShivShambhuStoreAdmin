package com.ats.shivstore.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
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
import com.ats.shivstore.model.AccountHead;
import com.ats.shivstore.model.Dept;
import com.ats.shivstore.model.ErrorMessage;
import com.ats.shivstore.model.GetIssueDetail;
import com.ats.shivstore.model.GetIssueHeader;
import com.ats.shivstore.model.GetItemGroup;
import com.ats.shivstore.model.GetSubDept;
import com.ats.shivstore.model.GetpassHeader;
import com.ats.shivstore.model.IssueDetail;
import com.ats.shivstore.model.IssueHeader;
import com.ats.shivstore.model.StockHeader;
import com.ats.shivstore.model.Type;
import com.ats.shivstore.model.doc.DocumentBean;
import com.ats.shivstore.model.doc.SubDocument;
import com.ats.shivstore.model.indent.IndentTrans;
import com.ats.shivstore.model.item.GetItem;
import com.ats.shivstore.model.item.ItemList;
import com.ats.shivstore.model.mrn.GetMrnHeader;
import com.ats.shivstore.model.mrn.MrnDetail;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY; 

@Controller
@Scope("session")
public class IssueController {
	
	
	
RestTemplate rest = new RestTemplate();
List<IssueDetail> issueDetailList = new ArrayList<IssueDetail>();
List<MrnDetail> batchList = new ArrayList<MrnDetail>();
List<MrnDetail> updateMrnDetail = new ArrayList<MrnDetail>();

	@RequestMapping(value = "/addIssueItem", method = RequestMethod.GET)
	public ModelAndView addIssueItem(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("issue/addIssueItem");
		try {
			 updateMrnDetail = new ArrayList<MrnDetail>();
			 issueDetailList = new ArrayList<IssueDetail>();
			Dept[] Dept = rest.getForObject(Constants.url + "/getAllDeptByIsUsed", Dept[].class);
			List<Dept> deparmentList = new ArrayList<Dept>(Arrays.asList(Dept)); 
			model.addObject("deparmentList", deparmentList);
			
			GetItemGroup[] itemGroupList = rest.getForObject(Constants.url + "/getAllItemGroupByIsUsed",
					GetItemGroup[].class);
			model.addObject("itemGroupList", itemGroupList);
			
			AccountHead[] accountHead = rest.getForObject(Constants.url + "/getAllAccountHeadByIsUsed", AccountHead[].class);
			List<AccountHead> accountHeadList = new ArrayList<AccountHead>(Arrays.asList(accountHead));

			model.addObject("accountHeadList", accountHeadList);
			
			Type[] type = rest.getForObject(Constants.url + "/getAlltype", Type[].class);
			List<Type> typeList = new ArrayList<Type>(Arrays.asList(type));
			model.addObject("typeList", typeList);
			
			
			StockHeader stockHeader = rest.getForObject(Constants.url + "/getCurrentRunningMonthAndYear",StockHeader.class);
			 
				 System.out.println( " stock Date: " + stockHeader.getDate());
				 
				 model.addObject("stockDateDDMMYYYY", DateConvertor.convertToDMY(stockHeader.getDate()));
				 
				 Date date = new Date();
				 SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
				 
				 model.addObject("date", sf.format(date));
				  
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/getSubDeptList", method = RequestMethod.GET)
	@ResponseBody
	public List<GetSubDept> getSubDeptListByDeptId(HttpServletRequest request, HttpServletResponse response) {

		List<GetSubDept> subDeptList = new ArrayList<GetSubDept>();
		try {
			int deptId = Integer.parseInt(request.getParameter("deptId"));

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			map.add("deptId", deptId);
			GetSubDept[] GetSubDept = rest.postForObject(Constants.url + "getSubDeptListByDeptId", map, GetSubDept[].class);

			subDeptList = new ArrayList<>(Arrays.asList(GetSubDept));
			 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return subDeptList;
	}
	
	@RequestMapping(value = "/getBatchByItemId", method = RequestMethod.GET)
	@ResponseBody
	public List<MrnDetail> getBatchByItemId(HttpServletRequest request, HttpServletResponse response) {

		
		try {
			int itemId = Integer.parseInt(request.getParameter("itemId"));
			int type = Integer.parseInt(request.getParameter("type"));
			String date = request.getParameter("date");

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			map.add("itemId", itemId);
			map.add("date", date);
			map.add("type", type);
			MrnDetail[] MrnDetail = rest.postForObject(Constants.url + "getBatchByItemId", map, MrnDetail[].class);

			batchList = new ArrayList<MrnDetail>(Arrays.asList(MrnDetail));
			
			for(int i = 0 ; i<issueDetailList.size() ; i++)
			{
				for(int j = 0 ; j<batchList.size() ; j++)
				{
					if(batchList.get(j).getMrnDetailId()==issueDetailList.get(i).getMrnDetailId())
					{
						batchList.get(j).setRemainingQty(batchList.get(j).getRemainingQty()-issueDetailList.get(i).getItemIssueQty());
						batchList.get(j).setIssueQty(batchList.get(j).getIssueQty()+issueDetailList.get(i).getItemIssueQty());
						break;
					}
				}
			}
			 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return batchList;
	}
	
	@RequestMapping(value = "/qtyValidationFromBatch", method = RequestMethod.GET)
	@ResponseBody
	public MrnDetail qtyValidationFromBatch(HttpServletRequest request, HttpServletResponse response) {

		MrnDetail mrnDetail = new MrnDetail();
		try {
			int mrnDetailId = Integer.parseInt(request.getParameter("batchNo"));

			 for(int i = 0 ;i< batchList.size() ; i++)
			 {
				 if(batchList.get(i).getMrnDetailId()==mrnDetailId)
				 {
					 mrnDetail = batchList.get(i);
				 }
			 }
			 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return mrnDetail;
	}
	
	@RequestMapping(value = "/addItmeInIssueList", method = RequestMethod.GET)
	@ResponseBody
	public List<IssueDetail> addItmeInIssueList(HttpServletRequest request, HttpServletResponse response) {

		
		try {
			int itemId = Integer.parseInt(request.getParameter("itemId"));
			int mrnDetailId = Integer.parseInt(request.getParameter("batchNo"));
			float qty = Float.parseFloat(request.getParameter("qty"));
			int groupId = Integer.parseInt(request.getParameter("groupId"));
			int deptId = Integer.parseInt(request.getParameter("deptId"));
			int subDeptId = Integer.parseInt(request.getParameter("subDeptId"));
			int acc = Integer.parseInt(request.getParameter("acc"));
			String editIndex =  request.getParameter("editIndex") ;
			String itemName =  request.getParameter("itemName") ;
			String groupName =  request.getParameter("groupName") ;
			String deptName =  request.getParameter("deptName") ;
			String subDeptName =  request.getParameter("subDeptName") ;
			String accName =  request.getParameter("accName") ;

			 if(editIndex.equalsIgnoreCase("") || editIndex.equalsIgnoreCase(null))
			 {
				 IssueDetail issueDetail = new IssueDetail();
				 issueDetail.setItemId(itemId);
				 issueDetail.setItemIssueQty(qty);
				 issueDetail.setItemGroupId(groupId);
				 issueDetail.setDeptId(deptId);
				 issueDetail.setSubDeptId(subDeptId);
				 issueDetail.setAccHead(acc);
				 issueDetail.setItemName(itemName);
				 issueDetail.setGroupName(groupName);
				 issueDetail.setDeptName(deptName);
				 issueDetail.setSubDeptName(subDeptName);
				 issueDetail.setAccName(accName);
				 issueDetail.setDelStatus(1);
				 for(int i = 0 ;i< batchList.size() ; i++)
				 {
					 if(batchList.get(i).getMrnDetailId()==mrnDetailId)
					 {
						 issueDetail.setBatchNo(batchList.get(i).getBatchNo());
						 issueDetail.setMrnDetailId(mrnDetailId); 
					 }
				 }
				 issueDetailList.add(issueDetail);
			 }
			 else
			 {
				 int index = Integer.parseInt(editIndex);
				 issueDetailList.get(index).setItemId(itemId); 
				 issueDetailList.get(index).setItemGroupId(groupId);
				 issueDetailList.get(index).setDeptId(deptId);
				 issueDetailList.get(index).setSubDeptId(subDeptId);
				 issueDetailList.get(index).setAccHead(acc);
				 issueDetailList.get(index).setItemName(itemName);
				 issueDetailList.get(index).setGroupName(groupName);
				 issueDetailList.get(index).setDeptName(deptName);
				 issueDetailList.get(index).setSubDeptName(subDeptName);
				 issueDetailList.get(index).setAccName(accName);
				  
					 for(int i = 0 ;i< batchList.size() ; i++)
					 {
						 if(batchList.get(i).getMrnDetailId()==mrnDetailId)
						 {
							 issueDetailList.get(index).setBatchNo(batchList.get(i).getBatchNo());
							 issueDetailList.get(index).setMrnDetailId(mrnDetailId); 
						 }
					 }
			 
				 issueDetailList.get(index).setItemIssueQty(qty);
				  
			 }
			  
			 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return issueDetailList;
	}
	
	@RequestMapping(value = "/editItemInIssueList", method = RequestMethod.GET)
	@ResponseBody
	public IssueDetail editItemInIssueList(HttpServletRequest request, HttpServletResponse response) {

		IssueDetail issueDetail = new IssueDetail();
		
		try {
			int index = Integer.parseInt(request.getParameter("index"));
			 
			issueDetail = issueDetailList.get(index);
			 
			 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return issueDetail;
	}
	
	@RequestMapping(value = "/deleteItemFromIssueList", method = RequestMethod.GET)
	@ResponseBody
	public List<IssueDetail> deleteItemFromIssueList(HttpServletRequest request, HttpServletResponse response) {
 
		
		try {
			int index = Integer.parseInt(request.getParameter("index")); 
			issueDetailList.remove(index); 
			 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return issueDetailList;
	}
	
	@RequestMapping(value = "/submitIssueReciept", method = RequestMethod.POST)
	public String submitIssueReciept(HttpServletRequest request, HttpServletResponse response) {

		try {
   
			 
			String issueNo = request.getParameter("issueNo"); 
			String issueDate = request.getParameter("issueDate");
			int deptId = Integer.parseInt(request.getParameter("deptId"));
			int subDeptId = Integer.parseInt(request.getParameter("subDeptId"));
			int acc = Integer.parseInt(request.getParameter("acc"));
			int type = Integer.parseInt(request.getParameter("type"));
			String issueSlipNo = request.getParameter("issueSlipNo"); 
			
			System.out.println("issueDate " + issueDate);
			 IssueHeader issueHeader = new IssueHeader();
		 
			 issueHeader.setIssueDate(issueDate);
			 DocumentBean docBean=null;
				try {
					
					MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
					map.add("docId",6);
					map.add("catId", 1);
					map.add("date", issueDate);
					map.add("typeId", type);
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
			 //issueHeader.setIssueNo(issueNo);
			 issueHeader.setDeleteStatus(1);
			 issueHeader.setIssueDetailList(issueDetailList);
			 issueHeader.setDeptId(deptId);
			 issueHeader.setSubDeptId(subDeptId);
			 issueHeader.setAccHead(acc);
			 issueHeader.setItemCategory(type);
			 issueHeader.setIssueSlipNo(issueSlipNo);
			 
			 String mrnDetailList = new String();
			 
			 for(int i=0 ; i<issueDetailList.size() ; i++)
			 {
				 mrnDetailList = mrnDetailList+","+issueDetailList.get(i).getMrnDetailId(); 
				 issueDetailList.get(i).setDeptId(deptId);
				 issueDetailList.get(i).setSubDeptId(subDeptId);
				 issueDetailList.get(i).setAccHead(acc);
			 }
  
			 mrnDetailList = mrnDetailList.substring(1, mrnDetailList.length());
			 
			 MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
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
			 }
			 
			 System.out.println("issueHeader  " + issueHeader); 
			 
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
	          }
			System.out.println(res); 

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/issueList";
	}
	
	@RequestMapping(value = "/issueList", method = RequestMethod.GET)
	public ModelAndView issueList(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("issue/issueList");
		try {
			
			Date date = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat disply = new SimpleDateFormat("dd-MM-yyyy");
			
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			
			if(request.getParameter("fromDate")==null || request.getParameter("toDate")==null) {
				
				map.add("fromDate", sf.format(date));
				map.add("toDate", sf.format(date));
				
				GetIssueHeader[] IssueHeader = rest.postForObject(Constants.url + "/getIssueHeaderList",map, GetIssueHeader[].class);
				List<GetIssueHeader> issueHeaderList = new ArrayList<GetIssueHeader>(Arrays.asList(IssueHeader)); 
				model.addObject("issueHeaderList", issueHeaderList);
				
				model.addObject("fromDate", disply.format(date));
				model.addObject("toDate", disply.format(date));
				
			}
			else {
				
				String fromDate = request.getParameter("fromDate");
				String toDate = request.getParameter("toDate");
				map.add("fromDate", DateConvertor.convertToYMD(fromDate));
				map.add("toDate", DateConvertor.convertToYMD(toDate));
				
				GetIssueHeader[] IssueHeader = rest.postForObject(Constants.url + "/getIssueHeaderList",map, GetIssueHeader[].class);
				List<GetIssueHeader> issueHeaderList = new ArrayList<GetIssueHeader>(Arrays.asList(IssueHeader)); 
				model.addObject("issueHeaderList", issueHeaderList);
				
				model.addObject("fromDate", fromDate);
				model.addObject("toDate", toDate);
				
			}
			
			
		 

		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/getIssueDateBetweenDate", method = RequestMethod.GET)
	@ResponseBody
	public List<GetIssueHeader> getIssueDateBetweenDate(HttpServletRequest request, HttpServletResponse response) {
 
		List<GetIssueHeader> issueHeaderList = new ArrayList<>();
		
		try {
			String fromDate = request.getParameter("fromDate") ;
			String toDate = request.getParameter("toDate") ;
			
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			map.add("fromDate",DateConvertor.convertToYMD(fromDate) );
			map.add("toDate",DateConvertor.convertToYMD(toDate) );
			
			GetIssueHeader[] IssueHeader = rest.postForObject(Constants.url + "/getIssueHeaderList",map, GetIssueHeader[].class);
			 issueHeaderList = new ArrayList<GetIssueHeader>(Arrays.asList(IssueHeader)); 
			 
			 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return issueHeaderList;
	}
	
	@RequestMapping(value = "/deleteIssueHeader/{issueId}", method = RequestMethod.GET) 
	public String deleteIssueHeader(@PathVariable int issueId, HttpServletRequest request, HttpServletResponse response) {
 
		 
		
		try {
			 
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			map.add("issueId",issueId); 
			GetIssueHeader getIssueHeader = rest.postForObject(Constants.url + "/getIssueHeaderAndDetailById",map, GetIssueHeader.class); 
			 String mrnDetailList = new String();
			 
			for(int i=0 ; i<getIssueHeader.getIssueDetailList().size() ; i++)
			 {
				 mrnDetailList = mrnDetailList+","+getIssueHeader.getIssueDetailList().get(i).getMrnDetailId();
			 }
 
			 mrnDetailList = mrnDetailList.substring(1, mrnDetailList.length());
			 
			 map = new LinkedMultiValueMap<>();
			 map.add("mrnDetailList", mrnDetailList);
			 MrnDetail[] MrnDetail = rest.postForObject(Constants.url + "/getMrnDetailListByMrnDetailId", map,
					 MrnDetail[].class);
			 List<MrnDetail> updateMrnDetail = new ArrayList<>(Arrays.asList(MrnDetail));
			 
			 for(int i=0 ; i<getIssueHeader.getIssueDetailList().size() ; i++)
			 {
				 for(int j=0 ; j<updateMrnDetail.size() ; j++)
				 {
					 if(updateMrnDetail.get(j).getMrnDetailId()==getIssueHeader.getIssueDetailList().get(i).getMrnDetailId())
					 {
						 updateMrnDetail.get(j).setRemainingQty(updateMrnDetail.get(j).getRemainingQty()+getIssueHeader.getIssueDetailList().get(i).getItemIssueQty());
						 updateMrnDetail.get(j).setIssueQty(updateMrnDetail.get(j).getIssueQty()-getIssueHeader.getIssueDetailList().get(i).getItemIssueQty());
					 }
				 }
			 }
			 
			 map = new LinkedMultiValueMap<String, Object>();
			map.add("issueId",issueId); 
			ErrorMessage errorMessage = rest.postForObject(Constants.url + "/deleteHeaderAndDetail",map, ErrorMessage.class); 
			System.out.println(errorMessage);
			
			if(errorMessage.isError()==false)
			{
				MrnDetail[] update = rest.postForObject(Constants.url + "/updateMrnDetailList", updateMrnDetail,
   					 MrnDetail[].class);
				System.out.println(update);
			}
			 
			 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return "redirect:/issueList";
	}
	
	List<GetIssueDetail> issueDetailEditList = new ArrayList<>();
	List<GetIssueDetail> originalIssueDetail = new ArrayList<>();
	GetIssueHeader getIssueHeader = new GetIssueHeader();
	List<MrnDetail> batchListInEditIssue = new ArrayList<MrnDetail>();
	
	@RequestMapping(value = "/editIssueHeader/{issueId}", method = RequestMethod.GET) 
	public ModelAndView editIssueHeader(@PathVariable int issueId, HttpServletRequest request, HttpServletResponse response) {
 
		ModelAndView model = new ModelAndView("issue/editIssueItem");
		
		try {
			originalIssueDetail = new ArrayList<>();
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			map.add("issueId",issueId); 
			 getIssueHeader = rest.postForObject(Constants.url + "/getIssueHeaderAndDetailById",map, GetIssueHeader.class); 
			model.addObject("getIssueHeader", getIssueHeader);
			
			 issueDetailEditList = getIssueHeader.getIssueDetailList();
			 
			 /*for(int i = 0 ; i< issueDetailEditList.size() ; i++)
			 {
				 originalIssueDetail.add(issueDetailEditList.get(i));
			 }*/
			
			Dept[] Dept = rest.getForObject(Constants.url + "/getAllDeptByIsUsed", Dept[].class);
			List<Dept> deparmentList = new ArrayList<Dept>(Arrays.asList(Dept)); 
			model.addObject("deparmentList", deparmentList);
			
			GetItemGroup[] itemGroupList = rest.getForObject(Constants.url + "/getAllItemGroupByIsUsed",
					GetItemGroup[].class);
			model.addObject("itemGroupList", itemGroupList);
			
			AccountHead[] accountHead = rest.getForObject(Constants.url + "/getAllAccountHeadByIsUsed", AccountHead[].class);
			List<AccountHead> accountHeadList = new ArrayList<AccountHead>(Arrays.asList(accountHead));

			model.addObject("accountHeadList", accountHeadList);
			
			Type[] type = rest.getForObject(Constants.url + "/getAlltype", Type[].class);
			List<Type> typeList = new ArrayList<Type>(Arrays.asList(type));
			model.addObject("typeList", typeList);
			
			StockHeader stockHeader = rest.getForObject(Constants.url + "/getCurrentRunningMonthAndYear",StockHeader.class);
			 
			 System.out.println( " stock Date: " + stockHeader.getDate());
			 
			 model.addObject("stockDateDDMMYYYY", DateConvertor.convertToDMY(stockHeader.getDate()));
			 model.addObject("date",DateConvertor.convertToYMD(getIssueHeader.getIssueDate()));
			 
			 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return model;
	}
	
	@RequestMapping(value = "/qtyValidationFromBatchInEdit", method = RequestMethod.GET)
	@ResponseBody
	public MrnDetail qtyValidationFromBatchInEdit(HttpServletRequest request, HttpServletResponse response) {

		MrnDetail mrnDetail = new MrnDetail();
		try {
			int mrnDetailId = Integer.parseInt(request.getParameter("batchNo"));

			 for(int i = 0 ;i< batchListInEditIssue.size() ; i++)
			 {
				 if(batchListInEditIssue.get(i).getMrnDetailId()==mrnDetailId)
				 {
					 mrnDetail = batchListInEditIssue.get(i);
				 }
			 }
			 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return mrnDetail;
	}
	
	@RequestMapping(value = "/getBatchByItemIdInIssueEdit", method = RequestMethod.GET)
	@ResponseBody
	public List<MrnDetail> getBatchByItemIdInIssueEdit(HttpServletRequest request, HttpServletResponse response) {

		
		try {
			int itemId = Integer.parseInt(request.getParameter("itemId"));
			int type = Integer.parseInt(request.getParameter("type"));
			String date = request.getParameter("date") ;
			
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			map.add("itemId", itemId);
			map.add("date", date);map.add("type", type);
			MrnDetail[] MrnDetail = rest.postForObject(Constants.url + "getBatchByItemId", map, MrnDetail[].class);

			batchListInEditIssue = new ArrayList<MrnDetail>(Arrays.asList(MrnDetail));
			
			
			
			
			for(int i = 0 ; i<issueDetailEditList.size() ; i++)
			{
				for(int j = 0 ; j<batchListInEditIssue.size() ; j++)
				{	
					
					if(batchListInEditIssue.get(j).getMrnDetailId()==issueDetailEditList.get(i).getMrnDetailId() 
							&& issueDetailEditList.get(i).getIssueDetailId()==0)
					{
						System.out.println("In if ");
						batchListInEditIssue.get(j).setRemainingQty(batchListInEditIssue.get(j).getRemainingQty()-issueDetailEditList.get(i).getItemIssueQty());
						batchListInEditIssue.get(j).setIssueQty(batchListInEditIssue.get(j).getIssueQty()+issueDetailEditList.get(i).getItemIssueQty());
						break;
					}
					else if(batchListInEditIssue.get(j).getMrnDetailId()==issueDetailEditList.get(i).getMrnDetailId() 
							&& issueDetailEditList.get(i).getDelStatus()==0)
					{
						System.out.println("In if else");
						batchListInEditIssue.get(j).setRemainingQty(batchListInEditIssue.get(j).getRemainingQty()+issueDetailEditList.get(i).getItemIssueQty());
						batchListInEditIssue.get(j).setIssueQty(batchListInEditIssue.get(j).getIssueQty()-issueDetailEditList.get(i).getItemIssueQty());
						break;
					}  
				}
			}
			 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return batchListInEditIssue;
	}
	
	@RequestMapping(value = "/addItmeInEditIssueList", method = RequestMethod.GET)
	@ResponseBody
	public List<GetIssueDetail> addItmeInEditIssueList(HttpServletRequest request, HttpServletResponse response) {

		
		try {
			int itemId = Integer.parseInt(request.getParameter("itemId"));
			int mrnDetailId = Integer.parseInt(request.getParameter("batchNo"));
			float qty = Float.parseFloat(request.getParameter("qty"));
			int groupId = Integer.parseInt(request.getParameter("groupId"));
			int deptId = Integer.parseInt(request.getParameter("deptId"));
			int subDeptId = Integer.parseInt(request.getParameter("subDeptId"));
			int acc = Integer.parseInt(request.getParameter("acc"));
			String editIndex =  request.getParameter("editIndex") ;
			String itemName =  request.getParameter("itemName") ;
			String groupName =  request.getParameter("groupName") ;
			String deptName =  request.getParameter("deptName") ;
			String subDeptName =  request.getParameter("subDeptName") ;
			String accName =  request.getParameter("accName") ;

			 if(editIndex.equalsIgnoreCase("") || editIndex.equalsIgnoreCase(null))
			 {
				 GetIssueDetail issueDetail = new GetIssueDetail();
				 issueDetail.setItemId(itemId);
				 issueDetail.setItemIssueQty(qty);
				 issueDetail.setItemGroupId(groupId);
				 issueDetail.setDeptId(deptId);
				 issueDetail.setSubDeptId(subDeptId);
				 issueDetail.setAccHead(acc);
				 issueDetail.setItemCode(itemName);
				 issueDetail.setGrpCode(groupName);
				 issueDetail.setDeptCode(deptName);
				 issueDetail.setSubDeptCode(subDeptName);
				 issueDetail.setAccHeadDesc(accName);
				 issueDetail.setDelStatus(1);
				 for(int i = 0 ;i< batchListInEditIssue.size() ; i++)
				 {
					 if(batchListInEditIssue.get(i).getMrnDetailId()==mrnDetailId)
					 {
						 issueDetail.setBatchNo(batchListInEditIssue.get(i).getBatchNo());
						 issueDetail.setMrnDetailId(mrnDetailId); 
					 }
				 }
				 issueDetailEditList.add(issueDetail);
			 }
			 else
			 {
				 int index = Integer.parseInt(editIndex);
				 issueDetailEditList.get(index).setItemId(itemId);
				 issueDetailEditList.get(index).setItemIssueQty(qty);
				 issueDetailEditList.get(index).setItemGroupId(groupId);
				 issueDetailEditList.get(index).setDeptId(deptId);
				 issueDetailEditList.get(index).setSubDeptId(subDeptId);
				 issueDetailEditList.get(index).setAccHead(acc);
				 issueDetailEditList.get(index).setItemCode(itemName);
				 issueDetailEditList.get(index).setGrpCode(groupName);
				 issueDetailEditList.get(index).setDeptCode(deptName);
				 issueDetailEditList.get(index).setSubDeptCode(subDeptName);
				 issueDetailEditList.get(index).setAccHeadDesc(accName);
				 for(int i = 0 ;i< batchListInEditIssue.size() ; i++)
				 {
					 if(batchListInEditIssue.get(i).getMrnDetailId()==mrnDetailId)
					 {
						 issueDetailEditList.get(index).setBatchNo(batchListInEditIssue.get(i).getBatchNo());
						 issueDetailEditList.get(index).setMrnDetailId(mrnDetailId); 
					 }
				 }
			 }
			 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return issueDetailEditList;
	}
	
	@RequestMapping(value = "/editItemInEditIssueList", method = RequestMethod.GET)
	@ResponseBody
	public GetIssueDetail editItemInEditIssueList(HttpServletRequest request, HttpServletResponse response) {

		GetIssueDetail issueDetail = new GetIssueDetail();
		
		try {
			int index = Integer.parseInt(request.getParameter("index"));
			 
			issueDetail = issueDetailEditList.get(index);
			 
			 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return issueDetail;
	}
	
	@RequestMapping(value = "/deleteItemFromEditIssueList", method = RequestMethod.GET)
	@ResponseBody
	public List<GetIssueDetail> deleteItemFromEditIssueList(HttpServletRequest request, HttpServletResponse response) {
 
		
		try {
			int index = Integer.parseInt(request.getParameter("index"));
			 
			if(issueDetailEditList.get(index).getIssueDetailId()==0)
			{
				issueDetailEditList.remove(index);
			}
			else
			{
				issueDetailEditList.get(index).setDelStatus(0);
			}
			
			 
			 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return issueDetailEditList;
	}
	
	@RequestMapping(value = "/submitEditIssueReciept", method = RequestMethod.POST)
	public String submitEditIssueReciept(HttpServletRequest request, HttpServletResponse response) {

		try {

			 
			String issueNo = request.getParameter("issueNo"); 
			String issueDate = request.getParameter("issueDate");
			
			
			int deptId = Integer.parseInt(request.getParameter("deptId"));
			int subDeptId = Integer.parseInt(request.getParameter("subDeptId"));
			int acc = Integer.parseInt(request.getParameter("acc"));
			String issueSlipNo = request.getParameter("issueSlipNo"); 
			
			getIssueHeader.setIssueDate(issueDate);
			//getIssueHeader.setIssueNo(issueNo);
			getIssueHeader.setDeleteStatus(1);
			getIssueHeader.setDeptId(deptId);
			getIssueHeader.setSubDeptId(subDeptId);
			getIssueHeader.setAccHead(acc);
			getIssueHeader.setIssueSlipNo(issueSlipNo);
			getIssueHeader.setIssueDetailList(issueDetailEditList); 
			
			String mrnDetailList = new String();
			 
			 for(int i=0 ; i<issueDetailEditList.size() ; i++)
			 {
				 mrnDetailList = mrnDetailList+","+issueDetailEditList.get(i).getMrnDetailId();
				 issueDetailEditList.get(i).setDeptId(deptId);
				 issueDetailEditList.get(i).setSubDeptId(subDeptId);
				 issueDetailEditList.get(i).setAccHead(acc);
			 }
 
			 mrnDetailList = mrnDetailList.substring(1, mrnDetailList.length());
			System.out.println("getIssueHeader      " + getIssueHeader);
			IssueHeader res = rest.postForObject(Constants.url + "/saveIssueHeaderAndDetail", getIssueHeader,
					IssueHeader.class); 
			 System.out.println(res);
			
			if(res!=null)
			{ 
				
				 
				 MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
				 map.add("mrnDetailList", mrnDetailList);
				 MrnDetail[] MrnDetail = rest.postForObject(Constants.url + "/getMrnDetailListByMrnDetailId", map,
						 MrnDetail[].class);
				 
				  List<MrnDetail> updateMrnDetail = new ArrayList<MrnDetail>(Arrays.asList(MrnDetail));
				 
				 for(int j=0 ; j<issueDetailEditList.size() ; j++)
				 {
					 for(int i=0 ; i<updateMrnDetail.size() ; i++)
					 { 
						 if(updateMrnDetail.get(i).getMrnDetailId()==issueDetailEditList.get(j).getMrnDetailId() && issueDetailEditList.get(j).getDelStatus()==0)
						 {
							 System.out.println("in if ");
							 updateMrnDetail.get(i).setRemainingQty(updateMrnDetail.get(i).getRemainingQty()+issueDetailEditList.get(j).getItemIssueQty());
							 updateMrnDetail.get(i).setIssueQty(updateMrnDetail.get(i).getIssueQty()-issueDetailEditList.get(j).getItemIssueQty());
							 break;
						 }
						 else if(updateMrnDetail.get(i).getMrnDetailId()==issueDetailEditList.get(j).getMrnDetailId()  && issueDetailEditList.get(j).getIssueDetailId()==0)
						 {
							 System.out.println("in else if ");
							 updateMrnDetail.get(i).setRemainingQty(updateMrnDetail.get(i).getRemainingQty()-issueDetailEditList.get(j).getItemIssueQty());
							 updateMrnDetail.get(i).setIssueQty(updateMrnDetail.get(i).getIssueQty()+issueDetailEditList.get(j).getItemIssueQty());
							 break;
						 }
					 }
				 }
				 
				 System.out.println("updateMrnDetail      " + updateMrnDetail);
				 MrnDetail[] update = rest.postForObject(Constants.url + "/updateMrnDetailList", updateMrnDetail,
	   					 MrnDetail[].class);
					System.out.println(update);
			 } 

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/issueList";
	}
	
	@RequestMapping(value = "/firstApproveIssue", method = RequestMethod.GET)
	public ModelAndView firstApprovePurchaseOrder(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("issue/approveIssue");
		try {

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
 
			map.add("status","0,1");  
			GetIssueHeader[] getIssueHeader =rest.postForObject(Constants.url+"getIssueHeaderByStatus", map,  GetIssueHeader[].class);
			List<GetIssueHeader> getIssueHeaderList = new ArrayList<GetIssueHeader>(Arrays.asList(getIssueHeader));
			model.addObject("approve", 1);
			model.addObject("getIssueHeaderList", getIssueHeaderList);
			
			Type[] type = rest.getForObject(Constants.url + "/getAlltype", Type[].class);
			List<Type> typeList = new ArrayList<Type>(Arrays.asList(type));
			model.addObject("typeList", typeList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/secondApproveIssue", method = RequestMethod.GET)
	public ModelAndView secondApprovePurchaseOrder(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("issue/approveIssue");
		try {

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
 
			map.add("status","1");  
			GetIssueHeader[] getIssueHeader =rest.postForObject(Constants.url+"getIssueHeaderByStatus", map,  GetIssueHeader[].class);
			List<GetIssueHeader> getIssueHeaderList = new ArrayList<GetIssueHeader>(Arrays.asList(getIssueHeader));
			model.addObject("approve", 2);
			model.addObject("getIssueHeaderList", getIssueHeaderList);
			
			Type[] type = rest.getForObject(Constants.url + "/getAlltype", Type[].class);
			List<Type> typeList = new ArrayList<Type>(Arrays.asList(type));
			model.addObject("typeList", typeList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	
	GetIssueHeader issueForApprove = new GetIssueHeader();
	
	@RequestMapping(value = "/approveIssueDetail/{issueId}/{approve}", method = RequestMethod.GET)
	public ModelAndView approvePoDetail(@PathVariable int issueId,@PathVariable int approve, HttpServletRequest request, HttpServletResponse response) {
 
		ModelAndView model = new ModelAndView("issue/approveIssueDetail");
		try {
			 
			issueForApprove = new GetIssueHeader();
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			map.add("issueId",issueId); 
			 issueForApprove = rest.postForObject(Constants.url + "/getIssueHeaderAndDetailById",map, GetIssueHeader.class);
			
           model.addObject("issueForApprove", issueForApprove);
			model.addObject("approve", approve);
			
			Type[] type = rest.getForObject(Constants.url + "/getAlltype", Type[].class);
			List<Type> typeList = new ArrayList<Type>(Arrays.asList(type));
			model.addObject("typeList", typeList);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/submitIssueApprove", method = RequestMethod.POST)
	public String submitMrnApprove(HttpServletRequest request, HttpServletResponse response) {

		String ret = null;
		int approve = Integer.parseInt(request.getParameter("approve"));
		try {
			 
			
			String issueDetalId = new String();
			int issueId = 0 ;
			int status = 1;
			
			
			if(approve==1) {
				
				issueForApprove.setStatus(1);
				issueId=issueForApprove.getIssueId();
				String[] checkbox = request.getParameterValues("select_to_approve");
				status=1;
				for(int i=0 ; i<checkbox.length ;i++) {
					
					for(int j=0 ; j<issueForApprove.getIssueDetailList().size() ; j++) {
						
						if(Integer.parseInt(checkbox[i])==issueForApprove.getIssueDetailList().get(j).getIssueDetailId()) {
							issueForApprove.getIssueDetailList().get(j).setStatus(1);
							issueDetalId=issueDetalId+","+issueForApprove.getIssueDetailList().get(j).getIssueDetailId();
							break;
						}
					}
				}
				
				 
			}
			else if(approve==2){
				
				issueForApprove.setStatus(2);
				issueId=issueForApprove.getIssueId();
				String[] checkbox = request.getParameterValues("select_to_approve");
				status=2;
				for(int i=0 ; i<checkbox.length ;i++) {
					
					for(int j=0 ; j<issueForApprove.getIssueDetailList().size() ; j++) {
						
						if(Integer.parseInt(checkbox[i])==issueForApprove.getIssueDetailList().get(j).getIssueDetailId()) {
							issueForApprove.getIssueDetailList().get(j).setStatus(2);
							issueDetalId=issueDetalId+","+issueForApprove.getIssueDetailList().get(j).getIssueDetailId();
							break;
						}
					}
				}
				
			}
			
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			map.add("issueId", issueId);
			map.add("issueDetalId", issueDetalId.substring(1, issueDetalId.length()));
			map.add("status", status);
			System.out.println("map " + map);
			ErrorMessage approved = rest.postForObject(Constants.url + "/updateStatusWhileIssueApprov", map, ErrorMessage.class);
			
			if(approve==2 && approved.isError()==false) {
				
				issueForApprove = new GetIssueHeader();
				map = new LinkedMultiValueMap<String, Object>();
				map.add("issueId",issueId); 
				 issueForApprove = rest.postForObject(Constants.url + "/getIssueHeaderAndDetailById",map, GetIssueHeader.class);
				
				String mrnDetailList = new String();
				
				for(int i=0 ; i<issueForApprove.getIssueDetailList().size() ; i++)
				 {
					 mrnDetailList = mrnDetailList+","+issueForApprove.getIssueDetailList().get(i).getMrnDetailId();
				 }
	 
				 mrnDetailList = mrnDetailList.substring(1, mrnDetailList.length());
				 
				 map = new LinkedMultiValueMap<>();
				 map.add("mrnDetailList", mrnDetailList);
				 MrnDetail[] MrnDetail = rest.postForObject(Constants.url + "/getMrnDetailListByMrnDetailId", map,
						 MrnDetail[].class);
				 List<MrnDetail> updateMrnDetail = new ArrayList<>(Arrays.asList(MrnDetail));
				 
				 for(int i=0 ; i<issueForApprove.getIssueDetailList().size() ; i++)
				 {
					 for(int j=0 ; j<updateMrnDetail.size() ; j++)
					 {
						 if(updateMrnDetail.get(j).getMrnDetailId()==issueForApprove.getIssueDetailList().get(i).getMrnDetailId() 
								 && (issueForApprove.getIssueDetailList().get(i).getStatus()!=2))
						 {
							 updateMrnDetail.get(j).setRemainingQty(updateMrnDetail.get(j).getRemainingQty()+issueForApprove.getIssueDetailList().get(i).getItemIssueQty());
							 updateMrnDetail.get(j).setIssueQty(updateMrnDetail.get(j).getIssueQty()-issueForApprove.getIssueDetailList().get(i).getItemIssueQty());
						 }
					 }
				 }
				 
				 
					MrnDetail[] update = rest.postForObject(Constants.url + "/updateMrnDetailList", updateMrnDetail,
	   					 MrnDetail[].class);
					System.out.println(update); 
			}
			  
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		if(approve==1) {
			ret = "redirect:/firstApproveIssue";
		}
		else {
			ret = "redirect:/secondApproveIssue";
		}

		return ret;
	}

}
