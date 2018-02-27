<%--
  Created by IntelliJ IDEA.
  User: Zach
  Date: 5/23/17
  Time: 10:42 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<jsp:useBean id="messageBoard" class="com.mastermycourse.beans.MessageBoardBean" scope="session"/>

<%
    messageBoard.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
%>

<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Message Board</title>
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
    <script src="assets/js/jquery.validate.js"></script>    <script src="https://cdn.ckeditor.com/4.6.2/standard/ckeditor.js"></script> <!-- Text Editor -->

    <style>
        .btn-small {
            height: 30px;
            line-height: 24px;
            padding: 0 0.5rem;
            font-size: 8px;
        }

        #message_editor_div {
            margin-bottom: 80px;
        }

        .card-title {
            margin-bottom: 15px;
        }

        .row {
            padding: 2px;
        }

        .card .card-content p {
            color: black;
        }
    </style>

    <script>
        $(document).ready(function() {
            Materialize.updateTextFields();
        });

        function addNewPost() {
            // clear the old post controls.
            $("#summary").val("");
            CKEDITOR.instances["editor"].setData('');

            // remove default and display
            $("#display_message_div").css("display", "block");
            $("#default").css("display", "none");

            // display the message editor div
            $("#message_editor_div").css("display", "block");
        }

        function cancelNewPost() {
            $("#message_editor_div").css("display", "none");
            $("#default").css("display", "block");
        }

        function displayMessage(summary, message) {
            $("#default").css("display", "none");
            
            $("#display_message_summary").text(summary);
            document.getElementById("display_message").innerHTML = message;

            $("#display_message_div").css("display", "block");
        }
    </script>
</head>

<c:choose>
    <c:when test="${sessionScope.course != null}">
        <c:set var="courseId" value="${sessionScope.course.courseId}"/>
        <c:set var="courseName" value="${sessionScope.course.courseName}"/>
        <%@include file="teacherNav.jsp"%>
    </c:when>
    <c:when test="${sessionScope.studentCourse != null}">
        <c:set var="courseId" value="${sessionScope.studentCourse.courseId}"/>
        <c:set var="courseName" value="${sessionScope.studentCourse.courseName}"/>
        <%@include file="studentNav.jsp"%>
    </c:when>
</c:choose>

<body>
    <div style="margin-top:15px;" class="row">

        <div class="col s3 grey lighten-4">
            <div class="row">
                <div>
                    <div class="col s5">
                        <button type="button" onclick="addNewPost();" id="new_post_button" class="btn btn-small">
                            <i class="material-icons left small">comment</i>New Post
                        </button>
                    </div>
                    <div class="col s7">
                        <div class="input-field col s12">
                            <input placeholder="Placeholder" id="first_name" type="text" class="validate">
                            <label for="first_name">Search</label>
                        </div>
                    </div>
                </div>
            </div>
            <div id="course_posts">
                <ul class="collection">
                    <c:forEach items="${messageBoard.getMessages(courseId)}" var="message">
                        <li style="cursor:pointer" onclick="displayMessage('${message.summary}', '${message.message}')" class="collection-item">
                            ${message.summary}
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>

        <div class="col s9">
            <div id="default">
                <!-- place whatever default message or content you want here -->
                Click a post or create a new post to get started.
            </div>

            <input type="hidden" name="courseIdHidden" value="${courseId}"/>
            <div class="row" id="display_message_div" style="padding: 10px; display: none;">
                <div class="col s12">
                    <div class="card">
                        <div class="card-content black-text">
                            <span id="display_message_summary" class="card-title blue-text"></span>
                            <div id="display_message"></div>
                        </div>
                        <div class="card-action grey lighten-4">
                            <button type="submit" class="btn blue">Edit</button>
                        </div>
                    </div>
                </div>
            </div>

            <form action="/CreateNewPost" method="post">
                <input type="hidden" name="courseIdHidden" value="${courseId}"/>
                <div class="row" id="message_editor_div" style="padding: 10px; display: none;">
                    <div class="col s12">
                        <div class="card">
                            <div class="card-content">
                                <span class="card-title blue-text">Create New Post</span>
                                <div class="input-field col s12">
                                    <input id="summary" name="summary" type="text" class="validate">
                                    <label for="summary">Summary</label>
                                </div>
                                <textarea id="editor" name="editor"></textarea>
                                <script>
                                    CKEDITOR.replace('editor');
                                </script>
                            </div>
                            <div class="card-action grey lighten-4">
                                <button type="submit" class="btn blue">Post!</button>
                                <button onclick="cancelNewPost();" type="button" class="btn grey">Cancel</button>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>
    <%@include file="footer.jsp" %>
</body>
</html>
