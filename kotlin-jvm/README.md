# Kotlin/JVM CheckVies Client Example

This project demonstrates how to configure and run the `com.checkvies:client` library in a simple Kotlin/JVM application.

## Requirements

- JDK 17+
- Network access to the configured CheckVies API URL

## Configuration

Configuration is read from environment variables in `AppConfig.fromEnvironment()`.

### Required

- `CHECKVIES_API_KEY` — your API key

### Optional

- `CHECKVIES_BASE_URL` (default: `https://api.checkvies.com`)
- `CHECKVIES_REQUEST_TIMEOUT_MS` (default: `30000`)
- `CHECKVIES_CONNECT_TIMEOUT_MS` (default: `10000`)
- `CHECKVIES_SOCKET_TIMEOUT_MS` (default: `30000`)
- `CHECKVIES_HTTP_LOG_LEVEL` (default: `ALL`, allowed values: `ALL`, `HEADERS`, `BODY`, `INFO`, `NONE`)

## How to run

From the project root:

### PowerShell (Windows)

```powershell
$env:CHECKVIES_API_KEY = "your-real-api-key"
./gradlew.bat run
```

### Bash (Linux/macOS)

```bash
CHECKVIES_API_KEY="your-real-api-key" ./gradlew run
```

## What the demo does

The demo in `Main.kt` and `CheckViesService.kt`:

1. Loads config from environment
2. Creates `CheckViesClient`
3. Calls API endpoints:
   - `startCheck`
   - `getCheckStates`
   - `getCheckDetails`
4. Prints successful responses and meaningful error messages

## Build

```powershell
./gradlew.bat build
```
