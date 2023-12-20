/**
 * saveMongo.js
 */

// 몽고db 저장 함수
function goSave() {
	console.log("gosave 호출");
    $.ajax({
        url: "/saveToMongoDB",
        type: "POST",
        success: function() {
            alert('몽고 DB 저장성공');
        },
        error: function(errorThrown) {
            alert('Error: ' + errorThrown);
        }
    });
}
