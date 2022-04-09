package io.github.gotchamana.magic;

import java.nio.Buffer;
import java.util.logging.*;

import com.sun.jna.*;
import com.sun.jna.platform.unix.LibCAPI.size_t;
import com.sun.jna.ptr.*;

interface MagicLibrary extends Library {

    MagicLibrary INSTANCE = loadLibrary();

    /**
     * No flags 
     */
    int MAGIC_NONE = 0x0000000;

    /**
     * Turn on debugging 
     */
    int MAGIC_DEBUG = 0x0000001;

    /**
     * Follow symlinks 
     */
    int MAGIC_SYMLINK = 0x0000002;

    /**
     * Check inside compressed files
     */
    int MAGIC_COMPRESS = 0x0000004;

    /**
     * Look at the contents of devices
     */
    int MAGIC_DEVICES = 0x0000008;

    /**
     * Return the MIME type
     */
    int MAGIC_MIME_TYPE = 0x0000010;

    /**
     * Return all matches
     */
    int MAGIC_CONTINUE = 0x0000020;

    /**
     * Print warnings to stderr
     */
    int MAGIC_CHECK = 0x0000040;

    /**
     * Restore access time on exit
     */
    int MAGIC_PRESERVE_ATIME = 0x0000080;

    /**
     * Don't convert unprintable chars
     */
    int MAGIC_RAW = 0x0000100;

    /**
     * Handle ENOENT etc as real errors
     */
    int MAGIC_ERROR = 0x0000200;

    /**
     * Return the MIME encoding
     */
    int MAGIC_MIME_ENCODING = 0x0000400;

    int MAGIC_MIME = MAGIC_MIME_TYPE | MAGIC_MIME_ENCODING;

    /**
     * Return the Apple creator/type
     */
    int MAGIC_APPLE = 0x0000800;

    /**
     * Return a /-separated list of extensions
     */
    int MAGIC_EXTENSION = 0x1000000;

    /**
     * Check inside compressed files but not report compression
     */
    int MAGIC_COMPRESS_TRANSP = 0x2000000;

    int MAGIC_NODESC = MAGIC_EXTENSION | MAGIC_MIME | MAGIC_APPLE;

    /**
     * Don't check for compressed files
     */
    int MAGIC_NO_CHECK_COMPRESS = 0x0001000;

    /**
     * Don't check for tar files
     */
    int MAGIC_NO_CHECK_TAR = 0x0002000;

    /**
     * Don't check magic entries
     */
    int MAGIC_NO_CHECK_SOFT = 0x0004000;

    /**
     * Don't check application type
     */
    int MAGIC_NO_CHECK_APPTYPE = 0x0008000;

    /**
     * Don't check for elf details
     */
    int MAGIC_NO_CHECK_ELF = 0x0010000;

    /**
     * Don't check for text files
     */
    int MAGIC_NO_CHECK_TEXT = 0x0020000;

    /**
     * Don't check for cdf files
     */
    int MAGIC_NO_CHECK_CDF = 0x0040000;

    /**
     * Don't check for CSV files
     */
    int MAGIC_NO_CHECK_CSV = 0x0080000;

    /**
     * Don't check tokens
     */
    int MAGIC_NO_CHECK_TOKENS = 0x0100000;

    /**
     * Don't check text encodings
     */
    int MAGIC_NO_CHECK_ENCODING = 0x0200000;

    /**
     * Don't check for JSON files
     */
    int MAGIC_NO_CHECK_JSON = 0x0400000;

    int MAGIC_PARAM_INDIR_MAX = 0;

    int MAGIC_PARAM_NAME_MAX = 1;

    int MAGIC_PARAM_ELF_PHNUM_MAX = 2;

    int MAGIC_PARAM_ELF_SHNUM_MAX = 3;

    int MAGIC_PARAM_ELF_NOTES_MAX = 4;

    int MAGIC_PARAM_REGEX_MAX = 5;

    int MAGIC_PARAM_BYTES_MAX = 6;

    /**
     * <p>Create a magic cookie pointer and returns it.
     * 
     * <p>It returns NULL if there was an error allocating the magic cookie.
     * 
     * @param flags
     * @return cookie pointer
     */
    Pointer magic_open(int flags);

    /**
     * Close the magic database and deallocates any resources used.
     * 
     * @param cookie
     */
    void magic_close(Pointer cookie);

    /**
     * <p>Return a textual description of the contents of the filename argument, or
     * NULL if an error occurred.
     * 
     * <p>If the filename is NULL, then stdin is used.
     * 
     * @param cookie
     * @param path file name
     * @return file description
     */
    String magic_file(Pointer cookie, String path);

    /**
     * Return a textual description of the contents of the buffer argument with
     * length bytes size.
     * 
     * @param cookie
     * @param buffer
     * @param length
     * @return file description
     */
    String magic_buffer(Pointer cookie, Buffer buffer, size_t length);

    /**
     * Return a value representing current flags set.
     * 
     * @param cookie
     * @return flags
     */
    int magic_getflags(Pointer cookie);

    /**
     * <p>Set the flags described above.
     * 
     * <p>Note that using both MIME flags together can also return extra information on
     * the charset.
     * 
     * @param cookie
     * @param flags
     * @return
     */
    int magic_setflags(Pointer cookie, int flags);

    /**
     * <p>Return the version number of this library which is compiled into the shared
     * library using the constant MAGIC_VERSION from <magic.h>.
     * 
     * <p>This can be used by client programs to verify that the version they compile
     * against is the same as the version that they run against.
     * 
     * @return magic version
     */
    int magic_version();

    /**
     * Must be used to load the colon separated list of database files passed in as
     * filename, or NULL for the default database file before any magic queries can
     * performed.
     * 
     * @param cookie
     * @param path database path
     * @return
     */
    int magic_load(Pointer cookie, String path);

    /**
     * <p>Take an array of size nbuffers of buffers with a respective size for each in
     * the array of sizes loaded with the contents of the magic databases from the
     * filesystem.
     * 
     * <p>This function can be used in environment where the magic library does not
     * have direct access to the filesystem, but can access the magic database via
     * shared memory or other IPC means.
     * 
     * @param cookie
     * @param buffers
     * @param sizes
     * @param nBuffers
     * @return
     */
    int magic_load_buffers(Pointer cookie, PointerByReference buffers, size_t[] sizes, size_t nBuffers);

    /**
     * <p>Can be used to compile the colon separated list of database files passed in
     * as filename, or NULL for the default database. It returns 0 on success and -1 on failure.
     * 
     * <p>The compiled files created are named from the basename of each file argument
     * with ".mgc" appended to it.
     * 
     * @param cookie
     * @param path database path
     * @return 0 on success, -1 on failure
     */
    int magic_compile(Pointer cookie, String path);

    /**
     * Can be used to check the validity of entries in the colon separated database
     * files passed in as filename, or NULL for the default database. It returns 0
     * on success and -1 on failure.
     * 
     * @param cookie
     * @param path database path
     * @return 0 on success, -1 on failure
     */
    int magic_check(Pointer cookie, String path);

    int magic_list(Pointer cookie, String path);

    /**
     * Return a textual explanation of the last error, or NULL if there was no
     * error.
     * 
     * @param cookie
     * @return error description
     */
    String magic_error(Pointer cookie);

    /**
     * Return the last operating system error number that was encountered by a
     * system call.
     * 
     * @param cookie
     * @return error number
     */
    int magic_errno(Pointer cookie);

    /**
     * Set various limits related to the magic library.
     * 
     * @param cookie
     * @param param
     * @param value
     * @return
     */
    int magic_setparam(Pointer cookie, int param, ByReference value);

    /**
     * Get various limits related to the magic library.
     * 
     * @param cookie
     * @param param
     * @param value
     * @return
     */
    int magic_getparam(Pointer cookie, int param, ByReference value);

    private static MagicLibrary loadLibrary() {
        final var log = Logger.getLogger(MagicLibrary.class.getName());

        var name = System.getProperty("magic.library.name");

        if (name == null || name.isBlank())
            name = System.getenv("MAGIC_LIBRARY_NAME");
        
        if (name == null || name.isBlank())
            name = "magic";

        log.log(Level.FINE, "Native magic library name is {0}", name);

        return Native.load(name, MagicLibrary.class);
    }
}
