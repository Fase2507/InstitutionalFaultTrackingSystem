<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Ticket Listesi</title>
</head>
<body>
<h2>Ticket Listesi</h2>

<c:if test="${not empty error}">
    <p style="color:red">${error}</p>
</c:if>

<p>
    <a href="${pageContext.request.contextPath}/tickets/new">Yeni Ticket</a>
</p>

<table border="1" cellpadding="6" cellspacing="0">
    <thead>
    <tr>
        <th>ID</th>
        <th>Baslik</th>
        <th>Kategori</th>
        <th>Durum</th>
        <th>Atanan</th>
        <th>Olusturma</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${tickets}" var="t">
        <tr>
            <td>${t.id}</td>
            <td>${t.title}</td>
            <td>
                <c:choose>
                    <c:when test="${not empty t.category}">
                        <c:out value="${t.category.catName}"/>
                    </c:when>
                    <c:otherwise>-</c:otherwise>
                </c:choose>
            </td>
            <td>
                <c:choose>
                    <c:when test="${not empty t.status}">
                        <c:out value="${t.status.statusName}"/>
                    </c:when>
                    <c:otherwise>-</c:otherwise>
                </c:choose>
            </td>
            <td>
                <c:choose>
                    <c:when test="${not empty t.assignedTo}">
                        <c:out value="${t.assignedTo.firstName}"/> <c:out value="${t.assignedTo.lastName}"/>
                    </c:when>
                    <c:otherwise>-</c:otherwise>
                </c:choose>
            </td>
            <td>${t.createdAt}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

</body>
</html>
