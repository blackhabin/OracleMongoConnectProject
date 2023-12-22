/**
 * saveMongo.js
 */

// 몽고db 저장 함수
function goSaveMongo() {
    console.log("gosave 호출");


	// jsp 화면에 오라클에서 불러온 리스트를 몽고 db로 insert 할 경우
	/*
    var mongoData = [];
    $("tbody tr").each(function() {
	// 여기에서 불러오는 값들은 자바 컨트롤단 안에 있는 리스트를 가져와야한다.
        var row = $(this);
        var boardVO = {
            boardNo: row.find("td:eq(0)").text(),
            boardTitle: row.find("td:eq(1)").text(),
            boardContent: row.find("td:eq(2)").text(),
            boardWriter: row.find("td:eq(3)").text(),
            boardFileName: row.find("td:eq(4)").text(),
            boardCreateTime: row.find("td:eq(5)").text(),
            boardModifiTime: row.find("td:eq(6)").text()
        };
        mongoData.push(boardVO);
    });

    $.ajax({
        url: "/saveToMongoDB",
        type: "POST",
        data: JSON.stringify(mongoData),
        contentType: "application/json; charset=utf-8",
        success: function() {
            alert('몽고 DB 저장성공');
        },
        error: function (xhr) {
			var e = JSON.parse(xhr.responseText);
        	alert(e.message);
        }
    });
	*/
	
	// jsp 화면에 몽고DB에서 불러온 리스트를 위치 시키고 컨트롤단 안에서 오라클 데이터를 몽고 db로 insert 할 경우
	$.ajax({
        url: "/saveToMongoDB",
        type: "POST",
        success: function() {
            alert('몽고 DB 저장성공');
			location.reload();
        },
        error: function (xhr) {
			var message = xhr.responseText || 'An error occurred';
        	alert(message);
        }
    });
}
