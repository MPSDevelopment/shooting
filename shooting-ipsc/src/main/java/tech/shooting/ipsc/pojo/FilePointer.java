package tech.shooting.ipsc.pojo;

import com.google.common.hash.HashCode;
import com.google.common.net.MediaType;
import org.springframework.data.mongodb.gridfs.GridFsResource;

import java.io.*;
import java.time.Instant;
import java.util.Optional;

public class FilePointer {

    private final GridFsResource resource;
    private final HashCode tag;
    private final MediaType mediaTypeOrNull;

    public FilePointer(GridFsResource resource) {
        this.resource = resource;
        this.tag = HashCode.fromInt(resource.hashCode());
        this.mediaTypeOrNull = resource.getContentType() != null ? MediaType.parse(resource.getContentType()) : null;
    }

    public GridFsResource open() {
        return resource;
    }

    public long getSize() {
        try {
            return resource.contentLength();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getEtag() {
        return "\"" + tag + "\"";
    }

    public Instant getLastModified() {
        try {
            return Instant.ofEpochMilli(resource.lastModified());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public boolean matchesEtag(String requestEtag) {
        return getEtag().equals(requestEtag);
    }

    public boolean modifiedAfter(Instant clientTime) {
        return clientTime.isAfter(getLastModified());
    }

    public Optional<MediaType> getMediaType() {
        return Optional.ofNullable(mediaTypeOrNull);
    }
}
