// settings.html
function loadContent(url) {
  $.ajax({
    type: "GET",
    url: url,
    success: function(data) {
      $("#content-container").html(data);
    }
  });
}

$(document).ready(function() {
  // Load initial content
 

  // Event delegation for button clicks
  $("#content-container").on("click", "button", function() {
	   loadContent("userlist");
    // Get the button's ID or class to determine what action to take
   /* var buttonId = $(this).attr("id");
    var buttonClass = $(this).attr("class");

    // Handle button click events
    switch (buttonId) {
      case "btn-save":
        // Handle save button click
        alert("Save button clicked!");
        break;
      case "btn-edit":
        // Handle edit button click
        alert("Edit button clicked!");
        break;
      default:
        // Handle other button clicks
        alert("Unknown button clicked!");
    }*/
  });
});