package com.ft.backup.drive

import spock.lang.Specification

class FileRemoverSpec extends Specification {
    def "test buildQuery"() {
        given:
            String folderId = 'testId'
            String formattedDateTimeFrom = '2016-04-20T12:13:04'
        and:
            String expectedQuery = "'testId' in parents and mimeType != 'application/vnd.google-apps.folder' and modifiedTime <= '2016-04-20T12:13:04'"
        expect:
            FileRemover.buildQuery(folderId, formattedDateTimeFrom) == expectedQuery
    }
}
