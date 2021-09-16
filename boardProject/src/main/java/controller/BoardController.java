package controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	private static final String FILE_PATH ="d:\\javastudy\\jspupload";
	 
	public void setBoardService(BoardService boardService) {
		this.boardService = boardService;  
	} 
	 
	@RequestMapping(value="/board/list") 
	public String list(@RequestParam(name="pageNum", required=false, defaultValue="0")int pageNum ,
			Model model, HttpSession session) {
		   
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
		
		session.setAttribute("pageNum", pageNum);
		
		return "/board/list";
	}
	
	@RequestMapping(value="/board/download/{num}")
	public void download(@PathVariable int num ,HttpServletResponse resp, HttpServletRequest req) {
		BoardDto vo = boardService.read(num);
		String filename = vo.getFilename();
		
		File downFile = new File(FILE_PATH + "\\"+ filename);
		
		if (downFile.exists() && downFile.isFile()) {
			try {
				filename = URLEncoder.encode(filename, "utf-8").replaceAll("\\+","%20");
				long filesize = downFile.length();
				
				resp.setContentType("application/octet-stream; charset=utf-8");
				resp.setContentLength((int) filesize);
				String strClient = req.getHeader("user-agent");
				
				if (strClient.indexOf("MSIE 5.5") != -1) {
					resp.setHeader("Content-Disposition", "filename="
                            + filename + ";");
                } else {
                	resp.setHeader("Content-Disposition",
                            "attachment; filename=" + filename + ";");
                }
				resp.setHeader("Content-Length", String.valueOf(filesize));
				resp.setHeader("Content-Transfer-Encoding", "binary;");
				resp.setHeader("Pragma", "no-cache");
				resp.setHeader("Cache-Control", "private");
 
                byte b[] = new byte[1024];
 
                BufferedInputStream in = new BufferedInputStream(
                        new FileInputStream(downFile));
 
                BufferedOutputStream out = new BufferedOutputStream(
                		resp.getOutputStream());
 
                int read = 0;
 
                while ((read = in.read(b)) != -1) {
                    out.write(b, 0, read);
                }
                out.flush();
                out.close();
                in.close();
				
			} catch (Exception e) {
				System.out.println("Download Exception : " + e.getMessage());
			}
		} else {
			System.out.println("Download Error : downFile Error [" + downFile + "]");
		}
		 
	}
	
	@RequestMapping(value="/board/content/{num}")
	public String read(Model model, @PathVariable int num, HttpSession req) {
		model.addAttribute("article", boardService.read(num));
		model.addAttribute("pageNum", req.getAttribute("pageNum"));
		return "/board/content";
	}
	
	@GetMapping(value="/board/write")
	public String write(Model model,HttpSession session) {
	
		int num = 0, ref = 1, step = 0,depth = 0; 
		BoardDto dto = new BoardDto();
		dto.setNum(num);
		dto.setRef(ref);
		dto.setStep(step);
		dto.setDepth(depth);
		
		model.addAttribute("article", dto);
		
		return "/board/write";
	}
	
	@GetMapping(value="/board/write/{num}")
	public String write(Model model,
			@PathVariable int num, HttpSession session
			) {
		int pageNum = (int)session.getAttribute("pageNum");
		BoardDto dto = boardService.read(num);
		
		model.addAttribute("article", dto);
		model.addAttribute("pageNum", pageNum);
		return "/board/write";
	}
	
	@PostMapping(value="/board/write")
	public String write(@RequestParam("file")MultipartFile file, @Valid BoardDto boardDto, BindingResult bindingResult,
			HttpServletRequest req, HttpSession session) throws IllegalStateException, IOException{
		String ip = req.getRemoteAddr();
		String fileName = file.getOriginalFilename();
		long size = file.getSize();
		int pageNum = (int)session.getAttribute("pageNum");

		if(!file.getOriginalFilename().isEmpty()) {
			file.transferTo(new File(FILE_PATH, fileName));
			boardDto.setFilename(fileName); 
			boardDto.setFilesize(size);
			boardDto.setIp(ip);
		} else {
			boardDto.setFilename("");
			boardDto.setFilesize(0);
			boardDto.setIp(ip);
		}
		
		if (boardDto.getNum() == 0) {
			session.setAttribute("pageNum", 0);
		}
		
		if(bindingResult.hasErrors()) {
			return "/board/write"; 
		}
		boardService.write(boardDto);
		return "redirect:/board/list?pageNum="+pageNum;
	}
	
	@GetMapping(value="/board/edit/{num}")
	public String edit(@PathVariable int num, Model model, HttpSession session) {
		int pageNum = (int)session.getAttribute("pageNum");
		BoardDto boardDto = boardService.editRead(num);
		model.addAttribute("article", boardDto);
		model.addAttribute("pageNum",pageNum);
		return "board/edit";
	}
	
	@PostMapping(value="/board/edit/{num}")
	public String edit(@Valid @ModelAttribute BoardDto boardDto, BindingResult result, String pwd, SessionStatus sessionStatus, Model model) {
		if(result.hasErrors()) {
			return "/board/edit";
		} else {			 
			if(boardDto.getPass().equals(pwd)) {
			boardService.edit(boardDto);
			sessionStatus.setComplete();   
			return "redirect:/board/content/{num}"; 
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
	public String delete(int num, String pwd, Model model, HttpSession req
			) {
		int pageNum = (int)req.getAttribute("pageNum");
		int rowCount;
		BoardDto boardDto = new BoardDto();
		boardDto.setNum(num);
		boardDto.setPass(pwd); 
		
		
		String filename = boardService.read(num).getFilename();
		
		if(filename == null) {
			filename= "";
		}
		
		if (!filename.equals("")) {
			File dir = new File(FILE_PATH);
			File[] files = dir.listFiles();
			for (File f : files) {
				if (f.getName().equals(filename)) {
					f.delete();
				}
			}
		}
		
		rowCount = boardService.delete(boardDto);
		
		if(rowCount == 0) {
			model.addAttribute("num", num);
			model.addAttribute("msg", "비밀번호가 일치하지 않습니다.");
			return "/board/delete";
		} else { 
			return "redirect:/board/list?pageNum="+pageNum; 
		}
	}
}