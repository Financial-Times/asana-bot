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
        worldTeam.setId("1")
        worldTeam.setName("World")
        worldTopicsInfo = new ProjectInfo();
        worldTopicsInfo.setId("12345");
        worldTopicsInfo.setName("World Topics")
        worldTopicsInfo.setTeam(worldTeam)
        worldTopicsInfo.setArchived(false)
        worldTopics = new ProjectChange(worldTopicsInfo)

        marketsTeam = new Team()
        marketsTeam.setId("2")
        marketsTeam.setName("Markets")

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
            slackService.notifyProjectChange(worldTopics)
        then:
            0 * mockRestTemplate.postForLocation(_)
            thrown RuntimeException;
    }

    void "notify when project name changes"(){
        String postedUrl = null
        HashMap payload = null
        given:
            ProjectInfo changedWorldTopicsInfo = new ProjectInfo();
            changedWorldTopicsInfo.setId("12345");
            changedWorldTopicsInfo.setName("World Topicsssss changed")
            changedWorldTopicsInfo.setTeam(worldTeam)
            changedWorldTopicsInfo.setArchived(false)
            worldTopics.build(changedWorldTopicsInfo);

            List<ProjectChange> projectChanges = Lists.newArrayList();
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
            payload.get("attachments").each{ attachment -> assertNameChanges(attachment);}

    }

    void "notify when project team changes"(){
        String postedUrl = null
        HashMap payload = null
        given:

            ProjectInfo changedWorldTopicsInfo = new ProjectInfo();
            changedWorldTopicsInfo.setId("12345");
            changedWorldTopicsInfo.setName("World Topics")
            changedWorldTopicsInfo.setTeam(marketsTeam)
            changedWorldTopicsInfo.setArchived(false)
            worldTopics.build(changedWorldTopicsInfo);

            List<ProjectChange> projectChanges = Lists.newArrayList();
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

            ProjectInfo changedWorldTopicsInfo = new ProjectInfo();
            changedWorldTopicsInfo.setId("12345");
            changedWorldTopicsInfo.setName("World Topics")
            changedWorldTopicsInfo.setTeam(worldTeam)
            changedWorldTopicsInfo.setArchived(true)
            worldTopics.build(changedWorldTopicsInfo);

            List<ProjectChange> projectChanges = Lists.newArrayList();
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
            payload.get("attachments").each{ attachment -> assertArchiveChanges(attachment);}
    }

    void "notify when project has multiple changes"(){
        String postedUrl = null
        HashMap payload = null
        given:

            ProjectInfo changedWorldTopicsInfo = new ProjectInfo();
            changedWorldTopicsInfo.setId("12345");
            changedWorldTopicsInfo.setName("World Topics")
            changedWorldTopicsInfo.setTeam(marketsTeam)
            changedWorldTopicsInfo.setArchived(true)
            worldTopics.build(changedWorldTopicsInfo);

            List<ProjectChange> projectChanges = Lists.newArrayList();
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
            payload.get("attachments").each{ attachment -> assignToChangeAssert(attachment);}
    }


    private void assertNameChanges(HashMap<String, Object> attachment){
        String title = attachment.get("text")
        assert title.contains(worldTopicsInfo.getId())
        List<HashMap> fields = attachment.get("fields")
        assert ("World Topicsssss changed").equals(fields.get(0).get("value"))
        assert ("new name").equals(fields.get(0).get("title"))
        assert ("World Topics").equals(fields.get(1).get("value"))
        assert ("old name").equals(fields.get(1).get("title"))
    }

    private void assertTeamChanges(HashMap<String, Object> attachment){
        String title = attachment.get("text")
        assert title.contains(worldTopicsInfo.getId())
        List<HashMap> fields = attachment.get("fields")
        assert ("Markets").equals(fields.get(0).get("value"))
        assert ("new team").equals(fields.get(0).get("title"))
        assert ("World").equals(fields.get(1).get("value"))
        assert ("old team").equals(fields.get(1).get("title"))
    }

    private void assertArchiveChanges(HashMap<String, Object> attachment){
        String title = attachment.get("text")
        assert title.contains(worldTopicsInfo.getId())
        assert title.contains("archived")
        assert attachment.get("fields").size() == 0
    }

    private void assignToChangeAssert(HashMap<String, Object> attachment){
        String title = attachment.get("text")
        if (title.contains("team")){
            assertTeamChanges(attachment)
        }
        else{
            assertArchiveChanges(attachment)
        }
    }


}
