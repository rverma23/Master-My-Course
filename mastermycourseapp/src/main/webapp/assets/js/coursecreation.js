/**
 * Created by joserodriguez on 5/13/17.
 */

var prevList;
var newList;
var courseOutline;
var elem;

$(document).ready(function () {
    // Initialize collapse button and other materialize controls
    $(".button-collapse").sideNav();
    $('select').material_select();
    $('.datepicker').pickadate({
        selectMonths: true,
        selectYears: 10
    });

    // quiz and question button handlers
    $("#add_question_button").click(function() {
        $("#answer_div").toggle("fast");
    });

    $("#cancel_question_edit").click(function() {
        $("#question_edit").toggle("fast");
    });

    $("#cancel_quiz_edit").click(function() {
        $("#quiz_edit").toggle("fast");
    });

    $("#cancel_answer_div").click(function() {
        $("#answer_div").toggle("fast");
    });

    $("#question_list li").click(function() {
        questionClick($(this));
    });

    $("#quiz_list li").click(function() {
        quizClick($(this));
    });

    $("#delete_button").click(function() {
        $("#edit_delete_form").attr('action', '/DeleteQuestion');
        $("#edit_delete_form").submit();
    });

    $("#update_button").click(function() {
        $("#edit_delete_form").attr('action', '/UpdateQuestion');
        $("#edit_delete_form").submit();
    });

    $("#delete_quiz_button").click(function() {
        $("#edit_quiz_form").attr('action', '/DeleteQuiz');
        $("#edit_quiz_form").submit();
    });

    $("#cancel_quiz_button").click(function() {
        location.reload();
    });

    $("#update_quiz_button").click(function() {
        var data = "";
        $("#edit_quiz_ul li").each(function(){
            var id1 = "#points" + $(this).attr("data-id2");
            var id2 = "#attempts" + $(this).attr("data-id2");
            data += $(this).attr("data-id1") + ":" + $(this).attr("data-id2") + ":";
            data += $(id1).val() + ":" + $(id2).val() + ",";
        });

        $("#edit_quiz_questions_hidden").val(data);
        // submit the form
        $("#edit_quiz_form").attr('action', '/UpdateQuiz');
        $("#edit_quiz_form").submit();
    });

});

function quizCreation() {
    location.href = "/quizCreation.htm";
}

function questionClick(elem) {
    $("#question_edit_template").empty();
    var question_type = elem.attr("data-qt");
    var correct_answer = elem.attr("data-answer");

    $("#edit_delete_hidden_id").val(elem.attr("data-id"));
    $("#edit_delete_hidden_type").val(question_type);
    $("#edit_delete_question_prompt").val(elem.attr("data-question"));
    var html_str = "";
    if (question_type == "multipleChoice") {
        var w1 = elem.attr("data-wrong1");
        var w2 = elem.attr("data-wrong2");
        var w3 = elem.attr("data-wrong3");

        html_str += '<ul class="answers">'
            + '<input checked type="radio" name="mult_radio" class="answer_radio" value="a" id="correct_answer"><label for="correct_answer"><input value="' + correct_answer + '"type="text" name="q1_prompt"}></label><br/>'
            + '<input type="radio" name="mult_radio" class="answer_radio" value="b" id="wrong_answer1"><label for="wrong_answer1"><input value="' + w1 + '" type="text" name="q2_prompt"></label></label><br/>'
            + '<input type="radio" name="mult_radio" class="answer_radio" value="c" id="wrong_answer2"><label for="wrong_answer2"><input value="' + w2 + '" type="text" name="q3_prompt"></label></label><br/>'
            + '<input type="radio" name="mult_radio" class="answer_radio" value="d" id="wrong_answer3"><label for="wrong_answer3"><input value="' + w3 + '" type="text" name="q4_prompt"></label></label><br/>'
            + "</ul>";
        $("#question_edit_template").append(html_str);
    } else if (question_type == "trueFalse") {

        html_str += '<ul class="answers">'
            + '<input id="true_radio" type="radio" name="tf_radio" class="answer_radio" value="true"><label for="true_radio"></label>True<br/>'
            + '<input id="false_radio" type="radio" name="tf_radio" class="answer_radio" value="false"><label for="false_radio"></label>False<br/>'
            + '</ul>';

        $("#question_edit_template").append(html_str);
        if (correct_answer == "1") {
            $("#true_radio").prop("checked", true);
        } else {
            $("#false_radio").prop("checked", true);
        }
    } else if (question_type == "exactAnswer") {
        html_str += '<label><input type="text" name="a_exact" placeholder="Enter Answer." value="' + correct_answer + '"></label>';
        $("#question_edit_template").append(html_str);
    } else {
        // code question to be completed later
    }

    $("#question_edit").toggle("fast");
}

var timer;

function changeCourse(id) {
    var data = {
        courseId: id
    };
    $.redirect("/ChangeCourse", data);
}

function changePage(contentModuleId) {
    var data = {contentModuleId: contentModuleId};
    $.redirect("/ChangePage", data);
}

function getProgress(){
    $.getJSON("/Progress", function(data){
        $("#progressMessage").html(data.message);
        $("#progressBar").css("width", data.percent + "%");
        if(data.message == "Completed" || data.percent == 100){
            window.clearInterval(timer);
        }
    });
}

function runProgress(){
    timer = window.setInterval(getProgress, 2000);
}

function parsePdfOutline(){
    $.getJSON("/ParsePDFOutline", function (data) {
        if(data.status == "started"){
            location.reload(true);
        } else {
            console.log("Error calling parse pdf with outline");
        }
    });
}


$('document').ready(function () {
    $("#startPage").on('input', function () {
        console.log("fired");
        $('#endPage').attr("min", $(this).val());
    });
});


//The dropify function to take care of the input
$( document ).ready(function() {

    $('.dropify').dropify({
        tpl: {
            wrap:            '<div class="dropify-wrapper"></div>',
            loader:          '<div class="dropify-loader"></div>',
            message:         '<div class="dropify-message"><span class="file-icon" /> <p>{{ default }}</p></div>',
            preview:         '<div class="dropify-preview"><span class="dropify-render"></span><div class="dropify-infos"><div class="dropify-infos-inner"><p class="dropify-infos-message">{{ replace }}</p></div></div></div>',
            filename:        '<p class="dropify-filename"><span class="file-icon"></span> <span class="dropify-filename-inner"></span></p>',
            clearButton:     '<button type="button" class="dropify-clear">{{ remove }}</button>',
            upload:          '<button type="submit" class="dropify-clear" style="top:50px;">Upload</button>',
            errorLine:       '<p class="dropify-error">{{ error }}</p>',
            errorsContainer: '<div class="dropify-errors-container"><ul></ul></div>'
        }
    });

    drEvent.on('dropify.afterClear', function(event, element){
        alert('File deleted');
    });
});
var drEvent = $('.dropify').dropify();

