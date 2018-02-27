<%--
  Authors: Zach Lerman, James DeCarlo, Rahul Verma and Jose Rodriguez

--%>
<script>
    $(document).ready(function() {
        $(".modal").modal();
        $(".button-collapse").sideNav();
        $(".dropdown-button").dropdown();
    });

    function studentChangeCourse(id) {
        var data = {
            courseId: id
        };
        $.redirect("/StudentChangeCourse", data); // go to TA course
    }

    function taCourse(id) {
        var data = {
            courseId: id
        };
        $.redirect("/TACourse", data); // go to TA course
    }


    function setStudent(email){
        var data = {email: email};
        $.redirect("/SetStudentMetrics", data);
    }
</script>

<header id="header" class="page-topbar">

    <div class="navbar-fixed">
        <ul id="ta_courses" class="dropdown-content">
            <c:forEach items="${studentCourse.TACourses}" var="c">
                <li onclick="taCourse(${c.value})"><a>${c.key}</a></li>
            </c:forEach>
        </ul>
        <ul id="dropdown1" class="dropdown-content">
            <c:forEach items="${studentCourse.courses}" var="c">
                <li onclick="studentChangeCourse(${c.value})"><a>${c.key}</a></li>
            </c:forEach>
            <li class="divider"></li>

            <li><a href="joinCourse.htm">Join New Course</a></li>
        </ul>

        <ul id="ta_courses_mobile" class="dropdown-content">
            <c:forEach items="${studentCourse.TACourses}" var="c">
                <li onclick="taCourse(${c.value})"><a>${c.key}</a></li>
            </c:forEach>
        </ul>
        <ul id="dropdown1_mobile" class="dropdown-content">
            <c:forEach items="${studentCourse.courses}" var="c">
                <li onclick="studentChangeCourse(${c.value})"><a>${c.key}</a></li>
            </c:forEach>
            <li class="divider"></li>

            <li><a href="joinCourse.htm">Join New Course</a></li>
        </ul>

        <nav>
            <div class="nav-wrapper">
                <a href="#" data-activates="slide-out" class="button-collapse"><i class="material-icons">menu</i></a>
                <a href="#!" class="brand-logo">Master My Course</a>
                <a href="#" data-activates="mobile-menu" class="right button-collapse" id="mobile-menu-button"><i class="material-icons">more_vert</i></a>
                <c:if test='${pageContext.request.servletPath eq "/WEB-INF/pages/course.jsp"}'>
                    <a href="#" data-activates="notesSideNav" class="right button-collapse-student-notes" id="notes-menu-button"><i class="material-icons"><i class="material-icons">event_note</i></i></a>
                </c:if>
                <ul class="right hide-on-med-and-down">
                    <c:if test="${studentCourse.isTA() == 1}">
                        <li><a class="dropdown-button" href="#!" data-activates="ta_courses" data-beloworigin="true">TA Courses<i class="material-icons right">arrow_drop_down</i></a></li>
                    </c:if>
                    <li><a class="dropdown-button" href="#!" data-activates="dropdown1" data-beloworigin="true">My Courses<i class="material-icons right">arrow_drop_down</i></a></li>
                    <li><a href="/Logout">Logout</a></li>
                    <c:set var="i" scope="page" value="${studentCourse.email}"/>
                    <li onclick="setStudent('${i}')"><a><i class="material-icons">view_module</i></a></li>
                    <li><a href="chat.htm" target="_blank"><i class="material-icons">sms</i></a></li>
                    <li><a href="messageBoard.htm" target="_blank"><i class="material-icons">sms</i></a></li>
                </ul>
                <ul class="side-nav z-depth-4" id="mobile-menu">
                    <c:if test="${studentCourse.isTA() == 1}">
                        <li><a class="dropdown-button" href="#!" data-activates="ta_courses_mobile" data-beloworigin="true">TA Courses<i class="material-icons right">arrow_drop_down</i></a></li>
                    </c:if>
                    <li><a class="dropdown-button" href="#!" data-activates="dropdown1_mobile" data-beloworigin="true">My Courses<i class="material-icons right">arrow_drop_down</i></a></li>
                    <li><a href="/Logout">Logout</a></li>
                    <c:set var="i" scope="page" value="${studentCourse.email}"/>
                    <li onclick="setStudent('${i}')"><a><i class="material-icons">view_module</i></a></li>
                    <li><a href="chat.htm" target="_blank"><i class="material-icons">sms</i></a></li>
                    <li><a href="messageBoard.htm" target="_blank"><i class="material-icons">sms</i></a></li>
                </ul>
            </div>
        </nav>
    </div>
</header>