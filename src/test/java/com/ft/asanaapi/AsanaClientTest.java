package com.ft.asanaapi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/** Test for testing integration with Asana
 *
 * set the following environment variables before running:
 * ASANA_TEST_API_KEY
 * ASANA_TEST_WORKSPACE
 * ASANA_TEST_DESTINATION_PROJECT
 *
 */
public class AsanaClientTest {

    AsanaClient asanaClient;

    @Before
    public void setup(){
        asanaClient = new AsanaClient(
                System.getenv("ASANA_TEST_API_KEY"),
                System.getenv("ASANA_TEST_WORKSPACE"),
                Asana.ASANA_API_URL);
    }

    @Ignore
    @Test
    public void testIntegrationMoveTasks() {
        asanaClient.addProjectToCurrentlyAssignedIncompleteTasks(System.getenv("ASANA_TEST_DESTINATION_PROJECT"));
        Assert.assertTrue("PLACEHOLDER", true);
    }
}