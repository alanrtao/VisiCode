package VisiCode.Domain;

import VisiCode.Domain.Exceptions.EntityException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.HashSet;
import java.util.UUID;

@Entity
public class Project {
    @Id
    private Long id;
    private final String name;

    @Field(name = "editor_id")
    private String editorId;
    @Field(name = "viewer_id")
    private final String viewerId;
    private final HashSet<String> notes;

    // set here to prevent
    public static final int MAX_NOTES = 4096;

    private static final String permissionView = "view";
    private static final String permissionEdit = "edit";

    @Transient
    private String permission;

    @JsonCreator
    private Project(
            @JsonProperty("name") String name,
            @JsonProperty("editorId") String editorId,
            @JsonProperty("viewerId") String viewerId,
            @JsonProperty("notes") HashSet<String> notes) {
        this.name = name;
        this.editorId = editorId;
        this.viewerId = viewerId;
        this.notes = notes;
        this.permission = permissionEdit;
    }

    public static Project forTest(String name) {
        return forTest(name, 0L);
    }

    public static Project forTest(String name, Long id) {
        Project p = new Project(name, "editor_" + id, "viewer_" + id, new HashSet<>());
        p.id = id;
        return p;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEditorId() {
        return editorId;
    }

    public void clearEditorId() {
        editorId = null;
    }

    public String getViewerId() {
        return viewerId;
    }

    public HashSet<String> getNotes() {
        return notes;
    }

    public void clearId() {
        this.id = -1L;
    }

    public static Project create(String name) {
        return new Project(
                name,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                new HashSet<>());
    }

    public boolean addNote(Note note) {
        if (notes.add(note.getId())) {
            if (notes.size() > MAX_NOTES) throw new ProjectSizeException();
            return true;
        } else return false;
    }

    public boolean removeNote(Note note) {
        return notes.removeIf(n -> n.equals(note.getId()));
    }

    public void setPermissionToView() {
        permission = permissionView;
    }

    public void setPermissionToEdit() {
        permission = permissionEdit;
    }

    public static class ProjectSizeException extends EntityException {
        public ProjectSizeException() {
            super(String.format("Project size cannot exceed %d notes", MAX_NOTES));
        }
    }

}