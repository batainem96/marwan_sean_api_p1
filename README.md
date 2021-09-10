# marwan-sean_api_p1

SignMeUp is a course registration application that allows for users to easily view and register for courses that have been added to the application. Users are able to create courses and upload them to the application so that they can be visible to other users, additional configurations such as course descriptions, start and end times, and max attendee limits can be specified as well. Users are able to register for open courses and view them in a convenient dashboard view that allows for them to explore the details of the registered course.

Features:

Users can register a new student account

Users can login to an existing account

Students can view their dashboard, which consists of course information for courses currently in the user's schedule

Students can delete a course from their schedule

Students can view the course catalog

Students can add a course to their schedule

Faculty can view a list of all users currently stored in the database

Faculty can view the course catalog

Faculty can create a new course and add it to the database

Faculty can edit existing course information

Faculty can delete a course from the database


Security is maintained through JWTs



Technologies Used:

Java, MongoDB, Maven, HTML, CSS, JavaScript, Bootstrap
AWS EC2, AWS S3, AWS CodeBuild, AWS CodePipeline, Elastic Beanstalk
REST, Postman, Agile-Scrum

Maven Dependencies:

  JavaxServlet - version 4.0.1
  jsonwebtoken - version 0.9.0
  Junit - version 4.13.12
  Mockito - version 3.11.12
  SLF4J - version 1.7.32
  Logback - version 1.2.5
  Jackson - version 2.10.0
  MongoDB Driver - version 4.2.3

We employed the DevOps strategy for this project, which meant that each push to our dev branch would send our code to an AWS pipeline, which hosted our API on an Elastic Beanstalk, and our UI on an S3 bucket configured for static web hosting.

Our UI is hosted at https://github.com/spdunn/marwan-sean_ui_p1.git
