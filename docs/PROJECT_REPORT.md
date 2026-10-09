# StepCal Capture — Project Report

## 1. Project Overview

StepCal Capture is an Android fitness-tracking application developed to bring everyday activity tracking, personalized fitness goals, and conversational fitness assistance into one application.

The project combines an Android client written in Java with a Spring Boot backend, MySQL persistence, JWT-based authentication, and integration with Google's Gemini API for AI-assisted chat.

The project originally began as a team project for **CSC299 (Junior Project Design)**, a second-year undergraduate course. After graduation, I revisited the application to strengthen my software development skills, improve the implementation, and continue developing the original concept.

This report documents the architecture, implementation technologies, development work, verification performed, known limitations, and potential future improvements.

## 2. Project Objectives

The main objectives of StepCal Capture are to:

- Provide an Android interface for tracking walking activity.
- Support calorie estimates and fitness-related tasks.
- Retrieve and display user profile information.
- Provide authentication for protected backend endpoints.
- Connect the Android application to a persistent backend.
- Integrate an AI-powered fitness chat assistant.
- Improve maintainability, configuration, and security through iterative development.

## 3. Technology Stack

| Area | Technologies |
|---|---|
| Android application | Java, XML, Android SDK |
| User interface | AndroidX AppCompat, Material Components, ConstraintLayout |
| API communication | Retrofit, Gson |
| Location services | Google Play Services Location |
| Maps dependency | MapLibre Native for Android |
| Backend | Java, Spring Boot 3.2.4 |
| Authentication and authorization | Spring Security, JWT |
| Persistence | MySQL, Spring Data JPA, Hibernate |
| AI integration | Google Gemini API, Google GenAI SDK |
| Additional backend functionality | Spring Mail, WebSocket |
| Testing | JUnit 5, Spring Boot Test, AndroidX Test, Espresso |
| Build tools | Gradle, Maven |

## 4. System Architecture

StepCal Capture follows a client–server architecture in which the Android application communicates with a Spring Boot backend.

### 4.1 Android Client

The Android client provides the user-facing fitness experience. It is implemented in Java and uses XML-based layouts and Android libraries for the interface.

Retrofit is used for communication with backend REST endpoints. The client includes fitness-related functionality such as step tracking, calorie estimates, task progress, user profiles, authentication, and AI chat.

The current development configuration uses a local-network backend address. This supports testing on devices that can reach the development computer, but it is not a public production endpoint.

### 4.2 Spring Boot Backend

The backend exposes REST endpoints and coordinates application services, authentication, data access, and AI chat requests.

Spring Security provides request authorization, while JWTs are used for authentication. Spring Data JPA and Hibernate provide the persistence layer for MySQL.

The backend is configured to run on port `4006` in the development profile.

### 4.3 Database

MySQL is used to persist application data. The backend accesses the database through Spring Data JPA and Hibernate.

The development configuration uses a database named `stepcal` on the local MySQL server. Hibernate schema updates are enabled in the development configuration.

For a production deployment, database configuration and schema-management practices should be reviewed and adapted to the deployment environment.

### 4.4 AI Fitness Chat

The application integrates Google's Gemini API through the Spring Boot backend.

The backend reads the Gemini API key from the `GEMINI_API_KEY` environment variable. This separates the API credential from the Android client and avoids embedding the key directly in the client source.

The service handles empty messages, empty model responses, and request failures with user-facing fallback messages. It also provides a more specific response when quota or rate-limit errors are detected.

The AI integration depends on valid credentials, provider availability, and applicable usage limits.

## 5. Authentication and Security Work

Security and configuration were important areas of the development and cleanup process.

### 5.1 JWT Configuration

The backend reads its signing secret from the `JWT_SECRET_BASE64` environment variable rather than relying on a secret hardcoded in the source.

The JWT service uses a configured token validity period of 24 hours for the access-token generation path. Refresh-token behavior should be assessed separately when reviewing the complete authentication flow.

### 5.2 Credential Handling

Sensitive configuration is supplied through environment variables, including:

- `DB_PASSWORD`
- `GEMINI_API_KEY`
- `JWT_SECRET_BASE64`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`

The production profile additionally requires `DB_URL` and `DB_USERNAME`.

Local environment files, generated build output, and IDE-specific configuration are excluded through Git ignore rules.

Credentials that have been exposed should be revoked or rotated before a public release. Removing a secret from the current source tree does not remove it from earlier Git history or previous public copies.

### 5.3 Logging

Authentication and debugging code was reviewed for unsafe output, including the risk of printing complete bearer tokens or sensitive login information.

The JWT authentication filter was updated to avoid logging the full authorization header. The login-details object's string representation was also changed to redact the password.

A complete review of all controllers, services, WebSocket handlers, and exception logging remains necessary before public deployment.

### 5.4 Deployment Security

The current configuration is intended for development and local testing. Before public deployment, the backend should use HTTPS, appropriately restricted access, protected secrets, and request-abuse controls.

Publicly accessible endpoints, including AI chat, require suitable usage controls to reduce abuse and unexpected provider costs.

## 6. Development and Implementation Work

Development involved iterative changes to the Android client and backend, followed by compilation, troubleshooting, and local device testing.

The work included:

- Configuring Android-to-backend communication over a local network.
- Correcting backend configuration so secrets are obtained from environment variables.
- Improving JWT and login-detail handling.
- Refining the Gemini chat service's error handling.
- Cleaning up project naming so the backend artifact and application class use StepCal Capture terminology.
- Maintaining separate development and production backend configuration files.
- Updating repository ignore rules for local configuration and generated files.
- Preparing project documentation for future maintenance and portfolio presentation.

The application was tested on physical Android devices connected to the same local network as the development computer. The user reported that the application and chat assistant worked in that setup.

## 7. Build and Test Verification

The following checks were performed during development:

| Check | Result |
|---|---|
| Backend Maven clean test | Passed |
| Lightweight JUnit test | Passed |
| Android debug build | Previously built successfully |
| Local-network Android-to-backend communication | Reported working on physical devices |
| Gemini chat in the local test setup | Reported working |

The lightweight JUnit test verifies that the main application class has the `@SpringBootApplication` annotation. It is not a full Spring application-context test and does not verify every endpoint or integration.

The build and testing results described here reflect the checks performed during development. They should not be interpreted as proof of production readiness or comprehensive security.

## 8. Known Limitations

The following limitations remain relevant:

- The Android API base URL is configured for a development network and is not suitable as a universal public endpoint.
- The backend has not been demonstrated as a fully deployed, production-ready service.
- Automated test coverage needs to expand beyond the existing lightweight backend test.
- Full endpoint, authentication, database, and AI integration testing remains necessary.
- Credential rotation and a complete repository-history audit are required if credentials were exposed.
- Logging and WebSocket behavior need a comprehensive security review.
- AI chat availability depends on provider access, quotas, and usage limits.
- Ownership and permissions for inherited contributions from the original team have not yet been fully confirmed.

## 9. Future Improvements

Potential future improvements include:

- Deploying the backend to a secure hosting environment.
- Replacing the development API URL with a configurable production endpoint.
- Expanding automated backend and Android test coverage.
- Reviewing authentication, authorization, WebSocket access, and request validation.
- Introducing stronger database migration and schema-management practices.
- Improving monitoring, error reporting, and API usage controls.
- Confirming ownership and permissions for inherited project contributions.

## 10. Conclusion

StepCal Capture combines an Android fitness-tracking interface with a Spring Boot backend, MySQL persistence, JWT-based authentication, and AI-assisted fitness chat.

The project provided practical experience in mobile application development, backend integration, configuration management, debugging, testing, and technical documentation. The current version has been exercised in a local development environment, while further testing, security review, deployment work, and ownership clarification remain necessary before a production release.

## License and Ownership Notice

This project is shared publicly for portfolio and demonstration purposes. Licensing and contribution rights are subject to review because portions originated in an earlier team project. Please contact the repository owner before reusing or redistributing the code.