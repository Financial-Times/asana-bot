package com.ft.geb.pages

import geb.Page

class GoogleSignin extends Page{

    static url = "https://accounts.google.com/ServiceLogin"
    static at = { title == "Sign in - Google Accounts" }
    static content = {
        emailForm { $("#Email") }
        nextButton { $("#next") }

        passwordForm { $("#Passwd") }
        signInButton { $("#signIn") }

        welcomeText { $('#view_container') }
    }
}
