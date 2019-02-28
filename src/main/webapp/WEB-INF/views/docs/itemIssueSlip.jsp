<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title>Item Issue PDF</title>
<meta name="description" content="">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<!-- Place favicon.ico and apple-touch-icon.png in the root directory -->

 

<style type="text/css">
table {
	border-collapse: separate;
	font-family: arial;
	font-weight: bold;
	font-size: 90%;
	width: 100%;
	page-break-inside: auto !important;
	display: block;
}

p {
	color: black;
	font-family: arial;
	font-size: 70%;
	margin-top: 0;
	padding: 0;
	font-weight: bold;
}

.pn {
	color: black;
	font-family: arial;
	font-size: 10%;
	margin-top: 0;
	padding: 0;
	font-weight: normal;
}

h4 {
	color: black;
	font-family: sans-serif;
	font-size: 80%;
	font-weight: bold;
	padding-bottom: 10px;
	margin: 0;
}

h5 {
	color: black;
	font-family: arial;
	font-size: 95%;
	font-weight: bold;
	padding-bottom: 10px;
	margin: 0;
}

h6 {
	color: black;
	font-family: arial;
	font-size: 60%;
	font-weight: normal;
	margin: 10%;
}

th {
	 
	color: black;
}

hr {
	height: 1px;
	border: none;
	color: rgb(60, 90, 180);
	background-color: rgb(60, 90, 180);
}

.invoice-box table tr.information table td {
	padding-bottom: 0px;
	align-content: center;
}

.set-height td {
	position: relative;
	overflow: hidden;
	height: 2em;
}

.set-height t {
	position: relative;
	overflow: hidden;
	height: 2em;
}

.set-height p {
	position: absolute;
	margin: .1em;
	left: 0;
	top: 0;
}
</style>


</head>
<body>



	<c:forEach items="${list}" var="item" varStatus="count">
 <br>
		<div align="left">
			<h5>${documentBean.docIsoSerialNumber}</h5>
		</div>


		<h4 align="center" align="center" style=" font-family: arial; font-weight: bold; font-size: 120%;">${company.companyName}</h4>

					<table>
						<tr style=" font-family: arial; font-weight: bold; font-size: 110%;">
 
								<td width="1200px" valign="top" align="center"
									style="font-weight: bold;">ITEMS ISSUE SLIP</td>
 
							</tr> 
						</table>

		 
					<table>
						 
						<tr style=" font-family: arial; font-weight: bold; font-size: 110%;">

								<td width="600px">Issue No. : &nbsp;&nbsp;${item.issueNo}</td>
 
								<td width="600px" align="right">Slip No.&nbsp;&nbsp;: ${item.issueSlipNo} 

								</td>
							</tr>
							<tr style=" font-family: arial; font-weight: bold; font-size: 110%;">

								<td width="600px"> </td>
 
								<td width="600px" align="right"> Date :
									${item.issueDate} 

								</td>
							</tr>
						</table> 



		 
						<table>
							<tr style=" font-family: arial; font-weight: bold; font-size: 110%;">
								<td    >
									Department.  &nbsp;&nbsp;&nbsp;: ${item.deptCode}<br>
									Sub.Dept. &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;: ${item.subDeptCode}
								</td>
								

								<!-- 	<td width="50%" valign="top"
								style="border-left: 0px solid #313131; border-top: 1px solid #313131; border-bottom: 1px solid #313131; padding: 8px; color: #000; font-size: 12px;"> &nbsp; ..
							</td> -->

							</tr>

						</table> 


		<br>


		<table align="center" border="1" cellspacing="0" cellpadding="1"
			id="table_grid" class="table table-bordered" style="table-layout: fixed; display: block; height: 300px; width: 100%;">
			<thead>
				<tr style=" font-family: arial; font-weight: bold; font-size: 110%;">
					<th>SR.</th>
					<th>ITEM</th>
					<th>DESCRIPTION</th>
					<th>UOM</th>
					<th>ISSUED QTY.</th>
					<th>WT/SIZE</th>
					<th>RECEIVER</th>

				</tr>
			</thead>
			<tbody>

				<c:set var="totalRowCount" value="0" />
				<c:set var="maxRowCount" value="3" />

				<c:forEach items="${item.issueReportDetailList}" var="row"
					varStatus="count">

					<c:choose>

						<c:when test="${totalRowCount eq maxRowCount}">

							<c:set var="totalRowCount" value="${totalRowCount+1}" />

							 



							<!-- new page -->
			</tbody>
		</table>
		 
		<br>



		<p
			style="color: #000;   text-align: left; margin: 0px;   font-family: arial; font-weight: bold; font-size: 90%;">
			Remark - <br> <br>  
			<br>

		</p>

		<div class="invoice-box">
			<table cellpadding="0" cellspacing="0">

				<tr class="information">
					<td colspan="3" valign="top">
						<table>
							<tr>
								<td width="400px" valign="top" align="center"
									style="padding: 8px; color: #000;   font-weight: bold;">

									Authorised By</td>

								<td width="400px" valign="top" align="center"
									style="padding: 8px; color: #000;   font-weight: bold;">

									Issued By</td>

								<td width="400px" valign="top" align="center"
									style="padding: 8px; color: #000;   font-weight: bold;">

									Received By</td>



							</tr>

						</table>
					</td>
				</tr>
			</table>
															<hr	style="height: 1px; border: none; color: black; background-color: black;">
			
		</div><br><br>


		<div align="left">
			<h5>${documentBean.docIsoSerialNumber}</h5>
		</div>


		<h4 align="center" align="center" style=" font-family: arial; font-weight: bold; font-size: 120%;">${company.companyName}</h4>


		<table>
						<tr style=" font-family: arial; font-weight: bold; font-size: 110%;">
 
								<td width="1200px" valign="top" align="center"
									style="font-weight: bold;">ITEMS ISSUE SLIP</td>
 
							</tr> 
						</table>

		 
					<table>
						 
						<tr style=" font-family: arial; font-weight: bold; font-size: 110%;">

								<td width="600px">Issue No. : &nbsp;&nbsp;${item.issueNo}</td>
 
								<td width="600px" align="right">Slip No.&nbsp;&nbsp;: ${item.issueSlipNo} 

								</td>
							</tr>
							<tr style=" font-family: arial; font-weight: bold; font-size: 110%;">

								<td width="600px"> </td>
 
								<td width="600px" align="right"> Date :
									${item.issueDate} 

								</td>
							</tr>
						</table>



					<table>
							<tr style=" font-family: arial; font-weight: bold; font-size: 110%;">
								<td    >
									Department.  &nbsp;&nbsp;&nbsp;: ${item.deptCode}<br>
									Sub.Dept. &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;: ${item.subDeptCode}
								</td>
								

								<!-- 	<td width="50%" valign="top"
								style="border-left: 0px solid #313131; border-top: 1px solid #313131; border-bottom: 1px solid #313131; padding: 8px; color: #000; font-size: 12px;"> &nbsp; ..
							</td> -->

							</tr>

						</table> 


		<br>


		<table align="center" border="1" cellspacing="0" cellpadding="1"
			id="table_grid" class="table table-bordered" style="table-layout: fixed; display: block; height: 300px; width: 100%;">
			<thead>
				<tr style=" font-family: arial; font-weight: bold; font-size: 110%;">
					<th>SR.</th>
					<th>ITEM</th>
					<th>DESCRIPTION</th>
					<th>UOM</th>
					<th>ISSUED QTY.</th>
					<th>WT/SIZE</th>
					<th>RECEIVER</th>

				</tr>
			</thead>
			<tbody>

				<c:set var="totalRowCount" value="0" />
				<c:set var="maxRowCount" value="3" />


				<!-- end of new page -->

				</c:when>

				</c:choose>

 				<c:set var="totalRowCount" value="${totalRowCount+1}" />
				<tr style=" font-family: arial; font-weight: bold; font-size: 110%;">
					<td width="0" align="center"><c:out value="${count.index+1}" /></td>
					<td width="0" align="center"><c:out value="${row.itemCode}" /></td>
					<td width="50%" align="left" style="padding: 10px;"><c:out value="${row.itemDesc}" /></td>
					<td width="0" align="center"><c:out value="${row.itemUom}" /></td>
					<td width="0" align="right" style="padding: 10px;"><c:out value="${row.itemIssueQty}" /></td>
					<td width="0" align="center"><c:out
							value="NA" /></td>
					<td width="0" align="center"><c:out
							value="NA" /></td>

				</tr>




				</c:forEach>

			</tbody>
		</table>

		<br>
		 

		<p
			style="color: #000;   text-align: left; margin: 0px;   font-family: arial; font-weight: bold; font-size: 90%;">
			Remark - <br> <br>  
			<br>

		</p>

		<div class="invoice-box">
			<table cellpadding="0" cellspacing="0">

				<tr class="information">
					<td colspan="3" valign="top">
						<table>
							<tr>
								<td width="400px" valign="top" align="center"
									style="padding: 8px; color: #000;   font-weight: bold;">

									Authorised By</td>

								<td width="400px" valign="top" align="center"
									style="padding: 8px; color: #000;   font-weight: bold;">

									Issued By</td>

								<td width="400px" valign="top" align="center"
									style="padding: 8px; color: #000;   font-weight: bold;">

									Received By</td>



							</tr>

						</table>
					</td>
				</tr>
			</table>
															<hr	style="height: 1px; border: none; color: black; background-color: black;">
			 
		</div>
	</c:forEach>

	<!-- END Main Content -->

</body>
</html>