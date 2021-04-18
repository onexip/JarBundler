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
import org.apache.tools.ant.util.FileUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;


/**
 * <p>Write out a Java application bundle property list file. For descriptions of
 * the property list keys, see <a
 * href="https://developer.apple.com/library/ios/documentation/General/Reference/InfoPlistKeyReference/Introduction/Introduction.html"
 * >Apple docs</a>.</p>
 */
public class PropertyListWriter {


    // Our application bundle properties
    private AppBundleProperties bundleProperties;

    private double version = 1.4;

    // DOM version of Info.plist file
    private Document document = null;


    private FileUtils fileUtils = FileUtils.getFileUtils();

    /**
     * Create a new Property List writer.
     *
     * @param bundleProperties All the AppBundleProperties
     */
    public PropertyListWriter(AppBundleProperties bundleProperties) {
        this.bundleProperties = bundleProperties;
        setJavaVersion(bundleProperties.getJVMVersion());
    }

    private void setJavaVersion(String version) {

        if (version == null) {
            return;
        }

        this.version = Double.valueOf(version.substring(0, 3)).doubleValue();
    }


    public void writeFile(File fileName) throws BuildException {

        Writer writer = null;

        try {

            this.document = createDOM();
            buildDOM();

            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer trans = transFactory.newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
            trans.transform(new DOMSource(document), new StreamResult(writer));
        } catch (TransformerConfigurationException tce) {
            throw new BuildException(tce);
        } catch (TransformerException te) {
            throw new BuildException(te);
        } catch (ParserConfigurationException pce) {
            throw new BuildException(pce);
        } catch (IOException ex) {
            throw new BuildException("Unable to write  \"" + fileName + "\"");
        } finally {
            fileUtils.close(writer);
        }
    }

    private Document createDOM() throws ParserConfigurationException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        DOMImplementation domImpl = documentBuilder.getDOMImplementation();

        // We needed to reference using the full class name here because we already have
        //  a class named "DocumentType"

        org.w3c.dom.DocumentType doctype = domImpl.createDocumentType(
            "plist",
            "-//Apple Computer//DTD PLIST 1.0//EN",
            "http://www.apple.com/DTDs/PropertyList-1.0.dtd");

        return domImpl.createDocument(null, "plist", doctype);
    }


    private void buildDOM() {

        Element plist = this.document.getDocumentElement();
        plist.setAttribute("version", "1.0");

        // Open the top level dictionary, <dict>

        Node dict = createNode("dict", plist);

        // Application short name i.e. About menu name
        writeKeyStringPair("CFBundleName", bundleProperties.getCFBundleName(), dict);

        // Finder 'Version' label, defaults to "1.0"
        writeKeyStringPair("CFBundleShortVersionString", bundleProperties.getCFBundleShortVersionString(), dict);

        // macOS required key, defaults to "false"
        writeKeyStringPair("CFBundleAllowMixedLocalizations",
            (bundleProperties.getCFBundleAllowMixedLocalizations() ? "true" : "false"), dict);

        // macOS required, defaults to "6.0"
        writeKeyStringPair("CFBundleInfoDictionaryVersion",
            bundleProperties.getCFBundleInfoDictionaryVersion(), dict);

        // Bundle Executable name, required, defaults to "JavaApplicationStub"
        writeKeyStringPair("CFBundleExecutable", bundleProperties.getCFBundleExecutable(), dict);

        // Bundle Development Region, required, defaults to "English"
        writeKeyStringPair("CFBundleDevelopmentRegion", bundleProperties.getCFBundleDevelopmentRegion(), dict);

        // Bundle Package Type, required, defaults tp "APPL"
        writeKeyStringPair("CFBundlePackageType", bundleProperties.getCFBundlePackageType(), dict);

        // Bundle Signature, required, defaults tp "????"
        writeKeyStringPair("CFBundleSignature", bundleProperties.getCFBundleSignature(), dict);

        // Application build number, optional
        if (bundleProperties.getCFBundleVersion() != null) {
            writeKeyStringPair("CFBundleVersion", bundleProperties.getCFBundleVersion(), dict);
        }

        // Application Icon file, optional
        if (bundleProperties.getCFBundleIconFile() != null) {
            writeKeyStringPair("CFBundleIconFile", bundleProperties.getCFBundleIconFile(), dict);
        }

        // Bundle Identifier, optional
        if (bundleProperties.getCFBundleIdentifier() != null) {
            writeKeyStringPair("CFBundleIdentifier", bundleProperties.getCFBundleIdentifier(), dict);
        }

        // Help Book Folder, optional
        if (bundleProperties.getCFBundleHelpBookFolder() != null) {
            writeKeyStringPair("CFBundleHelpBookFolder", bundleProperties.getCFBundleHelpBookFolder(), dict);
        }

        // Help Book Name, optional
        if (bundleProperties.getCFBundleHelpBookName() != null) {
            writeKeyStringPair("CFBundleHelpBookName", bundleProperties.getCFBundleHelpBookName(), dict);
        }

        // Copyright, optional
        if (bundleProperties.getNSHumanReadableCopyright() != null) {
            writeKeyStringPair("NSHumanReadableCopyright", bundleProperties.getNSHumanReadableCopyright(), dict);
        }

        // HiRes capability, optional
        if (bundleProperties.getNSHighResolutionCapable() != false) {
            writeKeyBooleanPair("NSHighResolutionCapable", bundleProperties.getNSHighResolutionCapable(), dict);
        }

        // automatic graphics switching capability, optional
        if (bundleProperties.getNSSupportsAutomaticGraphicsSwitching()) {
            writeKeyBooleanPair("NSSupportsAutomaticGraphicsSwitching", bundleProperties.getNSSupportsAutomaticGraphicsSwitching(), dict);
        }

        // Content size, optional
        if (bundleProperties.getNSPreferencesContentSize() != null) {
            writeKeyStringPair("NSPreferencesContentSize", "{" + bundleProperties.getNSPreferencesContentSize() + "}", dict);
        }

        // IsAgent, optional
        if (bundleProperties.getLSUIElement() != null) {
            writeKeyBooleanPair("LSUIElement", bundleProperties.getLSUIElement(), dict);
        }

        // LSApplicationCategoryType, optional
        // new since 2015-08-05 by Tobias Bley / UltraMixer
        if (bundleProperties.getLSApplicationCategoryType() != null) {
            writeKeyStringPair("LSApplicationCategoryType", bundleProperties.getLSApplicationCategoryType(), dict);
        }


        // New since 2015-08-05 Tobias Bley / UltraMixer
        // LSEnvironemnt dict node
        if (bundleProperties.getLSEnvironment() != null && bundleProperties.getLSEnvironment().keySet().size() > 0) {
            writeKey("LSEnvironment", dict);
            Node lsEnvironmentDict = createNode("dict", dict);

            // Main class, required
            Enumeration keys = bundleProperties.getLSEnvironment().keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                writeKeyStringPair(key, (String) bundleProperties.getLSEnvironment().get(key), lsEnvironmentDict);
            }
        }

        // Document Types, optional
        List documentTypes = bundleProperties.getDocumentTypes();

        if (documentTypes.size() > 0) {
            writeDocumentTypes(documentTypes, dict);
        }

        // Java / JavaX entry in the plist dictionary
        writeKey(bundleProperties.getJavaXKey() ? "JavaX" : "Java", dict);
        Node javaDict = createNode("dict", dict);

        // Main class, required
        writeKeyStringPair("MainClass", bundleProperties.getMainClass(), javaDict);

        // Target JVM version, optional but recommended
        if (bundleProperties.getJVMVersion() != null) {
            writeKeyStringPair("JVMVersion", bundleProperties.getJVMVersion(), javaDict);
        }

        // New in JarBundler 2.2.0; Tobias Bley ---------------------------------

        // JVMArchs, optional
        List jvmArchs = bundleProperties.getJVMArchs();

        if (jvmArchs != null && !jvmArchs.isEmpty()) {
            writeJVMArchs(jvmArchs, javaDict);
        }

        // lsArchitecturePriority, optional
        List lsArchitecturePriority = bundleProperties.getLSArchitecturePriority();

        if (lsArchitecturePriority != null && !lsArchitecturePriority.isEmpty()) {
            writeLSArchitecturePriority(lsArchitecturePriority, javaDict);
        }

        //-----------------------------------------------------------------------


        // Classpath is composed of two types, required
        // 1: Jars bundled into the JAVA_ROOT of the application
        // 2: External directories or files with an absolute path

        List classPath = bundleProperties.getClassPath();
        List extraClassPath = bundleProperties.getExtraClassPath();

        if ((classPath.size() > 0) || (extraClassPath.size() > 0)) {
            writeClasspath(classPath, extraClassPath, javaDict);
        }


        // JVM options, optional
        if (bundleProperties.getVMOptions() != null) {
            writeKeyStringPair("VMOptions", bundleProperties.getVMOptions(), javaDict);
        }

        // Working directory, optional
        if (bundleProperties.getWorkingDirectory() != null) {
            writeKeyStringPair("WorkingDirectory", bundleProperties.getWorkingDirectory(), javaDict);
        }

        // StartOnMainThread, optional
        if (bundleProperties.getStartOnMainThread() != null) {
            writeKey("StartOnMainThread", javaDict);
            createNode(bundleProperties.getStartOnMainThread().toString(), javaDict);
        }

        // SplashFile, optional
        if (bundleProperties.getSplashFile() != null) {
            writeKeyStringPair("SplashFile", bundleProperties.getSplashFile(), javaDict);
        }

        // Main class arguments, optional
        if (bundleProperties.getArguments() != null) {
            writeKeyStringPair("Arguments", bundleProperties.getArguments(), javaDict);
        }

        // Java properties, optional
        Hashtable javaProperties = bundleProperties.getJavaProperties();

        if (javaProperties.isEmpty() == false) {
            writeJavaProperties(javaProperties, javaDict);
        }

        //by Tobias Bley / UltraMixer
        writeKeyStringPair("SUFeedURL", bundleProperties.getSUFeedURL(), dict);

        // Sparkle Properties
        // new since 2015-08-05 by Tobias Bley / UltraMixer
        writeKeyStringPair("SUPublicDSAKeyFile", bundleProperties.getSUPublicDSAKeyFile(), dict);


        // Services, optional
        List services = bundleProperties.getServices();
        if (services.size() > 0) {
            writeServices(services, dict);
        }

    }


    private void writeDocumentTypes(List documentTypes, Node appendTo) {

        writeKey("CFBundleDocumentTypes", appendTo);

        Node array = createNode("array", appendTo);

        Iterator itor = documentTypes.iterator();

        while (itor.hasNext()) {

            DocumentType documentType = (DocumentType) itor.next();

            Node documentDict = createNode("dict", array);

            writeKeyStringPair("CFBundleTypeName", documentType.getName(), documentDict);
            writeKeyStringPair("CFBundleTypeRole", documentType.getRole(), documentDict);

            File iconFile = documentType.getIconFile();

            if (iconFile != null) {
                writeKeyStringPair("CFBundleTypeIconFile", iconFile.getName(), documentDict);
            }


            List extensions = documentType.getExtensions();

            if (extensions.isEmpty() == false) {
                writeKey("CFBundleTypeExtensions", documentDict);
                writeArray(extensions, documentDict);
            }

            List osTypes = documentType.getOSTypes();

            if (osTypes.isEmpty() == false) {
                writeKey("CFBundleTypeOSTypes", documentDict);
                writeArray(osTypes, documentDict);
            }


            List mimeTypes = documentType.getMimeTypes();

            if (mimeTypes.isEmpty() == false) {
                writeKey("CFBundleTypeMIMETypes", documentDict);
                writeArray(mimeTypes, documentDict);
            }

            List UTIs = documentType.getUTIs();

            if (UTIs.isEmpty() == false) {
                writeKey("LSItemContentTypes", documentDict);
                writeArray(UTIs, documentDict);
            }

            // Only write this key if true
            if (documentType.isBundle()) {
                writeKeyStringPair("LSTypeIsPackage", "true", documentDict);
            }
        }
    }

    private void writeServices(List services, Node appendTo) {

        writeKey("NSServices", appendTo);
        Node array = createNode("array", appendTo);
        Iterator itor = services.iterator();

        while (itor.hasNext()) {
            Service service = (Service) itor.next();
            Node serviceDict = createNode("dict", array);

            String portName = service.getPortName();
            if (portName == null) {
                portName = bundleProperties.getCFBundleName();
            }

            writeKeyStringPair("NSPortName", portName, serviceDict);
            writeKeyStringPair("NSMessage", service.getMessage(), serviceDict);

            List sendTypes = service.getSendTypes();
            if (!sendTypes.isEmpty()) {
                writeKey("NSSendTypes", serviceDict);
                writeArray(sendTypes, serviceDict);
            }

            List returnTypes = service.getReturnTypes();
            if (!returnTypes.isEmpty()) {
                writeKey("NSReturnTypes", serviceDict);
                writeArray(returnTypes, serviceDict);
            }

            writeKey("NSMenuItem", serviceDict);
            Node menuItemDict = createNode("dict", serviceDict);
            writeKeyStringPair("default", service.getMenuItem(), menuItemDict);

            String keyEquivalent = service.getKeyEquivalent();
            if (null != keyEquivalent) {
                writeKey("NSKeyEquivalent", serviceDict);
                Node keyEquivalentDict = createNode("dict", serviceDict);
                writeKeyStringPair("default", keyEquivalent, keyEquivalentDict);
            }

            String userData = service.getUserData();
            if (null != userData) {
                writeKeyStringPair("NSUserData", userData, serviceDict);
            }

            String timeout = service.getTimeout();
            if (null != timeout) {
                writeKeyStringPair("NSTimeout", timeout, serviceDict);
            }
        }
    }

    private void writeClasspath(List classpath, List extraClasspath, Node appendTo) {
        writeKey("ClassPath", appendTo);
        classpath.addAll(extraClasspath);
        writeArray(classpath, appendTo);
    }


    private void writeJavaProperties(Hashtable javaProperties, Node appendTo) {

        writeKey("Properties", appendTo);

        Node propertiesDict = createNode("dict", appendTo);

        for (Iterator i = javaProperties.keySet().iterator(); i.hasNext(); ) {
            String key = (String) i.next();

            if (key.startsWith("com.apple.") && (version >= 1.4)) {
                System.out.println("Deprecated as of 1.4: " + key);
                continue;
            }

            writeKeyStringPair(key, (String) javaProperties.get(key), propertiesDict);
        }
    }

    // New in JarBundler 2.2.0; Tobias Bley ---------------------------------

    private void writeJVMArchs(List jvmArchs, Node appendTo) {
        writeKey("JVMArchs", appendTo);
        writeArray(jvmArchs, appendTo);
    }

    private void writeLSArchitecturePriority(List lsArchitecturePriority, Node appendTo) {
        writeKey("LSArchitecturePriority", appendTo);
        writeArray(lsArchitecturePriority, appendTo);
    }

    //----------------------------------------------------------------------

    private Node createNode(String tag, Node appendTo) {
        Node node = this.document.createElement(tag);
        appendTo.appendChild(node);
        return node;
    }


    private void writeKeyStringPair(String key, String string, Node appendTo) {

        if (string == null) {
            return;
        }

        writeKey(key, appendTo);
        writeString(string, appendTo);
    }


    private void writeKeyBooleanPair(String key, Boolean b, Node appendTo) {

        if (b == null) {
            return;
        }

        writeKey(key, appendTo);
        writeBoolean(b, appendTo);
    }


    private void writeKey(String key, Node appendTo) {
        Element keyNode = this.document.createElement("key");
        appendTo.appendChild(keyNode);
        keyNode.appendChild(this.document.createTextNode(key));
    }


    private void writeString(String string, Node appendTo) {
        Element stringNode = this.document.createElement("string");
        stringNode.appendChild(this.document.createTextNode(string));
        appendTo.appendChild(stringNode);
    }

    private void writeArray(List stringList, Node appendTo) {

        Node arrayNode = createNode("array", appendTo);

        for (Iterator it = stringList.iterator(); it.hasNext(); ) {
            writeString((String) it.next(), arrayNode);
        }

    }

    private void writeBoolean(Boolean b, Node appendTo) {
        Element booleanNode = null;

        if (b.booleanValue()) {
            booleanNode = this.document.createElement("true");
        } else {
            booleanNode = this.document.createElement("false");

        }
        appendTo.appendChild(booleanNode);
    }
}
