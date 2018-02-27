/**
 * Created by JamesDeCarlo on 5/16/17.
 */


$(document).ready(function () {
    // Initialize collapse button
    $(".button-collapse").sideNav();

});

function allCourses() {
    $.redirect("/ListAllCourses");
}

function teacherCourses(id) {
    var data = {teacherId: id};
    $.redirect("/ListTeacherCourses", data);
}

function disableCourse(courseId, courseName) {
    var data = {courseId: courseId, courseName: courseName};
    $.redirect("/DisableTeacherCourse", data);
}

function enableCourse(courseId, courseName) {
    var data = {courseId: courseId, courseName: courseName};
    $.redirect("/EnableTeacherCourse", data);
}

function deleteCourse(courseId, courseName) {
    var data = {courseId: courseId, courseName: courseName};
    $.redirect("/DeleteTeacherCourse", data);
}
