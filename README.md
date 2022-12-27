# RMT Auction

This application was generated using JHipster 7.1.0, you can find documentation and help at [https://www.jhipster.tech/documentation-archive/v7.1.0](https://www.jhipster.tech/documentation-archive/v7.1.0).

## Getting Started
## Building for development environment

### Docker:
1. Requirements:
* Docker >= 17.12

2. Run following command:
```
docker-compose up -d --build
```

### Manual:
1. Requirements:
* JDK 11.0.13

2. Setting system environment
* Example:
```aidl
export DB_HOST="localhost"
export DB_PORT="3306"
export DB_SCHEMA="rmt-auction"
export DB_USER="root"
export DB_PASS="r00t"
```

3. Run following command:
```
 ./gradlew
```

### Packaging as jar

To build the final jar and optimize the RMT Auction application for production, run:

```
./gradlew -Pprod clean bootJar
```

Start jar:

```
java -jar build/libs/*.jar
```

Then navigate to [http://localhost:8086](http://localhost:8086) in your browser.

Refer to [Using JHipster in production][] for more details.

## Testing

To launch your application's tests, run:

```
./gradlew clean check
```

## CI/CD (TODO)
