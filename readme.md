## Project Name & Summary
*The UCD_2001 Air Quality Modeling Software*

A novel model to simulate air quality near roadways using java.

## Table of contents
* [Project Description](#project-description)
* [Getting Started](#getting-started)
* [Prerequisites](#prerequisites)
* [Installation](#installation)
* [Usage](#usage)
* [Screenshots](#screenshots)
* [License](#license)
* [Contact](#contact)
* [Resources & Acknowledgements](#Resources-&-Acknowledgements)

## Project Description

As part of my doctoral research to better predict air quality near roadways, 
I created new computational techniques to estimate pollutant transport 
that accounted for the effects of vehicles on a turbulent fluid flow. 
I implemented my new framework in java, which at the time 
was a comparatively new programming language and was still in version J2SE 1.3.

![image](graphics/shelterbelt_v03.jpg)

My model characterized vehicle emissions on roadways 
as an immense collection of point sources. 
Pollutant concentration fields could be determined by summing the contribution 
of each point source to receptor locations using the following equations:

![image](graphics/equations_v01.jpg)

The goal of the research was to distill complex physical processes into 
a simple computer model that could be used by a non-experts 
to accurately determine air quality near roadways. 
As such, I developed a simple GUI to allow users with little understanding 
of atmospheric physics to interact with the modeling software 
with a simple frontend using java swing (that had just been released) .

The documentation of this software's API is extensive and can be viewed 
[here](https://tonyserver.github.io/ucd2001/docs/).  
In addition, select code blocks are presented in this repository.

Interested readers can review publications associated with this model 
[here](https://tonyserver.github.io/ucd2001/tony_held_air_quality_model_publication.pdf). 
Masochists who would like to read a dissertation on 
computational fluid mechanics can read my thesis 
[here](https://tonyserver.github.io/ucd2001/tony_held_dissertation_2001_10_05.pdf).

## Getting Started

Usage
java -jar ucd_2001_gui.jar

To get a local copy up and running follow these simple steps.

### Prerequisites

This is an example of how to list things you need to use the software and how to install them.
* npm
  ```sh
  npm install npm@latest -g
  ```

### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/github_username/repo_name.git
   ```
2. Install Required packages
   ```sh
   npm install
   ```

## Usage

Use this space to show useful examples of how a project can be used. Additional screenshots, code examples and demos work well in this space. You may also link to more resources.

## Screenshots


## License

Distributed under the *** License. See `*** License Info ***` for more information.

## Contact

Tony Held - tony.held@gmail.com  
Project Link: [https://github.com/github_username/repo_name](https://github.com/github_username/repo_name)

## Resources & Acknowledgements

* []()
* []()
* []()