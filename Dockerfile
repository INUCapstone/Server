# 1. 베이스 이미지 선택 (OpenJDK를 사용)
FROM openjdk:17-jdk-slim

# 2. JAR 파일의 위치와 이름 설정
ARG JAR_FILE=build/libs/taxi-0.0.1-SNAPSHOT.jar

# 3. JAR 파일을 컨테이너의 /app 디렉토리에 복사
COPY ${JAR_FILE} /taxi-app.jar

# 4. 애플리케이션을 실행하는 명령어 설정
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/taxi-app.jar" ]