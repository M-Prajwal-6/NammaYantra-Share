# 🚗 NammaYantra-Share



## 📋 About the Project
NammaYantra-Share is an Android application designed for vehicle sharing and transportation services. It provides users with a platform to share rides, vehicles, or transportation-related services in a convenient and efficient manner.

## ✨ Features
- 👥 User-friendly interface for ride and vehicle sharing
- 📍 Real-time location tracking and mapping
- 🔐 Secure user authentication and profiles
- 💬 In-app messaging and notifications
- 💳 Payment integration for seamless transactions
- ⭐ Rating and review system for users and drivers

## 📋 Prerequisites
Before running this project, ensure you have the following installed:
- 🤖 Android Studio (latest version recommended)
- ☕ Java Development Kit (JDK) 8 or higher
- 📱 Android SDK with API level 21 or higher
- 🔥 Google Play Services (for Firebase integration)

## 🚀 Installation
1. 📥 Clone the repository:
   ```
   git clone <repository-url>
   cd Nammayantre-app
   ```

2. 📂 Open the project in Android Studio:
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned directory and select it

3. 🔄 Sync the project with Gradle files:
   - Android Studio should automatically prompt to sync
   - If not, go to File > Sync Project with Gradle Files

## 🏗️ Building and Running
1. 📱 Connect an Android device or start an emulator
2. ▶️ In Android Studio, click the "Run" button (green play icon) or press Shift + F10
3. 🎯 Select your target device/emulator
4. ⚡ The app will be built and installed automatically

Alternatively, you can build from command line:
```
./gradlew assembleDebug
./gradlew installDebug
```

## 📁 Project Structure
```
Nammayantre-app/
├── 📄 .gitignore
├── 📄 .gitattributes (if present)
├── 📁 .gradle/                    # Gradle cache and build files
├── 📁 .idea/                      # Android Studio IDE files
├── 📁 app/                        # Main application module
│   ├── 📄 build.gradle            # App-level build configuration
│   ├── 📄 google-services.json    # Firebase configuration
│   ├── 📁 src/
│   │   └── 📁 main/
│   │       ├── 📄 AndroidManifest.xml    # App manifest
│   │       ├── 🖼️ ic_launcher-playstore.png  # Play Store icon
│   │       ├── 📁 java/           # Java/Kotlin source code
│   │       └── 📁 res/            # Resources (layouts, drawables, etc.)
│   └── 📁 build/                  # Build outputs and intermediates
├── 📄 build.gradle                # Root-level build configuration
├── 📁 gradle/
│   └── 📁 wrapper/
│       └── 📄 gradle-wrapper.properties
├── 📄 gradle.properties           # Gradle properties
├── 📄 gradlew                     # Gradle wrapper (Unix)
├── 📄 gradlew.bat                 # Gradle wrapper (Windows)
├── 📄 local.properties            # Local properties (SDK path, etc.)
├── 📱 NammaYantra_v5.apk          # Built APK file
├── 📄 README.md                   # This file
├── 📄 settings.gradle             # Project settings
└── 📁 .git/                       # Git repository
```

### Key Directories Explained:
- **📁 app/**: Main application module containing all app-specific code and resources
- **📁 src/main/**: Main source directory with manifest, code, and resources
- **📁 java/**: Contains Java/Kotlin source files organized by package structure
- **📁 res/**: Resources like layouts, images, strings, and styles
- **📁 build/**: Generated build artifacts and intermediate files

## 🤝 Contributing
We welcome contributions to NammaYantra-Share! Please follow these steps:

1. 🍴 Fork the repository
2. 🌿 Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. 💾 Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. 📤 Push to the branch (`git push origin feature/AmazingFeature`)
5. 🔄 Open a Pull Request

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.