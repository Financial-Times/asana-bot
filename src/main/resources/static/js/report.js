populateSelect = function (el, items) {
    el.options.length = 0;

    $.each(items, function () {
        el.options[el.options.length] = new Option(this.name, this.id, this.primary, this.primary);
    });
};

hideDeskProjects = function() {
    var projectSelect = $('#projects');
    projectSelect.attr("multiple","multiple");
    projectSelect.children('option').attr('selected', 'selected');
    $('#projects-holder').hide();

};

var onTeamChange = function(teamName) {
    var showProjects = userDesks[teamName].showProjects;

    var projects = userDesks[teamName]['projects'];

    var container = $('#projects-holder');
    if (showProjects) {
        container.removeClass('hidden');
        container.show();
        $('#projects').removeAttr("multiple");
    }  else {
        container.hide();
        $('#projects').attr("multiple", "multiple");
    }
    populateSelect($('#projects').get(0), projects);
    var reportTypeOptions = reportTypes[userDesks[teamName].reportCategory.$name];
    reportTypes["WEEKDAY"].forEach(function(elem){
        elem.primary = (elem.id === criteria.reportType.$name);
    });
    populateSelect($('#reportTypeSelect').get(0),reportTypeOptions);
};

$(document).ready(function () {
    $('#email-sent').fadeIn(400).delay(3000).fadeOut(400);
    var teamSelect = $('#teamSelect');
    var showProjects = userDesks[teamSelect.val()]['showProjects'];
    if (!showProjects) {
        hideDeskProjects();
    }

    $('#email-report').bind('click', function(event){
        event.preventDefault();
        $("#sendEmail").val("true");
        $("#reportCriteriaForm").submit();

    });

    $('#submitButton').on("click", function () {
        $(this).addClass('disabled');
        $(this).val('Loading...');
    })

    teamSelect.bind('change', function() {
        var teamName = this.value;
        onTeamChange(teamName);
    });
});

