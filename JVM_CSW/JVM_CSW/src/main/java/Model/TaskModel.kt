package model

import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import lombok.ToString

@Getter
@Setter
@NoArgsConstructor
@ToString
data class Task(val id: String, val title: String, val completed: Boolean, val duration: Float, val children: List<String>)