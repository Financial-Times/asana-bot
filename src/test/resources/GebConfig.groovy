import org.openqa.selenium.chrome.ChromeDriver
import org.apache.tools.ant.taskdefs.condition.Os
import org.openqa.selenium.chrome.ChromeOptions


driver = {
    def chromeDriver = new File('/usr/local/bin/chromedriver')
    System.setProperty('webdriver.chrome.driver', chromeDriver.absolutePath)
    def options = new ChromeOptions()
    options.addArguments('--start-maximized')

    new ChromeDriver(options)
}

baseUrl = "http://localhost:8080"

reportsDir = new File("target/geb-reports")
reportOnTestFailureOnly = true

