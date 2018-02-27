
<%--
  Authors: James DeCarlo and Jose Rodriguez

--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<head>
    <title>Private Chat</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="assets/images/icon%20pack/favicon.ico" type="image/x-icon">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="assets/css/bootstrap.css" rel="stylesheet" type="text/css">

    <!--Menu-->
    <link rel="stylesheet" href="assets/css/style.css" type="text/css" />
    <link rel="stylesheet" href="assets/css/menu.css" type="text/css" />
    <link rel="stylesheet" href="assets/css/main.css" type="text/css" />
    <link rel="stylesheet" href="assets/css/slicknav.css">

    <!--Multipage Style-->
    <link rel="stylesheet" href="assets/css/vegas-slider.css" type="text/css" />
    <link rel="stylesheet" href="assets/css/colors/vegas-slider-color.css" id="color" type="text/css" />
    <!--Multipage Style-->

    <script>
        if(window.location.protocol == "http:"){
            window.location = window.location.href.replace(/^http:/, 'https:');
        }
    </script>
    <style>

        .video-background {
            height: 60%;
            position: relative;
            background-color: black;
            width: 100%;
            max-width: 100%;
            z-index: 0;
        }

        .video-background:fullscreen
        {
            height: 100%!important;
        }
        .video-background:-webkit-full-screen
        {
            height: 100%!important;
        }
        .video-background:-moz-full-screen
        {
            height: 100%!important;
        }

        #my-video {
            position: absolute;
            top: 0;
            right: 0;
            z-index: 10;
            width: 20%;
            height: 100%
        }         

        #my-video video {                        
            padding: 0;
            margin: 0;
            width: 100%;
            max-height: 100%;

        }

        #their-video {
            width: 100%;
            height: 100%;
        }
        #their-video video {   
            width: 100%;
            height: 100%;
            z-index: 1;
        }

        html, body {
            width: 100%;
            height: 100%;
            margin: 0;
            padding: 0;
        }

        #chatWindow {
            height: 25%;
            margin-top: 5px;
            margin-left: 1%;
            margin-right: 1%;
            width: 98%;
            background:white; 
            border: 1px solid black; 
            border-radius: 10px; 
            overflow-y:auto; 
            padding:10px;
            word-break: break-all;
        }

        .main {
            width: 100%;
            height: 100%;
        }

        .chatInputWraper {
            display: table;
            width: 98%;
            margin-left: 1%;
            height: 12%;
            margin-top: 1%;

        }

        .buttonWrapper{             
            width: 120px;           
            display: table-cell;
            vertical-align:top;
            padding-left: 10px;
        }        

        .chatMessageWraper {
            width: auto;
            display: table-cell;
        }
        #chatMessage {
            width: 100%;
            height: 100%;
            border-radius: 10px; 
            padding:10px;
        }
    </style>
</head>
<body>

    <!-- =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= -->
    <!-- My Phone Number & Dial Areas -->
    <!-- =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= -->
    <div class="main">
        <div id="video-fullscreen" class="video-background">
            <div id="my-video"></div>
            <div id="their-video"></div>
        </div>
        <div id="chatWindow" class="card-panel"></div>
        <div class="chatInputWraper">                       
            <div class="chatMessageWraper">
                <textarea class="form-control" id="chatMessage" onFocus="this.style.outline = 'none'"></textarea>
            </div>
            <div class="buttonWrapper">
                <button onMouseUp="sendMessage()" id="sendMessage" class="btn blue btn-3 btn-3e" >Send</button>
            </div> 
        </div>
    </div>

    <script src="assets/js/jquery-2.2.3.min.js"></script>
    <script src="assets/js/pubnub-3.14.5.min.js"></script>
    <script src="assets/js/webrtc.js"></script>
    <script src="assets/js/sound.js"></script>
    <script>
                    var myNumber = '${sessionScope.userId}';
                    var myName = '${sessionScope.userName}';
    </script>
    <script src="assets/js/privateChat.js"></script>
</body>
</html>
