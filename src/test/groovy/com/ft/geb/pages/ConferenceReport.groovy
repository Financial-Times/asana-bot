package com.ft.geb.pages

import geb.Page

class ConferenceReport extends Page{

    static url = "http://localhost:8080/"
    static at = { title == "Request for Permission" }
    static content = {
        acceptButton { $('#submit_approve_access') }
        acceptButtonDisabled { $('#submit_approve_access').getAttribute('disabled') }

        topFrame { $('body h1') }
        selectText { $('body h3') }
        reportType { $("#reportTypeSelect") }
        deskType { $("#teamSelect") }
        submitButton { $("#submitButton") }

        standardText { $("body div header") }
        displayedDeskText { $("body header span:nth-child(2)") }

        fullReportInformation { $('body div[class="panel-body printable"] div') }
        }
}
