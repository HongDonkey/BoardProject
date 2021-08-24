<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<title>Bootstrap Example</title>
<meta charset="utf-8">
<meta name="viewport" content="width--device-width, initial-scale=1">
<link rel="stylesheet" href="/css/bootstrap/css/bootstrap.min.css">
<script src="/js/jquery-3.5.1.min.js"></script>
<script src="/css/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javaScript" language="javascript" defer="defer">
	function add() {
		if ($("#title").val() == '') {
			alert("제목을 입력하세요");
			$("#title").focus();
			return;
		}
		if ($("#contents").val() == '') {
			alert("내용을 입력하세요");
			$("#contents").focus();
			return;
		}
		if (!confirm("등록하시겠습니까?")) {
			return;
		}

		document.boardRegForm.action = "<c:url value='mgmt.do'/>?mode=add";
		document.boardRegForm.submit();

	}
	function cancel() {
		location.href = "<c:url value='/mainList.do'/>";
	}
	$(document).ready(function() {
		$("#idx").attr("readonly", true);
		$("#writerNm").attr("readonly", true);
		$("#indate").attr("readonly", true);
	})

	function modify() {

		if ($("#title").val() == '') {
			alert("제목을 입력하세요");
			$("#title").focus();
			return;
		}
		if ($("#contents").val() == '') {
			alert("내용을 입력하세요");
			$("#contents").focus();
			return;
		}
		if (!confirm("수정 하시겠습니까?")) {
			return;
		}
		document.boardRegForm.action = "<c:url value='mgmt.do'/>?mode=modify";
		document.boardRegForm.submit();
	}
</script>
</head>

<div class="container">
	<h1>메인화면</h1>
	<div class="panel panel-default">

		<div class="panel-heading">
			<label for="">생각중</label>
		</div>
		<div class="panel-body">
			<form class="form-horizontal" id="boardRegForm" name="boardRegForm"
				method="post" action="/">
				<div class="form-group">
					<label class="control-label col-sm-2" for="idx">게시물아이디:</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" id="idx" name="idx"
							placeholder="자동발번" value="${boardVO.idx }">
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-sm-2" for="pwd">제목:</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" id="title" name="title"
							placeholder=" 제목을 입력하세요" maxLength="100"
							value="${boardVO.title }">
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-sm-2" for="pwd">등록자/등록일:</label>
					<div class="col-sm-10">
						<input type="hidden" class="form-control" id="writer"
							name="writer" placeholder="등록자를 입력하세요" maxLength="15"
							style="float: left; width: 40%" value="${boardVO.writer }">
						<input type="text" class="form-control" id="writerNm"
							name="writerNm" placeholder="등록자를 입력하세요" maxLength="15"
							style="float: left; width: 40%" value="${boardVO.writerNm }">
						<c:set var="indate" value="${boardVO.indate}" />
						<c:if test="fn:length(indate >9)">
							<c:set var="indate"
								value="${fn:substring(indate,0, fn:length(indate)-2) }" />
						</c:if>

						<input type="text" class="form-control" id="indate" name="indate"
							placeholder="등록일을 입력하세요" maxLength="10"
							style="float: left; width: 40%"
							value="${indate }">

					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-sm-2" for="pwd">내용:</label>
					<div class="col-sm-10">
						<textarea class="form-control" rows="5" id="contents"
							name="contents" maxlength="1000">${boardVO.contents }</textarea>
					</div>
				</div>
			</form>
		</div>

		<div class="panel-footer">
			<c:if test="${!empty sessionScope.userId }">

				<button type="button" class="btn btn-default" onclick="add();">등록</button>
				<button type="button" class="btn btn-default" onclick="modify();">수정</button>
			</c:if>

			<button type="button" class="btn btn-default" onclick="cancel();">취소</button>
		</div>
	</div>
</div>
</html>
