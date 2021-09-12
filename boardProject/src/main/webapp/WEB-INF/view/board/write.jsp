<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>자료실</title>
<script src="${pageContext.request.contextPath}/board/script.js"></script>
<link href="${pageContext.request.contextPath}/board/css/style.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/board/css/writeFormstyle.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<section>
<article>
<b>글쓰기</b>
<br></br>
<form method="POST" name="writeForm" action="<c:url value="/board/write"/>" enctype="multipart/form-data"
	onsubmit="return writeSave()">
	<input type="hidden" name="num" value="${article.num}">
	<input type="hidden" name="ref" value="${article.ref}">
	<input type="hidden" name="step" value="${article.step}">
	<input type="hidden" name="depth" value="${article.depth}">
	
	<table class="board">
		<tr>
			<td class="attr">이 름</td>
			<td><input type="text" name="writer"></td>
		</tr>
		<tr>
			<td class="attr">E-mail</td>
			<td><input type="email" name="email"></td>
		</tr>
		<tr>
			<td class="attr">제 목</td>
			<td>
				<c:if test="${article.num == 0}"><input class="input" type="text" name="subject"></c:if>
				<c:if test="${article.num != 0}"><input class="input" type="text" name="subject" value="[답변]"></c:if>
			</td>
		</tr>
		<tr>
			<td class="attr">설 명</td>
			<td><textarea name="content" rows="13" cols="40"></textarea></td>
		</tr>
		<tr>
			<td class="attr">첨부파일</td>
			<td><input type=file name="file"></td>
		</tr>
		<tr>
			<td class="attr">비밀번호</td>
			<td><input type="password" name="pass"></td>
		</tr>
		<tr>
			<td colspan="2" class="attr">
				<input type="submit" value="글쓰기">
				<input type="reset" value="다시작성">
				<input type="button" value="목록" onClick="window.location='<c:url value="/board/list"/>'">
			</td>
		</tr>
		</table>
</form>
</article>
</section>
</body>
</html>