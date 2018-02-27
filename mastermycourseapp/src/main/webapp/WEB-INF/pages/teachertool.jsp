
<%--
  Authors: Zach Lerman, James DeCarlo, Rahul Verma and Jose Rodriguez

--%>
<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<jsp:useBean id="userBean" class="com.mastermycourse.beans.UserBean" scope="session"/>
<jsp:useBean id="course" class="com.mastermycourse.beans.CourseBean" scope="session"/>
<jsp:useBean id="studentAnswers" class="com.mastermycourse.beans.StudentAnswerBean" scope="session"/>
<jsp:useBean id="teacherMetrics" class="com.mastermycourse.beans.TeacherMetricsBean" scope="session"/>

<%-- should be changed to course page --%>
<%
    teacherMetrics.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
    studentAnswers.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
    userBean.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
    course.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
%>

<!DOCTYPE html>
<html lan="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Teacher Metrics</title>
    <link rel="icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <script src="assets/js/jquery.js"></script>
    <link rel="stylesheet" href="assets/materialize/css/materialize.css">
    <script src="assets/materialize/js/materialize.js"></script>
    <link rel="stylesheet" href="assets/css/main.css">
    <link rel="stylesheet" href="assets/css/teachertool.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="assets/js/materialdesign.js"></script>
    <script src="assets/js/jqueryredirect.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.1.4/Chart.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/mathjs/3.12.1/math.min.js"></script>

    <style>
        html, body {
            height: 100%;
        }

        main {
            position: absolute;
            top: 65px;
            left: 0px;
            right: 0px;
            margin-bottom: 85px;
        }

        footer {
            position: fixed;
            bottom: 0;
            width: 100%;
            height: 75px;
        }

        .side-nav {
            top: 65px;
            height: calc(100% - 75px - 65px) !important;
        }

        main {
            padding-left: 300px;
        }

        #search {
            padding-left: 20px;
            width: 95%;
        }

        @media only screen and (max-width: 992px) {
            main {
                padding-left: 0;
            }
        }
    </style>
    <script>
        $(document).ready(function () {
            // Initialize collapse button
            $(".button-collapse").sideNav();
            $('ul.tabs').tabs();
        });


        // teacher clicked on specific student, show their information
        function selectStudent(studentId) {
            var data = {studentId: studentId};
            $.redirect("/SelectStudent", data);
        }

        function gradeQuiz(quizId, studentId, courseId) {
            var data = {quizId: quizId, studentId: studentId, courseId: courseId};
            $.redirect("/GradeQuiz", data);
        }

        function displayMetrics(quizzes, canvasId) {
            console.log(quizzes);
            var QuizLabels = [];
            var QuizData = [];
            var total = 0;
            var actual = 0;

            var i = 0;
            //console.log(quizzes);
            for (i = 0; i < quizzes.length; i++) {
                total = quizzes[i].totalpoints;
                actual = quizzes[i].actualpoints;
                QuizLabels.push(quizzes[i].title);
                if (total > 0) {
                    var d = actual / total;
                    d = d * 100;
                    d=d.toFixed(2);
                    QuizData.push(d);
                }
                else {
                    QuizData.push(0.00);
                }
            }

            var canvasContext;

            if (canvasId === 'MC') {

                resetCanvas('canvas', '#MC_container');
                resetCanvas('Pcanvas', '#pageMetricContainer');
                canvasContext = document.getElementById('canvas').getContext('2d');

            }
            else {

                resetCanvas('Mcanvas', '#MetricsCanvas');
                resetCanvas('Pcanvas', '#pageMetricContainer');
                canvasContext = document.getElementById('Mcanvas').getContext('2d');
            }

            var MetricsCanvas = new Chart(canvasContext, {
                type: 'bar',
                data: {
                    labels: QuizLabels,
                    datasets: [{
                        label: 'quizzes',
                        data: QuizData,
                        backgroundColor: "rgba(0, 217, 217, 1)"
                    }]
                },
                options: {
                    scales: {
                        yAxes: [{
                            ticks: {
                                max: 110,
                                min: 0,
                                stepSize: 10
                            }
                        }]
                    }
                }
            });

        }

        function displayHistograms(id, quizHistogram) {
            console.log(quizHistogram);


            var xAxisLabels = ['0-10', '10-20', '20-30', '30-40', '40-50', '50-60', '60-70', '70-80', '80-90', '90-100', '100-110'];
            var yMax = 0;

            for (var i = 0; i < quizHistogram.length; i++) {

                if (quizHistogram[i].id == id) {
                    var title = quizHistogram[i].title;
                    var histo = quizHistogram[i].histogram;

                    yMax = 0;
                    for (var j = 0; j < histo.length; j++) {
                        histo[j]=histo[j];
                        if (histo[j] > yMax) {
                            yMax = histo[j];
                        }
                    }
                    yMax = yMax + 1;


                    resetCanvas('canvas', '#MC_container');
                    resetCanvas('Pcanvas', '#pageMetricContainer');

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

        function displayTmets(chapterTimes) {

            console.log(chapterTimes);
            var labels = [];
            var data = [];
            var title = 'Chapter Metrics';

            for (var i = 0; i < chapterTimes.length; i++) {
                labels.push(chapterTimes[i].chapterTitle);
                data.push(chapterTimes[i].totalTimeSpent);
            }

            var yMax;
            if(data.length==0){
                yMax = math.max(data);
            }
            else{
                yMax=0;
            }
            var step = ((yMax / 25) | 0);

            for (var i = 0; i < chapterTimes.length; i++) {
                data[i]= data[i].toFixed(2);
            }


            resetCanvas('Mcanvas', '#MetricsCanvas');
            resetCanvas('Pcanvas', '#pageMetricContainer');
            var canvasContext = document.getElementById('Mcanvas').getContext('2d');

            new Chart(canvasContext, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: title,
                        data: data,
                        backgroundColor: "rgba(0, 217, 217, 1)"
                    }]
                },
                options: {
                    onClick: function (e) {
                        var element = this.getElementAtEvent(e);
                        console.log(chapterTimes[element[0]._index]);
                        displayPageMetrics(chapterTimes[element[0]._index]);
                    },
                    scales: {
                        yAxes: [{
                            ticks: {
                                min: 0,
                                stepSize: step
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

        function displayPageMetrics(chapter) {
            console.log(chapter)
            title = chapter.chapterTitle + " Pages Metrics";
            var labels = [];
            var data = [];

            var pm = chapter.pm;

            for (var i = 0; i < pm.length; i++) {
                labels.push(pm[i].title);
                data.push(pm[i].minutes.toFixed(2));
            }

            var yMax = math.max(data);
            var step = ((yMax / 30) | 0);


            resetCanvas('Pcanvas', '#pageMetricContainer');
            var canvasContext = document.getElementById('Pcanvas').getContext('2d');

            new Chart(canvasContext, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: title,
                        data: data,
                        backgroundColor: "rgba(0, 217, 217, 1)"
                    }]
                },
                options: {
                    scales: {
                        yAxes: [{
                            ticks: {
                                min: 0,
                                stepSize: step
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

        function resetCanvas(canvasId, containerId) {
            var canvas_html = '<canvas id=' + canvasId + '></canvas>';
            $(containerId).html(canvas_html);
        }

        function changeCourse(id) {
            var data = {
                courseId: id
            };
            $.redirect("/ChangeCourse", data);
        }


    </script>
</head>
<body>

<c:choose>
    <c:when test="${userBean.isTA() == 1}">
        <%@include file="studentNav.jsp" %>
    </c:when>
    <c:otherwise>
        <%@include file="teacherNav.jsp" %>
    </c:otherwise>
</c:choose>

<div id="slide-out" class="side-nav fixed">
    <nav>
        <div class="nav-wrapper cyan">
            <div class="col s12">
                <a href="/coursePreview.htm" class="breadcrumb">Students In Class ${course.courseName}</a>
            </div>
        </div>
    </nav>
    <ul class="collection z-depth-5 collection-students">
        <form action="/RemoveStudents" method="post">
            <li class="collection-item avatar">
                <div class="row">
                    <div class="col s6">
                        <p class="blue-text text-darken-2">Whole Class</p>
                    </div>
                    <div class="col s6">
                        <button id="WholeClassMetricsButton" type="button" class="btn blue"
                                onclick="selectStudent(-100)" style="margin: 0 0 0 8px">Metrics
                        </button>
                    </div>
                </div>
            </li>
            <c:forEach items="${course.students}" var="student">
                <li class="collection-item avatar">
                    <div class="row">
                        <div class="col s12 m12">
                                <input type="checkbox" name="studentName" id="${student.name}" value="${student.id}">
                                <label for="${student.name}">${student.name}</label>
                        </div>
                        <div class="col s12 m12">
                            <button id="metricsButton" type="button" class="btn light-blue lighten-1"
                                    onclick="selectStudent(${student.id})">Metrics
                            </button>
                        </div>
                    </div>
                </li>
            </c:forEach>

            <input type="submit" name="action" value="Remove" class="btn red" style="width: 100%">
        </form>

    </ul>
</div>

<main>
    <c:if test="${teacherMetrics.studentId == 0}">
        <script>selectStudent(-100);</script>
    </c:if>

    <c:if test="${teacherMetrics.studentId > 0}">
        <c:set var="quizzes" scope="page" value="${teacherMetrics.ungradedStudentQuizzes}"/>
        <c:choose>
            <c:when test="${quizzes.size() > 0}">
                <c:set var="quiz_string" scope="page" value="Quiz" />
                <c:if test="${quizzes.size() > 1}">
                    <c:set var="quiz_string" scope="page" value="Quizzes" />
                </c:if>
                <ul class="collapsible" data-collapsible="accordion">
                    <li>
                        <div class="collapsible-header"><span class="new badge" data-badge-caption="Quiz">${quizzes.size()}</span><i class="material-icons">list</i>Quizzes that Require Grading</div>
                        <div class="collapsible-body">
                            <ul style="cursor:pointer" class="collection">
                                <c:forEach items="${quizzes}" var="quiz">
                                    <li onclick="gradeQuiz(${quiz.id}, ${teacherMetrics.studentId}, ${course.courseId})"
                                        class="collection-item">${quiz.title}</li>
                                </c:forEach>
                            </ul>
                        </div>
                    </li>

                </ul>

            </c:when>
            <c:when test="${quizzes.size() == 0}">
                <text class="flow-text">No quizzes to grade for this student.</text>
            </c:when>
        </c:choose>

        <c:set var="gquizzes" scope="page" value="${teacherMetrics.gradedStudentQuizzes}"/>
        <c:set var="tMets" scope="page" value="${teacherMetrics.timeMetrics}"/>

        <c:if test="${gquizzes.length()>0 || tMets.length()>0}">
            <div class="row">
                <div class="col s12">
                    <ul class="tabs">
                        <c:if test="${gquizzes.length()>0}">
                            <li class="tab col s3"><a href="javascript:void(0)"
                                                      onclick="displayMetrics(${gquizzes}, 'MetricsCanvas');">Quiz
                                Grades</a>
                            </li>
                        </c:if>
                        <c:if test="${tMets.length()>0}">
                            <li class="tab col s3"><a href="javascript:void(0)"
                                                      onclick="displayTmets(${tMets});">Time Metrics</a>
                            </li>
                        </c:if>
                    </ul>
                </div>
            </div>
        </c:if>

        <div style="width:80%;">
            <div id="MetricsCanvas"></div>
        </div>
        <script>displayMetrics(${gquizzes}, 'MetricsCanvas');</script>
        <div>
            <div id="pageMetricContainer" style="width:80%;"></div>
        </div>
    </c:if>

    <c:if test="${teacherMetrics.studentId == -100}">
        <c:set var="aquizzes" scope="page" value="${teacherMetrics.gradedWholeClassQuizzesArrayList}"/>
        <c:set var="wquizzes" scope="page" value="${teacherMetrics.gradedWholeClassQuizzes}"/>
        <c:set var="hquizzes" scope="page" value="${teacherMetrics.histogramQuizzes}"/>
        <c:choose>
            <c:when test="${aquizzes.size() > 0}">
                <div class="row">
                    <div class="col s12">
                        <ul class="tabs">
                            <li class="tab"><a href="javascript:void(0)"
                                               onclick="displayMetrics(${wquizzes},'MC')">Class
                                Averages</a>
                            </li>
                            <c:forEach items="${aquizzes}" var="quiz">
                                <li class="tab">
                                    <a href="javascript:void(0)"
                                       onclick="displayHistograms('${quiz.id}',${hquizzes});">${quiz.title}</a>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
                <div>
                    <div id="MC_container" style="width:80%;"></div>
                </div>
                <script>displayMetrics(${wquizzes}, 'MC');</script>
            </c:when>
            <c:when test="${aquizzes.size() == 0}">
                <text class="flow-text">No quizzes.</text>
            </c:when>
        </c:choose>
    </c:if>

    </main>

    <%@include file="footer.jsp" %>

    <script src="assets/js/jquery.clearsearch.js"></script>
    <script>

    </script>
    </body>
</html>
