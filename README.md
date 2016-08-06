# air-reservations
A reservation application for air travels

To install the application only:

- Install java 8 from http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
- Install the last maven version from https://maven.apache.org/download.cgi

To start the webapp:

- By the command shell go to the project folder air-reservations
- Digit from your shell the command:
     
       mvn jetty:run-war

  Automatically the unit cases start and after a local mail server available on the 25000 port used to send the notifications and in the end the web server.
  
To navigate in the webapp:

- Open the browser and go to: http://localhost:8080
- Login. Are available three accounts:

       gonzo/gonzo - Is the user of the traveller searching new travels
       
       fozzie/fozzie - The user of the staff operator that can monitor the operations of the travellers
       
       kermit/kermit - The administrator. To use only for system problems 
     
To start only the unit test cases digit: 
 
       mvn test
       
  Three test cases are availables:
  
  - ReservationsTest: to test the choice of the flight, the checkouts, the refunds for travellers and staff
  
  - SchedulerTest: to test the operations started by the scheduler
  
  - PDFTest: to test the creation of the billing passes and ticket receipts
  
  Tests can be started one to one too with the command, for example in the case of reservations test:
     
    mvn test -DReservationsTest

To change the configuration of the mail server to point to a different server:

- Go in the file engine.properties. It contains three parameters for the mail server:

  engine.email.enabled - To activate or deactivate the mail server
  
  engine.email.host - The hostname or the address of the mail server
  
  engine.email.port - The SMTP port of the mail server   
  
To generate the documentation digit:

    mvn site
    
  It will create the javadocs and the build references. To see the docs open the file index.html in the generated folder air-reservations/target/site