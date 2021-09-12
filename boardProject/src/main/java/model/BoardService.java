package model;

import java.util.List;

public interface BoardService {
	
	public abstract int articleCount(); 
	
	public abstract List<BoardDto> list(int start, int end);
	
	public abstract int delete(BoardDto boardDto);
	
	public abstract BoardDto editRead(int num);
	
	public abstract int edit(BoardDto boardDto);
	
	public abstract void write(BoardDto boardDto);
	
	public abstract BoardDto read(int num);
}
