<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" %>

<%--
  Authors: Zach Lerman, James DeCarlo, and Jose Rodriguez

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<jsp:useBean id="course" class="com.mastermycourse.beans.CourseBean" scope="session"/>
<jsp:useBean id="studentCourse" class="com.mastermycourse.beans.StudentCourseBean" scope="session"/>

<%-- should be changed to course page --%>
<%
    course.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
    course.setEmail(session.getAttribute("userEmail").toString());
    if(course.getCourseId() == -1){
        response.sendRedirect("/newCourse.htm");
    }
%>
<!DOCTYPE html>
<html lan="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Course Preview Page</title>
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
        main{
            position: absolute;
            top: 65px;
            left: 0px;
            right: 0px;
            height: calc(100% - 75px - 65px) !important;
            margin-bottom: 100px;
        }
        footer{
            position: fixed;
            bottom: 0;
            width: 100%;
            height: 75px;
        }
        .side-nav{
            top: 65px;
            height: calc(100% - 75px - 65px) !important;
        }

        main{
            padding-left: 300px;
        }

        #pageWrapper{
            overflow: auto;
            width: 100%;
            height: 100%;
        }

        @media only screen and (max-width : 992px) {
            main {
                padding-left: 0;
            }
        }
    </style>
    <script>
        $(document).ready(function () {
            // Initialize collapse button
            $(".button-collapse").sideNav();
            $('select').material_select();

            $("#add_question_button").click(function() {
                $("#answers_div").toggle("fast");
            });
        });


        function changeCourse(id) {
            var data = {
                courseId: id
            };

            $.redirect("/ChangeCourse", data);
        }

        function changePage(contentModuleId) {
            var data = {contentModuleId: contentModuleId};
            $.redirect("/ChangePage", data);
        }

        function nextPage() {
            $.redirect("/NextPage");
        }

        function previousPage() {
            $.redirect("/PreviousPage");
        }
    </script>
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

        <ul class="collapsible" data-collapsible="accordion">

            <c:forEach items="${course.outline}" var="chapter">
            <li>
                <div class="collapsible-header"><i class="material-icons">filter_drama</i>${chapter.get(0).chapterTitle}</div>
                <div class="collapsible-body">
                    <c:forEach items="${chapter}" var="page">
                        <p onclick="changePage(${page.contentModuleId})">${page.pageTitle}</p>
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
            <nav>
                <div class="nav-wrapper deep-orange lighten-2">
                    <div class="col s9">
                        <a href="#" class="breadcrumb">${course.courseName} Course Preview</a>
                    </div>
                    <div class="col s3">
                        <button onclick="previousPage()" class="btn teal">Previous</button>
                        <button onclick="nextPage()" class="btn teal">Next</button>
                    </div>
                </div>
            </nav>
            <div id="pageWrapper">
                <img src="data:image/jpg;base64, ${course.pageImage}" alt="Not found" width="100%" />
            </div>
        </div>

    </div>
</main>

<%@include file="footer.jsp" %>

</body>
</html>