

<%--
  Authors: James DeCarlo and Jose Rodriguez

--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:if test="${sessionScope.course != null}">
    <c:set var="courseId" value="${sessionScope.course.courseId}"/>
    <c:set var="courseName" value="${sessionScope.course.courseName}"/>
</c:if>
<c:if test="${sessionScope.studentCourse != null}">
    <c:set var="courseId" value="${sessionScope.studentCourse.courseId}"/>
    <c:set var="courseName" value="${sessionScope.studentCourse.courseName}"/>
</c:if>
<!DOCTYPE html>
<html lan="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Master My Course - Chat</title>
    <link rel="icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="assets/js/jquery.js"></script>
    <link rel="stylesheet" href="assets/materialize/css/materialize.css">
    <script src="assets/materialize/js/materialize.js"></script>
    <link rel="stylesheet" href="assets/css/main.css">
    <link rel="stylesheet" href="assets/css/chat.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="assets/js/materialdesign.js"></script>

    <script>
        $(document).ready(function () {
            // Initialize collapse button
            $(".button-collapse").sideNav();
        });
    </script>
</head>
<body>

<a href="#" data-activates="slide-out" class="button-collapse chat-button"><i class="material-icons">menu</i></a>
<%@include file="chatnav.jsp"%>
<main>
<div class="row" style="height:600px;">

    <div class="col s12">


        <div class="chat z-depth-3">
            <div class="chat-header clearfix teal">
                <img id="chatting-user" src="http://www.icon100.com/up/367/128/dog.png" alt="avatar" class="circle responsive-img valign profile-image"/>

                <div class="chat-about">
                    <div class="chat-with">${courseName} Chat</div>
                    <div class="chat-num-messages">Online</div>
                </div>
                <i class="fa fa-star"></i>
            </div> <!-- end chat-header -->

            <div class="chat-history" id="chatWindowDiv">
                <ul id="chatWindow">
                    <%-- filled in from chatroom.js--%>
                </ul>

            </div> <!-- end chat-history -->

            <div class="chat-message clearfix">
                <textarea name="message-to-send" maxlength="20000" id="chatMessage" placeholder ="Type your message" rows="3"></textarea>

                <i class="fa fa-file-o"></i> &nbsp;&nbsp;&nbsp;
                <i class="fa fa-file-image-o"></i>

                <button onMouseUp="sendMessage()" id="sendMessage" class="btn teal white-text">Send</button>

            </div> <!-- end chat-message -->

        </div> <!-- end chat -->

    </div>

</div>
</main>

<script type="text/javascript" src="assets/js/pubnub-3.14.5.min.js"></script>
<script type="text/javascript">
    var myUuid = '${sessionScope.userId}';
    var myName = '${sessionScope.userName}';
    var myNumber = '${sessionScope.userId}';
    var channel = '${courseId}';
    var privateChannel = 'private'  + '${courseId}';
    var imageUrl = '${sessionScope.userImage}';
</script>
<script type="text/javascript" src="assets/js/chatroom.js"></script>


</body>
</html>
