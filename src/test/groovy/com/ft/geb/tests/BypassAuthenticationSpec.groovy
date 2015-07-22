package com.ft.geb.tests

import com.ft.AsanaBot
import com.ft.geb.pages.ConferenceReport
import com.ft.geb.pages.GoogleSignin
import geb.spock.GebReportingSpec
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Stepwise

@IntegrationTest
@ContextConfiguration(classes = AsanaBot.class, loader = SpringApplicationContextLoader.class)
@ActiveProfiles(["staging","web"])
@WebAppConfiguration
@Stepwise
class BypassAuthenticationSpec extends GebReportingSpec {

    def env = System.getenv()
    String user = env['FUNCTIONAL_ASANA_LOGIN']
    String pass = env['FUNCTIONAL_ASANA_PASSWORD']
    def page

    void "Google Login"() {
        when: "At google signin"
        page = to GoogleSignin

        then:
        assert waitFor { page.emailForm.isDisplayed() }

        when:
        page.emailForm = user
        page.nextButton.click()

        then:
        assert waitFor { page.passwordForm.isDisplayed() }

        when:
        page.passwordForm = pass
        page.signInButton.click()

        then:
        assert waitFor { page.welcomeText.isDisplayed() }
        page.welcomeText.text() == "Control, protect and secure your account, all in one place"
    }

    void "Asana Checks"() {
        when: "At asana reports page"
        page = to ConferenceReport

        then:
        waitFor { !page.acceptButtonDisabled }

        when:
        page.acceptButton.click()

        then:
        assert waitFor { page.getTitle() == 'Conference report'}
        page.topFrame.text() == 'Asana Reports'

        when:
        page.reportType.value('TODAY')
        page.deskType.value('UK')
        page.submitButton.click()

        then:
        assert waitFor { page.standardText.isDisplayed() }
    }

}