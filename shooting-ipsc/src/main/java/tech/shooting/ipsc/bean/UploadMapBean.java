package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UploadMapBean {

    @JsonProperty
    private String message;

    @JsonProperty
    private String path;
    
    @JsonProperty
    private String filename;

    public UploadMapBean(String path, String filename, String message, Object... objects) {
        this.path = path;
        this.filename = filename;
        this.message = String.format(message, objects);
    }

}
