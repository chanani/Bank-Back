# GUGU BANK

## 실생활에서 사용할 수 있는 가계부입니다.

---
## 🌎 Site
**운영 사이트** : [👉🏻클릭](https://gugu.ohuniverse.cloud)

## 🔧 Tech Stack

### Stack
* **Java**: 21
* **Spring Boot**: 3.5.3
* **Gradle**: 8.14.2
* **ORM**: JPA, QueryDSL
* **Database**: MySQL 8.0.33
* **Authentication**: JWT
* **Documentation**: Swagger 3


### 📚 API Documentation

프로젝트를 실행한 후, 아래 링크에서 **Swagger UI**를 통해 모든 API 문서를 확인할 수 있습니다.

* **Swagger UI**: [http://localhost:11101/gugu-bank/user/swagger-ui.html](http://localhost:11101/gugu-bank/user/swagger-ui.html)

### Package Structure

```
src/main/java/com/template/
├── controller/         # API 컨트롤러
├── domain/             # 비즈니스 로직 & 서비스
├── entity/             # JPA 엔티티
└── global/             # 공통 설정 & 유틸리티
```

---

## 🚀 Getting Started

### Prerequisites
* Java 21
* Gradle 8.14+
* Database (설정 필요)

### Installation
1.  **Clone the repository**
    ```bash
    git clone https://github.com/chanani/Bank-Back.git
    cd Bank-Back
    ```
2.  **Build the project**
    ```bash
    ./gradlew build
    ```
3.  **Run the application**
    ```bash
    ./gradlew bootRun
    ```

### Configuration
`application.yml` 파일에서 다음 설정들을 구성하세요.
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/schema-name?serverTimezone=Asia/Seoul
    username: your-username
    password: your-password
```