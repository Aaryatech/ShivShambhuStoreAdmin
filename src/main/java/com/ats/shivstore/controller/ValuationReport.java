package com.ats.shivstore.controller;
 

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
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
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.ats.shivstore.common.Constants;
import com.ats.shivstore.common.DateConvertor;
import com.ats.shivstore.model.Category;
import com.ats.shivstore.model.Company;
import com.ats.shivstore.model.ConsumptionReportWithCatId;
import com.ats.shivstore.model.Dept;
import com.ats.shivstore.model.ExportToExcel;
import com.ats.shivstore.model.GetCurrentStock;
import com.ats.shivstore.model.GetItem;
import com.ats.shivstore.model.GetSubDept;
import com.ats.shivstore.model.IndentStatusReport;
import com.ats.shivstore.model.IssueAndMrnGroupWise;
import com.ats.shivstore.model.IssueAndMrnItemWise;
import com.ats.shivstore.model.IssueDeptWise;
import com.ats.shivstore.model.IssueMonthWiseList;
import com.ats.shivstore.model.ItemValuationList;
import com.ats.shivstore.model.MonthCategoryWiseMrnReport;
import com.ats.shivstore.model.MonthItemWiseMrnReport;
import com.ats.shivstore.model.MonthSubDeptWiseIssueReport;
import com.ats.shivstore.model.MonthWiseIssueReport;
import com.ats.shivstore.model.MrnMonthWiseList;
import com.ats.shivstore.model.StockValuationCategoryWise;
import com.ats.shivstore.model.Type;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
 

@Controller
@Scope("session")
public class ValuationReport {

	
	RestTemplate rest = new RestTemplate();
	String fromDate;
	String toDate;
	int typeId;
	int isDev;
	int deptId;
	int subDeptId;
	int catId;
	Company companyInfo = new Company();
	DecimalFormat df = new DecimalFormat("####0.00");
	
	List<IssueMonthWiseList> listGlobal;List<Dept> deparmentList;
	@RequestMapping(value = "/stockBetweenDateWithCatId", method = RequestMethod.GET)
	public ModelAndView itemValueationReport(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("valuationReport/stockBetweenDateWithCatId");
		try {
			
			Category[] category = rest.getForObject(Constants.url + "/getAllCategoryByIsUsed", Category[].class);
			List<Category> categoryList = new ArrayList<Category>(Arrays.asList(category));

			model.addObject("categoryList", categoryList); 
			List<GetCurrentStock> getStockBetweenDate = new ArrayList<>();
			
			if(request.getParameter("fromDate")==null || request.getParameter("toDate")==null || request.getParameter("catId")==null) {
				
			}
			else {
				
				fromDate = request.getParameter("fromDate");
				toDate = request.getParameter("toDate");
				catId = Integer.parseInt(request.getParameter("catId"));
				 
				
				SimpleDateFormat yy = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy");
				
				Date date = dd.parse(fromDate);
				  Calendar calendar = Calendar.getInstance();
				  calendar.setTime(date);
				   
				 String firstDate = "01"+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR);
				 
				 System.out.println(DateConvertor.convertToYMD(firstDate) + DateConvertor.convertToYMD(fromDate));
				 
				 if(DateConvertor.convertToYMD(firstDate).compareTo(DateConvertor.convertToYMD(fromDate))<0)
				 {
					calendar.add(Calendar.DATE, -1);
					String previousDate = yy.format(new Date(calendar.getTimeInMillis())); 
					MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
					map.add("fromDate",DateConvertor.convertToYMD(firstDate));
		 			map.add("toDate",previousDate); 
		 			map.add("catId", catId);
		 			System.out.println(map);
		 			GetCurrentStock[] getCurrentStock = rest.postForObject(Constants.url + "/getStockBetweenDateWithCatId",map,GetCurrentStock[].class); 
		 			List<GetCurrentStock> diffDateStock = new ArrayList<>(Arrays.asList(getCurrentStock));
		 			
		 			calendar.add(Calendar.DATE, 1);
					String addDay = yy.format(new Date(calendar.getTimeInMillis()));
		 			map = new LinkedMultiValueMap<>();
					map.add("fromDate",addDay);
		 			map.add("toDate",DateConvertor.convertToYMD(toDate)); 
		 			map.add("catId", catId);
		 			System.out.println(map);
		 			GetCurrentStock[] getCurrentStock1 = rest.postForObject(Constants.url + "/getStockBetweenDateWithCatId",map,GetCurrentStock[].class); 
		 			 getStockBetweenDate = new ArrayList<>(Arrays.asList(getCurrentStock1));
		 			 
		 			 for(int i = 0 ; i< getStockBetweenDate.size() ; i++)
		 			 {
		 				 for(int j = 0 ; j< diffDateStock.size() ; j++)
			 			 {
		 					 if(getStockBetweenDate.get(i).getItemId()==diffDateStock.get(j).getItemId())
		 					 {
		 						getStockBetweenDate.get(i).setOpeningStock(diffDateStock.get(j).getOpeningStock()+diffDateStock.get(j).getApproveQty()-diffDateStock.get(j).getIssueQty()
								 +diffDateStock.get(j).getReturnIssueQty()-diffDateStock.get(j).getDamageQty()-diffDateStock.get(j).getGatepassQty()
								 +diffDateStock.get(j).getGatepassReturnQty());
		 						getStockBetweenDate.get(i).setOpStockValue(diffDateStock.get(j).getOpStockValue()+diffDateStock.get(j).getApprovedQtyValue()-diffDateStock.get(j).getIssueQtyValue()-diffDateStock.get(j).getDamagValue());
		 						
		 						break;
		 					 }
			 			 }
		 			 }
				 }
				 else
				 {
					MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
					map.add("fromDate",DateConvertor.convertToYMD(fromDate));
		 			map.add("toDate",DateConvertor.convertToYMD(toDate)); 
		 			map.add("catId", catId);
		 			System.out.println(map);
		 			GetCurrentStock[] getCurrentStock = rest.postForObject(Constants.url + "/getStockBetweenDateWithCatId",map,GetCurrentStock[].class); 
		 			getStockBetweenDate = new ArrayList<>(Arrays.asList(getCurrentStock));
				 }
				 model.addObject("list", getStockBetweenDate); 
				 model.addObject("fromDate", fromDate);
				 model.addObject("toDate", toDate);
				 model.addObject("catId", catId);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/getStockBetweenDateWithCatId", method = RequestMethod.GET)
	@ResponseBody
	public List<GetCurrentStock> getStockBetweenDateWithCatId(HttpServletRequest request, HttpServletResponse response) {

		List<GetCurrentStock> getStockBetweenDate = new ArrayList<>();
		
		try {
		 
			fromDate = request.getParameter("fromDate");
			toDate = request.getParameter("toDate");
			catId = Integer.parseInt(request.getParameter("catId"));
			 
			
			SimpleDateFormat yy = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy");
			
			Date date = dd.parse(fromDate);
			  Calendar calendar = Calendar.getInstance();
			  calendar.setTime(date);
			   
			 String firstDate = "01"+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR);
			 
			 System.out.println(DateConvertor.convertToYMD(firstDate) + DateConvertor.convertToYMD(fromDate));
			 
			 if(DateConvertor.convertToYMD(firstDate).compareTo(DateConvertor.convertToYMD(fromDate))<0)
			 {
				calendar.add(Calendar.DATE, -1);
				String previousDate = yy.format(new Date(calendar.getTimeInMillis())); 
				MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
				map.add("fromDate",DateConvertor.convertToYMD(firstDate));
	 			map.add("toDate",previousDate); 
	 			map.add("catId", catId);
	 			System.out.println(map);
	 			GetCurrentStock[] getCurrentStock = rest.postForObject(Constants.url + "/getStockBetweenDateWithCatId",map,GetCurrentStock[].class); 
	 			List<GetCurrentStock> diffDateStock = new ArrayList<>(Arrays.asList(getCurrentStock));
	 			
	 			calendar.add(Calendar.DATE, 1);
				String addDay = yy.format(new Date(calendar.getTimeInMillis()));
	 			map = new LinkedMultiValueMap<>();
				map.add("fromDate",addDay);
	 			map.add("toDate",DateConvertor.convertToYMD(toDate)); 
	 			map.add("catId", catId);
	 			System.out.println(map);
	 			GetCurrentStock[] getCurrentStock1 = rest.postForObject(Constants.url + "/getStockBetweenDateWithCatId",map,GetCurrentStock[].class); 
	 			 getStockBetweenDate = new ArrayList<>(Arrays.asList(getCurrentStock1));
	 			 
	 			 for(int i = 0 ; i< getStockBetweenDate.size() ; i++)
	 			 {
	 				 for(int j = 0 ; j< diffDateStock.size() ; j++)
		 			 {
	 					 if(getStockBetweenDate.get(i).getItemId()==diffDateStock.get(j).getItemId())
	 					 {
	 						getStockBetweenDate.get(i).setOpeningStock(diffDateStock.get(j).getOpeningStock()+diffDateStock.get(j).getApproveQty()-diffDateStock.get(j).getIssueQty()
							 +diffDateStock.get(j).getReturnIssueQty()-diffDateStock.get(j).getDamageQty()-diffDateStock.get(j).getGatepassQty()
							 +diffDateStock.get(j).getGatepassReturnQty());
	 						getStockBetweenDate.get(i).setOpStockValue(diffDateStock.get(j).getOpStockValue()+diffDateStock.get(j).getApprovedQtyValue()-diffDateStock.get(j).getIssueQtyValue()-diffDateStock.get(j).getDamagValue());
	 						
	 						break;
	 					 }
		 			 }
	 			 }
			 }
			 else
			 {
				MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
				map.add("fromDate",DateConvertor.convertToYMD(fromDate));
	 			map.add("toDate",DateConvertor.convertToYMD(toDate)); 
	 			map.add("catId", catId);
	 			System.out.println(map);
	 			GetCurrentStock[] getCurrentStock = rest.postForObject(Constants.url + "/getStockBetweenDateWithCatId",map,GetCurrentStock[].class); 
	 			getStockBetweenDate = new ArrayList<>(Arrays.asList(getCurrentStock));
			 }
			  
			  
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getStockBetweenDate;
	}
	
	@RequestMapping(value = "/valueationReportDetail/{itemId}/{openingStock}", method = RequestMethod.GET)
	public ModelAndView valueationReportDetail(@PathVariable int itemId,@PathVariable int openingStock, HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("valuationReport/valueationReportDetail");
		try {
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
			map.add("itemId", itemId);
			GetItem  item = rest.postForObject(Constants.url + "/getItemByItemId",map, GetItem .class);
			model.addObject("item", item);
			
			map.add("fromDate", fromDate);
			map.add("toDate", toDate);
			//map.add("catId", catId);
			ItemValuationList[] itemValuation = rest.postForObject(Constants.url + "/valueationReportDetail",map, ItemValuationList[].class);
			List<ItemValuationList> itemValuationList = new ArrayList<ItemValuationList>(Arrays.asList(itemValuation));

			model.addObject("itemValuationList", itemValuationList);
			model.addObject("openingStock", openingStock);
			model.addObject("fromDate", fromDate);
			model.addObject("toDate", toDate);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	List<StockValuationCategoryWise> stockCategoryWiseListForPdf = new ArrayList<StockValuationCategoryWise>();
	
	@RequestMapping(value = "/stockValueationReportCategoryWise", method = RequestMethod.GET)
	public ModelAndView stockValueationReportCategoryWise(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("valuationReport/stockValueationReportCategoryWise");
		try {
			List<StockValuationCategoryWise> categoryWiseReport = new ArrayList<StockValuationCategoryWise>();
			Type[] type = rest.getForObject(Constants.url + "/getAlltype", Type[].class);
			List<Type> typeList = new ArrayList<Type>(Arrays.asList(type));
			model.addObject("typeList", typeList);
			
			if(request.getParameter("fromDate")==null || request.getParameter("toDate")==null || request.getParameter("typeId")==null) {
				
				SimpleDateFormat yy = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy");
				Date date = new Date();
				  Calendar calendar = Calendar.getInstance();
				  calendar.setTime(date);
				   
				 
				 fromDate =  "01"+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR);
				 toDate = dd.format(date);
				 typeId=0;
				 MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
					map.add("fromDate",DateConvertor.convertToYMD(fromDate));
		 			map.add("toDate",yy.format(date)); 
		 			map.add("typeId",typeId);
					StockValuationCategoryWise[] stockValuationCategoryWise = rest.postForObject(Constants.url + "/stockValueationReport",map, StockValuationCategoryWise[].class);
					categoryWiseReport = new ArrayList<StockValuationCategoryWise>(Arrays.asList(stockValuationCategoryWise));
					
					model.addObject("categoryWiseReport", categoryWiseReport);
					model.addObject("fromDate", fromDate);
					model.addObject("toDate", dd.format(date));
					stockCategoryWiseListForPdf=categoryWiseReport;
			}
			else {
				fromDate = request.getParameter("fromDate");
				toDate = request.getParameter("toDate");
				 typeId = Integer.parseInt(request.getParameter("typeId"));
				
				
				SimpleDateFormat yy = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy");
				
				Date date = dd.parse(fromDate);
				  Calendar calendar = Calendar.getInstance();
				  calendar.setTime(date);
				   
				 String firstDate = "01"+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR);
				 
				 System.out.println(DateConvertor.convertToYMD(firstDate) + DateConvertor.convertToYMD(fromDate));
				 
				 if(DateConvertor.convertToYMD(firstDate).compareTo(DateConvertor.convertToYMD(fromDate))<0)
				 {
					calendar.add(Calendar.DATE, -1);
					String previousDate = yy.format(new Date(calendar.getTimeInMillis())); 
					MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
					map.add("fromDate",DateConvertor.convertToYMD(firstDate));
		 			map.add("toDate",previousDate); 
		 			map.add("typeId",typeId);
					StockValuationCategoryWise[] stockValuationCategoryWise = rest.postForObject(Constants.url + "/stockValueationReport",map, StockValuationCategoryWise[].class);
					 List<StockValuationCategoryWise> diffDateStock = new ArrayList<StockValuationCategoryWise>(Arrays.asList(stockValuationCategoryWise));
		 			
		 			calendar.add(Calendar.DATE, 1);
					String addDay = yy.format(new Date(calendar.getTimeInMillis()));
		 			map = new LinkedMultiValueMap<>();
					map.add("fromDate",addDay);
		 			map.add("toDate",DateConvertor.convertToYMD(toDate)); 
		 			map.add("typeId", typeId);
		 			System.out.println(map);
		 			StockValuationCategoryWise[] stockValuationCategoryWise1 = rest.postForObject(Constants.url + "/stockValueationReport",map, StockValuationCategoryWise[].class);
					 categoryWiseReport = new ArrayList<StockValuationCategoryWise>(Arrays.asList(stockValuationCategoryWise1));
		 			 
		 			 for(int i = 0 ; i< categoryWiseReport.size() ; i++)
		 			 {
		 				 for(int j = 0 ; j< diffDateStock.size() ; j++)
			 			 {
		 					 if(categoryWiseReport.get(i).getCatId()==diffDateStock.get(j).getCatId())
		 					 {
		 						categoryWiseReport.get(i).setOpeningStock(diffDateStock.get(j).getOpeningStock()+diffDateStock.get(j).getApproveQty()-diffDateStock.get(j).getIssueQty()
								  -diffDateStock.get(j).getDamageQty());
		 						categoryWiseReport.get(i).setOpStockValue(diffDateStock.get(j).getOpStockValue()+diffDateStock.get(j).getApprovedQtyValue()-diffDateStock.get(j).getIssueQtyValue()-diffDateStock.get(j).getDamageValue());
		 						
		 						break;
		 					 }
			 			 }
		 			 }
				 }
				 else
				 {
					MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
					map.add("fromDate",DateConvertor.convertToYMD(fromDate));
		 			map.add("toDate",DateConvertor.convertToYMD(toDate)); 
		 			map.add("typeId", typeId);
		 			System.out.println(map);
		 			StockValuationCategoryWise[] stockValuationCategoryWise1 = rest.postForObject(Constants.url + "/stockValueationReport",map, StockValuationCategoryWise[].class);
					 categoryWiseReport = new ArrayList<StockValuationCategoryWise>(Arrays.asList(stockValuationCategoryWise1));
				 }
				 
				model.addObject("categoryWiseReport", categoryWiseReport);
				model.addObject("fromDate", fromDate);
				model.addObject("toDate", toDate);
				model.addObject("typeId", typeId);
				stockCategoryWiseListForPdf=categoryWiseReport;
				
				
			}
			
			//----------------exel-------------------------
			
			
			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

			ExportToExcel expoExcel = new ExportToExcel();
			List<String> rowData = new ArrayList<String>();

			rowData.add("SR. No");
			rowData.add("CATEGORY");
			rowData.add("OP QTY");
			rowData.add("OP VALUE");
			rowData.add("MRN QTY");
			rowData.add("MRN VALUE");
			rowData.add("ISSUE QTY");
			rowData.add("ISSUE VALUE");
			rowData.add("DAMAGE QTY");
			rowData.add("DAMAGE VALUE");
			rowData.add("C\\L QTY");
			rowData.add("C\\L VALUE");
			

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);
			for (int i = 0; i < categoryWiseReport.size(); i++) {
				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();

				rowData.add((i+1)+"");
				rowData.add(categoryWiseReport.get(i).getCatDesc());
				rowData.add(""+categoryWiseReport.get(i).getOpeningStock());
				rowData.add(""+categoryWiseReport.get(i).getOpStockValue());
				rowData.add(""+categoryWiseReport.get(i).getApproveQty());
				rowData.add(""+categoryWiseReport.get(i).getApprovedQtyValue());
				rowData.add(""+categoryWiseReport.get(i).getIssueQty());
				rowData.add(""+categoryWiseReport.get(i).getIssueQtyValue());
				rowData.add(""+categoryWiseReport.get(i).getDamageQty());
				rowData.add(""+categoryWiseReport.get(i).getDamageValue());
				
				float closingQty = categoryWiseReport.get(i).getOpeningStock()+categoryWiseReport.get(i).getApproveQty()-
						categoryWiseReport.get(i).getIssueQty()-categoryWiseReport.get(i).getDamageQty();
				
				float closingValue = categoryWiseReport.get(i).getOpStockValue()+categoryWiseReport.get(i).getApprovedQtyValue()-
						categoryWiseReport.get(i).getIssueQtyValue()-categoryWiseReport.get(i).getDamageValue();
				
				
				rowData.add(""+closingQty);
				rowData.add(""+closingValue);


				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);

			}

			HttpSession session = request.getSession();
			session.setAttribute("exportExcelList", exportToExcelList);
			session.setAttribute("excelName", "CategoryWiseStockValue");
			
			 companyInfo = rest.getForObject(Constants.url + "getCompanyDetails",
						Company.class);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/stockValuetionReportCategoryWisePDF/{type}", method = RequestMethod.GET)
	public void stockValuetionReportCategoryWisePDF(@PathVariable String type,HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		BufferedOutputStream outStream = null;
		try {
		Document document = new Document(PageSize.A4);
		DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
		String reportDate = DF.format(new Date());
        document.addHeader("Date: ", reportDate);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		 
		System.out.println("time in Gen Bill PDF ==" + dateFormat.format(cal.getTime()));
		String timeStamp = dateFormat.format(cal.getTime());
		String FILE_PATH = Constants.REPORT_SAVE;
		File file = new File(FILE_PATH);

		PdfWriter writer = null;

		FileOutputStream out = new FileOutputStream(FILE_PATH);
		try {
			writer = PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
	
		PdfPTable table = new PdfPTable(12);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] {0.4f, 1.7f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f});
			Font headFont = new Font(FontFamily.TIMES_ROMAN, 8, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 11.0f, Font.UNDERLINE, BaseColor.BLUE);
			Font f1 = new Font(FontFamily.TIMES_ROMAN, 9.0f, Font.BOLD, BaseColor.DARK_GRAY);

			PdfPCell hcell = new PdfPCell();
			
			hcell.setPadding(4);
			hcell = new PdfPCell(new Phrase("SR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("CATEGORY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("OP QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("OP VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("MRN QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("MRN VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("ISSUE QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("ISSUE VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("DAMAGE QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("DAMAGE VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("C/L QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("C/L VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			float totalOpQty=0;
			float totalOpValue=0;
			float totalMrnQty=0;
			float totalMrnValue=0;
			float totalIssueQty=0;
			float totalIssueValue=0;
			float totalDamageQty=0;
			float totalDamageValue=0;
			float totalClQty=0;
			float totalClValue=0;
			
			
			int index = 0;
			if(!stockCategoryWiseListForPdf.isEmpty()) {
					for (int k = 0; k < stockCategoryWiseListForPdf.size(); k++) {
                            
						if(stockCategoryWiseListForPdf.get(k).getOpeningStock()>0 || stockCategoryWiseListForPdf.get(k).getOpStockValue()>0 
								|| stockCategoryWiseListForPdf.get(k).getApproveQty()>0 || stockCategoryWiseListForPdf.get(k).getApprovedQtyValue()>0
								|| stockCategoryWiseListForPdf.get(k).getIssueQty()>0 || stockCategoryWiseListForPdf.get(k).getIssueQtyValue()>0
								|| stockCategoryWiseListForPdf.get(k).getDamageQty()>0 || stockCategoryWiseListForPdf.get(k).getDamageValue()>0) {
							
						
							index++;
						
							PdfPCell cell;
							
							cell = new PdfPCell(new Phrase(""+index, headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPadding(3);
							table.addCell(cell);

						
							cell = new PdfPCell(new Phrase(stockCategoryWiseListForPdf.get(k).getCatDesc(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
						
							cell = new PdfPCell(new Phrase(""+df.format(stockCategoryWiseListForPdf.get(k).getOpeningStock()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalOpQty=totalOpQty+stockCategoryWiseListForPdf.get(k).getOpeningStock();
							
							cell = new PdfPCell(new Phrase(""+df.format(stockCategoryWiseListForPdf.get(k).getOpStockValue()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalOpValue=totalOpValue+stockCategoryWiseListForPdf.get(k).getOpStockValue();
							
							cell = new PdfPCell(new Phrase(""+df.format(stockCategoryWiseListForPdf.get(k).getApproveQty()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalMrnQty=totalMrnQty+stockCategoryWiseListForPdf.get(k).getApproveQty();
							
							cell = new PdfPCell(new Phrase(""+df.format(stockCategoryWiseListForPdf.get(k).getApprovedQtyValue()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalMrnValue=totalMrnValue+stockCategoryWiseListForPdf.get(k).getApprovedQtyValue();
							
							cell = new PdfPCell(new Phrase(""+df.format(stockCategoryWiseListForPdf.get(k).getIssueQty()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalIssueQty=totalIssueQty+stockCategoryWiseListForPdf.get(k).getIssueQty();
							
							cell = new PdfPCell(new Phrase(""+df.format(stockCategoryWiseListForPdf.get(k).getIssueQtyValue()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalIssueValue=totalIssueValue+stockCategoryWiseListForPdf.get(k).getIssueQtyValue();
							
							cell = new PdfPCell(new Phrase(""+df.format(stockCategoryWiseListForPdf.get(k).getDamageQty()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalDamageQty=totalDamageQty+stockCategoryWiseListForPdf.get(k).getDamageQty();
							
							cell = new PdfPCell(new Phrase(""+df.format(stockCategoryWiseListForPdf.get(k).getDamageValue()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalDamageValue=totalDamageValue+stockCategoryWiseListForPdf.get(k).getDamageValue();
							
							float closingQty = stockCategoryWiseListForPdf.get(k).getOpeningStock()+stockCategoryWiseListForPdf.get(k).getApproveQty()-
									stockCategoryWiseListForPdf.get(k).getIssueQty()-stockCategoryWiseListForPdf.get(k).getDamageQty();
							
							float closingValue = stockCategoryWiseListForPdf.get(k).getOpStockValue()+stockCategoryWiseListForPdf.get(k).getApprovedQtyValue()-
									stockCategoryWiseListForPdf.get(k).getIssueQtyValue()-stockCategoryWiseListForPdf.get(k).getDamageValue();
							
							cell = new PdfPCell(new Phrase(""+df.format(closingQty), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalClQty=totalClQty+closingQty;
							
							cell = new PdfPCell(new Phrase(""+df.format(closingValue), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalClValue=totalClValue+closingValue;
						}
					}
			}
			
			PdfPCell cell;
		 
			cell = new PdfPCell(new Phrase("Total ", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			cell.setColspan(2);
			table.addCell(cell);
		
			cell = new PdfPCell(new Phrase(""+df.format(totalOpQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			cell = new PdfPCell(new Phrase(""+df.format(totalOpValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format(totalMrnQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format(totalMrnValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format(totalIssueQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format(totalIssueValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format(totalDamageQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format(totalDamageValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			  
		 
			cell = new PdfPCell(new Phrase(""+df.format(totalClQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format(totalClValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			 
			document.open();
			Paragraph company = new Paragraph(companyInfo.getCompanyName()+"\n", f);
			company.setAlignment(Element.ALIGN_CENTER);
			document.add(company);
			
				Paragraph heading1 = new Paragraph(
						companyInfo.getFactoryAdd(),f1);
				heading1.setAlignment(Element.ALIGN_CENTER);
				document.add(heading1);
				Paragraph ex2=new Paragraph("\n");
				document.add(ex2);

				Paragraph reportName=new Paragraph("STOCK VALUEATION REPORT",f1);
				reportName.setAlignment(Element.ALIGN_CENTER);
				document.add(reportName);
				
				Paragraph typeName=new Paragraph("Type : " + type,f1);
				typeName.setAlignment(Element.ALIGN_CENTER);
				document.add(typeName);
				
				Paragraph headingDate=new Paragraph("From Date: " + fromDate+"  To Date: "+toDate+"",f1);
				headingDate.setAlignment(Element.ALIGN_CENTER);
				document.add(headingDate);
			
			Paragraph ex3=new Paragraph("\n");
			document.add(ex3);
			table.setHeaderRows(1);
			document.add(table);
			
		
			int totalPages = writer.getPageNumber();

			System.out.println("Page no " + totalPages);

			document.close();
			// Atul Sir code to open a Pdf File
			if (file != null) {

				String mimeType = URLConnection.guessContentTypeFromName(file.getName());

				if (mimeType == null) {

					mimeType = "application/pdf";

				}

				response.setContentType(mimeType);

				response.addHeader("content-disposition", String.format("inline; filename=\"%s\"", file.getName()));

				response.setContentLength((int) file.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				try {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				} catch (IOException e) {
					System.out.println("Excep in Opening a Pdf File");
					e.printStackTrace();
				}
			}

		} catch (DocumentException ex) {

			System.out.println("Pdf Generation Error" + ex.getMessage());

			ex.printStackTrace();

		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/listForStockValuetioinGraph", method = RequestMethod.GET)
	public @ResponseBody List<StockValuationCategoryWise> listForStockValuetioinGraph(HttpServletRequest request, HttpServletResponse response) {

		 
		return stockCategoryWiseListForPdf;
	}
	
	
	List<GetCurrentStock> itemWiseStockValuetionListForPDF = new ArrayList<>();
	
	@RequestMapping(value = "/stockSummaryWithCatId/{catId}/{type}/{catDesc}", method = RequestMethod.GET)
	public ModelAndView stockSummaryWithCatId(@PathVariable int catId,@PathVariable String type,@PathVariable String catDesc, HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("valuationReport/stockSummaryWithCatId");
		List<GetCurrentStock> getStockBetweenDate = new ArrayList<>();
		
		try {
		  
			SimpleDateFormat yy = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy");
			
			Date date = dd.parse(fromDate);
			  Calendar calendar = Calendar.getInstance();
			  calendar.setTime(date);
			   
			 String firstDate = "01"+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR);
			 
			 System.out.println(DateConvertor.convertToYMD(firstDate) + DateConvertor.convertToYMD(fromDate));
			 
			 if(DateConvertor.convertToYMD(firstDate).compareTo(DateConvertor.convertToYMD(fromDate))<0)
			 {
				calendar.add(Calendar.DATE, -1);
				String previousDate = yy.format(new Date(calendar.getTimeInMillis())); 
				MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
				map.add("fromDate",DateConvertor.convertToYMD(firstDate));
	 			map.add("toDate",previousDate); 
	 			map.add("catId", catId);
	 			map.add("typeId", typeId);
	 			System.out.println(map);
	 			GetCurrentStock[] getCurrentStock = rest.postForObject(Constants.url + "/getStockBetweenDateWithCatIdAndTypeId",map,GetCurrentStock[].class); 
	 			List<GetCurrentStock> diffDateStock = new ArrayList<>(Arrays.asList(getCurrentStock));
	 			
	 			calendar.add(Calendar.DATE, 1);
				String addDay = yy.format(new Date(calendar.getTimeInMillis()));
	 			map = new LinkedMultiValueMap<>();
				map.add("fromDate",addDay);
	 			map.add("toDate",DateConvertor.convertToYMD(toDate)); 
	 			map.add("catId", catId);
	 			map.add("typeId", typeId);
	 			System.out.println(map);
	 			GetCurrentStock[] getCurrentStock1 = rest.postForObject(Constants.url + "/getStockBetweenDateWithCatIdAndTypeId",map,GetCurrentStock[].class); 
	 			 getStockBetweenDate = new ArrayList<>(Arrays.asList(getCurrentStock1));
	 			 
	 			 for(int i = 0 ; i< getStockBetweenDate.size() ; i++)
	 			 {
	 				 for(int j = 0 ; j< diffDateStock.size() ; j++)
		 			 {
	 					 if(getStockBetweenDate.get(i).getItemId()==diffDateStock.get(j).getItemId())
	 					 {
	 						getStockBetweenDate.get(i).setOpeningStock(diffDateStock.get(j).getOpeningStock()+diffDateStock.get(j).getApproveQty()-diffDateStock.get(j).getIssueQty()
							 +diffDateStock.get(j).getReturnIssueQty()-diffDateStock.get(j).getDamageQty()-diffDateStock.get(j).getGatepassQty()
							 +diffDateStock.get(j).getGatepassReturnQty());
	 						getStockBetweenDate.get(i).setOpStockValue(diffDateStock.get(j).getOpStockValue()+diffDateStock.get(j).getApprovedQtyValue()-diffDateStock.get(j).getIssueQtyValue()-diffDateStock.get(j).getDamagValue());
	 						
	 						break;
	 					 }
		 			 }
	 			 }
			 }
			 else
			 {
				MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
				map.add("fromDate",DateConvertor.convertToYMD(fromDate));
	 			map.add("toDate",DateConvertor.convertToYMD(toDate)); 
	 			map.add("catId", catId);
	 			map.add("typeId", typeId);
	 			System.out.println(map);
	 			GetCurrentStock[] getCurrentStock = rest.postForObject(Constants.url + "/getStockBetweenDateWithCatIdAndTypeId",map,GetCurrentStock[].class); 
	 			getStockBetweenDate = new ArrayList<>(Arrays.asList(getCurrentStock));
			 }
			 itemWiseStockValuetionListForPDF=getStockBetweenDate;
			 model.addObject("list",getStockBetweenDate);
			 model.addObject("type",type);
			 model.addObject("catDesc",catDesc);
			//----------------exel-------------------------
				
				
				List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

				ExportToExcel expoExcel = new ExportToExcel();
				List<String> rowData = new ArrayList<String>();

				rowData.add("SR. No");
				rowData.add("ITEM NAME");
				rowData.add("OP QTY");
				rowData.add("OP VALUE");
				rowData.add("MRN QTY");
				rowData.add("MRN VALUE");
				rowData.add("ISSUE QTY");
				rowData.add("ISSUE VALUE");
				rowData.add("DAMAGE QTY");
				rowData.add("DAMAGE VALUE");
				rowData.add("C\\L QTY");
				rowData.add("C\\L VALUE");
				

				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);
				int k=0;
				for (int i = 0; i < getStockBetweenDate.size(); i++) {
					if(getStockBetweenDate.get(i).getOpeningStock()>0 || getStockBetweenDate.get(i).getOpStockValue()>0 || 
							getStockBetweenDate.get(i).getApproveQty()>0 || getStockBetweenDate.get(i).getApprovedQtyValue()>0 || 
							getStockBetweenDate.get(i).getIssueQty()>0 || getStockBetweenDate.get(i).getApprovedQtyValue()>0 || 
							getStockBetweenDate.get(i).getIssueQty()>0 || getStockBetweenDate.get(i).getIssueQtyValue()>0 
							|| getStockBetweenDate.get(i).getDamageQty()>0 || getStockBetweenDate.get(i).getDamagValue()>0) {
					expoExcel = new ExportToExcel();
					rowData = new ArrayList<String>();
					k++;
					rowData.add((k)+"");
					rowData.add(getStockBetweenDate.get(i).getItemCode());
					rowData.add(""+getStockBetweenDate.get(i).getOpeningStock());
					rowData.add(""+getStockBetweenDate.get(i).getOpStockValue());
					rowData.add(""+getStockBetweenDate.get(i).getApproveQty());
					rowData.add(""+getStockBetweenDate.get(i).getApprovedQtyValue());
					rowData.add(""+getStockBetweenDate.get(i).getIssueQty());
					rowData.add(""+getStockBetweenDate.get(i).getIssueQtyValue());
					rowData.add(""+getStockBetweenDate.get(i).getDamageQty());
					rowData.add(""+getStockBetweenDate.get(i).getDamagValue());
					
					float closingQty = getStockBetweenDate.get(i).getOpeningStock()+getStockBetweenDate.get(i).getApproveQty()-
							getStockBetweenDate.get(i).getIssueQty()-getStockBetweenDate.get(i).getDamageQty();
					
					float closingValue = getStockBetweenDate.get(i).getOpStockValue()+getStockBetweenDate.get(i).getApprovedQtyValue()-
							getStockBetweenDate.get(i).getIssueQtyValue()-getStockBetweenDate.get(i).getDamagValue();
					
					
					rowData.add(""+closingQty);
					rowData.add(""+closingValue);


					expoExcel.setRowData(rowData);
					exportToExcelList.add(expoExcel);
					}

				}

				HttpSession session = request.getSession();
				session.setAttribute("exportExcelList", exportToExcelList);
				session.setAttribute("excelName", "ItemWiseStockValue");
				
		} catch (Exception e) {
			e.printStackTrace();
		}

		 
		return model;
	}
	
	@RequestMapping(value = "/stockValuetionReportItemWisePDF/{type}/{catDesc}", method = RequestMethod.GET)
	public void stockValuetionReportItemWisePDF(@PathVariable String type,@PathVariable String catDesc, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		BufferedOutputStream outStream = null;
		try {
		Document document = new Document(PageSize.A3);
		DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
		String reportDate = DF.format(new Date());
        document.addHeader("Date: ", reportDate);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("time in Gen Bill PDF ==" + dateFormat.format(cal.getTime()));
		String timeStamp = dateFormat.format(cal.getTime());
		String FILE_PATH = Constants.REPORT_SAVE;
		File file = new File(FILE_PATH);

		PdfWriter writer = null;

		FileOutputStream out = new FileOutputStream(FILE_PATH);
		try {
			writer = PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
		
		float totalOpQty=0;
		float totalOpValue=0;
		float totalMrnQty=0;
		float totalMrnValue=0;
		float totalIssueQty=0;
		float totalIssueValue=0;
		float totalDamageQty=0;
		float totalDamageValue=0;
		float totalClQty=0;
		float totalClValue=0;
	
		PdfPTable table = new PdfPTable(12);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] {0.4f, 4.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f});
			Font headFont = new Font(FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 11.0f, Font.UNDERLINE, BaseColor.BLUE);
			Font f1 = new Font(FontFamily.TIMES_ROMAN, 9.0f, Font.BOLD, BaseColor.DARK_GRAY);

			PdfPCell hcell = new PdfPCell();
			
			hcell.setPadding(4);
			hcell = new PdfPCell(new Phrase("SR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("ITEM NAME", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("OP QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("OP VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("MRN QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("MRN VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("ISSUE QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("ISSUE VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("DAMAGE QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("DAMAGE VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("C/L QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("C/L VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			
			int index = 0;
			if(!itemWiseStockValuetionListForPDF.isEmpty()) {
					for (int k = 0; k < itemWiseStockValuetionListForPDF.size(); k++) {
                            
						if(itemWiseStockValuetionListForPDF.get(k).getOpeningStock()>0 || itemWiseStockValuetionListForPDF.get(k).getOpStockValue()>0 
								|| itemWiseStockValuetionListForPDF.get(k).getApproveQty()>0 || itemWiseStockValuetionListForPDF.get(k).getApprovedQtyValue()>0
								|| itemWiseStockValuetionListForPDF.get(k).getIssueQty()>0 || itemWiseStockValuetionListForPDF.get(k).getIssueQtyValue()>0
								|| itemWiseStockValuetionListForPDF.get(k).getDamageQty()>0 || itemWiseStockValuetionListForPDF.get(k).getDamagValue()>0) {
							
						
							index++;
						
							PdfPCell cell; 
							
							cell = new PdfPCell(new Phrase(""+index, headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPadding(3);
							table.addCell(cell);
						
							cell = new PdfPCell(new Phrase(itemWiseStockValuetionListForPDF.get(k).getItemCode(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
						
							cell = new PdfPCell(new Phrase(""+df.format(itemWiseStockValuetionListForPDF.get(k).getOpeningStock()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalOpQty=totalOpQty+itemWiseStockValuetionListForPDF.get(k).getOpeningStock();
							
							cell = new PdfPCell(new Phrase(""+df.format(itemWiseStockValuetionListForPDF.get(k).getOpStockValue()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalOpValue=totalOpValue+itemWiseStockValuetionListForPDF.get(k).getOpStockValue();
							
							cell = new PdfPCell(new Phrase(""+df.format(itemWiseStockValuetionListForPDF.get(k).getApproveQty()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalMrnQty=totalMrnQty+itemWiseStockValuetionListForPDF.get(k).getApproveQty();
							
							cell = new PdfPCell(new Phrase(""+df.format(itemWiseStockValuetionListForPDF.get(k).getApprovedQtyValue()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalMrnValue=totalMrnValue+itemWiseStockValuetionListForPDF.get(k).getApprovedQtyValue();
							
							cell = new PdfPCell(new Phrase(""+df.format(itemWiseStockValuetionListForPDF.get(k).getIssueQty()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalIssueQty=totalIssueQty+itemWiseStockValuetionListForPDF.get(k).getIssueQty();
							
							cell = new PdfPCell(new Phrase(""+df.format(itemWiseStockValuetionListForPDF.get(k).getIssueQtyValue()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalIssueValue=totalIssueValue+itemWiseStockValuetionListForPDF.get(k).getIssueQtyValue();
							
							cell = new PdfPCell(new Phrase(""+df.format(itemWiseStockValuetionListForPDF.get(k).getDamageQty()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalDamageQty=totalDamageQty+itemWiseStockValuetionListForPDF.get(k).getDamageQty();
							
							cell = new PdfPCell(new Phrase(""+df.format(itemWiseStockValuetionListForPDF.get(k).getDamagValue()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalDamageValue=totalDamageValue+itemWiseStockValuetionListForPDF.get(k).getDamagValue();
							
							float closingQty = itemWiseStockValuetionListForPDF.get(k).getOpeningStock()+itemWiseStockValuetionListForPDF.get(k).getApproveQty()-
									itemWiseStockValuetionListForPDF.get(k).getIssueQty()-itemWiseStockValuetionListForPDF.get(k).getDamageQty();
							
							float closingValue = itemWiseStockValuetionListForPDF.get(k).getOpStockValue()+itemWiseStockValuetionListForPDF.get(k).getApprovedQtyValue()-
									itemWiseStockValuetionListForPDF.get(k).getIssueQtyValue()-itemWiseStockValuetionListForPDF.get(k).getDamagValue();
							
							totalClQty=totalClQty+closingQty;
							totalClValue=totalClValue+closingValue;
							
							cell = new PdfPCell(new Phrase(""+df.format(closingQty), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							
							cell = new PdfPCell(new Phrase(""+df.format(closingValue), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
						}
					}
			}
			
			PdfPCell cell;
			 
			cell = new PdfPCell(new Phrase("Total ", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			cell.setColspan(2);
			table.addCell(cell);
		
			cell = new PdfPCell(new Phrase(""+df.format(totalOpQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			cell = new PdfPCell(new Phrase(""+df.format(totalOpValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format(totalMrnQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format(totalMrnValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format(totalIssueQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format(totalIssueValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format(totalDamageQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format(totalDamageValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			  
		 
			cell = new PdfPCell(new Phrase(""+df.format(totalClQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format(totalClValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			
			document.open();
			Paragraph company = new Paragraph(companyInfo.getCompanyName()+"\n", f);
			company.setAlignment(Element.ALIGN_CENTER);
			document.add(company);
			
				Paragraph heading1 = new Paragraph(
						companyInfo.getFactoryAdd(),f1);
				heading1.setAlignment(Element.ALIGN_CENTER);
				document.add(heading1);
				Paragraph ex2=new Paragraph("\n");
				document.add(ex2);
				
				Paragraph reportName=new Paragraph("STOCK VALUEATION REPORT ITEM WISE",f1);
				reportName.setAlignment(Element.ALIGN_CENTER);
				document.add(reportName);
				
				Paragraph typeName=new Paragraph("Category : "+ catDesc +", Type : " + type,f1);
				typeName.setAlignment(Element.ALIGN_CENTER);
				document.add(typeName);
				
				Paragraph headingDate=new Paragraph("From Date: " + fromDate+"  To Date: "+toDate+"",f1);
				headingDate.setAlignment(Element.ALIGN_CENTER);
				document.add(headingDate);
 
			
			Paragraph ex3=new Paragraph("\n");
			document.add(ex3);
			table.setHeaderRows(1);
			document.add(table);
			
		
			int totalPages = writer.getPageNumber();

			System.out.println("Page no " + totalPages);

			document.close();
			// Atul Sir code to open a Pdf File
			if (file != null) {

				String mimeType = URLConnection.guessContentTypeFromName(file.getName());

				if (mimeType == null) {

					mimeType = "application/pdf";

				}

				response.setContentType(mimeType);

				response.addHeader("content-disposition", String.format("inline; filename=\"%s\"", file.getName()));

				response.setContentLength((int) file.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				try {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				} catch (IOException e) {
					System.out.println("Excep in Opening a Pdf File");
					e.printStackTrace();
				}
			}

		} catch (DocumentException ex) {

			System.out.println("Pdf Generation Error" + ex.getMessage());

			ex.printStackTrace();

		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/stockValuetionReportItemWisePDFOnlyClStock/{type}/{catDesc}", method = RequestMethod.GET)
	public void stockValuetionReportItemWisePDFOnlyClStock(@PathVariable String type,@PathVariable String catDesc, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		BufferedOutputStream outStream = null;
		try {
		Document document = new Document(PageSize.A4);
		DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
		String reportDate = DF.format(new Date());
        document.addHeader("Date: ", reportDate);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("time in Gen Bill PDF ==" + dateFormat.format(cal.getTime()));
		String timeStamp = dateFormat.format(cal.getTime());
		String FILE_PATH = Constants.REPORT_SAVE;
		File file = new File(FILE_PATH);

		PdfWriter writer = null;

		FileOutputStream out = new FileOutputStream(FILE_PATH);
		try {
			writer = PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
		
		float totalOpQty=0;
		float totalOpValue=0;
		float totalMrnQty=0;
		float totalMrnValue=0;
		float totalIssueQty=0;
		float totalIssueValue=0;
		float totalDamageQty=0;
		float totalDamageValue=0;
		float totalClQty=0;
		float totalClValue=0;
	
		PdfPTable table = new PdfPTable(4);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] {0.6f, 6.0f, 1.0f, 1.0f });
			Font headFont = new Font(FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 11.0f, Font.UNDERLINE, BaseColor.BLUE);
			Font f1 = new Font(FontFamily.TIMES_ROMAN, 9.0f, Font.BOLD, BaseColor.DARK_GRAY);

			PdfPCell hcell = new PdfPCell();
			
			hcell.setPadding(4);
			hcell = new PdfPCell(new Phrase("SR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("ITEM NAME", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			 
			
			hcell = new PdfPCell(new Phrase("C/L QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("C/L VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			
			int index = 0;
			if(!itemWiseStockValuetionListForPDF.isEmpty()) {
					for (int k = 0; k < itemWiseStockValuetionListForPDF.size(); k++) {
                            
						if(itemWiseStockValuetionListForPDF.get(k).getOpeningStock()>0 || itemWiseStockValuetionListForPDF.get(k).getOpStockValue()>0 
								|| itemWiseStockValuetionListForPDF.get(k).getApproveQty()>0 || itemWiseStockValuetionListForPDF.get(k).getApprovedQtyValue()>0
								|| itemWiseStockValuetionListForPDF.get(k).getIssueQty()>0 || itemWiseStockValuetionListForPDF.get(k).getIssueQtyValue()>0
								|| itemWiseStockValuetionListForPDF.get(k).getDamageQty()>0 || itemWiseStockValuetionListForPDF.get(k).getDamagValue()>0) {
							
						
							index++;
						
							PdfPCell cell; 
							
							cell = new PdfPCell(new Phrase(""+index, headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPadding(3);
							table.addCell(cell);
						
							cell = new PdfPCell(new Phrase(itemWiseStockValuetionListForPDF.get(k).getItemCode(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
						 
							float closingQty = itemWiseStockValuetionListForPDF.get(k).getOpeningStock()+itemWiseStockValuetionListForPDF.get(k).getApproveQty()-
									itemWiseStockValuetionListForPDF.get(k).getIssueQty()-itemWiseStockValuetionListForPDF.get(k).getDamageQty();
							
							float closingValue = itemWiseStockValuetionListForPDF.get(k).getOpStockValue()+itemWiseStockValuetionListForPDF.get(k).getApprovedQtyValue()-
									itemWiseStockValuetionListForPDF.get(k).getIssueQtyValue()-itemWiseStockValuetionListForPDF.get(k).getDamagValue();
							
							totalClQty=totalClQty+closingQty;
							totalClValue=totalClValue+closingValue;
							
							cell = new PdfPCell(new Phrase(""+df.format(closingQty), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							
							cell = new PdfPCell(new Phrase(""+df.format(closingValue), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
						}
					}
			}
			
			PdfPCell cell;
			 
			cell = new PdfPCell(new Phrase("Total ", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			cell.setColspan(2);
			table.addCell(cell);
		 
			cell = new PdfPCell(new Phrase(""+df.format(totalClQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format(totalClValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			
			document.open();
			Paragraph company = new Paragraph(companyInfo.getCompanyName()+"\n", f);
			company.setAlignment(Element.ALIGN_CENTER);
			document.add(company);
			
				Paragraph heading1 = new Paragraph(
						companyInfo.getFactoryAdd(),f1);
				heading1.setAlignment(Element.ALIGN_CENTER);
				document.add(heading1);
				Paragraph ex2=new Paragraph("\n");
				document.add(ex2);
				
				Paragraph reportName=new Paragraph("STOCK VALUEATION REPORT ITEM WISE",f1);
				reportName.setAlignment(Element.ALIGN_CENTER);
				document.add(reportName);
				
				Paragraph typeName=new Paragraph("Category : "+ catDesc +", Type : " + type,f1);
				typeName.setAlignment(Element.ALIGN_CENTER);
				document.add(typeName);
				
				Paragraph headingDate=new Paragraph("From Date: " + fromDate+"  To Date: "+toDate+"",f1);
				headingDate.setAlignment(Element.ALIGN_CENTER);
				document.add(headingDate);
 
			
			Paragraph ex3=new Paragraph("\n");
			document.add(ex3);
			table.setHeaderRows(1);
			document.add(table);
			
		
			int totalPages = writer.getPageNumber();

			System.out.println("Page no " + totalPages);

			document.close();
			// Atul Sir code to open a Pdf File
			if (file != null) {

				String mimeType = URLConnection.guessContentTypeFromName(file.getName());

				if (mimeType == null) {

					mimeType = "application/pdf";

				}

				response.setContentType(mimeType);

				response.addHeader("content-disposition", String.format("inline; filename=\"%s\"", file.getName()));

				response.setContentLength((int) file.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				try {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				} catch (IOException e) {
					System.out.println("Excep in Opening a Pdf File");
					e.printStackTrace();
				}
			}

		} catch (DocumentException ex) {

			System.out.println("Pdf Generation Error" + ex.getMessage());

			ex.printStackTrace();

		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/stockValuetionReportItemWisePDFOnlyOpStock/{type}/{catDesc}", method = RequestMethod.GET)
	public void stockValuetionReportItemWisePDFOnlyOpStock(@PathVariable String type,@PathVariable String catDesc, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		BufferedOutputStream outStream = null;
		try {
		Document document = new Document(PageSize.A4);
		DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
		String reportDate = DF.format(new Date());
        document.addHeader("Date: ", reportDate);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("time in Gen Bill PDF ==" + dateFormat.format(cal.getTime()));
		String timeStamp = dateFormat.format(cal.getTime());
		String FILE_PATH = Constants.REPORT_SAVE;
		File file = new File(FILE_PATH);

		PdfWriter writer = null;

		FileOutputStream out = new FileOutputStream(FILE_PATH);
		try {
			writer = PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
		
		float totalOpQty=0;
		float totalOpValue=0; 
	
		PdfPTable table = new PdfPTable(4);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] {0.6f, 6.0f, 1.0f, 1.0f });
			Font headFont = new Font(FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 11.0f, Font.UNDERLINE, BaseColor.BLUE);
			Font f1 = new Font(FontFamily.TIMES_ROMAN, 9.0f, Font.BOLD, BaseColor.DARK_GRAY);

			PdfPCell hcell = new PdfPCell();
			
			hcell.setPadding(4);
			hcell = new PdfPCell(new Phrase("SR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("ITEM NAME", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("OP QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("OP VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			 
			int index = 0;
			if(!itemWiseStockValuetionListForPDF.isEmpty()) {
					for (int k = 0; k < itemWiseStockValuetionListForPDF.size(); k++) {
                            
						if(itemWiseStockValuetionListForPDF.get(k).getOpeningStock()>0 || itemWiseStockValuetionListForPDF.get(k).getOpStockValue()>0  ) {
							
						
							index++;
						
							PdfPCell cell; 
							
							cell = new PdfPCell(new Phrase(""+index, headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPadding(3);
							table.addCell(cell);
						
							cell = new PdfPCell(new Phrase(itemWiseStockValuetionListForPDF.get(k).getItemCode(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
						
							cell = new PdfPCell(new Phrase(""+df.format(itemWiseStockValuetionListForPDF.get(k).getOpeningStock()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalOpQty=totalOpQty+itemWiseStockValuetionListForPDF.get(k).getOpeningStock();
							
							cell = new PdfPCell(new Phrase(""+df.format(itemWiseStockValuetionListForPDF.get(k).getOpStockValue()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalOpValue=totalOpValue+itemWiseStockValuetionListForPDF.get(k).getOpStockValue();
							
							 
						}
					}
			}
			
			PdfPCell cell;
			 
			cell = new PdfPCell(new Phrase("Total ", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			cell.setColspan(2);
			table.addCell(cell);
		
			cell = new PdfPCell(new Phrase(""+df.format(totalOpQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			cell = new PdfPCell(new Phrase(""+df.format(totalOpValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			  
			document.open();
			Paragraph company = new Paragraph(companyInfo.getCompanyName()+"\n", f);
			company.setAlignment(Element.ALIGN_CENTER);
			document.add(company);
			
				Paragraph heading1 = new Paragraph(
						companyInfo.getFactoryAdd(),f1);
				heading1.setAlignment(Element.ALIGN_CENTER);
				document.add(heading1);
				Paragraph ex2=new Paragraph("\n");
				document.add(ex2);
				
				Paragraph reportName=new Paragraph("STOCK VALUEATION REPORT ITEM WISE",f1);
				reportName.setAlignment(Element.ALIGN_CENTER);
				document.add(reportName);
				
				Paragraph typeName=new Paragraph("Category : "+ catDesc +", Type : " + type,f1);
				typeName.setAlignment(Element.ALIGN_CENTER);
				document.add(typeName);
				
				Paragraph headingDate=new Paragraph("From Date: " + fromDate+"  To Date: "+toDate+"",f1);
				headingDate.setAlignment(Element.ALIGN_CENTER);
				document.add(headingDate);
 
			
			Paragraph ex3=new Paragraph("\n");
			document.add(ex3);
			table.setHeaderRows(1);
			document.add(table);
			
		
			int totalPages = writer.getPageNumber();

			System.out.println("Page no " + totalPages);

			document.close();
			// Atul Sir code to open a Pdf File
			if (file != null) {

				String mimeType = URLConnection.guessContentTypeFromName(file.getName());

				if (mimeType == null) {

					mimeType = "application/pdf";

				}

				response.setContentType(mimeType);

				response.addHeader("content-disposition", String.format("inline; filename=\"%s\"", file.getName()));

				response.setContentLength((int) file.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				try {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				} catch (IOException e) {
					System.out.println("Excep in Opening a Pdf File");
					e.printStackTrace();
				}
			}

		} catch (DocumentException ex) {

			System.out.println("Pdf Generation Error" + ex.getMessage());

			ex.printStackTrace();

		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	List<StockValuationCategoryWise> categoryWiseIssueAndMrnForPdf = new ArrayList<StockValuationCategoryWise>();
	
	@RequestMapping(value = "/issueAndMrnReportCategoryWise", method = RequestMethod.GET)
	public ModelAndView issueAndMrnReportCategoryWise(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("valuationReport/issueAndMrnReportCategoryWise");
		try {
			List<StockValuationCategoryWise> categoryWiseReport = new ArrayList<StockValuationCategoryWise>();
			Type[] type = rest.getForObject(Constants.url + "/getAlltype", Type[].class);
			List<Type> typeList = new ArrayList<Type>(Arrays.asList(type));
			model.addObject("typeList", typeList);
			
			if(request.getParameter("fromDate")==null || request.getParameter("toDate")==null || request.getParameter("typeId")==null || request.getParameter("isDev")==null) {
				
				SimpleDateFormat yy = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy");
				Date date = new Date();
				  Calendar calendar = Calendar.getInstance();
				  calendar.setTime(date);
				   
				 fromDate = "01"+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR);
				 toDate = dd.format(date);
				 typeId=0;
				 isDev=-1;
				 
				 MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
				 map.add("fromDate",DateConvertor.convertToYMD(fromDate));
		 			map.add("toDate",yy.format(date)); 
		 			map.add("typeId", typeId);
		 			map.add("isDev", isDev);
		 			System.out.println(map);
		 			StockValuationCategoryWise[] stockValuationCategoryWise1 = rest.postForObject(Constants.url + "/issueAndMrnCatWiseReport",map, StockValuationCategoryWise[].class);
					 categoryWiseReport = new ArrayList<StockValuationCategoryWise>(Arrays.asList(stockValuationCategoryWise1));
				 
				model.addObject("categoryWiseReport", categoryWiseReport);
				model.addObject("fromDate", fromDate);
				model.addObject("toDate", dd.format(date));
			}
			else {
				fromDate = request.getParameter("fromDate");
				toDate = request.getParameter("toDate");
				typeId = Integer.parseInt(request.getParameter("typeId"));
				isDev =Integer.parseInt(request.getParameter("isDev"));
				 
					MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
					map.add("fromDate",DateConvertor.convertToYMD(fromDate));
		 			map.add("toDate",DateConvertor.convertToYMD(toDate)); 
		 			map.add("typeId", typeId);
		 			map.add("isDev", isDev);
		 			System.out.println(map);
		 			StockValuationCategoryWise[] stockValuationCategoryWise1 = rest.postForObject(Constants.url + "/issueAndMrnCatWiseReport",map, StockValuationCategoryWise[].class);
					 categoryWiseReport = new ArrayList<StockValuationCategoryWise>(Arrays.asList(stockValuationCategoryWise1));
				 
				model.addObject("categoryWiseReport", categoryWiseReport);
				model.addObject("fromDate", fromDate);
				model.addObject("toDate", toDate);
				model.addObject("typeId", typeId);
				model.addObject("isDevelompent", isDev);
				
				
			}
			
			
			//----------------exel-------------------------
			
			categoryWiseIssueAndMrnForPdf=categoryWiseReport;
			
			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

			ExportToExcel expoExcel = new ExportToExcel();
			List<String> rowData = new ArrayList<String>();

			rowData.add("SR. No");
			rowData.add("CATEGORY NAME"); 
			rowData.add("MRN QTY");
			rowData.add("MRN VALUE");
			rowData.add("ISSUE QTY");
			rowData.add("ISSUE VALUE"); 
			

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);
			int k=0;
			for (int i = 0; i < categoryWiseReport.size(); i++) {
				if( categoryWiseReport.get(i).getApproveQty()>0 || categoryWiseReport.get(i).getApprovedQtyValue()>0 || 
						categoryWiseReport.get(i).getIssueQty()>0 || categoryWiseReport.get(i).getApprovedQtyValue()>0 ) {
				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();
				k++;
				rowData.add((k)+"");
				rowData.add(categoryWiseReport.get(i).getCatDesc()); 
				rowData.add(""+categoryWiseReport.get(i).getApproveQty());
				rowData.add(""+categoryWiseReport.get(i).getApprovedQtyValue());
				rowData.add(""+categoryWiseReport.get(i).getIssueQty());
				rowData.add(""+categoryWiseReport.get(i).getIssueQtyValue()); 
				 
				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);
				}

			}

			HttpSession session = request.getSession();
			session.setAttribute("exportExcelList", exportToExcelList);
			session.setAttribute("excelName", "CategoryWiseMrnAndIssue");
			
			companyInfo = rest.getForObject(Constants.url + "getCompanyDetails",
					Company.class);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/listForIssueAndMrnGraphCategoryWise", method = RequestMethod.GET)
	public @ResponseBody List<StockValuationCategoryWise> listForIssueAndMrnGraph(HttpServletRequest request, HttpServletResponse response) {

		 
		return categoryWiseIssueAndMrnForPdf;
	}
	
	@RequestMapping(value = "/issueAndMrnCategoryWisePDF/{type}/{isDev}", method = RequestMethod.GET)
	public void issueAndMrnCategoryWisePDF(@PathVariable String type,@PathVariable String isDev,HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		BufferedOutputStream outStream = null;
		try {
		Document document = new Document(PageSize.A4);
		DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
		String reportDate = DF.format(new Date());
        document.addHeader("Date: ", reportDate);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("time in Gen Bill PDF ==" + dateFormat.format(cal.getTime()));
		String timeStamp = dateFormat.format(cal.getTime());
		String FILE_PATH = Constants.REPORT_SAVE;
		File file = new File(FILE_PATH);

		PdfWriter writer = null;

		FileOutputStream out = new FileOutputStream(FILE_PATH);
		try {
			writer = PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
		
		float totalMrnQty=0;
		float totalMrnValue=0;
		float totalIssueQty=0;
		float totalIssueValue=0;
	
		PdfPTable table = new PdfPTable(6);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] {0.4f, 3.0f, 1.0f, 1.0f, 1.0f, 1.0f});
			Font headFont = new Font(FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 11.0f, Font.UNDERLINE, BaseColor.BLUE);
			Font f1 = new Font(FontFamily.TIMES_ROMAN, 9.0f, Font.BOLD, BaseColor.DARK_GRAY);

			PdfPCell hcell = new PdfPCell();
			
			hcell.setPadding(4);
			hcell = new PdfPCell(new Phrase("SR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("CATEGORY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			  
			hcell = new PdfPCell(new Phrase("MRN QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("MRN VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("ISSUE QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("ISSUE VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			  
			int index = 0;
			if(!categoryWiseIssueAndMrnForPdf.isEmpty()) {
					for (int k = 0; k < categoryWiseIssueAndMrnForPdf.size(); k++) {
                            
						if(categoryWiseIssueAndMrnForPdf.get(k).getOpeningStock()>0 || categoryWiseIssueAndMrnForPdf.get(k).getOpStockValue()>0 
								|| categoryWiseIssueAndMrnForPdf.get(k).getApproveQty()>0 || categoryWiseIssueAndMrnForPdf.get(k).getApprovedQtyValue()>0
								|| categoryWiseIssueAndMrnForPdf.get(k).getIssueQty()>0 || categoryWiseIssueAndMrnForPdf.get(k).getIssueQtyValue()>0
								|| categoryWiseIssueAndMrnForPdf.get(k).getDamageQty()>0 || categoryWiseIssueAndMrnForPdf.get(k).getDamageValue()>0) {
							
						
							index++;
						
							PdfPCell cell;
							
							cell = new PdfPCell(new Phrase(""+index, headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPadding(3);
							table.addCell(cell);

						
							cell = new PdfPCell(new Phrase(categoryWiseIssueAndMrnForPdf.get(k).getCatDesc(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
						 
							cell = new PdfPCell(new Phrase(""+df.format(categoryWiseIssueAndMrnForPdf.get(k).getApproveQty()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalMrnQty=totalMrnQty+categoryWiseIssueAndMrnForPdf.get(k).getApproveQty();
							
							cell = new PdfPCell(new Phrase(""+df.format(categoryWiseIssueAndMrnForPdf.get(k).getApprovedQtyValue()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalMrnValue=totalMrnValue+categoryWiseIssueAndMrnForPdf.get(k).getApprovedQtyValue();
							
							cell = new PdfPCell(new Phrase(""+df.format(categoryWiseIssueAndMrnForPdf.get(k).getIssueQty()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalIssueQty=totalIssueQty+categoryWiseIssueAndMrnForPdf.get(k).getIssueQty();
							
							cell = new PdfPCell(new Phrase(""+df.format(categoryWiseIssueAndMrnForPdf.get(k).getIssueQtyValue()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalIssueValue=totalIssueValue+categoryWiseIssueAndMrnForPdf.get(k).getIssueQtyValue();
							 
						}
					}
			}
			
			PdfPCell cell;
			
			cell = new PdfPCell(new Phrase("Total", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setPadding(3);
			cell.setColspan(2);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(totalMrnQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(totalMrnValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(totalIssueQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(totalIssueValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			
			document.open();
			Paragraph company = new Paragraph(companyInfo.getCompanyName()+"\n", f);
			company.setAlignment(Element.ALIGN_CENTER);
			document.add(company);
			
				Paragraph heading1 = new Paragraph(
						companyInfo.getFactoryAdd(),f1);
				heading1.setAlignment(Element.ALIGN_CENTER);
				document.add(heading1);
				Paragraph ex2=new Paragraph("\n");
				document.add(ex2);
				
				Paragraph reportName=new Paragraph("RECEIPT/ISSUE VALUEATION REPORT",f1);
				reportName.setAlignment(Element.ALIGN_CENTER);
				document.add(reportName);
				
				Paragraph typeName=new Paragraph("Type : " + type +",  Is Dev :"+isDev,f1);
				typeName.setAlignment(Element.ALIGN_CENTER);
				document.add(typeName);
				
				Paragraph headingDate=new Paragraph("From Date: " + fromDate+"  To Date: "+toDate+"",f1);
				headingDate.setAlignment(Element.ALIGN_CENTER);
				document.add(headingDate);
 
			Paragraph ex3=new Paragraph("\n");
			document.add(ex3);
			table.setHeaderRows(1);
			document.add(table);
			
		
			int totalPages = writer.getPageNumber();

			System.out.println("Page no " + totalPages);

			document.close();
			// Atul Sir code to open a Pdf File
			if (file != null) {

				String mimeType = URLConnection.guessContentTypeFromName(file.getName());

				if (mimeType == null) {

					mimeType = "application/pdf";

				}

				response.setContentType(mimeType);

				response.addHeader("content-disposition", String.format("inline; filename=\"%s\"", file.getName()));

				response.setContentLength((int) file.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				try {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				} catch (IOException e) {
					System.out.println("Excep in Opening a Pdf File");
					e.printStackTrace();
				}
			}

		} catch (DocumentException ex) {

			System.out.println("Pdf Generation Error" + ex.getMessage());

			ex.printStackTrace();

		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	List<IssueAndMrnGroupWise> issueAndMrnGroupWiseListForPdf = new ArrayList<IssueAndMrnGroupWise>();
	
	@RequestMapping(value = "/issueAndMrnReportGroupWise/{catId}/{type}/{isDevName}/{catDesc}", method = RequestMethod.GET)
	public ModelAndView issueAndMrnReportGroupWise(@PathVariable int catId,@PathVariable String type,
			@PathVariable String isDevName,@PathVariable String catDesc,HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("valuationReport/issueAndMrnReportGroupWise");
		List<IssueAndMrnGroupWise> groupWiseList = new ArrayList<IssueAndMrnGroupWise>();
		
		try {
		   
				MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
				map.add("fromDate",DateConvertor.convertToYMD(fromDate));
	 			map.add("toDate",DateConvertor.convertToYMD(toDate)); 
	 			map.add("catId", catId);
	 			map.add("typeId", typeId);
	 			map.add("isDev", isDev);
	 			System.out.println(map);
	 			IssueAndMrnGroupWise[] issueAndMrnGroupWise = rest.postForObject(Constants.url + "/issueAndMrnGroupWisReportByCatId",map,IssueAndMrnGroupWise[].class); 
	 			groupWiseList = new ArrayList<IssueAndMrnGroupWise>(Arrays.asList(issueAndMrnGroupWise));
	 			
	 			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

				ExportToExcel expoExcel = new ExportToExcel();
				List<String> rowData = new ArrayList<String>();

				rowData.add("SR. No");
				rowData.add("GROUP NAME"); 
				rowData.add("MRN QTY");
				rowData.add("MRN VALUE");
				rowData.add("ISSUE QTY");
				rowData.add("ISSUE VALUE"); 
				

				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);
				int k=0;
				for (int i = 0; i < groupWiseList.size(); i++) {
					if( groupWiseList.get(i).getApproveQty()>0 || groupWiseList.get(i).getApprovedQtyValue()>0 || 
							groupWiseList.get(i).getIssueQty()>0 || groupWiseList.get(i).getApprovedQtyValue()>0 ) {
					expoExcel = new ExportToExcel();
					rowData = new ArrayList<String>();
					k++;
					rowData.add((k)+"");
					rowData.add(groupWiseList.get(i).getGrpCode()); 
					rowData.add(""+groupWiseList.get(i).getApproveQty());
					rowData.add(""+groupWiseList.get(i).getApprovedQtyValue());
					rowData.add(""+groupWiseList.get(i).getIssueQty());
					rowData.add(""+groupWiseList.get(i).getIssueQtyValue()); 
					 
					expoExcel.setRowData(rowData);
					exportToExcelList.add(expoExcel);
					}

				}

				HttpSession session = request.getSession();
				session.setAttribute("exportExcelList", exportToExcelList);
				session.setAttribute("excelName", "GroupWiseMrnAndIssue");
			 
				issueAndMrnGroupWiseListForPdf = groupWiseList;
				
			 model.addObject("list",groupWiseList);
			 model.addObject("type",type);
			 model.addObject("isDevName",isDevName);
			 model.addObject("catDesc",catDesc); 
		} catch (Exception e) {
			e.printStackTrace();
		}

		 
		return model;
	}
	
	@RequestMapping(value = "/listForIssueAndMrnGraphGroupWise", method = RequestMethod.GET)
	public @ResponseBody List<IssueAndMrnGroupWise> listForIssueAndMrnGraphGroupWise(HttpServletRequest request, HttpServletResponse response) {

		 
		return issueAndMrnGroupWiseListForPdf;
	}
	
	@RequestMapping(value = "/issueAndMrnGroupWisePDF/{type}/{isDevName}/{catDesc}", method = RequestMethod.GET)
	public void issueAndMrnGroupWisePDF(@PathVariable String type,@PathVariable String isDevName,@PathVariable String catDesc,
			HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		BufferedOutputStream outStream = null;
		try {
		Document document = new Document(PageSize.A4);
		DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
		String reportDate = DF.format(new Date());
        document.addHeader("Date: ", reportDate);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("time in Gen Bill PDF ==" + dateFormat.format(cal.getTime()));
		String timeStamp = dateFormat.format(cal.getTime());
		String FILE_PATH = Constants.REPORT_SAVE;
		File file = new File(FILE_PATH);

		PdfWriter writer = null;

		FileOutputStream out = new FileOutputStream(FILE_PATH);
		try {
			writer = PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
		
		float totalMrnQty=0;
		float totalMrnValue=0;
		float totalIssueQty=0;
		float totalIssueValue=0;
	
		PdfPTable table = new PdfPTable(6);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] {0.4f, 3.0f, 1.0f, 1.0f, 1.0f, 1.0f});
			Font headFont = new Font(FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 11.0f, Font.UNDERLINE, BaseColor.BLUE);
			Font f1 = new Font(FontFamily.TIMES_ROMAN, 9.0f, Font.BOLD, BaseColor.DARK_GRAY);

			PdfPCell hcell = new PdfPCell();
			
			hcell.setPadding(4);
			hcell = new PdfPCell(new Phrase("SR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("GROUP", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			  
			hcell = new PdfPCell(new Phrase("MRN QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("MRN VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("ISSUE QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("ISSUE VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			  
			int index = 0;
			if(!issueAndMrnGroupWiseListForPdf.isEmpty()) {
					for (int k = 0; k < issueAndMrnGroupWiseListForPdf.size(); k++) {
                            
						if( issueAndMrnGroupWiseListForPdf.get(k).getApproveQty()>0 || issueAndMrnGroupWiseListForPdf.get(k).getApprovedQtyValue()>0
								|| issueAndMrnGroupWiseListForPdf.get(k).getIssueQty()>0 || issueAndMrnGroupWiseListForPdf.get(k).getIssueQtyValue()>0
								 ) {
							
						
							index++;
						
							PdfPCell cell;
							
							cell = new PdfPCell(new Phrase(""+index, headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPadding(3);
							table.addCell(cell);

						
							cell = new PdfPCell(new Phrase(issueAndMrnGroupWiseListForPdf.get(k).getGrpCode(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
						 
							cell = new PdfPCell(new Phrase(""+df.format(issueAndMrnGroupWiseListForPdf.get(k).getApproveQty()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalMrnQty=totalMrnQty+issueAndMrnGroupWiseListForPdf.get(k).getApproveQty();
							
							cell = new PdfPCell(new Phrase(""+df.format(issueAndMrnGroupWiseListForPdf.get(k).getApprovedQtyValue()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalMrnValue=totalMrnValue+issueAndMrnGroupWiseListForPdf.get(k).getApprovedQtyValue();
							
							cell = new PdfPCell(new Phrase(""+df.format(issueAndMrnGroupWiseListForPdf.get(k).getIssueQty()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalIssueQty=totalIssueQty+issueAndMrnGroupWiseListForPdf.get(k).getIssueQty();
							
							cell = new PdfPCell(new Phrase(""+df.format(issueAndMrnGroupWiseListForPdf.get(k).getIssueQtyValue()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalIssueValue=totalIssueValue+issueAndMrnGroupWiseListForPdf.get(k).getIssueQtyValue();
						}
					}
			}
			
			PdfPCell cell;
			
			cell = new PdfPCell(new Phrase("Total", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setPadding(3);
			cell.setColspan(2);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(totalMrnQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(totalMrnValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(totalIssueQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(totalIssueValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			
			document.open();
			Paragraph company = new Paragraph(companyInfo.getCompanyName()+"\n", f);
			company.setAlignment(Element.ALIGN_CENTER);
			document.add(company);
			
				Paragraph heading1 = new Paragraph(
						companyInfo.getFactoryAdd(),f1);
				heading1.setAlignment(Element.ALIGN_CENTER);
				document.add(heading1);
				Paragraph ex2=new Paragraph("\n");
				document.add(ex2);
				
				Paragraph reportName=new Paragraph("RECEIPT/ISSUE VALUEATION REPORT GROUP WISE",f1);
				reportName.setAlignment(Element.ALIGN_CENTER);
				document.add(reportName);
				
				Paragraph typeName=new Paragraph("Category: " + catDesc + ", Type : " + type +",  Is Dev :"+isDevName,f1);
				typeName.setAlignment(Element.ALIGN_CENTER);
				document.add(typeName);
				
				Paragraph headingDate=new Paragraph("From Date: " + fromDate+"  To Date: "+toDate+"",f1);
				headingDate.setAlignment(Element.ALIGN_CENTER);
				document.add(headingDate);
			
			Paragraph ex3=new Paragraph("\n");
			document.add(ex3);
			table.setHeaderRows(1);
			document.add(table);
			
		
			int totalPages = writer.getPageNumber();

			System.out.println("Page no " + totalPages);

			document.close();
			// Atul Sir code to open a Pdf File
			if (file != null) {

				String mimeType = URLConnection.guessContentTypeFromName(file.getName());

				if (mimeType == null) {

					mimeType = "application/pdf";

				}

				response.setContentType(mimeType);

				response.addHeader("content-disposition", String.format("inline; filename=\"%s\"", file.getName()));

				response.setContentLength((int) file.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				try {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				} catch (IOException e) {
					System.out.println("Excep in Opening a Pdf File");
					e.printStackTrace();
				}
			}

		} catch (DocumentException ex) {

			System.out.println("Pdf Generation Error" + ex.getMessage());

			ex.printStackTrace();

		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	List<IssueAndMrnItemWise> itemWiseIssueAndMrnListForPdf = new ArrayList<IssueAndMrnItemWise>();
	
	@RequestMapping(value = "/issueAndMrnReportItemWise/{groupId}/{type}/{isDevName}/{catDesc}/{grpCode}", method = RequestMethod.GET)
	public ModelAndView issueAndMrnReportItemWise(@PathVariable int groupId,@PathVariable String type,
			@PathVariable String isDevName,@PathVariable String catDesc,@PathVariable String grpCode,HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("valuationReport/issueAndMrnReportItemWise");
		List<IssueAndMrnItemWise> itemWiseList = new ArrayList<IssueAndMrnItemWise>();
		
		try {
		   
				MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
				map.add("fromDate",DateConvertor.convertToYMD(fromDate));
	 			map.add("toDate",DateConvertor.convertToYMD(toDate)); 
	 			map.add("groupId", groupId);
	 			map.add("typeId", typeId);
	 			map.add("isDev", isDev);
	 			System.out.println(map);
	 			IssueAndMrnItemWise[] issueAndMrnGroupWise = rest.postForObject(Constants.url + "/issueAndMrnItemWiseReportByGroupId",map,IssueAndMrnItemWise[].class); 
	 			itemWiseList = new ArrayList<IssueAndMrnItemWise>(Arrays.asList(issueAndMrnGroupWise));
	 			
	 			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

				ExportToExcel expoExcel = new ExportToExcel();
				List<String> rowData = new ArrayList<String>();

				rowData.add("SR. No");
				rowData.add("ITEM NAME"); 
				rowData.add("MRN QTY");
				rowData.add("MRN VALUE");
				rowData.add("ISSUE QTY");
				rowData.add("ISSUE VALUE"); 
				

				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);
				int k=0;
				for (int i = 0; i < itemWiseList.size(); i++) {
					if( itemWiseList.get(i).getApproveQty()>0 || itemWiseList.get(i).getApprovedQtyValue()>0 || 
							itemWiseList.get(i).getIssueQty()>0 || itemWiseList.get(i).getApprovedQtyValue()>0 ) {
					expoExcel = new ExportToExcel();
					rowData = new ArrayList<String>();
					k++;
					rowData.add((k)+"");
					rowData.add(itemWiseList.get(i).getItemCode()); 
					rowData.add(""+itemWiseList.get(i).getApproveQty());
					rowData.add(""+itemWiseList.get(i).getApprovedQtyValue());
					rowData.add(""+itemWiseList.get(i).getIssueQty());
					rowData.add(""+itemWiseList.get(i).getIssueQtyValue()); 
					 
					expoExcel.setRowData(rowData);
					exportToExcelList.add(expoExcel);
					}

				}

				HttpSession session = request.getSession();
				session.setAttribute("exportExcelList", exportToExcelList);
				session.setAttribute("excelName", "ItemWiseMrnAndIssue");
			 
			 model.addObject("list",itemWiseList);
			 model.addObject("type",type);
			 model.addObject("isDevName",isDevName);
			 model.addObject("catDesc",catDesc);
			 model.addObject("grpCode",grpCode);
			 itemWiseIssueAndMrnListForPdf=itemWiseList;
			 
		} catch (Exception e) {
			e.printStackTrace();
		}

		 
		return model;
	}
	
	@RequestMapping(value = "/issueAndMrnItemWisePDF/{type}/{isDevName}/{catDesc}/{grpCode}", method = RequestMethod.GET)
	public void issueAndMrnItemWisePDF(@PathVariable String type, @PathVariable String isDevName,
			@PathVariable String catDesc,@PathVariable String grpCode,HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		BufferedOutputStream outStream = null;
		try {
		Document document = new Document(PageSize.A4);
		DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
		String reportDate = DF.format(new Date());
        document.addHeader("Date: ", reportDate);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("time in Gen Bill PDF ==" + dateFormat.format(cal.getTime()));
		String timeStamp = dateFormat.format(cal.getTime());
		String FILE_PATH = Constants.REPORT_SAVE;
		File file = new File(FILE_PATH);

		PdfWriter writer = null;

		FileOutputStream out = new FileOutputStream(FILE_PATH);
		try {
			writer = PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
		
		float totalMrnQty=0;
		float totalMrnValue=0;
		float totalIssueQty=0;
		float totalIssueValue=0;
	
		PdfPTable table = new PdfPTable(6);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] {0.4f, 3.0f, 1.0f, 1.0f, 1.0f, 1.0f});
			Font headFont = new Font(FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 11.0f, Font.UNDERLINE, BaseColor.BLUE);
			Font f1 = new Font(FontFamily.TIMES_ROMAN, 9.0f, Font.BOLD, BaseColor.DARK_GRAY);

			PdfPCell hcell = new PdfPCell();
			
			hcell.setPadding(4);
			hcell = new PdfPCell(new Phrase("SR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("ITEM", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			  
			hcell = new PdfPCell(new Phrase("MRN QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("MRN VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("ISSUE QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("ISSUE VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			  
			int index = 0;
			if(!itemWiseIssueAndMrnListForPdf.isEmpty()) {
					for (int k = 0; k < itemWiseIssueAndMrnListForPdf.size(); k++) {
                            
						if( itemWiseIssueAndMrnListForPdf.get(k).getApproveQty()>0 || itemWiseIssueAndMrnListForPdf.get(k).getApprovedQtyValue()>0
								|| itemWiseIssueAndMrnListForPdf.get(k).getIssueQty()>0 || itemWiseIssueAndMrnListForPdf.get(k).getIssueQtyValue()>0
								 ) {
							
						
							index++;
						
							PdfPCell cell;
							
							cell = new PdfPCell(new Phrase(""+index, headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPadding(3);
							table.addCell(cell);

						
							cell = new PdfPCell(new Phrase(itemWiseIssueAndMrnListForPdf.get(k).getItemCode(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
						 
							cell = new PdfPCell(new Phrase(""+df.format(itemWiseIssueAndMrnListForPdf.get(k).getApproveQty()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalMrnQty=totalMrnQty+itemWiseIssueAndMrnListForPdf.get(k).getApproveQty();
							
							cell = new PdfPCell(new Phrase(""+df.format(itemWiseIssueAndMrnListForPdf.get(k).getApprovedQtyValue()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalMrnValue=totalMrnValue+itemWiseIssueAndMrnListForPdf.get(k).getApprovedQtyValue();
							
							cell = new PdfPCell(new Phrase(""+df.format(itemWiseIssueAndMrnListForPdf.get(k).getIssueQty()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalIssueQty=totalIssueQty+itemWiseIssueAndMrnListForPdf.get(k).getIssueQty();
							
							cell = new PdfPCell(new Phrase(""+df.format(itemWiseIssueAndMrnListForPdf.get(k).getIssueQtyValue()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							totalIssueValue=totalIssueValue+itemWiseIssueAndMrnListForPdf.get(k).getIssueQtyValue();
							 
						}
					}
			}
			
			PdfPCell cell;
			
			cell = new PdfPCell(new Phrase("Total", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setPadding(3);
			cell.setColspan(2);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(totalMrnQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(totalMrnValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(totalIssueQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(totalIssueValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			
			document.open();
			Paragraph company = new Paragraph(companyInfo.getCompanyName()+"\n", f);
			company.setAlignment(Element.ALIGN_CENTER);
			document.add(company);
			
				Paragraph heading1 = new Paragraph(
						companyInfo.getFactoryAdd(),f1);
				heading1.setAlignment(Element.ALIGN_CENTER);
				document.add(heading1);
				Paragraph ex2=new Paragraph("\n");
				document.add(ex2);
				
				Paragraph reportName=new Paragraph("RECEIPT/ISSUE VALUEATION REPORT ITEM WISE",f1);
				reportName.setAlignment(Element.ALIGN_CENTER);
				document.add(reportName);
				
				Paragraph typeName=new Paragraph("Category : " + catDesc +", Group : " + grpCode + ", Type : " + type +", Is Dev :"+isDevName,f1);
				typeName.setAlignment(Element.ALIGN_CENTER);
				document.add(typeName);
				
				Paragraph headingDate=new Paragraph("From Date: " + fromDate+"  To Date: "+toDate+"",f1);
				headingDate.setAlignment(Element.ALIGN_CENTER);
				document.add(headingDate);
			
			Paragraph ex3=new Paragraph("\n");
			document.add(ex3);
			table.setHeaderRows(1);
			document.add(table);
			
		
			int totalPages = writer.getPageNumber();

			System.out.println("Page no " + totalPages);

			document.close();
			// Atul Sir code to open a Pdf File
			if (file != null) {

				String mimeType = URLConnection.guessContentTypeFromName(file.getName());

				if (mimeType == null) {

					mimeType = "application/pdf";

				}

				response.setContentType(mimeType);

				response.addHeader("content-disposition", String.format("inline; filename=\"%s\"", file.getName()));

				response.setContentLength((int) file.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				try {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				} catch (IOException e) {
					System.out.println("Excep in Opening a Pdf File");
					e.printStackTrace();
				}
			}

		} catch (DocumentException ex) {

			System.out.println("Pdf Generation Error" + ex.getMessage());

			ex.printStackTrace();

		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	List<IssueDeptWise> deptWiselistGlobal=null; 
	String catIds = new String();
	@RequestMapping(value = "/issueReportDeptWise", method = RequestMethod.GET)
	public ModelAndView issueReportDeptWise(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("valuationReport/issueReportDeptWise");
		try {
			List<IssueDeptWise> deptWiselist = new ArrayList<IssueDeptWise>();
			Type[] type = rest.getForObject(Constants.url + "/getAlltype", Type[].class);
			List<Type> typeList = new ArrayList<Type>(Arrays.asList(type));
			
			Dept[] Dept = rest.getForObject(Constants.url + "/getAllDeptByIsUsed", Dept[].class);
			List<Dept> deparmentList = new ArrayList<Dept>(Arrays.asList(Dept));

			model.addObject("deparmentList", deparmentList); 
			model.addObject("typeList", typeList);
			
			Category[] category = rest.getForObject(Constants.url + "/getAllCategoryByIsUsed", Category[].class);
			List<Category> categoryList = new ArrayList<Category>(Arrays.asList(category));
			catIds = new String();
			model.addObject("categoryList", categoryList);
			
			if(request.getParameter("fromDate")==null || request.getParameter("toDate")==null || request.getParameter("typeId")==null || 
					request.getParameter("isDev")==null || request.getParameter("deptId")==null) {
				
				
				SimpleDateFormat yy = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy");
				Date date = new Date();
				  Calendar calendar = Calendar.getInstance();
				  calendar.setTime(date);
				   
				 fromDate = "01"+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR);
				 toDate = dd.format(date);
				 typeId=0;
				 isDev=-1;
				 deptId=0;
				 for(int i=0;i<categoryList.size();i++) {
						catIds=catIds+categoryList.get(i).getCatId()+",";
					}
				 MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
				 map.add("fromDate",DateConvertor.convertToYMD(fromDate));
		 			map.add("toDate",yy.format(date)); 
		 			map.add("typeId", typeId);
		 			map.add("isDev", isDev);
		 			map.add("deptId", deptId);
		 			map.add("catIds", catIds.substring(0, catIds.length()-1));
		 			System.out.println(map);
		 			IssueDeptWise[] IssueDeptWise = rest.postForObject(Constants.url + "/issueDepartmentWiseReport",map, IssueDeptWise[].class);
					 deptWiselist = new ArrayList<IssueDeptWise>(Arrays.asList(IssueDeptWise));
			
					 model.addObject("deptWiselist", deptWiselist);
						model.addObject("fromDate", fromDate);
						model.addObject("toDate", toDate);
						deptWiselistGlobal=deptWiselist;
			}
			else {
				fromDate = request.getParameter("fromDate");
				toDate = request.getParameter("toDate");
				typeId = Integer.parseInt(request.getParameter("typeId"));
				isDev =Integer.parseInt(request.getParameter("isDev"));
				deptId = Integer.parseInt(request.getParameter("deptId"));
				catId = Integer.parseInt(request.getParameter("catId"));
				
				 
					MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
					map.add("fromDate",DateConvertor.convertToYMD(fromDate));
		 			map.add("toDate",DateConvertor.convertToYMD(toDate)); 
		 			map.add("typeId", typeId);
		 			map.add("isDev", isDev);
		 			map.add("deptId", deptId);
		 			if(catId==0) {
						for(int i=0;i<categoryList.size();i++) {
							catIds=catIds+categoryList.get(i).getCatId()+",";
						}
						map.add("catIds", catIds.substring(0, catIds.length()-1));
					}
					else{
						map.add("catIds", catId);
					}
		 			
		 			System.out.println(map);
		 			IssueDeptWise[] IssueDeptWise = rest.postForObject(Constants.url + "/issueDepartmentWiseReport",map, IssueDeptWise[].class);
					 deptWiselist = new ArrayList<IssueDeptWise>(Arrays.asList(IssueDeptWise));
			
			    deptWiselistGlobal=deptWiselist;
				model.addObject("deptWiselist", deptWiselist);
				model.addObject("fromDate", fromDate);
				model.addObject("toDate", toDate);
				model.addObject("typeId", typeId);
				model.addObject("isDevelompent", isDev);
				model.addObject("deptId", deptId);
				model.addObject("isDevelompent", isDev);
				model.addObject("catId", catId);
				
			}
			//------------------------ Export To Excel--------------------------------------
			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

			ExportToExcel expoExcel = new ExportToExcel();
			List<String> rowData = new ArrayList<String>();

			rowData.add("SR. No");
			rowData.add("DEPARMENT NAME");
			rowData.add("ISSUE QTY");
			rowData.add("ISSUE VALUE");
			

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);
			for (int i = 0; i < deptWiselist.size(); i++) {
				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();

				rowData.add((i+1)+"");
				rowData.add(deptWiselist.get(i).getDeptCode());
				rowData.add(""+deptWiselist.get(i).getIssueQty());
				rowData.add(""+deptWiselist.get(i).getIssueQtyValue());


				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);

			}

			HttpSession session = request.getSession();
			session.setAttribute("exportExcelList", exportToExcelList);
			session.setAttribute("excelName", "DeptWiseConsumption(Issues)");
			//------------------------------------END------------------------------------------
			companyInfo = rest.getForObject(Constants.url + "getCompanyDetails",
					Company.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/getIssueReportDeptWise", method = RequestMethod.GET)
	public @ResponseBody List<IssueDeptWise> getIssueReportDeptWise(HttpServletRequest request, HttpServletResponse response) {

		List<IssueDeptWise> deptWiselist = new ArrayList<IssueDeptWise>();

		try {
			
			if(request.getParameter("fromDate")==null || request.getParameter("toDate")==null || request.getParameter("typeId")==null || 
					request.getParameter("isDev")==null || request.getParameter("deptId")==null) {
				
			}
			else {
				fromDate = request.getParameter("fromDate");
				toDate = request.getParameter("toDate");
				typeId = Integer.parseInt(request.getParameter("typeId"));
				isDev =Integer.parseInt(request.getParameter("isDev"));
				deptId = Integer.parseInt(request.getParameter("deptId"));
					MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
					map.add("fromDate",DateConvertor.convertToYMD(fromDate));
		 			map.add("toDate",DateConvertor.convertToYMD(toDate)); 
		 			map.add("typeId", typeId);
		 			map.add("isDev", isDev);
		 			map.add("deptId", deptId);
		 			if(catId==0) {
						for(int i=0;i<categoryList.size();i++) {
							catIds=catIds+categoryList.get(i).getCatId()+",";
						}
						map.add("catIds", catIds.substring(0, catIds.length()-1));
					}
					else{
						map.add("catIds", catId);
					}
		 			System.out.println(map);
		 			IssueDeptWise[] IssueDeptWise = rest.postForObject(Constants.url + "/issueDepartmentWiseReport",map, IssueDeptWise[].class);
					 deptWiselist = new ArrayList<IssueDeptWise>(Arrays.asList(IssueDeptWise));
			
			    deptWiselistGlobal=deptWiselist;
				
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return deptWiselist;
	}
	@RequestMapping(value = "/issueReportSubDeptWise/{deptId}/{typeName}/{isDevName}/{deptName}", method = RequestMethod.GET)
	public ModelAndView issueReportSubDeptWise(@PathVariable int deptId,@PathVariable String typeName,@PathVariable String isDevName,@PathVariable String deptName,HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("valuationReport/issueReportSubDeptWise");
		try {
				List<IssueDeptWise> deptWiselist = new ArrayList<IssueDeptWise>();
			  
					MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
					map.add("fromDate",DateConvertor.convertToYMD(fromDate));
		 			map.add("toDate",DateConvertor.convertToYMD(toDate)); 
		 			map.add("typeId", typeId);
		 			map.add("isDev", isDev);
		 			map.add("deptId", deptId);
		 			if(catId==0) { 
		 				
						map.add("catIds", catIds.substring(0, catIds.length()-1));
					}
					else{
						map.add("catIds", catId);
					}
		 			System.out.println(map);
		 			IssueDeptWise[] IssueDeptWise = rest.postForObject(Constants.url + "/issueSubDepartmentWiseReport",map, IssueDeptWise[].class);
					 deptWiselist = new ArrayList<IssueDeptWise>(Arrays.asList(IssueDeptWise));
				 
				model.addObject("deptWiselist", deptWiselist);
				model.addObject("deptId", deptId);
				model.addObject("typeName", typeName);
				model.addObject("isDevName", isDevName);
				model.addObject("deptName", deptName);
				   deptWiselistGlobal=deptWiselist;
				//------------------------ Export To Excel--------------------------------------
				List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

				ExportToExcel expoExcel = new ExportToExcel();
				List<String> rowData = new ArrayList<String>();

				rowData.add("SR. No");
				rowData.add("SUB-DEPARMENT NAME");
				rowData.add("ISSUE QTY");
				rowData.add("ISSUE VALUE");
				

				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);
				for (int i = 0; i < deptWiselist.size(); i++) {
					expoExcel = new ExportToExcel();
					rowData = new ArrayList<String>();

					rowData.add((i+1)+"");
					rowData.add(deptWiselist.get(i).getDeptCode());
					rowData.add(""+deptWiselist.get(i).getIssueQty());
					rowData.add(""+deptWiselist.get(i).getIssueQtyValue());


					expoExcel.setRowData(rowData);
					exportToExcelList.add(expoExcel);

				}

				HttpSession session = request.getSession();
				session.setAttribute("exportExcelList", exportToExcelList);
				session.setAttribute("excelName", "SubDeptWiseConsumption(Issues)");
				//------------------------------------END------------------------------------------
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	@RequestMapping(value = "/issueReportSubDeptWiseReport", method = RequestMethod.GET)
	public @ResponseBody List<IssueDeptWise> issueReportSubDeptWiseReport(HttpServletRequest request, HttpServletResponse response) {
		List<IssueDeptWise> deptWiselist = new ArrayList<IssueDeptWise>();

		try {
			deptId = Integer.parseInt(request.getParameter("deptId"));
					MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
					map.add("fromDate",DateConvertor.convertToYMD(fromDate));
		 			map.add("toDate",DateConvertor.convertToYMD(toDate)); 
		 			map.add("typeId", typeId);
		 			map.add("isDev", isDev);
		 			map.add("deptId", deptId);
		 			if(catId==0) { 
		 				
						map.add("catIds", catIds.substring(0, catIds.length()-1));
					}
					else{
						map.add("catIds", catId);
					}
		 			System.out.println(map);
		 			IssueDeptWise[] IssueDeptWise = rest.postForObject(Constants.url + "/issueSubDepartmentWiseReport",map, IssueDeptWise[].class);
					 deptWiselist = new ArrayList<IssueDeptWise>(Arrays.asList(IssueDeptWise));
					   deptWiselistGlobal=deptWiselist;
					   System.err.println(deptWiselistGlobal.toString());
				
		}catch (Exception e) {
			// TODO: handle exception
		    e.printStackTrace();
		}
		return deptWiselist;
	}
	@RequestMapping(value = "/issueReportItemWise/{subDeptId}/{typeName}/{isDevName}/{deptName}/{subDeptCode}", method = RequestMethod.GET)
	public ModelAndView issueReportItemWise(@PathVariable int subDeptId,@PathVariable String typeName
			,@PathVariable String isDevName,@PathVariable String deptName,@PathVariable String subDeptCode,HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("valuationReport/issueReportItemWise");
		try {
				List<IssueDeptWise> itemWiselist = new ArrayList<IssueDeptWise>();
			  
					MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
					map.add("fromDate",DateConvertor.convertToYMD(fromDate));
		 			map.add("toDate",DateConvertor.convertToYMD(toDate)); 
		 			map.add("typeId", typeId);
		 			map.add("isDev", isDev);
		 			map.add("subDept", subDeptId);
		 			if(catId==0) { 
		 				
						map.add("catIds", catIds.substring(0, catIds.length()-1));
					}
					else{
						map.add("catIds", catId);
					}
		 			System.out.println(map);
		 			IssueDeptWise[] IssueDeptWise = rest.postForObject(Constants.url + "/issueItemWiseReportBySubDept",map, IssueDeptWise[].class);
		 			itemWiselist = new ArrayList<IssueDeptWise>(Arrays.asList(IssueDeptWise));
		 			deptWiselistGlobal=itemWiselist;
				model.addObject("itemWiselist", itemWiselist);
				model.addObject("typeName", typeName);
				model.addObject("isDevName", isDevName);
				model.addObject("deptName", deptName);
				model.addObject("subDeptCode", subDeptCode);
				//------------------------ Export To Excel--------------------------------------
				List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

				ExportToExcel expoExcel = new ExportToExcel();
				List<String> rowData = new ArrayList<String>();

				rowData.add("SR. No");
				rowData.add("ITEM NAME");
				rowData.add("ISSUE QTY");
				rowData.add("ISSUE VALUE");
				

				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);
				for (int i = 0; i < itemWiselist.size(); i++) {
					expoExcel = new ExportToExcel();
					rowData = new ArrayList<String>();

					rowData.add((i+1)+"");
					rowData.add(itemWiselist.get(i).getDeptCode());
					rowData.add(""+itemWiselist.get(i).getIssueQty());
					rowData.add(""+itemWiselist.get(i).getIssueQtyValue());


					expoExcel.setRowData(rowData);
					exportToExcelList.add(expoExcel);

				}

				HttpSession session = request.getSession();
				session.setAttribute("exportExcelList", exportToExcelList);
				session.setAttribute("excelName", "ItemWiseConsumption(Issues)");
				//------------------------------------END------------------------------------------
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	@RequestMapping(value = "/issueReportDeptWisePDF/{typeName}/{isDev}", method = RequestMethod.GET)
	public void showProdByOrderPdf(@PathVariable String typeName,@PathVariable String isDev, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		BufferedOutputStream outStream = null;
		try {
		Document document = new Document(PageSize.A4);
		DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
		String reportDate = DF.format(new Date());
        document.addHeader("Date: ", reportDate);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("time in Gen Bill PDF ==" + dateFormat.format(cal.getTime()));
		String timeStamp = dateFormat.format(cal.getTime());
		String FILE_PATH = Constants.REPORT_SAVE;
		File file = new File(FILE_PATH);

		PdfWriter writer = null;

		FileOutputStream out = new FileOutputStream(FILE_PATH);
		try {
			writer = PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
		
		float issueQty=0;
		float issueValue=0;
	
		PdfPTable table = new PdfPTable(4);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] {0.4f, 1.7f, 1.0f, 0.9f});
			Font headFont = new Font(FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 11.0f, Font.UNDERLINE, BaseColor.BLUE);
			Font f1 = new Font(FontFamily.TIMES_ROMAN, 9.0f, Font.BOLD, BaseColor.DARK_GRAY);

			PdfPCell hcell = new PdfPCell();
			
			hcell.setPadding(4);
			hcell = new PdfPCell(new Phrase("SR.NO.", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("DEPARTMENT NAME", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("ISSUE QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("ISSUE VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			
			int index = 0;
			if(!deptWiselistGlobal.isEmpty()) {
					for (int k = 0; k < deptWiselistGlobal.size(); k++) {
                            if(deptWiselistGlobal.get(k).getIssueQty()>0 || deptWiselistGlobal.get(k).getIssueQtyValue()>0) {
                            	
                           
							index++;
						
							PdfPCell cell;
							
							cell = new PdfPCell(new Phrase(""+index, headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPadding(3);
							table.addCell(cell);

						
							cell = new PdfPCell(new Phrase(deptWiselistGlobal.get(k).getDeptCode(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
						
							cell = new PdfPCell(new Phrase(""+df.format(deptWiselistGlobal.get(k).getIssueQty()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							issueQty=issueQty+deptWiselistGlobal.get(k).getIssueQty();
							
							cell = new PdfPCell(new Phrase(""+df.format(deptWiselistGlobal.get(k).getIssueQtyValue()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							issueValue=issueValue+deptWiselistGlobal.get(k).getIssueQtyValue();
                            
                            } 
					}
			}
			
			PdfPCell cell;
			
			cell = new PdfPCell(new Phrase("Total", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setPadding(3);
			cell.setColspan(2);
			table.addCell(cell);
 
		
			cell = new PdfPCell(new Phrase(""+df.format(issueQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format(issueValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			 
			
			document.open();
			Paragraph company = new Paragraph(companyInfo.getCompanyName()+"\n", f);
			company.setAlignment(Element.ALIGN_CENTER);
			document.add(company);
			
				Paragraph heading1 = new Paragraph(
						companyInfo.getFactoryAdd(),f1);
				heading1.setAlignment(Element.ALIGN_CENTER);
				document.add(heading1);
				Paragraph ex2=new Paragraph("\n");
				document.add(ex2);
				
				Paragraph report=new Paragraph("Department Wise Consumption(Issues) Report ",f1);
				report.setAlignment(Element.ALIGN_CENTER);
				document.add(report);
			
				Paragraph type=new Paragraph("Type : " + typeName +", Is Dev : " + isDev,f1);
				type.setAlignment(Element.ALIGN_CENTER);
				document.add(type);

				Paragraph headingDate=new Paragraph(" From Date: " + fromDate+"  To Date: "+toDate+"",f1);
				headingDate.setAlignment(Element.ALIGN_CENTER);
			document.add(headingDate);
			
			Paragraph ex3=new Paragraph("\n");
			document.add(ex3);
			table.setHeaderRows(1);
			document.add(table);
			
		
			int totalPages = writer.getPageNumber();

			System.out.println("Page no " + totalPages);

			document.close();
			// Atul Sir code to open a Pdf File
			if (file != null) {

				String mimeType = URLConnection.guessContentTypeFromName(file.getName());

				if (mimeType == null) {

					mimeType = "application/pdf";

				}

				response.setContentType(mimeType);

				response.addHeader("content-disposition", String.format("inline; filename=\"%s\"", file.getName()));

				response.setContentLength((int) file.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				try {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				} catch (IOException e) {
					System.out.println("Excep in Opening a Pdf File");
					e.printStackTrace();
				}
			}

		} catch (DocumentException ex) {

			System.out.println("Pdf Generation Error" + ex.getMessage());

			ex.printStackTrace();

		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/issueReportSubDeptWisePDF/{typeName}/{isDevName}/{deptName}", method = RequestMethod.GET)
	public void issueReportSubDeptWisePDF(@PathVariable String typeName,@PathVariable String isDevName,@PathVariable String deptName, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		BufferedOutputStream outStream = null;
		try {
		Document document = new Document(PageSize.A4);
		DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
		String reportDate = DF.format(new Date());
        document.addHeader("Date: ", reportDate);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("time in Gen Bill PDF ==" + dateFormat.format(cal.getTime()));
		String timeStamp = dateFormat.format(cal.getTime());
		String FILE_PATH = Constants.REPORT_SAVE;
		File file = new File(FILE_PATH);

		PdfWriter writer = null;

		FileOutputStream out = new FileOutputStream(FILE_PATH);
		try {
			writer = PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
		
		float issueQty=0;
		float issueValue=0;
	
		PdfPTable table = new PdfPTable(4);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] {0.4f, 1.7f, 1.0f, 0.9f});
			Font headFont = new Font(FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 11.0f, Font.UNDERLINE, BaseColor.BLUE);
			Font f1 = new Font(FontFamily.TIMES_ROMAN, 9.0f, Font.BOLD, BaseColor.GRAY);

			PdfPCell hcell = new PdfPCell();
			
			hcell.setPadding(4);
			hcell = new PdfPCell(new Phrase("SR.NO.", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("DEPARTMENT NAME", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("ISSUE QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("ISSUE VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			
			int index = 0;
			if(!deptWiselistGlobal.isEmpty()) {
					for (int k = 0; k < deptWiselistGlobal.size(); k++) {
						
						if(deptWiselistGlobal.get(k).getIssueQty()>0 || deptWiselistGlobal.get(k).getIssueQtyValue()>0) {
                            
							index++;
						
							PdfPCell cell;
							
							cell = new PdfPCell(new Phrase(""+index, headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPadding(3);
							table.addCell(cell);

						
							cell = new PdfPCell(new Phrase(deptWiselistGlobal.get(k).getDeptCode(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
						
							cell = new PdfPCell(new Phrase(""+df.format(deptWiselistGlobal.get(k).getIssueQty()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							issueQty=issueQty+deptWiselistGlobal.get(k).getIssueQty();
							
							cell = new PdfPCell(new Phrase(""+df.format(deptWiselistGlobal.get(k).getIssueQtyValue()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							issueValue=issueValue+deptWiselistGlobal.get(k).getIssueQtyValue();
						}
					
					}
			}
			
			PdfPCell cell;
			
			cell = new PdfPCell(new Phrase("Total", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setPadding(3);
			cell.setColspan(2);
			table.addCell(cell);
 
			cell = new PdfPCell(new Phrase(""+df.format(issueQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			  
			cell = new PdfPCell(new Phrase(""+df.format(issueValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell);
			  
			document.open();
			Paragraph company = new Paragraph(companyInfo.getCompanyName()+"\n", f);
			company.setAlignment(Element.ALIGN_CENTER);
			document.add(company);
			
				Paragraph heading1 = new Paragraph(
						companyInfo.getFactoryAdd(),f1);
				heading1.setAlignment(Element.ALIGN_CENTER);
				document.add(heading1);
				Paragraph ex2=new Paragraph("\n");
				document.add(ex2);
				
				Paragraph report=new Paragraph("Sub-Department Wise Consumption(Issues) Report",f1);
				report.setAlignment(Element.ALIGN_CENTER);
				document.add(report);
				
				Paragraph type=new Paragraph("Department : "+deptName+", Type : " + typeName + ", Is Dev : " + isDevName,f1);
				type.setAlignment(Element.ALIGN_CENTER);
				document.add(type);
				
				Paragraph headingDate=new Paragraph("From Date: " + fromDate+"  To Date: "+toDate+"",f1);
				headingDate.setAlignment(Element.ALIGN_CENTER); 
				document.add(headingDate);
			
			Paragraph ex3=new Paragraph("\n");
			document.add(ex3);
			table.setHeaderRows(1);
			document.add(table);
			
		
			int totalPages = writer.getPageNumber();

			System.out.println("Page no " + totalPages);

			document.close();
			// Atul Sir code to open a Pdf File
			if (file != null) {

				String mimeType = URLConnection.guessContentTypeFromName(file.getName());

				if (mimeType == null) {

					mimeType = "application/pdf";

				}

				response.setContentType(mimeType);

				response.addHeader("content-disposition", String.format("inline; filename=\"%s\"", file.getName()));

				response.setContentLength((int) file.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				try {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				} catch (IOException e) {
					System.out.println("Excep in Opening a Pdf File");
					e.printStackTrace();
				}
			}

		} catch (DocumentException ex) {

			System.out.println("Pdf Generation Error" + ex.getMessage());

			ex.printStackTrace();

		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/issueReportItemWisePDF/{typeName}/{isDevName}/{deptName}/{subDeptName}", method = RequestMethod.GET)
	public void issueReportItemWisePDF(@PathVariable String typeName,@PathVariable String isDevName,@PathVariable String deptName,
			@PathVariable String subDeptName,HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		BufferedOutputStream outStream = null;
		try {
		Document document = new Document(PageSize.A4);
		DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
		String reportDate = DF.format(new Date());
        document.addHeader("Date: ", reportDate);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("time in Gen Bill PDF ==" + dateFormat.format(cal.getTime()));
		String timeStamp = dateFormat.format(cal.getTime());
		String FILE_PATH = Constants.REPORT_SAVE;
		File file = new File(FILE_PATH);

		PdfWriter writer = null;

		FileOutputStream out = new FileOutputStream(FILE_PATH);
		try {
			writer = PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
		
		float issueQty=0;
		float issueValue=0;
	
		PdfPTable table = new PdfPTable(4);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] {0.4f, 4.9f, 1.3f, 1.3f});
			Font headFont = new Font(FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 11.0f, Font.UNDERLINE, BaseColor.BLUE);
			Font f1 = new Font(FontFamily.TIMES_ROMAN, 9.0f, Font.BOLD, BaseColor.GRAY);

			PdfPCell hcell = new PdfPCell();
			
			hcell.setPadding(4);
			hcell = new PdfPCell(new Phrase("SR.", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("ITEM NAME", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("ISSUE QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("ISSUE VALUE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			
			int index = 0;
			if(!deptWiselistGlobal.isEmpty()) {
					for (int k = 0; k < deptWiselistGlobal.size(); k++) {
						
						if(deptWiselistGlobal.get(k).getIssueQty()>0 || deptWiselistGlobal.get(k).getIssueQtyValue()>0) {
                            
							index++;
						
							PdfPCell cell;
							
							cell = new PdfPCell(new Phrase(""+index, headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPadding(3);
							table.addCell(cell);

						
							cell = new PdfPCell(new Phrase(deptWiselistGlobal.get(k).getDeptCode(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
						
							cell = new PdfPCell(new Phrase(""+df.format(deptWiselistGlobal.get(k).getIssueQty()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							issueQty=issueQty+deptWiselistGlobal.get(k).getIssueQty();
							
							cell = new PdfPCell(new Phrase(""+df.format(deptWiselistGlobal.get(k).getIssueQtyValue()), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							issueValue=issueValue+deptWiselistGlobal.get(k).getIssueQtyValue();
						}
					
					}
			}
			
			PdfPCell cell;
			
			cell = new PdfPCell(new Phrase("Total", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setPadding(3);
			cell.setColspan(2);
			table.addCell(cell);
  
			cell = new PdfPCell(new Phrase(""+df.format(issueQty), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell); 
			
			cell = new PdfPCell(new Phrase(""+df.format(issueValue), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(2);
			cell.setPadding(3);
			table.addCell(cell); 
			
			document.open();
			Paragraph company = new Paragraph(companyInfo.getCompanyName()+"\n", f);
			company.setAlignment(Element.ALIGN_CENTER);
			document.add(company);
			
				Paragraph heading1 = new Paragraph(
						companyInfo.getFactoryAdd(),f1);
				heading1.setAlignment(Element.ALIGN_CENTER);
				document.add(heading1);
				Paragraph ex2=new Paragraph("\n");
				document.add(ex2);
				
				Paragraph report=new Paragraph("Item Wise Consumption(Issues) Report",f1);
				report.setAlignment(Element.ALIGN_CENTER);
				document.add(report);
				
				Paragraph type=new Paragraph(" Department : " + deptName + ", Sub-Dept: " + subDeptName + ", Type : " + typeName + ", Is Dev : " + isDevName,f1);
				type.setAlignment(Element.ALIGN_CENTER);
				document.add(type);
				
				Paragraph headingDate=new Paragraph("From Date: " + fromDate+"  To Date: "+toDate+"",f1);
				headingDate.setAlignment(Element.ALIGN_CENTER); 
				document.add(headingDate);
			
			Paragraph ex3=new Paragraph("\n");
			document.add(ex3);
			table.setHeaderRows(1);

			document.add(table);
			
		
			int totalPages = writer.getPageNumber();

			System.out.println("Page no " + totalPages);

			document.close();
			// Atul Sir code to open a Pdf File
			if (file != null) {

				String mimeType = URLConnection.guessContentTypeFromName(file.getName());

				if (mimeType == null) {

					mimeType = "application/pdf";

				}

				response.setContentType(mimeType);

				response.addHeader("content-disposition", String.format("inline; filename=\"%s\"", file.getName()));

				response.setContentLength((int) file.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				try {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				} catch (IOException e) {
					System.out.println("Excep in Opening a Pdf File");
					e.printStackTrace();
				}
			}

		} catch (DocumentException ex) {

			System.out.println("Pdf Generation Error" + ex.getMessage());

			ex.printStackTrace();

		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/issueMonthWieReport", method = RequestMethod.GET)
	public ModelAndView issueMonthWieReport(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("valuationReport/issueMonthWieReport");
		try {
			List<IssueMonthWiseList> list = new ArrayList<IssueMonthWiseList>();
			Type[] type = rest.getForObject(Constants.url + "/getAlltype", Type[].class);
			List<Type> typeList = new ArrayList<Type>(Arrays.asList(type));
			model.addObject("typeList", typeList);
			 
			if(request.getParameter("typeId")==null || request.getParameter("isDev")==null) {
				
				typeId = 0;
				isDev = -1;
				 
					MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
					/*map.add("fromDate",DateConvertor.convertToYMD(fromDate));
		 			map.add("toDate",DateConvertor.convertToYMD(toDate)); */
		 			map.add("typeId", typeId);
		 			map.add("isDev", isDev);
		 			System.out.println(map);
		 			IssueMonthWiseList[] issueMonthWiseList = rest.postForObject(Constants.url + "/issueMonthWiseReportByDept",map, IssueMonthWiseList[].class);
		 			list = new ArrayList<IssueMonthWiseList>(Arrays.asList(issueMonthWiseList));
		 			
		 			System.out.println("list " + list);
		 			
		 			for(int i=0 ; i<list.size() ; i++) {
		 				
		 				model.addObject("month"+i,list.get(i));
		 			}
				 listGlobal=list;
				model.addObject("list", list);
				model.addObject("fromDate", fromDate);
				model.addObject("toDate", toDate);
				model.addObject("typeId", typeId);
				model.addObject("isDevelompent", isDev);
				
				Dept[] Dept = rest.getForObject(Constants.url + "/getAllDeptByIsUsed", Dept[].class);
				 deparmentList = new ArrayList<Dept>(Arrays.asList(Dept));
				model.addObject("deparmentList", deparmentList);
			}
			else {
				/*fromDate = request.getParameter("fromDate");
				toDate = request.getParameter("toDate");*/
				typeId = Integer.parseInt(request.getParameter("typeId"));
				isDev =Integer.parseInt(request.getParameter("isDev"));
				 
					MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
					/*map.add("fromDate",DateConvertor.convertToYMD(fromDate));
		 			map.add("toDate",DateConvertor.convertToYMD(toDate)); */
		 			map.add("typeId", typeId);
		 			map.add("isDev", isDev);
		 			System.out.println(map);
		 			IssueMonthWiseList[] issueMonthWiseList = rest.postForObject(Constants.url + "/issueMonthWiseReportByDept",map, IssueMonthWiseList[].class);
		 			list = new ArrayList<IssueMonthWiseList>(Arrays.asList(issueMonthWiseList));
		 			
		 			System.out.println("list " + list);
		 			
		 			for(int i=0 ; i<list.size() ; i++) {
		 				
		 				model.addObject("month"+i,list.get(i));
		 			}
				 listGlobal=list;
				model.addObject("list", list);
				model.addObject("fromDate", fromDate);
				model.addObject("toDate", toDate);
				model.addObject("typeId", typeId);
				model.addObject("isDevelompent", isDev);
				
				Dept[] Dept = rest.getForObject(Constants.url + "/getAllDeptByIsUsed", Dept[].class);
				 deparmentList = new ArrayList<Dept>(Arrays.asList(Dept));
				model.addObject("deparmentList", deparmentList);
				
			}
			
			companyInfo = rest.getForObject(Constants.url + "getCompanyDetails",
					Company.class);

			//------------------------ Export To Excel--------------------------------------
			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

			ExportToExcel expoExcel = new ExportToExcel();
			List<String> rowData = new ArrayList<String>(); 

				/*rowData.add("SR. No");
				rowData.add("DEPARMENT NAME");
				rowData.add("APR ISSUE QTY");
				rowData.add("APR ISSUE VALUE");
				rowData.add("MAY ISSUE QTY");
				rowData.add("MAY ISSUE VALUE");
				rowData.add("JUNE ISSUE QTY");
				rowData.add("JUNE ISSUE VALUE");
				rowData.add("JULY ISSUE QTY");
				rowData.add("JULY ISSUE VALUE");
				rowData.add("AUGUST ISSUE QTY");
				rowData.add("AUGUST ISSUE VALUE");
				rowData.add("SEPTEMBR ISSUE QTY");
				rowData.add("SEPTEMBR ISSUE VALUE");
				rowData.add("OCTOMBER ISSUE QTY");
				rowData.add("OCTOMBER ISSUE VALUE");
				rowData.add("NOVEMBER ISSUE QTY");
				rowData.add("NOVEMBER ISSUE VALUE");
				rowData.add("DECEMBER ISSUE QTY");
				rowData.add("DECEMBER ISSUE VALUE");
				rowData.add("JANUARY ISSUE QTY");
				rowData.add("JANUARY ISSUE VALUE");
				rowData.add("FEBRUARY ISSUE QTY");
				rowData.add("FEBRUARY ISSUE VALUE");
				rowData.add("MARCH ISSUE QTY");
				rowData.add("MARCH ISSUE VALUE");*/

				rowData.add("SR. No");
				rowData.add("DEPARMENT NAME"); 
				rowData.add("APR ISSUE VALUE"); 
				rowData.add("MAY ISSUE VALUE"); 
				rowData.add("JUNE ISSUE VALUE"); 
				rowData.add("JULY ISSUE VALUE"); 
				rowData.add("AUGUST ISSUE VALUE"); 
				rowData.add("SEPTEMBR ISSUE VALUE"); 
				rowData.add("OCTOMBER ISSUE VALUE"); 
				rowData.add("NOVEMBER ISSUE VALUE"); 
				rowData.add("DECEMBER ISSUE VALUE"); 
				rowData.add("JANUARY ISSUE VALUE"); 
				rowData.add("FEBRUARY ISSUE VALUE"); 
				rowData.add("MARCH ISSUE VALUE");

				expoExcel.setRowData(rowData);
			
			exportToExcelList.add(expoExcel);
			for (int i = 0; i < deparmentList.size(); i++) {
				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();

				rowData.add((i+1)+"");
				rowData.add(deparmentList.get(i).getDeptCode()+" "+deparmentList.get(i).getDeptDesc());
				for(int k=0;k<list.size();k++) {
					List<MonthWiseIssueReport> monthList=list.get(k).getMonthList();
				
				for(int j=0;j<monthList.size();j++)
				{
					if(monthList.get(j).getDeptId()==deparmentList.get(i).getDeptId())
					{
						//rowData.add(""+monthList.get(j).getIssueQty());
						rowData.add(""+monthList.get(j).getIssueQtyValue());
					}
				}
				
				}
			


				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);

			}

			HttpSession session = request.getSession();
			session.setAttribute("exportExcelList", exportToExcelList);
			session.setAttribute("excelName", "MonthWiseConsumption(Issues)");
			//------------------------------------END------------------------------------------
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/listForIssueGraphDeptWise", method = RequestMethod.GET)
	public @ResponseBody List<IssueMonthWiseList> listForIssueGraphDeptWise(HttpServletRequest request, HttpServletResponse response) {

		 
		return listGlobal;
	}
	
	@RequestMapping(value = "/getDeptListForGraph", method = RequestMethod.GET)
	public @ResponseBody List<Dept> getDeptListForGraph(HttpServletRequest request, HttpServletResponse response) {

		 
		return deparmentList;
	}
	
	@RequestMapping(value = "/issueMonthWieReportPdf/{typeName}/{isDevName}", method = RequestMethod.GET)
	public void issueMonthWieReportPdf(@PathVariable String typeName,@PathVariable String isDevName, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		BufferedOutputStream outStream = null;
		try {
		Document document = new Document(PageSize.A4.rotate(), 10f, 10f, 10f, 0f);
		DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
		String reportDate = DF.format(new Date());
        document.addHeader("Date: ", reportDate);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("time in Gen Bill PDF ==" + dateFormat.format(cal.getTime()));
		String timeStamp = dateFormat.format(cal.getTime());
		String FILE_PATH = Constants.REPORT_SAVE;
		File file = new File(FILE_PATH);

		PdfWriter writer = null;

		FileOutputStream out = new FileOutputStream(FILE_PATH);
		try {
			writer = PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
	
		PdfPTable table = new PdfPTable(15);
		try {
			  
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] {0.4f, 1.7f, 1.0f,  1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f});
			Font headFont = new Font(FontFamily.TIMES_ROMAN,8, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 11.0f, Font.UNDERLINE, BaseColor.BLUE);
			Font f1 = new Font(FontFamily.TIMES_ROMAN, 9.0f, Font.BOLD, BaseColor.DARK_GRAY);
			PdfPCell hcell = new PdfPCell();
			
			hcell.setPadding(4);
			hcell = new PdfPCell(new Phrase("SR.", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Department Name", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("APR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			hcell = new PdfPCell(new Phrase("MAY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			hcell = new PdfPCell(new Phrase("JUN", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			hcell = new PdfPCell(new Phrase("JUL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			hcell = new PdfPCell(new Phrase("AUG", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("SEP", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("OCT", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("NOV", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("DEC", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("JAN", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("FEB", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("MAR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("Total", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			 
			float aprTotal=0;
			float mayTotal=0;
			float junTotal=0;
			float julTotal=0;
			float augTotal=0;
			float sepTotal=0;
			float octTotal=0;
			float novTotal=0;
			float decTotal=0;
			float janTotal=0;
			float febTotal=0;
			float marTotal=0;
			
			int index = 0;
			if(!deparmentList.isEmpty()) {
					for (int k = 0; k < deparmentList.size(); k++) {
                            
						float issueQtyValue=0;
						
							index++;
						
							PdfPCell cell;
							
							cell = new PdfPCell(new Phrase(""+index, headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPadding(3);
							table.addCell(cell);

						
							cell = new PdfPCell(new Phrase(deparmentList.get(k).getDeptCode()+" "+deparmentList.get(k).getDeptDesc(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							
							for (int j = 0; j < listGlobal.size(); j++) {
								
								List<MonthWiseIssueReport> monthList=listGlobal.get(j).getMonthList();
								
								for(int l=0;l<monthList.size();l++)
								{
									if(monthList.get(l).getDeptId()==deparmentList.get(k).getDeptId())
									{
											  
											cell = new PdfPCell(new Phrase(""+df.format(monthList.get(l).getIssueQtyValue()), headFont));
											cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
											cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
											cell.setPaddingRight(2);
											cell.setPadding(3);
											table.addCell(cell);
											issueQtyValue=issueQtyValue+monthList.get(l).getIssueQtyValue();
											
											if(monthList.get(l).getMonthNo()==1) {
												 janTotal=janTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==2) {
												 febTotal=febTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==3) {
												 marTotal=marTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==4) {
												 aprTotal=aprTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==5) {
												 mayTotal=mayTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==6) {
												 junTotal=junTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==7) {
												 julTotal=julTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==8) {
												 augTotal=augTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==9) {
												 sepTotal=sepTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==10) {
												 octTotal=octTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==11) {
												 novTotal=novTotal+monthList.get(l).getIssueQtyValue();
											 }
											 if(monthList.get(l).getMonthNo()==12) {
												 decTotal=decTotal+monthList.get(l).getIssueQtyValue();
											 }
									}
								}
							}
							
							cell = new PdfPCell(new Phrase(""+df.format(issueQtyValue), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPadding(3);
							table.addCell(cell);
					
					}
			}
			
			PdfPCell cell;
			
			cell = new PdfPCell(new Phrase("Total", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3);
			cell.setColspan(2);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(aprTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(mayTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(""+df.format(junTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(""+df.format(julTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(""+df.format(augTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(sepTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(octTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(novTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(decTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(janTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(febTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(marTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format((marTotal+aprTotal+mayTotal+junTotal+julTotal+augTotal+sepTotal+octTotal+novTotal+decTotal+febTotal+janTotal)), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
 
			
			document.open();
			Paragraph company = new Paragraph(companyInfo.getCompanyName()+"\n", f);
			company.setAlignment(Element.ALIGN_CENTER);
			document.add(company);
			
				Paragraph heading1 = new Paragraph(
						companyInfo.getFactoryAdd(),f1);
				heading1.setAlignment(Element.ALIGN_CENTER);
				document.add(heading1);
				Paragraph ex2=new Paragraph("\n");
				document.add(ex2);

				Paragraph report=new Paragraph("Month Wise Consumption(Issues)",f1);
				report.setAlignment(Element.ALIGN_CENTER);
				document.add(report);
			
				Paragraph type=new Paragraph("Type : " + typeName +", Is Dev :" + isDevName,f1);
				type.setAlignment(Element.ALIGN_CENTER);
				document.add(type);
		  
			Paragraph ex3=new Paragraph("\n");
			document.add(ex3);
			table.setHeaderRows(1);
			document.add(table);
			
		
			int totalPages = writer.getPageNumber();

			System.out.println("Page no " + totalPages);

			document.close();
			// Atul Sir code to open a Pdf File
			if (file != null) {

				String mimeType = URLConnection.guessContentTypeFromName(file.getName());

				if (mimeType == null) {

					mimeType = "application/pdf";

				}

				response.setContentType(mimeType);

				response.addHeader("content-disposition", String.format("inline; filename=\"%s\"", file.getName()));

				response.setContentLength((int) file.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				try {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				} catch (IOException e) {
					System.out.println("Excep in Opening a Pdf File");
					e.printStackTrace();
				}
			}

		} catch (DocumentException ex) {

			System.out.println("Pdf Generation Error" + ex.getMessage());

			ex.printStackTrace();

		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	List<IssueMonthWiseList> subDeptWiselistForPdf = new ArrayList<IssueMonthWiseList>();
	List<GetSubDept> subDeptList = new ArrayList<>();
	int deptIdForPdf=0;
	@RequestMapping(value = "/issueMonthSubDeptWieReportByDeptId/{deptId}/{typeName}/{isDevName}/{deptName}", method = RequestMethod.GET)
	public ModelAndView issueMonthSubDeptWieReportByDeptId(@PathVariable int deptId,@PathVariable String typeName,
			@PathVariable String isDevName,@PathVariable String deptName,HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("valuationReport/issueReportMonthSubDeptWise");
		try {
				List<IssueMonthWiseList> subDeptWiselist = new ArrayList<IssueMonthWiseList>();
			  
					MultiValueMap<String, Object> map = new LinkedMultiValueMap<>(); 
		 			map.add("typeId", typeId);
		 			map.add("isDev", isDev);
		 			map.add("deptId", deptId);
		 			System.out.println(map);
		 			IssueMonthWiseList[] issueMonthWiseList = rest.postForObject(Constants.url + "/issueMonthSubDeptWiseReportByDeptId",map, IssueMonthWiseList[].class);
		 			subDeptWiselist = new ArrayList<IssueMonthWiseList>(Arrays.asList(issueMonthWiseList));
		 			subDeptWiselistForPdf=subDeptWiselist;
				model.addObject("list", subDeptWiselist);
				
				GetSubDept[] getSubDept = rest.getForObject(Constants.url + "/getAllSubDept", GetSubDept[].class);
				 subDeptList = new ArrayList<GetSubDept>(Arrays.asList(getSubDept));
				
				model.addObject("subDeptList", subDeptList);
				
				model.addObject("deptId", deptId);
				model.addObject("type", typeName);
				model.addObject("isDevName", isDevName);
				model.addObject("deptName", deptName);
				deptIdForPdf=deptId;
				//------------------------ Export To Excel--------------------------------------
				List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

				ExportToExcel expoExcel = new ExportToExcel();
				List<String> rowData = new ArrayList<String>();
;

					/*rowData.add("SR. No");
					rowData.add("SUB DEPARMENT NAME");
					rowData.add("APR ISSUE QTY");
					rowData.add("APR ISSUE VALUE");
					rowData.add("MAY ISSUE QTY");
					rowData.add("MAY ISSUE VALUE");
					rowData.add("JUNE ISSUE QTY");
					rowData.add("JUNE ISSUE VALUE");
					rowData.add("JULY ISSUE QTY");
					rowData.add("JULY ISSUE VALUE");
					rowData.add("AUGUST ISSUE QTY");
					rowData.add("AUGUST ISSUE VALUE");
					rowData.add("SEPTEMBR ISSUE QTY");
					rowData.add("SEPTEMBR ISSUE VALUE");
					rowData.add("OCTOMBER ISSUE QTY");
					rowData.add("OCTOMBER ISSUE VALUE");
					rowData.add("NOVEMBER ISSUE QTY");
					rowData.add("NOVEMBER ISSUE VALUE");
					rowData.add("DECEMBER ISSUE QTY");
					rowData.add("DECEMBER ISSUE VALUE");
					rowData.add("JANUARY ISSUE QTY");
					rowData.add("JANUARY ISSUE VALUE");
					rowData.add("FEBRUARY ISSUE QTY");
					rowData.add("FEBRUARY ISSUE VALUE");
					rowData.add("MARCH ISSUE QTY");
					rowData.add("MARCH ISSUE VALUE");*/ 


					rowData.add("SR. No");
					rowData.add("SUB DEPARMENT NAME"); 
					rowData.add("APR ISSUE VALUE"); 
					rowData.add("MAY ISSUE VALUE"); 
					rowData.add("JUNE ISSUE VALUE"); 
					rowData.add("JULY ISSUE VALUE"); 
					rowData.add("AUGUST ISSUE VALUE"); 
					rowData.add("SEPTEMBR ISSUE VALUE"); 
					rowData.add("OCTOMBER ISSUE VALUE"); 
					rowData.add("NOVEMBER ISSUE VALUE"); 
					rowData.add("DECEMBER ISSUE VALUE"); 
					rowData.add("JANUARY ISSUE VALUE"); 
					rowData.add("FEBRUARY ISSUE VALUE"); 
					rowData.add("MARCH ISSUE VALUE");

					expoExcel.setRowData(rowData);
				
				exportToExcelList.add(expoExcel);
				int index = 0;
				for (int i = 0; i < subDeptList.size(); i++) {
					
					if(deptIdForPdf==subDeptList.get(i).getDeptId()) {
					expoExcel = new ExportToExcel();
					rowData = new ArrayList<String>();
					index++;
					rowData.add((index)+"");
					rowData.add(subDeptList.get(i).getSubDeptCode()+" "+subDeptList.get(i).getSubDeptDesc());
					for(int k=0;k<subDeptWiselist.size();k++) {
						List<MonthSubDeptWiseIssueReport> monthList=subDeptWiselist.get(k).getMonthSubDeptList();
					
					for(int j=0;j<monthList.size();j++)
					{
						if(monthList.get(j).getSubDeptId()==subDeptList.get(i).getSubDeptId())
						{
							//rowData.add(""+monthList.get(j).getIssueQty());
							rowData.add(""+monthList.get(j).getIssueQtyValue());
						}
					}
					
					}
				


					expoExcel.setRowData(rowData);
					exportToExcelList.add(expoExcel);
					}
				}

				HttpSession session = request.getSession();
				session.setAttribute("exportExcelList", exportToExcelList);
				session.setAttribute("excelName", "SubDeptMonthWiseConsumption(Issues)");
				 
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/listForIssueMonthGraphSubDeptWise", method = RequestMethod.GET)
	public @ResponseBody List<IssueMonthWiseList> listForIssueMonthGraphSubDeptWise(HttpServletRequest request, HttpServletResponse response) {

		 
		return subDeptWiselistForPdf;
	}
	
	@RequestMapping(value = "/getSubDeptListForGraph", method = RequestMethod.GET)
	public @ResponseBody List<GetSubDept> getSubDeptListForGraph(HttpServletRequest request, HttpServletResponse response) {

		 
		return subDeptList;
	}
	
	@RequestMapping(value = "/issueMonthSubDeptWiseReportPdf/{typeName}/{isDevName}/{deptName}", method = RequestMethod.GET)
	public void issueMonthSubDeptWiseReportPdf(@PathVariable String typeName, @PathVariable String isDevName, @PathVariable String deptName, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		BufferedOutputStream outStream = null;
		try {
		Document document = new Document(PageSize.A4.rotate(), 10f, 10f, 10f, 0f);
		DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
		String reportDate = DF.format(new Date());
        document.addHeader("Date: ", reportDate);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("time in Gen Bill PDF ==" + dateFormat.format(cal.getTime()));
		String timeStamp = dateFormat.format(cal.getTime());
		String FILE_PATH = Constants.REPORT_SAVE;
		File file = new File(FILE_PATH);

		PdfWriter writer = null;

		FileOutputStream out = new FileOutputStream(FILE_PATH);
		try {
			writer = PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
		
		float aprTotal=0;
		float mayTotal=0;
		float junTotal=0;
		float julTotal=0;
		float augTotal=0;
		float sepTotal=0;
		float octTotal=0;
		float novTotal=0;
		float decTotal=0;
		float janTotal=0;
		float febTotal=0;
		float marTotal=0;
	
		PdfPTable table = new PdfPTable(15);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] {0.4f, 1.7f, 1.0f,  1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f});
			Font headFont = new Font(FontFamily.TIMES_ROMAN,8, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 11.0f, Font.UNDERLINE, BaseColor.BLUE);
			Font f1 = new Font(FontFamily.TIMES_ROMAN, 9.0f, Font.BOLD, BaseColor.DARK_GRAY);
			PdfPCell hcell = new PdfPCell();
 
			
			hcell.setPadding(4);
			hcell = new PdfPCell(new Phrase("SR.", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Sub Department Name", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("APR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("MAY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("JUN", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("JUL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("AUG", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("SEP", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("OCT", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("NOV", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("DEC", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("JAN", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("FEB", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("MAR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("Total", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			 
			
			
			int index = 0;
			if(!subDeptWiselistForPdf.isEmpty()) {
					for (int k = 0; k < subDeptList.size(); k++) {
						if(deptIdForPdf==subDeptList.get(k).getDeptId()) {
							index++;
							float issueQtyValue=0;
							PdfPCell cell;
							
							cell = new PdfPCell(new Phrase(""+index, headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPadding(3);
							table.addCell(cell);

						
							cell = new PdfPCell(new Phrase(subDeptList.get(k).getSubDeptCode()+" "+subDeptList.get(k).getSubDeptDesc(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							
							for (int j = 0; j < subDeptWiselistForPdf.size(); j++) {
								
								List<MonthSubDeptWiseIssueReport> monthList=subDeptWiselistForPdf.get(j).getMonthSubDeptList();
								
								for(int l=0;l<monthList.size();l++)
								{
									if(monthList.get(l).getSubDeptId()==subDeptList.get(k).getSubDeptId())
									{
											 
											cell = new PdfPCell(new Phrase(""+monthList.get(l).getIssueQtyValue(), headFont));
											cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
											cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
											cell.setPaddingRight(2);
											cell.setPadding(3);
											table.addCell(cell);
											issueQtyValue=issueQtyValue+monthList.get(l).getIssueQtyValue();
											
											if(monthList.get(l).getMonthNo()==1) {
												 janTotal=janTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==2) {
												 febTotal=febTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==3) {
												 marTotal=marTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==4) {
												 aprTotal=aprTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==5) {
												 mayTotal=mayTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==6) {
												 junTotal=junTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==7) {
												 julTotal=julTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==8) {
												 augTotal=augTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==9) {
												 sepTotal=sepTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==10) {
												 octTotal=octTotal+monthList.get(l).getIssueQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==11) {
												 novTotal=novTotal+monthList.get(l).getIssueQtyValue();
											 }
											 if(monthList.get(l).getMonthNo()==12) {
												 decTotal=decTotal+monthList.get(l).getIssueQtyValue();
											 }
									}
								}
							}
							
							cell = new PdfPCell(new Phrase(""+df.format(issueQtyValue), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPadding(3);
							table.addCell(cell);
					
					}
					}
			}
			
			PdfPCell cell;
			
			cell = new PdfPCell(new Phrase("Total", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3);
			cell.setColspan(2);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(aprTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(mayTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(junTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(julTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(augTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(sepTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(octTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(novTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(decTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(janTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(febTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(marTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format((marTotal+aprTotal+mayTotal+junTotal+julTotal+augTotal+sepTotal+octTotal+novTotal+decTotal+febTotal+janTotal)), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			document.open();
			Paragraph company = new Paragraph(companyInfo.getCompanyName()+"\n", f);
			company.setAlignment(Element.ALIGN_CENTER);
			document.add(company);
			
				Paragraph heading1 = new Paragraph(
						companyInfo.getFactoryAdd(),f1);
				heading1.setAlignment(Element.ALIGN_CENTER);
				document.add(heading1);
				Paragraph ex2=new Paragraph("\n");
				document.add(ex2);

				Paragraph headingDate=new Paragraph("Sub Dept Month Wise Consumption(Issues)",f1);
				headingDate.setAlignment(Element.ALIGN_CENTER);
			document.add(headingDate);
			
			Paragraph type=new Paragraph("Department: "+deptName+", Type : " + typeName +", Is Dev : " + isDevName,f1);
			type.setAlignment(Element.ALIGN_CENTER);
		document.add(type);
			
			Paragraph ex3=new Paragraph("\n");
			document.add(ex3);
			table.setHeaderRows(1);
			document.add(table);
			
		
			int totalPages = writer.getPageNumber();

			System.out.println("Page no " + totalPages);

			document.close();
			// Atul Sir code to open a Pdf File
			if (file != null) {

				String mimeType = URLConnection.guessContentTypeFromName(file.getName());

				if (mimeType == null) {

					mimeType = "application/pdf";

				}

				response.setContentType(mimeType);

				response.addHeader("content-disposition", String.format("inline; filename=\"%s\"", file.getName()));

				response.setContentLength((int) file.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				try {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				} catch (IOException e) {
					System.out.println("Excep in Opening a Pdf File");
					e.printStackTrace();
				}
			}

		} catch (DocumentException ex) {

			System.out.println("Pdf Generation Error" + ex.getMessage());

			ex.printStackTrace();

		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	List<IssueMonthWiseList> issueItemWiselistForPdf = new ArrayList<IssueMonthWiseList>();
	List<GetItem> itemListforPdf = new ArrayList<>();
	
	@RequestMapping(value = "/issueMonthItemWieReportBySubDeptId/{subDeptId}", method = RequestMethod.GET)
	public ModelAndView issueMonthItemWieReportBySubDeptId(@PathVariable int subDeptId,HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("valuationReport/issueReportMonthItemWise");
		try {
				List<IssueMonthWiseList> subDeptWiselist = new ArrayList<IssueMonthWiseList>();
			  
					MultiValueMap<String, Object> map = new LinkedMultiValueMap<>(); 
		 			map.add("typeId", typeId);
		 			map.add("isDev", isDev);
		 			map.add("subDeptId", subDeptId);
		 			System.out.println(map);
		 			IssueMonthWiseList[] issueMonthWiseList = rest.postForObject(Constants.url + "/issueMonthItemWiseReportBySubDeptId",map, IssueMonthWiseList[].class);
		 			subDeptWiselist = new ArrayList<IssueMonthWiseList>(Arrays.asList(issueMonthWiseList));
		 			issueItemWiselistForPdf=subDeptWiselist;
				model.addObject("list", subDeptWiselist);
				
				GetItem[] item = rest.getForObject(Constants.url + "/getAllItems",  GetItem[].class); 
				List<GetItem> itemList = new ArrayList<GetItem>(Arrays.asList(item));
				model.addObject("itemList", itemList);
				itemListforPdf=itemList;
				//------------------------ Export To Excel--------------------------------------
				List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

				ExportToExcel expoExcel = new ExportToExcel();
				List<String> rowData = new ArrayList<String>(); 
				
				
					/*rowData.add("SR. No");
					rowData.add("ITEM NAME");
					rowData.add("APR ISSUE QTY");
					rowData.add("APR ISSUE VALUE");
					rowData.add("MAY ISSUE QTY");
					rowData.add("MAY ISSUE VALUE");
					rowData.add("JUNE ISSUE QTY");
					rowData.add("JUNE ISSUE VALUE");
					rowData.add("JULY ISSUE QTY");
					rowData.add("JULY ISSUE VALUE");
					rowData.add("AUGUST ISSUE QTY");
					rowData.add("AUGUST ISSUE VALUE");
					rowData.add("SEPTEMBR ISSUE QTY");
					rowData.add("SEPTEMBR ISSUE VALUE");
					rowData.add("OCTOMBER ISSUE QTY");
					rowData.add("OCTOMBER ISSUE VALUE");
					rowData.add("NOVEMBER ISSUE QTY");
					rowData.add("NOVEMBER ISSUE VALUE");
					rowData.add("DECEMBER ISSUE QTY");
					rowData.add("DECEMBER ISSUE VALUE");
					rowData.add("JANUARY ISSUE QTY");
					rowData.add("JANUARY ISSUE VALUE");
					rowData.add("FEBRUARY ISSUE QTY");
					rowData.add("FEBRUARY ISSUE VALUE");
					rowData.add("MARCH ISSUE QTY");
					rowData.add("MARCH ISSUE VALUE");*/
					
					rowData.add("SR. No");
					rowData.add("ITEM NAME"); 
					rowData.add("APR ISSUE VALUE"); 
					rowData.add("MAY ISSUE VALUE"); 
					rowData.add("JUNE ISSUE VALUE"); 
					rowData.add("JULY ISSUE VALUE"); 
					rowData.add("AUGUST ISSUE VALUE"); 
					rowData.add("SEPTEMBR ISSUE VALUE"); 
					rowData.add("OCTOMBER ISSUE VALUE"); 
					rowData.add("NOVEMBER ISSUE VALUE"); 
					rowData.add("DECEMBER ISSUE VALUE"); 
					rowData.add("JANUARY ISSUE VALUE"); 
					rowData.add("FEBRUARY ISSUE VALUE"); 
					rowData.add("MARCH ISSUE VALUE");

					expoExcel.setRowData(rowData);
				
				exportToExcelList.add(expoExcel);
				int index = 0;
				for (int i = 0; i < itemList.size(); i++) {
					
					 
					expoExcel = new ExportToExcel();
					rowData = new ArrayList<String>();
					index++;
					rowData.add((index)+"");
					rowData.add(itemList.get(i).getItemCode()+" "+itemList.get(i).getItemDesc());
					for(int k=0;k<subDeptWiselist.size();k++) {
						List<MonthSubDeptWiseIssueReport> monthList=subDeptWiselist.get(k).getMonthSubDeptList();
					
					for(int j=0;j<monthList.size();j++)
					{
						if(monthList.get(j).getSubDeptId()==itemList.get(i).getItemId())
						{
							//rowData.add(""+monthList.get(j).getIssueQty());
							rowData.add(""+monthList.get(j).getIssueQtyValue());
						}
					}
					
					}
				


					expoExcel.setRowData(rowData);
					exportToExcelList.add(expoExcel);
					 
				}

				HttpSession session = request.getSession();
				session.setAttribute("exportExcelList", exportToExcelList);
				session.setAttribute("excelName", "ItemIssueMonthWiseConsumption(Issues)");
				  
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	
	@RequestMapping(value = "/issueMonthItemWiseReportPdf", method = RequestMethod.GET)
	public void issueMonthItemWiseReportPdf(HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		BufferedOutputStream outStream = null;
		try {
		Document document = new Document(PageSize.A3);
		DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
		String reportDate = DF.format(new Date());
        document.addHeader("Date: ", reportDate);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("time in Gen Bill PDF ==" + dateFormat.format(cal.getTime()));
		String timeStamp = dateFormat.format(cal.getTime());
		String FILE_PATH = Constants.REPORT_SAVE;
		File file = new File(FILE_PATH);

		PdfWriter writer = null;

		FileOutputStream out = new FileOutputStream(FILE_PATH);
		try {
			writer = PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
	
		PdfPTable table = new PdfPTable(14);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] {0.4f, 3.0f, 0.6f,  0.6f, 0.6f, 0.6f, 0.6f, 0.6f, 0.6f, 0.6f, 0.6f, 0.6f, 0.6f, 0.6f});
			Font headFont = new Font(FontFamily.TIMES_ROMAN,6, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 11.0f, Font.UNDERLINE, BaseColor.BLUE);
			Font f1 = new Font(FontFamily.TIMES_ROMAN, 9.0f, Font.BOLD, BaseColor.DARK_GRAY);
			PdfPCell hcell = new PdfPCell();

			/*hcell.setPadding(4);
			hcell = new PdfPCell(new Phrase("SR.", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Item Name", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("APR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			hcell.setColspan(2);
			table.addCell(hcell);
			hcell = new PdfPCell(new Phrase("MAY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			hcell.setColspan(2);
			table.addCell(hcell);
			hcell = new PdfPCell(new Phrase("JUN", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			hcell.setColspan(2);
			table.addCell(hcell);
			hcell = new PdfPCell(new Phrase("JUL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			hcell.setColspan(2);
			table.addCell(hcell);
			hcell = new PdfPCell(new Phrase("AUG", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			hcell.setColspan(2);
			table.addCell(hcell);
			hcell = new PdfPCell(new Phrase("SEP", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			hcell.setColspan(2);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("OCT", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			hcell.setColspan(2);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("NOV", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			hcell.setColspan(2);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("DEC", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			hcell.setColspan(2);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("JAN", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			hcell.setColspan(2);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("FEB", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			hcell.setColspan(2);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("MAR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			hcell.setColspan(2);
			table.addCell(hcell);
			
			
		    
		    hcell = new PdfPCell();
			hcell.setPadding(4);
			hcell = new PdfPCell(new Phrase(" ", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase(" ", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("VAL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("VAL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("VAL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("VAL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("VAL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("VAL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("VAL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("VAL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("VAL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("VAL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			hcell = new PdfPCell(new Phrase("QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("VAL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("VAL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);*/
			
			hcell.setPadding(4);
			hcell = new PdfPCell(new Phrase("SR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Item Name", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("APR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("MAY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("JUN", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("JUL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("AUG", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("SEP", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("OCT", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("NOV", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("DEC", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("JAN", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("FEB", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("MAR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			 
			int index = 0;
			if(!issueItemWiselistForPdf.isEmpty()) {
					for (int k = 0; k < itemListforPdf.size(); k++) {
						 
							index++;
						
							PdfPCell cell;
							
							cell = new PdfPCell(new Phrase(""+index, headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPadding(3);
							table.addCell(cell);

						
							cell = new PdfPCell(new Phrase(itemListforPdf.get(k).getItemCode()+" "+itemListforPdf.get(k).getItemDesc(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							
							for (int j = 0; j < issueItemWiselistForPdf.size(); j++) {
								
								List<MonthSubDeptWiseIssueReport> monthList=issueItemWiselistForPdf.get(j).getMonthSubDeptList();
								
								for(int l=0;l<monthList.size();l++)
								{
									if(monthList.get(l).getSubDeptId()==itemListforPdf.get(k).getItemId())
									{
											/*cell = new PdfPCell(new Phrase(""+monthList.get(l).getIssueQty(), headFont));
											cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
											cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
											cell.setPaddingRight(2);
											cell.setPadding(3);
											table.addCell(cell);*/
											cell = new PdfPCell(new Phrase(""+monthList.get(l).getIssueQtyValue(), headFont));
											cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
											cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
											cell.setPaddingRight(2);
											cell.setPadding(3);
											table.addCell(cell);
									}
								}
							}
					
					}
				 
			}
			
			document.open();
			Paragraph company = new Paragraph(companyInfo.getCompanyName()+"\n", f);
			company.setAlignment(Element.ALIGN_CENTER);
			document.add(company);
			
				Paragraph heading1 = new Paragraph(
						companyInfo.getFactoryAdd(),f1);
				heading1.setAlignment(Element.ALIGN_CENTER);
				document.add(heading1);
				Paragraph ex2=new Paragraph("\n");
				document.add(ex2);

				Paragraph headingDate=new Paragraph("Item Month Wise Consumption(Issues)",f1);
				headingDate.setAlignment(Element.ALIGN_CENTER);
			document.add(headingDate);
			
			Paragraph ex3=new Paragraph("\n");
			document.add(ex3);
			table.setHeaderRows(1);
			document.add(table);
			
		
			int totalPages = writer.getPageNumber();

			System.out.println("Page no " + totalPages);

			document.close();
			// Atul Sir code to open a Pdf File
			if (file != null) {

				String mimeType = URLConnection.guessContentTypeFromName(file.getName());

				if (mimeType == null) {

					mimeType = "application/pdf";

				}

				response.setContentType(mimeType);

				response.addHeader("content-disposition", String.format("inline; filename=\"%s\"", file.getName()));

				response.setContentLength((int) file.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				try {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				} catch (IOException e) {
					System.out.println("Excep in Opening a Pdf File");
					e.printStackTrace();
				}
			}

		} catch (DocumentException ex) {

			System.out.println("Pdf Generation Error" + ex.getMessage());

			ex.printStackTrace();

		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	
	List<MrnMonthWiseList> mrnCategoryMonthWiseListForPdf = new ArrayList<MrnMonthWiseList>();
	List<Category> categoryList = new ArrayList<Category>();
	
	@RequestMapping(value = "/mrnMonthCategoryWieReport", method = RequestMethod.GET)
	public ModelAndView mrnMonthCategoryWieReport(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("valuationReport/mrnMonthCategoryWieReport");
		try {
			List<MrnMonthWiseList> list = new ArrayList<MrnMonthWiseList>();
			Type[] type = rest.getForObject(Constants.url + "/getAlltype", Type[].class);
			List<Type> typeList = new ArrayList<Type>(Arrays.asList(type));
			model.addObject("typeList", typeList);
			
			Dept[] Dept = rest.getForObject(Constants.url + "/getAllDeptByIsUsed", Dept[].class);
			List<Dept> deparmentList = new ArrayList<Dept>(Arrays.asList(Dept));
			model.addObject("deparmentList", deparmentList);
			 
			if(request.getParameter("typeId")==null || request.getParameter("isDev")==null) {
				
				typeId = 0;
				isDev =-1;
				deptId =0;
				subDeptId =0;
				
					MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
					/*map.add("fromDate",DateConvertor.convertToYMD(fromDate));
		 			map.add("toDate",DateConvertor.convertToYMD(toDate)); */
		 			map.add("typeId", typeId);
		 			
		 			map.add("deptId", deptId);
		 			map.add("subDeptId", subDeptId);
		 			if(isDev==-1) {
		 				map.add("isDev", "0,1");
		 			}
		 			else {
		 				map.add("isDev", isDev);
		 			}
		 			System.out.println(map);
		 			MrnMonthWiseList[] mrnMonthWiseList = rest.postForObject(Constants.url + "/mrnMonthCategoryWiseReport",map, MrnMonthWiseList[].class);
		 			list = new ArrayList<MrnMonthWiseList>(Arrays.asList(mrnMonthWiseList));
		 			  
				model.addObject("list", list); 
				model.addObject("typeId", typeId);
				model.addObject("isDevelompent", isDev);
				model.addObject("deptId", deptId);
				model.addObject("subDeptId", subDeptId);
				
				/*Category[] category = rest.getForObject(Constants.url + "/getAllCategoryByIsUsed", Category[].class);
				List<Category> categoryList = new ArrayList<Category>(Arrays.asList(category)); 
				model.addObject("categoryList", categoryList);*/
			}
			else {
				/*fromDate = request.getParameter("fromDate");
				toDate = request.getParameter("toDate");*/
				typeId = Integer.parseInt(request.getParameter("typeId"));
				isDev =Integer.parseInt(request.getParameter("isDev"));
				deptId =Integer.parseInt(request.getParameter("deptId"));
				subDeptId =Integer.parseInt(request.getParameter("subDeptId"));
				
					MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
					/*map.add("fromDate",DateConvertor.convertToYMD(fromDate));
		 			map.add("toDate",DateConvertor.convertToYMD(toDate)); */
		 			map.add("typeId", typeId);
		 			
		 			map.add("deptId", deptId);
		 			map.add("subDeptId", subDeptId);
		 			if(isDev==-1) {
		 				map.add("isDev", "0,1");
		 			}
		 			else {
		 				map.add("isDev", isDev);
		 			}
		 			System.out.println(map);
		 			MrnMonthWiseList[] mrnMonthWiseList = rest.postForObject(Constants.url + "/mrnMonthCategoryWiseReport",map, MrnMonthWiseList[].class);
		 			list = new ArrayList<MrnMonthWiseList>(Arrays.asList(mrnMonthWiseList));
		 			  
				model.addObject("list", list); 
				model.addObject("typeId", typeId);
				model.addObject("isDevelompent", isDev);
				model.addObject("deptId", deptId);
				model.addObject("subDeptId", subDeptId);
				 
			}
			
			Category[] category = rest.getForObject(Constants.url + "/getAllCategoryByIsUsed", Category[].class);
			categoryList = new ArrayList<Category>(Arrays.asList(category)); 
			model.addObject("categoryList", categoryList);
			
			mrnCategoryMonthWiseListForPdf=list;
			//------------------------ Export To Excel--------------------------------------
			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

			ExportToExcel expoExcel = new ExportToExcel();
			List<String> rowData = new ArrayList<String>();
;

				/*rowData.add("SR. No");
				rowData.add("CAT NAME");
				rowData.add("APR ISSUE QTY");
				rowData.add("APR ISSUE VALUE");
				rowData.add("MAY ISSUE QTY");
				rowData.add("MAY ISSUE VALUE");
				rowData.add("JUNE ISSUE QTY");
				rowData.add("JUNE ISSUE VALUE");
				rowData.add("JULY ISSUE QTY");
				rowData.add("JULY ISSUE VALUE");
				rowData.add("AUGUST ISSUE QTY");
				rowData.add("AUGUST ISSUE VALUE");
				rowData.add("SEPTEMBR ISSUE QTY");
				rowData.add("SEPTEMBR ISSUE VALUE");
				rowData.add("OCTOMBER ISSUE QTY");
				rowData.add("OCTOMBER ISSUE VALUE");
				rowData.add("NOVEMBER ISSUE QTY");
				rowData.add("NOVEMBER ISSUE VALUE");
				rowData.add("DECEMBER ISSUE QTY");
				rowData.add("DECEMBER ISSUE VALUE");
				rowData.add("JANUARY ISSUE QTY");
				rowData.add("JANUARY ISSUE VALUE");
				rowData.add("FEBRUARY ISSUE QTY");
				rowData.add("FEBRUARY ISSUE VALUE");
				rowData.add("MARCH ISSUE QTY");
				rowData.add("MARCH ISSUE VALUE");*/
				
				rowData.add("SR. No");
				rowData.add("CAT NAME"); 
				rowData.add("APR ISSUE VALUE"); 
				rowData.add("MAY ISSUE VALUE"); 
				rowData.add("JUNE ISSUE VALUE"); 
				rowData.add("JULY ISSUE VALUE"); 
				rowData.add("AUGUST ISSUE VALUE"); 
				rowData.add("SEPTEMBR ISSUE VALUE"); 
				rowData.add("OCTOMBER ISSUE VALUE"); 
				rowData.add("NOVEMBER ISSUE VALUE"); 
				rowData.add("DECEMBER ISSUE VALUE"); 
				rowData.add("JANUARY ISSUE VALUE"); 
				rowData.add("FEBRUARY ISSUE VALUE"); 
				rowData.add("MARCH ISSUE VALUE");

				expoExcel.setRowData(rowData);
			
			exportToExcelList.add(expoExcel);
			int index = 0;
			for (int i = 0; i < categoryList.size(); i++) {
				
				
				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();
				index++;
				rowData.add((index)+"");
				rowData.add(categoryList.get(i).getCatDesc());
				for(int k=0;k<list.size();k++) {
					List<MonthCategoryWiseMrnReport> monthList=list.get(k).getMonthList();
				
				for(int j=0;j<monthList.size();j++)
				{
					if(monthList.get(j).getCatId()==categoryList.get(i).getCatId())
					{
						//rowData.add(""+monthList.get(j).getApproveQty());
						rowData.add(""+monthList.get(j).getApprovedQtyValue());
					}
				}
				
				}
			


				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);
				 
			}

			HttpSession session = request.getSession();
			session.setAttribute("exportExcelList", exportToExcelList);
			session.setAttribute("excelName", "MrnCategoryMonthWiseList");
			 companyInfo = rest.getForObject(Constants.url + "getCompanyDetails",
						Company.class);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/listForMrnGraphCategoryMonthWise", method = RequestMethod.GET)
	public @ResponseBody List<MrnMonthWiseList> listForMrnGraphCategoryMonthWise(HttpServletRequest request, HttpServletResponse response) {

		 
		return mrnCategoryMonthWiseListForPdf;
	}
	
	@RequestMapping(value = "/getCatListForGraph", method = RequestMethod.GET)
	public @ResponseBody List<Category> getCatListForGraph(HttpServletRequest request, HttpServletResponse response) {

		 
		return categoryList;
	}
	
	@RequestMapping(value = "/mrnCategoryMonthWiseReportPdf/{typeName}/{isDevName}/{deptName}/{subDeptName}", method = RequestMethod.GET)
	public void mrnCategoryMonthWiseReportPdf(@PathVariable String typeName,@PathVariable String isDevName,@PathVariable String deptName,@PathVariable String subDeptName, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		BufferedOutputStream outStream = null;
		try {
		Document document = new Document(PageSize.A4.rotate(), 10f, 10f, 10f, 0f);
		DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
		String reportDate = DF.format(new Date());
        document.addHeader("Date: ", reportDate);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();
 
		System.out.println("time in Gen Bill PDF ==" + dateFormat.format(cal.getTime()));
		String timeStamp = dateFormat.format(cal.getTime());
		String FILE_PATH = Constants.REPORT_SAVE;
		File file = new File(FILE_PATH);

		PdfWriter writer = null;

		FileOutputStream out = new FileOutputStream(FILE_PATH);
		try {
			writer = PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
	 
			float aprTotal=0;
			float mayTotal=0;
			float junTotal=0;
			float julTotal=0;
			float augTotal=0;
			float sepTotal=0;
			float octTotal=0;
			float novTotal=0;
			float decTotal=0;
			float janTotal=0;
			float febTotal=0;
			float marTotal=0;
			
		PdfPTable table = new PdfPTable(15);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] {0.4f, 1.7f, 1.0f,  1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f});
			Font headFont = new Font(FontFamily.TIMES_ROMAN,9, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 11.0f, Font.UNDERLINE, BaseColor.BLUE);
			Font f1 = new Font(FontFamily.TIMES_ROMAN, 9.0f, Font.BOLD, BaseColor.DARK_GRAY);
			PdfPCell hcell = new PdfPCell();
			
			hcell.setPadding(4);
			hcell = new PdfPCell(new Phrase("SR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Cat Name", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("APR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("MAY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("JUN", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("JUL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("AUG", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("SEP", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("OCT", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("NOV", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("DEC", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("JAN", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("FEB", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("MAR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("TOTAL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
 
			int index = 0;
			if(!mrnCategoryMonthWiseListForPdf.isEmpty()) {
					for (int k = 0; k < categoryList.size(); k++) {
						 
							index++;
						
							float mrnQtyValue=0;
							
							PdfPCell cell;
							
							cell = new PdfPCell(new Phrase(""+index, headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPadding(3);
							table.addCell(cell);

						
							cell = new PdfPCell(new Phrase(categoryList.get(k).getCatDesc(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							
							for (int j = 0; j < mrnCategoryMonthWiseListForPdf.size(); j++) {
								
								List<MonthCategoryWiseMrnReport> monthList=mrnCategoryMonthWiseListForPdf.get(j).getMonthList();
								
								for(int l=0;l<monthList.size();l++)
								{
									if(monthList.get(l).getCatId()==categoryList.get(k).getCatId())
									{
											 
											cell = new PdfPCell(new Phrase(""+monthList.get(l).getApprovedQtyValue(), headFont));
											cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
											cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
											cell.setPaddingRight(2);
											cell.setPadding(3);
											table.addCell(cell);
											
											mrnQtyValue=mrnQtyValue+monthList.get(l).getApprovedQtyValue();
											
											if(monthList.get(l).getMonthNo()==1) {
												 janTotal=janTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==2) {
												 febTotal=febTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==3) {
												 marTotal=marTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==4) {
												 aprTotal=aprTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==5) {
												 mayTotal=mayTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==6) {
												 junTotal=junTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==7) {
												 julTotal=julTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==8) {
												 augTotal=augTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==9) {
												 sepTotal=sepTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==10) {
												 octTotal=octTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==11) {
												 novTotal=novTotal+monthList.get(l).getApprovedQtyValue();
											 }
											 if(monthList.get(l).getMonthNo()==12) {
												 decTotal=decTotal+monthList.get(l).getApprovedQtyValue();
											 }
									}
								}
							}
							
							cell = new PdfPCell(new Phrase(""+df.format(mrnQtyValue), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPadding(3);
							table.addCell(cell);
					
					}
				 
			}
			
			PdfPCell cell;
			
			cell = new PdfPCell(new Phrase("Total", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3);
			cell.setColspan(2);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(aprTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(mayTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(""+df.format(junTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(""+df.format(julTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(""+df.format(augTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(sepTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(octTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(novTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(decTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(janTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(febTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(marTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format((marTotal+aprTotal+mayTotal+junTotal+julTotal+augTotal+sepTotal+octTotal+novTotal+decTotal+febTotal+janTotal)), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			document.open();
			Paragraph company = new Paragraph(companyInfo.getCompanyName()+"\n", f);
			company.setAlignment(Element.ALIGN_CENTER);
			document.add(company);
			
				Paragraph heading1 = new Paragraph(
						companyInfo.getFactoryAdd(),f1);
				heading1.setAlignment(Element.ALIGN_CENTER);
				document.add(heading1);
				Paragraph ex2=new Paragraph("\n");
				document.add(ex2);

				Paragraph headingDate=new Paragraph("Mrn Category Month Wise Report ",f1);
				headingDate.setAlignment(Element.ALIGN_CENTER);
			document.add(headingDate);
			
			Paragraph type=new Paragraph("Type: "+typeName+", Is Dev: "+isDevName+", Department: "+deptName+", Sub-Dept: "+subDeptName,f1);
			type.setAlignment(Element.ALIGN_CENTER);
		document.add(type);
			
			Paragraph ex3=new Paragraph("\n");
			document.add(ex3);
			table.setHeaderRows(1);
			document.add(table);
			
		
			int totalPages = writer.getPageNumber();

			System.out.println("Page no " + totalPages);

			document.close();
			// Atul Sir code to open a Pdf File
			if (file != null) {

				String mimeType = URLConnection.guessContentTypeFromName(file.getName());

				if (mimeType == null) {

					mimeType = "application/pdf";

				}

				response.setContentType(mimeType);

				response.addHeader("content-disposition", String.format("inline; filename=\"%s\"", file.getName()));

				response.setContentLength((int) file.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				try {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				} catch (IOException e) {
					System.out.println("Excep in Opening a Pdf File");
					e.printStackTrace();
				}
			}

		} catch (DocumentException ex) {

			System.out.println("Pdf Generation Error" + ex.getMessage());

			ex.printStackTrace();

		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	List<MrnMonthWiseList> mrnItemMonthWiseListForPdf = new ArrayList<MrnMonthWiseList>();
	
	@RequestMapping(value = "/mrnMonthItemWiseReportBycatId/{catId}/{typeName}/{isDevName}/{deptName}/{subDeptName}/{catDesc}", method = RequestMethod.GET)
	public ModelAndView mrnMonthItemWiseReportBycatId(@PathVariable int catId,@PathVariable String typeName,@PathVariable String isDevName
			,@PathVariable String deptName,@PathVariable String subDeptName,@PathVariable String catDesc,HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("valuationReport/mrnMonthItemWiseReport");
		try {
			List<MrnMonthWiseList> list = new ArrayList<MrnMonthWiseList>();
			  
					MultiValueMap<String, Object> map = new LinkedMultiValueMap<>(); 
					map.add("typeId", typeId); 
		 			map.add("deptId", deptId);
		 			map.add("subDeptId", subDeptId);
		 			map.add("catId", catId);
		 			if(isDev==-1) {
		 				map.add("isDev", "0,1");
		 			}
		 			else {
		 				map.add("isDev", isDev);
		 			}
		 			System.out.println(map);
		 			MrnMonthWiseList[] mrnMonthWiseList = rest.postForObject(Constants.url + "/mrnMonthItemWiseReport",map, MrnMonthWiseList[].class);
		 			list = new ArrayList<MrnMonthWiseList>(Arrays.asList(mrnMonthWiseList));
				 
		 			mrnItemMonthWiseListForPdf=list;
		 			
				model.addObject("list", list);
				model.addObject("catId", catId);
				GetItem[] item = rest.getForObject(Constants.url + "/getAllItems",  GetItem[].class); 
				List<GetItem> itemList = new ArrayList<GetItem>(Arrays.asList(item));
				model.addObject("itemList", itemList);
				model.addObject("typeName", typeName);
				model.addObject("isDevName", isDevName);
				model.addObject("deptName", deptName);
				model.addObject("subDeptName", subDeptName); 
				model.addObject("catDesc", catDesc);
				
				 itemListforPdf=itemList;
				 deptIdForPdf=catId;
				//------------------------ Export To Excel--------------------------------------
				List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();
				
				ExportToExcel expoExcel = new ExportToExcel();
				List<String> rowData = new ArrayList<String>();
	 

					/*rowData.add("SR. No");
					rowData.add("Item NAME");
					rowData.add("APR ISSUE QTY");
					rowData.add("APR ISSUE VALUE");
					rowData.add("MAY ISSUE QTY");
					rowData.add("MAY ISSUE VALUE");
					rowData.add("JUNE ISSUE QTY");
					rowData.add("JUNE ISSUE VALUE");
					rowData.add("JULY ISSUE QTY");
					rowData.add("JULY ISSUE VALUE");
					rowData.add("AUGUST ISSUE QTY");
					rowData.add("AUGUST ISSUE VALUE");
					rowData.add("SEPTEMBR ISSUE QTY");
					rowData.add("SEPTEMBR ISSUE VALUE");
					rowData.add("OCTOMBER ISSUE QTY");
					rowData.add("OCTOMBER ISSUE VALUE");
					rowData.add("NOVEMBER ISSUE QTY");
					rowData.add("NOVEMBER ISSUE VALUE");
					rowData.add("DECEMBER ISSUE QTY");
					rowData.add("DECEMBER ISSUE VALUE");
					rowData.add("JANUARY ISSUE QTY");
					rowData.add("JANUARY ISSUE VALUE");
					rowData.add("FEBRUARY ISSUE QTY");
					rowData.add("FEBRUARY ISSUE VALUE");
					rowData.add("MARCH ISSUE QTY");
					rowData.add("MARCH ISSUE VALUE");*/
				
				rowData.add("SR. No");
				rowData.add("Item NAME"); 
				rowData.add("APR ISSUE VALUE"); 
				rowData.add("MAY ISSUE VALUE"); 
				rowData.add("JUNE ISSUE VALUE"); 
				rowData.add("JULY ISSUE VALUE"); 
				rowData.add("AUGUST ISSUE VALUE"); 
				rowData.add("SEPTEMBR ISSUE VALUE"); 
				rowData.add("OCTOMBER ISSUE VALUE"); 
				rowData.add("NOVEMBER ISSUE VALUE"); 
				rowData.add("DECEMBER ISSUE VALUE"); 
				rowData.add("JANUARY ISSUE VALUE"); 
				rowData.add("FEBRUARY ISSUE VALUE"); 
				rowData.add("MARCH ISSUE VALUE");

					expoExcel.setRowData(rowData);
				
				exportToExcelList.add(expoExcel);
				int index = 0;
				for (int i = 0; i < itemList.size(); i++) {
					
					if(itemList.get(i).getCatId()==catId) {
					expoExcel = new ExportToExcel();
					rowData = new ArrayList<String>();
					index++;
					rowData.add((index)+"");
					rowData.add(itemList.get(i).getItemCode()+" "+itemList.get(i).getItemDesc());
					for(int k=0;k<list.size();k++) {
						List<MonthItemWiseMrnReport> monthList=list.get(k).getItemWiseMonthList();
					
					for(int j=0;j<monthList.size();j++)
					{
						if(monthList.get(j).getItemId()==itemList.get(i).getItemId())
						{
							//rowData.add(""+monthList.get(j).getApproveQty());
							rowData.add(""+monthList.get(j).getApprovedQtyValue());
						}
					}
					
					}
				


					expoExcel.setRowData(rowData);
					exportToExcelList.add(expoExcel);
					}
				}

				HttpSession session = request.getSession();
				session.setAttribute("exportExcelList", exportToExcelList);
				session.setAttribute("excelName", "MrnItemMonthWiseList");
				 
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	@RequestMapping(value = "/mrnItemMonthWiseReportPdf/{typeName}/{catDesc}/{isDevName}/{deptName}/{subDeptName}", method = RequestMethod.GET)
	public void mrnItemMonthWiseReportPdf(@PathVariable String typeName, @PathVariable String catDesc
			,@PathVariable String isDevName,@PathVariable String deptName,@PathVariable String subDeptName,HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		BufferedOutputStream outStream = null;
		try {
		Document document = new Document(PageSize.A4.rotate(), 10f, 10f, 10f, 0f);
		DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
		String reportDate = DF.format(new Date());
        document.addHeader("Date: ", reportDate);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();
 
		System.out.println("time in Gen Bill PDF ==" + dateFormat.format(cal.getTime()));
		String timeStamp = dateFormat.format(cal.getTime());
		String FILE_PATH = Constants.REPORT_SAVE;
		File file = new File(FILE_PATH);

		PdfWriter writer = null;

		FileOutputStream out = new FileOutputStream(FILE_PATH);
		try {
			writer = PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
		
		float aprTotal=0;
		float mayTotal=0;
		float junTotal=0;
		float julTotal=0;
		float augTotal=0;
		float sepTotal=0;
		float octTotal=0;
		float novTotal=0;
		float decTotal=0;
		float janTotal=0;
		float febTotal=0;
		float marTotal=0;
	
		PdfPTable table = new PdfPTable(15);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] {0.4f, 3.0f, 0.8f,   0.8f,  0.8f,  0.8f,  0.8f,  0.8f,  0.8f,  0.8f,  0.8f,  0.8f,  0.8f,  0.8f,  0.8f});
			Font headFont = new Font(FontFamily.TIMES_ROMAN,8, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 11.0f, Font.UNDERLINE, BaseColor.BLUE);
			Font f1 = new Font(FontFamily.TIMES_ROMAN, 9.0f, Font.BOLD, BaseColor.DARK_GRAY);
			PdfPCell hcell = new PdfPCell();
  
			hcell.setPadding(4);
			hcell = new PdfPCell(new Phrase("SR.", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Item Name", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("APR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("MAY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("JUN", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("JUL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("AUG", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("SEP", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("OCT", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("NOV", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("DEC", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("JAN", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("FEB", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("MAR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("TOTAL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK); 
			table.addCell(hcell);
			
			 
			int index = 0;
			if(!mrnItemMonthWiseListForPdf.isEmpty()) {
					for (int k = 0; k < itemListforPdf.size(); k++) {
						 if(deptIdForPdf==itemListforPdf.get(k).getCatId()) {
							index++;
						
							PdfPCell cell;
							
							float mrnQtyValue=0;
							
							cell = new PdfPCell(new Phrase(""+index, headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPadding(3);
							table.addCell(cell);

						
							cell = new PdfPCell(new Phrase(itemListforPdf.get(k).getItemCode()+" "+itemListforPdf.get(k).getItemDesc(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							
							for (int j = 0; j < mrnItemMonthWiseListForPdf.size(); j++) {
								
								List<MonthItemWiseMrnReport> monthList=mrnItemMonthWiseListForPdf.get(j).getItemWiseMonthList();
								
								for(int l=0;l<monthList.size();l++)
								{
									if(monthList.get(l).getItemId()==itemListforPdf.get(k).getItemId())
									{
											 
											cell = new PdfPCell(new Phrase(""+monthList.get(l).getApprovedQtyValue(), headFont));
											cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
											cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
											cell.setPaddingRight(2);
											cell.setPadding(3);
											table.addCell(cell);
											
											mrnQtyValue=mrnQtyValue+monthList.get(l).getApprovedQtyValue();
											
											if(monthList.get(l).getMonthNo()==1) {
												 janTotal=janTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==2) {
												 febTotal=febTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==3) {
												 marTotal=marTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==4) {
												 aprTotal=aprTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==5) {
												 mayTotal=mayTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==6) {
												 junTotal=junTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==7) {
												 julTotal=julTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==8) {
												 augTotal=augTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==9) {
												 sepTotal=sepTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==10) {
												 octTotal=octTotal+monthList.get(l).getApprovedQtyValue();
											 }
											if(monthList.get(l).getMonthNo()==11) {
												 novTotal=novTotal+monthList.get(l).getApprovedQtyValue();
											 }
											 if(monthList.get(l).getMonthNo()==12) {
												 decTotal=decTotal+monthList.get(l).getApprovedQtyValue();
											 }
									}
								}
							}
							
							cell = new PdfPCell(new Phrase(""+df.format(mrnQtyValue), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPadding(3);
							table.addCell(cell);
					
					}
					}
			}
			
PdfPCell cell;
			
			cell = new PdfPCell(new Phrase("Total", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3);
			cell.setColspan(2);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(aprTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(mayTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(""+df.format(junTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(""+df.format(julTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(""+df.format(augTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(sepTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(octTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(novTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(decTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(janTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(febTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(""+df.format(marTotal), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			 
			
			cell = new PdfPCell(new Phrase(""+df.format((marTotal+aprTotal+mayTotal+junTotal+julTotal+augTotal+sepTotal+octTotal+novTotal+decTotal+febTotal+janTotal)), headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(3); 
			table.addCell(cell);
			
			document.open();
			Paragraph company = new Paragraph(companyInfo.getCompanyName()+"\n", f);
			company.setAlignment(Element.ALIGN_CENTER);
			document.add(company);
			
				Paragraph heading1 = new Paragraph(
						companyInfo.getFactoryAdd(),f1);
				heading1.setAlignment(Element.ALIGN_CENTER);
				document.add(heading1);
				Paragraph ex2=new Paragraph("\n");
				document.add(ex2);

				Paragraph headingDate=new Paragraph("Mrn Item Month Wise Report ",f1);
				headingDate.setAlignment(Element.ALIGN_CENTER);
			document.add(headingDate);
			
			Paragraph type=new Paragraph("Category: " + catDesc+", Type: "+typeName+", Department: "+deptName+", Sub-Dept: "+subDeptName,f1);
			type.setAlignment(Element.ALIGN_CENTER);
		document.add(type);
			
			Paragraph ex3=new Paragraph("\n");
			document.add(ex3);
			table.setHeaderRows(1);
			document.add(table);
			
		
			int totalPages = writer.getPageNumber();

			System.out.println("Page no " + totalPages);

			document.close();
			// Atul Sir code to open a Pdf File
			if (file != null) {

				String mimeType = URLConnection.guessContentTypeFromName(file.getName());

				if (mimeType == null) {

					mimeType = "application/pdf";

				}

				response.setContentType(mimeType);

				response.addHeader("content-disposition", String.format("inline; filename=\"%s\"", file.getName()));

				response.setContentLength((int) file.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				try {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				} catch (IOException e) {
					System.out.println("Excep in Opening a Pdf File");
					e.printStackTrace();
				}
			}

		} catch (DocumentException ex) {

			System.out.println("Pdf Generation Error" + ex.getMessage());

			ex.printStackTrace();

		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	List<IndentStatusReport> indentStatusReportListForPdf = new ArrayList<IndentStatusReport>();
	
	@RequestMapping(value = "/indentStatusReport", method = RequestMethod.GET)
	public ModelAndView indentStatusReport(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("valuationReport/indentStatusReport");
		try {
			 
			if(request.getParameter("fromDate")==null || request.getParameter("toDate")==null) {
				
				SimpleDateFormat yy = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy");
				Date date = new Date();
				  Calendar calendar = Calendar.getInstance();
				  calendar.setTime(date);
				   
				 
				 fromDate =  "01"+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR);
				 toDate = dd.format(date); 
				 
				 MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
					map.add("fromDate",DateConvertor.convertToYMD(fromDate));
		 			map.add("toDate",yy.format(date)); 
		 			IndentStatusReport[] indentStatusReport = rest.postForObject(Constants.url + "/indentStatusReport",map, IndentStatusReport[].class);
					List<IndentStatusReport> list = new ArrayList<IndentStatusReport>(Arrays.asList(indentStatusReport));
					
					model.addObject("indentStatusReport", list);
					model.addObject("fromDate", fromDate);
					model.addObject("toDate", dd.format(date));
					indentStatusReportListForPdf=list;
			}
			else {
				 
				 fromDate = request.getParameter("fromDate");
				 toDate = request.getParameter("toDate");
				
				 MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
					map.add("fromDate",DateConvertor.convertToYMD(fromDate));
		 			map.add("toDate",DateConvertor.convertToYMD(toDate));  
		 			
		 			IndentStatusReport[] indentStatusReport = rest.postForObject(Constants.url + "/indentStatusReport",map, IndentStatusReport[].class);
					List<IndentStatusReport> list = new ArrayList<IndentStatusReport>(Arrays.asList(indentStatusReport));
					
					model.addObject("indentStatusReport", list);
					model.addObject("fromDate", fromDate);
					model.addObject("toDate", toDate);
					indentStatusReportListForPdf=list;
			}
			
			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();
			
			ExportToExcel expoExcel = new ExportToExcel();
			List<String> rowData = new ArrayList<String>();
 
			rowData.add("FROM DATE-"); 
			rowData.add(fromDate); 
			rowData.add("TO DATE"); 
			rowData.add(toDate);

				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);
				
				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();
				
			rowData.add("SR. No");
			rowData.add("INDENT NO"); 
			rowData.add("INDENT DATE"); 
			rowData.add("ITEM DESC"); 
			rowData.add("INDENT QTY"); 
			rowData.add("SCH DATE"); 
			rowData.add("EXPRESS DAYS"); 
			rowData.add("REMARK");  
			
			expoExcel.setRowData(rowData); 
			exportToExcelList.add(expoExcel);
			
			int k=0;
			for (int i = 0; i < indentStatusReportListForPdf.size(); i++) {
				 
				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();
				k++;
				rowData.add((k)+"");
				rowData.add(indentStatusReportListForPdf.get(i).getIndMNo()); 
				rowData.add(""+indentStatusReportListForPdf.get(i).getIndMDate());
				rowData.add(""+indentStatusReportListForPdf.get(i).getItemCode());
				rowData.add(""+indentStatusReportListForPdf.get(i).getIndQty());
				rowData.add(""+indentStatusReportListForPdf.get(i).getIndItemSchddt()); 
				rowData.add(""+indentStatusReportListForPdf.get(i).getExcessDays());
				rowData.add(""+indentStatusReportListForPdf.get(i).getRemark());
				
				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);
				 
			}

			HttpSession session = request.getSession();
			session.setAttribute("exportExcelList", exportToExcelList);
			session.setAttribute("excelName", "indentSttatusList");
			
			 
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	
	@RequestMapping(value = "/indentStatusReportPDF", method = RequestMethod.GET)
	public void indentStatusReportPDF(HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		BufferedOutputStream outStream = null;
		try {
		Document document = new Document(PageSize.A4);
		DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
		String reportDate = DF.format(new Date());
        document.addHeader("Date: ", reportDate);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("time in Gen Bill PDF ==" + dateFormat.format(cal.getTime()));
		String timeStamp = dateFormat.format(cal.getTime());
		String FILE_PATH = Constants.REPORT_SAVE;
		File file = new File(FILE_PATH);

		PdfWriter writer = null;

		FileOutputStream out = new FileOutputStream(FILE_PATH);
		try {
			writer = PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
	
		PdfPTable table = new PdfPTable(8);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] {1.0f, 1.7f, 1.7f, 5.0f,1.7f,1.7f,1.7f,2.0f});
			Font headFont = new Font(FontFamily.TIMES_ROMAN,8, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 11.0f, Font.UNDERLINE, BaseColor.BLUE);
			Font f1 = new Font(FontFamily.TIMES_ROMAN, 9.0f, Font.BOLD, BaseColor.GRAY);

			PdfPCell hcell = new PdfPCell();
			
			hcell.setPadding(4);
			hcell = new PdfPCell(new Phrase("SR", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("INDENT NO", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("INDENT DATE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("ITEM DESC", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("INDENT QTY", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("SCH DATE", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("EXPRESS DAYS", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("REMARK", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			
			int index = 0;
			if(!indentStatusReportListForPdf.isEmpty()) {
					for (int k = 0; k < indentStatusReportListForPdf.size(); k++) {
                            
							index++;
						
							PdfPCell cell;
							
							cell = new PdfPCell(new Phrase(""+index, headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPadding(3);
							table.addCell(cell);

						
							cell = new PdfPCell(new Phrase(indentStatusReportListForPdf.get(k).getIndMNo(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
						
							cell = new PdfPCell(new Phrase(""+indentStatusReportListForPdf.get(k).getIndMDate(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							
							cell = new PdfPCell(new Phrase(""+indentStatusReportListForPdf.get(k).getItemCode(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							
							
							cell = new PdfPCell(new Phrase(""+indentStatusReportListForPdf.get(k).getIndQty(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							
							cell = new PdfPCell(new Phrase(""+indentStatusReportListForPdf.get(k).getIndItemSchddt(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							
							cell = new PdfPCell(new Phrase(""+indentStatusReportListForPdf.get(k).getExcessDays(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
							
							cell = new PdfPCell(new Phrase(""+indentStatusReportListForPdf.get(k).getRemark(), headFont));
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setPaddingRight(2);
							cell.setPadding(3);
							table.addCell(cell);
					
					}
			}
			
			document.open();
			Paragraph company = new Paragraph("Trambak Rubber Industries Limited\n", f);
			company.setAlignment(Element.ALIGN_CENTER);
			document.add(company);
			
				Paragraph heading1 = new Paragraph(
						"Address:  S. D. Aphale(General Manager) Flat No. 02, Maruti Building,\n Maharaj Nagar, Tagore Nagar NSK- 6, Nashik Road, Nashik - 422101, Maharashtra, India	",f1);
				heading1.setAlignment(Element.ALIGN_CENTER);
				document.add(heading1);
				Paragraph ex2=new Paragraph("\n");
				document.add(ex2);

				Paragraph headingDate=new Paragraph("Indent Status Report, From Date: " + fromDate+"  To Date: "+toDate+"",f1);
				headingDate.setAlignment(Element.ALIGN_CENTER);
				
			document.add(headingDate);
			
			Paragraph ex3=new Paragraph("\n");
			document.add(ex3);
			table.setHeaderRows(1);
			document.add(table);
			
		
			int totalPages = writer.getPageNumber();

			System.out.println("Page no " + totalPages);

			document.close();
			// Atul Sir code to open a Pdf File
			if (file != null) {

				String mimeType = URLConnection.guessContentTypeFromName(file.getName());

				if (mimeType == null) {

					mimeType = "application/pdf";

				}

				response.setContentType(mimeType);

				response.addHeader("content-disposition", String.format("inline; filename=\"%s\"", file.getName()));

				response.setContentLength((int) file.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				try {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				} catch (IOException e) {
					System.out.println("Excep in Opening a Pdf File");
					e.printStackTrace();
				}
			}

		} catch (DocumentException ex) {

			System.out.println("Pdf Generation Error" + ex.getMessage());

			ex.printStackTrace();

		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
