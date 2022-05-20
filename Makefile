compile:
	mvn install -DskipTests

run:
	java -jar target/backend-0.0.1-SNAPSHOT.jar

test:
	mvn test
