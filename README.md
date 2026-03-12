# 🏋️ FitNote - Fitness Activity Tracking Application
**A comprehensive Android-based health and workout monitoring system built with Java and SQLite.**

![Android](https://img.shields.io/badge/Platform-Android-brightgreen.svg)
![Java](https://img.shields.io/badge/Language-Java-orange.svg)
![SQLite](https://img.shields.io/badge/Database-SQLite-blue.svg)

---

## 📌 Project Overview
**FitNote** is a professional-grade fitness management application designed to help users track their physical activities and monitor essential health metrics. Developed as part of the **ICT3214 Mobile Application Development** module, this app focuses on data persistence, real-time calculations (BMI), and sophisticated data visualization.

The application provides a seamless user experience with a modern Indigo-themed interface, allowing users to log various workouts, visualize their weekly progress through stacked charts, and manage their personal health profiles efficiently.

## 🚀 Core Features
*   **🔐 Secure User Authentication**: Full registration and login system ensuring user data privacy and personalized tracking.
*   **⚖️ Intelligent BMI Engine**: Real-time Body Mass Index (BMI) calculator that updates instantly when user profile data changes, providing visual health status feedback.
*   **📊 Dynamic Weekly Progress**: High-performance **Stacked Bar Charts** using the MPAndroidChart library to visualize activity hours over a 7-day period.
*   **🎨 Color-Coded Activity Logs**: Unique color assignments for different activity types (Walking, Running, Cycling, etc.) for better visual organization in lists and charts.
*   **📱 Modern UI/UX**: Built with **Material Design 3** principles, featuring a responsive dashboard, interactive cards, and smooth navigation.
*   **💾 Robust Local Storage**: Full CRUD (Create, Read, Update, Delete) operations powered by **SQLite** to manage user sessions and workout history offline.

## 🏷️ Activity Labels & Categories
| Icon | Activity | Target Metric | Description |
| :--- | :--- | :--- | :--- |
| 🚶 | **Walking** | Steps / Duration | Daily movement and low-impact cardio |
| 🏃 | **Running** | Speed / Duration | High-intensity cardiovascular training |
| 🚴 | **Cycling** | Distance / Duration | Indoor/Outdoor biking and endurance |
| 💪 | **Push Ups** | Reps / Duration | Upper body strength and core stability |
| 🦵 | **Squats** | Reps / Duration | Lower body strength and explosive power |
| 🏋️ | **Weightlifting** | Sets / Duration | Strength training and muscle hypertrophy |
| 🤸 | **Jumping Jacks** | Reps / Duration | Full-body aerobic and agility training |
| 🚲 | **Bicycle Crunch** | Reps / Duration | Targeted abdominal and core conditioning |
| 💪 | **Bicep Curls** | Sets / Duration | Isolated upper arm strength training |
| 🏋️ | **Shoulder Press** | Sets / Duration | Upper body power and stability training |

## 🏗️ Technical Architecture
The application is built using a modular **Activity-based architecture**, ensuring clear separation of concerns between data management, business logic, and UI presentation.

### 🔄 System Data Flow
```text
[ User Interface ] <───> [ Logic Layer ] <───> [ SQLite Database ]
      │                        │                       │
      ▼                        ▼                       ▼
Add Activity UI         Duration Logic           Persistent Storage
BMI Profile UI          BMI Calculation          User & History Tables
Dashboard View          Chart Data Mapping       Query Results
```

## 🛠️ Technologies Used
| Category | Technology / Library |
| :--- | :--- |
| **Language** | Java (JDK 17+) |
| **Database** | SQLite (Local Persistent Storage) |
| **UI Framework** | Android XML / Material Design 3 |
| **Data Visualization** | MPAndroidChart (v3.1.0) |
| **Build System** | Gradle (Kotlin DSL / Groovy) |
| **Architecture** | MVC (Model-View-Controller) |

## 🗂️ Project Structure
```text
FitNote/
│
├── 📂 app/src/main/
│   ├── 📂 java/com/example/ict3214.../
│   │   ├── 🧠 DashboardActivity.java    # Main Hub, Charting & Data Sync
│   │   ├── 🧠 UserProfileActivity.java  # Health Stats & Account Management
│   │   ├── 🧠 add_activites.java       # Activity Logging & Logic
│   │   ├── 🧠 loginActivity.java       # Session Management
│   │   └── 🧠 DatabaseHelper.java      # Schema & SQLite CRUD Operations
│   │
│   ├── 📂 res/layout/                 # UI Design Files
│   │   ├── 🖼️ activity_dashboard.xml    # Modern Grid Layout
│   │   ├── 🖼️ activity_user_profile.xml # Profile Card Interface
│   │   └── 🖼️ dialog_edit_profile.xml   # Popup for updating health stats
│   │
│   └── 📂 res/drawable/               # Iconography & Custom Backgrounds
│
└── 📄 README.md
```

## ⚙️ Installation & Setup
1. **Clone the Repository**
   ```bash
   [git clone https://github.com/Kavinda/FitNote-Android-App.git](https://github.com/RashmikaKDH/ICT3214-Mobile-Application-Development-Mini-Project.git)
   ```
2. **Open in Android Studio**
   * Import the project and wait for Gradle sync to complete.
3. **Configure Dependencies**
   * Ensure `jitpack.io` is added to your `settings.gradle` to resolve the MPAndroidChart library.
4. **Run Application**
   * Deploy to an Emulator or Physical Device (API Level 30+ recommended).

## 👥 Team Members
This project was designed and developed by:

*   **K.D.H.Rashmika - ICT/2022/103 - 5706**
*   **K.M.G.K.N. Malwewa - ICT/2022/101 - 5704**
