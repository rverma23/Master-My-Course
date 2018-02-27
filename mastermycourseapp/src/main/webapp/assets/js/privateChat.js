/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 
 * Author James DeCarlo
 */

var chatMessage = document.getElementById('chatMessage');
var chatWindow = document.getElementById('chatWindow');
var curSession;

var phone = window.phone = PHONE({
    number: myNumber,
    publish_key: 'pub-c-54fce0a8-9aae-441b-97cf-0b65c51d612b',
    subscribe_key: 'sub-c-e6635db6-08ac-11e6-a9bb-02ee2ddab7fe',
    ssl: true
});

phone.ready(function () {
    console.log("Ready for connections");
    var number = getParameterByName("call");
    $('#my-video').append(phone.video);
    makeCall(number);


});

phone.receive(function (session) {
    session.connected(function (session) {
        console.log("Received Call from " + session);
        $('#their-video').html(session.video);
        curSession = session;
    });
    session.ended(function (session) {
        $('#their-video').html('');
        session.hangup();
    });
    session.message(function (session, message) {
        console.log("Message received");
        receiveMessage(message);
    });
});

var videoBg = document.getElementById("video-fullscreen");
$('#their-video').click(function () {
    call = videoBg.requestFullScreen || videoBg.webkitRequestFullScreen || videoBg.mozRequestFullScreen;
    call.call(videoBg);

});


window.addEventListener("beforeunload", function (e) {
    if (curSession !== null) {
        var message = '<p><mark style="color:red;">' + myName + ">> </mark>Disconnected<br></p>";
        phone.send(message);
        curSession.hangup();
    }
    phone.hangup();
});

function makeCall(number) {
    if (!window.phone)
        alert("Login First!");
    else if (number === null) {
    }
    else {
        try {
            phone.dial('' + number);
        } catch (e) {
            console.log(e);
        }
    }
}


function sendMessage() {
    if (chatMessage.value !== null && chatMessage.value !== "") {
        var message = '<p><mark style="color:red;">' + myName + ">> </mark>" + chatMessage.value + "<br></p>";

        chatMessage.value = null;
        chatMessage.focus();
        if (!window.phone)
            return;
        if (!curSession)
            return;
        phone.send(message);
        chatWindow.innerHTML += message;
        chatWindow.scrollTop = chatWindow.scrollHeight;
        console.log("message sent");
    }
}


function receiveMessage(m) {
    chatWindow.innerHTML += m;
    chatWindow.scrollTop = chatWindow.scrollHeight;
}

function getParameterByName(name, url) {
    if (!url)
        url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
            results = regex.exec(url);
    if (!results)
        return null;
    if (!results[2])
        return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}



$('#chatMessage').keyup(function (event) {
    if (event.keyCode === 13) {
        var content = this.value;
        var caret = getCaret(this);
        if (event.shiftKey) {
            this.value = content.substring(0, caret - 1) + "\n" + content.substring(caret, content.length);
            event.stopPropagation();
        } else {
            this.value = content.substring(0, caret - 1) + content.substring(caret, content.length);
            sendMessage();
        }
    }
});

function getCaret(el) {
    if (el.selectionStart) {
        return el.selectionStart;
    } else if (document.selection) {
        el.focus();
        var r = document.selection.createRange();
        if (r === null) {
            return 0;
        }
        var re = el.createTextRange(), rc = re.duplicate();
        re.moveToBookmark(r.getBookmark());
        rc.setEndPoint('EndToStart', re);
        return rc.text.length;
    }
    return 0;
}
