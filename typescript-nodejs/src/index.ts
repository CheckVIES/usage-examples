import { CheckViesApiException, CheckViesClientException } from "@checkvies/client";
import { loadConfig } from "./config.js";
import { CheckViesService } from "./service.js";
import "dotenv/config";

const config = loadConfig();
console.log(`Starting CheckVies example against ${config.baseUrl}`);

const service = new CheckViesService(config);

try {
  await service.runDemo();
} catch (e) {
  if (e instanceof CheckViesApiException) {
    console.error(`CheckVies API error: code=${e.errorCode}, status=${e.statusCode}, message=${e.message}`);
  } else if (e instanceof CheckViesClientException) {
    console.error(`CheckVies client error: ${e.message}`);
  } else {
    console.error("Unexpected error:", e);
  }
  process.exit(1);
}
