export interface AppConfig {
  apiKey: string;
  baseUrl: string;
}

export function loadConfig(): AppConfig {
  const apiKey = process.env.CHECKVIES_API_KEY?.trim();
  if (!apiKey) {
    throw new Error("Environment variable CHECKVIES_API_KEY is required, register an account here and get your first key: https://checkvies.com/auth/signup");
  }

  return {
    apiKey,
    baseUrl: process.env.CHECKVIES_BASE_URL?.trim() || "https://api.checkvies.com",
  };
}
