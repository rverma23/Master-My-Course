/**
 * Created by joserodriguez on 4/21/17.
 */
$(document).ready(function () {
    // Initialize collapse button
    $(".button-collapse").sideNav();
    $('select').material_select();

    $('textarea').val('');
    $('textarea').trigger('autoresize');
});

function changeCourse(id) {
    var data = {
        courseId: id
    };

    $.redirect("/StudentChangeCourse", data);
}

function changePage(contentModuleId) {
    var data = {contentModuleId: contentModuleId};
    $.redirect("/ChangePage", data);
}


// The function actually applying the offset
function offsetAnchor() {
    if (location.hash.length !== 0) {
        window.scrollTo(window.scrollX, window.scrollY - 100);
    }
}

// Captures click events of all a elements with href starting with #
$(document).on('click', 'a[href^="#"]', function(event) {
    // Click events are captured before hashchanges. Timeout
    // causes offsetAnchor to be called after the page jump.
    window.setTimeout(function() {
        offsetAnchor();
    }, 0);
});

// Set the offset when entering page with hash present in the url
window.setTimeout(offsetAnchor, 0);
