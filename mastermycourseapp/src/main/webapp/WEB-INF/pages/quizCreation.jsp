<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" %>

<%--
  Authors: Zach Lerman, James DeCarlo, and Jose Rodriguez

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<jsp:useBean id="createQuiz" class="com.mastermycourse.beans.CreateQuizBean" scope="session" />
<jsp:useBean id="course" class="com.mastermycourse.beans.CourseBean" scope="session" />
<%-- should be changed to course page --%>
<%
    createQuiz.setApplicationContext(RequestContextUtils.findWebApplicationContext(request));
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
    <title>Course Creation Page</title>
    <link rel="icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <script src="assets/js/jquery.js"></script>
    <link rel="stylesheet" href="assets/materialize/css/materialize.css">
    <script src="assets/materialize/js/materialize.js"></script>
    <link rel="stylesheet" href="assets/dropify/css/dropify.css">
    <script src="assets/dropify/js/dropify.js"></script>
    <link rel="stylesheet" href="assets/css/main.css">
    <link rel="stylesheet" href="assets/css/coursecreation.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="assets/js/materialdesign.js"></script>
    <script src="assets/js/jqueryredirect.js"></script>
    <script src="assets/ace/ace.js"></script>
    <link rel="stylesheet" href="assets/css/quizcreation.css">

    <script>
        var editQuiz = ${createQuiz.editQuiz};
        <%@include file="/assets/js/quizcreation.js"%>
    </script>
</head>
<body>

<%@include file="teacherNav.jsp" %>
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
                        <p class="user-roal">Professor</p>
                    </div>
                </div>
            </li>
        </div>

        <ul id="courseOutline" class="collapsible" data-collapsible="accordion">
            <c:forEach items="${course.outline}" var="chapter">
                <li data-id="${chapter.get(0).contentModuleId}">
                    <div class="collapsible-header"><i
                            class="material-icons">filter_drama</i>${chapter.get(0).chapterTitle}</div>
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
        <div class="quiz-container">

            <nav>
                <div class="nav-wrapper deep-orange lighten-2">
                    <a href="#" class="breadcrumb">${course.courseName} Quiz Creation Tool</a>
                    <div class="float-right" style="padding-right: 10px">
                        <a  href="#" data-activates="slide-out-quiz" class="button-collapse-quiz"><i data-position="bottom" data-delay="50" data-tooltip="Open Quiz sidenav" class="tooltipped material-icons">web</i></a>
                    </div>
                </div>
            </nav>

            <c:if test="${createQuiz.editQuiz == true}">
                <!-- UPDATE QUIZ -->
                <form id="edit_quiz" method="post" action="/UpdateQuiz">
                    <div style="border: 1px dashed black; margin: 5px; padding: 5px; margin-bottom: 85px;">
                        <input type="text" id="edit_quiz_name" name="quiz_name" placeholder="Enter Quiz Name" value="${createQuiz.quizName}">
                        <input type="hidden" name="edit_quiz_questions_hidden" id="edit_quiz_questions_hidden">
                        <input type="hidden" name="edit_quiz_id" value="${createQuiz.quizId}">
                        <input type="hidden" value="none" name="chapter_quiz" class="chapter_quiz" />
                        <label class="active" for="dueEdit">Due Date</label>
                        <input id="dueEdit" class="datepicker" name="dueDate" type="date" value="${createQuiz.date}">
                        <ul style="width:98%; min-height:200px; border: 1px dashed black;" data-quizId="${createQuiz.quizId}" id="edit_quiz_ul" class="quiz_questions">
                            <c:forEach items="${createQuiz.quiz_questions}" var="question">
                                <li data-moduleId="${question.id}" data-questionId="${question.question_id}" data-qt="${question.questionType}" data-question="${question.question}">
                                    <div class="row grey" style="border: 1px solid black; color:white;">
                                        <div class='col s3'>${question.question}</div>
                                        <div class="col s3">Points: <input class="input-attempts" id="points${question.question_id}" style="display:inline-block; width: 20%;" type="number" value="${question.points}"></div>
                                        <div class="col s3">Allowed Number of Attempts <input class="input-attempts" id="attempts${question.question_id}" style="display:inline-block; width: 20%;" type="number" value="${question.submissions}"/></div>
                                        <div style="cursor:pointer" class="col s3">
                                            <div class="transferTest" style="color: white;">Remove from Quiz <i class="material-icons tiny">trending_flat</i></div>
                                        </div>
                                    </div>
                                </li>
                            </c:forEach>
                        </ul>
                        <p class="flow-text"><strong style="background: lightgoldenrodyellow;">Below enter any notes you would like your students to see about that quiz:</strong></p>
                        <textarea id="teacher-notes-textarea" class="materialize-textarea" type="textarea" name="teacherNotes">${createQuiz.teacherNotes}</textarea>
                        <br/>
                        <div class="row">
                            <div class="col s12 m4">
                        <button type="button" id="cancel_quiz_edit" class="btn grey" style="float:left;width: 98%;">Cancel</button>
                            </div>
                                <div class="col s12 m5">
                            <button type="button" id="delete_quiz_button" class="btn red" style="float:left; width: 98%;">Delete</button>
                        </div>
                            <div class="col s12 m3">
                            <button type="button" id="update_quiz_button" class="btn blue" style="float:right;top: 10px; width: 98%; position: relative; left: 10px;">Update</button>
                        </div>
                        </div>
                        </div>
                </form>
                <!-- END UPDATE QUIZ -->
            </c:if>
            <div style="margin-bottom: 85px;">
                <!-- CREATE QUIZ -->
                <form id="quiz_form" action="/CreateQuiz" method="post">
                    <div id="quiz_div"
                         style="display: none; border: 1px dashed black; margin: 5px; padding: 5px; margin-bottom: 85px;">
                        <h4 class="blue-text">Create A Quiz</h4>
                        <input required type="text" id="quiz_name" name="quiz_name" placeholder="Enter Quiz Name">
                        <label class="flow-text active" for="due">Due Date</label>
                        <input id="due" class="datepicker" name="dueDate" type="date">
                        <ul class="quiz_questions" id="quiz_questions" class="connectedSortable sort_list" style="width:98%; min-height:200px; border: 1px dashed black;">
                        </ul>
                        <br/>
                        <p>Below enter any notes you would like your students to see about that quiz:</p>
                        <textarea class="materialize-textarea" type="textarea" name="teacherNotes"></textarea>
                        <br/>
                        <input type="hidden" name="quiz_questions_hidden" id="quiz_questions_hidden" />
                        <input type="hidden" value="none" name="chapter_quiz" class="chapter_quiz" />
                        <button type="button" onclick="location.reload()" class="btn grey" id="cancel_quiz_button">Cancel</button>
                        <button type="button" class="btn blue" id="submit_quiz_button">Create Quiz</button>
                    </div>
                </form>
                <!-- END CREATE QUIZ -->

                <!-- QUESTION CREATION -->
                <form id="question_form" action="/SubmitQuestion" method="post">
                    <!-- Question Creation -->
                    <div id="answer_div" style="display: none; border: 1px dashed black; margin: 5px; padding: 5px;">
                        <input class="required" id="questionPrompt" name="questionPrompt" type="text" placeholder="Input Question Prompt">
                        <input type="hidden" name="hidden_question_type" id="hidden_question_type">
                        <div class="input-field">
                            <select id="question_select">
                                <option value="" disabled selected>Select Question Type</option>
                                <option value="multipleChoice">Multiple Choice</option>
                                <option value="trueFalse">True/False</option>
                                <option value="exactAnswer">Exact Answer</option>
                                <option value="oversight">Oversight Question</option>
                                <option value="code">Code Question</option>
                            </select>
                        </div>
                        <div class="multipleChoice question_div">
                            <ul class="answers">
                                <input class="required_radio" type="radio" name="mult_radio" class="answer_radio" value="a" id="q1a"><label
                                    for="q1a"><input class="required" type="text" name="q1_prompt"
                                                     placeholder="Here input an answer choice."></label><br/>
                                <input class="required_radio" type="radio" name="mult_radio" class="answer_radio" value="b" id="q1b"><label
                                    for="q1b"><input class="required" type="text" name="q2_prompt"
                                                     placeholder="Here input an answer choice."></label></label><br/>
                                <input class="required_radio" type="radio" name="mult_radio" class="answer_radio" value="c" id="q1c"><label
                                    for="q1c"><input class="required" type="text" name="q3_prompt"
                                                     placeholder="Here input an answer choice."></label></label><br/>
                                <input class="required_radio" type="radio" name="mult_radio" class="answer_radio" value="d" id="q1d"><label
                                    for="q1d"><input class="required" type="text" name="q4_prompt"
                                                     placeholder="Here input an answer choice."></label></label><br/>
                            </ul>
                        </div>
                        <div class="trueFalse question_div">
                            <ul class="answers">
                                <input class="required_radio" type="radio" name="tf_radio" class="answer_radio" value="true" id="qtrue"><label
                                    for="qtrue">True</label><br/>
                                <input class="required_radio" type="radio" name="tf_radio" class="answer_radio" value="false"
                                       id="qfalse"><label for="qfalse">False</label><br/>
                            </ul>
                        </div>
                        <div class="exactAnswer question_div">
                            <label><input class="required" type="text" name="a_exact" placeholder="Enter Answer."></label>
                        </div>
                        <div class="oversight question_div">
                        </div>
                        <div class="code question_div">
                            <input type="hidden" name="language" id="language" value="3"/>
                            <div>
                                <text class="blue-text">Select a Programming Language:</text>
                                <select id="programming_languages">
                                    <option value="3" selected>Java</option>
                                    <option value="5">Python</option>
                                </select>
                            </div>
                            <text class="flow-text blue-text">Test Cases:</text> <br/>
                            <table>
                                <tr>
                                    <th>Input to stdin</th>
                                    <th>Expected Output from stdout</th>
                                </tr>
                                <tr>
                                    <!--<td>Test Case 1</td>-->
                                    <td><label><input id="stdin-input-two" class="required" type="text" name="stdin" placeholder="Enter input to stdin"></label></td>
                                    <td><label><input id="stdout-input-two" class="required" type="text" name="stdout" placeholder="Enter Expected Answer to stdout"></label></td>
                                    <td><button class="delete_test_case btn red" type="button" style="top: -10px;position: relative;">Delete Test Case</button></td>
                                </tr>
                            </table>
                            <button class="btn blue" type="button" onclick="addTestCase();">Add Test Case</button>
                            <div>
                                <text class="flow-text blue-text">Enter template code. When students answer this question they will be provided with this code.</text>
                                <div id="editor" class="editor">
                                </div>
                            </div>
                        </div>

                        <button type="button" id="cancel_answer_div" class="btn grey" style="float:left;">Cancel</button>
                        <button id="submit-question" class="btn waves-effect waves-light blue" type="button" onclick="submitQuestion();">Submit Question</button>
                    </div>
                </form>
                <!-- END QUESTION CREATION -->

                <!-- UPDATE QUESTION -->
                <form id="edit_question_form" action="/UpdateQuiz" method="post">
                    <!-- Question Editing/Deleting -->
                    <input id="edit_question_hidden_type" type="hidden" name="question_type">
                    <input id="edit_question_hidden_id" name="question_id" type="hidden">

                    <div id="question_edit" style="display: none; border: 1px dashed black; margin: 5px; padding: 5px;padding-bottom: 28px;">
                        <input id="edit_question_prompt" name="questionPrompt" type="text">
                        <div id="edit_question_template"></div>
                        <div class="row">
                            <div class="col s12 m4">
                        <button type="button" id="cancel_question_edit" style="float:left; width:98%" class="btn waves-effect waves-light grey" type="submit">Cancel</button>
                        </div>
                            <div class="col s12 m5">
                        <button id="delete_button" style="float:left; width: 98%;" class="btn waves-effect waves-light red">Delete Question</button>
                            </div>
                            <div class="col s12 m3">
                                <button onclick="updateQuestion();" type="button" style="float:right; top:10px; width:98%; left:10px;" class="btn waves-effect waves-light blue">Update</button>
                            </div>
                            </div>
                    </div>
                </form>
                <!-- END UPDATE QUESTION -->
            </div>
        </div>
    </div>

    <!-- Template Elements Start -->
    <div id="template_div" style="display:none;">
        <div id="code_template" class="codeTemplate">
            <input type="hidden" name="language" id="language_template" value="3"/>
            <input type="hidden" name="stdin" id="stdin_template" />
            <input type="hidden" name="stdout" id="stdout_template" />
            <input type="hidden" name="code_template" id="hidden_code"/>
            <div>
                <text class="blue-text flow-text">Select a Programming Language:</text>
                <select id="programming_languages_template">
                    <option value="3">Java</option>
                    <option value="5">Python</option>
                </select>
            </div>
            Test Cases: <br/>
            <table id="test_case_template">
                <tr>
                    <th>Input to stdin</th>
                    <th>Expected Output from stdout</th>
                </tr>
            </table>
            <button class="btn blue" type="button" onclick="editTestCase();">Add Test Case</button>
            <div id="edit_code_div">
                <text class="blue-text">Enter template code. When students answer this question they will be provided with this code.</text>
                <!--<div id="editor_template" class="editor"></div>-->
            </div>
        </div>

        <div class="trueFalseTemplate">
            <ul class="answers">
                <input class="required_radio" id="true_radio" type="radio" name="tf_radio" class="answer_radio" value="true"><label for="true_radio">True</label><br/>
                <input class="required_radio" id="false_radio" type="radio" name="tf_radio" class="answer_radio" value="false"><label for="false_radio">False</label><br/>
            </ul>
        </div>

        <div class="exactAnswerTemplate">
            <label><input class="required" type="text" name="a_exact" placeholder="Enter Answer." value=""></label>
        </div>

        <div class="multipleChoiceTemplate">
            <ul class="answers">
                <input checked type="radio" name="mult_radio" class="answer_radio required_radio" value="a" id="correct_answer"><label for="correct_answer"><input id="correct_answer_template" class="required" value=""type="text" name="q1_prompt"></label><br/>
                <input type="radio" name="mult_radio" class="answer_radio required_radio" value="b" id="wrong_answer1"><label for="wrong_answer1"><input id="wrong_answer1_template" class="required" value="" type="text" name="q2_prompt"></label></label><br/>
                <input type="radio" name="mult_radio" class="answer_radio required_radio" value="c" id="wrong_answer2"><label for="wrong_answer2"><input id="wrong_answer2_template" class="required" value="" type="text" name="q3_prompt"></label></label><br/>
                <input type="radio" name="mult_radio" class="answer_radio required_radio" value="d" id="wrong_answer3"><label for="wrong_answer3"><input id="wrong_answer3_template" class="required" value="" type="text" name="q4_prompt"></label></label><br/>
            </ul>
        </div>

        <div class="test_case_row_div">
            <table>
                <tr id="test_case_row">
                    <td><label><input class="required input-attempts" type="text" name="stdin" placeholder="Enter input to stdin"></label></td>
                    <td><label><input class="required input-attempts" type="text" name="stdout" placeholder="Enter Expected Answer to stdout"></label></td>
                    <td><button class="delete_test_case btn red" type="button" style="top: -10px;position: relative;">Delete Test Case</button></td>
                </tr>
            </table>
        </div>
    </div>
    <!-- Template Elements End -->


</main>
<!-- RIGHT PANEL MODULE AREA -->
<ul id="slide-out-quiz" class="side-nav fixed">
<%--<div class="side-nav fixed" id="notesSideNav">--%>
<div class="module-container teal lighten-5">
    <nav>
        <div class="nav-wrapper cyan">
            <div class="col s12">
                <text href="#" class="breadcrumb">Module Area</text>
            </div>
        </div>
    </nav>
    <button id="quiz_button" class="btn lime lighten-2 blue">Create Quiz</button>
    <button id="add_question_button" class="btn light-green lighten-2">Add Question</button>

    <!--Right Panel Quiz Questions, need search options -->
    <text class="flow-text">Questions List:</text>
    <br/>
    <div class="input-field col s12">
        <select id="filter_questions">
            <option value="all" selected>All</option>
            <option value="multipleChoice">Multiple Choice</option>
            <option value="trueFalse">True/False</option>
            <option value="exactAnswer">Exact Answer</option>
            <option value="oversight">Oversight</option>
            <option value="code">Code Question</option>
        </select>
        <label class="flow-text">Filter Questions</label>
    </div>

    <div class="question-container">
        <ul id="question_list" class="connectedSortable sort_list" style="min-width: 50px; min-height: 50px;">
            <c:forEach items="${createQuiz.question_list}" var="question">
                <c:choose>
                    <c:when test="${question.questionType=='multipleChoice'}">
                        <li data-moduleId="${question.id}" data-questionId="${question.question_id}"
                        data-qt="${question.questionType}" data-answer="${question.answer}"
                        data-wrong1="${question.prompts[1]}" data-wrong2="${question.prompts[2]}"
                        data-wrong3="${question.prompts[3]}" data-question="${question.question}">
                    </c:when>
                    <c:when test="${question.questionType=='trueFalse' || question.questionType=='exactAnswer'}">
                        <li data-moduleId="${question.id}" data-questionId="${question.question_id}" data-qt="${question.questionType}"
                        data-answer="${question.answer}"
                        data-question="${question.question}">
                    </c:when>
                    <c:when test="${question.questionType=='oversight'}">
                        <li data-moduleId="${question.id}" data-questionId="${question.question_id}" data-qt="${question.questionType}"
                        data-question="${question.question}">
                    </c:when>
                    <c:when test="${question.questionType=='code'}">
                        <li data-moduleId="${question.id}" data-questionId="${question.question_id}" data-templateCode="${question.template_code.replace("\"", "&quot;")}"
                            data-qt="${question.questionType}" data-answer="${question.answer}" data-question="${question.question}"
                            data-language="${question.language}"
                            data-testCases="${question.promptsToString()}">
                    </c:when>
                </c:choose>

                <div class="transfer_button"><i class="material-icons tiny flip">trending_flat</i>
                    Add to Quiz
                </div>
                ${question.question}
                </li>
            </c:forEach>
        </ul>
    </div>

    <div>
        <text class="flow-text">Quiz List:</text>
        <div class="question-container">
            <ul id="quiz_list" class="sort_list">
                <c:forEach items="${createQuiz.quizzes}" var="quiz">
                    <li onclick="editQuizData(${quiz.id}, '${quiz.title}', '${quiz.teacherNotes}', '${quiz.date}')" data-title="${quiz.title}" data-id="${quiz.id}"
                        >${quiz.title}</li>
                </c:forEach>
            </ul>
        </div>
    </div>

</div>
</ul>

<%@include file="footer.jsp" %>

</body>
</html>