package model;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardServiceImpl implements BoardService{
	
	@Autowired
	private BoardDao boardDao;
	
	public BoardServiceImpl(BoardDao boardDao) {
		this.boardDao = boardDao;
	}
	
	public BoardDao getBoardDao() {
		return boardDao;
	}
	
	public void setBoardDao(BoardDao boardDao) {
		this.boardDao = boardDao;
	}
	
	// 전체 글 개수를 알아오는 메서드
	@Override
	public int articleCount() {
		return boardDao.getArticleCount();
	}
	
	// 글 목록을 가져와서 List로 변환하는 메서드
	@Override
	public List<BoardDto> list(int start, int end){
		HashMap<String,Integer> map = new HashMap<String, Integer>();
		map.put("start", start);
		map.put("end", end);
		
		return boardDao.getArticles(map);
	}
	
	// DB에서 글을 삭제하는 메서드
	@Override
	public int delete(BoardDto boardDto) {
		return boardDao.deleteArticle(boardDto);
	}
	
	// 글 수정을 처리할 글의 세부 데이터를 받아올 수 있는 방법
	@Override
	public BoardDto editRead(int num) {
		return boardDao.updateGetArticle(num);
	}
	
	// 글 수정 처리할 메서드
	@Override
	public int edit(BoardDto boardDto) {
		return boardDao.updateArticle(boardDto);
	}
	
	// 글 저장을 처리하는 메서드
	@Override
	public void write(BoardDto boardDto) {
		boardDao.insertArticle(boardDto);
	}
	
	//글 내용을 가져오는 메서드
	@Override
	public BoardDto read(int num) {
		return boardDao.getArticle(num);
	}
}
