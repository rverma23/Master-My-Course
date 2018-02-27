
<%--
  Authors: Zach Lerman, James DeCarlo, and Jose Rodriguez

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<jsp:useBean id="studentCourse" class="com.mastermycourse.beans.StudentCourseBean" scope="session"/>

<!DOCTYPE html>
<html lan="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Join Course</title>
    <link rel="icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="assets/js/jquery.js"></script>
    <link rel="stylesheet" href="assets/materialize/css/materialize.css">
    <script src="assets/materialize/js/materialize.js"></script>
    <link rel="stylesheet" href="assets/dropify/css/dropify.css">
    <script src="assets/dropify/js/dropify.js"></script>
    <link rel="stylesheet" href="assets/css/main.css">
    <link rel="stylesheet" href="assets/css/joincourse.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="assets/js/materialdesign.js"></script>

    <script src="assets/js/jqueryredirect.js" ></script>


    <script>
        function changeCourse(id) {
            var data = {
                courseId: id
            };

            $.redirect("/StudentChangeCourse", data);
        }

        function joinCourse(courseId, email) {
            var data = {courseId: courseId, email: email};
            $.redirect("/JoinCourse", data);
        }
    </script>
</head>
<body>

<%@include file="studentNav.jsp"%>
<main>
<div class="row">
    <div class="col s12">
        <img src="assets/images/placeholder/cityscape-line1.png" style="max-width:99%; max-height: 100%">
        <div class="search-container">
            <div class="nav-wrapper">
                <div class="">
                    <div class="input-field">
                        <input id="search" type="text" required placeholder="Search Courses">
                        <label class="label-icon" for="search"><i class="material-icons flow-text">search</i></label>
                    </div>
                </div>
            </div>

            <form action="/JoinPrivateCourse" method="post">
                <div class="row">
                    <div class="col s2"></div>
                    <div class="input-field col s4" style="background-color: white; top: -25px; height: 54px; border-radius: 11px;">
                        <input placeholder="Input Class Code" name="classCode" type="text" class="validate flow-text" required="" aria-required="true">
                    </div>
                    <div class="col s2">
                        <input type="submit" value="Submit" class="waves-effect waves-light btn blue">
                    </div>
                </div>
            </form>
        </div>

        <div class="classes-available-container">
            <nav class="header deep-orange lighten-2">Available Courses</nav>
            <c:forEach items="${studentCourse.teacherCourses}" var="c">
                <div class="col s12 l4 course">
                    <div class="card horizontal blue-grey lighten-1">
                        <div style="background: #86BB71;padding: 1px;border-right: 1px solid darkolivegreen;">
                            <img src="${c.teacherImage}" class="circle responsive-img valign profile-image">
                        </div>
                        <div class="card-stacked" style="background: #86BB71;">
                            <div class="card-content">
                                <h5 class="flow-text teacherName">${c.teacherName}</h5>
                                <p class="courseName"><i>${c.name}</i></p>
                                <p class="description">${c.description}</p>
                            </div>
                            <div class="card-action">
                                <a class="btn blue" onclick="joinCourse(${c.courseId}, '${sessionScope.userEmail}')">Join</a>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>

        </div>
    </div>
</div>
</main>
<%@include file="footer.jsp" %>

<script src="assets/js/jquery.clearsearch.js"></script>
<script>
    $('#search').keyup(function () {
        var search = $(this).val().toLowerCase();
        $('.course').each(function () {
            var teacherName = $(this).find('.teacherName').text().toLowerCase();
            var courseName = $(this).find('.courseName').text().toLowerCase();
            var description = $(this).find('.description').text().toLowerCase();
            (teacherName.includes(search) || courseName.includes(search) || description.includes(search))  ? $(this).show() : $(this).hide();
            $('body').scrollTop(0);
        });
    });

    $('#search').clearSearch({callback:function(){
        $('.course').each(function () {
            $(this).show();
        });
    }});
</script>
</body>
</html>

