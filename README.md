# 📝 Smart Journaling Application

## Project Overview

**Smart Journaling** is a Java-based journaling application designed to help users reflect on their daily experiences, track emotional patterns, and improve mental well-being.  
The system integrates **weather tracking** and **AI-powered sentiment analysis** to enhance traditional journaling, aligning with **UN Sustainable Development Goal 3: Good Health and Well-Being**.

This project is developed for **WIX1002 Fundamentals of Programming** and fulfills both **basic** and **extra feature** requirements outlined in the assignment specification.

---

## 1. Objectives

- Encourage consistent journaling habits  
- Increase emotional self-awareness through mood tracking  
- Provide meaningful weekly insights using real data  
- Demonstrate API integration and Java modular programming  

---

## 2. Technology Stack

| Layer | Technology |
|-----|-----------|
| Language | Java |
| Backend | Spring Boot |
| Frontend | JavaFX |
| Database | SQLite |
| APIs | Malaysia Weather API, Hugging Face Sentiment Analysis |
| Build Tool | Maven |
| Version Control | Git & GitHub |

---

## 3. Project Structure

```
SmartJournaling-main/
│
├── Frontend/
│   ├── src/main/java/com/example/smartjournaling/
│   │   ├── App.java
│   │   ├── frontend/
│   │   └── model/
│   └── pom.xml
│
├── back/
│   ├── src/main/java/com/example/smartjournaling/backend/
│   │   ├── controller/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   └── util/
│   │       └── EnvLoader.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── SmartJournal.sqbpro
│
├── .gitignore
└── README.md
```

---

## 4. Implemented Features 

### User Authentication and Management
- User registration and login
- Email-based authentication
- Secure password handling

### Data Storage
- Persistent SQLite database
- Data retained after program termination

### Welcome Page & Menu
- Time-based greeting (GMT+8)
- Personalized welcome message

### Journal Entry Management
- Create, view, edit daily journals
- Special handling for today’s journal

### Weather Data Integration (GET API)
- Automatic weather retrieval via API
- Weather linked to journal entry

### Sentiment Analysis Integration (POST API)
- AI-powered mood detection
- Hugging Face DistilBERT model

### Weekly Summary Feature
- 7-day mood and weather overview

---

## 5. Extra Features 

### Graphical User Interface (GUI)
* Clean, user-friendly interface using JavaFX
* Improved usability compared to CLI
  
### Layered Backend Architecture
* Implements Controllers, Services, Repositories, DAOs, and DTOs
* Follows real-world principles for better maintainability and scalability

### Database-Oriented Persistence
* Repository-based persistence enables efficient data querying
* Optimized for generating weekly summaries

### Secure Environment Handling
* Uses `SecurityConfig.java` to enforce endpoint protection and controlled access to user data.
* Sensitive API tokens stored using `.env`
* `EnvLoader.java` reads environment variables safely
* `.env` excluded using `.gitignore`

### Modular Weekly Summary Design
* Separates journal and weather data into modular components

---

## 6. How to Run

### Prerequisites
- Java JDK 17+
- Maven

### Setup
Create `.env` file:
```
BEARER_TOKEN=hf_your_token_here
```

Run backend:
```
cd back
mvn spring-boot:run
```

Run frontend:
```
cd Frontend
mvn javafx:run
```

---

## 7. SDG Alignment

Supports **SDG 3: Good Health and Well-Being** through emotional awareness and reflection.

---

## 8. Special Thanks

We would like to extend our gratitude to:

* University of Malaya for providing the opportunity to work on this project.

* Our team members:
  `Jasmine Chin Jia Yee` • `Josephine Ding Jie Yu` • `Ng Shao Ern`
`Ng Geok Liu` • `Jasmine Chin Hui Ying` • `Ng Yue Qhi`
  for commiting and collaborating throughout the project.
