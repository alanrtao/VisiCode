package VisiCode.Domain;

import VisiCode.Domain.Exceptions.EntityException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.codec.binary.Base64;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Unindexed;
import org.springframework.data.annotation.Id;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Entity
public class Note {

    // https://cloud.google.com/datastore/docs/concepts/limits, minus two longs
    public static final int MAX_BLOB_SIZE = 1048572 - 8 - 8;
    public static final int MAX_CHAR_COUNT = MAX_BLOB_SIZE / 2;
    private final ENoteType type;

    @Unindexed
    String data;
    @Id
    private String id;

    @JsonCreator
    private Note(
            @JsonProperty("type") ENoteType type,
            @JsonProperty("data") String data) {
        this.type = type;
        this.data = data;
        this.id = UUID.randomUUID().toString();
    }

    public static Note makeTextNote(String text) throws BlobSizeException {
        // here we assume 1 character is 2 bytes, Java treats Strings messily so this is not the definitive answer
        // the worst case is the note could not be uploaded due to size issue, this will throw a harmless exception in the controller
        if (text.length() > MAX_CHAR_COUNT) throw new BlobSizeException(text.length() * 2L);
        return new Note(ENoteType.MARKDOWN, text);
    }

    public static Note makeFileNote(MultipartFile file) throws BlobSizeException, IOException {
        if (file.getSize() > MAX_BLOB_SIZE) throw new BlobSizeException(file.getSize());
        return new Note(ENoteType.IMAGE, Base64.encodeBase64String(file.getBytes()));
    }

    public static Note forTest(String id) {
        Note n = makeTextNote("For testing");
        n.id = id;
        return n;
    }

    public void fillIdForTest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type.name();
    }

    public String getData() {
        return data;
    }

    public enum ENoteType {
        MARKDOWN,
        IMAGE
    }

    public static class BlobSizeException extends EntityException {
        public BlobSizeException(long size) {
            super(String.format("Note size too large: %s", size));
        }
    }
}
