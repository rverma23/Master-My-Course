<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" %>

<%--
  Authors: James DeCarlo and Jose Rodriguez

--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<jsp:useBean id="admin" class="com.mastermycourse.beans.AdminPortalBean" scope="session"/>

<%
    admin.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
%>

<!DOCTYPE html>
<html lan="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Administration</title>
    <link rel="icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <script src="assets/js/jquery.js"></script>
    <link rel="stylesheet" href="assets/materialize/css/materialize.css">
    <script src="assets/materialize/js/materialize.js"></script>
    <link rel="stylesheet" href="assets/css/main.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="assets/js/materialdesign.js"></script>
    <script src="assets/js/jqueryredirect.js" ></script>
    <link href="/assets/css/admin.css" rel="stylesheet">
    <script src="/assets/js/admin.js"></script>
</head>
<body>
<header id="header" class="page-topbar">
    <div class="navbar-fixed">
        <nav>
            <div class="nav-wrapper">
                <a href="#" data-activates="slide-out" class="button-collapse"><i class="material-icons">menu</i></a>
                <a href="#!" class="brand-logo">Master My Course</a>
                <ul class="right hide-on-med-and-down">
                    <li><a href="/Logout">Logout</a></li>
                    <li class="language"><a href="collapsible.html"><img src="assets/images/flag-icons/United-States.png" alt="USA"></a></li>
                </ul>
            </div>
        </nav>
    </div>
</header>
<div id="slide-out" class="side-nav fixed">
    <ul class="collection z-depth-5 collection-students">

        <c:if test="${admin.pendingTeachers.size() > 0}">
        <div class="collection">
            <a href="#!" class="collection-item">
                <span class="new badge">${admin.pendingTeachers.size()}</span>
                Pending Teachers
            </a>
        </div>
        <form action="/AdminApproveDeleteTeachers" method="post">

            <c:forEach items="${admin.pendingTeachers}" var="teachers">
            <li class="collection-item avatar">
                <div class="row">
                    <div class="col s10">
                        <img src="${teachers.imageUrl}" alt="" class="circle">
                        <span class="title right-align">
                            ${teachers.name}
                        </span>
                        <p>${teachers.school}</p>
                        <p class="tooltipped" data-position="right" data-delay="50" data-tooltip="${teachers.description}" style="color: blue;">
                            About
                        </p>
                    </div>
                    <div class="col s2">
                        <input type="checkbox" name="userEmails" id="${teachers.email}" value="${teachers.email}">
                        <label for="${teachers.email}"></label>
                    </div>
                </div>
            </li>
            </c:forEach>

            <input type="submit" name="action" value="Approve" class="btn green" style="width: 100%">
            <input type="submit" name="action" value="Delete" class="btn red" style="width: 100%">

        </form>
        </c:if>

        <c:if test="${admin.approvedTeachers.size() > 0}">
            <div class="collection">
                <a onclick="allCourses()" class="collection-item">
                    <span class="badge">Teachers ${admin.approvedTeachers.size()}</span>
                    Approved Teachers<br>
                    All Courses
                </a>
            </div>
            <form action="/AdminDisableTeachers" method="post">

                <c:forEach items="${admin.approvedTeachers}" var="teachers">
                    <li class="collection-item avatar">
                        <div class="row">
                            <div class="col s10">
                                <img src="${teachers.imageUrl}" alt="" class="circle">
                                <span class="title right-align">
                                        ${teachers.name}
                                </span>
                                <p>${teachers.school}</p>
                                <p class="tooltipped" data-position="right" data-delay="50" data-tooltip="${teachers.description}" style="color: blue;">
                                    About
                                </p>
                                <p class="teal-text" onclick="teacherCourses(${teachers.id})">Courses</p>
                            </div>
                            <div class="col s2">
                                <input type="checkbox" name="userEmails" id="${teachers.email}approved" value="${teachers.email}">
                                <label for="${teachers.email}approved"></label>
                            </div>
                        </div>

                    </li>
                </c:forEach>

                <input type="submit" name="action" value="Disable" class="btn red" style="width: 100%">

            </form>
        </c:if>

    </ul>
</div>

<main>
    <c:set var="teacherCourses" scope="request" value="${admin.teacherCourses}"/>
<div class="row" style="height: 100%">
        <!-- Grey navigation panel -->

    <div class="col s12">
        <!-- Teal page content  -->
        <nav>
            <div class="nav-wrapper deep-orange lighten-2">
                <div class="col s12">
                    <h5 id="teacherId">${admin.teacherName}</h5>
                </div>
            </div>
        </nav>
        <div class="col s12 input-field">
            <input id="search" type="text" required placeholder="Search Courses">
            <label class="label-icon" for="search"><i class="material-icons">search</i></label>
        </div>
        <c:forEach items="${admin.teacherCourses}" var="course">
            <div class="col s12 l4 course">
                <div class="card horizontal blue-grey lighten-1">
                    <div>
                        <img src="${course.teacherImage}" class="circle responsive-img valign profile-image">
                    </div>
                    <div class="card-stacked">
                        <div class="card-content">
                            <h5 class="teacherName">${course.teacherName}</h5>
                            <p class="courseName">${course.name}</p>
                            <p class="description">${course.description}</p>
                        </div>
                        <div class="card-action">
                            <c:choose>
                                <c:when test="${course.enabled}">
                                    <a onclick="disableCourse(${course.courseId}, '${course.name}')">Disable</a>
                                </c:when>
                                <c:otherwise>
                                    <a onclick="enableCourse(${course.courseId}, '${course.name}')">Enable</a>
                                    <a onclick="deleteCourse(${course.courseId}, '${course.name}')">Delete</a>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
</main>

<%@include file="footer.jsp" %>

<script src="assets/js/jquery.clearsearch.js"></script>
<script>
    $('#search').keyup(function () {
        var search = $(this).val().toLowerCase();
        $('.course').each(function () {
            var teacherName = $(this).find('.teacherName').text().toLowerCase();
            var courseName = $(this).find('.courseName').text().toLowerCase();
            var description = $(this).find('.description').text().toLowerCase();
            (teacherName.includes(search) || courseName.includes(search) || description.includes(search))  ? $(this).show() : $(this).hide();
            $('body').scrollTop(0);
        });
    });

    $('#search').clearSearch({callback:function(){
        $('.course').each(function () {
            $(this).show();
        });
    }});
</script>
</body>
</html>

