package com.oracle.mongo.board;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.Mongo;
import com.mongodb.client.MongoClients;
import com.oracle.mongo.oracle.OracleConnect;

@Repository
public class boardDao {
	
	
//	
//	@Autowired(required=false)
//	private GridFsTemplate gridFsTemplate;
//	
//	@Autowired
//	private MongoTemplate mongoTemplate;
//	
//	
//	private Connection conn = null;
//	private PreparedStatement psmt = null;
//	private ResultSet rs = null;
//	
//	/*
//	 * 오라클 드라이버 연결
//	 */
//	public void connect() {
//        try {
//            Class.forName("oracle.jdbc.driver.OracleDriver");
//            conn = DriverManager.getConnection(OracleConnect.URL, OracleConnect.USER, OracleConnect.PASSWORD);
//        } catch (ClassNotFoundException e) {
//            System.out.println("드라이버 로딩 실패");
//            e.printStackTrace();
//        } catch (SQLException e) {
//            System.out.println("DB 연결 실패");
//            e.printStackTrace();
//        }
//    }
//
//	/*
//	 * 오라클 드라이브 연결해제
//	 */
//    public void disconnect() {
//        if (rs != null) {
//            try {
//                rs.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        if (psmt != null) {
//            try {
//            	psmt.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        if (conn != null) {
//            try {
//                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    
//    /*
//     * 오라클 sql에서 게시판 리스트 읽어오기
//     */
//    public List<boardVO> getBoardsOracle() {
//    	connect();
//    	List<boardVO> list = new ArrayList<boardVO>();
//    	String sql = "select * from BOARD";
//    	try {
//    		 psmt = conn.prepareStatement(sql);
//    	     rs =  psmt.executeQuery();
//    	        
//    		while (rs.next()) {
//    			boardVO board = new boardVO();
//	            board.setBoardNo(rs.getInt("boardNo"));
//	            board.setBoardTitle(rs.getString("boardTitle"));
//	            board.setBoardContent(rs.getString("boardContent"));
//	            board.setBoardWriter(rs.getString("boardWriter"));
//	            board.setBoardFileName(rs.getString("boardFileName"));
//	            board.setBoardFile(rs.getBlob("boardFile"));
//	            board.setBoardCreateTime(rs.getTimestamp("boardCreateTime"));
//	            board.setBoardModifiTime(rs.getTimestamp("boardModifiTime"));
//	            list.add(board);
//    		}
//    	} catch(SQLException e) {
//    		e.printStackTrace();
//    	} finally {
//    		disconnect();
//    	}
//    	return list;
//    }
//    
//    /*
//     * 몽고 DB에서 게시판 리스트 읽어오기
//     */
//    public List<Map<String, Object>> getBoardsMongo() {
//    	System.out.println(getClass().getSimpleName() + ".list()");
//    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//    	
//    	// 번호순으로 정렬
//    	Query query = new Query().with(new Sort(Direction.DESC, "boardNo"));   
//    	// OMCBoard를 찾아서 docs에 저장
//    	List<Document> docs = mongoTemplate.find(query, Document.class, "OMCBoard");
//    	
//    	for (Document doc : docs) {
//    		Map<String, Object> map = new HashMap<String, Object>();
//    		// JSON 형태이기 때문에 map으로 저장
//    		map.putAll(doc);
//    		// 리스트(맵)에 저장
//    		list.add(map);
//    	}
//    	
//    	return list;
//    }
//    
//    
//    /*
//     * 게시판 리스트를 몽고 DB로 전송
//     */
//    public void saveMongoDB(List<boardVO> list) throws IOException {
//
//        System.out.println("mongosave dao 몽고템플릿 소환 후");
//
//        for (boardVO boardList : list) {
//            // MongoDB에서 해당 게시글 번호의 게시글이 이미 존재하는지 확인
//            Query query = new Query();
//            query.addCriteria(Criteria.where("boardNo").is(boardList.getBoardNo()));
//
//            Update update = new Update();
//            update.set("boardTitle", boardList.getBoardTitle());
//            update.set("boardContent", boardList.getBoardContent());
//            update.set("boardWriter", boardList.getBoardWriter());
//            update.set("boardFileName", boardList.getBoardFileName());
//            update.set("boardCreateTime", boardList.getBoardCreateTime());
//            update.set("boardModifiTime", boardList.getBoardModifiTime());
//
//            // 해당 데이터가 존재하면 업데이트, 존재하지 않으면 새로운 데이터를 삽입
//            mongoTemplate.upsert(query, update, "OMCBoard");
//        }
//    }
//    
//    
//    

}
