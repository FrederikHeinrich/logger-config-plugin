# Logger Config Plugin

[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v?label=Gradle%20Plugin%20Portal&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fde%2Ffrederikheinrich%2Flogger-config-plugin%2Fmaven-metadata.xml)](https://plugins.gradle.org/plugin/de.frederikheinrich.logger-config-plugin)

## Overview

The Logger Config Plugin updates your logger configuration file by scanning your source imports for logger implementations. This helps in maintaining a consistent and up-to-date logger configuration throughout your project.

## Features

- Automatically scans source files for logger implementations.
- Updates the logger configuration file based on identified logger implementations.
- Supports popular logger frameworks.

## Getting Started

### Applying the Plugin

To use the Logger Config Plugin, add the following to your `build.gradle` file:

```groovy
plugins {
    id 'de.frederikheinrich.logger-config-plugin' version '1.0.0'
}
```

### Configuration

The plugin works out-of-the-box with default settings. However, you can customize its behavior by adding configuration options in your `build.gradle` file:

```groovy
loggerConfig {
    // Add your configuration options here
}
```

### Example

Here's a basic example to get you started:

```groovy
plugins {
    id 'java'
    id 'de.frederikheinrich.logger-config-plugin' version '1.0.0'
}

loggerConfig {
    // Configuration options
    configFilePath = "src/main/resources/logback.xml"
}
```

## Usage

Once the plugin is applied and configured, it will automatically scan your source files and update the logger configuration file during the build process.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request with your improvements.

## License

This project is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file for details.


## Links

- [Gradle Plugin Portal](https://plugins.gradle.org/plugin/de.frederikheinrich.logger-config-plugin)