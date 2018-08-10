# SFTP Client 

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

Below are the libraries, frameworks, and/or plug-ins used to develop the SFTP Client application in IntelliJ.
* [Java SE 10](http://www.oracle.com/technetwork/java/javase/downloads/jdk10-downloads-4416644.html)
* [JSch](http://www.jcraft.com/jsch/)
* [JUnit](https://github.com/junit-team/junit4/blob/master/doc/ReleaseNotes4.12.md)
* [Hamcrest](http://hamcrest.org/JavaHamcrest/)

### Installing

Git clone the project using HTTPS: 

```
https://github.com/bamcmanus/AGILE.git
```

Open the project in IntelliJ IDEA using Jave SE 10.

## Testing

All unit tests are located in the [test](src/test/java/) folder and require the login variables to be updated to credentials for a valid SFTP connection.

## Deployment

In order to run the SFTP client on your local computer you must add the prerequisites and do the following:

```
1. Right-click Main.java -> Run 'Main.main()'
```

This will prompt the user to establish an SFTP connection or exit the program. To establish a connection, the program will prompt the user for the following information.

```
Enter your username:
exampleUser
```
```
Enter your password:
strongPassword
```
```
Enter your hostname:
linux.cs.pdx.edu
```
Once a connection is established, the user is provided a menu to securely manage files.

## Brought To You By
|   Name    |    @mention   |
|-----------|---------------|
|   Belén   | @rooneyshuman |
|   Brent   | @bamcmanus    |
|  Carissa  | @carissaallen |
|   Dante   | @Liriaene     |
|   Mack    | @mackkcooper  |
|  Marcus   | @pdxbigman    |

## License

This project is licensed under the MIT License - see the [LICENSE.md](https://github.com/bamcmanus/AGILE/blob/master/LICENSE) file for details
