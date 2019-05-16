package com.ats.shivstore.controller;

import java.text.DateFormat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.ats.shivstore.common.Constants;
import com.ats.shivstore.common.DateConvertor;
import com.ats.shivstore.model.AccountHead;
import com.ats.shivstore.model.Category;
import com.ats.shivstore.model.ErrorMessage;
import com.ats.shivstore.model.GetPODetail;
import com.ats.shivstore.model.OpeningStockModel;
import com.ats.shivstore.model.PoDetail;
import com.ats.shivstore.model.SettingValue;
import com.ats.shivstore.model.StockHeader;
import com.ats.shivstore.model.Type;
import com.ats.shivstore.model.Vendor;
import com.ats.shivstore.model.doc.DocumentBean;
import com.ats.shivstore.model.doc.SubDocument;
import com.ats.shivstore.model.indent.Indent;
import com.ats.shivstore.model.indent.IndentTrans;
import com.ats.shivstore.model.indent.TempIndentDetail;
import com.ats.shivstore.model.mrn.MrnDetail;
import com.ats.shivstore.model.mrn.MrnHeader;
import com.ats.shivstore.model.po.PoHeader;

@Controller
@Scope("session")
public class OpeningUtilityController {
	RestTemplate rest = new RestTemplate();

	List<OpeningStockModel> itemList = new ArrayList<>();
	DecimalFormat df = new DecimalFormat("#.000");

	@RequestMapping(value = "/AddOPeningstockutility", method = RequestMethod.GET)
	public ModelAndView AddOPeningstockutility(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("stock/openingStockUtility");
		try {

			Category[] category = rest.getForObject(Constants.url + "/getAllCategoryByIsUsed", Category[].class);
			List<Category> categoryList = new ArrayList<Category>(Arrays.asList(category));
			model.addObject("categoryList", categoryList);

			AccountHead[] accountHead = rest.getForObject(Constants.url + "/getAllAccountHeadByIsUsed",
					AccountHead[].class);
			List<AccountHead> accountHeadList = new ArrayList<AccountHead>(Arrays.asList(accountHead));
			model.addObject("accountHeadList", accountHeadList);

			Vendor[] vendorRes = rest.getForObject(Constants.url + "/getAllVendorByIsUsed", Vendor[].class);
			List<Vendor> vendorList = new ArrayList<Vendor>(Arrays.asList(vendorRes));
			model.addObject("vendorList", vendorList);

			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date date = new Date();
			model.addObject("date", dateFormat.format(date));

			Type[] type = rest.getForObject(Constants.url + "/getAlltype", Type[].class);
			List<Type> typeList = new ArrayList<Type>(Arrays.asList(type));
			model.addObject("typeList", typeList);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}

	@RequestMapping(value = "getItemcategorywise", method = RequestMethod.GET)
	public @ResponseBody List<OpeningStockModel> GetAllitemOpeningStock(HttpServletRequest request,
			HttpServletResponse response) {

		itemList = new ArrayList<>();

		try {

			int catId = Integer.parseInt(request.getParameter("catId"));

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			map.add("cat_id", catId);
			OpeningStockModel[] itemRes = rest.postForObject(Constants.url + "/getAllitemOpeningStock", map,
					OpeningStockModel[].class);
			itemList = new ArrayList<OpeningStockModel>(Arrays.asList(itemRes));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return itemList;
	}

	@RequestMapping(value = "deleteItemFromOplist", method = RequestMethod.GET)
	public @ResponseBody List<OpeningStockModel> deleteItemFromOplist(HttpServletRequest request,
			HttpServletResponse response) {

		try {
			int index = Integer.parseInt(request.getParameter("key"));
			itemList.remove(index);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return itemList;
	}

	@RequestMapping(value = "/insertIndentPoMrn", method = RequestMethod.POST)
	public String insertIndentPoMrn(HttpServletRequest request, HttpServletResponse response) {

		try {

			int catId = Integer.parseInt(request.getParameter("catId"));

			String indNo = request.getParameter("indent_no");
			String chalan_no = request.getParameter("chalan_no");
			String chalan_date = request.getParameter("chalan_date");
			String bill_no = request.getParameter("bill_no");
			String bill_date = request.getParameter("bill_date");
			String transport = request.getParameter("transport");
			String lorry_remark = request.getParameter("lorry_remark");

			int indType = Integer.parseInt(request.getParameter("indent_type"));

			// String indHeaderRemark = request.getParameter("indHeaderRemark");

			String indDate = request.getParameter("indent_date");
			System.err.println("indeDate " + indDate);

			int accHead = Integer.parseInt(request.getParameter("acc_head"));

			int isMachineSpe = Integer.parseInt(request.getParameter("machine_specific"));

			int dept = 0;
			int subDept = 0;

			if (isMachineSpe == 1) {
				System.err.println("It is Machine Specific");
				dept = Integer.parseInt(request.getParameter("dept"));
				subDept = Integer.parseInt(request.getParameter("sub_dept"));

				System.err.println("dept " + dept + "sub Dept  " + subDept);

			}

			System.err.println("dept " + dept + "sub Dept  " + subDept);

			int isDev = Integer.parseInt(request.getParameter("is_dev"));
			int isMonthly = Integer.parseInt(request.getParameter("is_monthly"));

			Indent indent = new Indent();
			DocumentBean docBean = null;
			try {

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
				map.add("docId", 1);
				map.add("catId", catId);
				map.add("date", DateConvertor.convertToYMD(indDate));
				map.add("typeId", indType);
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

				indent.setIndMNo("" + code);

				docBean.getSubDocument().setCounter(docBean.getSubDocument().getCounter() + 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			indent.setAchdId(accHead);
			indent.setCatId(catId);
			indent.setIndIsdev(isDev);
			indent.setIndIsmonthly(isMonthly);
			indent.setIndMDate(DateConvertor.convertToYMD(indDate));

			indent.setIndMStatus(9);
			indent.setIndMType(indType);
			indent.setIndRemark("-");

			indent.setDeptId(dept);
			indent.setSubDeptId(subDept);
			indent.setIndApr1Date(DateConvertor.convertToYMD(indDate));
			indent.setIndApr2Date(DateConvertor.convertToYMD(indDate));

			indent.setDelStatus(Constants.delStatus);
			indent.setIndRemark("");
			List<IndentTrans> indTrasList = new ArrayList<IndentTrans>();
			for (int i = 0; i < itemList.size(); i++) {

				if (Float.parseFloat(request.getParameter("Qty" + itemList.get(i).getItemId())) > 0) {

					IndentTrans transDetail = new IndentTrans();
					transDetail.setIndItemCurstk(itemList.get(i).getItemOpQty());
					transDetail.setIndItemDesc(itemList.get(i).getItemDesc());
					transDetail.setIndItemSchd(1);
					transDetail.setIndItemSchddt(DateConvertor.convertToSqlDate(indDate));
					transDetail.setIndItemUom(itemList.get(i).getItemUom());
					transDetail.setIndMDate(indent.getIndMDate());
					transDetail.setIndMNo(indent.getIndMNo());
					transDetail.setIndQty(Float.parseFloat(request.getParameter("Qty" + itemList.get(i).getItemId())));
					transDetail.setIndRemark("");
					transDetail.setItemId(itemList.get(i).getItemId());
					transDetail.setIndFyr(transDetail.getIndQty());
					transDetail.setDelStatus(Constants.delStatus);
					transDetail.setIndDStatus(2);
					transDetail.setIndApr1Date(DateConvertor.convertToYMD(indDate));
					transDetail.setIndApr2Date(DateConvertor.convertToYMD(indDate));
					transDetail.setIndDStatus(9);
					indTrasList.add(transDetail);
				}

			}
			indent.setIndentTrans(indTrasList);

			System.err.println("Indent = " + indent.toString());

			RestTemplate restTemp = new RestTemplate();
			if (indTrasList.size() > 0) {

				Indent indRes = restTemp.postForObject(Constants.url + "/saveIndentAndTrans", indent, Indent.class);

				System.out.println("resposne indent" + indRes);

				indTrasList = indRes.getIndentTrans();

				if (indRes != null) {
					try {

						SubDocument subDocRes = restTemp.postForObject(Constants.url + "/saveSubDoc",
								docBean.getSubDocument(), SubDocument.class);

					} catch (Exception e) {
						e.printStackTrace();
					}

					int utility = Integer.parseInt(request.getParameter("indpomrn"));
					if (utility > 1) {

						int isState = 1;
						int vendId = Integer.parseInt(request.getParameter("Vendorlist"));

						float poBasicValue = 0;
						float discValue = 0;
						float taxValue = 0;

						MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
						/*
						 * map = new LinkedMultiValueMap<String, Object>(); map.add("name",
						 * "sameState");
						 */
						// System.out.println("map " + map);
						/*
						 * SettingValue settingValue = rest.postForObject(Constants.url +
						 * "/getSettingValue", map, SettingValue.class);
						 */

						/*
						 * if(intendDetailList.get(0).getStateCode().equals(settingValue.getValue())) {
						 * 
						 * isState=1; }
						 */

						PoHeader PoHeader = new PoHeader();

						// ----------------------------Inv No---------------------------------
						docBean = new DocumentBean();
						try {

							map = new LinkedMultiValueMap<String, Object>();
							map.add("docId", 2);
							map.add("catId", 1);
							map.add("date", DateConvertor.convertToYMD(indDate));
							map.add("typeId", indType);
							RestTemplate restTemplate = new RestTemplate();

							docBean = restTemplate.postForObject(Constants.url + "getDocumentData", map,
									DocumentBean.class);
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

							PoHeader.setPoNo("" + code);

							docBean.getSubDocument().setCounter(docBean.getSubDocument().getCounter() + 1);
						} catch (Exception e) {
							e.printStackTrace();
						}
						// ----------------------------Inv No---------------------------------
						PoHeader.setVendId(vendId);
						PoHeader.setVendQuation("-");
						PoHeader.setPoType(indType);
						PoHeader.setPaymentTermId(1);
						PoHeader.setDeliveryId(1);
						PoHeader.setDispatchId(1);
						PoHeader.setVendQuationDate(DateConvertor.convertToYMD(indDate));
						PoHeader.setPoDate(DateConvertor.convertToYMD(indDate));

						PoHeader.setOtherChargeAfterRemark("");
						PoHeader.setPoFrtRemark("");
						PoHeader.setPoInsuRemark("");
						PoHeader.setPoPackRemark("");
						// PoHeader.setIndId(PoHeader.getPoDetailList().get(0).getIndId());
						PoHeader.setDelStatus(1);
						PoHeader.setPoRemark("");
						PoHeader.setPoStatus(9);
						PoHeader.setApprovStatus(1);
						List<PoDetail> poDetailList = new ArrayList<>();

						Calendar c = Calendar.getInstance();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						for (int i = 0; i < indTrasList.size(); i++) {

							PoDetail poDetail = new PoDetail();
							poDetail.setIndId(indTrasList.get(i).getIndDId());
							poDetail.setSchDays(indTrasList.get(i).getIndItemSchd());
							poDetail.setItemId(indTrasList.get(i).getItemId());
							poDetail.setIndedQty(indTrasList.get(i).getIndQty());
							poDetail.setItemUom(indTrasList.get(i).getIndItemUom());
							poDetail.setStatus(9);
							poDetail.setItemQty(indTrasList.get(i).getIndQty());
							poDetail.setPendingQty(poDetail.getItemQty());
							poDetail.setDiscPer(0);
							poDetail.setItemRate(
									Float.parseFloat(request.getParameter("Rate" + indTrasList.get(i).getItemId())));
							poDetail.setSchRemark("");
							poDetail.setSchDays(indTrasList.get(i).getIndItemSchd());
							poDetail.setBalanceQty(poDetail.getPendingQty());
							poDetail.setSchDate(String.valueOf(indTrasList.get(i).getIndItemSchddt()));
							c.setTime(sdf.parse(poDetail.getSchDate()));
							c.add(Calendar.DAY_OF_MONTH, poDetail.getSchDays());
							poDetail.setSchDate(sdf.format(c.getTime()));
							poDetail.setIndId(indTrasList.get(i).getIndDId());
							poDetail.setIndMNo(indTrasList.get(i).getIndMNo());
							poDetail.setBasicValue(
									Float.parseFloat(df.format(poDetail.getItemQty() * poDetail.getItemRate())));
							poDetail.setDiscValue(Float
									.parseFloat(df.format((poDetail.getDiscPer() / 100) * poDetail.getBasicValue())));

							float taxPer = Float
									.parseFloat(request.getParameter("taxper" + indTrasList.get(i).getItemId()));

							if (isState == 0) {
								poDetail.setIgst(taxPer);

							} else {
								poDetail.setCgst(taxPer / 2);
								poDetail.setSgst(taxPer / 2);
							}
							poDetail.setTaxValue(Float.parseFloat(df.format((taxPer / 100)
									* (poDetail.getItemQty() * poDetail.getItemRate() - poDetail.getDiscValue()))));
							poDetail.setLandingCost(
									Float.parseFloat(df.format(poDetail.getItemQty() * poDetail.getItemRate()
											- poDetail.getDiscValue() + poDetail.getTaxValue())));
							poBasicValue = poBasicValue + poDetail.getBasicValue();
							discValue = discValue + poDetail.getDiscValue();
							taxValue = taxValue + poDetail.getTaxValue();
							poDetailList.add(poDetail);
							indTrasList.get(i).setIndFyr(indTrasList.get(i).getIndFyr() - poDetail.getItemQty());
							PoHeader.setIndNo(indTrasList.get(i).getIndMNo());

						}
						System.out.println(poDetailList);

						PoHeader.setIndId(indRes.getIndMId());
						PoHeader.setDiscValue(Float.parseFloat(df.format(discValue)));
						PoHeader.setPoBasicValue(Float.parseFloat(df.format(poBasicValue)));
						PoHeader.setPoDetailList(poDetailList);
						PoHeader.setPoTaxValue(taxValue);
						System.out.println(PoHeader);
						PoHeader save = rest.postForObject(Constants.url + "/savePoHeaderAndDetail", PoHeader,
								PoHeader.class);
						poDetailList = save.getPoDetailList();

						System.out.println(save);
						if (save != null) {
							try {

								SubDocument subDocRes = rest.postForObject(Constants.url + "/saveSubDoc",
										docBean.getSubDocument(), SubDocument.class);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (save != null && indTrasList.size() > 0) {
							for (int i = 0; i < indTrasList.size(); i++) {
								indTrasList.get(i)
										.setIndMDate(DateConvertor.convertToYMD(indTrasList.get(i).getIndMDate()));
								if (indTrasList.get(i).getIndFyr() == 0)
									indTrasList.get(i).setIndDStatus(2);
								else if (indTrasList.get(i).getIndFyr() > 0
										&& indTrasList.get(i).getIndFyr() < indTrasList.get(i).getIndQty())
									indTrasList.get(i).setIndDStatus(1);
								else
									indTrasList.get(i).setIndDStatus(0);
							}
							ErrorMessage errorMessage = rest.postForObject(Constants.url + "/updateIndendPendingQty",
									indTrasList, ErrorMessage.class);
							System.out.println(errorMessage);

							if (utility == 3) {

								System.err.println("inside /insertMrnProcess");

								MrnHeader mrnHeader = new MrnHeader();
								// ----------------------------Inv No---------------------------------
								// DocumentBean docBean=null;

								try {

									map = new LinkedMultiValueMap<String, Object>();
									map.add("docType", 1);
									map.add("date", DateConvertor.convertToYMD(indDate));

									RestTemplate restTemplate = new RestTemplate();

									errorMessage = restTemplate.postForObject(Constants.url + "generateIssueNoAndMrnNo",
											map, ErrorMessage.class);

									mrnHeader.setMrnNo("" + errorMessage.getMessage());

									// docBean.getSubDocument().setCounter(docBean.getSubDocument().getCounter()+1);
								} catch (Exception e) {
									e.printStackTrace();
								}
								// ----------------------------Inv No---------------------------------
								List<MrnDetail> mrnDetailList = new ArrayList<MrnDetail>();

								mrnHeader.setBillDate(DateConvertor.convertToYMD(bill_date));
								mrnHeader.setBillNo(bill_no);
								mrnHeader.setDelStatus(Constants.delStatus);
								mrnHeader.setDocDate(DateConvertor.convertToYMD(chalan_date));
								mrnHeader.setDocNo(chalan_no);
								mrnHeader.setGateEntryDate(DateConvertor.convertToYMD(indDate));
								mrnHeader.setGateEntryNo("");
								mrnHeader.setLrDate(DateConvertor.convertToYMD(indDate));
								mrnHeader.setLrNo("");
								mrnHeader.setMrnDate(DateConvertor.convertToYMD(indDate));

								mrnHeader.setMrnStatus(4);
								mrnHeader.setMrnType(indType);
								mrnHeader.setRemark1(lorry_remark);
								mrnHeader.setRemark2("def");
								mrnHeader.setTransport(transport);
								mrnHeader.setUserId(1);
								mrnHeader.setVendorId(vendId);

								for (PoDetail detail : poDetailList) {

									MrnDetail mrnDetail = new MrnDetail();
									mrnDetail.setIndentQty(detail.getIndedQty());
									mrnDetail.setPoQty(detail.getItemQty());
									mrnDetail.setMrnQty(detail.getItemQty());
									mrnDetail.setItemId(detail.getItemId());
									mrnDetail.setPoId(detail.getPoId());
									mrnDetail.setPoNo(save.getPoNo());
									mrnDetail.setMrnDetailStatus(4);
									mrnDetail.setBatchNo("Default Batch KKKK-00456");
									mrnDetail.setDelStatus(Constants.delStatus);
									mrnDetail.setPoDetailId(detail.getPoDetailId());
									mrnDetail.setMrnQtyBeforeEdit(-1);
									mrnDetail.setRemainingQty(mrnDetail.getMrnQty());
									mrnDetail.setApproveQty(mrnDetail.getMrnQty());
									mrnDetailList.add(mrnDetail);
								}

								mrnHeader.setMrnDetailList(mrnDetailList);

								System.err.println("Mrn Header   " + mrnHeader.toString());

								MrnHeader mrnHeaderRes = rest.postForObject(Constants.url + "/saveMrnHeadAndDetail",
										mrnHeader, MrnHeader.class);
								if (mrnHeaderRes != null) {
									try {

										// SubDocument subDocRes = restTemp.postForObject(Constants.url + "/saveSubDoc",
										// docBean.getSubDocument(), SubDocument.class);

									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								System.err.println("mrnHeaderRes " + mrnHeaderRes.toString());

							}
						}

					}
				}
			}

		} catch (Exception e) {

			System.err.println("Exception in @saveIndent  Indent" + e.getMessage());
			e.getCause();
			e.printStackTrace();
		}

		return "redirect:/AddOPeningstockutility";
	}
}
