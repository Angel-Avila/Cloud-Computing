var index=0;
var data=[];
$(document).ready(function() {
  $("#version").html("v0.14");

  $("#searchbutton").click( function (e) {
    displayModal();
  });

  $("#searchfield").keydown( function (e) {
    if(e.keyCode == 13) {
      displayModal();
    }
  });

  function displayModal() {
    $(  "#myModal").modal('show');

    $("#status").html("Searching...");
    $("#dialogtitle").html("Search for: "+$("#searchfield").val());
    $("#previous").hide();
    $("#next").hide();
    $.getJSON('/search/' + $("#searchfield").val() , function(data) {
      renderQueryResults(data);
    });
  }

  $("#next").click( function(e) {
    if(index+1 > (data.length/4)){
      console.log(index);
    }
    else{
      index++;
      $("#p0").attr('src', data.results[index*4])
      $("#p1").attr('src', data.results[1 + index*4])
      $("#p2").attr('src', data.results[2 + index*4])
      $("#p3").attr('src', data.results[3 + index*4])
    }
  });

  $("#previous").click( function(e) {
    if(index-1 < 0){
      console.log(index);
    }
    else{
      index--;
      $("#p0").attr('src', data.results[index*4])
      $("#p1").attr('src', data.results[1 + index*4])
      $("#p2").attr('src', data.results[2 + index*4])
      $("#p3").attr('src', data.results[3 + index*4])
    }
  });

  function renderQueryResults(data) {

    $("#p0").attr('src', '');
    $("#p1").attr('src', '');
    $("#p2").attr('src', '');
    $("#p3").attr('src', '');
    $("#p0").hide();
    $("#p1").hide();
    $("#p2").hide();
    $("#p3").hide();
    $("#previous").hide();
    $("#next").hide();

    if (data.error != undefined) {
      $("#status").html("Error: "+data.error);
      $("#p0").attr('src', '');
      $("#p1").attr('src', '');
      $("#p2").attr('src', '');
      $("#p3").attr('src', '');
    } else {
      this.data = data;
      $("#status").html(""+data.num_results+" result(s)");

      if(data.results.length > 4) {
        $("#next").show();
        $("#previous").show();
      }

      if(data.results.length  >= 1) {
        $("#p0").show();
        $("#p0").attr('src', data.results[0])
      }

      if(data.results.length  >= 2) {
        $("#p1").show();
        $("#p1").attr('src', data.results[1])
      }

      if(data.results.length  >= 3) {
        $("#p2").show();
        $("#p2").attr('src', data.results[2])
      }

      if(data.results.length  >= 4) {
        $("#p3").show();
        $("#p3").attr('src', data.results[3])
      }
     }
   }
});
