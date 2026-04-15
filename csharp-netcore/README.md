# CheckVIES API Usage Example (.NET Core)

This project demonstrates the integration of the `Deimdal.CheckVIES.Client` into a .NET 10 application.

## Key Features

- Use of `Microsoft.Extensions.Hosting` (Worker Service).
- Dependency injection for `ICheckViesClient`.
- Asynchronous validation and polling until completion.

## Configuration

Before running, you must specify your API key and service URL in `Program.cs`:

```csharp
// Program.cs
builder.Services.AddCheckViesClient("<your API key here>", "<service API URL here>");
```

## Running the Project

From the project directory, run the following command:

```bash
dotnet run
```

The application will start one validation for a test number, print the result to the console, and then stop.

## Dependencies

- `Deimdal.CheckVIES.Client` — Client library for interacting with the API.
