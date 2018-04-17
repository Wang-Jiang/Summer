<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>上传文件</title>
</head>
<body>
<h1>上传文件</h1>

<form enctype="multipart/form-data" method="post" action="/uploadAction?urlParam=aaaa">
<%--<form method="post" action="/uploadAction?urlParam=aaaa">--%>
    <input name="formParam">
    <br>
    <input type="file" name="file1" multiple>
    <br>
    <input type="file" name="file2">
    <br>
    <input type="text" name="urlParam">
    <br>
    <input type="submit">
</form>

</body>
</html>
