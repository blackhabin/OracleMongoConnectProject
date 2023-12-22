/**
 * saveOracle.js
 */

// oracle db 저장 함수


function gosaveOracle() {
    console.log("gosaveOracle 호출");

	var boardTitle = document.querySelector('input[name="boardTitle"]').value;
	var boardContent = document.querySelector('textarea[name="boardContent"]').value;
	var boardWriter = document.querySelector('input[name="boardWriter"]').value;
	//var File = document.querySelector('input[name="boardFile"]');
	
	// fileInput.files.length > 0 조건을 통해 사용자가 파일을 첨부했는지 확인하고, 
	// 첨부했다면 첫 번째 파일을 file 변수에 저장. 파일을 첨부하지 않았다면 file 변수에는 null이 저장
	//var boardFile = File.files.length > 0 ? File.files[0] : null;
	
	var formData = new FormData();
    formData.append('boardTitle', boardTitle);
    formData.append('boardContent', boardContent);
    formData.append('boardWriter', boardWriter);
    //formData.append('boardFile', boardFile);
	
	// 오라클로 insert 할 경우
	$.ajax({
        url: "/saveToOracle",
        type: "POST",
		data: formData,
		processData: false,  
    	contentType: false, 
        success: function() {
            alert('오라클 DB 저장성공');
			$('body').load('board');
			closeDialog();
	        console.log("삭제 후 - list 연결 성공");
        },
        error: function (xhr) {
			var message = xhr.responseText || 'An error occurred';
        	alert(message);
        }
    });
}



