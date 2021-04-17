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

/*
**    Java-JNI bridge for involking Apple Help Viewer
**
**    The library name is the name of the JNI Library file which must be created and
**    placed in the Contents/Java folder of the application bundle.
**
**    This filename is created by prepending 'lib' and append '.jnilib' as a file extension.
**
**    Thus, the target of loadLibrary("HelpBookJNI") will the file named:
**
**             Content/Java/libHelpBookJNI.jnilib
*/


package com.ultramixer.jarbundler.example;

public class HelpBook {
    static {
        System.loadLibrary("HelpBookJNI");
    }

    public static native void launchHelpViewer();
}
