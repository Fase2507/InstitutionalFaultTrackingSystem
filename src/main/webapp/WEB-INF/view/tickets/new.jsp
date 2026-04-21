<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Yeni Ticket</title>
</head>
<body>
<h2>Yeni Ticket</h2>

<p>Kategori baslik ve aciklamadan otomatik tahmin edilir.</p>

<c:if test="${not empty error}">
    <p style="color:red">${error}</p>
</c:if>

<p><a href="${pageContext.request.contextPath}/auth/login">Giris Yap</a></p>

<form method="post" action="${pageContext.request.contextPath}/tickets">
    <div>
        <label>Baslik</label>
        <input name="title" maxlength="200" required/>
    </div>
    <div>
        <label>Aciklama</label>
        <textarea name="description" maxlength="2000" rows="6" cols="60" required></textarea>
    </div>

    <button type="submit">Gonder</button>
</form>

<p><a href="${pageContext.request.contextPath}/tickets">Ticket Listesi</a></p>
</body>
</html>
