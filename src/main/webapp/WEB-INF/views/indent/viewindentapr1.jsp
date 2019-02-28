<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<jsp:include page="/WEB-INF/views/include/header.jsp"></jsp:include>

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
			</div>
			<!-- END Page Title -->



			<!-- BEGIN Main Content -->
			<div class="row">
				<div class="col-md-12">
					<div class="box">
						<div class="box-title">
							<h3>
								<i class="fa fa-bars"></i> View Indent For Approval ${apr}
							</h3>
							<div class="box-tool">
								<a href=""></a> <a data-action="collapse" href="#"><i
									class="fa fa-chevron-up"></i></a>
							</div>
							<!-- <div class="box-tool">
								<a data-action="collapse" href="#"><i
									class="fa fa-chevron-up"></i></a> <a data-action="close" href="#"><i
									class="fa fa-times"></i></a>
							</div> -->
						</div>


						<div class="box-content">
							<form action="${pageContext.request.contextPath}/getIndentsForApproval/"
								class="form-horizontal" id="validation-form" method="POST">

								<input type="hidden" name="apr" id="apr"
									value="${apr}"> 
								<div class="form-group">
									
									<label class="col-sm-3 col-lg-2 control-label">To Date</label>
									<div class="col-sm-5 col-lg-3 controls">
										<input class="form-control date-picker" id="to_date" size="16"
											type="text" name="to_date" required value="${toDate}" />
									</div>

									<div
										class="col-sm-25 col-sm-offset-3 col-lg-30 col-lg-offset-0">
										<input type="submit" value="Submit"  class="btn btn-primary">
									</div>
								</div>

								<div class="clearfix"></div>
								<div id="table-scroll" class="table-scroll">

									<div id="faux-table" class="faux-table" aria="hidden">
										<table id="table2" class="main-table">
											<thead>
												<tr class="bgpink">
													<!-- <th width="180" style="width: 90px">Indent No</th>
													<th width="200" align="left">Date</th>
													<th width="358" align="left">Category</th>
													<th width="194" align="left">Type</th>
													<th width="102" align="left">Development</th>
													<th width="102" align="left">Monthly</th>

													<th width="88" align="left">Action</th> -->
												</tr>
											</thead>
										</table>

									</div><div class="col-md-8" ></div> 
		<!-- 			<label for="search" class="col-md-2" id="search">
    <i class="fa fa-search" style="font-size:15px"></i>
									<input type="text" value="" id="myInput" style="text-align: left; width: 240px;" class="form-control" onkeyup="myFunction()" placeholder="Search Mrn by Name or Vendor" title="Type in a name">
										</label>  -->
										<div class="input-group">
    <input type="text"  id="myInput"  style="text-align: left; color: green;" class="form-control" onkeyup="myFunction()" placeholder="Search Indent By No Or Type"/>
    <span class="input-group-addon">
        <i class="fa fa-search"></i>
    </span>
</div>
<br/>
									<div class="table-wrap">

										<table id="table1" class="table table-advance">
											<thead>
												<tr class="bgpink">

													<!-- <th
														style="text-align: center; padding: 0px; align-items: center;"
														width="60"><input type="checkbox" name="name1"
														value="0" /> &nbsp;&nbsp;&nbsp;All</th> -->
														<th width="2%"  >Sr</th>
													<th class="col-md-1"  
														 >Indent No</th>
													<th class="col-md-1"  >Date</th>
													<th class="col-md-1"  >Category</th>
													<th class="col-md-1"  >Account Head</th>

													<th class="col-md-1"  >Type</th>
													<th style="width:2%;"  >Development</th>
													<th style="width:2%;"  >Monthly</th>
													<th class="col-md-1"  >Status</th>
													<th class="col-md-1">Rej Sts1</th>
													<th class="col-md-1">Rej Sts2</th>
													<th width="5%"  >Action</th>
												</tr>
											</thead>
											<!-- 	<div class="table-responsive" style="border: 0">
									<table width="100%" class="table table-advance" id="table1">
										<thead>
											<tr>
												<th width="180" style="width: 90px">Prod ID</th>
												<th width="200" align="left">Production Date</th>
												<th width="358" align="left">Category</th>
												<th width="194" align="left">Status</th>
												<th width="102" align="left">IsPlanned</th>
												<th width="88" align="left">Action</th>
											</tr>
										</thead> -->
											<tbody>
												<c:forEach items="${indentList}" var="indent"  varStatus="count">
													<tr>
													
														<%-- <td
															style="text-align: left; padding: 0px; align-items: center; align-content: center;"
															width="60">&nbsp;&nbsp;<input type="checkbox"
															name="name1" value="${indent.indMId}" /></td> --%>
															<td style="text-align: center;" width="2%"><c:out
																value="${count.index+1}" /></td>
														<td align="left"  ><c:out
																value="${indent.indMNo}" /></td>
														<td align="left"  ><c:out
																value="${indent.indMDate}" /></td>
														<td align="left"  ><c:out
																value="${indent.catDesc}" /></td>
																<td align="left"  ><c:out
																value="${indent.accHeadDesc}" /></td>
														<c:set var="indmtype" value="o"></c:set>
														<c:forEach items="${typeList}" var="typeList"  varStatus="count">
														<c:choose>
															<c:when test="${indent.indMType==typeList.typeId}">
																<c:set var="indmtype" value="${typeList.typeName}"></c:set>
															</c:when>
															 
														</c:choose>
														</c:forEach>

														<c:set var="indIsDev" value="o"></c:set>

														<c:choose>
															<c:when test="${indent.indIsdev==1}">
																<c:set var="indIsDev" value="Yes"></c:set>
															</c:when>
															<c:otherwise>
																<c:set var="indIsDev" value="No"></c:set>
															</c:otherwise>
														</c:choose>

														<c:set var="indIsmonthly" value="o"></c:set>

														<c:choose>
															<c:when test="${indent.indIsmonthly==1}">
																<c:set var="indIsmonthly" value="Yes"></c:set>
															</c:when>
															<c:otherwise>
																<c:set var="indIsmonthly" value="No"></c:set>
															</c:otherwise>
														</c:choose>

														<td align="left"  ><c:out
																value="${indmtype}" /></td>

														<td align="left"  ><c:out
																value="${indIsDev}" /></td>

														<td align="left"  ><c:out
																value="${indIsmonthly}" /></td>
																
																<c:set var="indSts" value="-"></c:set>
																<c:choose>
															<c:when test="${indent.indMStatus==9 && (indent.apprvRemark1==null || indent.apprvRemark1=='')}">
																<c:set var="indSts" value="1st Approve Pending"></c:set>
															</c:when>
															<c:when test="${indent.indMStatus==7 && (indent.apprvRemark2==null || indent.apprvRemark2=='')}">
																<c:set var="indSts" value="2nd Approve Pending"></c:set>
															</c:when>
															<c:when test="${indent.indMStatus==9 && (indent.apprvRemark1!=null || indent.apprvRemark1!='')}">
																<c:set var="indSts" value="Send Back to 1st Approve "></c:set>
															</c:when>
															<c:when test="${indent.indMStatus==7 && (indent.apprvRemark2!=null || indent.apprvRemark2!='')}">
																<c:set var="indSts" value="Send Back to 2nd Approve"></c:set>
															</c:when>
															<c:when test="${indent.indMStatus==0}">
																<c:set var="indSts" value="Indent Pending"></c:set>
															</c:when>
															<c:when test="${indent.indMStatus==1}">
																<c:set var="indSts" value="Partially Indent Pending"></c:set>
															</c:when>
															<c:when test="${indent.indMStatus==2}">
																<c:set var="indSts" value="Indent Complete"></c:set>
															</c:when>
															 <c:when test="${indent.indMStatus==6}">
																<c:set var="indSts" value="Rejected By 2nd"></c:set>
															</c:when>
															<c:when test="${indent.indMStatus==8}">
																<c:set var="indSts" value="Rejected By 1st"></c:set>
															</c:when>
														</c:choose>
														<td align="left"  ><c:out
																value="${indSts}" /></td>
														<td  ><c:out
																value="${indent.apprvRemark1}" /></td>
														<td  ><c:out
																value="${indent.apprvRemark2}" /></td>

														<td align="left"  >
															<a
															href="${pageContext.request.contextPath}/getIndentDetailToApprove/${indent.indMId}/${apr}" title="Get Indent To Approve "><span
																class="glyphicon glyphicon-th-list"></span></a>&nbsp;&nbsp;&nbsp;
															</td>
													</tr>
												</c:forEach>

											</tbody>
										</table>
										
									</div>

								</div>
							</form>
						</div>
					</div>

				</div>

			</div>
			<!-- END Main Content -->
			<footer>
			<p>2018 © Trumbak Rubber.</p>
			</footer>

			<a id="btn-scrollup" class="btn btn-circle btn-lg" href="#"><i
				class="fa fa-chevron-up"></i></a>
		</div>
		<!-- END Content -->
	</div>
	<!-- END Container -->

	<!--basic scripts-->


	<script type="text/javascript">
		function genPdf(id) {

			window.open('pdfForReport?url=indentPdfDoc/' + id);

		}
		
		
		
		function commonPdf() {

			var list = [];

			$("input:checkbox[name=name1]:checked").each(function() {
				list.push($(this).val());
			});

			window.open('pdfForReport?url=indentPdfDoc/' + list);

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
function getIndnetForAppr(apr){
/* 	alert(apr);
	var indToDate=document.getElementById("to_date").value;
	alert(indToDate);																																																																																																																																																																											
	/*  var form = document.getElementById("validation-form");
	    form.action ='${pageContext.request.contextPath}/getIndentsForApproval/1/'+indToDate;
	    form.submit(); 
window.open("${pageContext.request.contextPath}/getIndentsForApproval/1/"+indToDate+",_self");
	 */
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