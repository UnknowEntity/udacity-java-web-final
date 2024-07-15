# JAVA WEB FINAL

## TEST

Test was run using **mockMvc**.

I use the vscode java extension to get the code coverage.

Here is the result.

![VSCODE TEST COVERAGE RESULT](/images/tests/test_coverage.png)

I have also install the jacoco plugin so there is a test coverage file that generated.

Here is the latest result.

[JACOCO TEST COVERAGE REPORT](/extra/test_jacoco/index.html)

Here is the command line I use to generate the jacoco coverage report.

```powershell
mvn jacoco:prepare-agent test install jacoco:report
```

The file could be found in `target/site/jacoco/index.html` after the running the above command line.

## SPLUNK

The Splunk data was push using HEC (HTTP Event Collector), I have temporary disable the splunk Appender in logback.xml but you could still run it on local machine if config correctly.

You just need to follow the `.env.example` to config the application.

Here is some splunk screenshots.

SPLUNK ALERT SETUP

![SPLUNK ALERT SETUP](/images/splunk/alert_setup.png)

SPLUNK ALERT RESULT

![SPLUNK ALERT RESULT](/images/splunk/alert_result.png)

SPLUNK DASHBOARD

![SPLUNK DASHBOARD](/images/splunk/dashboard_create.png)

SPLUNK DASHBOARD CODE

![SPLUNK DASHBOARD CODE](/images/splunk/dashboard_code.png)

## JENKINS & CI/CD

Here is the log of the finish pipelines.

[jenkins.log](/logs/jenkins.log)
