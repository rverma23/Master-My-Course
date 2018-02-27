<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" %>

<%--
  Authors: Zach Lerman, James DeCarlo, Rahul Verma and Jose Rodriguez

--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<jsp:useBean id="course" class="com.mastermycourse.beans.CourseBean" scope="session"/>

<%
    if(course.getCourseId() == -1){
        response.sendRedirect("/newCourse.htm");
        return;
    }
    if(!course.isEnabled()){
        response.sendRedirect("/courseDisabled.htm");
        return;
    }
%>

<!DOCTYPE html>
<html lan="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Course Creation Page</title>
    <link rel="icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <script src="assets/js/jquery.js"></script>
    <script
            src="https://code.jquery.com/ui/1.12.0/jquery-ui.min.js"
            integrity="sha256-eGE6blurk5sHj+rmkfsGYeKyZx3M4bG+ZlFyA7Kns7E="
            crossorigin="anonymous">
    </script>
    <link rel="stylesheet" href="assets/materialize/css/materialize.css">
    <script src="assets/materialize/js/materialize.js"></script>
    <link rel="stylesheet" href="assets/dropify/css/dropify.css">
    <script src="assets/dropify/js/dropify.js"></script>
    <link rel="stylesheet" href="assets/css/main.css">
    <link rel="stylesheet" href="assets/css/coursecreation.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="assets/js/materialdesign.js"></script>

    <script src="assets/js/jqueryredirect.js" ></script>
    <script src="assets/js/coursecreation.js" ></script>

</head>
<body>

<%@include file="teacherNav.jsp"%>
<div>
    <ul id="slide-out" class="side-nav fixed z-depth-4">
        <div>
            <li class="user-details darken-2 z-depth-2" style="background-image: url(https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcSiJR0wZsiV1kN06OgGvsMmREKsldCGidZEiOmxKlFBpu0bWcwc); color: white;">
                <div class="row">
                    <div class="col col s4 m4 l4">
                        <img src="${sessionScope.userImage}" alt="" class="circle responsive-img valign profile-image">
                    </div>
                    <div class="col col s8 m8 l8">
                        <p>${sessionScope.userName}</p>
                        <p class="user-roal">Professor</p>
                    </div>
                </div>
            </li>
        </div>
        <ul id="courseOutline" class="collapsible" data-collapsible="accordion">
            <!-- removes get -->
            <c:forEach items="${course.outline}" var="chapter">
            <li data-id="${chapter.get(0).contentModuleId}">
                <div class="collapsible-header"><i class="material-icons">filter_drama</i>${chapter.get(0).chapterTitle}</div>
                <div class="collapsible-body">
                    <c:forEach items="${chapter}" var="page">
                        <p style="cursor:pointer;" onclick="changePage(${page.contentModuleId})">${page.pageTitle}</p>
                    </c:forEach>
                </div>
            </li>
            </c:forEach>
        </ul>
    </ul>
</div>

<main>
    <div class="row">
        <!-- Teal page content  -->
        <div class="row">
            <div class="col s12" id="course-creation-center">
                <nav>
                    <div class="nav-wrapper deep-orange lighten-2">
                        <%--<div class="col s12">--%>
                            <a href="#" class="breadcrumb">${course.courseName} File Share Tool</a>
                            <div class="float-right" style="padding-right: 10px">
                                <a  href="#modalDeleteConfirm"><i data-position="bottom" data-delay="50" data-tooltip="Delete Course" class="tooltipped material-icons">delete</i></a>
                            </div>
                            <div class="float-right" style="padding-right: 10px">
                            <form id="form-create-quiz"action="/GoToQuizCreation" method="post">
                                <a   id ="create-quiz" href="javascript:{}" onclick="document.getElementById('form-create-quiz').submit(); return false;"><i data-position="bottom" data-delay="50" data-tooltip="Create Quiz" class="tooltipped material-icons">assignment</i></a>
                            </form>
                            </div>
                        <%--</div>--%>
                    </div>
                </nav>
                <div>
                    <form action="/CreateNewDirectory" method="post">
                        <input type="hidden" name="courseId" value="${course.courseId}">
                        <div class="input-field">
                            <input type="text" id="dir" name="dir">
                            <label for="dir">New Directory</label>
                        </div>
                        <input type="submit" value="Create">
                    </form>
                </div>
            </div>

            </div>
        </div>

    </div>
</main>
<%@include file="footer.jsp" %>

<!-- Modal Course Delete Confirmation -->
<div id="modalDeleteConfirm" class="modal">
    <div class="modal-content">
        <h4>Delete Course Confirmation</h4>
        <p><strong>Are you sure you want to delete this course?</strong> <i>This is permanent and all data including student data will be lost.</i></p>
    </div>
    <div class="modal-footer">
        <form action="/TeacherDeleteCourse" method="post">
            <a href="#!" class="modal-action modal-close waves-effect waves-green btn grey">Cancel</a>
            <button type="submit"  style="margin-right: 10px;" class="modal-action modal-close waves-effect waves-green btn red">Delete Course</button>
        </form>
    </div>
</div>

<!-- Modal PDF Delete Confirmation -->
<div id="modalDeletePDF" class="modal">
    <div class="modal-content">
        <h4>Delete PDF Confirmation</h4>
        <p><strong>Are you sure you want to delete this PDF File?</strong> <i>It is recommended that you keep the file with the course for reference.</i></p>
    </div>
    <div class="modal-footer">
        <form action="/deletePdfFile" method="post">
            <a href="#!" class="modal-action modal-close waves-effect waves-green btn grey">Cancel</a>
            <button type="submit" class="modal-action modal-close waves-effect waves-green btn red">Delete PDF</button>
        </form>
    </div>
</div>

</body>
</html>
