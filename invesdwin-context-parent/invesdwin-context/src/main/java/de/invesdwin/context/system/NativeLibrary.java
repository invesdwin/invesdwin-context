package de.invesdwin.context.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.log.Log;
import de.invesdwin.util.lang.OperatingSystem;

/**
 * This class can be added as a private static final member in a class so that during initialization of the object the
 * native library gets loaded only once. Garbage Collection causes the maybe temporarily extracted library to be deleted
 * maybe.
 * 
 * This algorithm has been taken from JNA. It bases on extracting native libraries on demand from the classpath nad
 * loading them afterwards. When they are not needed anymore, they get deleted.
 * 
 * @author subes
 * 
 */
// CHECKSTYLE:OFF relevant stuff copied from the JNA Native.java
@NotThreadSafe
public final class NativeLibrary {

    private final String libname;
    private final String packagePath;
    private final Class<?> classFromSameJar;
    private boolean unpacked;
    private String nativeLibraryPath = null;

    public NativeLibrary(final String libname, final String packagePath, final Class<?> classFromSameJar) {
        this.libname = libname;
        this.packagePath = packagePath;
        this.classFromSameJar = classFromSameJar;
        loadNativeLibrary();
    }

    private void loadNativeLibrary() {
        final String libName = libname;
        final String bootPath = System.getProperty("jna.boot.library.path");
        if (bootPath != null) {
            final String[] dirs = bootPath.split(File.pathSeparator);
            for (int i = 0; i < dirs.length; ++i) {
                String path = new File(new File(dirs[i]), System.mapLibraryName(libName)).getAbsolutePath();
                try {
                    System.load(path);
                    nativeLibraryPath = path;
                    return;
                } catch (final UnsatisfiedLinkError ex) {
                }
                if (OperatingSystem.isMac()) {
                    String orig;
                    String ext;
                    if (path.endsWith("dylib")) {
                        orig = "dylib";
                        ext = "jnilib";
                    } else {
                        orig = "jnilib";
                        ext = "dylib";
                    }
                    try {
                        path = path.substring(0, path.lastIndexOf(orig)) + ext;
                        System.load(path);
                        nativeLibraryPath = path;
                        return;
                    } catch (final UnsatisfiedLinkError ex) {
                    }
                }
            }
        }
        try {
            System.loadLibrary(libName);
            nativeLibraryPath = libName;
        } catch (final UnsatisfiedLinkError e) {
            loadNativeLibraryFromJar();
        }
    }

    /**
     * Attempts to load the native library resource from the filesystem, extracting the JNA stub library from jna.jar if
     * not already available.
     */
    private void loadNativeLibraryFromJar() {
        final String libname = System.mapLibraryName(this.libname);
        final String arch = System.getProperty("os.arch");
        final String name = System.getProperty("os.name");
        String resourceName = getNativeLibraryResourcePath(OperatingSystem.getCurrent(), arch, name) + "/" + libname;
        URL url = classFromSameJar.getResource(resourceName);

        // Add an ugly hack for OpenJDK (soylatte) - JNI libs use the usual
        // .dylib extension
        if (url == null && OperatingSystem.isMac() && resourceName.endsWith(".dylib")) {
            resourceName = resourceName.substring(0, resourceName.lastIndexOf(".dylib")) + ".jnilib";
            url = NativeLibrary.class.getResource(resourceName);
        }
        if (url == null) {
            throw new UnsatisfiedLinkError(libname + " (" + resourceName
                    + ") not found in resource path. Seems like the os/arch is unsupported.");
        }

        File lib = null;
        if ("file".equals(url.getProtocol().toLowerCase())) {
            try {
                lib = new File(url.toURI());
            } catch (final URISyntaxException e) {
                lib = new File(url.getPath());
            }
            if (!lib.exists()) {
                throw new Error("File URL " + url + " could not be properly decoded");
            }
        } else {
            final InputStream is = NativeLibrary.class.getResourceAsStream(resourceName);
            if (is == null) {
                throw new Error("Can't obtain " + libname + " InputStream");
            }

            FileOutputStream fos = null;
            try {
                // Suffix is required on windows, or library fails to load
                // Let Java pick the suffix, except on windows, to avoid
                // problems with Web Start.
                lib = File.createTempFile("investemp_", OperatingSystem.isWindows() ? ".dll" : null);
                lib.deleteOnExit();
                final ClassLoader cl = NativeLibrary.class.getClassLoader();
                if (OperatingSystem.isWindows() && (cl == null || cl.equals(ClassLoader.getSystemClassLoader()))) {
                    Runtime.getRuntime().addShutdownHook(new DeleteNativeLibrary(lib, nativeLibraryPath, unpacked));
                }
                fos = new FileOutputStream(lib);
                int count;
                final byte[] buf = new byte[1024];
                while ((count = is.read(buf, 0, buf.length)) > 0) {
                    fos.write(buf, 0, count);
                }
            } catch (final IOException e) {
                throw new Error("Failed to create temporary file for " + libname + " library: " + e);
            } finally {
                try {
                    is.close();
                } catch (final IOException e) {
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (final IOException e) {
                    }
                }
            }
            unpacked = true;
        }
        System.load(lib.getAbsolutePath());
        nativeLibraryPath = lib.getAbsolutePath();
    }

    private String getNativeLibraryResourcePath(final OperatingSystem osType, String arch, final String name) {
        String osPrefix;
        arch = arch.toLowerCase();
        switch (osType) {
        case WINDOWS:
            if ("i386".equals(arch)) {
                arch = "x86";
            }
            osPrefix = "win32-" + arch;
            break;
        case MAC:
            osPrefix = "darwin";
            break;
        case LINUX:
            if ("x86".equals(arch)) {
                arch = "i386";
            } else if ("x86_64".equals(arch)) {
                arch = "amd64";
            }
            osPrefix = "linux-" + arch;
            break;
        case SOLARIS:
            osPrefix = "sunos-" + arch;
            break;
        default:
            osPrefix = name.toLowerCase();
            if ("x86".equals(arch)) {
                arch = "i386";
            }
            if ("x86_64".equals(arch)) {
                arch = "amd64";
            }
            if ("powerpc".equals(arch)) {
                arch = "ppc";
            }
            final int space = osPrefix.indexOf(" ");
            if (space != -1) {
                osPrefix = osPrefix.substring(0, space);
            }
            osPrefix += "-" + arch;
            break;
        }
        return packagePath + osPrefix;
    }

    private static boolean deleteNativeLibrary(String nativeLibraryPath, boolean unpacked) {
        final String path = nativeLibraryPath;
        if (path == null || !unpacked) {
            return true;
        }
        final File flib = new File(path);
        if (flib.delete()) {
            nativeLibraryPath = null;
            unpacked = false;
            return true;
        }
        // Reach into the bowels of ClassLoader to force the native
        // library to unload
        try {
            final ClassLoader cl = NativeLibrary.class.getClassLoader();
            Field f = ClassLoader.class.getDeclaredField("nativeLibraries");
            f.setAccessible(true);
            final List<?> libs = (List<?>) f.get(cl);
            for (final Iterator<?> i = libs.iterator(); i.hasNext();) {
                final Object lib = i.next();
                f = lib.getClass().getDeclaredField("name");
                f.setAccessible(true);
                final String name = (String) f.get(lib);
                if (name.equals(path) || name.indexOf(path) != -1) {
                    final Method m = lib.getClass().getDeclaredMethod("finalize", new Class[0]);
                    m.setAccessible(true);
                    m.invoke(lib, new Object[0]);
                    nativeLibraryPath = null;
                    if (unpacked) {
                        if (flib.exists()) {
                            if (flib.delete()) {
                                unpacked = false;
                                return true;
                            }
                            return false;
                        }
                    }
                    return true;
                }
            }
        } catch (final NoSuchFieldException e) {
        } catch (final IllegalArgumentException e) {
        } catch (final IllegalAccessException e) {
        } catch (final SecurityException e) {
        } catch (final NoSuchMethodException e) {
        } catch (final InvocationTargetException e) {
        }
        return false;
    }

    public static class DeleteNativeLibrary extends Thread {
        private final File file;
        private final String nativeLibraryPath;
        private final boolean unpacked;

        public DeleteNativeLibrary(final File file, final String nativeLibraryPath, final boolean unpacked) {
            this.file = file;
            this.nativeLibraryPath = nativeLibraryPath;
            this.unpacked = unpacked;
        }

        @Override
        public void run() {
            // If we can't force an unload/delete, spawn a new process
            // to do so
            if (!deleteNativeLibrary(nativeLibraryPath, unpacked)) {
                try {
                    Runtime.getRuntime()
                            .exec(new String[] {
                                    System.getProperty("java.home") + File.separator + "bin" + File.separator + "java",
                                    "-cp", System.getProperty("java.class.path"), getClass().getName(),
                                    file.getAbsolutePath(), });
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public static void main(final String[] args) {
            if (args.length == 1) {
                final File file = new File(args[0]);
                if (file.exists()) {
                    final long start = System.currentTimeMillis();
                    while (!file.delete() && file.exists()) {
                        try {
                            Thread.sleep(10);
                        } catch (final InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        if (System.currentTimeMillis() - start > 5000) {
                            new Log(DeleteNativeLibrary.class)
                                    .error("Could not remove temp file: " + file.getAbsolutePath());
                            break;
                        }
                    }
                }
            }
            System.exit(0);
        }
    }

    @Override
    protected void finalize() {
        deleteNativeLibrary(nativeLibraryPath, unpacked);
    }

}
