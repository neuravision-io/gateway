# Start Up Docker Compose
For running on local machine, you can use docker-compose to start up databases, rabbitmq, keycloack.

```bash
cd docker; docker-compose up --no-recreate
```

## WebSocket Integration with Angular 

To integrate WebSocket communication in your Angular application and connect through the Spring Boot Gateway running on port 8080, follow these steps:

### 1. Install the necessary libraries

First, you need to install the `@stomp/stompjs` and `sockjs-client` libraries.

```bash
  npm install @stomp/stompjs sockjs-client
```
### Keycloak

#### Create a new realm

1. Create a Realm 'akogare-platform-realm'
2. Manager your admin Account and fill the required fields
   - email
   - first name
   - last name
3. Login Screen Customization, realm settings -> login
   - User registration: on
   - Forgot password: on
   - Remember me: on
   - Email as username: on
4. Ream Email: realm settings -> email. Fill the required fields
5. Create Realm Roles
    - ADMIN
    - USER
6. Create new Group 'akogare-platform-group'
    - Role mapping - > assign the roles -> filter by realm roles -> select the roles USER, ADMIN

#### Create a new client

1. Create a new client 'akogare-platform-openidconnect'
    - Client authentication: off
    - Authorization: off
    - Standard Flow Enabled: on
    - Direct Access Grants Enabled: on
    - Root URL: http://localhost:4200
    - Valid Redirect URIs: 
      - http://localhost:4200/*
      - http://localhost:8080/*
      - https://oauth.pstmn.io/v1/callback
    - Web Origins: *

2. Create a new client 'akogare-app-cc'
    - Client authentication: on
    - Authorization: off
    - Standard Flow Enabled: off
    - Direct Access Grants Enabled: off
    - Service Accounts Enabled: on


## API Documentation

[Swagger API Documentation](http://localhost:8080/swagger-ui.html)


