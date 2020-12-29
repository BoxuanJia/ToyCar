package com.github.boxuanjia.toycar.cache;

import com.github.boxuanjia.toycar.disklrucache.DiskLruCache;
import com.github.boxuanjia.toycar.log.ToyLog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DiskLruCacheWrapper implements DiskCache {

    private static final int APP_VERSION = 1;

    private static final int VALUE_COUNT = 1;

    private static DiskLruCacheWrapper WRAPPER = null;

    /**
     * Get a DiskCache in the given directory and size. If a disk cache has alread been created with
     * a different directory and/or size, it will be returned instead and the new arguments
     * will be ignored.
     *
     * @param directory The directory for the disk cache
     * @param maxSize   The max size for the disk cache
     * @return The new disk cache with the given arguments, or the current cache if one already exists
     */
    public synchronized static DiskCache get(File directory, int maxSize) {
        if (WRAPPER == null) {
            WRAPPER = new DiskLruCacheWrapper(directory, maxSize);
        }
        return WRAPPER;
    }

    private final File directory;

    private final int maxSize;

    private DiskLruCache diskLruCache;

    protected DiskLruCacheWrapper(File directory, int maxSize) {
        this.directory = directory;
        this.maxSize = maxSize;
    }

    private synchronized DiskLruCache getDiskCache() throws IOException {
        if (diskLruCache == null) {
            diskLruCache = DiskLruCache.open(directory, APP_VERSION, VALUE_COUNT, maxSize);
        }
        return diskLruCache;
    }

    @Override
    public InputStream get(String key) {
        InputStream result = null;
        try {
            //It is possible that the there will be a put in between these two gets. If so that shouldn't be a problem
            //because we will always put the same value at the same key so our input streams will still represent
            //the same data
            final DiskLruCache.Snapshot snapshot = getDiskCache().get(key);
            if (snapshot != null) {
                result = snapshot.getInputStream(0);
            }
        } catch (IOException e) {
            ToyLog.e("Unable to get from disk cache");
        }
        return result;
    }

    @Override
    public void put(String key, Writer writer) {
        try {
            DiskLruCache.Editor editor = getDiskCache().edit(key);
            //editor will be null if there are two concurrent puts
            //worst case just silently fail
            if (editor != null) {
                OutputStream os = null;
                try {
                    os = editor.newOutputStream(0);
                    writer.write(os);
                } finally {
                    if (os != null) {
                        os.close();
                    }
                }
                editor.commit();
            }
        } catch (IOException e) {
            ToyLog.e("Unable to put to disk cache");
        }
    }

    @Override
    public void delete(String key) {
        try {
            getDiskCache().remove(key);
        } catch (IOException e) {
            ToyLog.e("Unable to delete from disk cache");
        }
    }
}
