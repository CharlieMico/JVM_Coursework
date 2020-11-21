/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Too
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class TasksModel {
    
    private String title;
    private Boolean completed;
    private Float duration;

    public TasksModel(String title, boolean completed, float duration) {
        this.title = title;
        this.completed = completed;
        this.duration = duration;
    }

    // Needed to access in scala -- It will not compile with these being only available at compile-time
    public Float getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }

}
