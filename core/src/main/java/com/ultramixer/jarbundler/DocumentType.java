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


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * <p>Represents an Info.plist DocumentType used for associating a document with
 * the application</p>
 * <p>The Document Types allows you to specify which documents your finished
 * product can handle. You should list the application's primary document type
 * first because the document controller uses that type by default when the user
 * requests a new document.</p>
 *
 * <dl>
 * <dt>Name</dt>
 * <dd>The name of the document type.</dd>
 *
 * <dt>UTI</dt>
 * <dd>A list of Uniform Type Identifier (UTI) strings for the document. UTIs
 * are strings that uniquely identify abstract types. They can be used to
 * describe a file format or data type but can also be used to describe type
 * information for other sorts of entities, such as directories, volumes, or
 * packages. For more information on UTIs, see the header file UTType.h,
 * available as part of LaunchServices.framework in Mac OS X v10.3 and later.</dd>
 *
 * <dt>Extensions</dt>
 * <dd>A list of the filename extensions for this document type. Don't
 * include the period in the extension.</dd>
 *
 * <dt>OS Types</dt>
 * <dd>A list of four-letter codes for the document. These codes are
 * stored in the document's resources or information property list files.</dd>
 *
 * <dt>MIME Types</dt>
 * <dd>A list of the Multipurpose Internet Mail Extensions (MIME) types
 * for the document. MIME types identify content types for Internet
 * applications.</dd>
 *
 * <dt>Icon File</dt>
 * <dd>The name of the file that contains the document type's icon.</dd>
 *
 * <dt>Role</dt>
 * <dd>A description of how the application uses the documents of this type.
 * <ul>
 * <li>Editor - The application can display, edit, and save documents of this type.</li>
 * <li>Viewer - The application can display, but not edit, documents of this type.</li>
 * <li>Shell - The application provides runtime services for other processes for
 * example, a Java applet viewer.</li>
 * <li>None - The application can neither display nor edit documents of this type
 * but instead uses them in some other way. For example, Sketch uses this role
 * to declare types it can export but not read.</li>
 * </ul></dd>
 *
 * <dt>Bundle</dt>
 * <dd>Specifies whether the document is a single file or a file bundle,
 * that is, a directory that is treated as a single document by certain
 * applications, such as the Finder.</dd>
 * </dl>
 *
 * <p>Example:</p>
 * <pre>
 * &lt;documenttype name="Scan Project" extensions="scansort scanproj"
 * ostypes="fold disk fdrp" iconfile="document.icns" mimetypes="text/html
 * image/jpeg" role="editor" bundle="true" /&gt;
 * </pre>
 */
public class DocumentType
{

    private static final List EMPTYLIST = new ArrayList(0);

    /**
     * Name. The name of the document type.
     */
    public String name = null;

    /**
     * Extensions. A list of the filename extensions for this document type.
     * Don't include the period in the extension.
     */
    public String[] extensions = null;

    /**
     * OS Types. A list of four-letter codes for the document. These codes are
     * stored in the document's resources or information property list files.
     */
    public String[] osTypes = null;
    /**
     * MIME Types. A list of the Multipurpose Internet Mail Extensions (MIME)
     * types for the document. MIME types identify content types for Internet
     * applications.
     */

    public String[] mimeTypes = null;

    /**
     * UTI. A list of Uniform Type Identifier (UTI) strings for the document.
     * UTIs are strings that uniquely identify abstract types. They can be used
     * to describe a file format or data type but can also be used to describe
     * type information for other sorts of entities, such as directories,
     * volumes, or packages. For more information on UTIs, see the header file
     * UTType.h, available as part of LaunchServices.framework in Mac OS X v10.3
     * and later.
     */
    public String[] UTIs = null;

    /**
     * Icon File. The name of the file that contains the document types icon.
     */
    public File iconFile = null;

    /**
     * Role. A description of how the application uses the documents of this
     * type. You can choose from four values:
     * <ul>
     * <li>Editor. The application can display, edit, and save documents of this
     * type.</li>
     * <li>Viewer. The application can display, but not edit, documents of this
     * type.</li>
     * <li>Shell. The application provides runtime services for other processesfor
     * example, a Java applet viewer.</li>
     * <li>None. The application can neither display nor edit documents of this type
     * but instead uses them in some other way. For example, Sketch uses this
     * role to declare types it can export but not read.</li>
     * </ul>
     */
    public String role = null;

    /**
     * Bundle. Specifies whether the document is a single file document or a
     * document bundle, that is, a directory that is treated as a single
     * document by certain applications, such as the Finder.
     */
    public boolean isBundle = false;

    public String getName()
    {
        return name;
    }

    // Document type name
    public void setName(String name)
    {
        this.name = name;
    }

    public List getExtensions()
    {
        return (extensions == null) ? EMPTYLIST : Arrays.asList(extensions);
    }

    // Extensions
    public void setExtensions(String extensions)
    {
        this.extensions = extensions.split("[\\s,]");
    }

    public List getOSTypes()
    {
        return (osTypes == null) ? EMPTYLIST : Arrays.asList(osTypes);
    }

    // OS Types
    public void setOSTypes(String osTypes)
    {
        this.osTypes = osTypes.split("[\\s,]");
    }

    public List getMimeTypes()
    {
        return (mimeTypes == null) ? EMPTYLIST : Arrays.asList(this.mimeTypes);
    }

    // mime-types
    public void setMimeTypes(String mimeTypes)
    {
        this.mimeTypes = mimeTypes.split("[\\s,]");
    }

    public List getUTIs()
    {
        return this.UTIs == null ? EMPTYLIST : Arrays.asList(this.UTIs);
    }

    // Uniform Type Identifiers
    public void setUTIs(String UTIs)
    {
        this.UTIs = UTIs.split("[\\s,]");
    }

    public File getIconFile()
    {
        return iconFile;
    }

    // Document icon file
    public void setIconFile(File iconFile)
    {
        this.iconFile = iconFile;
    }

    public String getRole()
    {
        return role;
    }

    // Document role
    public void setRole(String role)
    {
        this.role = role;
    }

    public boolean isBundle()
    {
        return isBundle;
    }

    // Is this document represented as a bundle
    public void setBundle(boolean isBundle)
    {
        this.isBundle = isBundle;
    }

}
