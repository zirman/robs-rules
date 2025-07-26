plugins {
    id("buildsrc.convention.detekt-rules")
}
group = "dev.robch.rules"
version = "1.0.0"
publishing {
    publications {
        create<MavenPublication>("robs-rules") {
            artifactId = "robs-rules"
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "myRepo"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}
