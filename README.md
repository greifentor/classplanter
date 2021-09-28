# classplanter
A tool which allows to create PlantUML class diagramms for whole projects. Classes to include could be filtered by different criteria.


## How Will It Work?

The application reads Java code files from a specified project path and filters the classes to show in the PlantUML
class diagram by a filter configuration. The filtered classes will be passed to a writer which creates the PlantUML
file.


## How to Compile?

### Requirements

**Java:** 11+

**Maven:** 3.5+

### Compile

```bash
mvn clean install
```