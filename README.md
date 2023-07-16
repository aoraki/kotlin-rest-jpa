# Outstanding TODOS

1. Complete integration tests and configure a separate integration run type in gradle
2. Add a github actions file that will build and run the unit tests
3. Add jacoco code coverage report
4. Add openapi and swagger
5. Add a dockerfile and docker compose file
6. Put the schema control under flyway.  Export the schema of the DB to an SQL file and use that as the base of schema versions
7. Add terraform to build an ECS Cluster with Fargate and a database
8. Modify the github actions file to run terraform, run flyway, build an image and then deploy image to ECS cluster
