<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<style>
body {
	font-family: Arial, Helvetica, sans-serif;
}

/* The Modal (background) */
.modal {
	display: none; /* Hidden by default */
	position: fixed; /* Stay in place */
	z-index: 1; /* Sit on top */
	padding-top: 20px; /* Location of the box */
	left: 0;
	top: 0;
	width: 100%; /* Full width */
	height: 100%; /* Full height */
	overflow: auto; /* Enable scroll if needed */
	background-color: rgb(0, 0, 0); /* Fallback color */
	background-color: rgba(0, 0, 0, 0.4); /* Black w/ opacity */
}

/* Modal Content */
.modal-content {
	background-color: #fefefe;
	margin: auto;
	padding: 20px;
	border: 1px solid #888;
	width: 100%;
	height: 100%;
}

/* The Close Button */
.close {
	color: #aaaaaa;
	float: right;
	font-size: 28px;
	font-weight: bold;
}

.close:hover, .close:focus {
	color: #000;
	text-decoration: none;
	cursor: pointer;
}

#overlay {
	position: fixed;
	display: none;
	width: 100%;
	height: 100%;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: rgba(101, 113, 119, 0.5);
	z-index: 2;
	cursor: pointer;
}

#text {
	position: absolute;
	top: 50%;
	left: 50%;
	font-size: 25px;
	color: white;
	transform: translate(-50%, -50%);
	-ms-transform: translate(-50%, -50%);
}

.bg-overlay {
	background: linear-gradient(rgba(0, 0, 0, .7), rgba(0, 0, 0, .7)),
		url("${pageContext.request.contextPath}/resources/images/smart.jpeg");
	background-repeat: no-repeat;
	background-size: cover;
	background-position: center center;
	color: #fff;
	height: auto;
	width: auto;
	padding-top: 10px;
	padding-left: 20px;
}
</style>
<jsp:include page="/WEB-INF/views/include/header.jsp"></jsp:include>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/tableSearch.css">
<body>
	<%-- <jsp:include page="/WEB-INF/views/include/logout.jsp"></jsp:include> --%>

	<c:url var="addItemFromItemListInIndent"
		value="/addItemFromItemListInIndent" />
	<c:url var="getItemFroItemListBelowROL"
		value="/getItemFroItemListBelowROL" />

	<c:url var="getgroupListByCatId" value="/getgroupListByCatId" />
	<c:url var="exportExcelforIndent" value="/exportExcelforIndent" />
	<c:url var="getIndentDetail" value="/getIndentDetail" />
	<c:url var="getInvoiceNo" value="/getInvoiceNo" />
	<c:url var="getlimitationValue" value="/getlimitationValue" />
	<c:url var="itemListByGroupId" value="/itemListByGroupId" />
	<c:url var="getIndentValueLimit" value="/getIndentValueLimit" />
	<c:url var="getIndentPendingValueLimit"
		value="/getIndentPendingValueLimit" />
	<c:url var="getLastRate" value="/getLastRate" />
	<c:url var="getMoqQtyForValidation" value="/getMoqQtyForValidation" />
	<c:url var="getItemcategorywise" value="/getItemcategorywise" />
	<c:url var="deleteItemFromOplist" value="/deleteItemFromOplist" />
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

			<!-- END Page Title -->
			<!-- BEGIN Main Content -->
			<div class="row">
				<div class="col-md-12">
					<div class="box">
						<div class="box-title">
							<h3>
								<i class="fa fa-bars"></i>Add Opening Stock
							</h3>


						</div>


						<div class="box-content">
							<form method="post" class="form-horizontal"
								action="${pageContext.request.contextPath}/insertIndentPoMrn">

								<div class="box-content">

									<label class="col-md-2">Indent Type</label>
									<div class="col-md-3">
										<select name="indent_type" onchange="getInvoiceNo()"
											id="indent_type" data-rule-required="true"
											class="form-control chosen" data-rule-required="true">
											<option value="">Select Indent Type</option>
											<c:forEach items="${typeList}" var="typeList">
												<c:choose>
													<c:when test="${typeList.typeId==indentTypeTemp}">
														<option value="${typeList.typeId}" selected>${typeList.typeName}</option>
													</c:when>
													<c:otherwise>
														<option value="${typeList.typeId}">${typeList.typeName}</option>
													</c:otherwise>
												</c:choose>

											</c:forEach>
										</select>
									</div>
									<div class="col-md-1"></div>
									<label class="col-md-2">Indent Category </label>
									<div class="col-md-3">
										<c:choose>
											<c:when test="${isSubmit==1}">

												<input type="hidden" name="catId" id="catId"
													value="${catIdTemp}" />
												<select id="ind_cat" name="ind_cat"
													class="form-control chosen" placeholder="Indent Category"
													onchange="getInvoiceNo()" disabled>
													<option value="">Select Indent Category</option>
													<c:forEach items="${categoryList}" var="cat"
														varStatus="count">
														<c:choose>
															<c:when test="${catIdTemp==cat.catId}">
																<option value="${cat.catId}" selected><c:out
																		value="${cat.catDesc}" /></option>
															</c:when>
															<c:otherwise>
																<option value="${cat.catId}"><c:out
																		value="${cat.catDesc}" /></option>
															</c:otherwise>
														</c:choose>

													</c:forEach>
												</select>
											</c:when>
											<c:otherwise>
												<input type="hidden" name="catId" id="catId" value="0" />
												<select id="ind_cat" name="ind_cat"
													class="form-control chosen" placeholder="Indent Category"
													onchange="getInvoiceNo()" required>
													<option value="">Select Indent Category</option>
													<c:forEach items="${categoryList}" var="cat"
														varStatus="count">
														<option value="${cat.catId}"><c:out
																value="${cat.catDesc}" /></option>
													</c:forEach>
												</select>
											</c:otherwise>
										</c:choose>

									</div>


								</div>
								<br> <br>

								<div class="box-content">
									<label class="col-md-2">Indent No.</label>
									<div class="col-md-3">
										<input type="text" name="indent_no" id="indent_no"
											class="form-control" placeholder="Indent No"
											value="${indentNoTemp}" readonly="readonly" />
									</div>
									<div class="col-md-1"></div>
									<label class="col-md-2">Date</label>
									<div class="col-md-3">
										<input class="form-control date-picker" id="indent_date"
											onblur="getInvoiceNo()" type="text" name="indent_date"
											value="${date}" required data-rule-required="true" />
									</div>

								</div>
								<br> <br> <input id="machine_specific" type="hidden"
									name="machine_specific" value="1" /> <input id="acc_head"
									type="hidden" name="acc_head" value="1" />

								<div class="box-content">

									<label class="col-md-2">Select Vendor</label>
									<div class="col-md-3">
										<select name="Vendorlist" id="Vendorlist"
											class="form-control chosen" required>
											<option value="">Select Vendor</option>
											<c:forEach items="${vendorList}" var="vendorList">


												<option value="${vendorList.vendorId}">${vendorList.vendorCode}
													--- ${vendorList.vendorName}</option>



											</c:forEach>
										</select>
									</div>

									<div class="col-md-1"></div>
									<label class="col-md-2">Select</label>
									<div class="col-md-3">
										<select name="indpomrn" id="indpomrn"
											class="form-control chosen" required>

											<option value="3" selected>MRN</option>
											<option value="2">PO</option>
											<option value="1">INDENT</option>

										</select>
									</div>
									<br> <br /> <input id="is_dev" type="hidden"
										name="is_dev" value="0" /> <input id="is_monthly"
										type="hidden" name="is_monthly" value="0" /> <input id="dept"
										type="hidden" name="dept" value="1" /> <input id="sub_dept"
										type="hidden" name="sub_dept" value="1" />
								</div>
								<div class="row">
									<div class="col-md-12" style="text-align: center">
										<input class="btn btn-info" onclick="getItemcategorywise()"
											value="Search" type="button" /> <input class="btn btn-info"
											onclick="getResetTable()" value="Reset" type="button" />

									</div>
								</div>

								<div class="box-content">
									<div class="table-wrap">

										<table id="table1" class="table table-advance"
											style="font-size: 14px">
											<thead>
												<tr class="bgpink">
													<th width="2%">Sr</th>
													<th class="col-md-1">Item Code</th>
													<th class="col-md-3">Item Desc</th>
													<th class="col-md-1">UOM</th>


													<th class="col-md-1">Indent Qty</th>
													<th class="col-md-1">Rate</th>
													<th class="col-md-1">Tax%</th>
													<th class="col-md-1">Taxable Amt</th>
													<th class="col-md-1">Tax Amt</th>
													<th class="col-md-1">Total Amt</th>
													<th class="col-md-1">Action</th>

												</tr>
											</thead>
											<tbody>


											</tbody>
										</table>
									</div>
								</div>
								<div class="row">
									<div class="col-md-12" style="text-align: center">

										<c:choose>
											<c:when test="${tempIndentList.size()>0}">
												<input type="submit" id="submit" class="btn btn-info"
													value="Submit">
											</c:when>
											<c:otherwise>
												<input type="submit" id="submitt" class="btn btn-info"
													value="Submit" disabled>
											</c:otherwise>
										</c:choose>

										<%-- <c:choose>
											<c:when test="${userInfo.id==1}">
												<input type="button" class="btn btn-info"
													value="Import Excel " onclick="exportExcel()">
											</c:when>
										</c:choose> --%>


									</div>
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
			<footer>
				<p>2019 © MONGINIS</p>
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
		function insertIndent() {
			//alert("inside Indent Insetr");
			var form = document.getElementById("validation-form");
			var indentDate = $('#indent_date').val();
			var indNo = $('#indent_no').val();
			var indentType = $('#indent_type').val();
			var accHead = $('#acc_head').val();
			var dept = $('#dept').val();
			var subDept = $('#sub_dept').val();

			//alert(indentDate);
			if (indentDate == "" || indentDate == null) {
				alert("Please Select Valid Indent Date");
			} else if (indNo == "" || indNo == null) {
				alert("Please provide Indent No");
			} else if (indentType == "" || indentType == null) {
				alert("Select PoType ");
			} else if (accHead == "" || accHead == null) {
				alert("Select Account Head  ");
			} else if (dept == "" || dept == null) {
				alert("Select Department  ");
			} else if (subDept == "" || subDept == null) {
				alert("Select Sub Department  ");
			} else {

				if (confirm("Do you really want Submit Indent ?")) {
					form.action = "${pageContext.request.contextPath}/saveIndent";
					form.submit();
				}

			}
		}
	</script>
	<script type="text/javascript">
		function showDept() {
			var mac_spec = document.getElementById("machine_specific").value;
			//alert("Machine Specific "+mac_spec);
			if (mac_spec == 1) {
				document.getElementById('deptDiv').style.display = "block";
			}
			if (mac_spec == 0) {
				document.getElementById('deptDiv').style.display = "none";
			}
		}
	</script>
	<script type="text/javascript">
		$(document)
				.ready(
						function() {

							$('#dept')
									.change(
											function() {

												$
														.getJSON(
																'${getSubDeptListByDeptId}',
																{
																	deptId : $(
																			this)
																			.val(),
																	ajax : 'true'
																},
																function(data) {

																	var len = data.length;

																	$(
																			'#sub_dept')
																			.find(
																					'option')
																			.remove()
																			.end()
																	// $("#items").append($("<option></option>").attr( "value",-1).text("ALL"));
																	for (var i = 0; i < len; i++) {

																		$(
																				"#sub_dept")
																				.append(
																						$(
																								"<option></option>")
																								.attr(
																										"value",
																										data[i].subDeptId)
																								.text(
																										data[i].subDeptCode
																												+ " "
																												+ data[i].subDeptDesc));
																	}

																	$(
																			"#sub_dept")
																			.trigger(
																					"chosen:updated");
																});
											});
						});
	</script>

	<script type="text/javascript">
		$(document)
				.ready(
						function() {

							$('#group')
									.change(
											function() {

												$
														.getJSON(
																'${itemListByGroupId}',
																{
																	grpId : $(
																			this)
																			.val(),
																	ajax : 'true'
																},
																function(data) {

																	var len = data.length;

																	$(
																			'#item_name')
																			.find(
																					'option')
																			.remove()
																			.end()
																	// $("#items").append($("<option></option>").attr( "value",-1).text("ALL"));
																	for (var i = 0; i < len; i++) {

																		$(
																				"#item_name")
																				.append(
																						$(
																								"<option></option>")
																								.attr(
																										"value",
																										data[i].itemId)
																								.text(
																										data[i].itemDesc));
																	}

																	$(
																			"#item_name")
																			.trigger(
																					"chosen:updated");
																});
											});
						});
	</script>



	<script type="text/javascript">
		/* $(document).ready(function() {
		
		 $('#ind_cat').change(
		 function() {
		
		 $.getJSON('${getgroupListByCatId}', {
		 catId : $(this).val(),
		 ajax : 'true'
		 }, function(data) {
		
		 var len = data.length;

		 $('#group')
		 .find('option')
		 .remove()
		 .end()
		 // $("#items").append($("<option></option>").attr( "value",-1).text("ALL"));
		 var html = '<option value="">Select group</option>';
		 html += '</option>';
		 $('#group').html(html);
		 for ( var i = 0; i < len; i++) {
		
		
		 $("#group").append(
		 $("<option></option>").attr(
		 "value", data[i].grpId).text(data[i].grpCode+' '+data[i].grpDesc)
		 );
		 }

		 $("#group").trigger("chosen:updated");
		
		 var html = '<option value="" selected >Select Item</option>';
		 html += '</option>';
		 $('#item_name').html(html);
		 $("#item_name").trigger("chosen:updated");
		 });
		 });
		 }); */

		$(document)
				.ready(
						function() {

							$('#ind_cat')
									.change(
											function() {

												$
														.getJSON(
																'${getgroupListByCatId}',
																{
																	catId : $(
																			this)
																			.val(),
																	ajax : 'true'
																},
																function(data) {

																	var len = data.length;

																	$(
																			'#item_name')
																			.find(
																					'option')
																			.remove()
																			.end()
																	// $("#items").append($("<option></option>").attr( "value",-1).text("ALL"));
																	var html = '<option value="">Select Item</option>';
																	html += '</option>';
																	$(
																			'#item_name')
																			.html(
																					html);
																	for (var i = 0; i < len; i++) {

																		if (data[i].itemIsCons == 0) {
																			$(
																					"#item_name")
																					.append(
																							$(
																									"<option></option>")
																									.attr(
																											"value",
																											data[i].itemId)
																									.text(
																											data[i].itemCode
																													+ ' '
																													+ data[i].itemDesc));
																		}
																	}

																	$(
																			"#item_name")
																			.trigger(
																					"chosen:updated");

																	/* var html = '<option value="" selected >Select Item</option>';
																	html += '</option>';
																	$('#item_name').html(html);
																	$("#item_name").trigger("chosen:updated"); */
																});
											});
						});
	</script>

	<script type="text/javascript">
		function insertIndentDetail() {

			var itemId = $('#item_name').val();
			var qty = $('#quantity').val();
			var remark = $('#remark').val();
			var schDay = $('#sch_days').val();
			var itemName = $("#item_name option:selected").html();
			var catId = $('#ind_cat').val();
			var indentDate = $('#indent_date').val();

			/* var moqQty = parseFloat(document.getElementById("moqQtyByItemId").value);
			
			
			var rem=qty%moqQty; */

			/* if(rem!=0){
			 alert("Enter Multiple of "+ moqQty +" Qty ");
			 document.getElementById("qty"+key).value=moqQty; 
			} */

			if (qty != 0 && (itemId != "" || itemId != null) && schDay != "") {
				$
						.getJSON(
								'${getIndentDetail}',
								{
									itemId : itemId,
									qty : qty,
									remark : remark,
									itemName : itemName,
									schDay : schDay,
									indentDate : indentDate,
									key : -1,
									ajax : 'true',

								},
								function(data) {
									//alert(data);

									var len = data.length;
									$('#table1 td').remove();
									$
											.each(
													data,
													function(key, trans) {
														if (trans.isDuplicate == 1) {
															alert("Item Already Added in Indent");
														}

														var tr = $('<tr></tr>');
														tr.append($(
																'<td   ></td>')
																.html(key + 1));
														tr
																.append($(
																		'<td   ></td>')
																		.html(
																				trans.itemCode));
														tr
																.append($(
																		'<td   ></td>')
																		.html(
																				trans.itemName));
														tr
																.append($(
																		'<td   ></td>')
																		.html(
																				trans.uom));
														tr
																.append($(
																		'<td   ></td>')
																		.html(
																				trans.curStock));

														tr
																.append($(
																		'<td   ></td>')
																		.html(
																				trans.qty));
														tr
																.append($(
																		'<td   ></td>')
																		.html(
																				trans.poPending));
														tr
																.append($(
																		'<td   ></td>')
																		.html(
																				(trans.avgIssueQty)
																						.toFixed(2)));
														tr
																.append($(
																		'<td   ></td>')
																		.html(
																				trans.moqQty));
														tr
																.append($(
																		'<td   ></td>')
																		.html(
																				trans.date));

														/* tr
														.append($(
															'<td class="col-md-1" style="text-align: center;"></td>')
															.html(
																	"<input type=button style='text-align:center; width:40px' class=form-control name=delete_indent_item"
																			+ trans.itemId+ "id=delete_indent_item"
																			+ trans.itemId
																			+ " onclick='deleteIndentItem("+trans.itemId+","+key+")'  />"));
														 */

														tr
																.append($(
																		'<td class="col-md-1" style="text-align: center;"></td>')
																		.html(
																				"<a href='#' class='action_btn'onclick=deleteIndentItem("
																						+ trans.itemId
																						+ ","
																						+ key
																						+ ")><abbr title='Delete'><i class='fa fa-trash-o  fa-lg'></i></abbr></a>"));

														$('#table1 tbody')
																.append(tr);
														//document.getElementById("ind_cat").disabled=true;
														$('#ind_cat')
																.prop(
																		'disabled',
																		true)
																.trigger(
																		"chosen:updated");

														document
																.getElementById("catId").value = catId;
														document
																.getElementById("submitt").disabled = false;

													})

									//getLastRate(qty,1);
								});
				document.getElementById("quantity").value = "0";
				document.getElementById("remark").value = "";
				//document.getElementById("item_name").selectedIndex = "0";
				document.getElementById("sch_days").value = "";

				$("#group").focus();
				//document.getElementById("rm_cat").selectedIndex = "0";  
			} else {
				/*  if(rem!=0){
					 alert("Enter Multiple of "+ moqQty +" Qty ");
					 
				 }
				 else{ */

				alert("Please Enter  valid Infromation");
				//}

			}
		}
	</script>
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

	<script type="text/javascript">
		function deleteIndentItem(key) {

			// var itemId=$('#item_name').val();

			$
					.getJSON(
							'${deleteItemFromOplist}',
							{

								key : key,
								ajax : 'true',

							},
							function(data) {

								//alert(data);
								if (data == "") {
									alert("No records found !!");
									$('#ind_cat').prop('disabled', false)
											.trigger("chosen:updated");
									document.getElementById("submitt").disabled = true;

								}

								$('#table1 td').remove();
								$
										.each(
												data,
												function(key, data) {

													try {

														var tr = $('<tr></tr>');

														tr
																.append($(
																		'<td class="col-sm-1" ></td>')
																		.html(
																				key + 1));
														tr
																.append($(
																		'<td class="col-md-1" ></td>')
																		.html(
																				data.itemCode));
														tr
																.append($(
																		'<td class="col-md-4" ></td>')
																		.html(
																				data.itemDesc));
														tr
																.append($(
																		'<td class="col-md-1" ></td>')
																		.html(
																				data.itemUom));

														tr
																.append($(
																		'<td class="col-md-1" ></td>')
																		.html(
																				'<input type=text    class=form-control id= "Qty'
																						+ data.itemId
																						+ '" name="Qty'
																						+ data.itemId
																						+ '" value = '
																						+ data.itemOpQty
																						+ ' onchange="changeValues('
																						+ data.itemId
																						+ ')">'));
														tr
																.append($(
																		'<td class="col-md-1" ></td>')
																		.html(
																				'<input type=text    class=form-control id= "Rate'
																						+ data.itemId
																						+ '" name="Rate'
																						+ data.itemId
																						+ '" value = '
																						+ data.itemOpRate
																						+ ' onchange="changeValues('
																						+ data.itemId
																						+ ')">'));

														tr
																.append($(
																		'<td class="col-md-1" style="text-align: right;"></td>')
																		.html(
																				'<input type=hidden    class=form-control id= "taxper'
																						+ data.itemId
																						+ '" name="taxper'
																						+ data.itemId
																						+ '" value = '
																						+ (data.cgstPer + data.sgstPer)
																						+ ' ">'
																						+ (data.cgstPer + data.sgstPer)));
														tr
																.append($(
																		'<td class="col-md-1" style="text-align: right;" id="taxableAmt'+data.itemId+'"></td>')
																		.html(
																				(data.itemOpRate * data.itemOpQty)
																						.toFixed(2)));
														tr
																.append($(
																		'<td class="col-md-1" style="text-align: right;" id="taxAmt'+data.itemId+'"></td>')
																		.html(
																				((data.itemOpRate
																						* data.itemOpQty * (data.cgstPer + data.sgstPer)) / 100)
																						.toFixed(2)));
														tr
																.append($(
																		'<td class="col-md-1" style="text-align: right;" id="totalAmt'+data.itemId+'"></td>')
																		.html(
																				((data.itemOpRate * data.itemOpQty) + (data.itemOpRate
																						* data.itemOpQty * (data.cgstPer + data.sgstPer)) / 100)
																						.toFixed(2)));

														tr
																.append($(
																		'<td class="col-md-1" style="text-align: center;"></td>')
																		.html(
																				"<a href='#' class='action_btn'onclick=deleteIndentItem("
																						+ key
																						+ ")><abbr title='Delete'><i class='fa fa-trash-o  fa-lg'></i></abbr></a>"));

														$('#table1 tbody')
																.append(tr);
													} catch (err) {

													}

												})

							});

		}
	</script>
	<script type="text/javascript">
		function getInvoiceNo() {
			//alert("hii");
			var date = $("#indent_date").val();
			var catId = $("#ind_cat").val();
			var typeId = $("#indent_type").val();
			//alert(catId);
			$
					.getJSON(
							'${getInvoiceNo}',
							{

								catId : catId,
								docId : 1,
								date : date,
								typeId : typeId,
								ajax : 'true',

							},
							function(data) {

								document.getElementById("indent_no").value = data.code;
								document.getElementById("mrnLimit").innerHTML = data.subDocument.categoryPostfix;
								document.getElementById("mrnLimitText").value = data.subDocument.categoryPostfix;
								;

							});

		}
	</script>
	<script type="text/javascript">
		function getResetTable() {

			$('#table1 td').remove();
			$('#ind_cat').prop('disabled', false).trigger("chosen:updated");
			document.getElementById("submitt").disabled = true;

		}
	</script>

	<script type="text/javascript">
		function exportExcel() {

			var catId = $("#ind_cat").val();
			var typeId = $("#indent_type").val();
			//alert(catId);
			$
					.getJSON(
							'${exportExcelforIndent}',

							{
								catId : catId,
								typeId : typeId,
								ajax : 'true'

							},
							function(data) {
								//alert(data);
								if (data == "") {
									alert("No records found !!");

								}

								$('#table1 td').remove();
								$
										.each(
												data,
												function(key, trans) {
													//alert(itemList.indDetailId);

													try {

														var tr = $('<tr></tr>');
														tr
																.append($(
																		'<td class="col-sm-1" ></td>')
																		.html(
																				key + 1));
														tr
																.append($(
																		'<td class="col-md-1" ></td>')
																		.html(
																				trans.itemCode));
														tr
																.append($(
																		'<td class="col-md-4" ></td>')
																		.html(
																				trans.itemName));
														tr
																.append($(
																		'<td class="col-md-1" ></td>')
																		.html(
																				trans.uom));
														tr
																.append($(
																		'<td class="col-md-1" ></td>')
																		.html(
																				trans.curStock));

														tr
																.append($(
																		'<td class="col-md-1" ></td>')
																		.html(
																				trans.qty));
														tr
																.append($(
																		'<td class="col-md-1" ></td>')
																		.html(
																				trans.date));

														tr
																.append($(
																		'<td class="col-md-1" style="text-align: center;"></td>')
																		.html(
																				"<a href='#' class='action_btn'onclick=deleteIndentItem("
																						+ trans.itemId
																						+ ","
																						+ key
																						+ ")><abbr title='Delete'><i class='fa fa-trash-o  fa-lg'></i></abbr></a>"));

														$('#table1 tbody')
																.append(tr);
														$('#ind_cat')
																.prop(
																		'disabled',
																		true)
																.trigger(
																		"chosen:updated");

														document
																.getElementById("catId").value = catId;
														document
																.getElementById("submitt").disabled = false;
													} catch (err) {

													}

												})

							});
		}
	</script>
	<script>
		function changeValues(detailNo) {

			var Qty = parseFloat($("#Qty" + detailNo).val());
			var Rate = parseFloat($("#Rate" + detailNo).val());
			var taxableAmt = (Qty * Rate);
			var taxper = parseFloat($("#taxper" + detailNo).val());
			var sgstPer = taxper / 2;
			var cgstPer = taxper / 2;
			var sgstRs = ((taxableAmt * sgstPer) / 100);
			var cgstRs = ((taxableAmt * cgstPer) / 100);
			var totalTax = sgstRs + cgstRs;
			var grandTotal = parseFloat(totalTax) + parseFloat(taxableAmt);

			$("#taxableAmt" + detailNo).html(taxableAmt.toFixed(2));
			$("#taxAmt" + detailNo).html(totalTax.toFixed(2));
			$("#totalAmt" + detailNo).html(grandTotal.toFixed(2));

		}
	</script>
	<script>
		function getItemcategorywise() {

			var catId = $("#ind_cat").val();
			//alert(catId);
			$
					.getJSON(
							'${getItemcategorywise}',

							{
								catId : catId,
								ajax : 'true',

							},
							function(data) {
								//alert(data);
								if (data == "") {
									alert("No records found !!");

								}

								$('#table1 td').remove();
								$
										.each(
												data,
												function(key, data) {

													try {

														var tr = $('<tr></tr>');

														tr
																.append($(
																		'<td class="col-sm-1" ></td>')
																		.html(
																				key + 1));
														tr
																.append($(
																		'<td class="col-md-1" ></td>')
																		.html(
																				data.itemCode));
														tr
																.append($(
																		'<td class="col-md-4" ></td>')
																		.html(
																				data.itemDesc));
														tr
																.append($(
																		'<td class="col-md-1" ></td>')
																		.html(
																				data.itemUom));

														tr
																.append($(
																		'<td class="col-md-1" ></td>')
																		.html(
																				'<input type=text    class=form-control id= "Qty'
																						+ data.itemId
																						+ '" name="Qty'
																						+ data.itemId
																						+ '" value = '
																						+ data.itemOpQty
																						+ ' onchange="changeValues('
																						+ data.itemId
																						+ ')">'));
														tr
																.append($(
																		'<td class="col-md-1" ></td>')
																		.html(
																				'<input type=text    class=form-control id= "Rate'
																						+ data.itemId
																						+ '" name="Rate'
																						+ data.itemId
																						+ '" value = '
																						+ data.itemOpRate
																						+ ' onchange="changeValues('
																						+ data.itemId
																						+ ')">'));

														tr
																.append($(
																		'<td class="col-md-1" style="text-align: right;"></td>')
																		.html(
																				'<input type=hidden    class=form-control id= "taxper'
																						+ data.itemId
																						+ '" name="taxper'
																						+ data.itemId
																						+ '" value = '
																						+ (data.cgstPer + data.sgstPer)
																						+ ' ">'
																						+ (data.cgstPer + data.sgstPer)));
														tr
																.append($(
																		'<td class="col-md-1" style="text-align: right;" id="taxableAmt'+data.itemId+'"></td>')
																		.html(
																				(data.itemOpRate * data.itemOpQty)
																						.toFixed(2)));
														tr
																.append($(
																		'<td class="col-md-1" style="text-align: right;" id="taxAmt'+data.itemId+'"></td>')
																		.html(
																				((data.itemOpRate
																						* data.itemOpQty * (data.cgstPer + data.sgstPer)) / 100)
																						.toFixed(2)));
														tr
																.append($(
																		'<td class="col-md-1" style="text-align: right;" id="totalAmt'+data.itemId+'"></td>')
																		.html(
																				((data.itemOpRate * data.itemOpQty) + (data.itemOpRate
																						* data.itemOpQty * (data.cgstPer + data.sgstPer)) / 100)
																						.toFixed(2)));

														tr
																.append($(
																		'<td class="col-md-1" style="text-align: center;"></td>')
																		.html(
																				"<a href='#' class='action_btn'onclick=deleteIndentItem("
																						+ key
																						+ ")><abbr title='Delete'><i class='fa fa-trash-o  fa-lg'></i></abbr></a>"));

														$('#table1 tbody')
																.append(tr);
														$('#ind_cat')
																.prop(
																		'disabled',
																		true)
																.trigger(
																		"chosen:updated");

														document
																.getElementById("catId").value = catId;
														document
																.getElementById("submitt").disabled = false;
													} catch (err) {

													}

												})

							});
		}
	</script>

</body>
</html>

