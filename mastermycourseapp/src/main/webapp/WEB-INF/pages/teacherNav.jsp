
<%--
  Authors: Zach Lerman, James DeCarlo, and Jose Rodriguez

--%>
<jsp:useBean id="messageBean" class="com.mastermycourse.beans.MessageBean" scope="session"/>
<script>
    $(document).ready(function() {
        $(".modal").modal();

        if (${!messageBean.message.equals("")}) {
            Materialize.toast('${messageBean.message}', 2500, '${messageBean.color}');
            <c:set target="${messageBean}" property="message" value="" /> // reset message
        }
    });
</script>


<header id="header" class="page-topbar">

    <div class="navbar-fixed">
        <ul id="dropdown1" class="dropdown-content">
            <c:forEach items="${course.courses}" var="c">
                <li onclick="changeCourse(${c.value})"><a>${c.key}</a></li>
            </c:forEach>
            <li class="divider"></li>
            <li><a href="newCourse.htm">Add New Course</a></li>
        </ul>
        <ul id="dropdown2" class="dropdown-content">
            <li class="language"><a onclick="$('#addModal').modal('open');" href="#addModal">Add TA</a></li>
            <li class="language"><a onclick="$('#deleteModal').modal('open');" href="#deleteModal">Delete TA</a></li>
        </ul>
        <ul id="dropdown1_mobile" class="dropdown-content">
            <c:forEach items="${course.courses}" var="c">
                <li onclick="changeCourse(${c.value})"><a>${c.key}</a></li>
            </c:forEach>
            <li class="divider"></li>
            <li><a href="newCourse.htm">Add New Course</a></li>
        </ul>
        <ul id="dropdown2_mobile" class="dropdown-content">
            <li class="language"><a onclick="$('#addModal').modal('open');" href="#addModal">Add TA</a></li>
            <li class="language"><a onclick="$('#deleteModal').modal('open');" href="#deleteModal">Delete TA</a></li>
        </ul>
        <nav>
            <div class="nav-wrapper">
                <a href="#" data-activates="slide-out" class="button-collapse"><i class="material-icons">menu</i></a>
                <a href="#!" class="brand-logo">Master My Course</a>
                <ul class="right hide-on-med-and-down">
                    <li><a class="dropdown-button" href="#!" data-activates="dropdown1" data-beloworigin="true">My Courses<i class="material-icons right">arrow_drop_down</i></a></li>
                    <li><a href="/Logout">Logout</a></li>
                    <li><a href="/teachertool.htm"><i class="material-icons">view_module</i></a></li>
                    <li><a class="dropdown-button" href="#!" data-activates="dropdown2" data-beloworigin="true">TA Options<i class="material-icons right">arrow_drop_down</i></a></li>
                    <li><a href="chat.htm" target="_blank"><i class="material-icons">sms</i></a></li>
                    <li><a href="messageBoard.htm" target="_blank"><i class="material-icons">sms</i></a></li>
                </ul>
                <a href="#" data-activates="mobile-menu" class="right button-collapse" id="mobile-menu-button"><i class="material-icons">more_vert</i></a>
                <ul class="side-nav z-depth-4" id="mobile-menu">
                    <li><a class="dropdown-button" href="#!" data-activates="dropdown1_mobile" data-beloworigin="true">My Courses<i class="material-icons right">arrow_drop_down</i></a></li>
                    <li><a href="/Logout">Logout</a></li>
                    <li><a href="/teachertool.htm"><i class="material-icons">view_module</i></a></li>
                    <li><a class="dropdown-button" href="#!" data-activates="dropdown2_mobile" data-beloworigin="true">TA Options<i class="material-icons right">arrow_drop_down</i></a></li>
                    <li><a href="chat.htm" target="_blank"><i class="material-icons">sms</i></a></li>
                    <li><a href="messageBoard.htm" target="_blank"><i class="material-icons">sms</i></a></li>
                </ul>
            </div>
        </nav>
    </div>

    <script>
        $(".button-collapse").sideNav();
    </script>

    <div id="addModal" class="modal">
        <form action="/AddTA" method="post">
            <input type="hidden" name="from" value="${pageContext.request.requestURI}" />
            <div class="modal-content">
                <h4>Add a Teaching Assistant</h4>
                <div class="row">
                    <div class="input-field col s6">
                        <input required id="ta_first_name" name="first_name" type="text" class="validate">
                        <label for="ta_first_name">First Name</label>
                    </div>
                    <div class="input-field col s6">
                        <input required id="ta_last_name" name="last_name" type="text" class="validate">
                        <label for="ta_last_name">Last Name</label>
                    </div>
                </div>
                <div class="row">
                    <div class="input-field col s12">
                        <input required id="ta_email" type="email" name="email" class="validate">
                        <label for="ta_email">Email</label>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="submit" class="modal-action waves-effect waves-green btn blue">Add TA</button>
                <button type="button" class="modal-action modal-close waves-effect waves-green btn grey">Cancel</button>
            </div>
        </form>
    </div>

    <div id="deleteModal" class="modal">
        <form action="/RemoveTAs" method="post">
            <input type="hidden" name="from" value="${pageContext.request.requestURI}" />
            <div class="modal-content">
                <h4>Delete a Teaching Assistant</h4>
                <c:forEach items="${course.TAs}" var="TA">
                    <div class="collection-item">
                        <div class="row">
                            <div class="col s12">
                                    <input type="checkbox" name="taEmails" id="${TA.email}" value="${TA.email}">
                                    <label for="${TA.email}">${TA.name}</label>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <div class="modal-footer">
                <input type="submit" name="action" value="Remove" class="btn red" style="width:100%">
                <button type="button" class="modal-action modal-close waves-effect waves-green btn grey">Cancel</button>
            </div>
        </form>
    </div>
</header>
