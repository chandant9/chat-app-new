var currentConversation = null;
var recipient;
var tabIndex;

$(document).ready(function () {
  $("#messages-list").append("<li>ChatApp Messages ...</li>");
  $("#messages-list").addClass("welcome");

  getOnlineUsers();

  getOfflineUsers();

  $("#send").click(function (event) {
    event.preventDefault();
    var text = $("#sentmessage").val();
    if (text !== "" && recipient != null) {
      sendMessage(text, recipient);
    }
  });

  $("#logout-button").click(function () {
    logout();
  });
});

function getOnlineUsers() {
  tabIndex = 1;
  $.ajax({
    type: "GET",
    url: "users/online",
    success: function (onlineUsers) {
      $("#online-users").empty();
      $.each(onlineUsers, function (index, onlineUser) {
        listUser(onlineUser);
      });
    },
    error: function (e) {
      $("#online-users").append("<div>?</div>");
    },
  });
}

function getOfflineUsers() {
  $.ajax({
    type: "GET",
    url: "users/offline",
    success: function (offlineUsers) {
      $("#offline-users").empty();
      $.each(offlineUsers, function (index, offlineUser) {
        listUser(offlineUser);
      });
    },
    error: function (e) {
      $("#offline-users").append("<div>?</div>");
    },
  });
}

function loadConversation(username) {
  $("#sentmessage").val("");
  $("#sentmessage").data("emojioneArea").setText("");

  $.ajax({
    type: "GET",
    url: "conversations/" + username,
    success: function (conversation) {
      $("#messages-list").empty();

      $.each(conversation, function (index, message) {
        var messageStyle = "recipientMessage";
        if (message.senderRecipient.recipient === username) {
          messageStyle = "senderMessage";
        }
        $("#messages-list").append(
          '<li class="' + messageStyle + '">' + message.text + "</li>"
        );
      });

      $("#messages").scrollTop($("#messages")[0].scrollHeight);
    },
    error: function (e) {
      console.log("error");
      $("#messages-list").append(e);
    },
  });
}

function sendMessage(text, recipient) {
  $.ajax({
    type: "POST",
    url: "messages/sendMessage",
    data: JSON.stringify({
      recipient: recipient,
      text: text,
    }),
    contentType: "application/json; charset=utf-8",
    dataType: "json",
    success: function (messageSent) {
      $("#messages-list").append(
        '<li class="senderMessage">' + messageSent.text + "</li>"
      );
      $("#sentmessage").val("");
      $("#sentmessage").data("emojioneArea").setText("");
      $("#messages").scrollTop($("#messages")[0].scrollHeight);
    },
    error: function (e) {
      $("#messages-list").append(
        '<li class="send-error">Message could not be sent</li>'
      );
    },
  });
}

function logout() {
  $.ajax({
    type: "GET",
    url: "/logout",
    success: function (response) {
      getLoginPage();
    },
    error: function (e) {
      console.log("Error loging out");
    },
  });
}

function getLoginPage() {
  location.href = "http://localhost:9001/login";
}

function listUser(user) {
  var status = user.online ? "online" : "offline";
  var availability = user.availability;
  var icon;

  $("#" + status + "-users").append('<div id="' + user.username + '"></div>');
  $("#" + user.username).addClass("user");

  if (user.online) {
    icon =
      availability === "available" || availability === "busy"
        ? '<i class="fa fa-circle fa-xs"></i>'
        : '<i class="fa fa-circle-o fa-xs"></i>';

    $("#" + user.username).addClass(availability);
  } else {
    $("#" + user.username).addClass(status);
    icon = '<i class="fa fa-circle-o-notch fa-xs"></i>';
  }

  $("#" + user.username).append(icon);
  $("#" + user.username).append(user.username);
  $("#" + user.username).attr("tabIndex", tabIndex++);

  $("#" + user.username).click(function () {
    if (
      currentConversation !== null &&
      currentConversation.attr("id") !== user.username
    ) {
      $(currentConversation).attr("data-clicked", false);
      $(currentConversation).removeClass("focus");
    }

    $("#" + user.username).attr("data-clicked", "true");
    $("#" + user.username).removeClass("new-message");
    currentConversation = $("#" + user.username);
    $(currentConversation).addClass("focus");
    recipient = user.username;
    localStorage.setItem("recipient", recipient);
    loadConversation(user.username);
  });
}

function getUserAvailability(username) {
  $.ajax({
    type: "GET",
    url: "users/" + username + "/availability",
    success: function (userAvailability) {
      var username = userAvailability.username;
      var availability = userAvailability.availability;

      setAvailabilityClass(username, availability);
    },
    error: function () {
      console.log("Error changing status");
    },
  });
}

function setAvailabilityClass(username, availability) {
  $("#" + username).attr("class", "user " + availability);
}
