var chatMessage = document.getElementById('chatMessage');
var chatWindow = document.getElementById('chatWindow');
var chatUsers = document.getElementById('chatUsers');
var chatWindowDiv = document.getElementById('chatWindowDiv');
var launchApplication = {};

var pubnub = PUBNUB.init({
    publish_key: 'pub-c-54fce0a8-9aae-441b-97cf-0b65c51d612b',
    subscribe_key: 'sub-c-e6635db6-08ac-11e6-a9bb-02ee2ddab7fe',
    ssl: true,
    uuid: myUuid
});


//Subscribe to the demo_tutorial channel with presence and state
pubnub.subscribe({
    channel: channel + ',' + privateChannel,
    state: {
        number: myNumber,
        name: myName,
        timestamp: new Date()
    },
    message: function (m, envelope, channel) {
        receiveMessage(m, channel);
    },
    presence: function (m) {
        prescence(m);
    },
    connect: function (m) {
        connect(m);
    },
    reconnect: function (m) {
        console.log('Reconnect');
        console.log(m);
    }
});

pubnub.state({
    channel: channel,
    uuid: myUuid,
    state: {
        number: myNumber,
        name: myName,
        timestamp: new Date(),
        image: imageUrl
    }
});

window.addEventListener("beforeunload", function (e) {
    pubnub.unsubscribe({
        channel: channel
    });
});

function prescence(m) {

    if (m.action === "state-change") {
        console.log('state-change event: ');
        handleStateChange(m.action, m.uuid, m.data);
    }
    else if (m.action === "join") {
        console.log('join event');
        //get the state of the user
        pubnub.state({
            channel: channel,
            uuid: m.uuid,
            callback: function (s) {
                handleStateChange('state-change', m.uuid, s);
            }
        });
        //UI.updateRoomCount(m.action, m.occupancy);
    }
    else if ((m.action === "timeout") || (m.action === "leave")) {
        console.log('timeout or leave event, remove the user element');
        handleLeaveEvent(m.action, m.uuid); //Remove the user from the list of participants
        //UI.updateRoomCount(m.action, m.occupancy); //Update the number of users present
    }
}

var historyPrinted = false;

function connect(m) {
    pubnub.history({
        channel: channel,
        count: 50,
        callback: function (m) {
            if(!historyPrinted){
                console.log('got history:');
                historyPrinted = !historyPrinted;
                m[0].forEach(function (e) { //First element is the history, last 2 are timestamps
                    receiveMessage(e, channel);
                });
            }
        }
    });
}

function handleLeaveEvent(paction, uuid) {
    var userlist = chatUsers;
    console.log('received a leave or timeout event');
    if (userlist.children) {
        for (var x = 0; x < userlist.children.length; ++x) {
            var e = userlist.children[x];
            if (e.getAttribute('data-uuid') === uuid) {
                //remove the user that is no longer online
                userlist.removeChild(e);
            }
        }
    }
}

function sendPrivateChatRequest(uuid) {
    if (uuid !== null && uuid !== "") {
        var msg = {receiver: uuid, sender: myNumber};
        console.log("Sending Private Chat: " + msg);
        pubnub.publish({
            channel: privateChannel,
            message: msg
        });
        
        pubnub.state({
            channel: channel,
            uuid: uuid,
            callback: function (s) {
                if(s.number !== 'undefined'){
                    openPrivateChat(s.number, false);
                }
            }
        });
    }
}
function handleStateChange(paction, uuid, userState) {
    var userlist = chatUsers;
    var userElement = null;
    if (uuid === myUuid) {
        return;
    }
    if (userlist.children !== 'undefined') {
        for (var x = 0; x < userlist.children.length; ++x) {
            var e = userlist.children[x];
            if (e.getAttribute("data-uuid") === uuid) {
                userElement = e;
                break;
            }
        }
    }

    var userDiv = null;
    var li = null;
    if (userElement !== null) {
        console.log('found existing element');
        userDiv = userElement.firstChild;
        li = userElement;
    } else {
    }
    var userLi = '<li class="clearfix online" data-uuid="'+uuid+'" id="'+uuid+'">' +
                    '<hr class="teal-text">' +
                    '<img onclick="sendPrivateChatRequest(\'' + uuid + '\')" src="' + userState.image +'" alt="avatar" />'+
                    '<div class="about">' +
                        '<div class="name">' + userState.name + '</div>' +
                        '<div class="status">' +
                            '<i class="fa fa-circle online"></i> online' +
                        '</div>' +
                    '</div>' +
                '</li>';
    if (userElement === null) {
        userlist.insertAdjacentHTML('beforeend', userLi);
    }
}



function receiveMessage(m, chan) {
    if (chan === channel) {
        var message = "";
        var messageEscaped = m.message.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
        if(m.uuid != myUuid){
            message = '<li class="clearfix">' +
                '<div class="message-data align-right">' +
                '<span class="message-data-time" >' + m.dateTime +'</span> &nbsp; &nbsp;' +
                '<span class="message-data-name" >' + m.name + '</span> <i class="fa fa-circle me"></i>' +
                '</div>' +
                '<div class="message other-message float-right">' +
                messageEscaped +
                '</div>' +
                '</li>';
        } else {
            message = '<li class="clearfix">' +
                '<div class="message-data">' +
                '<span class="message-data-name" ><i class="fa fa-circle online"></i>'+ m.name+ '</span>' +
                '<span class="message-data-time" >' + m.dateTime +'</span> &nbsp; &nbsp;' +
                '</div>' +
                '<div class="message my-message">' +
                    messageEscaped +
                '</div>' +
                '</li>';
        }

        chatWindow.innerHTML += message;
        chatWindowDiv.scrollTop = chatWindowDiv.scrollHeight;
    } else if (chan === privateChannel) {
        if (m.receiver === myUuid && $('#privateForm' + m.sender).length == 0) {
            $("#" + m.sender).append("<div id='privateForm" + m.sender + "'>" +
                "<h5>Private Message</h5>" +
                "<button class='btn red' onclick='declinePrivateChat(\""+m.sender+"\")'>Decline</button>" +
                "<button class='btn green' onclick='openPrivateChat(\""+m.sender+"\", true)'>Accept</button>" +
                "</div>");

        }

    }
}

function declinePrivateChat(uuid) {
    $("#privateForm" + uuid).remove();
}

function openPrivateChat(m, receive) {
    var l_url = "privateChat.htm?call=" + m;
    window.open(l_url, "_blank");
}



function getDayOfWeek(index){
    if(index == 0){
        return "Saturday";
    }
    if(index == 1){
        return "Monday";
    }
    if(index == 2){
        return "Tuesday";
    }
    if(index == 3){
        return "Wednesday";
    }
    if(index == 4){
        return "Thursday";
    }
    if(index == 5){
        return "Friday";
    }
    return "Saturday";
}


function sendMessage() {    
    if (chatMessage.value !== null && chatMessage.value !== "") {
        var date = new Date();
        var dateMessage = "" + date.getHours() + ":" + date.getMinutes() + ", " + getDayOfWeek(date.getDay()) + " "
            + (date.getMonth() + 1) + "/" + date.getDate() + "/" + date.getFullYear();


        chatMessage.value = removeProfanity(chatMessage.value);
        var data = {uuid: myUuid, message: chatMessage.value, dateTime: dateMessage, name: myName};
        pubnub.publish({
            channel: channel,
            message: data
        });

        chatMessage.value = null;
        chatMessage.focus();
    }
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

function fillChars(chr,cnt) {
    var s = '';
    for (var i=0; i<cnt; i++) { s += chr; }
    return s;
}

var BadWords = ['fuck','shit','ass', 'asshole'];

function removeProfanity(message) {
    message = message.replace(/\r?\n/g,' <br>');
    var rg;
    for (var i=0; i<BadWords.length; i++) {
        console.log(i);
        rg = new RegExp(BadWords[i],"ig")
        message = message.replace(rg,fillChars('*',BadWords[i].length));
    }
    return message;
}