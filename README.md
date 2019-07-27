# CSYE 6225 - Spring 2019

## Team Information

| Name | NEU ID | Email Address |
| --- | --- | --- |
| Satish Kumar Anbalagan| 001351994| anbalagan.s@husky.neu.edu|
| Paavan Gopala Reddy| 001813403| gopalareddy.p@husky.neu.edu|
| HemalKumar Gadhiya|001460577 |gadhiya.h@husky.neu.edu|
| Akshay Murgod|001635872 |murgod.a@husky.neu.edu |

## Technology Stack
Springboot
Maven
MySQL
GitHub Account
Apache Tomcat
AWS - Cloud Formation
AWS - VPC
Amazon Route 53
Amazon S3 Bucket



## Build Instructions
WebApp --> Import the project using the existing maven project, and find the class having the main method to run the SpringBoot Application. And also before running the SpringBoot application, make sure the MySQL server is running. And open Postman to test all the applicable REST API calls.

Added Note folder inside webapp. Designed and implemented the following files:

a) Note.java
b) NoteDao.java
c) NoteRepository.java

Note is the model class which is basically a POJO file. It consists of notes format.

NoteDao is the service file which has all the method to create, update and delete the notes.

NoteRepository is an Interface where we extend the JPA Repository

Added Attachments folder inside WebApp. Designed and implemented the following files:

a) attachment.java
b) attachmentDao.java
c) attachmentRepository.java
d) metaData.java

Added fileStorage folder inside WebApp.Designed and implemented the following files:

a) DefaultFileStorageService.java
b) DevFileStorageService.java
c) FileStorageProperties.java
d) FileStorageService.java

AWS -->

a) i) Navigate to cloud formation folder and run the following scripts ./csye6225-aws-cf-create-stack.sh and ./csye6225-aws-cf-terminate-stack.sh to create and teardown the stack respectively.
a) ii) Navigate to cloud formation folder and run the following scripts ./csye6225-aws-cf-application-creat-stack.sh and ./csye6225-aws-cf-terminate-stack.sh to create\delete EC2 instance, create\delete DynamoDB table, create\delete RDS instance etc.

b) Navigate to scripts folder and run the following scripts ./csye6225-aws-networking-setup.sh and ./csye6225-aws-networking-teardown.sh to create and teardown the VPC respectively.

Added cloudwatchconfig.json in the project root directory.

Provided necessary policy to the cloudwatch agent.

Updated POM.xml with the latest dependency.

Designed and Implemented log4j2-spring.xml in the Resources folder.
Designed and Implemented MetricsConfig.java module in the SpringBoot Application.

-> As a user, we are now be able to request reset password link by calling /reset API endpoint
-> As a user, we have enabled the web application to send a message on password_reset SNS topic for the email service function to actually send email and track the active tokens in DynamoDB.

## Deploy Instructions

--> Code Deploy for AMI
--> Code Deploy for Lambda Function
--> Code Deploy for WebApp

## Running Tests
Check for valid email ID.
Check for stacks creation and deletion

## CI/CD
#Create the CI-CI stack
#Create the networking stack

Trigger the build for circleci using the curl command

#create the application Stack

Trigger the build for circleci using the curl command to deploy the code in centos.




