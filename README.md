[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)
[![Join Discord](https://img.shields.io/discord/595582070596698116?color=738ADB&label=Discord)](https://discord.gg/XYYuKn2)
[![Build Status](https://travis-ci.org/rsbox/rsbox.svg?branch=master)](https://travis-ci.org/rsbox/rsbox)
[![codecov](https://codecov.io/gh/rsbox/rsbox/branch/master/graph/badge.svg)](https://codecov.io/gh/rsbox/rsbox)
[![codebeat badge](https://codebeat.co/badges/16a2c791-bd07-41e6-9fc1-10c0c644f160)](https://codebeat.co/projects/github-com-rsbox-rsbox-master)
![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/rsbox/rsbox?include_prereleases)
[![RSBox License](https://img.shields.io/github/license/rsbox/rsbox)](https://github.com/rsbox/rsbox/blob/master/LICENSE)



<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/rsbox/rsbox">
    <img src="https://i.imgur.com/yufiGp7.pngg" alt="RSBox" width="80" height="80">
  </a>

  <h3 align="center">RSBox</h3>

  <p align="center">
    An Open-Source Oldschool Runescape Private Server
    <br />
    <a href="https://rsbox.io"><strong>Visit Our Forum »</strong></a>
    <br />
    <br />
    <a href="#">Explore the docs</a>
    ·
    <a href="#">Report Bug</a>
    ·
    <a href="#">Request Feature</a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
## Table of Contents

* [About the Project](#about-the-project)
  * [Built With](#built-with)
* [Quick Start](#quick-start)
* [Roadmap](#roadmap)
* [Contributing](#contributing)
* [License](#license)
* [Contact](#contact)
* [Acknowledgements](#acknowledgements)



<!-- ABOUT THE PROJECT -->
## About The Project

RSBox is an Open-Source Runescape Private Server running the latest OSRS revision. It aims to provide a very simple and documented way to build your own custom content in OSRS using our documented API.

Here's What RSBox Offers:
* Streamlined plugin system with Bukkit style API design. The API layer stits on top of the engine so no matter what OSRS revision you use, plugins should not break.
* Drag and drop plugin installation through compiled JAR files.
* No development knowledge needed for server setup. If you just want to run your own RSPS server, we offer download and run solutions to get your server running quickly.

Of course, this is an ongoing long-term project run primarily by our community on discord. If you are at all interested it playing with RSBox, please join our discord by clicking the discord badge at the top.

A list of core contributions can be found in the acknowledgements section at the bottom.

### Built With
This project relies on a couple of core software and libraries. They are listed below.
* [Gradle](https://gradle.com/)
* [Java](https://www.java.com/en/)
* [Kotlin](https://kotlinlang.org/)

<!-- QUICK START -->
## Quick Start

You can setup RSBox two different ways, either in an IDE if you plan on adding content.
Or you can download the server JAR executable from the releases page.<br>
Below is a quick guide on how to setup the project both ways. You can always find more information on the Wiki or by joining our discord server.<br>
### Server Executable Setup

1. Download the latest *rsbox-server-<version>.jar* from the releases page. You can click on the badge above for quick navigation.<br>
2. Before you can start your server for the first time, you must run the setup wizard. This downloads everything needed for RSBox to function properly.
<br><br>
<code>
java -Xmx1G -Xms1G -jar rsbox-server-0.1.jar --setup
</code><br><br>

<img src="https://i.gyazo.com/6d37aec99533683833b591264cb3fbcf.gif"><br><br>
After the setup wizard completes, You may start your server with the following command.<br><br>
<code>
java -Xmx1G -Xms1G -jar rsbox-server-0.1.jar
</code>
<br><br>

### Server IDE Setup
1. Clone the repository into a directory.<br><br>
<code>
git clone https://github.com/rsbox/rsbox
</code><br><br>
To run the setup wizard within an IDE, run the gradle setup task.<br>
<img src="https://i.imgur.com/Xi5f2En.png">
<br>
<br>
You can now start the server by just executing `gradle run`.



<!-- ROADMAP -->
## Roadmap

See the [open issues](#) for a list of proposed features (and known issues).



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request



<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE` for more information.



<!-- CONTACT -->
## Contact

Kyle Escobar - [@KyleEscobar](#)

Project Link: [https://github.com/rsbox/rsbox](https://github.com/rsbox/rsbox)



<!-- ACKNOWLEDGEMENTS -->
## Acknowledgements
