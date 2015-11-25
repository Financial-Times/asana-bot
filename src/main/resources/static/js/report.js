var selectedTeam = $("#teamSelect").val();

populateSelect = function (el, items) {
    el.options.length = 0;

    $.each(items, function () {
        el.options[el.options.length] = new Option(this.name, this.id);
    });
}
showDeskProjects = function (showProjects) {
    if(!showProjects){
        $('#projectSelect').attr("multiple","multiple");
        $('#projectSelect option').attr('selected', 'selected');
        $('#projects-holder').hide();
        $("#reportCriteriaForm").append('<input type="hidden" name="multiproject" id="multiproject"/>');

    }
}
toggleReportType = function (selectedTeam){
    if(selectedTeam == "Weekend") {
        $("#reportTypeSelectWeekend").show();
        $("#reportTypeSelectWeekend").attr('name', 'reportType');
        $("#reportTypeSelect").hide();
        $("#reportTypeSelect").removeAttr('name');
    } else {
        $("#reportTypeSelectWeekend").hide();
        $("#reportTypeSelectWeekend").removeAttr('name');
        $("#reportTypeSelect").show();
        $("#reportTypeSelect").attr('name', 'reportType');
    }
}
$(document).ready(function () {
    $('#email-sent').fadeIn(400).delay(3000).fadeOut(400);
    var showProjects = $.inArray($('#teamSelect').val(), showProjectsTeams) > -1
    showDeskProjects(showProjects);
    toggleReportType(selectedTeam);

    $('#email-report').bind('click', function(event){
        event.preventDefault();
        $("#sendEmail").val("true");
        $("#reportCriteriaForm").submit();

    });
    console.log("report types "+ JSON.stringify(reportTypes));
    $('#teamSelect').bind('change', function() {
        var teamName = this.value;
        var showProjects = $.inArray(teamName, showProjectsTeams) > -1;
        toggleReportType(teamName);

        var projects = desks[teamName];
        console.log(" projects "+ JSON.stringify(projects))
        //Descending order
        projects.sort(function(first, second){
            return first.primary ? -1 : (second.primary ? 1 : 0)
        });

        populateSelect($('#projectSelect').get(0), projects);
        var container = $('#projects-holder');
        if (projects.length > 1 && showProjects) {
            container.removeClass('hidden');
            container.css("visibility", "visible");
            $('#projectSelect').removeAttr("multiple");
            $("#reportCriteriaForm").remove("#multiproject");
        }  else {
            $('#projectSelect').attr("multiple","multiple");
            $('#projectSelect option').attr('selected', 'selected');
            container.css("visibility", "hidden");
            $("#reportCriteriaForm").append('<input type="hidden" name="multiproject" id="multiproject"/>');
        }
    });
});

