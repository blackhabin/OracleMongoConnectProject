/**
 * detail.js
 */



$(document).ready(function() {

	// list.js 가 로드 되면 함수 실행
	$.getScript("resources/js/views/board/listMongo.js", function() {
		
		// 수정 페이지 로드 함수
		console.log("getScript 호출");
		$('.modify-link').click(function(e) {
			e.preventDefault();
			$.ajax({
				type: 'POST',
				url: '/setSessionNo',
				contentType: 'application/json',
				data: JSON.stringify({
					boardNo: $(this).data('boardNo')
				}),
				success: function() {
					$.get('modify.do', function(data) {			
						dialog.html(data);
				        dialog.dialog("open");
				    });
					console.log("modify 연결 성공");
				}
			});
		});
		
		// 리스트 로드 함수
		$("#btnBoardList").click(function(e){
			e.preventDefault();
			 $('body').load('board');
	         closeDialog();

		
		});
		
		// 게시물 삭제 함수
		$('#btnDeletePost').click(function(e) {
	        e.preventDefault();
			console.log("게시물 삭제 js 진입");
	        var boardNo = $("#deleteForm input[name='boardNo']").val();
	        if (confirm("정말 삭제하시겠습니까?")) {
	            $.ajax({
	                type: 'POST',
	                url: '/deletePost',
	                data: {
	                    boardNo: boardNo
	                },
	                success: function() {
	                    $('body').load('board');
	                    // Dialog를 닫기
	                    closeDialog();
	                    console.log("삭제 후 - list 연결 성공");
	                },
	                error: function() {
	                    console.log("삭제 요청 실패");
	                }
	            });
	        }
	    });

	});
});