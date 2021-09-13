package model;

import java.io.File;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BoardDaoImpl implements BoardDao {
	private SqlSessionTemplate sqlSessionTemplate;

	public BoardDaoImpl(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	public void setSqlMapClient(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	// 전체 글 개수를 알아오는 메서드
	@Override
	public int getArticleCount() {
		return sqlSessionTemplate.selectOne("count");
	}

	// 글 목록을 가져와서 List로 변환하는 메서드
	@Override
	public List<BoardDto> getArticles(HashMap<String, Integer> map) {
		return sqlSessionTemplate.selectList("list", map);
	}

	// 글 저장을 처리하는 메서드
	@Override
	public void insertArticle(BoardDto article) {
		int num = article.getNum();
		int ref = article.getRef();
		int step = article.getStep();
		int depth = article.getDepth();
		int number = 0;
		
		if(sqlSessionTemplate.selectOne("selectNum") != null) {
		number = sqlSessionTemplate.selectOne("selectNum");
		}
		
		System.out.println(num);
		System.out.println(number);
		
		if (number != 0) {
			number += 1;
		} else {
			number = 1;
		}
		if (num != 0) {
			sqlSessionTemplate.update("replyUpdate", article);
			step = step + 1;
			depth = depth +1;
		} else {
			ref = number;
			step = 0;
			depth = 0;
		}
		article.setRef(ref);
		article.setStep(step);
		article.setDepth(depth);
		sqlSessionTemplate.insert("insert", article);
	}

	// 글 내용을 가져오는 메서드
	@Override
	public BoardDto getArticle(int num) {
		sqlSessionTemplate.update("readcountUpdate", num);
		return sqlSessionTemplate.selectOne("articleSelect", num);
	}

	// 글 수정을 처리할 글의 세부 데이터를 받아올 수 있는 방법
	@Override
	public BoardDto updateGetArticle(int num) {
		return sqlSessionTemplate.selectOne("articleSelect", num);
	}

	// 글 수정 처리할 메서드
	@Override
	public int updateArticle(BoardDto article) {
		return sqlSessionTemplate.update("articleUpdate", article);
	}

	// DB에서 글을 삭제하는 메서드
	@Override
	public int deleteArticle(BoardDto article) {
		return sqlSessionTemplate.delete("deleteArticle", article);
	}
}
