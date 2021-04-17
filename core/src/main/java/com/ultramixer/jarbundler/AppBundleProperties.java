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
 *
 */
package com.ultramixer.jarbundler;


// Java Utility
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
// import java.util.Scanner;
import java.util.List;


public class AppBundleProperties {

    // Required
    private String mApplicationName;
    private String mMainClass;

    // Application short name
    private String mCFBundleName = null;

    // Finder version, with default
    private String mCFBundleShortVersionString = "1.0";

    // Build number, optional
    private String mCFBundleVersion = null;

    // Help Book folder, optional
    private String mCFHelpBookFolder = null;

    // Help Book name, optional
    private String mCFHelpBookName = null;

    // StartOnMainThread, optional
    private Boolean mStartOnMainThread = null;

    // StartAsAgent, optional (per Michael Bader <nufan_k@me.com>)
    private Boolean mLSUIElement = null;

    // Explicit default: false
    private boolean mCFBundleAllowMixedLocalizations = false;

    // Copyright, optional

    private String mNSHumanReadableCopyright = null;

    // Window size, optional (per Adrien Quillet <aquillet@is2t.com>)
    private String mNSPreferencesContentSize = null;

    // Support for JavaX key, optional
    private boolean mJavaXKey = false;

    // Explicit default: JavaApplicationStub
    private String mCFBundleExecutable = "JavaApplicationStub";

    // Explicit default: English
    private String mCFBundleDevelopmentRegion = "English";

    // Explicit default: APPL
    private final String mCFBundlePackageType = "APPL";

    // Explicit default: ????
    private String mCFBundleSignature = "????";

    // Explicit default: 1.3+
    private String mJVMVersion = "1.3+";

    // Explicit default: 6.0
    private final String mCFBundleInfoDictionaryVersion = "6.0";

    // Optional keys, with no defaults.
    private String mCFBundleIconFile = null;
    private String mSplashFile = null;
    private String mCFBundleIdentifier = null;
    private String mVMOptions = null; // Java VM options
    private String mWorkingDirectory = null; // Java Working Dir
    private String mArguments = null; // Java command line arguments

    // Class path and extra class path elements
    private List mClassPath = new ArrayList();
    private List mExtraClassPath = new ArrayList();

    // New since JarBundler 2.2.0; Tobias Bley / UltraMixer ----------------
    private List mJVMArchs = new ArrayList();
    private List mLSArchitecturePriority = new ArrayList();
    private String mLSApplicationCategoryType = null;


    // Sparkle Properties
    private String mSUFeedURL = null;
    // New 2015-08-05 Tobias Bkey / UltraMixer
    private String mSUPublicDSAKeyFile;

    // Java properties
    private Hashtable mJavaProperties = new Hashtable();

    // Document types
    private List mDocumentTypes = new LinkedList();

    // Services
    private List mServices = new LinkedList();


    // New since 2015-08-05 Tobias Bley / UltraMixer
    // HiDPI support (Retina)
    // NSHighResolutionCapable
    // HiRes capability, optional
    private boolean mNSHighResolutionCapable;

    private boolean mNSSupportsAutomaticGraphicsSwitching = true;


    /**
     * LSEnvironment (Dictionary - OS X) defines environment variables to be set before launching this app. The names of the environment variables are the keys of the dictionary, with the values being the corresponding environment variable value. Both keys and values must be strings.
     * These environment variables are set only for apps launched through Launch Services. If you run your executable directly from the command line, these environment variables are not set.
     *
     * @author Tobias Bley
     * @since 3.2.0
     */
    private Hashtable mLSEnvironments = new Hashtable();


    // ================================================================================

    /**
     * Add a LSEnvironment key-value pair to the mLSEnvironments hashtable.
     *
     * @author Tobias Bley
     * @since 3.2.0
     *
     * @param key A Key
     * @param value A Value
     */
    public void addLSEnvironment(String key, String value) {
        mLSEnvironments.put(key, value);
    }


    /**
     *
     * @author Tobias Bley
     * @since 3.2.0
     *
     * @return LSEnvironment
     */
    public Hashtable getLSEnvironment() {
        return mLSEnvironments;
    }

    /**
     * Add a Java runtime property to the properties hashtable.
     *
     * @param prop A property
     * @param val A value
     */
    public void addJavaProperty(String prop, String val) {
        mJavaProperties.put(prop, val);
    }

    public Hashtable getJavaProperties() {
        return mJavaProperties;
    }

    // New in JarBundler 2.2.0; Tobias Bley ----------------

    public void addToJVMArchs(String s) {
        mJVMArchs.add(s);
    }

    public List getJVMArchs() {
        return mJVMArchs;
    }

    //------------------------------------------------------

    public void addToClassPath(String s)
    {
        mClassPath.add("$JAVAROOT/" + s);
    }

    public void addToExtraClassPath(String s) {
        mExtraClassPath.add(s);
    }

    public List getExtraClassPath() {
        return mExtraClassPath;
    }

    public DocumentType createDocumentType() {
        return new DocumentType();
    }

    public List getDocumentTypes() {
        return mDocumentTypes;
    }

    /**
     * Add a document type to the document type list.
     *
     * @param documentType A document type
     */
    public void addDocumentType(DocumentType documentType) {
        mDocumentTypes.add(documentType);
    }

    public Service createService() {
        return new Service();
    }

    public List getServices() {
        return mServices;
    }

    /**
     * Add a service to the services list.
     *
     * @param service Service
     */
    public void addService(Service service) {
        mServices.add(service);
    }

    // ================================================================================

    public void setApplicationName(String s) {
        mApplicationName = s;
    }

    public String getApplicationName() {
        return mApplicationName;
    }

    // ================================================================================
    //
    // Bundle setters and getters
    //

    public void setCFBundleName(String s) {

        if (s.length() > 16)
            System.err
                    .println("WARNING: 'shortname' is recommeded to be no more than 16 "
                            + "charaters long. See usage notes.");
        mCFBundleName = s;
    }

    public String getCFBundleName() {
        if (mCFBundleName == null)
            return getApplicationName();

        return mCFBundleName;
    }

    public void setCFBundleVersion(String s) {
        mCFBundleVersion = s;
    }

    public String getCFBundleVersion() {
        return mCFBundleVersion;
    }

    public void setCFBundleInfoDictionaryVersion(String s) {
        // mCFBundleInfoDictionaryVersion = s;
    }

    public String getCFBundleInfoDictionaryVersion() {
        return mCFBundleInfoDictionaryVersion;
    }

    public void setCFBundleIdentifier(String s) {
        mCFBundleIdentifier = s;
    }

    public String getCFBundleIdentifier() {
        return mCFBundleIdentifier;
    }

    public void setCFBundleShortVersionString(String s) {
        mCFBundleShortVersionString = s;
    }

    public String getCFBundleShortVersionString() {
        return mCFBundleShortVersionString;
    }

    public void setCFBundleIconFile(String s) {
        mCFBundleIconFile = s;
    }

    public String getCFBundleIconFile() {
        return mCFBundleIconFile;
    }

    public void setSplashFile(String s) {
        mSplashFile = s;
    }

    public String getSplashFile() {
        return mSplashFile;
    }

    public void setCFBundleAllowMixedLocalizations(boolean b) {
        mCFBundleAllowMixedLocalizations = b;
    }

    public boolean getCFBundleAllowMixedLocalizations() {
        return mCFBundleAllowMixedLocalizations;
    }

    public void setNSHumanReadableCopyright(String s) {
        mNSHumanReadableCopyright = s;
    }

    public String getNSHumanReadableCopyright() {
        return mNSHumanReadableCopyright;
    }

    public void setNSHighResolutionCapable(boolean b) {
        mNSHighResolutionCapable = b;
    }

    public boolean getNSHighResolutionCapable() {
        return mNSHighResolutionCapable;
    }

    public void setNSSupportsAutomaticGraphicsSwitching(boolean b) {
        mNSSupportsAutomaticGraphicsSwitching = b;
    }

    public boolean getNSSupportsAutomaticGraphicsSwitching() {
        return mNSSupportsAutomaticGraphicsSwitching;
    }

    public void setNSPreferencesContentSize(String s) {
        mNSPreferencesContentSize = s;
    }

    public String getNSPreferencesContentSize() {
        return mNSPreferencesContentSize;
    }

    public void setJavaXKey(boolean b) {
        mJavaXKey = b;
    }

    public boolean getJavaXKey() {
        return mJavaXKey;
    }

    public void setCFBundleExecutable(String s) {
        mCFBundleExecutable = s;
    }

    public String getCFBundleExecutable() {
        return mCFBundleExecutable;
    }

    public void setCFBundleDevelopmentRegion(String s) {
        mCFBundleDevelopmentRegion = s;
    }

    public String getCFBundleDevelopmentRegion() {
        return mCFBundleDevelopmentRegion;
    }

    public void setCFBundlePackageType(String s) {
        // mCFBundlePackageType = s;
    }

    public String getCFBundlePackageType() {
        return mCFBundlePackageType;
    }

    public void setCFBundleSignature(String s) {
        mCFBundleSignature = s;
    }

    public String getCFBundleSignature() {
        return mCFBundleSignature;
    }

    public void setCFBundleHelpBookFolder(String s) {
        mCFHelpBookFolder = s;
    }

    public String getCFBundleHelpBookFolder() {
        return mCFHelpBookFolder;
    }

    public void setCFBundleHelpBookName(String s) {
        mCFHelpBookName = s;
    }

    public String getCFBundleHelpBookName() {
        return mCFHelpBookName;
    }


    public void setStartOnMainThread(Boolean b) {
        mStartOnMainThread = b;
    }

    public Boolean getStartOnMainThread() {
        return mStartOnMainThread;
    }



    public Boolean getLSUIElement() {
        return mLSUIElement;
    }



    public void setLSUIElement( Boolean b ) {
        this.mLSUIElement = b;
    }



    public void setMainClass( String s ) {
        mMainClass = s;
    }

    public String getMainClass() {
        return mMainClass;
    }

    public void setJVMVersion(String s) {
        mJVMVersion = s;
    }

    public String getJVMVersion() {
        return mJVMVersion;
    }

    public void setVMOptions(String s) {
        mVMOptions = s;
    }

    public String getVMOptions() {
        return mVMOptions;
    }

    public void setWorkingDirectory(String s) {
        mWorkingDirectory = s;
    }

    public String getWorkingDirectory() {
        return mWorkingDirectory;
    }

    public void setArguments(String s) {
        mArguments = s;
    }

    public String getArguments() {
        return mArguments;
    }

    public List getClassPath() {
        return mClassPath;
    }

    // New in JarBundler 2.2.0; Tobias Bley ----------------------------------------------------

    /**
     * @param archs space separated archs, e.g. i386 x64_64 ppc
     */

    public void setJVMArchs(String archs) {

        // Use for 1.4 backwards compatability
        String[] tokens = archs.split("\\s+");
        for (int i=0; i<tokens.length; i++)
            mJVMArchs.add(tokens[i]);

        // 'java.util.Scanner' is available in JDK 1.5
        // Scanner s = new Scanner(archs);
        // s = s.useDelimiter("\\s+");
        // while (s.hasNext())
        //     mJVMArchs.add(s.next());
    }

    public List getLSArchitecturePriority() {
        return mLSArchitecturePriority;

    }

    /**
     * @param lsArchitecturePriority space separated LSArchitecturePriority, e.g. i386 x64_64 ppc
     */
    public void setLSArchitecturePriority(String lsArchitecturePriority) {

        // Use for 1.4 backwards compatability
        String[] tokens = lsArchitecturePriority.split("\\s+");
        for (int i=0; i<tokens.length; i++)
            mLSArchitecturePriority.add(tokens[i]);

        // 'java.util.Scanner' is available in JDK 1.5
        // Scanner s = new Scanner(lsArchitecturePriority);
        // s = s.useDelimiter("\\s+");
        // while (s.hasNext())
        //     mLSArchitecturePriority.add(s.next());
    }

    public String getSUFeedURL() {
        return mSUFeedURL;
    }

    public void setSUFeedURL(String suFeedURL) {
        this.mSUFeedURL = suFeedURL;
    }

    // New since 2015-08-05 Tobias Bley / UltraMixer
    public void setSUPublicDSAKeyFile(String file)
    {
        this.mSUPublicDSAKeyFile = file;
    }

    // New since 2015-08-05 Tobias Bley / UltraMixer
    public String getSUPublicDSAKeyFile()
    {
        return mSUPublicDSAKeyFile;
    }

    // New since 2015-08-05 Tobias Bley / UltraMixer
    public String getLSApplicationCategoryType() { return mLSApplicationCategoryType; }

    // New since 2015-08-05 Tobias Bley / UltraMixer
    public void setLSApplicationCategoryType(String lsApplicationCategoryType) { this.mLSApplicationCategoryType = lsApplicationCategoryType; }

    //------------------------------------------------------------------------------------------
}
