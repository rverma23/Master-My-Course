//Contains Javascript from gradequiz

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

    $.redirect("/ChangeCourse", data);
}