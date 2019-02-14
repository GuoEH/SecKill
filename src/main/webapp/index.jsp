<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
<h2>Index</h2>
<%--<% String basePath = request--%>
<%String path = request.getScheme()+"://"+request.getServerPort()+"/";
%>
<p><a href="/seckill/list">点击前往秒杀列表</a></p>
<%--<p><%=basePath%></p>--%>
<p><%=path%></p>
</body>
</html>
