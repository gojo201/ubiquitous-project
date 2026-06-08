# RunMap - Fitness Tracker App
A Strava-like fitness tracking Android app built with Kotlin, Jetpack Compose and Firebase.

## Team Members
- **Athina Papachatzi** - UI/UX, Navigation, Authentication, Design Tokens, Unit Tests, Push Notifications
- **Taha Can Senel** - GPS tracking, Maps, Weather API, Firebase backend

## Features
- User Authentication(register, login, logout)
- GPS workout tracking
- Live map route display
- Weather information 
- Workout history and statistics
- Calorie calculation
- Push notifications
- Light/Dark theme toggle
- Change password

## Architecture
- **UI Layer** - Jetpack Compose screens, ViewModels
- **Domain Layer** - Use cases, data models
- **Data Layer** - Firebase storage, Retrofit API calls

## Tech Stack
- Kotlin
- Jetpack Compose 
- Firebase Authentication + Firestore
- Google Maps SDK 
- Retrofit + Open-Meteo Weather API 
- Firebase Cloud Messaging (Push Notifications)
- Navigation Compose
- Material Design 3

## Setup Instructions
1. Clone the GitHub repository
2. Open in Android Studio 
3. Add 'google-services.json' to /app folder (from Firebase)
4. Add Maps API key to 'AndroidManifest.xml'
5. Build and run the app

## Project Structure
## Project Structure
```
com.tcsappdev.ubiquitous
├──data
│   └──remote
├──domain
│   └──model
│   └──repository
│   └──usecase
├──ui
│   └──navigation
│   └──screens
│   └──theme
│   └──viewmodel
├──utils
```

## Testing 
- Email validation
- Password validation
- Password matching 
- Name validation