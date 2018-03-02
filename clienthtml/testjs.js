$(document).ready(function() {
    readyServicerdialog();
    $("#customerroom").detach();
    $("#customerbutton").button().click(function() {
        $("#customerbutton, #servicerbutton").css("display", "none");
        $("#main > *").remove();
        $("#main").css("background", "url()");
        createclearhome("c");
        createTextarea("c");
    });
});

function readyServicerdialog() {        //準備客服登入dialog
    $("#dialog").dialog({autoOpen: false});
    $("#servicerbutton").button().click(function() {
        openServicerdialog();
    });

}

function openServicerdialog() {         //開啟客服登入doalog
    errorDialog();
    $("#dialog").dialog({
        resizable: false,
        draggable: false,
        width: 250,
        modal: true,
        show: {
            effect: "blind",
            duration: 400
        },
        buttons: {
            "登入": function() {
                loginServicer();
            },
            "取消": function() {
                $("#dialog").dialog("close");
            }
        },
    });
    $("#dialog").dialog("open");
}

function errorDialog(){         //JQ UI dialog關閉icon的錯誤修正
    $("#ui-id-1").parent().children().remove();
    var errorcorrection = "<span id='ui-id-1' class='ui-dialog-title'>請登入客服帳號</span>\n\
                           <button type='button' class='ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only ui-dialog-titlebar-close' role='button' title='Close'>\n\
                           <span class='ui-button-icon-primary ui-icon ui-icon-closethick'></span><span class='ui-button-text'>Close</span></button>"

    $(errorcorrection).appendTo($(".ui-dialog-titlebar"));
    $(".ui-dialog-titlebar-close").click(function() {
        $("#dialog").dialog("close");
    });
}

function loginServicer() {      //判斷客服帳號密碼是否正確並登入
    if ($("#name").val() === "user" && $("#name").val() === "user") {
        $("#dialog").dialog("close");
        $("#servicerbutton, #customerbutton").css("display", "none");
        $("#main > *").remove();
        $("#main").css("background", "url()");
        $("<div class='col-md-1 column' id='customerroom'></div>").appendTo($("#main").parent()).insertBefore($("#main"));
        createclearhome("s");
        createTextarea("s");
    } else {
        var alert = "<p style='color:red;'>請輸入正確帳密</p>"
        $(alert).appendTo($("#dialog"));
    }
}


function createclearhome(a) {       //製造清除以及回大廳按鈕
    var customerbutton = "<button id='clear' type='button' class='btn btn-danger'>清除所有訊息</button>";

    var servicerbutton = "<button id='clear' type='button' class='btn btn-danger'>清除所有訊息</button>\n\
                          <button type='button' class='btn btn-info' id='serverhome' onclick='backServerhome()'>大廳</button>"
    if (a == "c") {
        $(customerbutton).appendTo($("#buttongroup"));

    } else {
        $(servicerbutton).appendTo($("#buttongroup"));
    }
    clearTextarea();

}

function clearTextarea() {          //清除textarea事件
    $("#clear").button().click(function() {
        $("#chatbox").text("");
    });
}

function createTextarea(a) {                //製造 清除button、大廳按鈕、customerroom的DIV、textarea和輸入框
    var customerdiv = "<textarea id='chatbox' readonly></textarea>\n\
                       <div class='col-md-11 col-sm-11 col-xs-11 column'><input type='text' id='message' class='form-control'></div>\n\
                       <div class='col-md-1 col-sm-1 col-xs-1 column'>\n\
                       <button id='enter' type='button' class='btn btn-default btn-sm'><img src='./img/enter5.png' alt=''/></button></div>";
    var servicerdiv = "<textarea id='chatbox' readonly></textarea>\n\
                       <div class='col-md-11 col-sm-11 col-xs-11 column'><input type='text' id='message' class='form-control'></div>\n\
                       <div class='col-md-1 col-sm-1 col-xs-1 column'>\n\
                       <button id='enter' type='button' class='btn btn-default btn-sm'><img src='./img/enter5.png' alt=''/></button></div>";

    if (a == "c") {
        $(customerdiv).appendTo($("#main"));
        $("#customerroom", "#menu").remove();
        $("#main").attr("class", "col-md-12 column");
        $("#buttongroup").parent().attr("class", "col-md-12 column");
        connectServer();
    } else {
        $(servicerdiv).appendTo($("#main"));
        $("<div class='col-md-3 column' id='menu'></div>").appendTo($("#main").parent());
        $("#main").attr("class", "col-md-8 column");
        createCan();
        connectServer2();
        createStatus();
    }
    enterEvent();

}

function enterEvent() {         //ENTER發送訊息事件

    $("#enter").button().click(function() {
        if ($.inArray("r", $("#message").val()) == 0 && $.inArray(":", $("#message").val()) == 1) {
            $("#message").val("");
        } else {
            sendMessage();
            $("#message").val("");
        }
    });
    $("#message").keypress(function(event) {
        if (event.keyCode == 13) {
            if ($.inArray("r", $("#message").val()) == 0 && $.inArray(":", $("#message").val()) == 1) {
                $("#message").val("");
            } else {
                sendMessage();
                $("#message").val("");
            }
        }
    });

}

function backServerhome() {         //回大廳
    $("#customerroom").children().removeAttr("disabled");
    var message = "-server,home";
    w.send(message);
    $("#" + user + num + "status").remove();
    setStatus(user, num);
}

function createCan() {          //製造 罐頭訊息的按鈕以及選單內容
    var can = "<button class='btn btn-primary btn-lg dropdown-toggle' type='button' data-toggle='dropdown'>\n\
               <span class='glyphicon glyphicon-plus'></span>罐頭訊息<span class='caret'></span></button>\n\
               <ul class='dropdown-menu' role='menu'>\n\
               <li id='a' onclick='sendCanmessage(a)'><a href='#'>感謝您對本公司的支持，我們已經積極在處理，謝謝</a></li>\n\
               <li id='b' onclick='sendCanmessage(b)'><a href='#'>已經收到您的需求，會在三至五個工作天內為您處理</a></li>\n\
               <li id='c' onclick='sendCanmessage(c)'><a href='#'>請提供您的商品型號，謝謝</a></li>\n\
               <li id='d' onclick='sendCanmessage(d)'><a href='#'>感謝您對本公司的支持，若有任何問題請來信Email:whobirdyou@mail.com</a></li>\n\
               <li id='e' onclick='sendCanmessage(e)'><a href='#'>請不要說不雅言語</a></li>\n\
               </ul>";

    $(can).appendTo($("#buttongroup"));

}

function sendCanmessage(id) {            //發送 罐頭訊息
    switch (id) {
        case a:
            var message = $("#a").text();
            w.send(message);
            break;
        case b:
            var message = $("#b").text();
            w.send(message);
            break;
        case c:
            var message = $("#c").text();
            w.send(message);
            break;
        case d:
            var message = $("#d").text();
            w.send(message);
            break;
        case e:
            var message = $("#e").text();
            w.send(message);
            break;

    }
    ;
}

function createStatus() {       //製造 客戶及客服狀態列表
    var panel = "<div class='panel panel-primary' id='customerstatus'>\n\
                 <div class='panel-heading'>\n\
                 <h3 class='panel-title'>客戶狀態</h3>\n\</div></div>\n\
                 <div class='panel panel-info' id='servicerstatus'>\n\
                 <div class='panel-heading'>\n\
                 <h3 class='panel-title'>客服狀態</h3>\n\</div></div>";
    $(panel).appendTo($("#menu"));

}

function setStatus(user, num, stat) {         //動態產生 客戶及客服狀態
    var id = user + num;
    var img = "";

    if (user == "customer") {
        if (stat == "0") {
            img = "./img/free.png";
            var list = "<div class='panel-body' id='" + id + "status'><img src ='" + img + "'> 客戶 " + num + " (缺乏客服人員)</div>";
        }
        else {
            img = "./img/busy.png";
            var list = "<div class='panel-body' id='" + id + "status'><img src ='" + img + "'> 客戶 " + num + " (已有客服人員)</div>";
        }
    } else {
        if (room == 999) {
            img = "./img/free.png";
            var list = "<div class='panel-body' id='" + id + "status'><img src ='" + img + "'> 客服 " + num + " (閒置中)</div>";
        }
        else {
            img = "./img/busy.png";
            var list = "<div class='panel-body' id='" + id + "status'><img src ='" + img + "'> 客服 " + num + "(與客戶 " + room + " 接洽中)</div>";
        }
    }



    if (user === "customer") {
        $(list).appendTo($("#customerstatus"));
    } else {
        $(list).appendTo($("#servicerstatus"));

    }

}

function connectServer() {
    url = "ws://140.113.73.212:8887/echo";
    w = new WebSocket(url);
    w.onopen = function() {
        w.send("//userenter");
    };
    w.onmessage = function(e) {
        var message = e.data;
        var tail = message.substring(message.indexOf(":") + 1);

        if (tail.indexOf("IMAGE:") == 0) {
            document.getElementById("imageBox").src = tail.substring(tail.indexOf("IMAGE:") + 6);
            return;
        }
        if (tail.indexOf("ALERT:") == 0) {
            alert(tail.substring(tail.indexOf("ALERT:") + 6));
            return;
        }

        if (message.match("-sever")) {  //get sever msg action  ex;-sever,add,customer,01
            return;
        } else {
            document.getElementById("chatbox").innerHTML += e.data + "\n";
            document.getElementById("chatbox").scrollTop = document.getElementById("chatbox").scrollHeight;
        }
    };
    w.onclose = function(e) {
        document.getElementById("chatbox").innerHTML += "伺服器尚未連接，請稍等伺服器重新開啟...";
    };
    w.onerror = function(e) {
        document.getElementById("chatbox").innerHTML += "錯誤：";
    };

}

function connectServer2() {
    url = "ws://localhost:8887/echo";
    w = new WebSocket(url);
    w.onopen = function() {
        w.send("//servicerenter");
    };
    w.onmessage = function(e) {
        var message = e.data;
        var tail = message.substring(message.indexOf(":") + 1);

        if (tail.indexOf("IMAGE:") == 0) {
            document.getElementById("imageBox").src = tail.substring(tail.indexOf("IMAGE:") + 6);
            return;
        }
        if (tail.indexOf("ALERT:") == 0) {
            alert(tail.substring(tail.indexOf("ALERT:") + 6));
            return;
        }

        if (message.match("-sever")) {  //get sever msg action  ex;-sever,add,customer,01

            getmsg(message);
        } else {
            document.getElementById("chatbox").innerHTML += e.data + "\n";
            document.getElementById("chatbox").scrollTop = document.getElementById("chatbox").scrollHeight;
        }

    };
    w.onclose = function(e) {
        document.getElementById("chatbox").innerHTML += "伺服器尚未連接，請稍等伺服器重新開啟...";
    };
    w.onerror = function(e) {
        document.getElementById("chatbox").innerHTML += "錯誤:";
    };
}

function sendMessage() {
    var message = $("#message").val();
    w.send(message);
}

function getmsg(msg) {
    var act = msg.split(",");
    action = act[1];
    user = act[2];
    room = act[3];
    num = act[4];
    stat = "";
    if (act[5] !== null) {
        stat = act[5];
    } else {
        stat = "";
    }

    if (action === "add") {
        if (user === "customer") {
            if ($("#" + user + num).val() === undefined)
            {
                setStatus(user, num);
                var button = "<button type='button' class='btn btn-success' id='" + user + num + "' onclick='clickCustomerroombutton(" + num + ")'>客戶" + num + "</button>";
                $(button).appendTo($("#customerroom"));
            }
        } else {
            if ($("#" + user + num + "status").val() === undefined)
            {
                setStatus(user, num);
            }
        }
    } else if (action === "del") {
        if (user === "customer") {
            $("#" + user + num).remove();
            $("#" + user + num + "status").remove();
        } else {
            $("#" + user + num + "status").remove();
        }
    } else if (action === "edi") {
        if (user === "customer") {
            $("#" + user + num + "status").remove();
            setStatus(user, num, stat);
        } else {
            $("#" + user + num + "status").remove();
            setStatus(user, num, stat);
        }
    }
}

function clickCustomerroombutton(num) {     //按客戶房間按鈕觸發事件
    var message = "-server,home";
    w.send(message);
    $("#customerroom").children().removeAttr("disabled");
    $("#customer" + num).attr("disabled", "disabled");
    message = "r:" + num;
    w.send(message);
}