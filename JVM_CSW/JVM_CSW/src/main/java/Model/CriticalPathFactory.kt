package model
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import lombok.ToString

/**
@author josed
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
class CriticalPathFactory(val id : String,val children: List<String>, val Duration :Float)