package de.frederikheinrich

import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.regex.Pattern

class LoggerConfigPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create("loggerConfig", LoggerConfigPluginExtension)

        project.task('updateLogbackConfig') {
            doLast {
                def extension = project.extensions.loggerConfig
                def configFilePath = extension.configFilePath
                project.logger.lifecycle("> Task :updateLogbackConfig using config file at ${configFilePath}")

                def logbackFile = project.file(configFilePath)
                if (!logbackFile.exists()) {
                    throw new FileNotFoundException("logback.xml file not found at $logbackFile")
                }

                def logbackContent = logbackFile.text
                def loggerFinder = new LoggerFinder(project.projectDir.path)
                ArrayList<String> loggers = loggerFinder.findLoggers()
                ArrayList<String> existingLoggers = findExistingLoggers(logbackContent)
                ArrayList<String> newLoggers = new ArrayList<String>()

                loggers.forEach {
                    if(!existingLoggers.contains(it)){
                        newLoggers.add(it);
                    }
                }

                if (!newLoggers.isEmpty()) {
                    def updatedLoggersContent = buildLoggersContent(newLoggers)
                    def updatedLogbackContent = logbackContent.replace(
                           '</configuration>',
                            "${updatedLoggersContent}\n</configuration>"
                    )
                    logbackFile.write(updatedLogbackContent)
                }

                project.logger.lifecycle('> Task :updateLogbackConfig Completed')
            }
        }

        project.tasks.named('build').configure { it.dependsOn project.tasks.named('updateLogbackConfig') }
    }

    private List<String> findExistingLoggers(String content) {
        List<String> existingLoggers = new ArrayList<>()
        def existingLoggerPattern = Pattern.compile('<logger\\s+name="([^"]+)"\\s+level="[^"]+"\\s*/?>')
        def matcher = existingLoggerPattern.matcher(content)
        while (matcher.find()) {
            existingLoggers.add(matcher.group(1) as String) // Ensure the added value is a String
        }
        return existingLoggers
    }

    private String buildLoggersContent(Collection<String> loggers) {
        def loggersContent = new StringBuilder()
        def currentPackage = ""

        loggers.toSorted().each { logger ->
            def packageName = logger.substring(0, logger.lastIndexOf('.'))
            if (packageName != currentPackage) {
                currentPackage = packageName
                loggersContent.append("\n")
            }
            loggersContent.append("    <logger name=\"${logger}\" level=\"DEBUG\"/>\n")
        }
        return loggersContent.toString()
    }

    class LoggerFinder {
        private final String projectDirPath

        LoggerFinder(String projectDirPath) {
            this.projectDirPath = projectDirPath
        }

        ArrayList<String> findLoggers() {
            def loggerPattern = ~/import\s+lombok.extern.slf4j.Slf4j;/
            def packagePattern = Pattern.compile('package\\s+([a-zA-Z0-9._]+);')
            def classPattern = Pattern.compile('public\\s+class\\s+([a-zA-Z0-9_]+)\\s+')
            def loggers = [] as List

            new File(projectDirPath, "src/main/java").eachFileRecurse { file ->
                if (file.name.endsWith('.java')) {
                    def content = file.text
                    if (loggerPattern.matcher(content).find()) {
                        def packageMatcher = packagePattern.matcher(content)
                        def classMatcher = classPattern.matcher(content)
                        if (packageMatcher.find() && classMatcher.find()) {
                            def packageName = packageMatcher.group(1)
                            def className = classMatcher.group(1)
                            loggers.add("${packageName}.${className}" as String)
                        }
                    }
                }
            }
            return loggers
        }
    }
}

class LoggerConfigPluginExtension {
    String configFilePath = "src/main/resources/logback.xml"
}
