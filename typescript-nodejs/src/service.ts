import {
  CheckViesClient,
  CheckViesApiException,
  CheckViesClientException,
} from "@checkvies/client";
import type { AppConfig } from "./config.js";

const POLL_INTERVAL_MS = 1000;
const MAX_POLL_ATTEMPTS = 10;

export class CheckViesService {
  private readonly client: CheckViesClient;

  constructor(config: AppConfig) {
    this.client = new CheckViesClient({
      apiKey: config.apiKey,
      baseUrl: config.baseUrl,
    });
  }

  async runDemo(): Promise<void> {
    // 1. Start check
    const created = await this.client
      .startCheck({ number: "7171642051", isoAlpha2: "PL", cache: {maxAgeMinutes: "60", cacheOnly: false}})
      .then((res) => {
        console.log(`startCheck success: requestId=${res.id}`);
        return res;
      })
      .catch((e) => {
        logError("startCheck", e);
        return null;
      });

    if (!created) return;

    // 2. Get states list
    await this.client
      .getCheckStates({ requests: [created.id] })
      .then((stateList) => {
        console.log(`getCheckStates success: ${stateList.requests.length} item(s)`);
        stateList.requests.forEach((item) => {
          console.log(` - requestId=${item.requestId}, state=${item.state}`);
        });
      })
      .catch((e) => logError("getCheckStates", e));

    // 3. Poll for final result
    for (let i = 0; i < MAX_POLL_ATTEMPTS; i++) {
      console.log(`Retrieve status, attempt ${i + 1}`);

      const details = await this.client
        .getCheckDetails(created.id)
        .then((res) => {
          console.log(`getCheckDetails success: id=${res.requestId}, state=${res.state}`);
          if (res.state === "Success") {
            console.log(` - valid=${res.result?.valid}, cacheUsed=${res.result?.cacheUsed}`);
          }
          if (res.errorCode) {
            console.log(` - errorCode=${res.errorCode}`);
          }
          return res;
        })
        .catch((e) => {
          logError("getCheckDetails", e);
          return null;
        });

      if (!details) break;

      if (details.state === "Pending" || details.state === "TemporaryError") {
        await sleep(POLL_INTERVAL_MS);
        continue;
      }

      break;
    }
  }
}

function sleep(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function logError(operation: string, error: unknown): void {
  if (error instanceof CheckViesApiException) {
    console.error(
      `${operation} failed: code=${error.errorCode}, status=${error.statusCode}, message=${error.message}`
    );
  } else if (error instanceof CheckViesClientException) {
    console.error(`${operation} failed (client error): ${error.message}`);
    console.error("Cause:", error.cause);
  } else {
    console.error(`${operation} failed unexpectedly:`, error);
  }
}
