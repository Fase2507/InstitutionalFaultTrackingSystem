<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Kayıt Ol</title>
</head>
<body>
<h2>Kayıt Ol</h2>

<c:if test="${not empty error}">
    <p style="color:red">${error}</p>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/auth/register">
    <div>
        <label>Ad</label>
        <input name="firstName" required/>
    </div>
    <div>
        <label>Soyad</label>
        <input name="lastName" required/>
    </div>
    <div>
        <label>Email (@duzce.edu.tr)</label>
        <input name="email" type="email" required/>
    </div>
    <div>
        <label>Şifre</label>
        <input name="password" type="password" required/>
    </div>
    <button type="submit">Kayıt Ol</button>
</form>

<p><a href="${pageContext.request.contextPath}/auth/login">Giriş Yap</a></p>
</body>
</html>
