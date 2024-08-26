package com.amuse.permit.wrapper.file;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amuse.permit.Instance;
import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.NameFilters;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.process.ServiceProcess;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.ProcessRoute;
import com.amuse.permit.process.action.ClientAction;
import com.amuse.permit.process.action.ResultCreator;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * An abstract representation of file and directory pathnames.
 *
 * @see java.io.File
 */
@Annotations.RequesterSide
@SuppressWarnings("unused")
public class File extends FileModel {

    @JsonIgnore
    protected Context context;

    /**
     * Creates a new <code>File</code> instance by converting the given
     * pathname string into an abstract pathname.  If the given string is
     * the empty string, then the result is the empty abstract pathname.
     *
     * @see     java.io.File#File(String)
     * @param   context Application context instance
     * @param   pathname  A pathname string
     */
    @Annotations.Constructor
    public static ResultTask<File> fileFrom(Context context, @NonNull String pathname) {
        ResultTask<File> resultTask = new ResultTask<>();
        resultTask.mOnInvokeAttached = result -> {
            PacketData packetData = new PacketData();
            ArgsInfo argsInfo = new ArgsInfo();

            packetData.ticketId = String.format("file@%s_%s", pathname, System.currentTimeMillis());
            packetData.fromPackageName = Instance.getInstance().getServerPeer().getPackageName();
            argsInfo.put(pathname);

            ClientAction clientAction = new ClientAction(context, packetData);
            clientAction.pushClass(ProcessConst.ACTION_TYPE_FILE, argsInfo);

            ProcessRoute.registerInnerResultTask(context, packetData.ticketId, resultTask);
            clientAction.send();
        };
        return resultTask;
    }

    /**
     * Creates a new <code>File</code> instance from a parent pathname string
     * and a child pathname string.
     *
     * <p> If <code>parent</code> is <code>null</code> then the new
     * <code>File</code> instance is created as if by invoking the
     * single-argument <code>File</code> constructor on the given
     * <code>child</code> pathname string.
     *
     * <p> Otherwise the <code>parent</code> pathname string is taken to denote
     * a directory, and the <code>child</code> pathname string is taken to
     * denote either a directory or a file.  If the <code>child</code> pathname
     * string is absolute then it is converted into a relative pathname in a
     * system-dependent way.  If <code>parent</code> is the empty string then
     * the new <code>File</code> instance is created by converting
     * <code>child</code> into an abstract pathname and resolving the result
     * against a system-dependent default directory.  Otherwise each pathname
     * string is converted into an abstract pathname and the child abstract
     * pathname is resolved against the parent.
     *
     * @see     java.io.File#File(String, String)
     * @param   context Application context instance
     * @param   parent  The parent pathname string
     * @param   child   The child pathname string
     */
    @Annotations.Constructor
    public static ResultTask<File> fileFrom(Context context, @Nullable String parent, @NonNull String child) {
        return fileFrom(context, parent + child);
    }

    /**
     * Creates a new <code>File</code> instance from a parent abstract
     * pathname and a child pathname string.
     *
     * <p> If <code>parent</code> is <code>null</code> then the new
     * <code>File</code> instance is created as if by invoking the
     * single-argument <code>File</code> constructor on the given
     * <code>child</code> pathname string.
     *
     * <p> Otherwise the <code>parent</code> abstract pathname is taken to
     * denote a directory, and the <code>child</code> pathname string is taken
     * to denote either a directory or a file.  If the <code>child</code>
     * pathname string is absolute then it is converted into a relative
     * pathname in a system-dependent way.  If <code>parent</code> is the empty
     * abstract pathname then the new <code>File</code> instance is created by
     * converting <code>child</code> into an abstract pathname and resolving
     * the result against a system-dependent default directory.  Otherwise each
     * pathname string is converted into an abstract pathname and the child
     * abstract pathname is resolved against the parent.
     *
     * @see     java.io.File#File(java.io.File, String)
     * @param   context Application context instance
     * @param   parent  The parent abstract pathname
     * @param   child   The child pathname string
     * @throws IllegalArgumentException
     *      If <code>parent</code> is <code>null</code>
     */
    @Annotations.Constructor
    public static ResultTask<File> fileFrom(Context context, @Nullable File parent, @NonNull String child) {
        if (parent != null && parent.checkIsFetched()) {
            return fileFrom(context, parent.getAbsolutePath() + child);
        }
        throw new IllegalStateException("Parent directory file object is not initialized");
    }

    /**
     * Creates a new <tt>File</tt> instance by converting the given
     * <tt>file:</tt> URI into an abstract pathname.
     *
     * <p> The exact form of a <tt>file:</tt> URI is system-dependent, hence
     * the transformation performed by this constructor is also
     * system-dependent.
     *
     * <p> For a given abstract pathname <i>f</i> it is guaranteed that
     *
     * <blockquote><tt>
     * new File(</tt><i>&nbsp;f</i><tt>.{@link #toURI() toURI}()).equals(</tt><i>&nbsp;f</i><tt>.{@link #getAbsolutePath()})
     * </tt></blockquote>
     *
     * so long as the original abstract pathname, the URI, and the new abstract
     * pathname are all created in (possibly different invocations of) the same
     * Java virtual machine.  This relationship typically does not hold,
     * however, when a <tt>file:</tt> URI that is created in a virtual machine
     * on one operating system is converted into an abstract pathname in a
     * virtual machine on a different operating system.
     *
     * @param  uri
     *         An absolute, hierarchical URI with a scheme equal to
     *         <tt>"file"</tt>, a non-empty path component, and undefined
     *         authority, query, and fragment components
     *
     * @throws  IllegalArgumentException
     *          If the preconditions on the parameter do not hold
     *
     * @see     java.io.File#File(java.net.URI)
     * @see #toURI()
     * @see java.net.URI
     */
    @Annotations.Constructor
    public static ResultTask<File> fileFrom(Context context, @NonNull URI uri) {
        return fileFrom(context, uri.getPath());
    }

    /**
     * Creates a new <code>File</code> instance without any path-associated attributes
     * to use static methods, like {@link #listRoots()} or {@link #createTempFile(String, String)}
     *
     * @param   context Application context instance
     */
    @Annotations.StaticMethod
    @Annotations.Constructor
    public static File fileStatic(Context context) {
        File file = new File();
        file.context = context;
        return file;
    }

    /**
     * Tests whether the application can execute the file denoted by this
     * abstract pathname.
     *
     * @return  <code>true</code> if and only if the abstract pathname exists
     *          <em>and</em> the application is allowed to execute the file
     * @see     java.io.File#canExecute()
     */
    @JsonGetter("canExecute")
    public boolean canExecute() {
        return canExecute;
    }

    /**
     * Tests whether the application can read the file denoted by this
     * abstract pathname.
     *
     * @return  <code>true</code> if and only if the file specified by this
     *          abstract pathname exists <em>and</em> can be read by the
     *          application; <code>false</code> otherwise
     *
     * @see     java.io.File#canRead() (java.net.URI)
     */
    @JsonGetter("canRead")
    public boolean canRead() {
        return canRead;
    }

    /**
     * Tests whether the application can modify the file denoted by this
     * abstract pathname.
     *
     * @return  <code>true</code> if and only if the file system actually
     *          contains a file denoted by this abstract pathname <em>and</em>
     *          the application is allowed to write to the file;
     *          <code>false</code> otherwise.
     *
     * @see     java.io.File#canWrite()
     */
    @JsonGetter("canWrite")
    public boolean canWrite() {
        return canWrite;
    }

    /**
     * Tests whether the file or directory denoted by this abstract pathname
     * exists.
     *
     * @return  <code>true</code> if and only if the file or directory denoted
     *          by this abstract pathname exists; <code>false</code> otherwise
     *
     * @see     java.io.File#exists()
     */
    @JsonGetter("exists")
    public boolean exists() {
        return exists;
    }

    /**
     * Returns the absolute path of this file. An absolute path is a path that starts at a root
     * of the file system. On Android, there is only one root: {@code /}.
     *
     * <p>A common use for absolute paths is when passing paths to a {@code Process} as
     * command-line arguments, to remove the requirement implied by relative paths, that the
     * child must have the same working directory as its parent.
     *
     * @return  The absolute pathname string denoting the same file or
     *          directory as this abstract pathname
     *
     * @see   #isAbsolute()
     * @see     java.io.File#getAbsolutePath()
     */
    @JsonGetter("absolutePath")
    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * Returns the canonical pathname string of this abstract pathname.
     *
     * <p> A canonical pathname is both absolute and unique.  The precise
     * definition of canonical form is system-dependent.  This method first
     * converts this pathname to absolute form if necessary, as if by invoking the
     * {@link #getAbsolutePath} method, and then maps it to its unique form in a
     * system-dependent way.  This typically involves removing redundant names
     * such as <tt>"."</tt> and <tt>".."</tt> from the pathname, resolving
     * symbolic links (on UNIX platforms), and converting drive letters to a
     * standard case (on Microsoft Windows platforms).
     *
     * @return  The canonical pathname string denoting the same file or
     *          directory as this abstract pathname
     * @see     java.io.File#getCanonicalPath()
     */
    @JsonGetter("canonicalPath")
    public String getCanonicalPath() {
        return canonicalPath;
    }

    /**
     * Returns the number of unallocated bytes in the partition <a
     * href="#partName">named</a> by this abstract path name.
     *
     * <p> The returned number of unallocated bytes is a hint, but not
     * a guarantee, that it is possible to use most or any of these
     * bytes.  The number of unallocated bytes is most likely to be
     * accurate immediately after this call.  It is likely to be made
     * inaccurate by any external I/O operations including those made
     * on the system outside of this virtual machine.  This method
     * makes no guarantee that write operations to this file system
     * will succeed.
     *
     * @return  The number of unallocated bytes on the partition or <tt>0L</tt>
     *          if the abstract pathname does not name a partition.  This
     *          value will be less than or equal to the total file system size
     *          returned by {@link #getTotalSpace}.
     * @see java.io.File#getFreeSpace()
     */
    @JsonGetter("freeSpace")
    public long getFreeSpace() {
        return freeSpace;
    }

    /**
     * Returns the name of the file or directory denoted by this abstract
     * pathname.  This is just the last name in the pathname's name
     * sequence.  If the pathname's name sequence is empty, then the empty
     * string is returned.
     *
     * @return  The name of the file or directory denoted by this abstract
     *          pathname, or the empty string if this pathname's name sequence
     *          is empty
     * @see java.io.File#getName()
     */
    @JsonGetter("name")
    public String getName() {
        return name;
    }

    /**
     * Returns the pathname string of this abstract pathname's parent, or
     * <code>null</code> if this pathname does not name a parent directory.
     *
     * <p> The <em>parent</em> of an abstract pathname consists of the
     * pathname's prefix, if any, and each name in the pathname's name
     * sequence except for the last.  If the name sequence is empty then
     * the pathname does not name a parent directory.
     *
     * @return  The pathname string of the parent directory named by this
     *          abstract pathname, or <code>null</code> if this pathname
     *          does not name a parent
     * @see java.io.File#getParent
     */
    @JsonGetter("parent")
    public String getParent() {
        return parent;
    }

    /**
     * Converts this abstract pathname into a pathname string.  The resulting
     * string uses the separator default name-separator character to
     * separate the names in the name sequence.
     *
     * @return  The string form of this abstract pathname
     * @see java.io.File#getPath()
     */
    @JsonGetter("path")
    public String getPath() {
        return path;
    }

    /**
     * Returns the size of the partition <a href="#partName">named</a> by this
     * abstract pathname.
     *
     * @return  The size, in bytes, of the partition or <tt>0L</tt> if this
     *          abstract pathname does not name a partition
     *
     * @see java.io.File#getTotalSpace()
     */
    @JsonGetter("totalSpace")
    public long getTotalSpace() {
        return totalSpace;
    }

    /**
     * Returns the number of bytes available to this virtual machine on the
     * partition <a href="#partName">named</a> by this abstract pathname.  When
     * possible, this method checks for write permissions and other operating
     * system restrictions and will therefore usually provide a more accurate
     * estimate of how much new data can actually be written than {@link
     * #getFreeSpace}.
     *
     * <p> The returned number of available bytes is a hint, but not a
     * guarantee, that it is possible to use most or any of these bytes.  The
     * number of unallocated bytes is most likely to be accurate immediately
     * after this call.  It is likely to be made inaccurate by any external
     * I/O operations including those made on the system outside of this
     * virtual machine.  This method makes no guarantee that write operations
     * to this file system will succeed.
     *
     * <p> On Android (and other Unix-based systems), this method returns the number of free bytes
     * available to non-root users, regardless of whether you're actually running as root,
     * and regardless of any quota or other restrictions that might apply to the user.
     * (The {@code getFreeSpace} method returns the number of bytes potentially available to root.)
     *
     * @return  The number of available bytes on the partition or <tt>0L</tt>
     *          if the abstract pathname does not name a partition.  On
     *          systems where this information is not available, this method
     *          will be equivalent to a call to {@link #getFreeSpace}.
     **
     * @see java.io.File#getUsableSpace()
     */
    @JsonGetter("usableSpace")
    public long getUsableSpace() {
        return usableSpace;
    }

    /**
     * Tells whether or not this URI is absolute.
     *
     * <p> A URI is absolute if, and only if, it has a scheme component. </p>
     *
     * @return  {@code true} if, and only if, this URI is absolute
     * @see java.io.File#isAbsolute()
     */
    @JsonGetter("isAbsolute")
    public boolean isAbsolute() {
        return isAbsolute;
    }

    /**
     * Tests whether the file denoted by this abstract pathname is a
     * directory.
     *
     * @return <code>true</code> if and only if the file denoted by this
     *          abstract pathname exists <em>and</em> is a directory;
     *          <code>false</code> otherwise
     *
     * @see java.io.File#isDirectory()
     */
    @JsonGetter("isDirectory")
    public boolean isDirectory() {
        return isDirectory;
    }

    /**
     * Tests whether the file denoted by this abstract pathname is a normal
     * file.  A file is <em>normal</em> if it is not a directory and, in
     * addition, satisfies other system-dependent criteria.  Any non-directory
     * file created by a Java application is guaranteed to be a normal file.
     *
     * @return  <code>true</code> if and only if the file denoted by this
     *          abstract pathname exists <em>and</em> is a normal file;
     *          <code>false</code> otherwise
     *
     * @see java.io.File#isFile()
     */
    @JsonGetter("isFile")
    public boolean isFile() {
        return isFile;
    }

    /**
     * Tests whether the file named by this abstract pathname is a hidden
     * file.  The exact definition of <em>hidden</em> is system-dependent.  On
     * UNIX systems, a file is considered to be hidden if its name begins with
     * a period character (<code>'.'</code>).  On Microsoft Windows systems, a file is
     * considered to be hidden if it has been marked as such in the filesystem.
     *
     * @return  <code>true</code> if and only if the file denoted by this
     *          abstract pathname is hidden according to the conventions of the
     *          underlying platform
     *
     * @see java.io.File#isHidden()
     */
    @JsonGetter("isHidden")
    public boolean isHidden() {
        return isHidden;
    }

    /**
     * Returns the time that the file denoted by this abstract pathname was
     * last modified.
     *
     * @return  A <code>long</code> value representing the time the file was
     *          last modified, measured in milliseconds since the epoch
     *          (00:00:00 GMT, January 1, 1970), or <code>0L</code> if the
     *          file does not exist or if an I/O error occurs
     *
     * @see java.io.File#lastModified()
     */
    @JsonGetter("lastModified")
    public long lastModified() {
        return lastModified;
    }

    /**
     * Returns the length of the file denoted by this abstract pathname.
     * The return value is unspecified if this pathname denotes a directory.
     *
     *
     * @return  The length, in bytes, of the file denoted by this abstract
     *          pathname, or <code>0L</code> if the file does not exist.  Some
     *          operating systems may return <code>0L</code> for pathnames
     *          denoting system-dependent entities such as devices or pipes.
     *
     * @see java.io.File#length()
     */
    @JsonGetter("length")
    public long length() {
        return length;
    }

    /**
     * Atomically creates a new, empty file named by this abstract pathname if
     * and only if a file with this name does not yet exist.  The check for the
     * existence of the file and the creation of the file if it does not exist
     * are a single operation that is atomic with respect to all other
     * filesystem activities that might affect the file.
     * <P>
     * Note: this method should <i>not</i> be used for file-locking, as
     * the resulting protocol cannot be made to work reliably. The
     * {@link java.nio.channels.FileLock FileLock}
     * facility should be used instead.
     *
     * @return  <code>true</code> if the named file does not exist and was
     *          successfully created; <code>false</code> if the named file
     *          already exists
     *
     * @see java.io.File#createNewFile()
     */
    public ResultTask<Boolean> createNewFile() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("createNewFile", Boolean.class)).postMethodProcess(context);
    }

    /**
     * Creates an empty file in the default temporary-file directory, using
     * the given prefix and suffix to generate its name. Invoking this method
     * is equivalent to invoking <code>{@link java.io.File#createTempFile(java.lang.String,
     * java.lang.String, java.io.File)
     * createTempFile(prefix,&nbsp;suffix,&nbsp;null)}</code>.
     *
     * <p> The {@link
     * java.nio.file.Files#createTempFile(String,String,java.nio.file.attribute.FileAttribute[])
     * Files.createTempFile} method provides an alternative method to create an
     * empty file in the temporary-file directory. Files created by that method
     * may have more restrictive access permissions to files created by this
     * method and so may be more suited to security-sensitive applications.
     *
     * @param  prefix     The prefix string to be used in generating the file's
     *                    name; must be at least three characters long
     *
     * @param  suffix     The suffix string to be used in generating the file's
     *                    name; may be <code>null</code>, in which case the
     *                    suffix <code>".tmp"</code> will be used
     *
     * @return  An abstract pathname denoting a newly-created empty file
     *
     * @see java.io.File#createTempFile(String, String)
     */
    @Annotations.StaticMethod
    public ResultTask<File> createTempFile(String prefix, String suffix) {
        return new ResultCreator<File>(buildMethodCallPacketData("createTempFile", File.class, prefix, suffix)).postMethodProcess(context);
    }

    /**
     * Deletes the file or directory denoted by this abstract pathname.  If
     * this pathname denotes a directory, then the directory must be empty in
     * order to be deleted.
     *
     * <p> Note that the {@link java.nio.file.Files} class defines the {@link
     * java.nio.file.Files#delete(Path) delete} method to throw an {@link IOException}
     * when a file cannot be deleted. This is useful for error reporting and to
     * diagnose why a file cannot be deleted.
     *
     * @return  <code>true</code> if and only if the file or directory is
     *          successfully deleted; <code>false</code> otherwise
     *
     * @see java.io.File#delete()
     */
    public ResultTask<Boolean> delete() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("delete", Boolean.class)).postMethodProcess(context);
    }

    /**
     * Returns an array of strings naming the files and directories in the
     * directory denoted by this abstract pathname.
     *
     * <p> If this abstract pathname does not denote a directory, then this
     * method returns {@code null}.  Otherwise an array of strings is
     * returned, one for each file or directory in the directory.  Names
     * denoting the directory itself and the directory's parent directory are
     * not included in the result.  Each string is a file name rather than a
     * complete path.
     *
     * <p> There is no guarantee that the name strings in the resulting array
     * will appear in any specific order; they are not, in particular,
     * guaranteed to appear in alphabetical order.
     *
     * <p> Note that the {@link java.nio.file.Files} class defines the {@link
     * java.nio.file.Files#newDirectoryStream(Path) newDirectoryStream} method to
     * open a directory and iterate over the names of the files in the directory.
     * This may use less resources when working with very large directories, and
     * may be more responsive when working with remote directories.
     *
     * @return  An array of strings naming the files and directories in the
     *          directory denoted by this abstract pathname.  The array will be
     *          empty if the directory is empty.  Returns {@code null} if
     *          this abstract pathname does not denote a directory, or if an
     *          I/O error occurs.
     *
     * @see java.io.File#list()
     */
    public ResultTask<String[]> list() {
        return new ResultCreator<String[]>(buildMethodCallPacketData("list", String[].class)).postMethodProcess(context);
    }

    /**
     * Returns an array of strings naming the files and directories in the
     * directory denoted by this abstract pathname that satisfy the specified
     * filter.  The behavior of this method is the same as that of the
     * {@link #list()} method, except that the strings in the returned array
     * must satisfy the filter.  If the given {@code filter} is {@code null}
     * then all names are accepted.  Otherwise, a name satisfies the filter if
     * and only if the value {@code true} results when the {@link
     * FilenameFilter#accept FilenameFilter.accept(File,&nbsp;String)} method
     * of the filter is invoked on this abstract pathname and the name of a
     * file or directory in the directory that it denotes.
     *
     * @param  filters
     *         A filename filter
     *
     * @return  An array of strings naming the files and directories in the
     *          directory denoted by this abstract pathname that were accepted
     *          by the given {@code filter}.  The array will be empty if the
     *          directory is empty or if no names were accepted by the filter.
     *          Returns {@code null} if this abstract pathname does not denote
     *          a directory, or if an I/O error occurs.
     *
     * @see java.io.File#list(FilenameFilter)
     */
    public ResultTask<String[]> list(NameFilters.NameFilter<String> filters) {
        ResultTask<String[]> finalResult = new ResultTask<>();
        ResultTask<String[]> rawResult = new ResultCreator<String[]>(buildMethodCallPacketData("list", String[].class))
                .postMethodProcess(context);

        rawResult.setOnTaskCompleteListener(result -> {
            ResultTask.Result<String[]> resultObj = new ResultTask.Result<>();
            resultObj.setSuccess(result.isSuccess());
            resultObj.setHasException(result.hasException());
            resultObj.setException(result.getException());

            if(result.isSuccess()) {
                ArrayList<String> resultList = new ArrayList<>();
                for(String str : (String[]) result.getResultData()) {
                    if(filters.accept(str)) {
                        resultList.add(str);
                    }
                }
                resultObj.setResultData(resultList.toArray());
            }
            finalResult.callCompleteListener(resultObj);
        });

        finalResult.mOnInvokeAttached = result1 -> rawResult.invokeTask();
        return finalResult;
    }

    /**
     * Returns an array of abstract pathnames denoting the files in the
     * directory denoted by this abstract pathname.
     *
     * <p> If this abstract pathname does not denote a directory, then this
     * method returns {@code null}.  Otherwise an array of {@code File} objects
     * is returned, one for each file or directory in the directory.  Pathnames
     * denoting the directory itself and the directory's parent directory are
     * not included in the result.  Each resulting abstract pathname is
     * constructed from this abstract pathname using the {@link File}
     * constructor.  Therefore if this pathname is absolute then each resulting pathname is absolute; if this
     * pathname is relative then each resulting pathname will be relative to
     * the same directory.
     *
     * <p> There is no guarantee that the name strings in the resulting array
     * will appear in any specific order; they are not, in particular,
     * guaranteed to appear in alphabetical order.
     *
     * <p> Note that the {@link java.nio.file.Files} class defines the {@link
     * java.nio.file.Files#newDirectoryStream(Path) newDirectoryStream} method
     * to open a directory and iterate over the names of the files in the
     * directory. This may use less resources when working with very large
     * directories.
     *
     * @return  An array of abstract pathnames denoting the files and
     *          directories in the directory denoted by this abstract pathname.
     *          The array will be empty if the directory is empty.  Returns
     *          {@code null} if this abstract pathname does not denote a
     *          directory, or if an I/O error occurs.
     *
     * @see java.io.File#listFiles()
     */
    public ResultTask<File[]> listFiles() {
        return invokeListFiles("listFiles",null, null);
    }

    /**
     * Returns an array of abstract pathnames denoting the files and
     * directories in the directory denoted by this abstract pathname that
     * satisfy the specified filter.  The behavior of this method is the same
     * as that of the {@link #listFiles()} method, except that the pathnames in
     * the returned array must satisfy the filter.  If the given {@code filter}
     * is {@code null} then all pathnames are accepted.  Otherwise, a pathname
     * satisfies the filter if and only if the value {@code true} results when
     * the {@link FilenameFilter#accept
     * FilenameFilter.accept(File,&nbsp;String)} method of the filter is
     * invoked on this abstract pathname and the name of a file or directory in
     * the directory that it denotes.
     *
     * @param  filters
     *         A filename filter
     *
     * @return  An array of abstract pathnames denoting the files and
     *          directories in the directory denoted by this abstract pathname.
     *          The array will be empty if the directory is empty.  Returns
     *          {@code null} if this abstract pathname does not denote a
     *          directory, or if an I/O error occurs.
     *
     * @see java.io.File#listFiles(FilenameFilter)
     */
    public ResultTask<File[]> listFiles(NameFilters.NameFilter<String> filters) {
        return invokeListFiles("listFiles", filters, null);
    }

    /**
     * Returns an array of abstract pathnames denoting the files and
     * directories in the directory denoted by this abstract pathname that
     * satisfy the specified filter.  The behavior of this method is the same
     * as that of the {@link #listFiles()} method, except that the pathnames in
     * the returned array must satisfy the filter.  If the given {@code filter}
     * is {@code null} then all pathnames are accepted.  Otherwise, a pathname
     * satisfies the filter if and only if the value {@code true} results when
     * the {@link NameFilters.NameFilterWithExtra#accept} method of the
     * filter is invoked on the pathname.
     *
     * @param  filters
     *         A file filter
     *
     * @return  An array of abstract pathnames denoting the files and
     *          directories in the directory denoted by this abstract pathname.
     *          The array will be empty if the directory is empty.  Returns
     *          {@code null} if this abstract pathname does not denote a
     *          directory, or if an I/O error occurs.
     *
     * @see java.io.File#listFiles(FileFilter)
     */
    public ResultTask<File[]> listFiles(NameFilters.NameFilterWithExtra<String, File> filters) {
        return invokeListFiles("listFiles",null, filters);
    }

    /**
     * Returns the file system roots. On Android and other Unix systems, there is
     * a single root, {@code /}.
     *
     * @see java.io.File#listRoots()
     */
    @Annotations.StaticMethod
    public ResultTask<File[]> listRoots() {
        return invokeListFiles("listRoots", null, null);
    }

    private ResultTask<File[]> invokeListFiles(String methodName,
                                               @Nullable NameFilters.NameFilter<String> filterWithName,
                                               @Nullable NameFilters.NameFilterWithExtra<String, File> filterWithObj) {
        ResultTask<File[]> finalResult = new ResultTask<>();
        ResultTask<FileModel[]> rawResult = new ResultCreator<FileModel[]>(buildMethodCallPacketData(methodName, FileModel[].class))
                .postMethodProcess(context);

        rawResult.setOnTaskCompleteListener(result -> {
            ResultTask.Result<File[]> resultObj = new ResultTask.Result<>();
            resultObj.setSuccess(result.isSuccess());
            resultObj.setHasException(result.hasException());
            resultObj.setException(result.getException());

            if(result.isSuccess()) {
                ArrayList<File> resultList = new ArrayList<>();
                for(FileModel files : (FileModel[]) result.getResultData()) {
                    File convertedFileObj = (File) ServiceProcess.convertToFinalFormat(files, File.class);
                    convertedFileObj.context = context;
                    convertedFileObj.setIsFetched(true);

                    if(filterWithName != null && filterWithName.accept(convertedFileObj.name)) {
                        resultList.add(convertedFileObj);
                    } else if(filterWithObj != null && filterWithObj.accept(convertedFileObj.name, convertedFileObj)) {
                        resultList.add(convertedFileObj);
                    } else {
                        resultList.add(convertedFileObj);
                    }
                }
                resultObj.setResultData(resultList.toArray());
            }
            finalResult.callCompleteListener(resultObj);
        });

        finalResult.mOnInvokeAttached = result1 -> rawResult.invokeTask();
        return finalResult;
    }

    /**
     * Creates the directory named by this abstract pathname.
     *
     * @return  <code>true</code> if and only if the directory was
     *          created; <code>false</code> otherwise
     *
     * @see java.io.File#mkdir()
     */
    public ResultTask<Boolean> mkdir() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("mkdir", Boolean.class)).postMethodProcess(context);
    }

    /**
     * Creates the directory named by this abstract pathname, including any
     * necessary but nonexistent parent directories.  Note that if this
     * operation fails it may have succeeded in creating some of the necessary
     * parent directories.
     *
     * @return  <code>true</code> if and only if the directory was created,
     *          along with all necessary parent directories; <code>false</code>
     *          otherwise
     *
     * @see java.io.File#mkdirs()
     */
    public ResultTask<Boolean> mkdirs() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("mkdirs", Boolean.class)).postMethodProcess(context);
    }

    /**
     * Renames the file denoted by this abstract pathname.
     *
     * <p>Many failures are possible. Some of the more likely failures include:
     * <ul>
     * <li>Write permission is required on the directories containing both the source and
     * destination paths.
     * <li>Search permission is required for all parents of both paths.
     * <li>Both paths be on the same mount point. On Android, applications are most likely to hit
     * this restriction when attempting to copy between internal storage and an SD card.
     * </ul>
     *
     * <p>The return value should always be checked to make sure
     * that the rename operation was successful.
     *
     * <p> Note that the {@link java.nio.file.Files} class defines the {@link
     * java.nio.file.Files#move move} method to move or rename a file in a
     * platform independent manner.
     *
     * @param  dest  The new abstract pathname for the named file
     *
     * @return  <code>true</code> if and only if the renaming succeeded;
     *          <code>false</code> otherwise
     *
     * @see java.io.File#renameTo(java.io.File)
     */
    public ResultTask<Boolean> renameTo(File dest) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("renameTo", Boolean.class, dest)).postMethodProcess(context);
    }

    /**
     * Sets the owner's or everybody's execute permission for this abstract
     * pathname.
     *
     * @param   executable
     *          If <code>true</code>, sets the access permission to allow execute
     *          operations; if <code>false</code> to disallow execute operations
     *
     * @return  <code>true</code> if and only if the operation succeeded.  The
     *          operation will fail if the user does not have permission to
     *          change the access permissions of this abstract pathname.  If
     *          <code>executable</code> is <code>false</code> and the underlying
     *          file system does not implement an execute permission, then the
     *          operation will fail.
     *
     * @see java.io.File#setExecutable(boolean)
     */
    public ResultTask<Boolean> setExecutable(boolean executable) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("setExecutable", Boolean.class, executable)).postMethodProcess(context);
    }

    /**
     * Sets the last-modified time of the file or directory named by this
     * abstract pathname.
     *
     * <p> All platforms support file-modification times to the nearest second,
     * but some provide more precision.  The argument will be truncated to fit
     * the supported precision.  If the operation succeeds and no intervening
     * operations on the file take place, then the next invocation of the
     * <code>{@link #lastModified}</code> method will return the (possibly
     * truncated) <code>time</code> argument that was passed to this method.
     *
     * @param  time  The new last-modified time, measured in milliseconds since
     *               the epoch (00:00:00 GMT, January 1, 1970)
     *
     * @return <code>true</code> if and only if the operation succeeded;
     *          <code>false</code> otherwise
     *
     * @see java.io.File#setLastModified(long)
     */
    public ResultTask<Boolean> setLastModified(long time) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("setExecutable", Boolean.class, time)).postMethodProcess(context);
    }

    /**
     * Marks the file or directory named by this abstract pathname so that
     * only read operations are allowed. After invoking this method the file
     * or directory will not change until it is either deleted or marked
     * to allow write access. Whether or not a read-only file or
     * directory may be deleted depends upon the underlying system.
     *
     * @return <code>true</code> if and only if the operation succeeded;
     *          <code>false</code> otherwise
     *
     * @see java.io.File#setReadOnly()
     */
    public ResultTask<Boolean> setReadOnly() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("setReadOnly", Boolean.class)).postMethodProcess(context);
    }

    /**
     * Marks the file or directory named by this abstract pathname so that
     * only read operations are allowed. After invoking this method the file
     * or directory will not change until it is either deleted or marked
     * to allow write access. Whether or not a read-only file or
     * directory may be deleted depends upon the underlying system.
     *
     * @return <code>true</code> if and only if the operation succeeded;
     *          <code>false</code> otherwise
     *
     * @see java.io.File#setReadable(boolean)
     */
    public ResultTask<Boolean> setReadable(boolean readable) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("setReadable", Boolean.class, readable)).postMethodProcess(context);
    }

    /**
     * Sets the owner's or everybody's write permission for this abstract
     * pathname.
     *
     * <p> The {@link java.nio.file.Files} class defines methods that operate on
     * file attributes including file permissions. This may be used when finer
     * manipulation of file permissions is required.
     *
     * @param   writable
     *          If <code>true</code>, sets the access permission to allow write
     *          operations; if <code>false</code> to disallow write operations
     *
     * @return  <code>true</code> if and only if the operation succeeded. The
     *          operation will fail if the user does not have permission to change
     *          the access permissions of this abstract pathname.
     *
     * @see java.io.File#setWritable(boolean)
     */
    public ResultTask<Boolean> setWritable(boolean writable) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("setWritable", Boolean.class, writable)).postMethodProcess(context);
    }

    /**
     * Constructs a <tt>file:</tt> URI that represents this abstract pathname.
     *
     * <p> The exact form of the URI is system-dependent.  If it can be
     * determined that the file denoted by this abstract pathname is a
     * directory, then the resulting URI will end with a slash.
     *
     * @return  An absolute, hierarchical URI with a scheme equal to
     *          <tt>"file"</tt>, a path representing this abstract pathname,
     *          and undefined authority, query, and fragment components
     *
     * @see java.io.File#toURI()
     */
    public ResultTask<URI> toURI() {
        return new ResultCreator<URI>(buildMethodCallPacketData("toURI", URI.class)).postMethodProcess(context);
    }

    /**
     * Creates a <code>FileInputStream</code> by
     * opening a connection to an actual file,
     * the file named by the path name <code>name</code>
     * in the file system.  A new <code>FileDescriptor</code>
     * object is created to represent this file
     * connection.
     *
     * @return An {@link java.io.InputStream} object to read
     *
     * @see java.io.FileInputStream#FileInputStream(String)
     */
    public ResultTask<InputStream> openFileInputStream() {
        return new ResultCreator<InputStream>(buildStreamCallPacketData(true), true).postMethodProcess(context);
    }

    /**
     * Creates a file output stream to write to the file with the
     * specified name. A new <code>FileDescriptor</code> object is
     * created to represent this file connection.
     * <p>
     * If the file exists but is a directory rather than a regular file, does
     * not exist but cannot be created, or cannot be opened for any other
     * reason then a <code>FileNotFoundException</code> is thrown.
     *
     * @return An {@link java.io.OutputStream} object to write
     *
     * @see java.io.FileOutputStream#FileOutputStream(String)
     */
    public ResultTask<OutputStream> openFileOutputStream() {
        return new ResultCreator<OutputStream>(buildStreamCallPacketData(false), true).postMethodProcess(context);
    }

    @Override
    public int hashCode() {
        checkIsFetched();
        return hashCode;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getPath();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof File) {
            return (((File) object).getAbsolutePath()).equals(this.getAbsolutePath());
        } else return false;
    }

    private PacketData buildMethodCallPacketData(String methodName, Class<?> parameterCls, Object... args) {
        Instance instance = Instance.getInstance();
        PacketData packet = new PacketData();
        ArgsInfo argsInfo = new ArgsInfo();
        String ticket = String.format("%s@%s", methodName, System.currentTimeMillis());

        argsInfo.put(getAbsolutePath());
        argsInfo.put(parameterCls, methodName);

        for(Object arg : args) {
            argsInfo.put(arg);
        }

        packet.argsInfo = argsInfo;
        packet.fromPackageName = instance.getServerPeer().getPackageName();
        packet.ticketId = ticket;
        packet.apiType = ProcessConst.ACTION_TYPE_FILE;
        packet.actionType = ProcessConst.ACTION_REQUEST_METHOD;

        return packet;
    }

    private PacketData buildStreamCallPacketData(Boolean isInputStream) {
        Instance instance = Instance.getInstance();
        PacketData packet = new PacketData();
        ArgsInfo argsInfo = new ArgsInfo();

        String ticket = String.format(ProcessConst.STREAM_KEY_PREFIX,
                isInputStream ? ProcessConst.STREAM_INPUT : ProcessConst.STREAM_OUTPUT,
                System.currentTimeMillis());

        argsInfo.put(getAbsolutePath());
        argsInfo.put(isInputStream);

        packet.argsInfo = argsInfo;
        packet.fromPackageName = instance.getServerPeer().getPackageName();
        packet.ticketId = ticket;
        packet.apiType = ProcessConst.ACTION_TYPE_FILE;
        packet.actionType = ProcessConst.ACTION_REQUEST_STREAM;

        return packet;
    }
}
