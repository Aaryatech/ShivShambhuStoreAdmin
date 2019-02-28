package com.ats.shivstore.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
import com.ats.shivstore.model.Category;
import com.ats.shivstore.model.ErrorMessage;
import com.ats.shivstore.model.GetEnquiryDetail;
import com.ats.shivstore.model.GetEnquiryHeader;
import com.ats.shivstore.model.GetMrnDetailRej;
import com.ats.shivstore.model.GetMrnHeaderRej;
import com.ats.shivstore.model.GetpassDetail;
import com.ats.shivstore.model.GetpassHeader;
import com.ats.shivstore.model.GetpassReturnVendor;
import com.ats.shivstore.model.Vendor;
import com.ats.shivstore.model.doc.DocumentBean;
import com.ats.shivstore.model.item.GetItem;
import com.ats.shivstore.model.item.ItemList;
import com.ats.shivstore.model.mrn.GetMrnDetail;
import com.ats.shivstore.model.mrn.GetMrnHeader;
import com.ats.shivstore.model.mrn.MrnHeader;
import com.ats.shivstore.model.rejection.GetRejectionMemo;
import com.ats.shivstore.model.rejection.GetRejectionMemoDetail;
import com.ats.shivstore.model.rejection.RejectionMemo;
import com.ats.shivstore.model.rejection.RejectionMemoDetail;

@Controller

public class RejectionController {

	RestTemplate rest = new RestTemplate();
	List<RejectionMemoDetail> rejectionMemoDetailList = new ArrayList<RejectionMemoDetail>();
	List<GetMrnHeaderRej> getMrnList = new ArrayList<GetMrnHeaderRej>();

	@RequestMapping(value = "/showRejectionMemo", method = RequestMethod.GET)
	public ModelAndView showRejectionMemo(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("rejection/addRejectionMemo");
		try {

			Vendor[] vendorRes = rest.getForObject(Constants.url + "/getAllVendorByIsUsed", Vendor[].class);
			List<Vendor> vendorList = new ArrayList<Vendor>(Arrays.asList(vendorRes));

			model.addObject("vendorList", vendorList);
			
			Date date = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
			model.addObject("date", sf.format(date));

			/*MrnHeader[] mrnHeaderList = rest.getForObject(Constants.url + "/getMrnList", MrnHeader[].class);
			List<MrnHeader> mrnList = new ArrayList<MrnHeader>(Arrays.asList(mrnHeaderList));

			model.addObject("mrnList", mrnList);*/
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/getMrnListByVendorIdForRejectionMemo", method = RequestMethod.GET)
	@ResponseBody
	public List<MrnHeader> getMrnListByVendorIdForRejectionMemo(HttpServletRequest request, HttpServletResponse response) {

		List<MrnHeader> mrnList = new ArrayList<MrnHeader>();
		
		try {
			
			
			int vendId = Integer.parseInt(request.getParameter("vendId"));
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<>(); 
			map.add("vendId", vendId);
			MrnHeader[] mrnHeaderList = rest.postForObject(Constants.url + "/getMrnListByVendorIdForRejectionMemo",map, MrnHeader[].class);
			 mrnList = new ArrayList<MrnHeader>(Arrays.asList(mrnHeaderList));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mrnList;
	}

	@RequestMapping(value = "/getMrnListByMrnId", method = RequestMethod.GET)
	@ResponseBody
	public List<GetMrnHeaderRej> getMrnListByMrnId(HttpServletRequest request, HttpServletResponse response) {

		try {

			int mrnIdList = Integer.parseInt(request.getParameter("mrnId"));
			System.out.println("mrn Td" + mrnIdList);

			/*StringBuilder sb = new StringBuilder();

			for (int i = 0; i < mrnIdList.length; i++) {
				sb = sb.append(mrnIdList[i] + ",");

			}
			String items = sb.toString();
			items = items.substring(0, items.length() - 1);*/

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

			map.add("status", mrnIdList);

			// getMrnList = rest.postForObject(Constants.url + "getMrnHeaderDetail", map,
			// List.class);

			ParameterizedTypeReference<List<GetMrnHeaderRej>> typeRef = new ParameterizedTypeReference<List<GetMrnHeaderRej>>() {
			};
			ResponseEntity<List<GetMrnHeaderRej>> responseEntity = rest.exchange(Constants.url + "getMrnHeaderDetail",
					HttpMethod.POST, new HttpEntity<>(map), typeRef);
			getMrnList = responseEntity.getBody();
			
			
			System.out.println("getMrnList" + getMrnList);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return getMrnList;
	}

	@RequestMapping(value = "/insertRejectionMemo", method = RequestMethod.POST)
	public String insertRejectionMemo(HttpServletRequest request, HttpServletResponse response) {

		try {

			List<RejectionMemo> rejectionMemoList = new ArrayList<RejectionMemo>();

			int vendId = Integer.parseInt(request.getParameter("vendId"));

			String rejectionNo =  request.getParameter("rejectionNo") ;

			String rejectionDate = request.getParameter("rejectionDate");

			String docDate = request.getParameter("docDate");

			int mrnId = Integer.parseInt(request.getParameter("mrnId"));
			String remark = request.getParameter("remark");

			String remark1 = request.getParameter("remark1");

			int docNo = Integer.parseInt(request.getParameter("docNo"));

			String rejDate = DateConvertor.convertToYMD(rejectionDate);

			String docuDate = DateConvertor.convertToYMD(docDate);
			RejectionMemo rejectionMemo = new RejectionMemo();
			DocumentBean docBean=null;
			try {
				
				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
				map.add("docId", 7);
				map.add("catId", 1);
				map.add("date", DateConvertor.convertToYMD(rejDate));
				map.add("typeId", 1);
				RestTemplate restTemplate = new RestTemplate();

				 docBean = restTemplate.postForObject(Constants.url + "getDocumentData", map, DocumentBean.class);
				 String indMNo = docBean.getSubDocument().getCategoryPrefix() + "";
					int counter = docBean.getSubDocument().getCounter();
					int counterLenth = String.valueOf(counter).length();
					counterLenth = 4 - counterLenth;
					StringBuilder code = new StringBuilder(indMNo + "");

					for (int i = 0; i < counterLenth; i++) {
						String j = "0";
						code.append(j);
					}
					code.append(String.valueOf(counter));
				
				rejectionMemo.setMrnNo(""+code);
				
				docBean.getSubDocument().setCounter(docBean.getSubDocument().getCounter()+1);
			}catch (Exception e) {
				e.printStackTrace();
			}

			
			//for (int i = 0; i < mrnIdList.length; i++) {
				//System.out.println(" \n current mrn Id" + mrnIdList[i].toString()); 
				rejectionMemo.setDcoDate(docuDate);
				rejectionMemo.setDcoId(docNo);
				rejectionMemo.setIsUsed(1);
				rejectionMemo.setMrnId(mrnId);
				rejectionMemo.setRejectionDate(rejDate);
				rejectionMemo.setRejectionNo(rejectionNo);
				rejectionMemo.setRejectionRemark(remark);
				rejectionMemo.setRejectionRemark1(remark1);
				rejectionMemo.setStatus(1);
				rejectionMemo.setVendorId(vendId);
				rejectionMemoDetailList = new ArrayList<RejectionMemoDetail>();

				for (int j = 0; j < getMrnList.size(); j++) {

					if (mrnId == getMrnList.get(j).getMrnId()) {

						for (int k = 0; k < getMrnList.get(j).getGetMrnDetailRejList().size(); k++) {
							RejectionMemoDetail rejectionMemoDetail = new RejectionMemoDetail();
							GetMrnDetailRej getMrnDetail = getMrnList.get(j).getGetMrnDetailRejList().get(k);
							rejectionMemoDetail.setIsUsed(1);
							rejectionMemo.setMrnNo(getMrnList.get(j).getMrnNo());

							rejectionMemoDetail.setItemId(getMrnDetail.getItemId());
							rejectionMemoDetail.setMemoQty(
									Float.parseFloat(request.getParameter("memoQty" + getMrnDetail.getMrnDetailId())));
							rejectionMemoDetail.setMrnDate(DateConvertor.convertToYMD(getMrnList.get(j).getMrnDate()));
							rejectionMemoDetail.setMrnNo(getMrnList.get(j).getMrnNo());
							rejectionMemoDetail.setRejectionQty(getMrnDetail.getRejectQty());
							rejectionMemoDetail.setStatus(1);
							rejectionMemoDetailList.add(rejectionMemoDetail);
						}

					}
				}
				rejectionMemo.setRejectionMemoDetailList(rejectionMemoDetailList);
				rejectionMemoList.add(rejectionMemo);

			//}
			System.out.println("rejectionMemoList" + rejectionMemoList);
			List<RejectionMemo> res = rest.postForObject(Constants.url + "/saveRejectionMemoHeaderDetail",
					rejectionMemoList, List.class);
			System.out.println("response:" + res);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/showRejectionMemo";
	}

	@RequestMapping(value = "/getListOfRejectionMemo", method = RequestMethod.GET)
	@ResponseBody
	public List<GetRejectionMemo> getListOfRejectionMemo(HttpServletRequest request, HttpServletResponse response) {

		List<GetRejectionMemo> list = new ArrayList<GetRejectionMemo>();
		try {

			String fromDate = request.getParameter("fromDate");
			String toDate = request.getParameter("toDate");

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
			map.add("fromDate", DateConvertor.convertToYMD(fromDate));
			map.add("toDate", DateConvertor.convertToYMD(toDate));

			GetRejectionMemo[] getlist = rest.postForObject(Constants.url + "/getRejectionMemoByDate", map,
					GetRejectionMemo[].class);
			list = new ArrayList<GetRejectionMemo>(Arrays.asList(getlist));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	@RequestMapping(value = "/listOfRejectionMemo", method = RequestMethod.GET)
	public ModelAndView listOfRejectionMemo(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("rejection/listOfRejectionMemo");
		try {
			List<GetRejectionMemo> list = new ArrayList<GetRejectionMemo>();
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
			
			if(request.getParameter("fromDate")==null || request.getParameter("toDate")==null) {
				 
				Date date = new Date();
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat disp = new SimpleDateFormat("dd-MM-yyyy");
				
				map.add("fromDate", sf.format(date));
				map.add("toDate", sf.format(date));
				
				model.addObject("fromDate", disp.format(date));
				model.addObject("toDate", disp.format(date));
			}
			else {
				
				String fromDate = request.getParameter("fromDate");
				String toDate = request.getParameter("toDate");
 
				map.add("fromDate", DateConvertor.convertToYMD(fromDate));
				map.add("toDate", DateConvertor.convertToYMD(toDate));
				
				model.addObject("fromDate", fromDate);
				model.addObject("toDate", toDate);
			}
			
			

			GetRejectionMemo[] getlist = rest.postForObject(Constants.url + "/getRejectionMemoByDate", map,
					GetRejectionMemo[].class);
			list = new ArrayList<GetRejectionMemo>(Arrays.asList(getlist));
			
			model.addObject("list", list);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}

	@RequestMapping(value = "/deleteRejectionMemo/{rejectionId}", method = RequestMethod.GET)
	public String deleteRejectionMemo(@PathVariable int rejectionId, HttpServletRequest request,
			HttpServletResponse response) {

		try {

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
			map.add("rejectionId", rejectionId);

			ErrorMessage errorMessage = rest.postForObject(Constants.url + "/deleteRejectionMemo", map,
					ErrorMessage.class);
			System.out.println(errorMessage);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/listOfRejectionMemo";
	}

	List<GetRejectionMemoDetail> getRejectionMemoDetailList = new ArrayList<GetRejectionMemoDetail>();
	GetRejectionMemo editRejection = new GetRejectionMemo();

	@RequestMapping(value = "/editRejectionMemo/{rejectionId}", method = RequestMethod.GET)
	public ModelAndView editRejectionMemo(@PathVariable int rejectionId, HttpServletRequest request,
			HttpServletResponse response) {

		ModelAndView model = new ModelAndView("rejection/editERejMemo");
		try {

			/*Vendor[] vendorRes = rest.getForObject(Constants.url + "/getAllVendorByIsUsed", Vendor[].class);
			List<Vendor> vendorList = new ArrayList<Vendor>(Arrays.asList(vendorRes));
			model.addObject("vendorList", vendorList);

			MrnHeader[] mrnHeaderList = rest.getForObject(Constants.url + "/getMrnList", MrnHeader[].class);
			List<MrnHeader> mrnList = new ArrayList<MrnHeader>(Arrays.asList(mrnHeaderList));
			model.addObject("mrnList", mrnList);*/

			getRejectionMemoDetailList = new ArrayList<GetRejectionMemoDetail>();

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
			map.add("rejectionId", rejectionId);

			editRejection = rest.postForObject(Constants.url + "/getRejectionHeaderAndDetail", map,
					GetRejectionMemo.class);
			getRejectionMemoDetailList = editRejection.getGetRejectionMemoDetail();

			model.addObject("editRejection", editRejection);
			model.addObject("editRejectionDetailList", getRejectionMemoDetailList);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}

	@RequestMapping(value = { "/editMemoQty" }, method = RequestMethod.GET)
	public @ResponseBody List<GetRejectionMemoDetail> editMemoQty(HttpServletRequest request,
			HttpServletResponse response) {

		try {

			float memoQty = Float.parseFloat(request.getParameter("memoQty"));

			int rejDetailId = Integer.parseInt(request.getParameter("rejDetailId"));

			if (getRejectionMemoDetailList.size() > 0) {

				System.err.println("Inside getRejectionMemoDetailList.size >0 ");

				for (int i = 0; i < getRejectionMemoDetailList.size(); i++) {

					if (getRejectionMemoDetailList.get(i).getRejDetailId() == rejDetailId) {
						System.err.println("Inside rejDetailId matched  ");

						getRejectionMemoDetailList.get(i).setMemoQty(memoQty);

					} else {

						System.err.println("getRejectionMemoDetailList no match");
					}
				}

			}

			System.err.println(
					"getRejectionMemoDetailList  List Using Ajax Call  " + getRejectionMemoDetailList.toString());

		} catch (Exception e) {

			System.err.println(
					"Exception in getting getRejectionMemoDetailList @editMemoQty By Ajax Call " + e.getMessage());
			e.printStackTrace();
		}

		return getRejectionMemoDetailList;

	}

	@RequestMapping(value = "/submitEditRejectionMemo", method = RequestMethod.POST)
	public String submitEditRejectionMemo(HttpServletRequest request, HttpServletResponse response) {

		try {

			List<RejectionMemo> rejectionMemoList = new ArrayList<RejectionMemo>();

			int vendId = Integer.parseInt(request.getParameter("vendId"));

			String rejectionNo =  request.getParameter("rejectionNo") ;

			String rejectionDate = request.getParameter("rejectionDate");

			String docDate = request.getParameter("docDate");

			String mrnIdList = request.getParameter("mrnId[]");
			String remark = request.getParameter("remark");

			String remark1 = request.getParameter("remark1");

			int docNo = Integer.parseInt(request.getParameter("docNo"));

			String rejDate = DateConvertor.convertToYMD(rejectionDate);

			String docuDate = DateConvertor.convertToYMD(docDate);

			RejectionMemo rejectionMemo = new RejectionMemo();
			rejectionMemo = new RejectionMemo();
			rejectionMemo.setDcoDate(docuDate);
			rejectionMemo.setDcoId(docNo);
			rejectionMemo.setIsUsed(1);
			rejectionMemo.setMrnId(editRejection.getMrnId());
			rejectionMemo.setRejectionDate(rejDate);
			rejectionMemo.setRejectionNo(rejectionNo);
			rejectionMemo.setRejectionRemark(remark);
			rejectionMemo.setRejectionRemark1(remark1);
			rejectionMemo.setStatus(1);
			rejectionMemo.setVendorId(vendId);
			rejectionMemo.setMrnNo(mrnIdList);

			rejectionMemo.setRejectionId(editRejection.getRejectionId());

			rejectionMemoDetailList = new ArrayList<RejectionMemoDetail>();

			for (GetRejectionMemoDetail detail : getRejectionMemoDetailList) {

				RejectionMemoDetail rejectionMemoDetail = new RejectionMemoDetail();

				rejectionMemoDetail.setRejDetailId(detail.getRejDetailId());
				rejectionMemoDetail.setMemoQty(detail.getMemoQty());

				rejectionMemoDetail.setIsUsed(detail.getIsUsed());
				rejectionMemoDetail.setItemId(detail.getItemId());
				rejectionMemoDetail.setMrnDate(DateConvertor.convertToYMD(detail.getMrnDate()));
				rejectionMemoDetail.setMrnNo(detail.getMrnNo());

				rejectionMemoDetail.setRejectionId(detail.getRejectionId());
				rejectionMemoDetail.setRejectionQty(detail.getRejectionQty());
				rejectionMemoDetail.setStatus(detail.getStatus());

				rejectionMemoDetailList.add(rejectionMemoDetail);

			}

			rejectionMemo.setRejectionMemoDetailList(rejectionMemoDetailList);
			rejectionMemoList.add(rejectionMemo);

			System.out.println("rejectionMemoList" + rejectionMemoList);
			List<RejectionMemo> res = rest.postForObject(Constants.url + "/saveRejectionMemoHeaderDetail",
					rejectionMemoList, List.class);
			System.out.println("edit rejectionMemoList response:" + res);

		} catch (Exception e) {

			System.err.println("Exception in submitEditRejectionMemo @Rejec Controller ");
			e.printStackTrace();
		}

		return "redirect:/showRejectionMemo";
	}

}
