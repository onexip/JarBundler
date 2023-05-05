/*
 * Copyright (c) 2015, UltraMixer Digital Audio Solutions <info@ultramixer.com>, Seth J. Morabito <sethm@loomcom.com>
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.ultramixer.jarbundler;


import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.FileScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Chmod;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.util.FileUtils;

import java.io.*;
import java.util.*;


/**
 * <p>
 * An ant task which creates a macOS Application Bundle for a Java
 * application.
 * </p>
 *
 * <dl>
 * <dt>dir</dt>
 * <dd>The directory into which to put the new application bundle.</dd>
 * <dt>name</dt>
 * <dd>The name of the application bundle. Note that the maximum length of this
 * name is 16 characters, and it will be silently cropped if it is longer than
 * this.</dd>
 * <dt>mainclass</dt>
 * <dd>The main Java class to call when running the application.</dd>
 * </dl>
 *
 * <p>
 * One of the following three MUST be used:
 *
 * <ol>
 * <li>jars Space or comma-separated list of JAR files to include.; OR</li>
 * <li>One or more nested &lt;jarfileset&gt;s. These are normal ANT FileSets;
 * OR </li>
 * <li>One or more nested &lt;jarfilelist&gt;s. These are standard ANT
 * FileLists. </li>
 * </ol>
 *
 * <p>
 * Optional attributes:
 *
 * <p>
 * The following attributes are not required, but you can use them to override
 * default behavior.
 *
 * <dl>
 * <dt>verbose
 * <dd>If true, show more verbose output while running the task
 *
 * <dt>version
 * <dd>Version information about your application (e.g., "1.0")
 * </dl>
 * <p>
 * These attributes control the fine-tuning of the "macOS" look and feel.
 *
 * <dl>
 * <dt>arguments
 * <dd>Command line arguments. (no default)
 *
 * <dt>antialiasedgraphics
 * <dd>Use anti-aliased graphics (default "false")
 *
 * <dt>antialiasedtext
 * <dd>Use anti-aliased text (default "false")
 *
 * <dt>bundleid
 * <dd>Unique identifier for this bundle, in the form of a Java package. No
 * default.
 *
 * <dt>buildnumber
 * <dd>Unique identifier for this build
 *
 * <dt>developmentregion
 * <dd>Development Region. Default "English".
 *
 * <dt>execs
 * <dd>Files to be copied into "Resources/MacOS" and made executable
 *
 * <dt>growbox
 * <dd>Show growbox (default "true")
 *
 * <dt>screenmenu
 * <dd>Put swing menu into macOS menu bar.
 *
 * <dt>type
 * <dd>Bundle type (default "APPL")
 *
 * <dt>signature
 * <dd>Bundle Signature (default "????")
 *
 * <dt>stubfile
 * <dd>The Java Application Stub file to copy for your application (default
 * MacOS system stub file)
 * </dl>
 *
 * <p>
 * Rarely used optional attributes.
 * <dl>
 * <dt>chmod
 * <dd>Full path to the chmod command. This almost certainly does NOT need to
 * be set.
 * </dl>
 *
 * <p>
 * The task also supports nested &lt;execfileset&gt; and/or &lt;execfilelist&gt;
 * elements, and &lt;resourcefileset&gt; and/or &lt;resourcefilelist&gt;
 * elements, which are standard Ant FileSet and FileList elements. In the first
 * case, the referenced files are copied to the <code>Contents/MacOS</code>
 * directory and made executable, and in the second they are copied to the
 * <code>Contents/Resources</code> directory and not made executable. If you
 * winrces, note that in fact the files are installed in locations which have
 * the same relation to the <code>Contents/Resources</code> directory as the
 * files in the FileSet or FileList have to the 'dir' attribute. Thus in the
 * case:
 * </p>
 *
 * <pre>
 *   &lt;resourcefileset dir=&quot;builddir/architectures&quot;
 *                       includes=&quot;ppc/*.jnilib&quot;/&gt;
 * </pre>
 *
 * <p>
 * the <code>*.jnilib</code> files will be installed in
 * <code>Contents/Resources/ppc</code>.
 * </p>
 *
 * <p>
 * The task supports a nested &lt;javaproperty&gt; element, which allows you to
 * specify further properties which are set for the JVM when the application is
 * launched. This takes a required <code>key</code> attribute, giving the
 * property key, plus an attribute giving the property value, which may be one
 * of <code>value</code>, giving the string value of the property,
 * <code>file</code>, setting the value of the property to be the absolute
 * path of the given file, or <code>path</code>, which sets the value to the
 * given path. If you are setting paths here, recall that, within the bundle,
 * <code>$APP_PACKAGE</code> is set to the root directory of the bundle (ie,
 * the path to the <code>foo.app</code> directory), and <code>$JAVAROOT</code>
 * to the directory <code>Contents/Resources/Java</code>.
 * </p>
 *
 * <p>
 * Minimum example:
 * </p>
 *
 * <pre>
 *    &lt;jarbundler dir=&quot;release&quot; name=&quot;Bar Project&quot; mainclass=&quot;org.bar.Main&quot;
 *        jars=&quot;bin/Bar.jar&quot; /&gt;
 * </pre>
 *
 * <p>
 * Using Filesets
 * </p>
 *
 * <pre>
 *    &lt;jarbundler dir=&quot;release&quot; name=&quot;Bar Project&quot; mainclass=&quot;org.bar.Main&quot;&gt;
 *      &lt;jarfileset dir=&quot;bin&quot;&gt;
 *        &lt;include name=&quot;*.jar&quot; /&gt;
 *        &lt;exclude name=&quot;test.jar&quot; /&gt;
 *      &lt;/jarfileset&gt;
 *      &lt;execfileset dir=&quot;execs&quot;&gt;
 *        &lt;include name=&quot;**&quot; /&gt;
 *      &lt;/execfileset&gt;
 *    &lt;/jarbundler&gt;
 * </pre>
 *
 * <p>
 * Much Longer example:
 * </p>
 *
 * <pre>
 *    &lt;jarbundler dir=&quot;release&quot;
 *                name=&quot;Foo Project&quot;
 *                mainclass=&quot;org.bar.Main&quot;
 *                version=&quot;1.0 b 1&quot;
 *                copyright=&quot;Foo Project (c) 2002&quot;
 *                type=&quot;APPL&quot;
 *                jars=&quot;bin/foo.jar bin/bar.jar&quot;
 *                execs=&quot;exec/foobar&quot;
 *                signature=&quot;????&quot;
 *                workingdirectory=&quot;temp&quot;
 *                icon=&quot;resources/foo.icns&quot;
 *                jvmversion=&quot;1.4.1+&quot;
 *                vmoptions=&quot;-Xmx256m&quot;/&gt;
 * </pre>
 * <p>
 * http://developer.apple.com/documentation/MacOSX/Conceptual/BPRuntimeConfig/
 */
public class JarBundler extends MatchingTask {

    private static final String DEFAULT_STUB = "/System/Library/Frameworks/JavaVM.framework/Versions/Current/Resources/MacOS/JavaApplicationStub";

    private static final Set menuItems = new HashSet();
    private File mAppIcon;

    private File mRootDir;

    private final List mJavaFileLists = new ArrayList();
    private final List mJarFileSets = new ArrayList();

    private final List mExecFileLists = new ArrayList();
    private final List mExecFileSets = new ArrayList();

    private final List mResourceFileLists = new ArrayList();
    private final List mResourceFileSets = new ArrayList();

    private final List mJarFileLists = new ArrayList();
    private final List mJavaFileSets = new ArrayList();

    private final List mExtraClassPathFileLists = new ArrayList();
    private final List mExtraClassPathFileSets = new ArrayList();

    private final List mJarAttrs = new ArrayList();

    private final List mExecAttrs = new ArrayList();

    private final List mExtraClassPathAttrs = new ArrayList();

    private final List mHelpBooks = new ArrayList();

    private boolean mVerbose = false;
    private boolean mShowPlist = false;

    // Java properties used by macOS Java applications

    private File mStubFile = new File(DEFAULT_STUB);

    private Boolean mAntiAliasedGraphics = null;

    private Boolean mAntiAliasedText = null;

    private Boolean mScreenMenuBar = null;

    private Boolean mGrowbox = null;

    // The root of the application bundle
    private File bundleDir;

    // "Contents" directory
    private File mContentsDir;

    // "Contents/MacOS" directory
    private File mMacOsDir;

    // "Contents/Resources" directory
    private File mResourcesDir;

    // "Contents/Resources/Java" directory
    private File mJavaDir;

    private AppBundleProperties bundleProperties = new AppBundleProperties();

    // Ant file utilities
    private FileUtils mFileUtils = FileUtils.getFileUtils();


    /***************************************************************************
     * Set task attributes
     **************************************************************************/

    /**
     * Arguments to the application
     *
     * @param s The arguments to pass to the application being launched.
     */
    public void setArguments(String s) {
        bundleProperties.setArguments(s);
    }

    /**
     * Override the stub file path to build on non-MacOS platforms
     *
     * @param file The path to the stub file
     */
    public void setStubFile(File file) {
        mStubFile = (file.exists()) ? file : new File(DEFAULT_STUB);
        bundleProperties.setCFBundleExecutable(file.getName());
    }

    /**
     * Setter for the "dir" attribute (required)
     *
     * @param f The directory in which your application bundle will be created.
     */
    public void setDir(File f) {
        mRootDir = f;
    }

    /**
     * <p>Setter for the "name" attribute (required)</p>
     * <p>This attribute names the
     * output application bundle and asks as the CFBundleName if 'bundlename' is
     * not specified</p>
     *
     * @param s The name of your application bundle
     */
    public void setName(String s) {
        bundleProperties.setApplicationName(s);
    }

    /**
     * <p>Setter for the "shortname" attribute (optional)</p>
     * <p>This key identifies the short name of the bundle. This name
     * should be less than 16 characters long and be suitable for
     * displaying in the menu and the About box. The name is (silently)
     * cropped to this if necessary.</p>
     *
     * @param s The short name for this application bundle
     * @see <a href="https://developer.apple.com/documentation/bundleresources/information_property_list/CFBundleName">Apple Developer Docs for 'CFBundleName' key</a>
     */
    public void setShortName(String s) {
        bundleProperties.setCFBundleName(s);
    }

    /**
     * Setter for the "mainclass" attribute (required)
     *
     * @param s The name of the main class to run when the application bundle is launched.
     */
    public void setMainClass(String s) {
        bundleProperties.setMainClass(s);
    }

    /**
     * Setter for the "WorkingDirectory" attribute (optional)
     *
     * @param s The working directory for the Java application.
     */
    public void setWorkingDirectory(String s) {
        bundleProperties.setWorkingDirectory(s);
    }

    /**
     * Setter for the "icon" attribute (optional)
     *
     * <p>The file containing the bundle's icon.</p>
     *
     * @param f File reference to a macOS icon file.
     * @see <a href="https://developer.apple.com/documentation/bundleresources/information_property_list/CFBundleIconFile">Apple Developer Docs for 'CFBundleIconFile' key</a>
     */
    public void setIcon(File f) {
        mAppIcon = f;
        bundleProperties.setCFBundleIconFile(f.getName());
    }

    /**
     * Setter for the "splashfile" attribute (optional). If it is somewhere
     * in a jar file, which contains a Splash-Screen manifest entry,
     * use "$JAVAROOT/myjar.jar"
     *
     * @param s Path to a splash file
     * @author Angelo van der Sijpt
     * @since 2.0.0
     */
    public void setSplashFile(String s) {
        bundleProperties.setSplashFile(s);
    }

    /**
     * <p>Setter for the "bundleid" attribute (optional) This key specifies a
     * unique identifier string for the bundle. This identifier should be in the
     * form of a Java-style package name, for example com.mycompany.myapp. The
     * bundle identifier can be used to locate the bundle at runtime. The
     * preferences system uses this string to identify applications uniquely.</p>
     * <p>No default.</p>
     *
     * @param s Unique identifier string for the bundle.
     * @see <a href="https://developer.apple.com/documentation/bundleresources/information_property_list/CFBundleIdentifier">Apple Developer Docs for 'CFBundleIdentifier' key</a>
     */
    public void setBundleid(String s) {
        bundleProperties.setCFBundleIdentifier(s);
    }

    /**
     * <p>Setter for the "developmentregion" attribute (optional)</p>
     * <p>Default "English".</p>
     *
     * <p>The default language and region for the bundle, as a language ID.</p>
     *
     * @param s Development region (country)
     * @see <a href="https://developer.apple.com/documentation/bundleresources/information_property_list/CFBundleDevelopmentRegion">Apple Developer Docs for 'CFBundleDevelopmentRegion' key</a>
     */
    public void setDevelopmentregion(String s) {
        bundleProperties.setCFBundleDevelopmentRegion(s);
    }

    /**
     * <p>Setter for the "allowmixedlocalizations" attribute (optional)</p>
     * <p>Default "false".</p>
     *
     * <p>A Boolean value that indicates whether the bundle supports the retrieval of localized strings from frameworks.</p>
     *
     * @param b True to allow mixed localizations in your app
     * @author Tobias Fischer
     * @see <a href="https://developer.apple.com/documentation/bundleresources/information_property_list/cfbundleallowmixedlocalizations">Apple Developer Docs for 'CFBundleAllowMixedLocalizations' key</a>
     * @since 2.2.1
     */
    public void setAllowMixedLocalizations(boolean b) {
        bundleProperties.setCFBundleAllowMixedLocalizations(b);
    }

    /**
     * Setter for the "NSHumanReadableCopyright" attribute (optional)
     *
     * <p>A human-readable copyright notice for the bundle.</p>
     *
     * @param s A string with the copyright notice for the bundle
     * @author Tobias Fischer
     * @see <a href="https://developer.apple.com/documentation/bundleresources/information_property_list/nshumanreadablecopyright">Apple Developer Docs for 'NSHumanReadableCopyright' key</a>
     * @since 2.3.0
     */
    public void setCopyright(String s) {
        bundleProperties.setNSHumanReadableCopyright(s);
    }

    /**
     * <p>Setter for the "NSHighResolutionCapable" attribute (optional)</p>
     * <p>Default "false".</p>
     *
     * <p>A Boolean value indicating whether the app supports high-resolution displays.</p>
     *
     * @param b True if app is retina compatible
     * @author Tobias Fischer
     * @see <a href="https://developer.apple.com/documentation/bundleresources/information_property_list/nshighresolutioncapable">Apple Developer Docs for 'NSHighResolutionCapable' key</a>
     * @since 2.4.0
     */
    public void setHighResolutionCapable(boolean b) {
        bundleProperties.setNSHighResolutionCapable(b);
    }

    /**
     * Setter for the "NSSupportsAutomaticGraphicsSwitching" attribute (optional) Default "true".
     *
     * @param b A Boolean value indicating whether an OpenGL app may utilize the integrated GPU.
     * @see <a href="https://developer.apple.com/documentation/bundleresources/information_property_list/nssupportsautomaticgraphicsswitching">Apple Developer Docs for 'NSSupportsAutomaticGraphicsSwitching' key</a>
     */
    public void setSupportsAutomaticGraphicsSwitching(boolean b) {
        bundleProperties.setNSSupportsAutomaticGraphicsSwitching(b);
    }

    /**
     * Setter for the "NSPreferencesContentSize" attribute (optional).
     *
     * @param s content size for window "int,int"
     * @author Adrien Quillet
     * @since 2.5.0
     */
    public void setContentSize(String s) {
        // Check input consistency
        String pattern = "[0-9]+,[0-9]+";
        if (!s.matches(pattern)) {
            throw new BuildException("Invalid content size format (expected 'width,height')");
        }
        bundleProperties.setNSPreferencesContentSize(s);
    }

    /**
     * <p>Setter for the alternative 'JavaX' dictionary key (optional)</p>
     * <p>The 'JavaX' key is used to support the 'universalJavaApplicationStub'
     * project which supports Java versions 1.4-16</p>
     *
     * @param b True sets 'JavaX' dictionary key instead of 'Java' key
     * @author Tobias Fischer
     * @see <a href="https://github.com/tofi86/universalJavaApplicationStub">universalJavaApplicationStub Project Docs</a>
     * @since 2.4.0
     */
    public void setUseJavaXKey(boolean b) {
        bundleProperties.setJavaXKey(b);
    }

    /**
     * Setter for the "vmoptions" attribute (optional)
     *
     * @param s Command line options to pass the JVM at startup.
     */
    public void setVmoptions(String s) {
        bundleProperties.setVMOptions(s);
    }

    /**
     * Setter for the "antialiasedgraphics" attribute
     *
     * @param b If set to true, use anti-aliasing when rendering graphics.
     * @deprecated Use &lt;javaproperty name="apple.awt.antialiasing" .../&gt; instead
     */
    public void setAntialiasedgraphics(boolean b) {
        mAntiAliasedGraphics = new Boolean(b);
    }

    /**
     * Setter for the "antialiasedtext" attribute
     *
     * @param b If set to true, use anti-aliasing when rendering text.
     * @deprecated Use &lt;javaproperty name="apple.awt.textantialiasing" .../&gt; instead
     */
    public void setAntialiasedtext(boolean b) {
        mAntiAliasedText = new Boolean(b);
    }

    /**
     * Setter for the "screenmenu" attribute
     *
     * @param b If set to true, puts Swing menus in the macOS menu bar if using the Aqua look and feel.
     * @deprecated Use &lt;javaproperty name="apple.laf.useScreenMenuBar" value="true"/&gt; instead
     */
    public void setScreenmenu(boolean b) {
        mScreenMenuBar = new Boolean(b);
    }

    /**
     * Setter for the "growbox" attribute
     *
     * @param b Show the Aqua resize (grow) box.
     * @deprecated Use &lt;javaproperty name="apple.awt.showGrowBox" .../&gt; instead
     */
    public void setGrowbox(boolean b) {
        mGrowbox = new Boolean(b);
    }

    /**
     * Setter for the "type" attribute
     *
     * @param s The Mac OS type of the bundle.
     * @see <a href="https://developer.apple.com/documentation/bundleresources/information_property_list/cfbundlepackagetype">Apple Developer Docs for 'CFBundlePackageType' key</a>
     * @deprecated As it should always be set to APPL for applications.
     */
    public void setType(String s) {
        bundleProperties.setCFBundlePackageType(s);
    }

    /**
     * Setter for the "signature" attribute (optional)
     *
     * @param s The four-letter code identifying the bundle.
     */
    public void setSignature(String s) {
        bundleProperties.setCFBundleSignature(s);
    }

    /**
     * Setter for the "jvmversion" attribute (optional)
     *
     * <p>Default is `1.4+` if this attribute isn't used.</p>
     *
     * <p>The value must represent a Java version higher than 1.4 and may contain special chars
     * such as wildcards or range indicators:</p>
     *
     * <ul>
     *     <li>1.4</li>
     *     <li>1.7+</li>
     *     <li>1.8.0_144</li>
     *     <li>9.0.1+</li>
     *     <li>10*</li>
     * </ul>
     *
     * <p>Specifying a maximum JvmVersion is supported if you use the
     * universalJavaApplicationStub project as launcherStub:</p>
     *
     * <ul>
     *     <li>1.8;11*</li>
     * </ul>
     *
     * <p>See <a href="https://github.com/tofi86/universalJavaApplicationStub#specify-minmax-java-requirement"
     * >https://github.com/tofi86/universalJavaApplicationStub#specify-minmax-java-requirement</a>
     * for further details.</p>
     *
     * @param s The version of the JVM required to run the application.
     */
    public void setJvmversion(String s) {
        bundleProperties.setJVMVersion(s);
    }


    /**
     * Setter for the "JVMArchs" attribute (optional)
     *
     * @param s A space delimited string. Example: "i386 x64_86 ppc"
     * @author Tobias Bley
     * @since 2.2.0
     */
    public void setJvmArchs(String s) {
        bundleProperties.setJVMArchs(s);
    }


    /**
     * Setter for the "LSArchitecturePriority" attribute (optional)
     *
     * <p>An array of the architectures that the app supports, arranged according to their preferred usage.</p>
     *
     * @param s A space delimited string. Example: "i386 x64_86 ppc"
     * @author Michael Bader
     * @see <a href="https://developer.apple.com/documentation/bundleresources/information_property_list/lsarchitecturepriority">Apple Developer Docs for 'LSArchitecturePriority' key</a>
     * @since 2.2.0
     */
    public void setLSArchitecturePriority(String s) {
        bundleProperties.setLSArchitecturePriority(s);
    }


    /**
     * Setter for the "startonmainthread" attribute (optional)
     *
     * @param b true to start on JVM main thread
     * @author Mitch Coopet
     * @since 2.1.0
     */
    public void setStartonmainthread(boolean b) {
        bundleProperties.setStartOnMainThread(new Boolean(b));
    }


    /**
     * Setter for the "startasagent" attribute (optional)
     *
     * <p>A Boolean value indicating whether the app is an agent app that runs in the background and doesn't appear in the Dock.</p>
     *
     * @param b ???
     * @see <a href="https://developer.apple.com/documentation/bundleresources/information_property_list/lsuielement">Apple Developer Docs for 'LSUIElement' key</a>
     */
    public void setIsAgent(boolean b) {
        bundleProperties.setLSUIElement(new Boolean(b));
    }


    /**
     * Setter for the "verbose" attribute (optional)
     *
     * @param verbose If true, output more verbose information to Ant while the task is running.
     */
    public void setVerbose(boolean verbose) {
        this.mVerbose = verbose;
    }


    /**
     * Setter for the "showplist" attribute (optional)
     *
     * @param showPlist If true, display the contents of the Info.plist file
     */
    public void setShowPlist(boolean showPlist) {
        this.mShowPlist = showPlist;
    }


    /**
     * <p>Setter for the "build" attribute (optional) This key specifies the
     * exact build version of the bundle. This string is usually of the form
     * nn.n.nxnnn where n is a digit and x is a character from the set [abdf].
     * The first number is the major version number of the bundle and can
     * contain one or two digits to represent a number in the range 0-99. The
     * second and third numbers are minor revision numbers and must be a single
     * numeric digit. The fourth set of digits is the specific build number for
     * the release.</p>
     *
     * <p>You may omit minor revision and build number information as appropriate.
     * You may also omit major and minor revision information and specify only a
     * build number. For example, valid version numbers include: 1.0.1,
     * 1.2.1b10, 1.2d200, d125, 101, and 1.0.</p>
     *
     * <p>The value of this key typically changes between builds and is displayed
     * in the Cocoa About panel in parenthesis. To specify the version
     * information of a released bundle, use the CFBundleShortVersionString key.</p>
     *
     * @param s The build number
     * @see <a href="https://developer.apple.com/documentation/bundleresources/information_property_list/cfbundleversion">Apple Developer Docs for 'CFBundleVersion' key</a>
     */
    public void setBuild(String s) {
        bundleProperties.setCFBundleVersion(s);
    }

    /**
     * Setter for the version attribute (optional). It is this property, not
     * CFBundleVersion, which should receive the `short' version string. See for
     * example
     * http://developer.apple.com/documentation/MacOSX/Conceptual/BPRuntimeConfig/
     *
     * @param s The version number
     * @see <a href="https://developer.apple.com/documentation/bundleresources/information_property_list/cfbundleshortversionstring">Apple Developer Docs for 'CFBundleShortVersionString' key</a>
     */
    public void setVersion(String s) {
        bundleProperties.setCFBundleShortVersionString(s);
    }

    /**
     * Setter for the "helpbookfolder" attribute (optional)
     *
     * @param s The directory name of the Help Book in the "resources" directory
     */
    public void setHelpBookFolder(String s) {
        bundleProperties.setCFBundleHelpBookFolder(s);
    }

    /**
     * Setter for the "helpbookname" attribute (optional)
     *
     * @param s The name of the Help Book
     */
    public void setHelpBookName(String s) {
        bundleProperties.setCFBundleHelpBookName(s);
    }


    /**
     * Setter for the "jars" attribute (required if no "jarfileset" is present)
     *
     * @param s A list of jar files or patternsets (space or comma seperated)
     * @deprecated Use &lt;jarfileset&gt; and/or &lt;jarfilelist&gt; nested tasks instead.
     */
    public void setJars(String s) {
        PatternSet patset = new PatternSet();
        patset.setIncludes(s);

        String[] jarNames = patset.getIncludePatterns(getProject());

        for (int i = 0; i < jarNames.length; i++) {
            mJarAttrs.add(getProject().resolveFile(jarNames[i]));
        }
    }

    /**
     * Setter for the "jar" attribute (required if no "jarfileset" is present)
     *
     * @param s A single jar file to be used in your application.
     */
    public void setJar(File s) {
        mJarAttrs.add(s);
    }

    /**
     * Setter for the "execs" attribute
     *
     * @param s A list of files or patternsets (space or comma seperated)
     * @deprecated Use &lt;execfileset/&gt; or &lt;execfilelist/&gt; nested tasks instead.
     */
    public void setExecs(String s) {
        PatternSet patset = new PatternSet();
        patset.setIncludes(s);

        String[] execNames = patset.getIncludePatterns(getProject());

        for (int i = 0; i < execNames.length; i++) {
            File f = new File(execNames[i]);
            mExecAttrs.add(f);
        }
    }

    /**
     * Setter for the "extraclasspath" attribute (optional)
     *
     * @param s A list of files or patternsets (space or comma seperated)
     */
    public void setExtraclasspath(String s) {
        if (s == null || s.trim().equals("")) {
            return;
        }
        PatternSet patset = new PatternSet();
        patset.setIncludes(s);

        String[] cpNames = patset.getIncludePatterns(getProject());

        for (int i = 0; i < cpNames.length; i++) {
            File f = new File(cpNames[i]);
            mExtraClassPathAttrs.add(f);
        }
    }

    /**
     * Set the 'chmod' executable.
     *
     * @param s File to chmod
     * @deprecated use the ANT Chmod task instead
     */
    public void setChmod(String s) {
        log("The \"chmod\" attribute is deprecated, use the ANT Chmod task instead");
    }


    /**
     * Setter for the 'suFeedURL' attribute (optional)
     *
     * @param url URL used to check for new version of the applications.
     * @author Tobias Bley
     * @see <a href="https://sparkle-project.org/documentation/">Sparkle software update framework docs</a>
     * @since 2.2.0
     */
    public void setSUFeedURL(String url) {
        this.bundleProperties.setSUFeedURL(url);
    }

    /**
     * Setter for the 'SUPublicDSAKeyFile' attribute (optional)
     *
     * @param file DSA key file
     * @author Tobias Bley
     * @see <a href="https://sparkle-project.org/documentation/">Sparkle software update framework docs</a>
     * @since 3.2.0
     */
    public void setSUPublicDSAKeyFile(String file) {
        this.bundleProperties.setSUPublicDSAKeyFile(file);
    }

    /**
     * Setter for the 'LSApplicationCategoryType' attribute (optional)
     *
     * @param type Type of application category
     * @author Tobias Bley
     * @see <a href="https://developer.apple.com/documentation/bundleresources/information_property_list/lsapplicationcategorytype">Apple Developer Docs for 'LSApplicationCategoryType' key</a>
     * @since 3.2.0
     */
    public void setLSApplicationCategoryType(String type) {
        this.bundleProperties.setLSApplicationCategoryType(type);
    }


    /***************************************************************************
     * Nested tasks - derived from FileList and FileSet
     **************************************************************************/

    /**
     * @param fs A FileSet
     */
    public void addJarfileset(FileSet fs) {
        mJarFileSets.add(fs);
    }

    public void addJarfilelist(FileList fl) {
        mJarFileLists.add(fl);
    }

    public void addExecfileset(FileSet fs) {
        mExecFileSets.add(fs);
    }

    public void addExecfilelist(FileList fl) {
        mExecFileLists.add(fl);
    }

    public void addResourcefileset(FileSet fs) {
        mResourceFileSets.add(fs);
    }

    public void addResourcefilelist(FileList fl) {
        mResourceFileLists.add(fl);
    }

    public void addJavafileset(FileSet fs) {
        mJavaFileSets.add(fs);
    }

    public void addJavafilelist(FileList fl) {
        mJavaFileLists.add(fl);
    }

    public void addExtraclasspathfileset(FileSet fs) {
        mExtraClassPathFileSets.add(fs);
    }

    public void addExtraclasspathfilelist(FileList fl) {
        mExtraClassPathFileLists.add(fl);
    }


    /***************************************************************************
     * Nested tasks - new tasks with custom attributes
     **************************************************************************/

    /**
     * Add a configured LSEnvironment
     *
     * @param lsEnvironment A 'lsenvironment' element
     * @throws BuildException 'lsenvironment' must have both 'name' and 'value' attributes
     * @author Tobias Bley
     * @since 3.2.0
     */
    public void addConfiguredLSEnvironment(LSEnvironment lsEnvironment) throws BuildException {

        String name = lsEnvironment.getName();
        String value = lsEnvironment.getValue();

        if ((name == null) || (value == null)) {
            throw new BuildException(
                "'<lsenvironment>' must have both 'name' and 'value' attributes");
        }

        bundleProperties.addLSEnvironment(name, value);
    }

    public void addConfiguredJavaProperty(JavaProperty javaProperty) throws BuildException {
        String name = javaProperty.getName();
        String value = javaProperty.getValue();

        if ((name == null) || (value == null)) {
            throw new BuildException(
                "'<javaproperty>' must have both 'name' and 'value' attributes");
        }

        bundleProperties.addJavaProperty(name, value);
    }

    public void addConfiguredDocumentType(DocumentType documentType) throws BuildException {
        String name = documentType.getName();
        String role = documentType.getRole();
        List osTypes = documentType.getOSTypes();
        List extensions = documentType.getExtensions();
        List mimeTypes = documentType.getMimeTypes();

        if ((name == null) || (role == null)) {
            throw new BuildException(
                "'<documenttype>' must have both a 'name' and a 'role' attribute");
        }

        if ((osTypes.isEmpty()) && (extensions.isEmpty()) && (mimeTypes.isEmpty())) {
            throw new BuildException(
                "'<documenttype>' of \""
                    + name
                    + "\" must have 'osTypes' or 'extensions' or 'mimeTypes'");
        }

        bundleProperties.addDocumentType(documentType);
    }

    public void addConfiguredService(Service service) {

        //if (service.getPortName() == null)
        //    throw new BuildException("\"<service>\" must have a \"portName\" attribute");

        if (service.getMessage() == null) {
            throw new BuildException("\"<service>\" must have a \"message\" attribute");
        }

        String menuItem = service.getMenuItem();
        if (menuItem == null) {
            throw new BuildException("\"<service>\" must have a \"menuItem\" attribute");
        }
        if (!menuItems.add(menuItem)) {
            throw new BuildException("\"<service>\" \"menuItem\" value must be unique");
        }

        if (service.getSendTypes().isEmpty() && service.getReturnTypes().isEmpty()) {
            throw new BuildException("\"<service>\" must have either a \"sendTypes\" attribute, a \"returnTypes\" attribute or both");
        }

        String keyEquivalent = service.getKeyEquivalent();
        if ((keyEquivalent != null) && (1 != keyEquivalent.length())) {
            throw new BuildException("\"<service>\" \"keyEquivalent\" must be one character if present");
        }

        String timeoutString = service.getTimeout();
        if (timeoutString != null) {
            long timeout = -1;
            try {
                timeout = Long.parseLong(timeoutString);
            } catch (NumberFormatException nfe) {
                throw new BuildException("\"<service>\" \"timeout\" must be a positive integral number");
            }
            if (timeout < 0) {
                throw new BuildException("\"<service>\" \"timeout\" must not be negative");
            }
        }

        bundleProperties.addService(service);
    }

    public void addConfiguredHelpBook(HelpBook helpBook) {

        // Validity check on 'foldername'
        if (helpBook.getFolderName() == null) {
            if (bundleProperties.getCFBundleHelpBookFolder() == null) {
                throw new BuildException("Either the '<helpbook>' attribute 'foldername' or the '<jarbundler>' attribute 'helpbookfolder' must be defined");
            }
            helpBook.setFolderName(bundleProperties.getCFBundleHelpBookFolder());
        }

        // Validity check on 'title'
        if (helpBook.getName() == null) {
            if (bundleProperties.getCFBundleHelpBookName() == null) {
                throw new BuildException("Either the '<helpbook>' attribute 'name' or the '<jarbundler>' attribute 'helpbookname' must be defined");
            }
            helpBook.setName(bundleProperties.getCFBundleHelpBookName());
        }

        // Make sure some file were selected...
        List fileLists = helpBook.getFileLists();
        List fileSets = helpBook.getFileSets();

        if (fileLists.isEmpty() && fileSets.isEmpty()) {
            throw new BuildException("The '<helpbook>' task must have either " +
                "'<fileset>' or  '<filelist>' nested tags");
        }

        mHelpBooks.add(helpBook);
    }


    /***************************************************************************
     * Execute the task
     **************************************************************************/

    /**
     * The method executing the task
     */
    public void execute() throws BuildException {

        // Delete any existing Application bundle directory structure

        bundleDir = new File(mRootDir, bundleProperties.getApplicationName() + ".app");

        if (bundleDir.exists()) {
            Delete deleteTask = new Delete();
            deleteTask.setProject(getProject());
            deleteTask.setDir(bundleDir);
            deleteTask.execute();
        }

        // Validate - look for required attributes
        // ///////////////////////////////////////////

        if (mRootDir == null) {
            throw new BuildException("Required attribute \"dir\" is not set.");
        }

        if (mJarAttrs.isEmpty() && mJarFileSets.isEmpty()
            && mJarFileLists.isEmpty()) {
            throw new BuildException("Either the attribute \"jar\" must "
                + "be set, or one or more jarfilelists or "
                + "jarfilesets must be added.");
        }

        if (!mJarAttrs.isEmpty()
            && (!mJarFileSets.isEmpty() || !mJarFileLists.isEmpty())) {
            throw new BuildException(
                "Cannot set both the attribute "
                    + "\"jars\" and use jar filesets/filelists.  Use only one or the other.");
        }

        if (bundleProperties.getApplicationName() == null) {
            throw new BuildException("Required attribute \"name\" is not set.");
        }

        if (bundleProperties.getMainClass() == null) {
            throw new BuildException(
                "Required attribute \"mainclass\" is not set.");
        }

        // /////////////////////////////////////////////////////////////////////////////////////

        // Set up some Java properties

        // Anti Aliased Graphics
        String antiAliasedProperty = "apple.awt.antialiasing";
        if (mAntiAliasedGraphics != null) {
            bundleProperties.addJavaProperty(antiAliasedProperty,
                mAntiAliasedGraphics.toString());
        }

        // Anti Aliased Text
        String antiAliasedTextProperty = "apple.awt.textantialiasing";
        if (mAntiAliasedText != null) {
            bundleProperties.addJavaProperty(antiAliasedTextProperty,
                mAntiAliasedText.toString());
        }

        // Screen Menu Bar
        String screenMenuBarProperty = "apple.laf.useScreenMenuBar";
        if (mScreenMenuBar != null) {
            bundleProperties.addJavaProperty(screenMenuBarProperty,
                mScreenMenuBar.toString());
        }

        // Growbox, added with 1.4+
        if (mGrowbox != null) {
            bundleProperties.addJavaProperty("apple.awt.showGrowBox",
                mGrowbox.toString());
        }

        if (!mRootDir.exists()
            || (mRootDir.exists() && !mRootDir.isDirectory())) {
            throw new BuildException(
                "Destination directory specified by \"dir\" "
                    + "attribute must already exist.");
        }

        if (bundleDir.exists()) {
            throw new BuildException("The directory/bundle \""
                + bundleDir.getName()
                + "\" already exists, cannot continue.");
        }

        // Status message
        log("Creating application bundle: " + bundleDir);

        if (!bundleDir.mkdir()) {
            throw new BuildException("Unable to create bundle: " + bundleDir);
        }

        // Make the Contents directory
        mContentsDir = new File(bundleDir, "Contents");

        if (!mContentsDir.mkdir()) {
            throw new BuildException("Unable to create directory "
                + mContentsDir);
        }

        // Make the "MacOS" directory
        mMacOsDir = new File(mContentsDir, "MacOS");

        if (!mMacOsDir.mkdir()) {
            throw new BuildException("Unable to create directory " + mMacOsDir);
        }

        // Make the Resources directory
        mResourcesDir = new File(mContentsDir, "Resources");

        if (!mResourcesDir.mkdir()) {
            throw new BuildException("Unable to create directory "
                + mResourcesDir);
        }

        // Make the Resources/Java directory
        mJavaDir = new File(mResourcesDir, "Java");

        if (!mJavaDir.mkdir()) {
            throw new BuildException("Unable to create directory " + mJavaDir);
        }

        // Copy icon file to resource dir. If no icon parameter
        // is supplied, the default icon will be used.

        if (mAppIcon != null) {
            try {
                File dest = new File(mResourcesDir, mAppIcon.getName());

                if (mVerbose) {
                    log("Copying application icon file to \"" + bundlePath(dest) + "\"");
                }

                mFileUtils.copyFile(mAppIcon, dest);
            } catch (IOException ex) {
                throw new BuildException("Cannot copy icon file: " + ex);
            }
        }

        // Copy document type icons, if any, to the resource dir
        try {
            Iterator itor = bundleProperties.getDocumentTypes().iterator();

            while (itor.hasNext()) {
                DocumentType documentType = (DocumentType) itor.next();
                File iconFile = documentType.getIconFile();
                if (iconFile != null) {
                    File dest = new File(mResourcesDir, iconFile.getName());
                    if (mVerbose) {
                        log("Copying document icon file to \"" + bundlePath(dest) + "\"");
                    }
                    mFileUtils.copyFile(iconFile, dest);
                }
            }
        } catch (IOException ex) {
            throw new BuildException("Cannot copy document icon file: " + ex);
        }

        // Copy application jar(s) from the "jars" attribute (if any)
        processJarAttrs();

        // Copy application jar(s) from the nested jarfileset element(s)
        processJarFileSets();

        // Copy application jar(s) from the nested jarfilelist element(s)
        processJarFileLists();

        // Copy executable(s) from the "execs" attribute (if any)
        processExecAttrs();

        // Copy executable(s) from the nested execfileset element(s)
        processExecFileSets();

        // Copy executable(s) from the nested execfilelist element(s)
        processExecFileLists();

        // Copy resource(s) from the nested resourcefileset element(s)
        processResourceFileSets();

        // Copy resource(s) from the nested javafileset element(s)
        processJavaFileSets();

        // Copy resource(s) from the nested resourcefilelist element(s)
        processResourceFileLists();

        // Copy resource(s) from the nested javafilelist element(s)
        processJavaFileLists();

        // Add external classpath references from the extraclasspath attributes
        processExtraClassPathAttrs();

        // Add external classpath references from the nested
        // extraclasspathfileset element(s)
        processExtraClassPathFileSets();

        // Add external classpath references from the nested
        // extraclasspathfilelist attributes
        processExtraClassPathFileLists();

        // Copy HelpBooks into place
        copyHelpBooks();

        // Copy the JavaApplicationStub file from the Java system directory to
        // the MacOS directory
        copyApplicationStub();

        // Create the Info.plist file
        writeInfoPlist();

        // Create the PkgInfo file
        writePkgInfo();

        // Done!
    }

    /***************************************************************************
     * Private utility methods.
     **************************************************************************/

    private void setExecutable(File f) {

        Chmod chmodTask = new Chmod();
        chmodTask.setProject(getProject());
        chmodTask.setFile(f);
        chmodTask.setPerm("ugo+rx");

        if (mVerbose) {
            log("Setting \"" + bundlePath(f) + "\" to executable");
        }

        chmodTask.execute();

    }

    private void processJarAttrs() throws BuildException {

        try {

            for (Iterator jarIter = mJarAttrs.iterator(); jarIter.hasNext(); ) {
                File src = (File) jarIter.next();
                File dest = new File(mJavaDir, src.getName());

                if (mVerbose) {
                    log("Copying JAR file to \"" + bundlePath(dest) + "\"");
                }


                mFileUtils.copyFile(src, dest);
                bundleProperties.addToClassPath(dest.getName());
            }
        } catch (IOException ex) {
            throw new BuildException("Cannot copy jar file: " + ex);
        }
    }

    private void processJarFileSets() throws BuildException {

        for (Iterator jarIter = mJarFileSets.iterator(); jarIter.hasNext(); ) {

            FileSet fs = (FileSet) jarIter.next();

            Project p = fs.getProject();
            File srcDir = fs.getDir(p);
            FileScanner ds = fs.getDirectoryScanner(p);
            fs.setupDirectoryScanner(ds, p);
            ds.scan();

            String[] files = ds.getIncludedFiles();

            try {

                for (int i = 0; i < files.length; i++) {
                    String fileName = files[i];
                    File src = new File(srcDir, fileName);
                    File dest = new File(mJavaDir, fileName);

                    if (mVerbose) {
                        log("Copying JAR file to \"" + bundlePath(dest) + "\"");
                    }

                    mFileUtils.copyFile(src, dest);

                    bundleProperties.addToClassPath(fileName);
                }

            } catch (IOException ex) {
                throw new BuildException("Cannot copy jar file: " + ex);
            }
        }
    }

    private void processJarFileLists() throws BuildException {

        for (Iterator jarIter = mJarFileLists.iterator(); jarIter.hasNext(); ) {
            FileList fl = (FileList) jarIter.next();
            Project p = fl.getProject();
            File srcDir = fl.getDir(p);
            String[] files = fl.getFiles(p);

            try {

                for (int i = 0; i < files.length; i++) {
                    String fileName = files[i];
                    File src = new File(srcDir, fileName);
                    File dest = new File(mJavaDir, fileName);

                    if (mVerbose) {
                        log("Copying JAR file to \"" + bundlePath(dest) + "\"");
                    }


                    mFileUtils.copyFile(src, dest);

                    bundleProperties.addToClassPath(fileName);
                }
            } catch (IOException ex) {
                throw new BuildException("Cannot copy jar file: " + ex);
            }
        }
    }

    private void processExtraClassPathAttrs() throws BuildException {

        for (Iterator jarIter = mExtraClassPathAttrs.iterator(); jarIter.hasNext(); ) {
            File src = (File) jarIter.next();
            String path = src.getPath().replace(File.separatorChar, '/');
            bundleProperties.addToExtraClassPath(path);
        }
    }

    private void processExtraClassPathFileSets() throws BuildException {

        for (Iterator jarIter = mExtraClassPathFileSets.iterator(); jarIter.hasNext(); ) {
            FileSet fs = (FileSet) jarIter.next();
            Project p = fs.getProject();
            File srcDir = fs.getDir(p);
            FileScanner ds = fs.getDirectoryScanner(p);
            fs.setupDirectoryScanner(ds, p);
            ds.scan();

            String[] files = ds.getIncludedFiles();

            for (int i = 0; i < files.length; i++) {
                File f = new File(srcDir, files[i]);
                String path = f.getPath().replace(File.separatorChar, '/');
                bundleProperties.addToExtraClassPath(path);
            }
        }
    }

    private void processExtraClassPathFileLists() throws BuildException {
        for (Iterator jarIter = mExtraClassPathFileLists.iterator(); jarIter.hasNext(); ) {
            FileList fl = (FileList) jarIter.next();
            Project p = fl.getProject();
            File srcDir = fl.getDir(p);
            String[] files = fl.getFiles(p);

            for (int i = 0; i < files.length; i++) {
                File f = new File(srcDir, files[i]);
                String path = f.getPath().replace(File.separatorChar, '/');
                bundleProperties.addToExtraClassPath(path);
            }
        }
    }

    private void processExecAttrs() throws BuildException {
        try {
            for (Iterator execIter = mExecAttrs.iterator(); execIter.hasNext(); ) {
                File src = (File) execIter.next();
                File dest = new File(mMacOsDir, src.getName());

                if (mVerbose) {
                    log("Copying exec file to \"" + bundlePath(dest) + "\"");
                }


                mFileUtils.copyFile(src, dest);
                setExecutable(dest);
            }
        } catch (IOException ex) {
            throw new BuildException("Cannot copy exec file: " + ex);
        }
    }

    // Methods for copying FileSets into the application bundle ///////////////////////////////

    // Files for the Contents/MacOS directory
    private void processExecFileSets() {
        processCopyingFileSets(mExecFileSets, mMacOsDir, true);
    }

    // Files for the Contents/Resources directory
    private void processResourceFileSets() {
        processCopyingFileSets(mResourceFileSets, mResourcesDir, false);
    }

    // Files for the Contents/Resources/Java directory
    private void processJavaFileSets() {
        processCopyingFileSets(mJavaFileSets, mJavaDir, false);
    }

    private void processCopyingFileSets(List fileSets, File targetdir, boolean setExec) {
        for (Iterator execIter = fileSets.iterator(); execIter.hasNext(); ) {
            FileSet fs = (FileSet) execIter.next();
            Project p = fs.getProject();
            File srcDir = fs.getDir(p);
            FileScanner ds = fs.getDirectoryScanner(p);
            fs.setupDirectoryScanner(ds, p);
            ds.scan();

            String[] files = ds.getIncludedFiles();

            if (files.length == 0) {
                // this is probably an error -- warn about it
                System.err
                    .println("WARNING: fileset for copying from directory "
                        + srcDir + ": no files found");
            } else {
                try {
                    for (int i = 0; i < files.length; i++) {
                        String fileName = files[i];
                        File src = new File(srcDir, fileName);
                        File dest = new File(targetdir, fileName);

                        if (mVerbose) {
                            log("Copying "
                                + (setExec ? "exec" : "resource")
                                + " file to \"" + bundlePath(dest) + "\"");
                        }

                        mFileUtils.copyFile(src, dest);
                        if (setExec) {
                            setExecutable(dest);
                        }
                    }
                } catch (IOException ex) {
                    throw new BuildException("Cannot copy file: " + ex);
                }
            }
        }
    }

    // Methods for copying FileLists into the application bundle /////////////////////////////

    // Files for the Contents/MacOS directory
    private void processExecFileLists() throws BuildException {
        processCopyingFileLists(mExecFileLists, mMacOsDir, true);
    }

    // Files for the Contents/Resources directory
    private void processResourceFileLists() throws BuildException {
        processCopyingFileLists(mResourceFileLists, mResourcesDir, false);
    }

    // Files for the Contents/Resources/Java directory
    private void processJavaFileLists() throws BuildException {
        processCopyingFileLists(mJavaFileLists, mJavaDir, false);
    }

    private void processCopyingFileLists(List fileLists, File targetDir, boolean setExec) throws BuildException {
        for (Iterator execIter = fileLists.iterator(); execIter.hasNext(); ) {

            FileList fl = (FileList) execIter.next();
            Project p = fl.getProject();
            File srcDir = fl.getDir(p);
            String[] files = fl.getFiles(p);

            if (files.length == 0) {
                // this is probably an error -- warn about it
                System.err.println("WARNING: filelist for copying from directory "
                    + srcDir + ": no files found");
            } else {
                try {
                    for (int i = 0; i < files.length; i++) {
                        String fileName = files[i];
                        File src = new File(srcDir, fileName);
                        File dest = new File(targetDir, fileName);

                        if (mVerbose) {
                            log("Copying "
                                + (setExec ? "exec" : "resource")
                                + " file to \"" + bundlePath(dest) + "\"");
                        }

                        mFileUtils.copyFile(src, dest);
                        if (setExec) {
                            setExecutable(dest);
                        }
                    }
                } catch (IOException ex) {
                    throw new BuildException("Cannot copy jar file: " + ex);
                }
            }
        }
    }


    private void copyHelpBooks() {
        for (Iterator itor = mHelpBooks.iterator(); itor.hasNext(); ) {

            HelpBook helpBook = (HelpBook) itor.next();

            String folderName = helpBook.getFolderName();
            String name = helpBook.getName();
            String locale = helpBook.getLocale();

            List fileLists = helpBook.getFileLists();
            List fileSets = helpBook.getFileSets();


            File helpBookDir = null;

            if (locale == null) {

                // Set the Bundle entries for a nonlocalized Help Book
                if (folderName != null) {
                    bundleProperties.setCFBundleHelpBookFolder(folderName);
                }

                if (name != null) {
                    bundleProperties.setCFBundleHelpBookName(name);
                }

                // The non-localized Help Book is top level "/Resources"
                helpBookDir = new File(mResourcesDir, folderName);
                helpBookDir.mkdir();

                if (mVerbose) {
                    log("Creating Help Book at \"" +
                        bundlePath(helpBookDir) + "\"");
                }


            } else {

                // The localized Help Book is "/Resources/locale.lproj"

                File lproj = new File(mResourcesDir, locale + ".lproj");
                lproj.mkdir();
                helpBookDir = new File(lproj, folderName);
                helpBookDir.mkdir();

                if (mVerbose) {
                    log("Creating Help Book for \"" + locale +
                        "\" at \"" + bundlePath(helpBookDir) + "\"");
                }

                // Create a local file to override the Bundle settings
                File infoPList = new File(lproj, "InfoPlist.strings");
                PrintWriter writer = null;
                try {
                    writer = new PrintWriter(new FileWriter(infoPList));
                    writer.println("CFBundleHelpBookFolder = \"" + folderName + "\";");
                    writer.println("CFBundleHelpBookName = \"" + name + "\";");
                    writer.println("CFBundleName = \"" + bundleProperties.getCFBundleName() + "\";");
                } catch (IOException ioe) {
                    throw new BuildException("IOException in writing Help Book locale: " + locale);
                } finally {
                    FileUtils.close(writer);
                }
            }

            // Write the Help Book source files into the bundle

            processCopyingFileSets(fileSets, helpBookDir, false);
            processCopyingFileLists(fileLists, helpBookDir, false);

        }
    }


    /**
     * Copy the application stub into the bundle
     *
     * @throws BuildException
     */
    private void copyApplicationStub() throws BuildException {
        File newStubFile = new File(mMacOsDir, bundleProperties.getCFBundleExecutable());

        if (mVerbose) {
            log("Copying Java application stub to \"" + bundlePath(newStubFile) + "\"");
        }

        try {
            mFileUtils.copyFile(mStubFile, newStubFile);
        } catch (IOException ex) {
            throw new BuildException("Cannot copy Java Application Stub: " + ex);
        }

        // Set the permissions on the stub file to executable

        setExecutable(newStubFile);
    }

    private void writeInfoPlist() throws BuildException {
        PropertyListWriter listWriter = new PropertyListWriter(bundleProperties);
        File infoPlist = new File(mContentsDir, "Info.plist");

        listWriter.writeFile(infoPlist);

        if (mVerbose) {
            log("Creating \"" + bundlePath(infoPlist) + "\" file");
        }


        if (mShowPlist) {
            try {
                BufferedReader in = new BufferedReader(new FileReader(infoPlist));
                String str;
                while ((str = in.readLine()) != null) {
                    log(str);
                }
                in.close();
            } catch (IOException e) {
                throw new BuildException(e);
            }
        }
    }

    /**
     * Write the PkgInfo file into the application bundle
     *
     * @throws BuildException
     */
    private void writePkgInfo() throws BuildException {
        File pkgInfo = new File(mContentsDir, "PkgInfo");
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(pkgInfo)));
            writer.print(bundleProperties.getCFBundlePackageType());
            writer.println(bundleProperties.getCFBundleSignature());
            writer.flush();
        } catch (IOException ex) {
            throw new BuildException("Cannot create PkgInfo file: " + ex);
        } finally {
            FileUtils.close(writer);
        }
    }

    private String bundlePath(File bundleFile) {
        String rootPath = bundleDir.getAbsolutePath();
        String thisPath = bundleFile.getAbsolutePath();

        return thisPath.substring(rootPath.length());
    }

}
