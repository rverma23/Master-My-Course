
<%--
  Authors: Zach Lerman, James DeCarlo, and Jose Rodriguez

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lan="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Master My Course - Registration</title>
    <link rel="icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <script src="assets/js/jquery.js"></script>
    <link rel="stylesheet" href="assets/materialize/css/materialize.css">
    <script src="assets/materialize/js/materialize.js"></script>
    <link rel="stylesheet" href="assets/css/main.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="assets/js/materialdesign.js"></script>

    <style>
        html,body{
            height: 100%;
        }
        header{
            position: fixed;
            width: 100%;
            top: 0;
            z-index: 9999;
        }
        main{
            position: absolute;
            top: 65px;
            left: 0px;
            right: 0px;
            margin-bottom: 85px;
        }
        footer{
            position: fixed;
            bottom: 0;
            width: 100%;
            height: 75px;
            z-index: 9999;
        }
    </style>


    <script>
    function radioSelect() {
        var selected = document.querySelector('input[name="userType"]:checked').value;
        var studentForm = document.getElementById("studentForm");
        var teacherForm = document.getElementById("teacherForm");
        var title = document.getElementById("title");
        if(selected == "student"){
            teacherForm.style.display = "none";
            studentForm.style.display = "block";
            title.innerHTML = "Start Learning!";
        } else if(selected == "teacher"){
            studentForm.style.display = "none";
            teacherForm.style.display = "block";
            title.innerHTML = "Start Teaching!";
        }
    }

    </script>
</head>
<body>

<header>
<nav>
    <div class="nav-wrapper" style="padding-left: 10px; padding-right: 10px;">
        <a href="#" class="brand-logo left">Master My Course</a>
        <ul class="right">
            <li><a href="/Logout">Logout</a></li>
        </ul>
    </div>
</nav>
</header>
<main>
<div class="row">
    <div class="col s0 m3 l4"></div>
    <div class="col s12 m6 l4">
        <div style="display:block; hpeight:650px">
            <h5 class="center-align" id="title">Start Learning!</h5>
            <hr>
            <div class="row">
                <h5>Hello ${sessionScope.userName}</h5>
                <div class="row">
                    <p>
                        <input type="radio" name="userType" value="student" id="student" onclick="radioSelect()" checked/>
                        <label for="student">Student</label>
                    </p>
                    <p>
                        <input type="radio" name="userType" value="teacher" id="teacher" onclick="radioSelect()"/>
                        <label for="teacher">Teacher</label>
                    </p>
                </div>
                <div id="studentForm" class="row">
                    <form action="/StudentRegistration" method="post">
                        <button class="btn waves-effect waves-light center-align red" type="submit" name="action">Register with Google
                            <i class="material-icons right">send</i>
                        </button>
                    </form>
                </div>
                <div id="teacherForm" class="row" hidden>
                    <form action="/TeacherRegistration" method="post">
                        <div class="input-field row">
                            <input type="text" maxlength="45" name="schoolName" id="schoolName" class="validate" required="" aria-required="true"/>
                            <label for="schoolName">School Name</label>
                        </div>
                        <div class="input-field row">
                            <textarea name="description" id="description" maxlength="2000" class="materialize-textarea validate" required="" aria-required="true"></textarea>
                            <label for="description">Tell us about you and your school and the courses you will offer</label>
                        </div>
                        <button class="btn waves-effect waves-light center-align red" type="submit" name="action">Register with Google
                            <i class="material-icons right">send</i>
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <div class="col s0 m3 l4"></div>
</div>
</main>
<%@include file="footer.jsp" %>
</body>
</html>