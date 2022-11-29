package VisiCode.Controllers;

import VisiCode.Domain.*;
import VisiCode.Payload.ProjectCreationRequest;
import VisiCode.Payload.ProjectRemovalRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(MockitoJUnitRunner.class)
class ProjectControllerTest {

    public static final String USERNAME = "test user";
    private static final String SMALL = "0".repeat(10);
    private static final String LARGE = "0".repeat(Note.MAX_CHAR_COUNT);
    private static final String OVERSIZED = "0".repeat(Note.MAX_CHAR_COUNT + 1);

    private static final MockMultipartFile FSMALL = new MockMultipartFile("file", "FSMALL.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[10]);
    private static final MockMultipartFile FLARGE = new MockMultipartFile("file", "FLARGE.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[Note.MAX_BLOB_SIZE]);
    private static final MockMultipartFile FOVERSIZED = new MockMultipartFile("file", "FOVERSIZED.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[Note.MAX_BLOB_SIZE + 1]);
    private static final MockMultipartFile FEXTRALARGE = new MockMultipartFile("file", "FEXTRALARGE.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[20480 * 1024] // 20MB
    );
    @MockBean
    ProjectRepository projectRepository;
    @MockBean
    NoteRepository noteRepository;
    @MockBean
    UserRepository userRepository;
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();
    Authentication authentication;
    private Project p1, p2, p3, pn1, pnMax;
    private Note n1;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        p1 = Project.forTest("Project1", 1L);
        p2 = Project.forTest("Project2", 2L);
        p3 = Project.forTest("Project3", 3L);

        n1 = Note.forTest(1L + "");

        pn1 = Project.forTest("ProjectWithNote1");
        pn1.addNote(n1);

        pnMax = Project.forTest("ProjectFull");

        for (int i = 0; i < Project.MAX_NOTES; i++) {
            pnMax.addNote(Note.forTest( i + 1000 + ""));
        }
    }

    @Test
    void myProjectsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/project")).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(USERNAME)
    void myProjectsEmpty() throws Exception {
        when(userRepository.findByUsername(eq(USERNAME))).thenReturn(Optional.of(new User(USERNAME, "", new HashSet<>())));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/project")).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(USERNAME)
    void myProjectsOne() throws Exception {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(new User(USERNAME, "", new HashSet<>(List.of(p1.getId())))));
        when(projectRepository.findAllById(argThat(ids -> StreamSupport.stream(ids.spliterator(), false).anyMatch(id -> Objects.equals(id, p1.getId()))))).thenReturn(List.of(p1));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/project")).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1))).andExpect(MockMvcResultMatchers.jsonPath("$", hasItem(p1.getName())));
    }

    @Test
    @WithMockUser(USERNAME)
    void myProjectsMany() throws Exception {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(new User(USERNAME, "", new HashSet<>())));
        when(projectRepository.findAllById(argThat(id -> true))).thenReturn(List.of(p1, p2, p3));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/project")).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(3))).andExpect(MockMvcResultMatchers.jsonPath("$", hasItem(p1.getName()))).andExpect(MockMvcResultMatchers.jsonPath("$", hasItem(p2.getName()))).andExpect(MockMvcResultMatchers.jsonPath("$", hasItem(p3.getName())));
    }

    @Test
    @WithMockUser(USERNAME)
    void createProject() throws Exception {
        when(userRepository
                .findByUsername(USERNAME))
                .thenReturn(Optional.of(new User(USERNAME, "", new HashSet<>(List.of(p2.getId())))));
        final String PROJECT_NAME = "Create one project";
        String resp = mockMvc.perform(MockMvcRequestBuilders.post("/api/project/create").contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(ProjectCreationRequest.forTest(PROJECT_NAME)))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Project p = objectMapper.readValue(resp, Project.class);
        assertEquals(p.getName(), PROJECT_NAME);
        assertEquals(p.getEditorId().length(), 36);
        assertEquals(p.getViewerId().length(), 36);
        assertEquals(p.getId(), -1);
        assertEquals(p.getNotes().size(), 0);
    }

    @Test
    @WithMockUser(USERNAME)
    void createProjectDuplicateName() throws Exception {
        when(userRepository
                .findByUsername(USERNAME))
                .thenReturn(Optional.of(new User(USERNAME, "", new HashSet<>(List.of(p1.getId())))));
        when(projectRepository.findAllById(argThat(a->true))).thenReturn(List.of(p1));
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/project/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectWriter.writeValueAsString(ProjectCreationRequest.forTest(p1.getName()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(USERNAME)
    void createProjectTooMany() throws Exception {
        HashSet<Long> projects = new HashSet<>(ProjectController.MAX_PROJECT_COUNT);
        for (int i = 0; i < ProjectController.MAX_PROJECT_COUNT; i++) {
            projects.add((long) i);
        }

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(new User(USERNAME, "", projects)));
        final String PROJECT_NAME = "Create one project";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/project/create").contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(ProjectCreationRequest.forTest(PROJECT_NAME)))).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(USERNAME)
    void removeProjectNonExistentInUser() throws Exception {
        when(projectRepository.findAllById(argThat(a->true))).thenReturn(List.of(p1));
        when(projectRepository.findById(p1.getId())).thenReturn(Optional.of(p1));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/project/remove").contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(ProjectRemovalRequest.forTest(p1.getName())))).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(USERNAME)
    void removeProjectNonExistentInRepository() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/project/remove").contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(ProjectRemovalRequest.forTest(p1.getName())))).andExpect(status().isBadRequest());
    }

    @Test
    void removeProjectUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/project/remove").contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(ProjectRemovalRequest.forTest(p1.getName())))).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(USERNAME)
    void removeProjectValid() throws Exception {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(new User(USERNAME, "", new HashSet<>(List.of(pn1.getId())))));
        when(projectRepository.findAllById(argThat(a->true))).thenReturn(List.of(pn1));
        when(projectRepository.findById(pn1.getId())).thenReturn(Optional.of(pn1));

        RequestBuilder req = MockMvcRequestBuilders.post("/api/project/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(ProjectRemovalRequest.forTest(pn1.getName())));

        mockMvc.perform(req).andExpect(status().isOk());
    }

    @Test
    void viewOwnProjectUnauthorized() throws Exception {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(new User(USERNAME, "", new HashSet<>(List.of(p1.getId())))));
        when(projectRepository.findById(p1.getId())).thenReturn(Optional.of(p1));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/project/" + p1.getName())).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(USERNAME)
    void viewOwnProjectNonExistent() throws Exception {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(new User(USERNAME, "", new HashSet<>(List.of(p1.getId())))));
        when(projectRepository.findById(p1.getId())).thenReturn(Optional.of(p1));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/project/" + p1.getName())).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(USERNAME)
    void viewOwnProjectValid() throws Exception {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(new User(USERNAME, "", new HashSet<>(List.of(p1.getId())))));
        when(projectRepository.findAllById(argThat(a -> true))).thenReturn(List.of(p1));

        String resp = mockMvc.perform(MockMvcRequestBuilders.get("/api/project/" + p1.getName())).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Project p = objectMapper.readValue(resp, Project.class);
        assertEquals(p.getName(), p1.getName());
        assertEquals(p.getEditorId().length(), p1.getEditorId().length());
        assertEquals(p.getViewerId().length(), p1.getViewerId().length());
        assertEquals(p.getId(), -1);
        assertEquals(p.getNotes().size(), 0);
    }

    @Test
    void viewOtherProject() throws Exception {
        when(projectRepository.findByViewerId(p1.getViewerId())).thenReturn(Optional.of(p1));

        String resp = mockMvc.perform(MockMvcRequestBuilders.get("/api/project/visit/" + p1.getViewerId())).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Project p = objectMapper.readValue(resp, Project.class);
        assertEquals(p.getName(), p1.getName());
        assertNull(p.getEditorId());
        assertEquals(p.getViewerId().length(), p1.getViewerId().length());
        assertEquals(p.getId(), -1);
        assertEquals(p.getNotes().size(), 0);
    }

    @Test
    void viewOtherProjectWithEditPermissions() throws Exception {
        when(projectRepository.findByEditorId(p1.getEditorId())).thenReturn(Optional.of(p1));

        String resp = mockMvc.perform(MockMvcRequestBuilders.get("/api/project/visit/" + p1.getEditorId())).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Project p = objectMapper.readValue(resp, Project.class);
        assertEquals(p.getName(), p1.getName());
        assertEquals(p.getEditorId().length(), p1.getEditorId().length());
        assertEquals(p.getViewerId().length(), p1.getViewerId().length());
        assertEquals(p.getId(), -1);
        assertEquals(p.getNotes().size(), 0);
    }

    @Test
    void viewOtherProjectNonExistent() throws Exception {
        when(projectRepository.findByViewerId(p1.getViewerId())).thenReturn(Optional.of(p1));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/project/visit/" + p2.getEditorId())).andExpect(status().isBadRequest());
    }

    @Test
    void addTextNoteLarge() throws Exception {
        when(projectRepository.findByEditorId(p1.getEditorId())).thenReturn(Optional.of(p1));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/note/text?editorId=" + p1.getEditorId()).contentType(MediaType.TEXT_PLAIN).content(LARGE)).andExpect(status().isOk());
    }

    @Test
    void addTextNoteOversized() throws Exception {
        when(projectRepository.findByEditorId(p1.getEditorId())).thenReturn(Optional.of(p1));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/note/text?editorId=" + p1.getEditorId()).contentType(MediaType.TEXT_PLAIN).content(OVERSIZED)).andExpect(status().isBadRequest());
    }

    @Test
    void addTextNoteNoPermission() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/note/text?editorId=" + p1.getEditorId()).contentType(MediaType.TEXT_PLAIN).content(SMALL)).andExpect(status().isBadRequest());
    }

    @Test
    void addTextNoteViewer() throws Exception {
        when(projectRepository.findByViewerId(p1.getViewerId())).thenReturn(Optional.of(p1));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/note/text?editorId=" + p1.getViewerId()).contentType(MediaType.TEXT_PLAIN).content(SMALL)).andExpect(status().isBadRequest());
    }

    @Test
    void addTextNoteEditor() throws Exception {
        when(projectRepository.findByEditorId(p1.getEditorId())).thenReturn(Optional.of(p1));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/note/text?editorId=" + p1.getEditorId()).contentType(MediaType.TEXT_PLAIN).content(SMALL)).andExpect(status().isOk());
    }

    @Test
    void AddNoteTooMany() throws Exception {
        when(projectRepository.findByEditorId(pnMax.getEditorId())).thenReturn(Optional.of(pnMax));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/note/text?editorId=" + pnMax.getEditorId()).contentType(MediaType.TEXT_PLAIN).content(SMALL)).andExpect(status().isBadRequest());
    }

    @Test
    void addFileNoteLarge() throws Exception {
        when(projectRepository.findByEditorId(p1.getEditorId())).thenReturn(Optional.of(p1));
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/note/file?editorId=" + p1.getEditorId()).file(FLARGE)).andExpect(status().isOk());
    }

    @Test
    void addFileNoteOversized() throws Exception {
        when(projectRepository.findByEditorId(p1.getEditorId())).thenReturn(Optional.of(p1));
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/note/file?editorId=" + p1.getEditorId()).file(FOVERSIZED)).andExpect(status().isBadRequest());
    }

    @Test
    void addFileNoteExtraLarge() throws Exception {
        when(projectRepository.findByEditorId(p1.getEditorId())).thenReturn(Optional.of(p1));
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/note/file?editorId=" + p1.getEditorId()).file(FEXTRALARGE)).andExpect(status().isBadRequest());
    }

    @Test
    void addFileNoteNoPermission() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/note/file?editorId=" + p1.getEditorId()).file(FSMALL)).andExpect(status().isBadRequest());
    }

    @Test
    void addFileNoteViewer() throws Exception {
        when(projectRepository.findByViewerId(p1.getViewerId())).thenReturn(Optional.of(p1));
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/note/file?editorId=" + p1.getViewerId()).file(FSMALL)).andExpect(status().isBadRequest());
    }

    @Test
    void addFileNoteEditor() throws Exception {
        when(projectRepository.findByEditorId(p1.getEditorId())).thenReturn(Optional.of(p1));
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/note/file?editorId=" + p1.getEditorId()).file(FSMALL)).andExpect(status().isOk());
    }

    @Test
    void removeNoteNoPermission() throws Exception {
        when(noteRepository.findById(n1.getId())).thenReturn(Optional.of(n1));
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/note/" + n1.getId() + "?editorId=" + pn1.getEditorId())).andExpect(status().isBadRequest());
    }

    @Test
    void removeNoteViewer() throws Exception {
        when(projectRepository.findByViewerId(pn1.getViewerId())).thenReturn(Optional.of(pn1));
        when(noteRepository.findById(n1.getId())).thenReturn(Optional.of(n1));
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/note/" + n1.getId() + "?editorId=" + pn1.getViewerId())).andExpect(status().isBadRequest());
    }

    @Test
    void removeNoteNonExistentInRepository() throws Exception {
        when(projectRepository.findByEditorId(pn1.getEditorId())).thenReturn(Optional.of(pn1));
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/note/" + n1.getId() + "?editorId=" + pn1.getEditorId())).andExpect(status().isBadRequest());
    }

    @Test
    void removeNoteNonExistentInProject() throws Exception {
        when(projectRepository.findByEditorId(p1.getEditorId())).thenReturn(Optional.of(p1));
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/note/" + n1.getId() + "?editorId=" + p1.getEditorId())).andExpect(status().isBadRequest());
    }

    @Test
    void removeNoteNotInProject() throws Exception {
        when(projectRepository.findByEditorId(p1.getEditorId())).thenReturn(Optional.of(p1));
        when(noteRepository.findById(n1.getId())).thenReturn(Optional.of(n1));
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/note/" + n1.getId() + "?editorId=" + pn1.getEditorId())).andExpect(status().isBadRequest());
    }

    @Test
    void removeNoteValid() throws Exception {
        when(projectRepository.findByEditorId(pn1.getEditorId())).thenReturn(Optional.of(pn1));
        when(noteRepository.findById(n1.getId())).thenReturn(Optional.of(n1));
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/note/" + n1.getId() + "?editorId=" + pn1.getEditorId())).andExpect(status().isOk());
    }

    @Test
    void viewNoteNoPermission() throws Exception {
        when(noteRepository.findById(n1.getId())).thenReturn(Optional.of(n1));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/note/" + n1.getId() + "?viewerOrEditorId=" + pn1.getViewerId())).andExpect(status().isBadRequest());
    }

    @Test
    void viewNoteNonExistentInProject() throws Exception {
        when(projectRepository.findByViewerId(p1.getViewerId())).thenReturn(Optional.of(p1));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/note/" + n1.getId() + "?viewerOrEditorId=" + p1.getViewerId())).andExpect(status().isBadRequest());
    }

    @Test
    void viewNoteNonExistentInRepository() throws Exception {
        when(projectRepository.findByViewerId(pn1.getViewerId())).thenReturn(Optional.of(pn1));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/note/" + n1.getId() + "?viewerOrEditorId=" + pn1.getViewerId())).andExpect(status().isBadRequest());
    }

    @Test
    void viewNoteViewer() throws Exception {
        when(projectRepository.findByViewerId(pn1.getViewerId())).thenReturn(Optional.of(pn1));
        when(noteRepository.findById(n1.getId())).thenReturn(Optional.of(n1));
        String resp = mockMvc.perform(MockMvcRequestBuilders.get("/api/note/" + n1.getId() + "?viewerOrEditorId=" + pn1.getViewerId())).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        Note note = objectMapper.readValue(resp, Note.class);
        assertEquals(note.getType(), n1.getType());
        assertEquals(note.getData(), n1.getData());
        assertEquals(note.getId(), n1.getId());
    }

    @Test
    void viewNoteEditor() throws Exception {
        when(projectRepository.findByEditorId(pn1.getEditorId())).thenReturn(Optional.of(pn1));
        when(noteRepository.findById(n1.getId())).thenReturn(Optional.of(n1));
        String resp = mockMvc.perform(MockMvcRequestBuilders.get("/api/note/" + n1.getId() + "?viewerOrEditorId=" + pn1.getEditorId())).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        Note note = objectMapper.readValue(resp, Note.class);
        assertEquals(note.getType(), n1.getType());
        assertEquals(note.getData(), n1.getData());
        assertEquals(note.getId(), n1.getId());
    }
}