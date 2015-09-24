package com.ft.geb

import com.ft.geb.tests.LoginFunctCase
import org.junit.runner.RunWith
import org.junit.runners.Suite
import spock.lang.Ignore

@RunWith(Suite.class)
@Suite.SuiteClasses([
        //LoginFunctCase.class, commenting this out since password for test account has been changed
])
class FunctionalSuiteSpec {
}
