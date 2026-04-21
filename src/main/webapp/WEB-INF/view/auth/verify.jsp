<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Email Doğrulama</title>
</head>
<body>
<h2>Email Doğrulama</h2>

<c:if test="${not empty info}">
    <p style="color:green">${info}</p>
</c:if>
<c:if test="${not empty error}">
    <p style="color:red">${error}</p>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/auth/verify">
    <div>
        <label>Email</label>
        <input name="email" type="email" value="${email}" required/>
    </div>
    <div>
        <label>Kod</label>
        <input name="code" inputmode="numeric" pattern="[0-9]{6}" required/>
    </div>
    <button type="submit">Doğrula</button>
</form>

<p><a href="${pageContext.request.contextPath}/auth/login?email=${email}">Giriş Sayfası</a></p>
</body>
</html>
