<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" %>

<%--
  Authors: Zach Lerman, James DeCarlo, Rahul Verma and Jose Rodriguez

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<jsp:useBean id="studentAnswers" class="com.mastermycourse.beans.StudentAnswerBean" scope="session"/>
<jsp:useBean id="course" class="com.mastermycourse.beans.CourseBean" scope="session"/>
<jsp:useBean id="studentCourse" class="com.mastermycourse.beans.StudentCourseBean" scope="session"/>

<%
    studentAnswers.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
    course.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
    studentCourse.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
    studentCourse.setEmail(session.getAttribute("userEmail").toString());
%>

<!DOCTYPE html>
<html lan="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Take Quiz</title>
    <link rel="icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <script src="assets/js/jquery.js"></script>
    <link rel="stylesheet" href="assets/materialize/css/materialize.css">
    <script src="assets/materialize/js/materialize.js"></script>
    <link rel="stylesheet" href="assets/css/main.css">
    <link rel="stylesheet" href="assets/css/gradequiz.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="assets/js/materialdesign.js"></script>
    <script src="assets/js/jqueryredirect.js" ></script>
    <script src="assets/js/gradequiz.js" ></script>
</head>
<body>
<c:choose>
<c:when test="${studentCourse.isTA() > 0}">
    <%@include file="studentNav.jsp"%>
</c:when>

<c:when test="${studentCourse.isTA() == 0}">
    <%@include file="teacherNav.jsp"%>
</c:when>
</c:choose>
<main>
    <div class="row">
        <div class="col s12 m8">
    <form action="/GradeOversight" method="post">
        <c:forEach items="${studentAnswers.answers}" var="answer">
            <c:choose>
                <c:when test="${answer.question.questionType.equals('oversight')}">
                    <p id="grade-oversight"class="flow-text">Question Prompt: ${answer.question.question}</p>
                    <p class="blue-text text-darken-2">Student Answer: <strong class="black-text text-darken-2">${answer.answer}</strong></p>
                    <p class="teal-text text-darken-2 yellow lighten-5">Grading Notes: These will be displayed to the student.</p>
                    <textarea required name="${answer.question.question}teacherNotes"></textarea>
                    <input required type="radio" name="${answer.question.question}radio" class="answer_radio" value="true" id="${answer.question.question}true"><label for="${answer.question.question}true">Correct</label><br/>
                    <input type="radio" name="${answer.question.question}radio" value="false" id="${answer.question.question}false"><label for="${answer.question.question}false">Incorrect</label><br/>
                </c:when>
            </c:choose>
        </c:forEach>
        <button id="grade-oversight-submit" type="submit" class="btn blue">Submit</button>
    </form>
        </div>
    </div>
</main>

<%@include file="footer.jsp" %>

</body>
</html>