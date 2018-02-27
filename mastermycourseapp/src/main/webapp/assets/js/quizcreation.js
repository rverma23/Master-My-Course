/**
 * Created by joserodriguez on 5/14/17.
 */
$(document).ready(function(){
    $('.button-collapse-quiz').sideNav({
            menuWidth: 300, // Default is 300
            edge: 'right', // Choose the horizontal origin
            closeOnClick: true, // Closes side-nav on <a> clicks, useful for Angular/Meteor
            draggable: true // Choose whether you can drag to open on touch screens
        }
    );

    var displayButtonQuiz = function(){
        var width= $(window).width();
        if(width>992){
            $(".button-collapse-quiz").hide();
        }else{
            $(".button-collapse-quiz").show();
        };
    }

    //Set initial state
    displayButtonQuiz();
    //subscribe to resize event
    $(window).on('resize',function(evt){
        //set navigation state on every resize new
        displayButtonQuiz();
    });

});

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

var courseOutline;
var editor; // for creating questions
var editor2; // for updating code questions

$(document).ready(function () {
    // Initialize collapse button and other materialize controls
    $(".button-collapse").sideNav();
    $('select').material_select();
    $("#programming_languages_template").material_select();
    $('.datepicker').pickadate({
        selectMonths: true,
        selectYears: 10
    });

    $(".transferTest").on("click", function() {
        transferQuizQuestion(this);
    });

    // initialize code editor


    // display controls for that question type
    $("#question_select").change(function () {
        $(this).find("option:selected").each(function () {
            var optVal = $(this).attr("value");
            if (optVal) {
                $(".question_div").not("." + optVal).hide();
                $("." + optVal).show();
            } else {
                $(".question_div").hide();
            }

            $("#hidden_question_type").val(optVal);
        });
    }).change(); // call it now to initialize it.

    $('#programming_languages').on('change', function (e) {
        var value = this.value;

        $("#language").val(value);
        if (value == 3) {
            editor.getSession().setMode("ace/mode/java")
        } else {
            // python
            editor.getSession().setMode("ace/mode/python")
        }
    });

    $("#programming_languages_template").on('change', function (e) {
        var value = this.value;

        $("#language_template").val(value);
        if (value == 3) {
            editor2.getSession().setMode("ace/mode/java")
        } else {
            // python
            editor2.getSession().setMode("ace/mode/python")
        }
    });

    // filter user questions
    $("#filter_questions").change(function () {
        var type = $(this).val();
        $("#question_list li").each(function (index) {
            if ($(this).attr("data-qt") == type || type == "all") {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
    });

    // quiz and question button handlers
    $("#add_question_button").click(function () {
        $("#answer_div").toggle("fast");
    });

    $("#cancel_question_edit").click(function () {
        $("#question_edit").toggle("fast");
    });

    $("#cancel_quiz_edit").click(function () {
        $.redirect("/CancelCreateQuiz");
    });

    $("#cancel_answer_div").click(function () {
        $("#answer_div").toggle("fast");
    });

    $("#question_list li").click(function () {
        questionClick($(this));
    });

    $("#delete_button").click(function () {
        $("#edit_question_form").attr('action', '/DeleteQuestion');
        $("#edit_question_form").submit();
    });

    $("#delete_quiz_button").click(function () {
        $("#edit_quiz").attr('action', '/DeleteQuiz');
        $("#edit_quiz").submit();
    });

    $("body").on('click', '.delete_test_case', function() {
        $(this).closest('tr').remove();
    });

    $("body").on('click', '.delete_test_case_template', function() {
        $(this).closest('tr').remove();
    });


    // Serializes the questions so that they are available to the UpdateQuiz servlet
    $("#update_quiz_button").click(function () {
        var data = [];

        // make sure they have at least one question
        if ($("#edit_quiz_ul li").length == 0) {
            Materialize.toast("Please add at least one question to the quiz.", 2000, "red");
            return;
        }

        if ($("#edit_quiz_name").val() == "") {
            Materialize.toast("Please enter a name for the quiz.", 2000, "red");
            return;
        }

        $("#edit_quiz_ul li").each(function () {
            var id1 = "#points" + $(this).attr("data-questionId");
            var id2 = "#attempts" + $(this).attr("data-questionId");
            var question = {
                "quizId": $("#edit_quiz_ul").attr("data-quizId"),
                "moduleId": $(this).attr("data-moduleId"),
                "points": $(id1).val(),
                "attempts": $(id2).val(),
                "question": $(this).attr("data-question")
            };
            data.push(question);
        });

        $("#edit_quiz_questions_hidden").val(JSON.stringify(data));

        // submit the form
        $("#edit_quiz").attr('action', '/UpdateQuiz');
        $("#edit_quiz").submit();
    });

    $("#submit_quiz_button").click(function () {

        // make sure they have at least one question
        if ($("#quiz_questions li").length == 0) {
            Materialize.toast("Please add at least one question to the quiz.", 2000, "red");
            return;
        }

        if ($("#quiz_name").val() == "") {
            Materialize.toast("Please enter a name for the quiz.", 2000, "red");
            return;
        }

        var data = [];
        $("#quiz_questions li").each(function () {
            var id1 = "#points" + $(this).attr("data-questionId");
            var id2 = "#attempts" + $(this).attr("data-questionId");
            var question = {
                "question_type": $(this).attr("data-qt"),
                "question_id": $(this).attr("data-questionId"),
                "points": $(id1).val(),
                "attempts": $(id2).val()
            };
            data.push(question)

        });
        $("#quiz_questions_hidden").val(JSON.stringify(data));

        $("#quiz_form").submit();
    });

    // Create Quiz/Edit quiz
    $("#quiz_button").click(function () {
        initEditQuiz();
        // show the div
        $("#quiz_div").toggle("fast");
    });

    if (editQuiz == true)
    {
        initEditQuiz();
    }
});

function initEditQuiz() {
    // disable handlers for now, user must finish editing quiz
    $("#quiz_button").off("click");
    $("#courseOutline").off("click");
    $("#courseOutline li div p").off("click");
    $("#question_list li").off("click");
    $("#quiz_list li").off("click");

    // show the transfer buttons
    $(".transfer_button").css("display", "inline-block");

    // update handler to now move questions to the quiz list
    $("#question_list li").click(function () {
        transferQuestion(this);
    });
}

// transfer from question list to quiz list
function transferQuestion(elem) {
    var element = $(elem).clone();
    $(elem).remove();

    // get the data
    var question = element.attr("data-question");
    var id = element.attr("data-questionId");
    var moduleId = element.attr("data-moduleId");
    var type = element.attr("data-qt");

    // make the element and append it
    var html_string = '<li data-questionId=' + id + ' data-qt=' + type + ' data-question="' + question + '" data-moduleId=' + moduleId +'><div class="row">';
    html_string += "<div class='col s3'>" + question + "</div>";
    html_string += '<div class="col s3">Points: <input style="display:inline-block; width: 20%;" type="number" id="points' + id + '"value="10"></div>';
    html_string += '<div class="col s3">Allowed Number of Attempts <input style="display:inline-block; width: 20%;" type="number" value="1" id="attempts' + id + '"></div>';
    html_string += '<div style="cursor:pointer" class="col s3">';
    html_string += '<div id=remove' + id + '> Remove from Quiz <i class="material-icons tiny">trending_flat</i></div>';
    html_string += '</div>';
    html_string += '</div></li>';
    $(".quiz_questions").append(html_string);

    // update the handler
    $("#remove" + id).click(function () {
        transferQuizQuestion(this);
    });
}

// transfer from quiz list to question list
function transferQuizQuestion(elem) {
    // get the whole list element
    var element = $(elem).parent().parent().parent();

    // get the data
    var question = element.attr("data-question");
    var id = element.attr("data-questionId");
    var moduleId = element.attr("data-moduleId");
    var type = element.attr("data-qt");

    // create the element and append it
    var list_elem = '<li id="transferQuestion' + id + '" data-questionId=' + id + ' data-qt=' + type + ' data-question="' + question + '" data-moduleId=' + moduleId +'>';
    list_elem += '<div><i class="material-icons tiny flip">trending_flat</i> Add to Quiz:   ' + question + '</div></li>';
    $("#question_list").append(list_elem);
    $(element).remove();

    // update the handler
    $("#transferQuestion" + id).click(function() {
        transferQuestion($(this));
    });
}

// click to display/edit a question
function questionClick(elem) {
    $("#template_div").append($("#edit_question_template").children()); // return the template back
    $("#edit_question_template").empty(); // remove the old template
    var question_type = elem.attr("data-qt");
    var correct_answer = elem.attr("data-answer");

    // set some fields.
    $("#edit_question_hidden_id").val(elem.attr("data-questionId"));
    $("#edit_question_hidden_type").val(question_type);
    $("#edit_question_prompt").val(elem.attr("data-question"));

    var html_str = "";

    if (question_type == "multipleChoice") {
        $("#correct_answer_template").val(correct_answer);
        $("#wrong_answer1_template").val(elem.attr("data-wrong1"));
        $("#wrong_answer2_template").val(elem.attr("data-wrong2"));
        $("#wrong_answer3_template").val(elem.attr("data-wrong3"));
        $("#edit_question_template").append($(".multipleChoiceTemplate"));
    } else if (question_type == "trueFalse") {
        $("#edit_question_template").append($(".trueFalseTemplate"));
        if (correct_answer == "1") {
            $("#true_radio").prop("checked", true);
        } else {
            $("#false_radio").prop("checked", true);
        }
    } else if (question_type == "exactAnswer") {
        $(".exactAnswerTemplate input").val(correct_answer);
        $("#edit_question_template").append($(".exactAnswerTemplate"));
    } else if (question_type == "code") {
        // code question
        $("#test_case_template").find("tr:gt(0)").remove(); // remove old test cases from the template
        $("#edit_code_div").empty();
        var expectedOutput = $(elem).attr("data-answer").split(",");
        var testCases = $(elem).attr("data-testCases").split(",");

        var language = $(elem).attr("data-language");
        $("#language_template").val(language);  // set the hidden field
        $("#programming_languages_template").val(language);  // set the select box
        $("#programming_languages_template").material_select();

        // add the ace editor
        var content = '<div id="editor_template" class="editor">';
        content += $(elem).attr("data-templateCode");
        content += "</div>";
        $("#edit_code_div").append(content);

        try {
            editor2 = ace.edit("editor_template");
            editor2.setTheme("ace/theme/sqlserver");
            editor2.getSession().setMode("ace/mode/java");
        } catch(e){}

        if (language == 3) {
            editor2.getSession().setMode("ace/mode/java")
        } else {
            editor2.getSession().setMode("ace/mode/python")
        }

        for (var indx = 0; indx < testCases.length; indx++) {
            var table_row = "<tr>";
            table_row += '<td><label><input id="input-stdin" class="required input-attempts" type="text" name="stdin" placeholder="Enter input to stdin" value="' + testCases[indx] + '"></label></td>';
            table_row += '<td><label><input id="input-stdout" class="required input-attempts" type="text" name="stdout" placeholder="Enter Expected Answer to stdout" value="' + expectedOutput[indx] + '"></label></td>';
            table_row += '<td><button class="delete_test_case_template btn red" type="button" style="top: -10px;position: relative;">Delete Test Case</button></td>';
            table_row += "</tr>";
            $("#test_case_template").append(table_row);
        }

        $("#edit_question_template").append($("#code_template"));
    }

    $("#question_edit").toggle("fast");
}

function editQuizData(quizId, title, teacherNotes, date) {
    var data = {
        quizId: quizId,
        title: title,
        teacherNotes: teacherNotes,
        date: date
    };

    $.redirect("/EditQuiz", data);
}

function editTestCase() {
    $("#test_case_template tbody").append($("#test_case_row").clone());
}

function addTestCase() {
    var row = $("#test_case_row").clone();
    $(".code table tbody").append(row);
}

/**
 * Make sure all fields are filled in
 *
 */
function checkFields(questionType) {
    var empty_fields = 0;

    $("." + questionType + " .required").each(function() {
        if(!$.trim($(this).val())) {
            empty_fields++;
        }
    });

    if (questionType == "multipleChoice" || questionType == "trueFalse") {
        if ($("." + questionType + " .required_radio:checked").length == 0) {
            empty_fields++;
        }
    }

    return empty_fields;
}

/**
 *  Submit a new question
 */
function submitQuestion() {
    var type = $("#question_select").val();
    if (checkFields(type) > 0 || $("#questionPrompt").val() == "" || $("#question_select").val() == "" || $("#question_select").val() == null ) {
        Materialize.toast("Please fill in all fields", 2000);
        return; // exit, don't submit the incomplete form.
    }

    if (type == "code") {
        var template_code = editor.getValue();
        var question = $("#questionPrompt").val();
        var language = $("#programming_languages").val();
        var stdin = $("input[name='stdin']").map(function(){return $(this).val();}).get().join();
        var stdout = $("input[name='stdout']").map(function(){return $(this).val();}).get().join();

        var data = {
            template_code: template_code,
            question: question,
            language: language,
            stdin: stdin,
            stdout: stdout
        };

        $.redirect("/CreateCodeQuestion", data);
    } else {
        // use normal /SubmitQuestion servlet
        $("#question_form").submit();
    }
}

/**
 * Save changes and submit the updated question
 */
function updateQuestion() {
    $("#edit_question_form").attr('action', '/UpdateQuestion');
    var questionType = $("#edit_question_hidden_type").val();

    if (checkFields(questionType + "Template") > 0 || $("#edit_question_prompt").val() == "") {
        Materialize.toast("Please fill in all fields", 2000);
        return; // exit, don't submit the incomplete form.
    }

    if (questionType == "code") {
        $("#language_template").val($("#programming_languages_template").val());

        var stdin = "";
        var stdout = "";

        var rows = $('tr', $("#test_case_template"));
        for (var i = 1; i < $("#test_case_template tr").length; i++) {
            var inputs = rows.eq(i).find("input");
            stdin += $(inputs).eq(0).val();
            stdout += $(inputs).eq(1).val();

            if (i != $("#test_case_template tr").length - 1) {
                stdin += ",";
                stdout += ",";
            }
        }

        $("#stdin_template").val(stdin);
        $("#stdout_template").val(stdout);

        var editor2 = ace.edit("editor_template");
        var template_code = editor2.getValue();
        $("#hidden_code").val(template_code);
    }

    $("#edit_question_form").submit();
}
