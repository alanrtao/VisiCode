package VisiCode.Payload;

import javax.validation.constraints.NotBlank;

public class ProjectCreationRequest {
    @NotBlank
    private String name;

    public static ProjectCreationRequest forTest(String name) {
        ProjectCreationRequest req = new ProjectCreationRequest();
        req.name = name;
        return req;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
