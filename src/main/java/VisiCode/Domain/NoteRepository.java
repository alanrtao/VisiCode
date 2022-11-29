package VisiCode.Domain;

import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

public interface NoteRepository extends DatastoreRepository<Note, String> {
}
