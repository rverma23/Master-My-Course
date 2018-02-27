<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" %>
<%@ page import="com.mastermycourse.database.StudentDBCommands" %>


<%--
  Authors: Zach Lerman, James DeCarlo, Rahul Verma and Jose Rodriguez

--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<jsp:useBean id="quiz" class="com.mastermycourse.beans.QuizBean" scope="session"/>
<jsp:useBean id="studentCourse" class="com.mastermycourse.beans.StudentCourseBean" scope="session"/>
<jsp:useBean id="studentMetrics" class="com.mastermycourse.beans.StudentMetricsBean" scope="session"/>
<jsp:useBean id="course" class="com.mastermycourse.beans.CourseBean" scope="session"/>

<%-- should be changed to course page --%>
<%
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

    if (!studentCourse.isEnabled()) {
        response.sendRedirect("/courseDisabledStudent.htm");
        return;
    }

    if (studentCourse.getContentModuleId() < 1) {
        response.sendRedirect("/courseDisabledStudent.htm");
        return;
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
    <link rel="stylesheet" href="assets/css/course.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="assets/js/materialdesign.js"></script>
    <script src="assets/js/jqueryredirect.js" ></script>
    <script src="assets/js/jquery-ui.js" ></script>
    <script src="assets/js/jquery-ui-touchpunch.js"></script>
    <script src="assets/js/jquery.validate.js"></script>

    <script>
        var rawText = "${studentCourse.rawTextEscapedNewLineDoubleQuote}";
    </script>

    <script src='https://code.responsivevoice.org/responsivevoice.js'></script>
    <script src="assets/js/course.js" ></script>
</head>
<body>


<%@include file="studentNav.jsp"%>
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
                        <p class="user-roal">Student</p>
                    </div>
                </div>
            </li>
        </div>

        <ul class="collapsible" data-collapsible="accordion">
            <c:forEach items="${studentCourse.outline}" var="chapter">
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
                    <a href="#" class="breadcrumb">${studentCourse.courseName} Course</a>
                </div>
            </nav>
            <div class="row">
                <div class="input-field col s12 m4">
                    <select id="voiceselection"></select>
                    <label>Select Voice</label>
                </div>
                <div class="col s12 m8" style="padding-top: 10px;padding-right: 0;">
                    <a id="playPauseButton" onclick="playAudio()" class="btn blue"><i class="material-icons">play_arrow</i></a>
                    <a id="restartButton" onclick="restartAudio()" class="btn amber"><i class="material-icons">replay</i></a>
                    <a onclick="previousPage()" class="btn teal"><i class="material-icons">skip_previous</i></a>
                    <a onclick="nextPage()" class="btn teal"><i class="material-icons">skip_next</i></a>
                    <a id="toggleImageTextButton" onclick="toggleImageText()" class="btn orange"><i class="material-icons">text_fields</i></a>
                </div>
            </div>

            <div style="margin-bottom: 75px;">
                <div id="imageDiv">
                    <img src="data:image/jpg;base64, ${studentCourse.pageImage}" alt="Not found" width="100%" />
                </div>
                <div id="textDiv" hidden="hidden">
                    <p class="flow-text">${studentCourse.rawTextAsHtml}</p>
                </div>
            </div>
        </div>
    </div>
</main>

<!-- Notes section for student for adding a note-->
<div class="side-nav" id="notesSideNav">
    <div class="ui-widget-content">

        <nav class="nav-notes">
            <div class="nav-wrapper deep-teal lighten-2">
                <div class="row">
                    <h5>Notes:</h5>
                    <a id= "add-note" data-target="modal-notes" href="#" class="right-aligned"><i class="material-icons white-text">note_add</i></a>
                </div>
            </div>
        </nav>


        <div class="notes">
            <c:forEach items="${studentCourse.notes}" var="note">
                <div class="card blue darken-1 hoverable">
                    <div class="card-content white-text">
                        <span class="card-title">${note.titleNote}</span>
                        <p class="note-text ">${note.noteTextHtml}</p>
                    </div>
                    <div class="card-action">
                        <div class="row">
                            <div class="col s6">
                                <button id="note-delete" onclick="deleteNote('${note.titleNote}')" class="btn red" href="#">Delete</button>
                            </div>
                            <div class="col s6">
                                <button id="note-edit" data-target="modal-notes-edit" class="btn orange" onclick="editNoteView(${studentCourse.userId}, ${studentCourse.courseId}, '${note.titleNote}', '${note.noteEscapedJavascript}')">Edit</button>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>

<!-- Modal Structure for adding a note-->
<div id="modal-notes" class="modal">
    <div class="modal-content">
        <h4>Note for ${studentCourse.courseName}</h4>
        <form id="form-add-note" action="/AddNote" method="post">
            <div class="row">
                <div class="input-field col s6">
                    <input id="title-note" type="text" name="titleNote" value="" data-length="200" data-error=".errorTxt4" onblur="validateNoteTitle(${studentCourse.userId}, ${studentCourse.courseId})">
                    <label for="title-note" >Title</label>
                    <div class="errorTxt4"></div>
                </div>
            </div>

            <div class="row">
            <div class="input-field col s12">
                <textarea id="textarea-note" class="materialize-textarea" name="noteText" data-length="10000" data-error=".errorTxt3"></textarea>
                <label for="textarea-note">Note text:</label>
                <div class="errorTxt3"></div>
            </div>
            </div>
            <input id="note-userId-submit" type="hidden" value="${studentCourse.userId}" name="userId">
            <input id="note-courseId-submit" type="hidden" value="${studentCourse.courseId}" name="courseId">
            <input type="submit" class="btn blue" value="Submit">
        </form>
    </div>
    <div class="modal-footer">
        <a id="close-note-module" href="#!" class="modal-action modal-close waves-effect waves-green btn-flat">Close</a>
    </div>
</div>

<div id="modal-notes-edit" class="modal">
    <div class="modal-content">
        <h4 id="note-title"></h4>
        <form action="/EditNote" method="post">

            <div class="row">
                <div class="input-field col s12">
                    <textarea id="note-text" class="materialize-textarea" name="noteText" data-length="10000"></textarea>
                    <label id="textarea-note-label" for="textarea-note">Note text:</label>
                </div>
            </div>
            <input id="note-userId" type="hidden" value="" name="userId">
            <input id="note-courseId" type="hidden" value="" name="courseId">
            <input id="note-title-param" type="hidden" value="" name="title">
            <input type="submit" class="btn blue" value="Submit">

        </form>
    </div>
    <div class="modal-footer">
        <a href="#!" class="modal-action modal-close waves-effect waves-green btn-flat">Close</a>
    </div>
</div>
<!--end notes section-->

<%@include file="footer.jsp" %>

</body>
</html>