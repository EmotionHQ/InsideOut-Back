
FROM eclipse-temurin:17-jdk-alpine

# 빌드된 JAR 파일을 컨테이너로 복사
COPY build/libs/Insideout-0.0.1-SNAPSHOT.jar app.jar

# 포트 노출
EXPOSE 8080


# Step 4: 컨테이너 실행 시 실행할 명령어 지정
CMD ["java", "-jar", "app.jar"]