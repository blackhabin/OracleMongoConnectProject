/**
 * listMongo.js
 */

//  다른 js에서도 사용을 해야하기 때문에 dialog는 전역변수 선언함.
var dialog;
// 다른 js에서 사용하기 위해서 전역함수로 선언
openDialog = function(url) { // board 객체의 메서드로 선언
    board.dialog.load(url, function() {
        board.dialog.dialog("open");
    });
}


var $jqGrid = $('#jqGrid');
$(document).ready(function() {
	console.log("listmongo js 호출");
	$jqGrid.jqGrid({
			url: '/board/data',
			jsonReader: {
		        root: 'rows'
		    },
			mtype: "GET",
			datatype: "json",
			page: 100,
			pageable: true,
			countable: true,
			sortable: true,
			domainId : "몽고 게시판 리스트",
			colNames:['번호','제목','내용','작성자', '파일이름', '작성일자','수정일자'],
			colModel: [
				{align:"center", name: 'boardNo', key: true },
				{align:"center", name: 'boardTitle'},
				{align:"center", name: 'boardContent'},
				{align:"center", name: 'boardWriter'},
				{align:"center", name: 'boardFileName'},
				{align:"center", name: 'boardCreateTime'},
				{align:"center", name: 'boardModifiTime'}
			],
			pager: "#jqGridNavi",
			caption: "몽고 게시판 리스트",
			autowidth: true,
			rownumbers: true,
			rowNum: 15,
			cellsubmit: 'clientArray',
			rowClickFocus: false,
			rowClickColor: 'default',
			
			/* 상세화면 들어가기 */
			ondblClickRow: function(rowid, iRow, iCol, e) {
	            var cm = $(this).jqGrid('getGridParam', 'colModel');
				// 컬럼을 더블클릭한 경우
	            if (cm[iCol].name === 'boardTitle' 
					|| cm[iCol].name === 'boardNo' 
					|| cm[iCol].name === 'boardWriter'
					|| cm[iCol].name === 'boardContent'
					|| cm[iCol].name === 'boardFileName'
					|| cm[iCol].name === 'boardCreateTime'
					|| cm[iCol].name === 'boardModifiTime') { 
	                $.ajax({
	                    type: 'POST',   
						url: '/setSessionNo',
	                    contentType: 'application/json',
	                    data: JSON.stringify({
	                        boardNo: rowid
	                    }),
	                    success: function() {
	                        $.get('detail.do', function(data) {
								dialog.html(data); // Dialog 내용 변경
			                    dialog.dialog("open");
	                        });
	                    }
	                });
	            }
	        },
			
			emptyrecords: '조회된 데이타가 없습니다.'
	});
	
	$(window).bind('resize', function () {
        var width = $(".grid-wrapper:visible").eq(0).width();
        $('#jqGrid').setGridWidth(width);
     });

		// 글 작성 호출 함수
    console.log("boardWriteOracle 호출");
	$('#btnWriteOracle').click(function(e) {
        e.preventDefault();
        $.get('boardWriteOracle', function(data) {
           // $('body').html(data);  // 페이지의 전체 내용 변경
			dialog.html(data); // Dialog 내용 변경
			dialog.dialog("open");
            console.log("list->write 성공");
        });
    });


		/* dialog 함수 */
    dialog = $('<div id="dialog-form"></div>').dialog({
		appendTo: 'body',
	    autoOpen: false,
	    width: 1200,
	    height: 600,
		title: '상세보기',
		// 모달 다이얼로그로 생성
	    modal: true,
	    close: function(e) {
		 	$('body').load('board');
			dialog.dialog("close");
	    }
	});
});
