# Jumo Loans

## Task
Given the below CSV from our accounting department calculate the
aggregate loans by the tuple of (Network, Product, Month) with the
total currency amounts and counts and output into a file CSV file
Output.csv

## Result
"tmp" folder is created inside project root folder. Output file "Output.csv"
is generated and save inside "tmp" folder.

## Development

Used the following dependencies for development to build and run:

1. Node.js: Using Node to run a development web server and build the project. (Please use the latest LTS version to be compatible with JHipster)
2. Yarn: Using Yarn to manage Node dependencies.
3. Gulp: Using Gulp as build system.
4. Bower: Using Bower to manage CSS and JavaScript dependencies.

## Production

1. To optimize the loans application for production, run:

    ./mvnw -Pprod clean package

2. Ensure everything worked:

    java -jar target/*.war
    
Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

3. Can be deployed to the cloud for production using the following command:

    yo jhipster:heroku
    
and then run:

    heroku open

## Testing

Application tests:

    ./mvnw clean test

## Technologies Used

Java 8, 
jHipster, 
Git, 
Node.js, 
Yarn, 
Yeoman, 
Bower, 
AngularJS, 
jQuery, 
Spring, 
SpringBoot, 
Maven, 
Heroku

I chose Java as my language as I'm confident in using Java and its my main development language.
Along with Java I used Spring, SpringSecurity, SpringBoot and Maven as I am also confident with it and have used it for
a significant time now.

I am reasonably confident with the use of AngularJS as front-end and have been using it for about 2 years now.

I have not had much experience in Node, Yarn, Yeoman and Bower however, the reason for using it is also to show that
I am willing and keen to learn new technologies, etc and also to express that I am able to pick new things up and learn swiftly.
