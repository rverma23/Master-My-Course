<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" %>
<%--
  Authors: Zach Lerman, James DeCarlo, and Jose Rodrigue
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<jsp:useBean id="quiz" class="com.mastermycourse.beans.QuizBean" scope="session"/>
<jsp:useBean id="studentCourse" class="com.mastermycourse.beans.StudentCourseBean" scope="session"/>
<jsp:useBean id="studentMetrics" class="com.mastermycourse.beans.StudentMetricsBean" scope="session"/>
<jsp:useBean id="course" class="com.mastermycourse.beans.CourseBean" scope="session"/><%
    quiz.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
    studentCourse.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
    course.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
    course.setEmail(session.getAttribute("userEmail").toString());
    studentCourse.setEmail(session.getAttribute("userEmail").toString());
    studentMetrics.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
    studentMetrics.setEmail(session.getAttribute("userEmail").toString());
    course.setCourseId(studentCourse.getCourseId());
    if (studentCourse.getCourseId() == -1) {
        response.sendRedirect("/joinCourse.htm");
        return;
    }
    if(!studentCourse.isEnabled()){
        response.sendRedirect("/courseDisabledStudent.htm");
        return;
    }
    if(studentCourse.getContentModuleId() < 1){
        response.sendRedirect("/courseDisabledStudent.htm");
        return;
    }
%>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <script src="assets/js/jquery.js"></script>
    <link rel="stylesheet" href="assets/materialize/css/materialize.css">
    <script src="assets/materialize/js/materialize.js"></script>
    <link rel="stylesheet" href="assets/css/main.css">
    <link rel="stylesheet" href="assets/css/quiz.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="assets/js/materialdesign.js"></script>
    <script src="assets/js/jqueryredirect.js" ></script>
    <title>Success</title>
</head>
<body>
    <%@include file="studentNav.jsp"%>
    <form action="/redirectToCourse" method="post">
        <div class="col s12">
            <div class="success">Quiz successfully submitted.</div>
        </div>
        <button type="submit" class="btn btn-return">Return</button>
    </form>
</body>
</html>
