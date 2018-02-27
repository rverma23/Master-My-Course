<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" %>
<%--
  Created by IntelliJ IDEA.
  User: Rahul
  Date: 4/18/2017
  Time: 1:56 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<jsp:useBean id="quiz" class="com.mastermycourse.beans.QuizBean" scope="session"/>
<jsp:useBean id="studentCourse" class="com.mastermycourse.beans.StudentCourseBean" scope="session"/>
<jsp:useBean id="userBean" class="com.mastermycourse.beans.UserBean" scope="session"/>
<jsp:useBean id="studentMetrics" class="com.mastermycourse.beans.StudentMetricsBean" scope="session"/>


<%
    quiz.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
    studentCourse.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
    userBean.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
    studentMetrics.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
    studentMetrics.setEmail(session.getAttribute("userEmail").toString());
%>

<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Metrics</title>
    <link rel="icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <script src="assets/js/jquery.js"></script>
    <link rel="stylesheet" href="assets/materialize/css/materialize.css">
    <script src="assets/materialize/js/materialize.js"></script>
    <link rel="stylesheet" href="assets/css/main.css">
    <link rel="stylesheet" href="assets/css/course.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="assets/js/materialdesign.js"></script>
    <script src="assets/js/jqueryredirect.js"></script>
    <script src='https://code.responsivevoice.org/responsivevoice.js'></script>
    <script src="assets/js/course.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.1.4/Chart.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/mathjs/3.12.1/math.min.js"></script>

    <script>
        /**
         * Display the upcoming quizzes (ones not past due date and not taken)
         * to the student
         * @param upcomingQuizzes a JSON array string of upcoming quizzes
         */
        function upcoming(upcomingQuizzes) {
            $("#quiz_div").css("display", "none");
            $("#upcoming_div").css("display", "inline-block");
            $("#upcoming_quiz_list").empty();
            var list_string = '<li class="collection-header"><h5>Upcoming Quizzes</h5></li>';
            var json = upcomingQuizzes;
            if (json.length > 0) {
                for (var i = 0; i < json.length; i++) {
                    list_string += '<li class="collection-item">';
                    list_string += '<form action="/TakeQuiz" method="post">';
                    list_string += '<input name="quizId" type="hidden" value="' + json[i]["id"] + '"/>';
                    list_string += '<p>Quiz Name: ' + json[i]["title"] + '</p>';
                    list_string += '<p>Due Date: ' + json[i]["date"] + '</p>';
                    list_string += '<p>Teacher Notes: ' + json[i]["teachersNote"] + '</p>';
                    list_string += '<button type="submit" class="btn">Take Chapter Quiz</button>';
                    list_string += '</form></li>';
                }

            } else {
                list_string += '<li class="collection-item">No upcoming quizzes.</li>';
            }
            $("#upcoming_quiz_list").append(list_string);
        }

        function metrics(quizId, classQuizzes, allStudentQuizzes, quizHistogram, teacherNotes) {
            $("#upcoming_div").css("display", "none");
            $("#quiz_div").css("display", "inline-block");
            // set teacher's notes
            $("#teacher_notes").empty();

            //var json = JSON.parse(teacherNotes);
            var json = teacherNotes;
            var note_num = json.length;
            if (json.length > 0) {
                var html_string = "<li>";
                html_string += '<div class="collapsible-header"><span class="new badge" data-badge-caption="Comment(s)">' + note_num + '</span><i class="material-icons">message</i>Teachers Comments</div>';
                html_string += '<div class="collapsible-body"><ul class="collection body-collection">';
                var collapsible_body_html = '';
                for (var i = 0; i < note_num; i++) {
                    var correct;
                    if (json[i]["isCorrect"] == "1") {
                        correct = "Grade: correct";
                    } else {
                        correct = "Grade: incorrect";
                    }
                    collapsible_body_html += '<li class="collection-item">'
                        + '<p class="qPrompt">' + 'Question Prompt: ' + json[i]['questionPrompt'] + '</p>'
                        + '<p class="correct">' + correct + '</p>'
                        + '<p class="comments">' + "Teacher's Comments: " + json[i]["teacherComment"] + '</p>'
                        + '</li>';
                }

                html_string += '</ul></div>';
                $("#teacher_notes").append(html_string);
                $(".body-collection").append(collapsible_body_html);
            }

            // set student grade
            var yourTr_html = '<td id="your"></td>';
            $('#yourTr').html(yourTr_html);
            for (var i = 0; i < allStudentQuizzes.length; i++) {
                if (allStudentQuizzes[i].id == quizId) {
                    var average = allStudentQuizzes[i].actualpoints / allStudentQuizzes[i].totalpoints;
                    average = average * 100;
                    $('#your').append(parseFloat(average).toFixed(2));
                }
            }

            //set the average
            var avgTr_html = '<td id="avg"></td>';
            $('#yourTr').append(avgTr_html);
            for (var i = 0; i < classQuizzes.length; i++) {
                if (classQuizzes[i].id == quizId) {
                    var grade = classQuizzes[i].actualpoints / classQuizzes[i].totalpoints;
                    grade = grade * 100;
                    $('#avg').html(parseFloat(grade).toFixed(2));
                }
            }

            //set the median
            var medTr_html = '<td id="med"></td>';
            $('#yourTr').append(medTr_html);
            for (var i = 0; i < quizHistogram.length; i++) {
                if (quizHistogram[i].id == quizId) {
                    var data = quizHistogram[i].allGrades;
                    var median = math.median(data);
                    $('#med').html(parseFloat(median).toFixed(2));
                }
            }

            //make the histogram
            var xAxisLabels = ['0-10', '10-20', '20-30', '30-40', '40-50', '50-60', '60-70', '70-80', '80-90', '90-100', '100-110'];
            var yMax = 0;
            for (var i = 0; i < quizHistogram.length; i++) {

                if (quizHistogram[i].id == quizId) {
                    var title = quizHistogram[i].title;
                    var histo = quizHistogram[i].histogram;

                    yMax = 0;
                    for (var j = 0; j < histo.length; j++) {
                        if (histo[j] > yMax) {
                            yMax = histo[j];
                        }
                    }
                    yMax = yMax + 1;

                    var canvas_html = '<canvas id="canvas"></canvas>';
                    $('#histo_container').html(canvas_html);

                    var canvasContext = document.getElementById('canvas').getContext('2d');
                    new Chart(canvasContext, {
                        type: 'bar',
                        data: {
                            labels: xAxisLabels,
                            datasets: [{
                                label: title,
                                data: histo,
                                backgroundColor: "rgba(0, 217, 217, 1)"
                            }]
                        },
                        options: {
                            scales: {
                                yAxes: [{
                                    ticks: {
                                        max: yMax,
                                        min: 0,
                                        stepSize: 1
                                    }
                                }],
                                xAxes: [{
                                    categoryPercentage: .99,
                                    barPercentage: 1.0
                                }]
                            }
                        }
                    });
                }
            }
        }
    </script>

</head>
<body>
<%@include file="studentNav.jsp" %>

<main>
    <div style="width:100%" id="upcoming_div">
            <ul id="upcoming_quiz_list" class="collection with-header">

            </ul>
    </div>

    <div style="width:100%" id="quiz_div">
        <div>
            <table class="striped">
                <thead>
                <tr>
                    <th>Your Score</th>
                    <th>Average</th>
                    <th>Median</th>
                </tr>
                </thead>
                <tbody>
                <tr id="yourTr">
                </tr>
                </tbody>
            </table>

        </div>
        <div>
            <div id="histo_container" style="width:80%;"></div>
        </div>

        <div style="margin-bottom: 150px;">
            <ul id="teacher_notes" class="collapsible" data-collapsible="accordion">
            </ul>
        </div>
    </div>
</main>


<div id="slide-out" class="side-nav fixed">
    <nav>
        <div class="nav-wrapper cyan">
            <div class="col s12">
                <a href="/studenttool.htm" class="breadcrumb">Metrics for ${studentCourse.courseName} </a>
            </div>
        </div>
    </nav>
    <c:set var="flag" scope="page" value="${1}"/>
    <ul class="collection">
        <c:set var="gquizzes" scope="page" value="${studentMetrics.gradedCalculatedQuizzes}"/>
        <c:set var="hquizzes" scope="page" value="${studentMetrics.quizzesHistogram}"/>
        <c:set var="wquizzes" scope="page" value="${studentMetrics.wholeClassQuizzes}"/>
        <li class="collection-item">
            <div class="row">
                <div class="col s7">
                    <div class="col s7">
                        <p>Upcoming Quizzes</p>
                    </div>
                </div>
                <div class="col s5">
                    <button type="button" class="btn light-blue lighten-1" onclick="upcoming(${studentMetrics.upcoming()})">View</button>
                </div>
            </div>
        </li>
        <c:forEach items="${studentMetrics.gradedQuizzes}" var="quiz">
            <li class="collection-item">
                <div class="row">
                    <div class="col s7">
                        <div class="col s7">
                            <p>${quiz.title}</p>
                        </div>
                    </div>
                    <div class="col s5">
                        <button id="metricsButton" type="button" class="btn light-blue lighten-1"
                                onclick="metrics(${quiz.id},${wquizzes},${gquizzes},${hquizzes}, ${studentMetrics.getNotes(quiz.id)});">
                            Results
                        </button>
                        <c:if test="${flag == 1}">
                            <script>metrics(${quiz.id}, ${wquizzes}, ${gquizzes}, ${hquizzes}, ${studentMetrics.getNotes(quiz.id)});</script>
                            <c:set var="flag" scope="page" value="${0}"/>
                        </c:if>
                    </div>
                </div>
            </li>
        </c:forEach>
    </ul>
</div>

<%@include file="footer.jsp" %>
<script src="assets/js/jquery.clearsearch.js"></script>

</body>
</html>
