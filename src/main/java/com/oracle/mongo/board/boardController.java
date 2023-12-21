package com.oracle.mongo.board;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.oracle.mongo.oracle.OracleConnect;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class boardController{

	@Autowired
	private MongoTemplate mongoTemplate;
	
	private Connection conn = null;
	private PreparedStatement psmt = null;
	private ResultSet rs = null;
	
	
	/*
	 * 오라클 드라이버 연결
	 */
	public void connect() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("오라클 드라이버 연결 전");
            conn = DriverManager.getConnection(OracleConnect.URL, OracleConnect.USER, OracleConnect.PASSWORD);
            System.out.println("오라클 드라이버 연결 후");
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로딩 실패");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("DB 연결 실패");
            e.printStackTrace();
        }
    }

	/*
	 * 오라클 드라이브 연결해제
	 */
    public void disconnect() {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (psmt != null) {
            try {
            	psmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
	
	
	
	
	@RequestMapping("/board")
	public String board() {
	    return "board/boardList";
	}

	
	@RequestMapping("/board/data")
	@ResponseBody
	public JSONObject boardMongoList() {
		/*
		List<Map<String, Object>> list = boardDao.getBoardsMongo();
		*/
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 번호순으로 정렬
    	Query query = new Query().with(new Sort(Direction.DESC, "boardNo"));   
    	// OMCBoard를 찾아서 docs에 저장
    	List<Document> docs = mongoTemplate.find(query, Document.class, "OMCBoard");
    	
    	for (Document doc : docs) {
    		Map<String, Object> map = new HashMap<String, Object>();
    		// JSON 형태이기 때문에 map으로 저장
    		map.putAll(doc);
    		// 리스트(맵)에 저장
    		list.add(map);
    	}
		
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
			row.put("boardCreateTime", item.get("boardCreateTime"));
			row.put("boardModifiTime", item.get("boardModifiTime"));
			
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
			 
		 	// boardDAo를 안쓰고 컨트롤러에서 적용되도록 해보아라
		 	/*
		 	List<boardVO> list = boardDao.getBoardsOracle();
		 	boardDao.saveMongoDB(list);
		 	*/
		    connect();    
		 	List<String> list = new ArrayList<String>();
		    System.out.println("getBoardsOracle");
		 	String sql = "select * from BOARD";
		 	 
		 	try {
		 		 System.out.println("psmt 설정 전");
		 		 psmt = conn.prepareStatement(sql);
	 			 System.out.println("psmt 설정 후");
		 		 rs = psmt.executeQuery();
		 		
		 		 while (rs.next()) {
		 			
		 			  // Timestamp 호출 후 문자열로 변환
		 			 Timestamp boardCreateTimeStamp = rs.getTimestamp("boardCreateTime");
		 		     Timestamp boardModifiTimeStamp = rs.getTimestamp("boardModifiTime");
		 		     
		 		     int boardNo = rs.getInt("boardNo");;
			 	     String boardTitle =rs.getString("boardTitle");;
			 	     String boardContent = rs.getString("boardContent");
			 	     String boardWriter = rs.getString("boardWriter");
			 	     String boardFilename = rs.getString("boardFileName");
			 	     Blob boardFile = rs.getBlob("boardFile");
			 	     String boardCreateTime = boardCreateTimeStamp != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(boardCreateTimeStamp) : "null";
			 	     String boardModifiTime = boardModifiTimeStamp != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(boardModifiTimeStamp) : "null";

			 	     // MongoDB에서 해당 게시글 번호의 게시글이 이미 존재하는지 확인
			 	     Query query = new Query();
			 	     query.addCriteria(Criteria.where("boardNo").is(boardNo));

			 	     Update update = new Update();
			 	     update.set("boardTitle", boardTitle);
			 	     update.set("boardContent", boardContent);
			 	     update.set("boardWriter", boardWriter);
			 	     update.set("boardFileName", boardFilename);
			 	     update.set("boardFile", boardFile);
			 	     update.set("boardCreateTime", boardCreateTime);
			 	     update.set("boardModifiTime", boardModifiTime);

			 	     // 해당 데이터가 존재하면 업데이트, 존재하지 않으면 새로운 데이터 삽입
			 	     mongoTemplate.upsert(query, update, "OMCBoard");
		 		    
		 		     // 모든 값을 하나의 문자열로 합침
		 		     String record = boardNo + "," + boardTitle + "," + boardContent + "," + boardWriter + "," + boardFilename + "," + boardFile + "," + boardCreateTime + "," + boardModifiTime;
		 			 
		 			 list.add(record);
		 		 }
			 		
		 	} catch(SQLException e) {
		 	 	 	e.printStackTrace();
		 	} finally {
		 		 	disconnect();
		 	}
		 	 
		 	System.out.println("saveMongoDB");
		 	
		 	/* 이 포맷을 list를 호출하면서 한번에 처리 가능
		 	for (String boardRecord : list) {
		 	    // 문자열을 콤마로 나누고 배열값을 가져옴
		 	    String[] mongoData = boardRecord.split(",");

		 	    // 변수를 설정하여 배열 순서대로 할당
		 	    int boardNo = Integer.parseInt(mongoData[0]);
		 	    String boardTitle = mongoData[1];
		 	    String boardContent = mongoData[2];
		 	    String boardWriter = mongoData[3];
		 	    String boardFilename = mongoData[4];
		 	    String boardFile = mongoData[5];
		 	    String boardCreateTime = mongoData[6];
		 	    String boardModifiTime = mongoData[7];

		 	    // MongoDB에서 해당 게시글 번호의 게시글이 이미 존재하는지 확인
		 	    Query query = new Query();
		 	    query.addCriteria(Criteria.where("boardNo").is(boardNo));

		 	    Update update = new Update();
		 	    update.set("boardTitle", boardTitle);
		 	    update.set("boardContent", boardContent);
		 	    update.set("boardWriter", boardWriter);
		 	    update.set("boardFileName", boardFilename);
		 	    update.set("boardFile", boardFile);
		 	    update.set("boardCreateTime", boardCreateTime);
		 	    update.set("boardModifiTime", boardModifiTime);

		 	    // 해당 데이터가 존재하면 업데이트, 존재하지 않으면 새로운 데이터 삽입
		 	    mongoTemplate.upsert(query, update, "OMCBoard");
		 	}
		 	*/
		 	
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
