<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" %>

<%--
  Authors: Zach Lerman, James DeCarlo, Rahul Verma and Jose Rodriguez

--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<jsp:useBean id="quiz" class="com.mastermycourse.beans.QuizBean" scope="session"/>
<jsp:useBean id="studentCourse" class="com.mastermycourse.beans.StudentCourseBean" scope="session"/>
<jsp:useBean id="course" class="com.mastermycourse.beans.CourseBean" scope="session"/>

<%
    course.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
    quiz.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
%>

<!DOCTYPE html>
<html lan="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Take Quiz</title>
    <link rel="icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <script src="assets/js/jquery.js"></script>
    <link rel="stylesheet" href="assets/materialize/css/materialize.css">
    <script src="assets/materialize/js/materialize.js"></script>
    <link rel="stylesheet" href="assets/css/main.css">
    <link rel="stylesheet" href="assets/css/quiz.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="assets/js/materialdesign.js"></script>
    <script src="assets/js/jqueryredirect.js"></script>
    <script src="assets/js/quiz.js"></script>
    <script src="assets/ace/ace.js"></script>
</head>
<body>
<style>
    #editor {
        width: 600px;
        height: 400px;
    }
</style>
<script>

    var editor;
    var mode = 3;
    $(document).ready(function () {
        // initialize the code editor in case of code questions
        try {
            editor = ace.edit("editor");
            editor.setTheme("ace/theme/sqlserver");

            if (mode == 3) {
                editor.getSession().setMode("ace/mode/java");
            } else {
                editor.getSession().setMode("ace/mode/python");
            }
        } catch (e) {
        }
    });

    function updateMode(newMode) {
        mode = newMode;
    }

    function submitAnswer() {
        var questionType = $("#question_type").val();
        if (questionType == "code") {
            var student_code = editor.getValue();
            data = {
                student_code: student_code
            };
            $.redirect("/AnswerCodeQuestion", data);
        } else {
            if (questionType == "multipleChoice" || questionType == "trueFalse") {
                if ($("." + questionType + " .required_radio:checked").length == 0) {
                    Materialize.toast("Please fill in all fields", 2000);
                    return; // exit, don't submit the incomplete form.
                }
            }
            $("#questionForm").submit();
        }
    }
</script>

<%@include file="studentNav.jsp" %>
<c:set var="i" value="0" scope="page"/>
<div>
    <ul id="slide-out" class="side-nav fixed z-depth-4">
        <div>
            <li class="user-details darken-2 z-depth-2"
                style="background-image: url(https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcSiJR0wZsiV1kN06OgGvsMmREKsldCGidZEiOmxKlFBpu0bWcwc); color: white;">
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
            <li>
                <h5 class="blue lighten-5" style="color:#64b5f6; height:50px;padding:0;margin:0px">Questions on this
                    quiz:</h5>
                <div class="collection">
                    <c:forEach items="${quiz.question_list}" var="question">
                        <c:choose>
                            <c:when test="${quiz.index == i}">
                                <a href="#!"
                                   class="collection-item blue lighten-5">Question ${i+1}: ${question.question}</a> <!-- Highlight the current questions -->
                            </c:when>
                            <c:otherwise>
                                <a href="#!" class="collection-item">Question ${i+1}: ${question.question}</a>
                            </c:otherwise>
                        </c:choose>
                        <c:set var="i" value="${i + 1}" scope="page"/>
                    </c:forEach>
                </div>
            </li>
        </div>
    </ul>
</div>

<c:set var="question" value="${quiz.question}" scope="page"/>
<c:set var="index" value="${quiz.index}" scope="page"/>
<c:if test='${question.questionType.equals("code")}'>
    <script>
        updateMode(${question.language});
    </script>
</c:if>
<main>
    ${quiz.hacker_response}
    <form id="questionForm" action="/AnswerQuestion" method="post">
        <!-- Question Creation -->
        <div id="quiz_div">
            <h3 class="quiz-title">${quiz.title}</h3>
            <input id="question_type" value="${question.questionType}" type="hidden"/>
            <div class="question-container card-panel blue lighten-3">
                <label id="a_${index}"
                       class="title-question">Question ${quiz.index+1}: ${question.question}</label><br/>
                <c:choose>
                    <c:when test="${question.questionType=='multipleChoice'}">
                        <ul class="answers multipleChoice">
                            <input type="radio" name="question${index}" class="answer_radio required_radio"
                                   value="${question.prompts[0]}" id="q${index}_a"><label
                                for="q${index}_a">${question.prompts[0]}</label><br/>
                            <input type="radio" name="question${index}" class="answer_radio required_radio"
                                   value="${question.prompts[1]}" id="q${index}_b"><label
                                for="q${index}_b">${question.prompts[1]}</label><br/>
                            <input type="radio" name="question${index}" class="answer_radio required_radio"
                                   value="${question.prompts[2]}" id="q${index}_c"><label
                                for="q${index}_c">${question.prompts[2]}</label><br/>
                            <input type="radio" name="question${index}" class="answer_radio required_radio"
                                   value="${question.prompts[3]}" id="q${index}_d"><label
                                for="q${index}_d">${question.prompts[3]}</label><br/>
                        </ul>
                    </c:when>
                    <c:when test="${question.questionType=='trueFalse'}">
                        <ul class="answers trueFalse">
                            <input type="radio" name="question${index}" class="answer_radio required_radio" value="1"
                                   id="qtrue${index}"><label for="qtrue${index}">True</label><br/>
                            <input type="radio" name="question${index}" class="answer_radio required_radio" value="0"
                                   id="qfalse${index}"><label for="qfalse${index}">False</label><br/>
                        </ul>
                    </c:when>
                    <c:when test="${question.questionType=='exactAnswer'}">
                        <div class="input-field col s12">
                            <textarea required name="question${index}" id="q${index}"
                                      class="materialize-textarea"></textarea>
                            <label for="q${index}">Enter Answer</label>
                        </div>
                    </c:when>
                    <c:when test="${question.questionType=='oversight'}">
                        <div class="input-field col s12">
                            <textarea required name="question${index}" id="q${index}"
                                      class="materialize-textarea"></textarea>
                            <label for="q${index}">Enter Answer</label>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!-- code question -->
                        <div id="editor">${question.template_code}</div>
                    </c:otherwise>
                </c:choose>
                You have ${quiz.attempts} attempt(s) remaining for this question
                <button id="sub-quiz" onclick="submitAnswer();" class="btn blue waves-effect waves-light" type="button">Submit Question</button>
            </div>
        </div>
    </form>
</main>

<%@include file="footer.jsp" %>

</body>
</html>