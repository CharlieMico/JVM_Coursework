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

/**
 *
 * @author Too
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
class ProjectFactory(val name: String, val status: Boolean, val email: String,val tlf: String,val teamLeader: String,val deadline: String)


@Getter
@Setter
@NoArgsConstructor
@ToString
data class Task(val id: String, val title: String, val completed: Boolean, val duration: Float, val children: List<String>)