package com.ft.geb

import com.ft.geb.tests.LoginFunctCase
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses([
        LoginFunctCase.class,
])

class FunctionalSuiteSpec {
}
