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


package net.sourceforge.jarbundler.example;

public class HelpBook {
	static {
		System.loadLibrary("HelpBookJNI");
	}

	public static native void launchHelpViewer();
}
