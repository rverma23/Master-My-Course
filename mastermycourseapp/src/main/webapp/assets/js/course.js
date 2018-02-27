/**
 * Created by joserodriguez on 4/21/17.
 */
$(document).ready(function () {
    // Initialize collapse button
    $(".button-collapse").sideNav();
    $('.button-collapse-student-notes').sideNav({
            menuWidth: 300, // Default is 300
            edge: 'right', // Choose the horizontal origin
            closeOnClick: true, // Closes side-nav on <a> clicks, useful for Angular/Meteor
            draggable: true // Choose whether you can drag to open on touch screens
        }
    );
    $('select').material_select();

    $("#add_question_button").click(function() {
        $("#answers_div").toggle("fast");
    });
    //For notes
    $('.modal').modal();


    $('input#titleNote, textarea#textarea-note').characterCounter();

    var response;
    $.validator.addMethod(
        "uniqueTitle",
        function(value, element) {
            var title = $("#title-note").prop("value");
            var userId = $("#note-userId-submit").prop("value");
            var courseId = $("#note-courseId-submit").prop("value");
            $.ajax({
                type: 'POST',
                url: '/ValidateNoteTitle',
                data:{"title":title,"courseId": courseId, "userId":userId},
                success: function (data){
                    response = ( data == 'False' ) ? false : true;
                }
            });
            return response;
        },
        "title is Already Taken"
    );

    //note form validation
    $("#form-add-note").validate({
        rules: {
            titleNote:{
                required: true,
                minlength: 3,
                uniqueTitle: true
            },
            noteText:{
                required: true,
                minlength: 5
            }
        },
        //For custom messages
        messages: {
            noteText:{
                required: "Enter some notes!",
                minlength: "Enter at least 5 characters"
            }
        },
        errorElement : 'div',
        errorPlacement: function(error, element) {
            var placement = $(element).data('error');
            if (placement) {
                $(placement).append(error)
            } else {
                error.insertAfter(element);
            }
        }
    });

    $('#imageDiv').click(function () {
        var imageDiv = document.getElementById('imageDiv');
        call = imageDiv.requestFullScreen || imageDiv.webkitRequestFullScreen || imageDiv.mozRequestFullScreen;
        call.call(imageDiv);

    });

});

function deleteNote(title) {
    var data = {
        title: title
    };

    $.redirect("/DeleteNote", data);
}

function changeCourse(id) {
    var data = {
        courseId: id
    };

    $.redirect("/StudentChangeCourse", data);
}

function changePage(contentModuleId) {
    var data = {contentModuleId: contentModuleId};
    $.redirect("/StudentChangePage", data);
}

function nextPage() {
    $.redirect("/StudentNextPage");
}

function previousPage() {
    $.redirect("/StudentPreviousPage");
}

var started = false;
var paused = true;
function endedPlayingCallback(){
    $("#playPauseButton").html('<i class="material-icons">play_arrow</i>');
    started = false;
    paused = true;
}

function startedPlayingCallback(){
    console.log("started playing");
    started = true;
    paused = false;
    $("#playPauseButton").html('<i class="material-icons">pause</i>');
}

function pause(){
    responsiveVoice.pause();
    paused = true;
    console.log("Paused");
    $("#playPauseButton").html('<i class="material-icons">play_arrow</i>');
}

function resumed(){
    responsiveVoice.resume();
    paused = false;
    console.log("Resumed");
    $("#playPauseButton").html('<i class="material-icons">pause</i>');
}

function errorCallback(data) {
    console.log("Error Playing: " + data);
}

function playAudio(){
    if (paused == false) {
        pause();
    } else {

        if(started == true){
            resumed();
        } else {
            var paramaters = {
                onstart: startedPlayingCallback,
                onend: endedPlayingCallback,
                onerror: errorCallback
            };
            responsiveVoice.speak(rawText, $('#voiceselection').val(), paramaters);
        }
    }
}

function restartAudio() {
    started = false;
    paused = true;
    playAudio();
}

var isImage = true;

function toggleImageText() {
    var button = $("#toggleImageTextButton");
    var image = $("#imageDiv");
    var text = $("#textDiv");
    if(!isImage){
        button.html('<i class="material-icons">text_fields</i>');
        text.hide()
        image.show();
    } else {
        button.html('<i class="material-icons">photo</i>');
        image.hide();
        text.show();
    }
    isImage = !isImage;
}

var startTime = new Date().getTime()/1000;

function updateStudentTimeOnPage(){
    var now = new Date().getTime()/1000;
    var seconds = Math.round(now - startTime);
    $.post("/UpdateStudentTimeOnPage", {seconds: seconds}, function (response){
        if(response.status == "success"){
            console.log("Student Time On Page Updated");
            startTime = new Date().getTime()/1000;
        } else {
            console.log("Student Time On Page Failed to Update " + response.status);
        }
    }, 'json');
}
//Validates the title so it doesnt repeat
function validateNoteTitle(userId, courseId){
    var title = $("#title-note").prop("value");
    $.ajax({
        type: 'POST',
        url: '/ValidateNoteTitle',
        data:{"title":title,"courseId": courseId, "userId":userId},
        success: function (data){
            if(data=='False'){
                // alert("pick another username");
                $('.errorTxt4').text("Pick a different title.");
                $('.errorTxt4').css("color","red");
            }else{
                $('.errorTxt4').text("");
            }
        }
    });
}

function editNoteView(userId, courseId, title, text) {
    $('#note-title').html(title);
    $('#note-text').text(text);
    $('#textarea-note-label').addClass('active');
    $('#note-text').trigger('autoresize');
    $('#note-courseId').val(courseId);
    $('#note-userId').val(userId);
    $('#note-title-param').val(title);
    $('#modal-notes-edit').modal('open');
}

$("document").ready(function () {
    //Populate voice selection dropdown
    var voicelist = responsiveVoice.getVoices();
    var vselect = $("#voiceselection");
    $.each(voicelist, function() {
        vselect.append($("<option />").val(this.name).text(this.name));
    });

    updateStudentTimeOnPage();
    setInterval(updateStudentTimeOnPage, 20000);

    $('select').material_select();

});