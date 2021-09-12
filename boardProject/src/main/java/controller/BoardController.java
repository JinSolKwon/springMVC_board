package controller;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import model.BoardDto;
import model.BoardService;

@Controller
@SessionAttributes("BoardDto")
public class BoardController{
	private BoardService boardService;
	
	private static final String FILE_PATH = "d:\\javastudy\\jspupload";

	public void setBoardService(BoardService boardService) {
		this.boardService = boardService;
	}

	@RequestMapping(value="/board/list")
	public String list(@RequestParam(value = "pageNum", required =false, defaultValue = "0")int pageNum ,
			Model model) {
		if (pageNum == 0) {
			pageNum = 1;
		}
		int pageSize = 10; //한 페이지당 글의 개수
		int currentPage = pageNum;
		// 페이지의 시작 글 번호
		
		int startRow = (currentPage - 1) * pageSize + 1;
		int endRow = currentPage * pageSize;	//한 페이지의 마지막 글 번호
		int count = 0;
		int number = 0;
		List<BoardDto> articleList = null;
		count = boardService.articleCount();	// 전체 글 개수
		if(count > 0) {
			articleList = boardService.list(startRow, endRow);
		} else {
			articleList = Collections.emptyList();
		}
		number = count - (currentPage - 1) * pageSize;	//글 목록에 표시할 글 번호
		 
		model.addAttribute("currentPage",new Integer(currentPage));
		model.addAttribute("startRow",new Integer(startRow));
		model.addAttribute("endRow",new Integer(endRow));
		model.addAttribute("count",new Integer(count));
		model.addAttribute("pageSize",new Integer(pageSize)); 
		model.addAttribute("number",new Integer(number));
		model.addAttribute("articleList",articleList);
		
		return "/board/list";
	}
	
	@RequestMapping(value="/board/content/{num},{pageNum}")
	public String read(Model model, @PathVariable int num) { 
		model.addAttribute("article", boardService.read(num));
		return "/board/content";
	}
	
	@GetMapping(value="/board/write")
	public String write(Model model,
			@RequestParam(value = "num", required =false, defaultValue = "0")int num,
			@RequestParam(value = "ref", required =false, defaultValue = "1")int ref, 
			@RequestParam(value = "step", required =false, defaultValue = "0")int step,
			@RequestParam(value = "depth", required =false, defaultValue = "0")int depth,
			HttpServletRequest req
			) {
		
		
		if (req.getParameter("num") != null) {
			num = Integer.parseInt(req.getParameter("num"));
			ref = Integer.parseInt(req.getParameter("ref"));
			step = Integer.parseInt(req.getParameter("step"));
			depth = Integer.parseInt(req.getParameter("depth"));
		}
		
		BoardDto dto = new BoardDto();
		dto.setNum(num);
		dto.setRef(ref);
		dto.setStep(step);
		dto.setDepth(depth);
		
		model.addAttribute("article", dto);
		return "/board/write";
	}
	
	@PostMapping(value="/board/write")
	public String write(@RequestParam("file")MultipartFile file, @Valid BoardDto boardDto, BindingResult bindingResult, HttpServletRequest req) 
																			throws IllegalStateException, IOException{
		String ip = req.getRemoteAddr();
		String fileName = file.getOriginalFilename();
		long size = file.getSize();
		
		if(!file.getOriginalFilename().isEmpty()) {
			file.transferTo(new File(FILE_PATH, fileName));
			
			boardDto.setFilename(fileName);
			boardDto.setFilesize(size);
			boardDto.setIp(ip);
		} 
		
		if(bindingResult.hasErrors()) {
			return "/board/write"; 
		}
		boardService.write(boardDto);
		return "redirect:/board/list";
	}
	
	@GetMapping(value="/board/edit/{num}")
	public String edit(@PathVariable int num, Model model) {
		BoardDto boardDto = boardService.editRead(num);
		model.addAttribute("article", boardDto);
		
		return "board/edit";
	}
	
	@PostMapping(value="/board/edit")
	public String edit(@Valid @ModelAttribute BoardDto boardDto, BindingResult result, String pwd, SessionStatus sessionStatus, Model model) {
		if(result.hasErrors()) {
			return "/board/edit";
		} else {			 
			if(boardDto.getPass().equals(pwd)) {
			boardService.edit(boardDto);
			sessionStatus.setComplete();   
			return "redirect:/board/content/{num},{currentPage}";
			}
		}
		model.addAttribute("msg", "비밀번호가 일치하지 않습니다.");
		return "redirect:/board/edit/{num}"; 
	}
	
	@GetMapping(value="/board/delete/{num}")
	public String delete(@PathVariable int num, Model model) {
		model.addAttribute("num", num);
		return "/board/delete";
	}
	
	@PostMapping(value="/board/delete/{num}")
	public String delete(int num, String pwd, Model model) {
		int rowCount;
		BoardDto boardDto = new BoardDto();
		boardDto.setNum(num);
		boardDto.setPass(pwd);
		System.out.println(boardDto.getNum());
		System.out.println(boardDto.getPass()); 
		
		rowCount = boardService.delete(boardDto);
		
		if(rowCount == 0) {
			model.addAttribute("num", num);
			model.addAttribute("msg", "비밀번호가 일치하지 않습니다.");
			return "/board/delete";
		} else { 
			return "redirect:/board/list"; 
		}
	}
}