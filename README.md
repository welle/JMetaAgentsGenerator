# JMetaAgentsGenerator [![Build Status](https://travis-ci.org/welle/JMetaAgentsGenerator.svg?branch=master)](https://travis-ci.org/welle/JMetaAgentsGenerator) [![Quality Gate](https://sonarcloud.io/api/badges/gate?key=aka.jmetaagentsgenerator:JMetaAgentsGenerator)](https://sonarcloud.io/dashboard/index/aka.jmetaagentsgenerator:JMetaAgentsGenerator) #

## Quick summary ##

JMetaAgentsGenerator will create java pojo from JSON string from TVDB, TMDB, etc

### Dependencies for the generated test units:

```xml
<!-- https://mvnrepository.com/artifact/org.mockito/mockito-all -->
<dependency>
	<groupId>org.mockito</groupId>
	<artifactId>mockito-all</artifactId>
	<version>1.10.19</version>
	<scope>test</scope>
</dependency>
<!-- https://mvnrepository.com/artifact/org.powermock/powermock-mockito-release-full -->
<dependency>
	<groupId>org.powermock</groupId>
	<artifactId>powermock-mockito-release-full</artifactId>
	<version>1.6.4</version>
	<type>pom</type>
	<scope>test</scope>
</dependency>
```

### How to use it ###

See examples in unit tests.

### Version

Go to [my maven repository](https://github.com/welle/maven-repository) to get the latest version.

## Notes
Need the eclipse-external-annotations-m2e-plugin: 

p2 update site to install this from: http://www.lastnpe.org/eclipse-external-annotations-m2e-plugin-p2-site/ (The 404 is normal, just because there is no index.html; it will work in Eclipse.)