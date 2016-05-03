package com.ft.services

import org.junit.Rule
import org.springframework.boot.test.OutputCapture
import spock.lang.Specification

class BotSchedulerSpec extends Specification {
    private static final int ONE_HOUR = 60 * 60_000
    private static final int FIVE_MINUTES = 5 * 60_000
    private static final int TWENTY_SEC = 20_000

    private BotScheduler botScheduler
    private AsanaBotService mockAsanaBotService

    @Rule
    OutputCapture capture = new OutputCapture()

    void setup() {
        mockAsanaBotService = Mock(AsanaBotService)
        botScheduler = new BotScheduler(mockAsanaBotService)

        capture.flush()
    }

    void "runTwentySecondsBots"() {
        when:
            botScheduler.runTwentySecondsBots()
        then:
            1 * mockAsanaBotService.runBots(TWENTY_SEC)
            0 * _
    }

    void "runOneHourBots"() {
        when:
            botScheduler.runOneHourBots()
        then:
            1 * mockAsanaBotService.runBots(ONE_HOUR)
            0 * _
    }

    void "runFiveHoursBots"() {
        when:
            botScheduler.runFiveMinutesBots()
        then:
            1 * mockAsanaBotService.runBots(FIVE_MINUTES)
            0 * _
    }
}
