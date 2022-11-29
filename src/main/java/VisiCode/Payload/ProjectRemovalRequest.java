package VisiCode.Payload;

import javax.validation.constraints.NotBlank;

public class ProjectRemovalRequest {

    @NotBlank
    private String name;

    public static ProjectRemovalRequest forTest(String name) {
        ProjectRemovalRequest r = new ProjectRemovalRequest();
        r.name = name;
        return r;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
