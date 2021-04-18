# JarBundler

JarBundler is a feature-rich Ant task which will create a macOS application bundle from a list of Jar files and a main class name.

You can add an icon resource, set various macOS native look-and-feel bells and whistles, and maintain your application bundles as part of your normal build and release cycle.

Project moved from http://sourceforge.net/projects/jarbundler/


# Releases

## Maven

Add the following dependency to your `pom.xml` file:
```xml
<dependency>
    <groupId>com.ultramixer.jarbundler</groupId>
    <artifactId>jarbundler-core</artifactId>
    <version>3.3.0</version>
</dependency>
```

## Download

Check the [Releases page](https://github.com/UltraMixer/JarBundler/releases) to get the latest distribution.


# Documentation

Take a look at [./docs/index.html](http://htmlpreview.github.io/?https://github.com/UltraMixer/JarBundler/blob/master/docs/index.html) *(Currently outdated. Sorry.)*


# Build

Run `mvn clean install` to build the project and push the JAR artifacts to your local maven repo. You can then use the SNAPSHOT version in your project.

To skip the GPG signing process (for maven central release) add `-Dgpg.skip` to your commandline.


# ChangeLog

## Version 3.4.0 (Work In Progress)

* Massive code refactoring
  * code formatting
  * specify maven source encoding
  * fix Ant build files
  * run examples with a modern JavaApplicationStub ([universalJavaApplicationStub](https://github.com/tofi86/universalJavaApplicationStub))
* Updates to docs and examples
* Optional `supportsAutomaticGraphicsSwitching` attribute which defaults to `true` *(for Plist key `NSSupportsAutomaticGraphicsSwitching`)*
  * PullRequest !14, thanks to Björn Kautler (@Vampire) for his contribution!
* Update Ant from 1.9.3 to 1.9.14
* Bugfix: JarBundler on Windows created a classpath with backward slashes which broke the Info.plist file on macOS
  * `\` in classpath are now replaced with `/`
* Remove support for Java 1.3
  * if no `jvmversion` attribute is set, the default version is now `1.4+`
  * removed deprecated `growboxintrudes` attribute
  * removed deprecated `liveresize` attribute
  * removed deprecated `smalltabs` attribute

## Version 3.3.0 (2015-11-09)

* Merged changes from [tofi86/Jarbundler](https://github.com/tofi86/Jarbundler/) into official release
  * optional `contentSize` attribute *(for Plist key `NSPreferencesContentSize`)*
  * optional `useJavaXKey` attribute *(for [universalJavaApplicationStub](https://github.com/tofi86/universalJavaApplicationStub) support)*
  * optional `allowmixedlocalizations` attribute *(for Plist key `CFBundleAllowMixedLocalizations`)*
  * optional `copyright` attribute *(for Plist key `NSHumanReadableCopyright`)*
  * removed deprecated `aboutmenuname` attribute *(use `shortname` attribute instead)*
  * removed deprecated `infostring` and `shortinfostring` attributes *(use `copyright` attribute instead)*

## Version 3.2.0 (2015-08-05)

* optional `highResolutionCapable` attribute (for Plist key `NSHighResolutionCapable`)
* optional `LSApplicationCategoryType` attribute (for Plist key `LSApplicationCategoryType`)
* optional `SUPublicDSAKeyFile` attribute (for Plist key `SUPublicDSAKeyFile`)

## Previous history

Take a look at [./docs/index.html](http://htmlpreview.github.io/?https://github.com/UltraMixer/JarBundler/blob/master/docs/index.html)


# License

Licensed under `Apache License v2.0`.
