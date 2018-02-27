<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" %>

<%--
  Authors: Zach Lerman, James DeCarlo, Rahul Verma and Jose Rodriguez

--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<jsp:useBean id="file" class="com.mastermycourse.beans.UserFileBean" scope="session"/>
<jsp:useBean id="course" class="com.mastermycourse.beans.CourseBean" scope="session"/>
<jsp:useBean id="createQuiz" class="com.mastermycourse.beans.CreateQuizBean" scope="session"/>
<jsp:setProperty name="file" property="userId" value='${sessionScope.get("userId")}' />

<%
    createQuiz.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
    course.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
    course.setEmail(session.getAttribute("userEmail").toString());
    if(course.getCourseId() == -1){
        response.sendRedirect("/newCourse.htm");
        return;
    }
    if(!course.isEnabled()){
        response.sendRedirect("/courseDisabled.htm");
        return;
    }
    file.setCourse(course.getCourseId() + "");
    file.setCourseId(course.getCourseId());
    file.setDirectoryRoot(session.getServletContext().getRealPath("/"));
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
                            <c:set var="courseCode" scope="page" value="${course.courseCode}"/>
                            <c:if test="${courseCode != 0}">
                                Course Code: ${courseCode}
                            </c:if>
                            <a href="#" class="breadcrumb">${course.courseName} Course Creation Tool</a>
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
                    <c:set var="progress" value="progress${sessionScope.userId}"/>
                    <c:choose>
                        <c:when test="${file.existingPDFFile != null}">
                            <a href="/getCoursePdf" target="_blank"><h5 id="pdf-name">${file.PDFFileName}</h5></a>
                            <c:if test="${applicationScope[progress] == null || applicationScope[progress].courseId != course.courseId}">
                                <a class="waves-effect waves-light btn red parse-pdf" href="#modalDeletePDF">Delete File</a>
                            </c:if>
                            <br />
                            <br />
                            <c:choose>
                                <c:when test="${applicationScope[progress] != null}">
                                    <script>runProgress();</script>
                                    <p id="progressMessage"></p>
                                    <div class="progress">
                                        <div class="determinate" style="width: 0%" id="progressBar"></div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <c:choose>
                                        <c:when test="${file.hasOutline}">
                                            <button class="btn parse-pdf blue lighten-2" onclick="parsePdfOutline()">Parse PDF</button>
                                        </c:when>

                                        <c:otherwise>
                                            <form action="/ParsePDFRegX" method="post">
                                                <p class="teal-text strong">Enter Table of Contents Start Page and End Page</p>
                                                <div class="input-field">
                                                    <input maxlength="4" min="1" max="${file.totalPages}" step="1" type="number" name="startPage" id="startPage" class="validate" required="" aria-required="true">
                                                    <label for="startPage">Enter Start Page Number For Table Of Contents</label>
                                                </div>
                                                <div class="input-field">
                                                    <input maxlength="4" min="1" max="${file.totalPages}" step="1" type="number" name="endPage" id="endPage" class="validate" required="" aria-required="true">
                                                    <label for="endPage">Enter End Page Number For Table Of Contents</label>
                                                </div>
                                                <input class="btn" type="submit" value="Parse PDF">
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <div id="upload-pdf-message" class="flow-text blue white-text">
                                <h5>Upload PDF Text Book To Get Started</h5>
                                <p><i>PDF's should have an outline or table of contents to work with this site</i></p>
                            </div>
                            <%--Dropbox area--%>
                            <form method="post" action="/upload" enctype="multipart/form-data" accept="application/pdf" novalidate="" class="has-advanced-upload">
                                <div class="box__input">
                                    <input type="file" name="file" id="file" id="input-file-now" class="dropify" data-height="450" data-allowed-file-extensions="pdf" data-max-file-size="100M" data-multiple-caption="{count} files selected" multiple="">
                                </div>
                            </form>
                        </c:otherwise>
                    </c:choose>
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
