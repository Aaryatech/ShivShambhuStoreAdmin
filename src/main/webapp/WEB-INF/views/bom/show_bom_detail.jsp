<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<jsp:include page="/WEB-INF/views/include/header.jsp"></jsp:include>
<c:url value="/getMrnDetailBatchForIssue" var="getMrnDetailBatchForIssue"></c:url>
<body>
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
			<div class="page-title">
				<!-- <div>
					<h1>
						<i class="fa fa-file-o"></i>Indent Header
					</h1>

				</div> -->
			</div><br>
			<!-- END Page Title -->



			<!-- BEGIN Main Content -->
			<div class="row">
				<div class="col-md-12">
					<div class="box">
						<div class="box-title">
							<h3>
								<i class="fa fa-bars"></i>BOM Item Request List
							</h3>
							<div class="box-tool">
								<div class="box-tool">
								<%-- <a href="${pageContext.request.contextPath}/showIndent">Add Indent</a> <a data-action="collapse" href="#"><i
									class="fa fa-chevron-up"></i></a>  --%>
							</div>
							</div>
							<!-- <div class="box-tool">
								<a data-action="collapse" href="#"><i
									class="fa fa-chevron-up"></i></a> <a data-action="close" href="#"><i
									class="fa fa-times"></i></a>
							</div> -->
						</div>


						<div class="box-content">
							<form action="${pageContext.request.contextPath}/insertIssueFromBomReq"
								class="form-horizontal" id="validation-form" method="post">

							<!-- 	<input type="hidden" name="mode_add" id="mode_add"
									value="add_att"> -->

								<div class="form-group">
									<label class="col-sm-3 col-lg-2 control-label">Plant Name</label>
									<div class="col-sm-5 col-lg-3 controls">
										<input id="plant_name" readonly="readonly"
											size="16" type="text" name="plant_name" value="${bomHeader.plantName}"
											 />
									</div>
									<!-- </div>


								<div class="form-group"> -->
									<label class="col-sm-3 col-lg-2 control-label">Sub Plant Name</label>
									<div class="col-sm-5 col-lg-3 controls">
										<input  id="to_date" size="16" readonly
											type="text" name="to_date"  value="${bomHeader.subplantName}" />
									</div>

									<!-- <div
										class="col-sm-25 col-sm-offset-3 col-lg-30 col-lg-offset-0">
										<input type="submit" value="Submit" class="btn btn-primary">
									</div> -->

								</div>
								
								
								<div class="form-group">
									<label class="col-sm-3 col-lg-2 control-label">Production Id</label>
									<div class="col-sm-5 col-lg-3 controls">
										<input id="prod_id" readonly="readonly"
											size="16" type="text" name="prod_id" value="${bomHeader.plantName}"
											 />
									</div>
									<!-- </div>


								<div class="form-group"> -->
									<label class="col-sm-3 col-lg-2 control-label">Sender Name</label>
									<div class="col-sm-5 col-lg-3 controls">
										<input  id="sender_id" size="16" readonly
											type="text" name="sender_id"  value="${bomHeader.usrName}" />
									</div>

									<!-- <div
										class="col-sm-25 col-sm-offset-3 col-lg-30 col-lg-offset-0">
										<input type="submit" value="Submit" class="btn btn-primary">
									</div> -->

								</div>
								
								<input type="hidden" name="prodId" value="${bomHeader.productionId}"> 
													<input type="hidden" name="bomReqId" value="${bomHeader.bomReqId}"> 
													<input type="hidden" name="plantId" value="${bomHeader.plantId}"> 
													<input type="hidden" name="subPlantId" value="${bomHeader.subPlantId}"> 
													<input type="hidden" name="senderUserId" value="${bomHeader.senderUserId}"> 
																					
					
								
								<c:set value="0" var="isEdit"></c:set>
								<c:set value="0" var="isDelete"></c:set>
								 
				 <c:forEach items="${sessionScope.newModuleList}" var="allModuleList" >
						<c:choose>
							<c:when test="${allModuleList.moduleId==sessionScope.sessionModuleId}">
								  <c:forEach items="${allModuleList.subModuleJsonList}" var="subModuleJsonList" >
								  		<c:choose>
										  	<c:when test="${subModuleJsonList.subModuleId==sessionScope.sessionSubModuleId}">
										  		  <c:choose>
										  				<c:when test="${subModuleJsonList.editReject eq 'visible'}">
										  				<c:set value="1" var="isEdit"></c:set>
										  				</c:when>
										  			</c:choose>
													<c:choose>
										  				<c:when test="${subModuleJsonList.deleteRejectApprove eq 'visible'}">
										  				<c:set value="1" var="isDelete"></c:set>
										  				</c:when>
										  			</c:choose>
										  	</c:when>
									  	</c:choose>
								  </c:forEach>
							</c:when> 
						</c:choose>
					 
					</c:forEach>  


								<div class="clearfix"></div>
								<div id="table-scroll" class="table-scroll">

									<div id="faux-table" class="faux-table" aria="hidden">
										<table id="table2" class="main-table">
											<thead>
												<tr class="bgpink">
												
												</tr>
											</thead>
										</table>

									</div><div class="col-md-8" ></div> 
		
										<div class="input-group">
    <input type="text"  id="myInput"  style="text-align: left; color: green;" class="form-control" onkeyup="myFunction()" placeholder="Search Request By Type"/>
    <span class="input-group-addon">
        <i class="fa fa-search"></i>
    </span>
</div>
<br/>
									<div class="table-wrap">

										<table id="table1" class="table table-advance">
											<thead>
												<tr class="bgpink">

												
													<th align="center" width="10%" >Sr</th>
													<th align="center" width="25%">RM Name</th>
													<th  align="center" width="10%">UOM</th>
													<th  align="center" width="10%">Auto Req Qty</th>
													<th  align="center" width="10%">Req Qty</th>
													<th  align="center" width="10%">Issue Qty</th>
													<th  align="center" width="25%">Issue Batch</th>
												</tr>
											</thead>
											
											<tbody>
												<c:forEach items="${bomDetailList}" var="bom" varStatus="count">
													<tr>
														
														<td width="10%"  ><c:out
																value="${count.index+1}" /></td>
														<td width="25%" align="left" ><c:out
																value="${bom.itemCode}" /></td>
														<td width="10%"  align="left" ><c:out
																value="${bom.rmUomName}" /></td>
																<td width="10%" align="center" ><c:out
																value="${bom.autoRmReqQty}" /></td>
														 
														<td  width="10%" align="center" ><c:out
																value="${bom.rmReqQty}" /></td>
																
														 
														  <td align="right"width="10%" ><input type="text" id="issueQty${bom.rmId}" onkeypress="return allowOnlyNumber(event);" name="issueQty${bom.rmId}"
																value="${bom.rmIssueQty}" style="width: 100%;" onchange="getMrnBatch(${bom.rmId});setMrnBatchName(${bom.rmId})" /></td>
																
														<td width="25%" ><select id="mrnBatch${bom.rmId}" onclick="setMrnBatchName(${bom.rmId})" style="width: 100%;"  name="mrnBatch${bom.rmId}"><option value="-1">Select Batch</option></select></td>
														 <input type="hidden" id="mrnBatchName${bom.rmId}" name="mrnBatchName${bom.rmId}"/>
														<%-- <td><a
															href="javascript:genPdf(${bom.bomReqId});" title="PDF"><span
																class="glyphicon glyphicon glyphicon-file"></span></a>&nbsp;&nbsp;&nbsp;&nbsp;
																<a
																	href="${pageContext.request.contextPath}/getReqBomDetail/${bom.bomReqId}" title="Get Detail"><span
																		class="glyphicon glyphicon-info-sign"></span></a>
															<c:choose>
																<c:when test="${isEdit==1}">	
																	<a
																	href="${pageContext.request.contextPath}/getReqBomDetail/${bom.bomReqId}" title="Get Detail"><span
																		class="glyphicon glyphicon-info-sign"></span></a>&nbsp;&nbsp;&nbsp;
																</c:when>
															</c:choose>
															
															
															</td> --%>
													</tr>
												</c:forEach>

											</tbody>
										</table>


										<br> <br>
										<button
											style="background-color: #008CBA; border: none; color: white; text-align: center; text-decoration: none; display: block; font-size: 12px; cursor: pointer; width: 50px; height: 30px; margin: auto;"
											onclick="commonPdf()">PDF</button>


									</div>
									<div
										class="col-sm-25 col-sm-offset-3 col-lg-30 col-lg-offset-0">
										<input type="submit" value="Submit" class="btn btn-primary">
									</div>

								</div>
							</form>
						</div>
					</div>

				</div>

			</div>
			<!-- END Main Content -->
			<footer>
			<p>2018 © ATS for Shiv Shambhu.</p>
			</footer>

			<a id="btn-scrollup" class="btn btn-circle btn-lg" href="#"><i
				class="fa fa-chevron-up"></i></a>
		</div>
		<!-- END Content -->
	</div>
	<!-- END Container -->

	<!--basic scripts-->


	<script type="text/javascript">
		  /* function genPdf(id) {

			window.open('pdfForReport?url=/pdf/indentPdfDoc/' + id);

		}  */ 
		
		  function genPdf(id,isMonthly) {
			
			if(isMonthly==0){
				window.open('indentPdfDoc/' + id);
			}
			else{
				 
				window.open('indentPdfDocFullPage/' + id);
			}

			

		}  
		
		
		
		function commonPdf() {

			var list = [];

			$("input:checkbox[name=name1]:checked").each(function() {
				list.push($(this).val());
			});

			window.open('pdfForReport?url=/pdf/indentPdfDoc/' + list);

		}
	</script>
	<script type="text/javascript">
	
		function allowOnlyNumber(evt){
	    var charCode = (evt.which) ? evt.which : event.keyCode
	    if (charCode == 46){
	        var inputValue = $("#floor").val();
	        var count = (inputValue.match(/'.'/g) || []).length;
	        
	        if(count<1){
	            if (inputValue.indexOf('.') < 1){
	                return true;
	            }
	            return false;
	        }else{
	            return false;
	        }
	    }
	    if (charCode != 46 && charCode > 31 && (charCode < 48 || charCode > 57)){
	        return false;
	    }
	    return true;
	}
	
	</script>
	
	
	<script type="text/javascript">
	
	function getMrnBatch(rmId){
		var issueQty=document.getElementById("issueQty"+rmId).value;
		//alert("issueQty " +issueQty);
		var valid=true;
		if(issueQty=="" || issueQty==null || issueQty<=0.0){
			valid=false;
		}
		if(valid==true){
		
			$.getJSON('${getMrnDetailBatchForIssue}', {

				rmId : rmId,
				ajax : 'true',

			},

			function(data) {
				var html;
				var len = data.length;
				var html = '<option value="-1"  >Select</option>';
				for (var i = 0; i < len; i++) {

					html += '<option value="' + data[i].mrnDetailId + '">'
							+ data[i].batchNo + '</option>';
				}
				html += '</option>';

				$('#mrnBatch'+rmId).html(html);
				$("#mrnBatch"+rmId).trigger("chosen:updated");

			});
			
		}
		
	}
	
	function setMrnBatchName(rmId){
	//	alert(" rm Id " +rmId);
		batchName=$("#mrnBatch"+rmId+" option:selected").text();
		//alert("Batch name " +batchName);
		//var materialName=$("#rm_material_name option:selected").html();
		//batchName=document.getElementById("mrnBatchName"+rmId)
		 document.getElementById("mrnBatchName"+rmId).value=batchName;
		 var batchHidden=document.getElementById("mrnBatchName"+rmId).value;
		// alert("Batch batchHidden " +batchHidden);
		
	}
	
	</script>
	
	
	<script>
function myFunction() {
  var input, filter, table, tr, td,td1, i;
  input = document.getElementById("myInput");
  filter = input.value.toUpperCase();
  table = document.getElementById("table1");
  tr = table.getElementsByTagName("tr");
  for (i = 0; i < tr.length; i++) {
    td = tr[i].getElementsByTagName("td")[5];
    td1 = tr[i].getElementsByTagName("td")[1];
    if (td || td1) {
      if (td.innerHTML.toUpperCase().indexOf(filter) > -1) {
        tr[i].style.display = "";
      }else if (td1.innerHTML.toUpperCase().indexOf(filter) > -1) {
        tr[i].style.display = "";
      }  else {
        tr[i].style.display = "none";
      }
    }       
  }//end of for
  
 
  
}
</script>


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
</body>
</html>