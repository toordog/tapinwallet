# Tapin Wallet Developer Guide

Tapin Wallet is a interoperable crypto data wallet powered by the Bitcrumb network, built using JavaFX, Gluon, and GraalVM. It runs on macOS, Linux, Windows, Android, and iOS, allowing users to self-custody verifiable data using Bitcrumb’s proof-of-origin network. The project is designed to be fully cross-platform with a unified build system using Maven and GluonFX.

---

## Toolchain

| Tool | Version | Notes |
|------|----------|-------|
| **Java (GraalVM)** | 25 LTS | Oracle GraalVM 25+37.1 (build 25+37-LTS-jvmci-b01) |
| **Maven** | 3.9.11 | Unified build tool |
| **JavaFX** | 24.0.2 | Cross-platform UI framework |
| **GluonFX Maven Plugin** | 1.0.25 | For mobile/native builds |
| **Netbeans** | 27 | IDE |

To verify versions:  
```bash
java --version
mvn -v
mvn help:effective-pom | grep gluonfx.maven.plugin.version
```

---

## Running Locally

| Platform | Command |
|-----------|----------|
| **macOS** | `mvn -Pmacos-x64 clean javafx:run` |
| **Linux** | `mvn -Plinux-x64 clean javafx:run` |
| **Windows** | `mvn -Pdesktop clean javafx:run` |

---

## Deploying to Mobile

| Platform | Command | Notes |
|-----------|----------|-------|
| **Android** | `mvn -Pandroid clean gluonfx:compile gluonfx:link gluonfx:package gluonfx:install gluonfx:nativerun` | Build from Android |
| **iOS** | `need ios target` | Requires macOS and Xcode |

---

## Environment Setup

### macOS
```bash
export JAVA_HOME=$(/usr/libexec/java_home)
export GRAALVM_HOME=/Library/Java/JavaVirtualMachines/graalvm-25.jdk/Contents/Home
export PATH="$GRAALVM_HOME/bin:$PATH"
```

### Linux
```bash
export JAVA_HOME=/usr/lib/jvm/graalvm
export GRAALVM_HOME=/usr/lib/jvm/graalvm
export PATH="$GRAALVM_HOME/bin:$PATH"
```

### Windows (PowerShell)
```powershell
setx JAVA_HOME "C:\Program Files\GraalVM\graalvm-25"
setx GRAALVM_HOME "C:\Program Files\GraalVM\graalvm-25"
setx PATH "%GRAALVM_HOME%\bin;%PATH%"
```
---

## Project Information

| Item | Details |
|------|----------|
| **UI Resources** | `src/main/resources/com/tapinwallet` |
| **Branching Rule** | Development branches must follow `<name>-dev` (example: `marquez-dev`) |
| **Platform Profiles** | `macos-x64`, `linux-x64`, `desktop`, `android`, `ios` |
| **Build Tool** | Maven (configured via `pom.xml`) |
| **Native Builds** | Require GraalVM with native-image (`gu install native-image`) |

---

## Dependencies (install automatically)

| Library | Version | Purpose |
|----------|----------|----------|
| **JavaFX (controls, fxml, web)** | 24.0.2 | UI components |
| **Gluon Attach (util, display)** | 4.0.23 | Mobile support |
| **Charm Glisten** | 6.2.3 | Cross-platform UI toolkit |
| **Jackson (core, databind, annotations)** | 2.19.0 | JSON processing |
| **Lombok** | 1.18.38 | Code generation |
| **XRPL4J (core, client)** | 5.0.0 | XRPL interaction |

---

## Notes

- Platform-agnostic and can build/run on multiple operating systems.  
- iPhone builds require macOS and Xcode.  
- Android builds require Linux.  
- Native builds require GraalVM with native-image installed.  
- JavaFX runtime modules are auto-selected by Maven profiles.  
- UI resources must remain under `src/main/resources/com/tapinwallet`.  
- If builds fail, verify that `GRAALVM_HOME` is correct and native-image is installed.  

---

© 2025 Tapin Wallet / [Crumbylabs](https://crumbylabs.com). All rights reserved.
