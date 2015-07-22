package com.ft.geb

import com.ft.geb.tests.LoginTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses([
        LoginTest.class,
])

class FunctionalSuiteSpec {
}
