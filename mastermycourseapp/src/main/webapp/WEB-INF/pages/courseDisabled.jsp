
<%--
  Author: James DeCarlo

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<!DOCTYPE html>
<html lan="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Master My Course - Registration</title>
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
        header{
            position: fixed;
            width: 100%;
            top: 0;
            z-index: 9999;
        }
        main{
            position: absolute;
            top: 65px;
            left: 0px;
            right: 0px;
            margin-bottom: 85px;
        }
        footer{
            position: fixed;
            bottom: 0;
            width: 100%;
            height: 75px;
            z-index: 9999;
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

<header id="header" class="page-topbar">

    <div class="navbar-fixed">
        <ul id="dropdown1" class="dropdown-content">
            <c:forEach items="${sessionScope.course.courses}" var="c">
                <li onclick="changeCourse(${c.value})"><a>${c.key}</a></li>
            </c:forEach>
            <li class="divider"></li>
            <li><a href="newCourse.htm">Add New Course</a></li>
        </ul>
        <ul id="dropdown1_mobile" class="dropdown-content">
            <c:forEach items="${sessionScope.course.courses}" var="c">
                <li onclick="changeCourse(${c.value})"><a>${c.key}</a></li>
            </c:forEach>
            <li class="divider"></li>
            <li><a href="newCourse.htm">Add New Course</a></li>
        </ul>
        <nav>
            <div class="nav-wrapper">
                <a href="#!" class="brand-logo">Master My Course</a>
                <ul class="right hide-on-med-and-down">
                    <li><a class="dropdown-button" href="#!" data-activates="dropdown1" data-beloworigin="true">My Courses<i class="material-icons right">arrow_drop_down</i></a></li>
                    <li><a href="/Logout">Logout</a></li>
                </ul>
                <a href="#" data-activates="mobile-menu" class="right button-collapse" id="mobile-menu-button"><i class="material-icons">more_vert</i></a>
                <ul class="side-nav z-depth-4" id="mobile-menu">
                    <li><a class="dropdown-button" href="#!" data-activates="dropdown1_mobile" data-beloworigin="true">My Courses<i class="material-icons right">arrow_drop_down</i></a></li>
                    <li><a href="/Logout">Logout</a></li>
                </ul>
            </div>
        </nav>
    </div>

    <script>
        $(".button-collapse").sideNav();
    </script>
</header>

<main>
<div class="row messageDiv">
    <h>This Course ${sessionScope.course.courseName} is Not Enabled</h>
    <p>Your Course at Master My Course is not currently enabled if you recently signed up your account will be reviewed
        and emailed upon Approval or Decline of your account.</p>
    <p>If your are seeing this and your course was active your course has been disabled this could be due to
        Non payment or copyright infringement and you would have been emailed. Please contact us to resolve this matter.</p>
</div>
</main>
<%@include file="footer.jsp" %>
</body>
</html>