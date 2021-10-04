# classplanter
A tool which allows to create PlantUML class diagramms for whole projects. Classes to include could be filtered by different criteria.


## How Will It Work?

The application reads Java code files from a specified project path and filters the classes to show in the PlantUML
class diagram by a filter configuration. The filtered classes will be passed to a writer which creates the PlantUML
file.


## What Does It Manage?

The application is generally able to create a class diagram for a java source path:

* Reads classes.
* Respects super classes and implemented interfaces.
* Creates associations to other classes via attribute references.
* Identifies many to one relations (of type "List", "Set" and "Stack").


## What Does It NOT Manage?

* Lists of lists, sets of sets and so on.


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

### Source File Folder "-sf {folder name}"

This option allows to define the folder whose JAVA files are to read for the diagram.

### Target File Name "-tf {file name}"

Set the target file name via this option. If not set, a file "result.plantuml" in the current folder will be created.



## How to Configure

The output can be configured by setting properties.

### Having Packages in the Diagram

To have information about the packages in the diagram, set a package mode:

```
property name: classplanter.output.packagemode
values: FLAT, NONE
```

**FLAT**: creates a box for every package (also if one package is included in another one.

**NONE**: ignores packages completely in the diagram.

### Having Explicitly Included Packages

Setting this property limits included classes to those contained in the passed package(s). It is possible to configure several package names comma separated. Only full matching packages will be respected.

```
property name: classplanter.input.include.packages
values: PackageName[,PackageName]
```
