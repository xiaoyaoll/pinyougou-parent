<%--
  Created by IntelliJ IDEA.
  User: Lenovo
  Date: 2018/7/28
  Time: 18:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <form action="/login" method="post">
        <!--认证token,解决CSRF攻击-->
        <%--<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>--%>
        用户名:<input name="username"><br/>
        密码:<input name="password"><br/>
        <input type="submit">
    </form>
</body>
</html>
