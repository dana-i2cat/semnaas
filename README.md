# SemNaaS (Semantic Network as a Service) system

## Description

SemNaaS is a Semantic Web based approach for developing and supporting operations of NaaS systems. SemNaaS can be used by any NaaS provider but we have tested it with with the existing OpenNaaS framework. Furthermore, SemNaaS exposes its functionality as set of APIs and RESTful web services, which OpenNaaS consumes.

SemNaaS aims to improve OpenNaaS using Semantic Web technologies. It has several potential improvement aspects on OpenNaaS:
	- Support of network request validation and network connectivity check;
	- System monitoring and failure condition detection;
	- Capability of constructing distributed and interconnected OpenNaaS instances;
	- Complex status report generation.

## Prerequisites 

	1- JDK 7.0.
	2- Maven 2 or later.
	3- Apache Tomcat 7.0 or higher.
	4- VAADIN 7.3 or higher.

## Execution

SemNaaS is divided into 3 main subprojects:

	1- validation: which does the actual NaaS request validation. It performs 2 levels of validation namely request validation, and connectivity check.
	2- RequestValidationREST: provides the validation funcaionality in the form of a RESTful service, via which any application built on top of SemNaaS can validate its request before proceeding with the actual provisioing process.
	3- RequestValidatorWeb: is a VAADIN-based Web application, which constitutes the GUI interface of SemNaaS.


All applications are Maven-based, so in order to build any of them the user can use "mvn install" command.
With regard to application "RequestValidatorWeb", after running the command, a WAR file is generated which should be deployed into your Apache Tomcat "webapps" dierctory.

## Contact

Mohamed Morsey
Department of Computer Science
University of Amsterdam
m.morsey@uva.nl

## Licensing

THIS PROGRAM IS DISTRIBUTED IN THE HOPE THAT IT WILL BE USEFUL, BUT WITHOUT ANY WARRANTY. IT IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED.

#### Enhancing the library

 1. Checkout the code
 2. Run "mvn clean compile" to auto generate binding files
 3. Open with IDE (e.g. Eclipse or IntelliJ)
