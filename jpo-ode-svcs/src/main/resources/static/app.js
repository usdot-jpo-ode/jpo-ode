var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/ode-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/subscribers', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
        stompClient.subscribe('/topic/messages', function (greeting) {
            showMessage(JSON.parse(greeting.body).content);
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/connect", {}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function showMessage(message) {
    $("#messages").append("<tr><td>" + message + "</td></tr>");
}

function upload() {
    var formData = new FormData();
    formData.append('file', $('#file').get(0).files[0]);
    console.log("Ajax call submitted");
    $.ajax({
        url: '/',
        type: 'POST',
        data: formData,
        cache: false,
        contentType: false,
        processData: false
    }).done(function(response) {
        console.log("File upload response received");
        $( "#uploadResponse" ).append("File uploaded succesfully.");
    });
}
function sendSnmp() {
    var ip1 = $("#snmp-ip").val();
    var oid1 = $("#snmp-oid").val();
    console.log("[INFO] SNMP request received IP:[" + ip1 + "] OID:[" + oid1 + "]");
    $.ajax({
        url: "/rsuHeartbeat"
        , type: "get",
        dataType: "text",
        data: {
            ip: ip1
            , oid: oid1
        }
        , success: function (response) {
            console.log("[SUCCESS] Response: " + response);
            $("#snmp-response").append("<p>" + response + "</p>");
        }
        , error: function (error) {
            console.log("[ERROR] " + error.responseText);
        }
    });
}

$(function () {

    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
    $( "#upload" ).click( function() { upload() } );
    $( "#snmp-submit").click( function() { sendSnmp() })
});
