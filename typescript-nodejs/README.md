# TypeScript CheckVies Client Example

This project demonstrates how to configure and run the `@checkvies/client` library in a simple Node.js/TypeScript application.

## Requirements

- Node.js 18+
- Yarn 4+

## Installation

```bash
yarn install
```

## Configuration

Configuration is read from environment variables or a `.env` file.

### Required

- `CHECKVIES_API_KEY` — your API key

### Optional

- `CHECKVIES_BASE_URL` (default: `https://api.checkvies.com`)

### Option 1: .env file (recommended)

Create a `.env` file in this directory: CHECKVIES_API_KEY=your-real-api-key
CHECKVIES_BASE_URL=https://api.checkvies.com

Then just run:

```bash
yarn start
```

### Option 2: environment variables

PowerShell (Windows):

```powershell
$env:CHECKVIES_API_KEY = "your-real-api-key"
yarn start
```

Bash (Linux/macOS):

```bash
CHECKVIES_API_KEY="your-real-api-key" yarn start
```

## What the demo does

The demo in `src/index.ts` and `src/service.ts`:

1. Loads config from environment
2. Creates `CheckViesClient`
3. Calls API endpoints:
  - `startCheck`
  - `getCheckStates`
  - `getCheckDetails` with polling until a final state is reached
4. Prints successful responses and meaningful error messages
