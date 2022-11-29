package VisiCode.Domain.Exceptions;

public class EntityException extends VisiCodeException {
    protected EntityException(String message) {
        super(message);
    }

    public static EntityException cannotViewById(String id) {
        return new EntityException(String.format("%s not associated with any viewable project", id));
    }

    public static EntityException cannotEditById(String id) {
        return new EntityException(String.format("%s not associated with any editable project", id));
    }

    public static EntityException noSuchProject(String id) {
        return new EntityException(String.format("No such project %s", id));
    }

    public static EntityException noSuchProject(Long id) {
        return new EntityException(String.format("No such project %s", id));
    }

    public static EntityException duplicateProject(String name) {
        return new EntityException(String.format("Project %s exists", name));
    }

    public static EntityException noSuchNote(String id) {
        return new EntityException(String.format("No such note %s", id));
    }

    public static EntityException duplicateNote(String id) {
        return new EntityException(String.format("Note %s exists", id));
    }
}