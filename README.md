# StepCal Capture

StepCal Capture is an Android fitness-tracking app built with Java, XML, Spring Boot, and MySQL, featuring activity tracking, personalized goals, JWT authentication, and an AI fitness coach.

Originally developed as a team project for CSC299 (Junior Project Design), I later reconstructed and expanded it to bring the original vision closer to life. Along the way, I explored how the free version of ChatGPT could assist with app development, using it for coding support, debugging, and problem-solving.

## Screenshots

<table>
  <tr>
    <td align="center" width="33%">
      <img src="screenshots/Login.png" alt="Login screen" width="130"><br>
      <sub><strong>Login</strong></sub>
    </td>
    <td align="center" width="33%">
      <img src="screenshots/Step%20Count.png" alt="Step count screen" width="130"><br>
      <sub><strong>Step Count</strong></sub>
    </td>
    <td align="center" width="33%">
      <img src="screenshots/Today's%20plan.png" alt="Today's plan screen" width="130"><br>
      <sub><strong>Today's Plan</strong></sub>
    </td>
  </tr>
  <tr>
    <td align="center" width="33%">
      <img src="screenshots/Today's%20progress.png" alt="Today's progress screen" width="130"><br>
      <sub><strong>Today's Progress</strong></sub>
    </td>
    <td align="center" width="33%">
      <img src="screenshots/My%20profile.png" alt="My profile screen" width="130"><br>
      <sub><strong>My Profile</strong></sub>
    </td>
    <td align="center" width="33%">
      <img src="screenshots/StepCal%20Coach.png" alt="StepCal Coach screen" width="130"><br>
      <sub><strong>StepCal Coach</strong></sub>
    </td>
  </tr>
</table>

## Features

- **Step Tracking** — tracks walking activity using supported Android step sensors.
- **Calorie Tracking** — calculates calorie estimates and integrates activity data with the backend.
- **Daily Tasks and Progress** — retrieves fitness tasks and records completed activity.
- **User Profiles** — retrieves profile information used by the fitness features.
- **Authentication** — registration and login with JWT-based authentication for protected API endpoints.
- **AI Fitness Coach** — integrates Google's Gemini API through the Spring Boot backend for conversational fitness assistance.
- **Backend Integration** — connects the Android application to REST endpoints backed by MySQL.

## Technology Stack

| Component | Technologies |
|---|---|
| Android Frontend | Java, XML, Android SDK |
| UI | AndroidX AppCompat, Material Components, ConstraintLayout |
| API Communication | Retrofit, Gson |
| Location Services | Google Play Services Location |
| Maps Dependency | MapLibre Native for Android |
| Backend | Java, Spring Boot 3.2.4 |
| Security | Spring Security, JSON Web Tokens (JWT) |
| Database | MySQL, Spring Data JPA, Hibernate |
| AI Integration | Google Gemini API, Google GenAI SDK |
| Additional Backend Support | Spring Mail, WebSocket |
| Testing | JUnit 5, Spring Boot Test, AndroidX Test, Espresso |
| Build Tools | Gradle, Maven |

## Architecture

StepCal Capture uses a client-server architecture.

<p align="center">
  <img src="screenshots/architecture.png" alt="StepCal Capture system architecture" width="650">
</p>

<p align="center"><em>High-level architecture of the StepCal Capture application and its backend services.</em></p>

- **Android client:** provides the fitness-tracking interface and communicates with backend endpoints through Retrofit.
- **Spring Boot backend:** exposes REST endpoints for authentication, profile information, task management, and AI chat.
- **MySQL database:** persists application data through Spring Data JPA and Hibernate.
- **Gemini integration:** processes fitness-chat requests through the backend, keeping the AI integration separate from the Android client.

The Android application and backend are maintained in the same repository.

## Repository Layout

```text
StepCal-Capture/
├── frontend/       # Android application
├── backend/        # Spring Boot REST API
├── docs/           # Project documentation and engineering report
├── screenshots/    # App screenshots and architecture diagram
├── .gitignore
└── README.md
```

## Quick Start — Windows

**Prerequisites:** Android Studio, a compatible JDK, MySQL, and a Gemini API key for AI chat.

### Run the Backend

Create a MySQL database named `stepcal` and configure the required environment variables: `DB_PASSWORD`, `GEMINI_API_KEY`, `JWT_SECRET_BASE64`, `MAIL_USERNAME`, and `MAIL_PASSWORD`.

From the repository root, run:

```bat
cd backend
mvnw.cmd spring-boot:run
```

The backend uses port `4006` by default.

### Build the Android App

From the repository root, run:

```bat
cd frontend
gradlew.bat assembleDebug
```

The debug APK is generated in `frontend/app/build/outputs/apk/debug/`.

### Device Testing

Configure the Android API base URL to use your development computer's reachable local IP address. The phone and computer must be able to communicate over the network.

For full technical details, see the [Project Report](docs/PROJECT_REPORT.md).

## Configuration and Security

- Never commit API keys, database passwords, email credentials, JWT secrets, or private environment files.
- Keep local configuration files out of Git and use environment variables for sensitive settings.
- Rotate credentials if they have been exposed.
- Use HTTPS, appropriate access controls, and request-abuse protections before deploying the backend publicly.
- Review inherited code and resolve ownership and permission questions before commercial use or redistribution.

## Development Status

StepCal Capture is a personal development project. The Android client and Spring Boot backend have been integrated and tested in a local development environment.

The backend has passed compilation checks and a lightweight JUnit test. That test verifies the Spring Boot annotation on the main application class; it does not constitute a full application-startup or integration test.

### Roadmap

- Expand automated backend and Android test coverage.
- Improve configuration for development and production environments.
- Complete the security and credential-handling audit.
- Improve deployment and setup documentation.
- Continue refining the fitness-tracking experience and AI coach.

## Project Report

The detailed engineering report documents the project background, architecture, implementation decisions, security work, build and test results, known limitations, and future improvements.

See the [Project Report](docs/PROJECT_REPORT.md).

## License

**Proprietary — All rights reserved.**

StepCal Capture is shared publicly for portfolio and demonstration purposes only. No license is currently granted to copy, modify, redistribute, or use this project commercially. Licensing and contribution rights will be reviewed before any public launch.

Some portions originated in an earlier team project; ownership and permissions for those contributions have not yet been fully confirmed.
