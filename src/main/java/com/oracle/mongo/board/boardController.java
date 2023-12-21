package com.oracle.mongo.board;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;

import com.oracle.mongo.oracle.OracleConnect;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class boardController{

	//@Autowired
	//private MongoTemplate mongoTemplate;
	
	private Connection conn = null;
	private PreparedStatement psmt = null;
	private ResultSet rs = null;
	
	MongoClient mongoClient = null;
	MongoDatabase database = null;
	MongoCollection<Document> collection = null;
	
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
	
	
	
	/*
	 * boardList
	 */
	@RequestMapping("/board")
	public String board() {
	    return "board/boardList";
	}

	/*
	 * boardList에 필요한 데이터를 몽고 DB에서 호출하기 위한 메서드
	 */
	@RequestMapping("/board/data")
	@ResponseBody
	public JSONObject boardMongoList() {
		
		List<Map<String, Object>> list = new ArrayList<>();
		
		
		//List<Map<String, Object>> list = boardDao.getBoardsMongo();
		/* Query query = new Query().with(new Sort(Direction.DESC, "boardNo"));   */
		// OMCBoard를 찾아서 docs에 저장
		//List<Document> docs = mongoTemplate.find(query, Document.class, "OMCBoard");
		try {
	        mongoClient = MongoClients.create("mongodb://localhost:27017");
	        database = mongoClient.getDatabase("OMCBoard"); 
	        collection = database.getCollection("OMCBoard"); 
			
	        // 번호순으로 정렬하여 문서 검색
//	        List<Document> docsList = collection.find()
//                    .sort(Sorts.descending("boardNo"))
//                    .into(new ArrayList<>());
	        MongoIterable<Document> docList = collection
	        		.find()
	        		.sort(Sorts.descending("boardNo"));

	        for (Document doc : docList) {
	            Map<String, Object> map = new HashMap<>();
	            map.putAll(doc);
	            list.add(map);
	        }
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
	        if (mongoClient != null) {
	            mongoClient.close(); 
	        }
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
			Map<String, Object> item = list.get(i);
			row.put("id", i+1);
			row.put("boardNo", item.get("boardNo"));
			row.put("boardTitle", item.get("boardTitle"));
			row.put("boardContent", item.get("boardContent"));
			row.put("boardWriter", item.get("boardWriter"));
			row.put("boardFileName", item.get("boardFileName"));
			row.put("boardFile", item.get("boardFile"));
			row.put("boardCreateTime", item.get("boardCreateTime"));
			row.put("boardModifiTime", item.get("boardModifiTime"));
			
			rows.add(row);
		}
		response.put("rows", rows);
		return response;
	}
	 
	/*
	 * 자바 단에서 오라클 데이터를 몽고 DB에 삽입하기 위한 메서드
	 */
	 @RequestMapping(value ="/saveToMongoDB", method = RequestMethod.POST)
	 public ResponseEntity saveToMongoDB() throws IOException {
		System.out.println("boardSaveMongo 저장 컨트롤러");
		
		try {
			connect();
			
			try {
				mongoClient = MongoClients.create("mongodb://localhost:27017");
		        database = mongoClient.getDatabase("OMCBoard"); 
		        collection = database.getCollection("OMCBoard");
		        
		        List<WriteModel<Document>> upsertData = new ArrayList<>();
		        String sql = "SELECT * FROM BOARD";
		        
		        try {
		        	psmt = conn.prepareStatement(sql);
		        	rs = psmt.executeQuery();
		        	
		        	while (rs.next()) {
		        		int boardNo = rs.getInt("boardNo");
		                String boardTitle = rs.getString("boardTitle");
		                String boardContent = rs.getString("boardContent");
		                String boardWriter = rs.getString("boardWriter");
		                String boardFileName = rs.getString("boardFileName");
		                Timestamp boardCreateTimeStamp = rs.getTimestamp("boardCreateTime");
		                Timestamp boardModifiTimeStamp = rs.getTimestamp("boardModifiTime");

		                String boardCreateTime = boardCreateTimeStamp != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(boardCreateTimeStamp) : "null";
		                String boardModifiTime = boardModifiTimeStamp != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(boardModifiTimeStamp) : "null";
		                
		                // MongoDB Document를 업데이트하기 위해 $set 연산자를 사용하는 Document 객체를 생성합
		                // 이 Document 객체에는 업데이트하고자 하는 필드와 그에 해당하는 새로운 값들이 포함.
		                Document upsertDoc = new Document();
		                // put 방식
		                upsertDoc.put("boardTitle", boardTitle);
		                upsertDoc.put("boardContent", boardContent);
		                upsertDoc.put("boardWriter", boardWriter);
		                upsertDoc.put("boardFileName", boardFileName);
		                upsertDoc.put("boardCreateTime", boardCreateTime);
		                upsertDoc.put("boardModifiTime", boardModifiTime);
		                Document upsertDocs = new Document("$set", upsertDoc);
		                
		                // append 방식
//		                Document upsertDocs = new Document("$set", upsertDocs)
//		                		.append("boardTitle", boardTitle)
//		                		.append("boardContent", boardContent)
//		                		.append("boardWriter", boardWriter)
//		                		.append("boardFileName", boardFileName)
//		                		.append("boardCreateTime", boardCreateTime)
//		                		.append("boardModifiTime", boardModifiTime));
		                
		                // UpdateOneModel 객체를 생성하여 'upsertData' 리스트에 추가.
		                // 이 객체는 단일 문서에 대한 업데이트 작업을 나타냄
		                upsertData.add(new UpdateOneModel<>(
		                		// "boardNo" 필드가 'boardNo' 변수에 담긴 값과 일치하는 문서를 찾움
		                        Filters.eq("boardNo", boardNo), 
		                        // 위에서 생성한 업데이트 문서를 사용
		                        upsertDocs, 
		                        // 업데이트 옵션을 설정하여, 일치하는 문서가 없으면 새로운 문서를 삽입
		                        new UpdateOptions().upsert(true)
		                ));
		        	}
		        	
		        	// 'upsertData' 리스트가 하나 이상의 업데이트 작업이 있다면,
		        	// bulkWrite 메서드를 호출하여 모든 업데이트 작업을 실행.
		        	if (!upsertData.isEmpty()) {
		        		// 업데이트 작업을 데이터베이스에 실행
		        		collection.bulkWrite(upsertData);
		        		upsertData.clear();
		        	}
		        	
		        } catch(SQLException e) {
		        	e.printStackTrace();
		        	return ResponseEntity.status(500).body("{\"message\":\"" + e.getMessage() + "\"}");
		        } 
		        
		        System.out.println("성공");
		        return ResponseEntity.ok().build();
		        
				
			} catch(Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(500).body("{\"message\":\"" + e.getMessage() + "\"}");
			} finally {
				if (mongoClient != null) {
		            mongoClient.close(); 
		        }
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("{\"message\":\"" + e.getMessage() + "\"}");
		} finally {
			disconnect();
		}
	 }		
		
		/*
		try {
			 
		 	// boardDAo를 안쓰고 컨트롤러에서 적용되도록 해보아라
		 	
		 	// List<boardVO> list = boardDao.getBoardsOracle();
		 	// boardDao.saveMongoDB(list);
		    connect();
		    JSONObject jsonDate = new JSONObject();
		    ArrayList<WriteModel<Document>> jsonList = new ArrayList<>();
		 	//List<String> list = new ArrayList<String>();
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
			 	     String boardFileName = rs.getString("boardFileName");
			 	     // Blob boardFile = rs.getBlob("boardFile");
			 	     String boardCreateTime = boardCreateTimeStamp != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(boardCreateTimeStamp) : "null";
			 	     String boardModifiTime = boardModifiTimeStamp != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(boardModifiTimeStamp) : "null";

			 	     Update update = new Update();
			 	     
			 	     //update.set("boardTitle", boardTitle);
			 	     //update.set("boardContent", boardContent);
			 	     //update.set("boardWriter", boardWriter);
			 	     //update.set("boardFileName", boardFileName);
			 	     //update.set("boardFile", boardFile);
			 	     //update.set("boardCreateTime", boardCreateTime);
			 	     //update.set("boardModifiTime", boardModifiTime);
			 	     
			 	     
			 	     jsonDate.put(boardTitle, update.set("boardTitle", boardTitle));
			 	     jsonDate.put(boardContent, update.set("boardContent", boardContent));
			 	   	 jsonDate.put(boardWriter, update.set("boardWriter", boardWriter));
			 	  	 jsonDate.put(boardFileName, update.set("boardFileName", boardFileName));
				 	 //jsonDate.put(boardFile, update.set("boardFile", boardFile));
				 	 jsonDate.put(boardCreateTime, update.set("boardCreateTime", boardCreateTime));
				 	 jsonDate.put(boardModifiTime, update.set("boardModifiTime", boardModifiTime));
				 	 
				 	
				 	 
			 	     // 해당 데이터가 존재하면 업데이트, 존재하지 않으면 새로운 데이터 삽입
			 	     mongoTemplate.upsert(query, update, "OMCBoard");
		 		     // jsonList.add(jsonDate);
		 		     // 모든 값을 하나의 문자열로 합침
		 		     // String record = boardNo + "," + boardTitle + "," + boardContent + "," + boardWriter + "," + boardFileName + "," + boardFile + "," + boardCreateTime + "," + boardModifiTime;
		 			 //list.add(record);
		 		 }
			 		
		 	} catch(SQLException e) {
		 	 	 e.printStackTrace();
		 	} finally {
		 		 disconnect();
		 	}
		 	 
		 	System.out.println("saveMongoDB");
		 	
		 	
		 	
//		 	 이 포맷을 list를 호출하면서 한번에 처리 가능
//		 	for (String boardRecord : list) {
//		 	    // 문자열을 콤마로 나누고 배열값을 가져옴
//		 	    String[] mongoData = boardRecord.split(",");
//
//		 	    // 변수를 설정하여 배열 순서대로 할당
//		 	    int boardNo = Integer.parseInt(mongoData[0]);
//		 	    String boardTitle = mongoData[1];
//		 	    String boardContent = mongoData[2];
//		 	    String boardWriter = mongoData[3];
//		 	    String boardFilename = mongoData[4];
//		 	    String boardFile = mongoData[5];
//		 	    String boardCreateTime = mongoData[6];
//		 	    String boardModifiTime = mongoData[7];
//
//		 	    // MongoDB에서 해당 게시글 번호의 게시글이 이미 존재하는지 확인
//		 	    Query query = new Query();
//		 	    query.addCriteria(Criteria.where("boardNo").is(boardNo));
//
//		 	    Update update = new Update();
//		 	    update.set("boardTitle", boardTitle);
//		 	    update.set("boardContent", boardContent);
//		 	    update.set("boardWriter", boardWriter);
//		 	    update.set("boardFileName", boardFilename);
//		 	    update.set("boardFile", boardFile);
//		 	    update.set("boardCreateTime", boardCreateTime);
//		 	    update.set("boardModifiTime", boardModifiTime);
//
//		 	    // 해당 데이터가 존재하면 업데이트, 존재하지 않으면 새로운 데이터 삽입
//		 	    mongoTemplate.upsert(query, update, "OMCBoard");
//		 	}
//		 	
		 	
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
	  
	  */
	
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
	 
	 // LocalDateTime으로 변환하기 위한 메서드
	 public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
	     return dateToConvert.toInstant()
	       .atZone(ZoneId.systemDefault())
	       .toLocalDateTime();
	 }

}
