package model;

import java.util.HashMap;
import java.util.List;

public interface BoardDao {
	
	//전체 글 개수를 알아오는 메서드
	public abstract int getArticleCount();
	//글 목록을 가져와서 List로 변환하는 메서드
	public abstract List<BoardDto> getArticles(HashMap<String, Integer> map);	
	//글 저장을 처리하는 메서드
	public abstract void insertArticle(BoardDto article);
	//글 내용을 가져오는 메서드
	public abstract BoardDto getArticle(int num);
	//글 수정을 처리할 글의 세부 데이터를 받아올 수 있는 방법
	public abstract BoardDto updateGetArticle(int num);
	//글 수정 처리할 메서드
	public abstract int updateArticle(BoardDto article);
	//DB에서 글을 삭제하는 메서드
	public abstract int deleteArticle(BoardDto article);	
}
