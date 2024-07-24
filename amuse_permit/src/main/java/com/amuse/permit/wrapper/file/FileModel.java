package com.amuse.permit.wrapper.file;

import com.amuse.permit.model.Wrappable;

import java.io.Serializable;


public abstract class FileModel extends Wrappable implements Serializable {
    private static final long serialVersionUID = 3141592L;
    private static final String ACTION_TYPE_FILE = "action_type_file";

    protected boolean canExecute;
    protected boolean canRead;
    protected boolean canWrite;
    protected boolean exists;
    protected String absolutePath;
    protected String canonicalPath;
    protected long freeSpace;
    protected String name;
    protected String parent;
    protected String path;
    protected long totalSpace;
    protected long usableSpace;
    protected int hashCode;
    protected boolean isAbsolute;
    protected boolean isDirectory;
    protected boolean isFile;
    protected boolean isHidden;
    protected long lastModified;
    protected long length;

    @Override
    public String getClassTag() {
        return ACTION_TYPE_FILE;
    }

    @Override
    public Class<?> getNativeImplClass() {
        return FileNativeWrapper.class;
    }
}
