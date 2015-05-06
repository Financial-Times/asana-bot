package com.ft.asanaapi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class AsanaClientTest {

    public static final String ASANA_TEST_API_KEY = "";
    public static final String ASANA_TEST_WORKSPACE = "";
    public static final String ASANA_TEST_DESTINATION_PROJECT = "";
    AsanaClient asanaClient;

    @Before
    public void setup(){
        asanaClient = new AsanaClient(ASANA_TEST_API_KEY, ASANA_TEST_WORKSPACE);
    }

    @Ignore
    @Test
    public void testIntegrationMoveTasks() {
        asanaClient.addProjectToCurrentlyAssignedIncompleteTasks(ASANA_TEST_DESTINATION_PROJECT);
        Assert.assertTrue("PLACEHOLDER", true);
    }
}