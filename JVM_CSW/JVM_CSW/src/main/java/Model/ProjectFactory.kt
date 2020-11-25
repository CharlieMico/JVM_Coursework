/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model

import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import lombok.ToString


@Getter
@Setter
@NoArgsConstructor
@ToString
class ProjectFactory(val name: String, val status: String, val email: String,val tlf: String
                     ,val teamLeader: String,val deadline: String,val id : String,val Child: String,val children: List<String>,val Duration :Float)

