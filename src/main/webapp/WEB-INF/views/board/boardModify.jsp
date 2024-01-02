<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>글 상세보기</title>
</head>
	<style>
		h2 { 
			text-align: center;}
	  	table { 
	  		width: 600px;}
	  	textarea { 
	  		width: 100%;}
	 	#content {
			display: block;
			width: 40%;
			margin: auto;
		}
		
	    div.button-container {
	        display: flex;
	        justify-content: flex-start;
	    }
	    div.button-container button {
	        margin-right: 5px;
	    }
	    .myButton {
        	width: 80px;  /* 버튼의 너비 */
        	height: 30px;  /* 버튼의 높이 */
    	}
	</style>
	<body>
		<br><br><br>
		 
			<div id="content">
				<table border="1">
					<tr>
						<td>
							제목: ${board.boardTitle }
						</td>
					</tr>
					<tr>
						<td>
							작성자: ${board.boardWriter }
							<span style="float: right; margin-right: 10px;">${board.boardCreateTime }</span>
						</td>
					</tr>
					<tr>
					    <td>첨부파일:
					    </td>
					</tr>
					<tr>
						<td><div style="height: 300px; margin: 10px; display: inline-block">${board.boardContent }</div></td>
					</tr>
				</table>
				
				
				<div class="button-container">
				    <button type="button" class="modify-link myButton" data-no="${board.boardNo}">수정</button>
				    <form id="deleteForm" action="/deletePost" method="POST">
				        <input type="hidden" name="boardNo" value="${board.boardNo}">
				        <button type="submit" class="myButton" id="btnDeletePost" data-no="${board.boardNo}">삭제</button>
				    </form>
					<input type="button" value="작성글 목록" class="myButton btn-outline-primary" style="float: right;" id="btnBoardList"> 
				</div>
			</div>

			
			<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
			<script src="/resources/js/views/board/boardDetail.js"></script>
	</body>
</html>