<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Students Search and Insert</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/school/static/css/studentsmenu.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/school/static/js/studentsmenu.js.js"></script> <!-- Link to your JavaScript file -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
</head>
<body>

<div class="center">
    <p>Hello ${sessionScope.loginName}</p>
</div>

<div class="center">
    <!-- Students Search -->
    <div class="search-wrapper">
        <div class="bot-gap">
            <span class="title">Students Search</span>
        </div>
        <div class="bot-gap">
            <form method="POST" action="${pageContext.request.contextPath}/schoolapp/searchStudent">
                <input name="lastname" type="text" class="search rounded" placeholder="Insert student's lastname" autofocus/>
                <br><br>
                <button class="search-btn rounded color-btn" type="submit">Search</button>
            </form>
        </div>
    </div>

    <!-- Students Insert -->
    <div class="insert-wrapper">
        <div class="bot-gap">
            <span class="title">Students Insert</span>
        </div>
        <div class="bot-gap">
            <form method="POST" action="${pageContext.request.contextPath}/schoolapp/studentInsert">
                <input name="firstname" type="text" value="${requestScope.insertedStudent.firstname}" class="insert rounded" placeholder="First name" autofocus  required/><br>
                <input name="lastname" type="text" value="${requestScope.insertedStudent.lastname}" class="insert rounded" placeholder="Last name" autofocus  required/><br>
                <input name="gender" type="text" value="${requestScope.insertedStudent.gender}" class="insert rounded" placeholder="Gender" autofocus  required/><br>
                <input name="birthdate" type="text" value="${requestScope.insertedStudent.birthdate}" class="insert rounded" placeholder="Birthdate" autofocus  required/><br>
                <label for="cityId"></label>
                <select name="cityId" id="cityId">
                    <option value="">Select a city</option>
                    <!-- Iterate over the "cities" attribute passed from the controller -->
                    <c:forEach items="${cities}" var="city">
                        <option value="${city.id}">${city.name}</option>
                    </c:forEach>
                </select>
                <br>
                <button class="search-btn rounded color-btn" type="submit">Insert</button>
            </form>
        </div>
    </div>
</div>

<div class="center">
    <c:if test="${requestScope.sqlError}">
        <p>${requestScope.message}</p>
    </c:if>
</div>

<div class="center">
    <c:if test="${requestScope.studentsNotFound}">
        <p>No students found</p>
    </c:if>

    <p>${requestScope.error}</p>
</div>

<!-- Add a Back button to the Control Panel -->
<div class="center">
    <a href="${pageContext.request.contextPath}/school/static/templates/controlPanel.jsp" class="back-btn">
        <i class="fas fa-arrow-left"></i> Back to Control Panel
    </a>
</div>

</body>
</html>
