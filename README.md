<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/rooneyshuman/SFTP-App">
    <img src="images/logo.png" alt="Logo" width="20%" height="20%">
  </a>

  <h3 align="center">SFTP Client</h3>

  <p align="center">
    Transfer files securely to and from a remote computer.
    <br />
    <a href="https://github.com/rooneyshuman/SFTP-App"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/rooneyshuman/SFTP-App">View Demo</a>
    ·
    <a href="https://github.com/rooneyshuman/SFTP-App/issues">Report Bug</a>
    ·
    <a href="https://github.com/rooneyshuman/SFTP-App/issues">Request Feature</a>
  </p>
</p>

<!-- TABLE OF CONTENTS -->
## Table of Contents

* [About the Project](#about-the-project)
  * [Built With](#built-with)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Usage](#usage)
* [Testing](#testing)
* [Contributing](#contributing)
* [Contributors](#contributors)
* [License](#license)
* [Acknowledgements](#acknowledgements)

## About the Project

This SFTP Client was originally created by a team of students in order to learn agile practices while developing a product. The result is a simple, menu-based command line program that walks the user through a secure transfer of files to and from a remote computer. 

### Built With

* [Java SE 10](http://www.oracle.com/technetwork/java/javase/downloads/jdk10-downloads-4416644.html)
* [JSch](http://www.jcraft.com/jsch/)
* [JUnit](https://github.com/junit-team/junit4/blob/master/doc/ReleaseNotes4.12.md)
* [Hamcrest](http://hamcrest.org/JavaHamcrest/)

## Getting Started

Folow these instructions to get a local copy of this application up and running.

### Prerequisites

### Installation

1. Clone the repo

```
git clone https://github.com/rooneyshuman/SFTP-App.git
```

2. Build the project
```
mvn package
```

3. Run the project in an IDE
```
Right-click Main.java -> Run 'Main.main()'
```

### Usage
The application will prompt the user to establish an SFTP connection or exit the program. To establish a connection, the program will prompt the user for the following information.

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

## Testing

All unit tests are located in the [test](src/test/java/) folder and require the login variables to be updated to credentials for a valid SFTP connection.

## Contributing
Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**. 

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a pull request

## Contributors

* [Belén](https://github.com/rooneyshuman/) 
* [Carissa](https://github.com/carissaallen)  
* [Bassel](https://github.com/bahamieh)
* Brent
* Mack 
* Marcus
* Dante

## License

Distributed under the MIT License. See [LICENSE.md](https://github.com/rooneyshuman/SFTP-App/blob/master/LICENSE) for more information.

## Acknowledgements
* [README Inspiration](https://github.com/carissaallen/Best-README-Template)
