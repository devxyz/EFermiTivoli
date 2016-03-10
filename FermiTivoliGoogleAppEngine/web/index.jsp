<%--
  Created by IntelliJ IDEA.
  User: stefano
  Date: 29/07/15
  Time: 23:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<a href="/JSonCircolariServlet" target="_blank">Elenco circolari (JSON)</a>
<br>
<br>
<a href="/PrintCircolariServlet" target="_blank">Elenco circolari (TABLE)</a>
<br>
<br>
<a href="/UpdateCircolariServlet?secret-key=$123$&sync=true" target="_blank">Aggiornamento circolari</a>
<br>
<br>
<a href="/InvalidateServlet?secret-key=$123$" target="_blank">Invalidate cache</a>
<br>
<br>

<form method="get" target="_blank" action="/DownloadCircolareServlet">
    url:
    <input type="text" name="url" maxlength="1025" size="400">
    <input type="hidden" name="secret-key" value="$123$">
    <input type="submit">
</form>
</body>
</html>
