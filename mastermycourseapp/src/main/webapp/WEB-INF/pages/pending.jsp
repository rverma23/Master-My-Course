
<%--
  Author: James DeCarlo

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
<div class="row messageDiv">
    <h>Account is Not Enabled</h>
    <p>Your account at Master My Course is not currently enabled if you recently signed up your account will be reviewed
        and emailed upon Approval or Decline of your account.</p>
    <p>If your are seeing this and your account was active your account has been disabled this could be due to
        Non payment or copyright infringement and you would have been emailed. Please contact us to resolve this matter.</p>
</div>
</main>
<%@include file="footer.jsp" %>
</body>
</html>