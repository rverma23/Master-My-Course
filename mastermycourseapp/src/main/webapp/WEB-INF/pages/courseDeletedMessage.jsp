
<%--
  Authors: James DeCarlo and Jose Rodriguez

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<jsp:useBean id="course" class="com.mastermycourse.beans.CourseBean" scope="session"/>
<!DOCTYPE html>
<html lan="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Master My Course - New Course</title>
    <link rel="icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <script src="assets/js/jquery.js"></script>
    <link rel="stylesheet" href="assets/materialize/css/materialize.css">
    <script src="assets/materialize/js/materialize.js"></script>
    <link rel="stylesheet" href="assets/css/main.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="assets/js/materialdesign.js"></script>

    <script src="assets/js/jqueryredirect.js" ></script>
    <style>
        html,body{
            height: 100%;
        }

        header {
            height: 65px;
        }

        main{
            position: absolute;
            left: 0;
            right: 0;
            top: 65px;
            height: calc(100% - 65px - 75px);
        }

        footer{
            position: fixed;
            bottom: 0px;
            width: 100%;
            height: 75px;
        }
    </style>

    <script>
        function changeCourse(id) {
            var data = {
                courseId: id
            };

            $.redirect("/ChangeCourse", data);
        }
    </script>
</head>
<body>
<%@include file="teacherNav.jsp" %>
<main>
<div class="row messageDiv">
    <h5>Course Deleted Successfully</h5>
</div>
</main>
<%@include file="footer.jsp" %>
</body>
</html>
