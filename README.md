# JarBundler
JarBundler is a feature-rich Ant task which will create a Mac OS X application bundle from a list of Jar files and a main class name. You can add an Icon resource, set various Mac OS X native look-and-feel bells and whistles, and maintain your application bundles as part of your normal build and release cycle.

Project moved from http://sourceforge.net/projects/jarbundler/

# Maven
```xml
<dependency>
    <groupId>com.ultramixer.jarbundler</groupId>
    <artifactId>jarbundler-core</artifactId>
    <version>3.2.2</version>
</dependency>
```


# Documentation
Take a look at ./dox/index.html


# ChangeLog

* **version 3.3.0** (2015-11-09)
* Merged changes from https://github.com/tofi86/Jarbundler/blob/ into official release
* Added optional `contentSize` attribute to JarBundler task (for Plist key `NSPreferencesContentSize`)
* Added optional `useJavaXKey` attribute to JarBundler task (for [universalJavaApplicationStub](https://github.com/tofi86/universalJavaApplicationStub)-Support)
* removed deprecated `aboutmenuname` attribute
* removed deprecated `infostring` and `shortinfostring` attributes (use `copyright` attribute instead)


# License
Licensed under `Apache License v2.0`