<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<jsp:include page="/WEB-INF/views/include/header.jsp"></jsp:include>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/tableSearch.css">
<body onload="getInvoiceNo()">
	<%-- <jsp:include page="/WEB-INF/views/include/logout.jsp"></jsp:include> --%>

	<c:url var="getSubDeptListByDeptId" value="/getSubDeptListByDeptId" />
	<c:url var="getgroupListByCatId" value="/getgroupListByCatId" />

	<c:url var="getIndentDetailForEdit" value="/getIndentDetailForEdit" />

	<c:url var="itemListByGroupId" value="/itemListByGroupId" />

	<c:url var="updateIndDetail" value="/updateIndDetail" />
	
	<c:url var="getInvoiceNo" value="/getInvoiceNo" />
<c:url var="getlimitationValue" value="/getlimitationValue" /> 
	<c:url var="getIndentValueLimit" value="/getIndentValueLimit" />
	<c:url var="getIndentPendingValueLimit" value="/getIndentPendingValueLimit" />
	<c:url var="getLastRate" value="/getLastRate" />

	<div class="container" id="main-container">

		<!-- BEGIN Sidebar -->
		<div id="sidebar" class="navbar-collapse collapse">

			<jsp:include page="/WEB-INF/views/include/navigation.jsp"></jsp:include>

			<div id="sidebar-collapse" class="visible-lg">
				<i class="fa fa-angle-double-left"></i>
			</div>
			<!-- END Sidebar Collapse Button -->
		</div>
		<!-- END Sidebar -->

		<!-- BEGIN Content -->
		<div id="main-content">
			<!-- BEGIN Page Title -->
			<!-- <div class="page-title">
				<div>
					<h1>
						<i class="fa fa-file-o"></i>Edit Indent
					</h1>

				</div>
			</div> -->
			<!-- END Page Title -->
			<!-- BEGIN Main Content -->
			<div class="row">
				<div class="col-md-12">
					<div class="box">
						<div class="box-title">
							<h3>
								<i class="fa fa-bars"></i>Approve Indent Detail : Approval ${apr}
							</h3>
							<div class="box-tool">
								<a href="${pageContext.request.contextPath}/getIndents">Back to List</a> <a data-action="collapse" href="${pageContext.request.contextPath}/getIndents"><i
									class="fa fa-chevron-up"></i></a>
							</div>

						</div>
<!-- ${pageContext.request.contextPath}/aprIndentProcess  -->
						<div class="box-content">
							<form
								
								method="post" class="form-horizontal" id="validation-form">

								<div class="form-group">
								
								
								<c:set var="indmtype" value="o"></c:set>
								
								<c:forEach items="${typeList}" var="typeList" >
								<c:choose>
										<c:when test="${indent.indMType==typeList.typeId}">
											<c:set var="indmtype" value="${typeList.typeName}"></c:set>
										</c:when>
										 
									</c:choose> 
												</c:forEach>
 
<div class="col-md-1"></div>
									<div class="col-md-2">Indent
										Type</div>
									<div class="col-md-3">
										<c:out value="${indmtype}"></c:out>
									</div>
									<input type="hidden" value="${indent.indMType}" name="indent_type" id="indent_type">
									
									<div class="col-md-2">Indent
										Category </div>
									<div class="col-md-3">
										<c:out value="${indent.catDesc}"></c:out>
										<input type="hidden" value="${indent.indMId}" name="indentId">
									</div>
									<input type="hidden" value="${indent.catId}" name="ind_cat" id="ind_cat">
								</div>
								<div class="form-group">
								
								<div class="col-md-1"></div>
									<div class="col-md-2">Indent
										No.</div>
									<div class="col-md-3">
										<c:out value="${indent.indMNo}"></c:out>
									</div>
									<div class="col-md-2">Date</div>
									<div class="col-md-3">
										<input class="form-control date-picker" id="indent_date"
											size="16" type="text" disabled="disabled" name="indent_date"
											value="${indent.indMDate}" required />
									</div>
								</div>
								<div class="form-group">

										 
										 		<div class="col-md-1"></div>
														<div class="col-md-2">Remark</div>
														<div class="col-md-8">
															${indent.indRemark}
														</div> 
														 
										 	 
								</div>
								 
								<div class="form-group">
								<div class="col-md-1"></div>
									<div class="col-md-2">Account
										Head</div>
									<div class="col-md-3">
										<%-- <select name="acc_head" id="acc_head" disabled
		 									class="form-control chosen" placeholder="Account Head"
											data-rule-required="true">
											<c:forEach items="${accountHeadList}" var="accHead"
												varStatus="count">
												<c:choose>
													<c:when test="${indent.achdId==accHead.accHeadId}">
														<option selected value="${accHead.accHeadId}"><c:out value="${accHead.accHeadDesc}"/></option>
													</c:when>
													
												</c:choose>
											</c:forEach>
										</select> --%>
										<c:out value="${indent.accHeadDesc}"></c:out>
									</div>

									<div class="col-md-2">Machine
										Specific</div>

									<div class="col-md-3">
										<select name="machine_specific" id="machine_specific"
											onchange="showDept()" class="form-control chosen" disabled
											placeholder="Is Machine Specific" data-rule-required="true">
											<c:choose>
												<c:when test="${indent.deptId==0}">
													<option selected value="0">No</option>
													<option value="1">Yes</option>
												</c:when>
												<c:otherwise>
													<option value="0">No</option>
													<option selected value="1">Yes</option>
												</c:otherwise>
											</c:choose>
										</select>
									</div>
								</div>
								<div class="form-group" style="display: none" id="deptDiv">
									<div class="col-md-1"></div>	<div class="col-md-2">Department
									</div>
									<div class="col-md-3">
										<select name="dept" id="dept" class="form-control chosen" disabled
											placeholder="Department">
											<c:forEach items="${deparmentList}" var="dept"
												varStatus="count">
												<c:choose>
													<c:when test="${indent.deptId==dept.deptId}">
														<option selected value="${dept.deptId}"><c:out value="${dept.deptDesc}"/></option>
													</c:when>
													<c:otherwise>
														<option value="${dept.deptId}"><c:out value="${dept.deptDesc}"/></option>
													</c:otherwise>
												</c:choose>
											</c:forEach>
										</select>
									</div>
									<div class="col-md-2">Sub
										Department </div>
									<div class="col-md-3">
										<select name="sub_dept" id="sub_dept" disabled
											class="form-control chosen" placeholder="Sub Department">
										</select>
									</div>
								</div>
								<div class="form-group"><div class="col-md-1"></div>
								<div class="col-md-2">For
										Development </div>
									<div class="col-md-3">
										<select name="is_dev" id="is_dev" class="form-control" disabled
											placeholder="Is Dev" data-rule-required="true">
											<c:choose>
												<c:when test="${indent.indIsdev==1}">
													<option selected value="1">Yes</option>
													<option value="0">No</option>
												</c:when>
												<c:otherwise>
													<option selected value="0">No</option>
													<option value="1">Yes</option>

												</c:otherwise>
											</c:choose>
										</select>
									</div>
									<div class="col-md-2"> For Monthly
									</div>
									<div class="col-md-3">
										<select name="is_monthly" id="is_monthly" class="form-control" disabled
											placeholder="Is Monthly" data-rule-required="true">
											<c:choose>
												<c:when test="${indent.indIsmonthly==1}">
													<option selected value="1">Yes</option>
													<option value="0">No</option>
												</c:when>
												<c:otherwise>
													<option value="1">Yes</option>

													<option selected value="0">No</option>

												</c:otherwise>
											</c:choose>
										</select>
									</div>
								</div>
								
								<div class="form-group">

										 
							<div class="col-md-1"></div>
									<div class="col-md-2">Department</div>
									<div class="col-md-3">
										<c:out value="${indent.deptCode}"></c:out>
									</div>
									 
									
									<div class="col-md-2">Sub-Department</div>
									<div class="col-md-3">
										<c:out value="${indent.subDeptCode}"></c:out>
										 
									</div>
									 
								</div>
								<br />
								<div class="form-group">

										 <c:choose>
										 	<c:when test="${apr==1}">
										 		<div class="col-md-1"></div>
														<div class="col-md-2">Reject Remark1</div>
														<div class="col-md-3">
															<input type="text" name="rejectRemark1" id="rejectRemark1"
																value="${indent.apprvRemark1}" class="form-control" placeholder="Reject Remark1"  
																 />
														</div> 
														<div class="col-md-2">Reject Remark2</div>
														<div class="col-md-3">
															<input type="text" name="rejectRemark2" id="rejectRemark2"
															value="${indent.apprvRemark2}"	class="form-control" placeholder="Reject Remark2" readonly="readonly"
																 /> 
														</div>
										 	
										 	</c:when>
										 	<c:otherwise> 
										 		<div class="col-md-1"></div>
														<div class="col-md-2">Reject Remark1</div>
														<div class="col-md-3">
															<input type="text" name="rejectRemark1" id="rejectRemark1"
																value="${indent.apprvRemark1}" class="form-control" placeholder="Reject Remark1"  readonly="readonly"
																 />
														</div> 
														<div class="col-md-2">Reject Remark2</div>
														<div class="col-md-3">
															<input type="text" name="rejectRemark2" id="rejectRemark2"
																value="${indent.apprvRemark2}" class="form-control" placeholder="Reject Remark2" 
																 /> 
														</div> 
										 	</c:otherwise>
										 </c:choose> 
									 
								</div>
								<br /> 
<hr/>
<div class="box-content">
								
								<div class="col-md-2">MRN Limit : 
									</div>
									
									<div class="col-md-2"  style="font-weight: bold; font-size: 15px;" id="mrnLimit"> 
									</div>
									<input type="hidden" name="mrnLimitText" id="mrnLimitText" />
									
									<div class="col-md-2">Total MRN : 
									</div>
									<div  class="col-md-2" style="font-weight: bold; font-size: 15px;" id="totalmrn">
 
									</div>
									<input type="hidden" name="totalmrnText" id="totalmrnText" />
									   
									
									<div class="col-md-2">Approved Indent Value : 
									</div>
									
									<div class="col-md-2"  style="font-weight: bold; font-size: 15px;" id="approvedIndentValue">
 
									</div>
									
									 <input type="hidden" name="approvedIndentValueText" id="approvedIndentValueText" />
								</div>
								<div class="box-content">
									 
									
									<div class="col-md-2"> 
									</div>
									
									<div class="col-md-2">
 
									</div>
									
									
									<div class="col-md-2">Total Indent Value : 
									</div>
									
									<div class="col-md-2"  style="font-weight: bold; font-size: 15px;" id="totalIndentValue">
 
									</div>
									<input type="hidden" name="totalIndentValueText" id="totalIndentValueText" />
									
									<div class="col-md-2">Non-Approved Indent Value : 
									</div>
									
									<div class="col-md-2"  style="font-weight: bold; font-size: 15px;" id="totalIndentPendingValue">
 
									</div>
									 <input type="hidden" name="totalIndentPendingValueText" id="totalIndentPendingValueText" />
								</div>
								<br><br>
								
								<h4> Items to Approve</h4>
								<div class="clearfix"></div>
								<div id="table-scroll" class="table-scroll">

									<div class="table-wrap">

										<table id="table1" class="table table-advance">
											<thead>
												<tr class="bgpink">
													<th  style="text-align: center; padding: 0px; align-items: center;"
														width="1%"><input type="checkbox" name="name1"
														value="0" /> &nbsp;&nbsp;&nbsp;All</th>
													<th width="2%" style="text-align: center;">Sr</th>
													<th width="5%" style="text-align: center;">Item
														Code</th>
													<th class="col-md-3" style="text-align: center;">Item
														Desc</th>
													<th width="5%" style="text-align: center;">UOM</th>
												
													<th class="col-md-1" style="text-align: center;">Indent
														Qty</th>
													<th class="col-md-1" style="text-align: center;">Cur Stock</th>
													<th class="col-md-1" style="text-align: center;">Sch Day</th>
													<th class="col-md-1" style="text-align: center;">Sch
														Date</th>
														<th class="col-md-1" style="text-align: left;">Remark</th>
														<th class="col-md-1" style="text-align: center;">Monthly Req</th>
													<th class="col-md-1" style="text-align: center;">Rate</th>
													<th class="col-md-1" style="text-align: center;">Value</th>
													<th class="col-md-1" style="text-align: center;">Reject remark1</th>
													<th class="col-md-1" style="text-align: center;">Reject remark2</th> 
														<!-- <th class="col-md-1" style="text-align: center;">Action
														</th> -->
												</tr>
											</thead>
											<tbody>
											<c:set var="thisIndentValue" value="0"></c:set>
											
												<c:forEach items="${indDetailList}" var="indDetail"
													varStatus="count">
<input type="hidden" name="apr" id="apr" value="${apr}"><input type="hidden" name="aprOrReject" id="aprOrReject" value="1"><input type="hidden" name="sts" id="sts" value="${indent.indMStatus}">
													<tr>
												
													<td  style="text-align: left; padding: 0px; align-items: center; align-content: center;"
															width="1%">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
															<c:choose>
															<c:when test="${apr==1}">
															<c:choose>
															<c:when test="${indDetail.indDStatus==7}">
															<input type="checkbox" checked 
															name="name1" value="${indDetail.indDId}" />
															</c:when>
															<c:otherwise>
															<input type="checkbox" 
															name="name1" value="${indDetail.indDId}" />
															</c:otherwise>
															</c:choose>
															</c:when>
															
																<c:when test="${apr==2}">
																<c:choose>
															<c:when test="${indDetail.indDStatus==7}">
															<input type="checkbox" checked 
															name="name1" value="${indDetail.indDId}" />
															</c:when>
															<c:otherwise>
															<input type="checkbox"  disabled
															name="name1" value="${indDetail.indDId}" />
															</c:otherwise>
															</c:choose>
															</c:when>
															</c:choose>
															
															</td>
														<td style="text-align: center;" width="2%"><c:out
																value="${count.index+1}" /></td>
																
																<td style="text-align: center;" width="5%"><c:out
																value="${indDetail.itemCode}" /></td>

														<td style="text-align: left;" class="col-md-3"><c:out
																value="${indDetail.itemDesc}" /></td>
																
																	
																<td style="text-align: center;" width="5%"><c:out
																value="${indDetail.itemUom}" /></td>
																

														<td style="text-align: center;" class="col-md-1"><%-- <input
															type="number" class="form-control" readonly
															value="${indDetail.indQty}" min="1"  
															onchange="(this.value,${indDetail.indDId},${indent.indMId})"
															id="indQty${indDetail.indDId}"
															name="indQty${indDetail.indDId}"> --%>
															<c:out value="${indDetail.indQty}"></c:out>
															</td>
															<td style="text-align: center;" class="col-md-1"><c:out value="${indDetail.indItemCurstk}"></c:out></td>
														<td style="text-align: center;" class="col-md-1"><c:out value="${indDetail.indItemSchd}"></c:out>
														<%-- <input type="number" readonly class="form-control"  id="indSchDays${indDetail.indDId}" name="indSchDays${indDetail.indDId}" value="${indDetail.indItemSchd}"  /> --%></td>

														<td style="text-align: center;" class="col-md-1"><c:out
																value="${indDetail.indItemSchddt}" /></td>

														<td style="text-align: left;" class="col-md-1"><c:out
																value="${indDetail.indRemark}" /></td>
														
															<c:forEach items="${itemList}" var="itemList">
															<c:choose>
																<c:when test="${itemList.itemId==indDetail.itemId}">
																<td style="text-align: center;" class="col-md-1"><c:out value="${itemList.itemMaxLevel}" /></td>	
																	<td style="text-align: center;" class="col-md-1"><c:out value="${itemList.itemClRate}" /></td>	
																	<td style="text-align: center;" class="col-md-1">
																	<fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2"
										value="${itemList.itemClRate*indDetail.indQty}" /> </td>
										
										<c:set var="thisIndentValue" value="${thisIndentValue+(itemList.itemClRate*indDetail.indQty)}"></c:set>
																</c:when>
															</c:choose>
															</c:forEach>
															
															<c:set var="rejRem1" value=""></c:set>
															<c:set var="rejRem2" value=""></c:set>
								
															<c:choose>
																<c:when test="${indDetail.apprvRemark1==null || indDetail.apprvRemark1==''}">
																	<c:set var="rejRem1" value="-"></c:set>
																</c:when>
																<c:otherwise>
																	<c:set var="rejRem1" value="${indDetail.apprvRemark1}"></c:set>
																</c:otherwise>
															</c:choose>
															
															<c:choose>
																<c:when test="${indDetail.apprvRemark2==null || indDetail.apprvRemark2==''}">
																	<c:set var="rejRem2" value="-"></c:set>
																</c:when>
																<c:otherwise>
																	<c:set var="rejRem2" value="${indDetail.apprvRemark2}"></c:set>
																</c:otherwise>
															</c:choose>
								
													<td style="text-align: left;" class="col-md-1"> 
														 <c:choose>
										 					<c:when test="${apr==1}">
																<input
																		type="text" class="form-control" 
																		id="apprvRemark1${indDetail.indDId}"
																	 value="${rejRem1}" name="apprvRemark1${indDetail.indDId}">
															</c:when>
															<c:otherwise>
															<input
																		type="text" class="form-control" 
																		id="apprvRemark1${indDetail.indDId}"
																		value="${rejRem1}" name="apprvRemark1${indDetail.indDId}" readonly>
															</c:otherwise>
															</c:choose>
													
													</td>
													
													<td style="text-align: left;" class="col-md-1"> 
													 <c:choose>
										 					<c:when test="${apr==1}">
															<input
																	type="text" class="form-control" 
																	id="apprvRemark2${indDetail.indDId}"
																	value="${rejRem2}" name="apprvRemark2${indDetail.indDId}" readonly>
															</c:when>
															<c:otherwise>
															<input
																	type="text" class="form-control" 
																	id="apprvRemark2${indDetail.indDId}"
																	value="${rejRem2}" name="apprvRemark2${indDetail.indDId}">
															</c:otherwise>
															</c:choose>
													
													</td>
															
<%-- 
														<td style="text-align: center;" class="col-md-1">
															<input
															type="button" value="update"
															onclick="updateCall(${indDetail.indDId},${indent.indMId})">

															<c:choose>
																<c:when test="${indDetail.indDStatus==0}">

																	<a href="#" class="action_btn" title="Update" 
																		onclick="updateCall(${indDetail.indDId},${indent.indMId},0)"><i id="updateButton${indDetail.indDId}"
																		class="fa fa-edit"></i></a> &nbsp;&nbsp;<a href="#"
																		class="action_btn" title="Delete"
																		onclick="updateCall(${indDetail.indDId},${indent.indMId},1)"><i
																		class="fa fa-trash-o"></i></a>
																</c:when>
															</c:choose>
														</td> --%>
													</tr>
												</c:forEach>
												<tr>
												
													<td colspan="12"><c:out
																value="Total" /></td>
													<td class="col-md-1"><fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2"
										value="${thisIndentValue}" /> </td>
													<td colspan="2"><c:out
																value="-" /></td>
												</tr>
											</tbody>
										</table>
									</div>
								</div>

								<div class="row">
									<div class="col-md-12" style="text-align: center">
									
												<input type="button" class="btn btn-info"
													value="Submit" onclick="callApproveIndent(${apr})">
													
													<c:choose>
														<c:when test="${(indent.indMStatus==9 || indent.indMStatus==6) && (apr==1)}">
														<input type="button" class="btn btn-info"
													value="Reject" onclick="rejectIntent(${indent.indMStatus},${apr},${indent.indMId})">
														</c:when>
														<c:when test="${apr==2 && indent.indMStatus==7 }">
														<input type="button" class="btn btn-info"
													value="Reject" onclick="rejectIntent(${indent.indMStatus},${apr},${indent.indMId})">
														</c:when>
													</c:choose>

									</div>
								</div>

							</form>

						</div>
					</div>
				</div>
			</div>
		</div>
		<footer>
			<p>2018 © TRAMBAK RUBBER</p>
		</footer>
	</div>
	<!-- END Main Content -->


	<a id="btn-scrollup" class="btn btn-circle btn-lg" href="#"><i
		class="fa fa-chevron-up"></i></a>
	</div>
	<!-- END Content -->
	<!-- END Container -->

	<!--basic scripts-->
	<script
		src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
	<script>
		window.jQuery
				|| document
						.write('<script src="${pageContext.request.contextPath}/resources/assets/jquery/jquery-2.0.3.min.js"><\/script>')
	</script>
	<script
		src="${pageContext.request.contextPath}/resources/assets/bootstrap/js/bootstrap.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/assets/jquery-slimscroll/jquery.slimscroll.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/assets/jquery-cookie/jquery.cookie.js"></script>

	<!--page specific plugin scripts-->
	<script
		src="${pageContext.request.contextPath}/resources/assets/flot/jquery.flot.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/assets/flot/jquery.flot.resize.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/assets/flot/jquery.flot.pie.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/assets/flot/jquery.flot.stack.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/assets/flot/jquery.flot.crosshair.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/assets/flot/jquery.flot.tooltip.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/assets/sparkline/jquery.sparkline.min.js"></script>


	<!--page specific plugin scripts-->
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/jquery-validation/dist/jquery.validate.min.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/jquery-validation/dist/additional-methods.min.js"></script>





	<!--flaty scripts-->
	<script src="${pageContext.request.contextPath}/resources/js/flaty.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/js/flaty-demo-codes.js"></script>
	<!--page specific plugin scripts-->
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-fileupload/bootstrap-fileupload.min.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/chosen-bootstrap/chosen.jquery.min.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/clockface/js/clockface.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-timepicker/js/bootstrap-timepicker.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-colorpicker/js/bootstrap-colorpicker.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-daterangepicker/date.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-daterangepicker/daterangepicker.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/js/common.js"></script>

	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/jquery-validation/dist/jquery.validate.min.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/jquery-validation/dist/additional-methods.min.js"></script>
		<script type="text/javascript">

function getInvoiceNo() {
	
	var date = $("#indent_date").val(); 
	var catId = $("#ind_cat").val(); 
	var typeId = $("#indent_type").val(); 
	
	$.getJSON('${getInvoiceNo}', {

		catId:catId,
		docId:1,
		date : date,
		typeId : typeId,
		ajax : 'true',

	}, function(data) { 
		 
	 
	document.getElementById("mrnLimit").innerHTML = data.subDocument.categoryPostfix;
	document.getElementById("mrnLimitText").value = data.subDocument.categoryPostfix;;
	getlimitationValue(catId,typeId);
	getIndentValueLimit(catId,typeId);
	
	});

}

function getlimitationValue(catId,typeId) {
	 
	$.getJSON('${getlimitationValue}', {
 
		ajax : 'true',

	}, function(data) { 
		
		var flag=0;
		
	for(var i=0;i<data.length;i++){
		
		if(data[i].typeId==typeId){
			
			for(var j=0;j<data[i].consumptionReportList.length;j++){
				
				if(data[i].consumptionReportList[j].catId==catId){
					
					//alert("Monthly Value Is " + data[i].consumptionReportList[j].monthlyValue);
					document.getElementById("totalmrn").innerHTML = data[i].consumptionReportList[j].monthlyValue;
					document.getElementById("totalmrnText").value = data[i].consumptionReportList[j].monthlyValue;
					flag=1;
					break;
				}
				 
			}
			 
		}
	}
	
	});

}

function getIndentValueLimit(catId,typeId) {
	 
	$.getJSON('${getIndentValueLimit}', {
 
		catId:catId,  
		typeId : typeId,
		ajax : 'true',

	}, function(data) { 
		 
		document.getElementById("approvedIndentValue").innerHTML = data;
		document.getElementById("approvedIndentValueText").value = data;
		getIndentPeningValueLimit(catId,typeId);
	});

}

function getIndentPeningValueLimit(catId,typeId) {
	 
	$.getJSON('${getIndentPendingValueLimit}', {
 
		catId:catId,  
		typeId : typeId,
		ajax : 'true',

	}, function(data) {  
		 
		document.getElementById("totalIndentPendingValue").innerHTML = data;
		document.getElementById("totalIndentPendingValueText").value = data;
		
		var approvedIndentValueText = parseFloat($("#approvedIndentValueText").val());
		
		document.getElementById("totalIndentValue").innerHTML = parseFloat(approvedIndentValueText+data);
		document.getElementById("totalIndentValueText").value = parseFloat(approvedIndentValueText+data);
	});

}

function getLastRate(qty,flag) {
	 
	var itemId = $("#item_name").val();
	var totalIndentValueText = parseFloat($("#totalIndentValueText").val());
	$.getJSON('${getLastRate}', {
  
		itemId : itemId,
		flag : flag,
		qty : qty,
		totalIndentValueText : totalIndentValueText,
		ajax : 'true',

	}, function(data) {  
		   
			document.getElementById("totalIndentValue").innerHTML = data;
			document.getElementById("totalIndentValueText").value = data;
		  
	});

}

</script>
	<script type="text/javascript">
		function callApproveIndent(apr) {
			
			if(apr==2){
				var x=confirm("This is Final Approval: You can not change anything further");
				
				if(x==true)
			{
					document.getElementById("aprOrReject").value = 1;
					
			var form = document.getElementById("validation-form");
			form.action = "${pageContext.request.contextPath}/aprIndentProcess";
			form.submit();
			}
			}
			else{
				document.getElementById("aprOrReject").value = 1;
				var form = document.getElementById("validation-form");
				form.action = "${pageContext.request.contextPath}/aprIndentProcess";
				form.submit();
			}
				
				

		}
		
function rejectIntent(sts,apr,indId) {
			
	 
	var checkboxs=document.getElementsByName("name1");
    var okay=true;
    if(apr==1){
    	
    	for(var i=1;i<checkboxs.length;i++)
        {
        	var x = checkboxs[i].value; 
        	var re = document.getElementById("apprvRemark1"+x).value; 
            if(document.getElementById("apprvRemark1"+checkboxs[i].value).value=="" || document.getElementById("apprvRemark1"+checkboxs[i].value).value==null)
            {
                okay=false;
                break;
            }
             
        }
    	var rejectRemark1=document.getElementById("rejectRemark1").value ;
    	if(rejectRemark1=="" || rejectRemark1==null){
    		alert("Enter Reject Remark1")
    	}
    	else{
    		
    		if(okay==true){
    			var sub=confirm("You Want To Reject Indent ");
    			
    			if(sub==true)
    			{
    				document.getElementById("aprOrReject").value = 2;
    	        	var form = document.getElementById("validation-form");
    				form.action = "${pageContext.request.contextPath}/aprIndentProcess";
    				form.submit();
    			}
            }
            else{
            	alert("Fill All Reject Remark 1 ");
            }
    	}
        
        
    }else{
    	
    	for(var i=1;i<checkboxs.length;i++)
        {
        	var x = checkboxs[i].value;
        	 
        	var re = document.getElementById("apprvRemark2"+x).value;
        	  
            if(document.getElementById("apprvRemark2"+checkboxs[i].value).value=="" || document.getElementById("apprvRemark2"+checkboxs[i].value).value==null)
            {
                okay=false;
                break;
            }
             
        }
    	var rejectRemark2=document.getElementById("rejectRemark2").value ;
    	if(rejectRemark2=="" || rejectRemark2==null){
    		alert("Enter Reject Remark2")
    	}
    	
    	else{
    		
	        if(okay==true){
	        	var sub=confirm("You Want To Reject Indent ");
				
				if(sub==true)
				{
					document.getElementById("aprOrReject").value = 2;
		        	var form = document.getElementById("validation-form");
					form.action = "${pageContext.request.contextPath}/aprIndentProcess";
					form.submit();
				}
	        }
	        else{
	        	alert("Fill All Reject Remark 2 ");
	        }
    	}
    }
    
			 
				/* var x=confirm("You Want To Reject Indent ");
				
			if(x==true)
			{
				document.getElementById("aprOrReject").value = 2;
				location.href='${pageContext.request.contextPath}/rejectIndent/'
					+ sts+'/'+apr+'/'+indId;  
			} */
			 
				
				

		}
	</script>
	
	<!-- 1 -->
	<script>
		function myFunction() {
			var input, filter, table, tr, td, i;
			input = document.getElementById("myInput");
			filter = input.value.toUpperCase();
			table = document.getElementById("table1");
			tr = table.getElementsByTagName("tr");
			for (i = 0; i < tr.length; i++) {
				td = tr[i].getElementsByTagName("td")[1];
				if (td) {
					if (td.innerHTML.toUpperCase().indexOf(filter) > -1) {
						tr[i].style.display = "";
					} else {
						tr[i].style.display = "none";
					}
				}
			}
		}
	</script>
	
	

</body>
</html>

