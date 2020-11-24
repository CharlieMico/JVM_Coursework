package model

import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import lombok.ToString

/**
 *
 * @author Too
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
class CriticalPathFactory(val id : String,val Children: List<String>,val Duration :Float)

