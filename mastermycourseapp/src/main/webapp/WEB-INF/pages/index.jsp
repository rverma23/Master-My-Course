
<%--
  Authors: Zach Lerman, James DeCarlo, and Jose Rodriguez

--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="userBean" class="com.mastermycourse.beans.UserBean" scope="session"/>

<!DOCTYPE html>
<html lan="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Master My Course - Login</title>
    <link rel="icon" href="/assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="/assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <script src="/assets/js/jquery.js"></script>
    <link rel="stylesheet" href="/assets/materialize/css/materialize.css">
    <script src="/assets/materialize/js/materialize.js"></script>
    <link rel="stylesheet" href="/assets/css/main.css">
    <link rel="stylesheet" href="/assets/css/index.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="/assets/js/materialdesign.js"></script>
    <script src="/assets/js/jqueryredirect.js" ></script>
</head>
<body>
<main>
        <div id="carouselHomePage" class="carousel carousel-slider center" data-ride="carousel">
            <div class="carousel-fixed-item center" id="login-register">
                <a class="btn waves-effect blue lighten-2 white-text darken-text-2" id="signInButton">Log In/Register</a>
            </div>

            <div class="carousel-fixed-item center" id="title-carousel">
                <h4 class="white-text">Master My Course</h4>
            </div>

            <div class="carousel-item white-text active">
                <div class="context-login">
                    <h1>Teachers</h1>
                    <h5 class="flow-text blue-text text-lighten-2">
                        Easily convert a PDF Text Book into a online course complete with text to speech in minutes.
                        Test and Quiz creation with all standard question formats including coding questions.
                        Engage with your Students through chat and private messaging and track their Progress.
                    </h5>

                </div>
                <img class ="image-index" src="/assets/images/carousel/person-apple-laptop-notebook.jpg">
            </div>

            <div class="carousel-item white-text">
                <div class="context-login">
                    <h1>Students</h1>
                    <h5 class="flow-text blue-text text-lighten-2">
                        Learn from the comfort of your own home at your pace or under the guidance and demand of a professor.
                        Courses are available in image, standard text and audio for what ever your preference.
                        One click registration to join with google so start today!
                    </h5>
                </div>
                <img class ="image-index" src="/assets/images/carousel/pexels-photo-169915.jpeg">
            </div>

            <div class="carousel-item white-text">
                <div class="context-login">
                    <h1>Virtual Class Room</h1>
                    <h5 class="flow-text blue-text text-lighten-2">
                        Virtual class at the tips of your fingers day or night. Busy schedule no problem log in any time of day and
                        we will be here for all of your learning needs.
                    </h5>
                </div>
                <img class ="image-index" src="/assets/images/carousel/notes-macbook-study-conference.jpg">
            </div>

            <div class="carousel-item white-text">
                <div class="context-login">
                    <h1>Collaboration</h1>
                    <h5 class="flow-text blue-text text-lighten-2">
                        Chat with other Students, Teaching Assistants and Professors in real time. Have a question
                        just looking for a little guidance login to the course chat room get help in a snap.
                    </h5>
                </div>
                <img class ="image-index" src="/assets/images/carousel/people-apple-iphone-writing.jpg">
            </div>
        </div>
</main>
<%@include file="footer.jsp" %>
<script>
    function signIn(auth2) {
        var profile = auth2.currentUser.get().getBasicProfile();
        var data = {
            state: '${sessionScope.state}',
            userName: profile.getName(),
            userEmail: profile.getEmail(),
            userId: profile.getId(),
            userImage: profile.getImageUrl()
        };

        $.redirect("/Login", data);
    }

    // on google client API load.
    function authInit() {
        console.log("init");
        gapi.load('auth2', function() {
            auth2 = gapi.auth2.init({
                client_id: '62228642744-d2acfs521ah38phingjmpq6l0lui7oju.apps.googleusercontent.com',
                scope: 'profile'
            });

            if (auth2.isSignedIn.get()) {
                signIn(auth2);
            }

            // sign in handler
            $("#signInButton").click(function() {
                // open sign in dialog
                auth2.signIn().then(function() {
                    signIn(auth2);
                });
            });
        });
    }
</script>
<!-- Google Sign in APIs -->
<script src="https://apis.google.com/js/platform.js?onload=authInit" async defer></script>

</body>
</html>
