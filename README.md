Recruiting Test : Silanis Lottery
=================================

Requirements
------------

* maven (built using 3.6.1)
* jdk 15

Install from binaries
---------------------

Download the release zip archive and use the following command at the location of the archive.

```
$ unzip silanislottery-v%VERSION%-binaries.zip

Archive:  silanislottery-v%VERSION%-binaries.zip
   creating: silanislottery-v%VERSION%/
  inflating: silanislottery-v%VERSION%/LICENSE.md  
  inflating: silanislottery-v%VERSION%/README.md  
  inflating: silanislottery-v%VERSION%/silanislottery-%VERSION%-jar-with-dependencies.jar  

$ cd silanislottery-v%VERSION%

$ java -jar silanislottery-%VERSION%-jar-with-dependencies.jar

Welcome to Silanis Lottery!

[...]

Current pot: 200$ > 
```

Install from sources
--------------------

Use the following command at the root of the project folder to build and run the application.

```
$ mvn clean compile assembly:single

[INFO] Scanning for projects...
[INFO]                                                                         
[INFO] ------------------------------------------------------------------------
[INFO] Building Silanis Lottery %VERSION%
[INFO] ------------------------------------------------------------------------

[...]

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------

[...]

$ cd target

$ java -jar silanislottery-v%VERSION%-jar-with-dependencies.jar

Welcome to Silanis Lottery!

[...]

Current pot: 200$ > 
```

The first command will generate `target/silanislottery-VERSION-jar-with-dependencies.jar` with all dependencies.
The `java -jar` command will run the application: it opens a prompt where you input commands to manage the lottery.

Command line parameters:
------------------------

Get command line parameters information with the `-help` flag. 

```
$ java -jar silanislottery-%VERSION%-jar-with-dependencies.jar -help
```


Problem statement
-----------------

The Silanis lottery happens once a month. For each draw, Tommy takes out his old ball machine that contains 50 balls numbered from 1 to 50. After mixing the balls, 3 balls are drawn at random.  The first ball drawn wins 75%, the second wins 15%, and the third wins 10% of the available prize money. This available prize money corresponds to 50% of the total money in the pot at draw time.

To serve as an example, let’s say the pot contains 200$. If there were a draw now, the prizes would be given out as follows:

* 75$ for the first ball

* 15$ for the second ball

* 10$ for the third ball

To enter the next draw, you can purchase a ticket anytime at the price of 10$. Upon each draw, Tommy uses a new series of 50 tickets numbered the same way as the balls in the ball machine.

Tommy would like to enter the modern age and is asking you to write a software program to replace his old ball machine and tickets. Here are the requirements of this program:

* I want to buy a ticket for the draw by providing a first name. At purchase time, the number of the ball is displayed on the screen.

* I want to start a draw.

* I want to display the winning tickets as follows:


| 1st ball | 2nd ball | 3rd ball |
|----------|----------|----------|
| Dave: 75$|Remy: 15$ | Greg: 10$|

Tommy will be happy with a console application. But he’s asking that the three functionalities mentioned above be executed with the commands “purchase”, “draw” and “winners”.

Upon launching the program, you can initialize the pot with 200$. The program does not need to store information on disk nor in a database. You can round or truncate amounts to the nearest dollar value.
