# FlingAway
[핵심 문제해결 전략 첨부](./docs/flingAway.pdf)  
[ERD (파일 다운로드 후 브라우저 오픈)](./docs/erd.html)

# Spec
- JDK: 1.8
- Framework: Spring Boot
- DB: Postgres ( Embedded )
- LANG: Java

## Swagger API 테스트
| /    | URL                                              |
|:-----|:-------------------------------------------------|
| 로컬  | http://localhost:5000/swagger-ui.html            |

# How To
- DB Port
    - 62501 (application.properties 에서 변경)
- DML
    - src > resources > db > migration 에 위치
    - flyway 로 자동 수행
- Test
    - http 파일 통한 validation 검증: src > test > http 파일로 JetBrain 계열 IDE 에서 구동
    - jUnit 통한 DB 검증: src > test > FlingApplicationTest 구동
