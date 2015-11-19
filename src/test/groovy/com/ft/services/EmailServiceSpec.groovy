import com.ft.config.Config
import com.ft.report.model.Report
import com.ft.services.EmailService
import org.thymeleaf.TemplateEngine
import org.thymeleaf.resourceresolver.ClassLoaderResourceResolver
import org.thymeleaf.templateresolver.TemplateResolver
import spock.lang.Specification

class EmailServiceSpec extends Specification {

    private EmailService emailService
    private TemplateEngine templateEngine
    private Config mockConfig
    def emailTeams

    void setup() {
        emailService = new EmailService();
        mockConfig = Mock(Config)
        templateEngine = Spy(TemplateEngine)
        emailTeams = []

        def team1 = [name: "one", email: "two"]
        def team2 = [name: "three", email: "four"]

        emailTeams.add(team1)
        emailTeams.add(team2)

        mockConfig.getEmailTeams() >> emailTeams;
        emailService.config = mockConfig
        emailService.templateEngine = templateEngine


    }

    void "should return team's email address"() {

        expect:
            emailService.getEmailAddress(team) == emailAddress

        where:
            scenerio                | team    | emailAddress
            'when team name is one' | 'one'   | "two"
            'when team name is two' | 'three' | "four"
    }

    void "should return team's email status"() {

        expect:
            emailService.isEmailTeam(team) == status

        where:
            scenerio                         | team     | status
            'when team is in email team'     | 'one'    | true
            'when team is not in email team' | 'noteam' | false
    }

    void "should send email"() {

        given:
            String email = "one@ft.com"
            Report report = new Report();
            TemplateResolver templateResolver = Spy(TemplateResolver)
            templateResolver.setResourceResolver(GroovyMock(ClassLoaderResourceResolver))
            templateEngine.addTemplateResolver(templateResolver)

        when:
            def response = emailService.sendEmail(email, report, "one")

        then:
            response != null
            1 * templateResolver.getName()


    }

}
