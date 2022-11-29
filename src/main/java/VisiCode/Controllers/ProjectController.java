package VisiCode.Controllers;

import VisiCode.Domain.Exceptions.EntityException;
import VisiCode.Domain.Exceptions.UserException;
import VisiCode.Domain.*;
import VisiCode.Payload.ProjectCreationRequest;
import VisiCode.Payload.ProjectRemovalRequest;
import com.google.cloud.datastore.DatastoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/api")
public class ProjectController extends UserAuthenticable {
    @Autowired
    NoteRepository noteRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    UserRepository userRepository;

    public static int MAX_PROJECT_COUNT = 1024;

    @ResponseBody
    @GetMapping("/project")
    public List<String> myProjects(Authentication auth) {
        User user = getAuthenticated(auth);
        Iterable<Project> existingProjects = projectRepository.findAllById(user.getProjects());
        List<String> names = new ArrayList<>();
        for (Project project : existingProjects)
            names.add(project.getName());
        return names;
    }

    @ResponseBody
    @PostMapping("/project/create")
    public Project createProject(Authentication auth, @RequestBody @Valid ProjectCreationRequest request) {
        User user = getAuthenticated(auth);

        if (user.getProjects().size() == MAX_PROJECT_COUNT) throw new ProjectCountException();

        Iterable<Project> existingProjects = projectRepository.findAllById(user.getProjects());
        for (Project p : existingProjects) {
            if (p.getName().equals(request.getName()))
                throw EntityException.duplicateProject(request.getName());
        }

        Project project = Project.create(request.getName());
        projectRepository.save(project);
        if (user.addProject(project.getId())) {
            userRepository.save(user);
            project.clearId();
            return project;
        } else
            throw EntityException.duplicateProject(request.getName());
    }

    @PostMapping("/project/remove")
    @ResponseStatus(value = HttpStatus.OK)
    public void removeProject(Authentication auth, @RequestBody @Valid ProjectRemovalRequest request) {
        User user = getAuthenticated(auth);

        Iterable<Project> existingProjects = projectRepository.findAllById(user.getProjects());
        Project toDelete = StreamSupport
                .stream(existingProjects.spliterator(), false)
                .filter((project -> project.getName().equals(request.getName())))
                .findFirst()
                .orElseThrow(()->EntityException.noSuchProject(request.getName()));

        if (user.removeProject(toDelete.getId())) {
            userRepository.save(user);
            Project project = projectRepository.findById(toDelete.getId()).orElseThrow(() -> EntityException.noSuchProject(toDelete.getId()));
            projectRepository.deleteById(toDelete.getId());
            for (String i : project.getNotes()) {
                noteRepository.deleteById(i);
            }
        } else
            throw UserException.notOwner(user.getUsername());
    }

    @ResponseBody
    @GetMapping("/project/{name}")
    public Project viewOwnProject(Authentication auth, @PathVariable String name) {
        User user = getAuthenticated(auth);
        Optional<Project> findProject = StreamSupport.stream(projectRepository
                        .findAllById(user.getProjects()).spliterator(), false)
                .filter((p) -> p.getName().equals(name))
                .findFirst();
        if (findProject.isEmpty()) throw EntityException.noSuchProject(name);

        Project project = findProject.get();
        if (user.getProjects().contains(project.getId())) {
            project.clearId();
            return project;
        } else
            throw UserException.notOwner(user.getUsername());
    }

    @ResponseBody
    @GetMapping("/project/visit/{id}")
    public Project viewOtherProject(@PathVariable String id) {
        Project viewable = getViewable(id);
        viewable.clearId();
        return viewable;
    }

    @ResponseBody
    @PostMapping(value = "/note/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addFileNote(@RequestParam String editorId, @RequestBody MultipartFile file) throws IOException {
        Project editableProject = getEditable(editorId);
        System.out.println(file);
        System.out.println(editableProject.getNotes().size());
        Note note = Note.makeFileNote(file);
        addNote(editableProject, note);
        System.out.println(editableProject.getNotes().size());
    }

    @ResponseBody
    @PostMapping("/note/text")
    public String addTextNote(@RequestParam String editorId, @RequestBody String text) {
        Project editableProject = getEditable(editorId);
        // https://www.codejava.net/frameworks/spring-boot/spring-boot-file-upload-tutorial
        Note note = Note.makeTextNote(text);
        addNote(editableProject, note);
        return text;
    }

    @ResponseBody
    @DeleteMapping("/note/{noteId}")
    public void removeNote(@PathVariable String noteId, @RequestParam String editorId) {
        Project editableProject = getEditable(editorId);
        Note note = noteRepository.findById(noteId).orElseThrow(() -> EntityException.noSuchNote(noteId));
        if (editableProject.removeNote(note)) {
            projectRepository.save(editableProject);
            noteRepository.deleteById(note.getId());
        } else {
            throw EntityException.noSuchNote(noteId);
        }
    }

    @ResponseBody
    @GetMapping("/note/{noteId}")
    public Note viewNote(@PathVariable String noteId, @RequestParam String viewerOrEditorId) {
        Project viewableProject = getViewable(viewerOrEditorId);
        if (viewableProject.getNotes().contains(noteId)) {
            return noteRepository.findById(noteId).orElseThrow(() -> EntityException.noSuchNote(noteId));
        } else
            throw EntityException.noSuchNote(noteId);
    }

    private Project getEditable(String editorId) {
        return projectRepository.findByEditorId(editorId).orElseThrow(() -> EntityException.cannotEditById(editorId));
    }

    private Project getViewable(String editOrViewId) {
        AtomicBoolean canSeeEditorId = new AtomicBoolean(true);
        Project project = projectRepository.findByEditorId(editOrViewId)
                .or(() -> {
                    canSeeEditorId.set(false);
                    return projectRepository.findByViewerId(editOrViewId);
                })
                .orElseThrow(() -> EntityException.cannotViewById(editOrViewId));
        if (!canSeeEditorId.get()) {
            project.clearEditorId();
            project.setPermissionToView();
        }
        return project;
    }

    private void addNote(Project project, Note note) {
        if (project.addNote(note)) {
            try {
                noteRepository.save(note);
                projectRepository.save(project);
            } catch (DatastoreException e) {
                throw new Note.BlobSizeException(note.getData().length());
            }
        } else
            throw EntityException.duplicateNote(note.getId());
    }

    public static class ProjectCountException extends EntityException {
        public ProjectCountException() {
            super(String.format("Project size cannot exceed %d notes", MAX_PROJECT_COUNT));
        }
    }
}
