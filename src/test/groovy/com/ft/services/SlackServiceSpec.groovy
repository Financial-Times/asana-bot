import com.ft.asanaapi.model.ProjectInfo
import com.ft.asanaapi.model.Team
import com.ft.monitoring.ProjectChange
import com.ft.services.SlackService
import com.google.api.client.util.Lists
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class SlackServiceSpec extends Specification {

    private SlackService slackService
    private RestTemplate mockRestTemplate

    private Team worldTeam;
    private Team marketsTeam;
    private ProjectInfo worldTopicsInfo;

    private ProjectChange worldTopics;

    private String slackUrl = "http://dummy.slack.co.uk";


    void setup() {
        mockRestTemplate = Mock(RestTemplate)
        slackService = new SlackService(mockRestTemplate, slackUrl)

        worldTeam = new Team()
        worldTeam.id = "1"
        worldTeam.name = "World"

        worldTopicsInfo = new ProjectInfo()
        worldTopicsInfo.id = "12345"
        worldTopicsInfo.name = "World Topics"
        worldTopicsInfo.team = worldTeam
        worldTopicsInfo.archived = false
        worldTopics = new ProjectChange(worldTopicsInfo)

        marketsTeam = new Team()
        marketsTeam.id = "2"
        marketsTeam.name = "Markets"

    }

    void "null changes to notify"(){
        when:
            slackService.notifyProjectChange(null)
        then:
            0 * mockRestTemplate.postForLocation(_)
            thrown RuntimeException;
    }

    void "no changes to notify"(){
        when:
            slackService.notifyProjectChange([worldTopics])
        then:
            0 * mockRestTemplate.postForLocation(_)
            thrown RuntimeException;
    }

    void "notify when project name changes"(){
        String postedUrl = null
        HashMap payload = null
        given:
            ProjectInfo changedWorldTopicsInfo = new ProjectInfo()
            changedWorldTopicsInfo.id = "12345"
            changedWorldTopicsInfo.name = "World Topicsssss changed"
            changedWorldTopicsInfo.team = worldTeam
            changedWorldTopicsInfo.archived = false
            worldTopics.build(changedWorldTopicsInfo)

            List<ProjectChange> projectChanges = []
            projectChanges.add(worldTopics)

        when:

            slackService.notifyProjectChange(projectChanges)

        then:
            1 * mockRestTemplate.postForLocation(*_)  >> {arguments ->
                    postedUrl = arguments[0]
                    payload = arguments[1]
                    return null
                }

            postedUrl.toString().equals(slackUrl)
            payload.get("text").contains("Project Changed Alert <!channel>")
            payload.get("attachments").each{ attachment -> assertNameChanges(attachment)}

    }

    void "notify when project team changes"(){
        String postedUrl = null
        HashMap payload = null
        given:
            ProjectInfo changedWorldTopicsInfo = new ProjectInfo()
            changedWorldTopicsInfo.id = "12345"
            changedWorldTopicsInfo.name = "World Topics"
            changedWorldTopicsInfo.team = marketsTeam
            changedWorldTopicsInfo.archived = false
            worldTopics.build(changedWorldTopicsInfo)

            List<ProjectChange> projectChanges = []
            projectChanges.add(worldTopics)

        when:

            slackService.notifyProjectChange(projectChanges)

        then:
            1 * mockRestTemplate.postForLocation(*_)  >> {arguments ->
                postedUrl = arguments[0]
                payload = arguments[1]
                return null
            }

            postedUrl.toString().equals(slackUrl)
            payload.get("text").contains("Project Changed Alert <!channel>")
            payload.get("attachments").each{ attachment -> assertTeamChanges(attachment);}
    }

    void "notify when project is archived"(){
        String postedUrl = null
        HashMap payload = null
        given:

            ProjectInfo changedWorldTopicsInfo = new ProjectInfo()
            changedWorldTopicsInfo.id = "12345"
            changedWorldTopicsInfo.name = "World Topics"
            changedWorldTopicsInfo.team = worldTeam
            changedWorldTopicsInfo.archived = true
            worldTopics.build(changedWorldTopicsInfo)

            List<ProjectChange> projectChanges = []
            projectChanges.add(worldTopics)

        when:

            slackService.notifyProjectChange(projectChanges)

        then:
            1 * mockRestTemplate.postForLocation(*_)  >> {arguments ->
                postedUrl = arguments[0]
                payload = arguments[1]
                return null
            }

            postedUrl.toString().equals(slackUrl)
            payload.get("text").contains("Project Changed Alert <!channel>")
            payload.get("attachments").each{ attachment -> assertArchiveChanges(attachment)}
    }

    void "notify when project has multiple changes"(){
        String postedUrl = null
        HashMap payload = null
        given:

            ProjectInfo changedWorldTopicsInfo = new ProjectInfo()
            changedWorldTopicsInfo.id = "12345"
            changedWorldTopicsInfo.name = "World Topics"
            changedWorldTopicsInfo.team = marketsTeam
            changedWorldTopicsInfo.archived = true
            worldTopics.build(changedWorldTopicsInfo)

            List<ProjectChange> projectChanges = []
            projectChanges.add(worldTopics)

        when:

            slackService.notifyProjectChange(projectChanges)

        then:
            1 * mockRestTemplate.postForLocation(*_)  >> {arguments ->
                postedUrl = arguments[0]
                payload = arguments[1]
                return null
            }

            postedUrl.toString().equals(slackUrl)
            payload.get("text").contains("Project Changed Alert <!channel>")
            payload.get("attachments").each{ attachment -> assignToChangeAssert(attachment)}
    }


    private void assertNameChanges(HashMap<String, Object> attachment){
        String title = attachment["text"]
        assert title.contains(worldTopicsInfo.getId())
        List<Map> fields = attachment.get("fields")
        assert fields[0]['value'] == 'World Topicsssss changed'
        assert fields[0]['title'] == 'new name'
        assert fields[1]['value'] == 'World Topics'
        assert fields[1]['title'] == 'old name'
    }

    private void assertTeamChanges(HashMap<String, Object> attachment){
        String title = attachment["text"]
        assert title.contains(worldTopicsInfo.getId())
        List<Map> fields = attachment["fields"]
        assert fields[0]['value'] == 'Markets'
        assert fields[0]['title'] == 'new team'
        assert fields[1]['value'] == 'World'
        assert fields[1]['title'] == 'old team'
    }

    private void assertArchiveChanges(HashMap<String, Object> attachment){
        String title = attachment["text"]
        assert title.contains(worldTopicsInfo.getId())
        assert title.contains("archived")
        assert attachment["fields"].size() == 0
    }

    private void assignToChangeAssert(HashMap<String, Object> attachment){
        String title = attachment["text"]
        if (title.contains("team")){
            assertTeamChanges(attachment)
        }
        else{
            assertArchiveChanges(attachment)
        }
    }


}
