package com.ft.services

import spock.lang.Specification

class BotSchedulerSpec extends Specification {
    private BotScheduler botScheduler
    private AsanaService mockAsanaService = Mock(AsanaService)

    void setup() {
        botScheduler = new BotScheduler()
        botScheduler.asanaService = mockAsanaService
    }

    void "GraphicsBot"() {
        when:
            botScheduler.graphicsBot()

        then:
            1 * mockAsanaService.addGraphicsProjectToGraphicsBotAssignedTasks()
            0 * _
    }

    void "PicturesBot"() {
        when:
            botScheduler.picturesBot()

        then:
            1 * mockAsanaService.addPicturesProjectToPicturesBotAssignedTasks()
            0 * _

    }
}
