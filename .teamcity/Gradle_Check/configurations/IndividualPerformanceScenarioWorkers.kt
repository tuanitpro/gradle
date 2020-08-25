package configurations

import common.Os
import common.applyPerformanceTestSettings
import common.buildToolGradleParameters
import common.checkCleanM2
import common.gradleWrapper
import common.individualPerformanceTestArtifactRules
import common.performanceTestCommandLine
import jetbrains.buildServer.configs.kotlin.v2019_2.AbsoluteId
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import model.CIBuildModel

class IndividualPerformanceScenarioWorkers(model: CIBuildModel, os: Os = Os.linux) : BaseGradleBuildType(model, init = {
    uuid = model.projectPrefix + "IndividualPerformanceScenarioWorkers${os.name.capitalize()}"
    id = AbsoluteId(uuid)
    name = "Individual Performance Scenario Workers - ${os.name.capitalize()}"

    applyPerformanceTestSettings(os = os, timeout = 420)
    artifactRules = individualPerformanceTestArtifactRules

    if (os == Os.windows) {
        // to avoid pathname too long error
        vcs {
            checkoutDir = "C:\\gradle-perf"
        }
    }

    params {
        param("baselines", "defaults")
        param("templates", "")
        param("channel", if (os == Os.linux) "commits" else "commits-windows")
        param("checks", "all")
        param("runs", "defaults")
        param("warmups", "defaults")
        param("scenario", "")

        param("env.ANDROID_HOME", os.androidHome)
        param("env.PATH", "%env.PATH%:/opt/swift/4.2.3/usr/bin")
    }

    steps {
        script {
            name = "KILL_GRADLE_PROCESSES"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            scriptContent = if (os == Os.windows) killAllGradleProcessesWindows else killAllGradleProcessesLinux
        }
        gradleWrapper {
            name = "GRADLE_RUNNER"
            tasks = ""
            gradleParams = (
                performanceTestCommandLine(
                    "clean %templates% :performance:fullPerformanceTest",
                    "%baselines%",
                    """--scenarios "%scenario%" --warmups %warmups% --runs %runs% --checks %checks% --channel %channel%""",
                    individualPerformanceTestJavaHome(os),
                    os
                ) +
                    buildToolGradleParameters(isContinue = false, os = os) +
                    buildScanTag("IndividualPerformanceScenarioWorkers") +
                    model.parentBuildCache.gradleParameters(os)
                ).joinToString(separator = " ")
        }
        checkCleanM2(os)
    }

    applyDefaultDependencies(model, this)
})
