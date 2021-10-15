# classplanter
A tool which allows to create PlantUML class diagramms for whole projects. Classes to include could be filtered by different criteria.


## How Does It Work?

The application reads Java code files from a specified project path and filters the classes to show in the PlantUML
class diagram by a filter configuration. The filtered classes will be passed to a writer which creates the PlantUML
file.


## What Does It Manage?

The application is generally able to create a class diagram for a java source path:

* Reads classes.
* Reads enums.
* Reads interfaces.
* Respects super classes and implemented interfaces.
* Creates associations to other classes via attribute references.
* Identifies many to one relations (of type "List", "Set" and "Stack").
* Identifies utility classes and sets a stereotype "<< utility class >>" for these classes.


## What Does It NOT Manage?

* Lists of lists, sets of sets and so on.
* Member classes.


## How to Compile?

### Requirements

**Java:** 11+

**Maven:** 3.5+

### Compile

```bash
mvn clean install
```


## How to Run?

After building the project there should be a JAR file in the projects target folder.

This could be started by ``java -jar {JAR file name}`` with the parameters as described below:

### Memory

Setting ``-Xmx4g`` option to enhance memory is recommended.

### Source File Folder "-cnf {configuration file name}"

Allows to read a YAML configuration file. The values of the file do override parameters set by properties.

### Source File Folders "-sf {folder name [, folder name]}"

This option allows to define the folders whose JAVA files are to read for the diagram. The folder names are comma
separated.

### Target File Name "-tf {file name}"

Set the target file name via this option. If not set, a file "result.plantuml" in the current folder will be created.



## How to Configure?

The output can be configured by setting properties.

### Having Packages in the Diagram

To have information about the packages in the diagram, set a package mode:

Properties file:

```
property name: classplanter.output.packageMode
values: FLAT | NONE
```
YAML:

```
output:
  packageMode: FLAT | NONE
```

**FLAT**: creates a box for every package (also if one package is included in another one.

**NONE**: ignores packages completely in the diagram.

classplanter.input.includeClasses

### Having Explicitly Included Classes

Setting this property limits included classes to have matching (qualified) names. It is possible to configure several 
class names comma separated. All (qualified) class names which are ending to the configured values will handled as 
matching.

Properties file:

```
property name: classplanter.input.includeClasses
values: ClassName[,ClassName]
```
YAML:

```
input:
  explicitClasses:
    - (QUALIFIED) CLASS NAME 1
    - (QUALIFIED) CLASS NAME 2
    ...
```

Class names could be qualified. 

### Having Explicitly Included Packages

Setting this property limits included classes to those contained in the passed package(s). It is possible to configure several package names comma separated. Only full matching packages will be respected.

Properties file:

```
property name: classplanter.input.includePackages
values: PackageName[,PackageName]
```
YAML:

```
input:
  explicitPackages:
    - PACKAGE NAME 1
    - PACKAGE NAME 2
    ...
```

### Show Members

If the class members should be shown in the diagram, set the property as listed below to "true".
S
Properties file:

```
property name: classplanter.output.showMembers
values: "true" | "false"
```
YAML:

```
output:
  showMembers: true | false
```

Setting this option will suspend the option to unite equal associations.

### Unite Equal Associations

To unite equal associations set the property listed below to "true".

Properties file:

```
property name: classplanter.output.uniteEqualAssociations
values: "true" | "false"
```
YAML:

```
output:
  uniteEqualAssociations: true | false
```