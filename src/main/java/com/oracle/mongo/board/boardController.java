package com.oracle.mongo.board;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class boardController{
	private boardDao boardDao;
	 
	@Autowired
    public boardController(boardDao boardDao) {
        this.boardDao = boardDao;
    }
	
	@RequestMapping("/board")
	public String board() {
	    return "board/boardList";
	}

	
	@RequestMapping("/board/data")
	@ResponseBody
	public JSONObject boardMongoList() {
		List<Map<String, Object>> list = boardDao.getBoardsMongo();
		JSONObject response = new JSONObject();
	    response.put("total", 5); // 전체 페이지 수
	    response.put("page", 1); // 현재 페이지 번호
	    response.put("records", list.size()); // 전체 레코드 수
		System.out.println("몽고보드 리스트 컨트롤러");
		
		JSONArray rows = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject row = new JSONObject();
			// 테이블 행
			row.put("id", i+1);
			Map<String, Object> item = list.get(i);
			
			row.put("boardNo", item.get("boardNo"));
			row.put("boardTitle", item.get("boardTitle"));
			row.put("boardContent", item.get("boardContent"));
			row.put("boardWriter", item.get("boardWriter"));
			row.put("boardFileName", item.get("boardFileName"));
			
			//날짜 포맷팅
			Date boardCreateTime = (Date)item.get("boardCreateTime");
	        LocalDateTime localDateTimeC = convertToLocalDateTimeViaInstant(boardCreateTime);
	        DateTimeFormatter formatterC = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	        String boardCreateTimeformat = localDateTimeC.format(formatterC);
			row.put("boardCreateTime", boardCreateTimeformat);
			
			Date boardModifiTime = (Date)item.get("boardCreateTime");
	        LocalDateTime localDateTimeM = convertToLocalDateTimeViaInstant(boardModifiTime);
	        DateTimeFormatter formatterM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	        String boardModifiTimeformat = localDateTimeM.format(formatterM);
			row.put("boardModifiTime", boardModifiTimeformat);
			
			rows.add(row);
		}
		response.put("rows", rows);
		return response;
	}
	 
	 // 자바단에서 오라클 데이터 -> 몽고 db 삽입
	 @RequestMapping(value ="/saveToMongoDB", method = RequestMethod.POST)
	 public ResponseEntity saveToMongoDB() throws IOException {
		 System.out.println("boardSaveMongo 저장 컨트롤러");
		 try {
			 	List<boardVO> list = boardDao.getBoardsOracle();
		        boardDao.saveMongoDB(list);
		        System.out.println("성공");
		        return ResponseEntity.ok().build(); 
		    } catch (RuntimeException e) {
		        e.printStackTrace();
		        System.out.println("실패");
		        return ResponseEntity.status(500).body("{\"message\":\"" + e.getMessage() + "\"}"); 
		    }
	 }
	 
	 // jsp 화면단에서 오라클 데이터 -> 몽고 db 삽입
	 /* 
	  
	 @RequestMapping(value = "/board", method = RequestMethod.GET)
     public String board(Model model) {
        List<boardVO> list = boardDao.getBoardsOracle();
        model.addAttribute("list", list);
        System.out.println("보드리스트 컨트롤러");
        return "board/boardList";
    }	
	  
	 @RequestMapping(value ="/saveToMongoDB", method = RequestMethod.POST)
	 public ResponseEntity saveToMongoDB(@RequestBody List<boardVO> list) throws IOException {
		 System.out.println("boardSaveMongo 저장 컨트롤러");
		 try {
		        boardDao.saveMongoDB(list);
		        System.out.println("성공");
		        return ResponseEntity.ok().build(); 
		    } catch (RuntimeException e) {
		        e.printStackTrace();
		        System.out.println("실패");
		        return ResponseEntity.status(500).body(e.getMessage()); 
		    }
	 }
	 */
	 
		// Date to LocalDateTime
		public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
		    return dateToConvert.toInstant()
		      .atZone(ZoneId.systemDefault())
		      .toLocalDateTime();
		}

}
