# Noonlight Demo

The purpose of this application is to demonstrate Android coding techniques while using the Noonlight 
dispatch API.

## Materials Used

- Timber logging library; It's extensible and easy-to-use.
- MockK for mocking in unit / UI tests.

## Questions

- "Launching the application should request “Always” location permissions if not already granted." Is this a hard requirement? Android recommends asking only in context requiring the permission. For example, creating an Alarm.

## Assumptions

- We need fine-grained location permissions to track the individual and respond effectively.
- We do not need to create a custom Theme for Compose. Used MaterialTheme.