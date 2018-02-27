
<%--
  Author: Jose Rodriguez

--%>
<script>
    $(document).ready(function() {
        $(".modal").modal();
    });
</script>


<div id="slide-out" class="side-nav fixed" style="top: 0px;">
    <%--<ul id="slide-out side-chat" class="side-nav fixed z-depth-4" style="top: 0px;">--%>


        <div class="container-chat clearfix z-depth-5">
            <div class="people-list" id="people-list">
                <div class="search">
                    <input type="text" placeholder="search" />
                    <i class="fa fa-search"></i>
                </div>
                <ul class="list" id="chatUsers">
                    <%-- filled in by chatroom.js --%>
                </ul>
            </div>
        </div> <!-- end container -->

    <%--</ul>--%>

</div>