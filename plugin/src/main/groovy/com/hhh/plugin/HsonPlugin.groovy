package com.hhh.plugin

import com.hhh.transform.HsonTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

class HsonPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("Hello Hson!")
        project.android.registerTransform(new HsonTransform(project))
    }
}
