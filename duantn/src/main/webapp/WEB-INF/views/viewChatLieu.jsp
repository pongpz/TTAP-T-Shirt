<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: duong
  Date: 05/07/2024
  Time: 4:02 CH
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Bootstrap demo</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
</head>
<body class="container">

<form action="/chat-lieu/<c:if test="${not empty detailChatLieu.id}">update?id=${detailChatLieu.id}</c:if><c:if test="${empty detailChatLieu.id}">add</c:if>" method="post">

    <div class="mb-3">
        <label for="ma" class="form-label">Mã chất liệu</label>
        <input type="text" class="form-control" id="ma" name="ma" value="${detailChatLieu.ma}">
    </div>
    <div class="mb-3">
        <label for="ten" class="form-label">Tên chất liệu</label>
        <input type="text" class="form-control" id="ten" name="ten" value="${detailChatLieu.ten}">
    </div>
    <button type="submit" class="btn btn-success">Submit</button>
</form>

<table class="table table-hover">
    <thead>
    <tr>
        <th scope="col">STT</th>
        <th scope="col">Mã</th>
        <th scope="col">Tên</th>
        <th scope="col">Hành động</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${listChatLieu}" var="a" varStatus="i">
        <tr onclick="return location.href='/chat-lieu/detail?id=${a.id}'">
            <th>${i.index+1}</th>
            <td>${a.ma}</td>
            <td>${a.ten}</td>
            <td>
                <a href="/thuong-hieu/delete?id=${a.id}" class="btn btn-danger">Delete</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
