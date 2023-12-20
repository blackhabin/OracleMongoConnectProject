<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Board</title>
		<style>
			table {
				width: 100%;
				border-collapse: collapse;
			}

			th, td {
				border: 1px solid #ddd;
				padding: 8px;
			}

			th {
				background-color: #4CAF50;
				color: white;
			}
		</style>
		<link rel="stylesheet" href="/resources/css/views/common/jquery-ui.css" />
		<link rel="stylesheet" href="/resources/css/views/common/ui.jqgrid.css" />

		<script src="/resources/js/views/common/jquery.min.js"></script>
		<script src="/resources/js/views/common/jquery-ui.min.js"></script>
		<script src="/resources/js/views/common/grid.locale-kr.js"></script>
		<script src="/resources/js/views/common/jquery.jqGrid.js"></script>
		
	</head>
	<body>
		<table>
			<thead>
				<tr>
					<th>번호</th>
					<th>제목</th>
					<th>내용</th>
					<th>작성자</th>
					<th>파일명</th>
					<th>생성 시간</th>
					<th>수정 시간</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="boardVO" items="${list}">
					<tr>
						<td>${boardVO.boardNo }</td>
						<td>${boardVO.boardTitle }</td>
						<td>${boardVO.boardContent }</td>
						<td>${boardVO.boardWriter }</td>
						<td>${boardVO.boardFileName }</td>
						<td>${boardVO.boardCreateTime }</td>
						<td>${boardVO.boardModifiTime }</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<input id="subBtn" type="button" value="몽고DB저장" style="float: right;" onclick="javascript:goSave()"/>

	<!-- tomcat에서 module Path 에 따라서 경로 달라짐 -->
	<script src="/resources/js/views/board/saveMongo.js"></script>
	</body>
</html>