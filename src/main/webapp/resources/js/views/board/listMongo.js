/**
 * listMongo.js
 */

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
			colNames:['번호','제목','내용','작성s자', '파일이름', '작성일자','수정일자'],
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
			emptyrecords: '조회된 데이타가 없습니다.'
	});
	
	$(window).bind('resize', function () {
        var width = $(".grid-wrapper:visible").eq(0).width();
        $('#jqGrid').setGridWidth(width);
     });

});