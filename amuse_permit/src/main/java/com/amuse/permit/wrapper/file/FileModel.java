package com.amuse.permit.wrapper.file;

import com.amuse.permit.model.Wrappable;

import java.io.Serializable;

public class FileModel extends Wrappable implements Serializable {
    private static final long serialVersionUID = 3141592L;

    protected boolean canExecute;
    protected boolean canRead;
    protected boolean canWrite;
    protected boolean exists;
    protected String absolutePath;
    protected String canonicalPath;
    protected String name;
    protected String parent;
    protected String path;
    protected long freeSpace;
    protected long totalSpace;
    protected long usableSpace;
    protected int hashCode;
    protected boolean isAbsolute;
    protected boolean isDirectory;
    protected boolean isFile;
    protected boolean isHidden;
    protected long lastModified;
    protected long length;

    public FileModel() {
        // Stub constructor for Object Mapper
    }
}
