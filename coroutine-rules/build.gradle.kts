plugins {
    id("buildsrc.convention.detekt-rules")
}
mavenPublishing {
    publishToMavenCentral()
//    signAllPublications()
    coordinates("dev.robch.rules", "robs-rules", "1.0.2")
    pom {
        name = "Rob's Rules"
        description = "Detekt rules for preventing coroutine cancellation bugs"
        inceptionYear = "2025"
        url = "https://github.com/zirman/robs-rules/"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "zirman"
                name = "Robert Chrzanowski"
                url = "https://github.com/zirman/"
            }
        }
        scm {
            url = "https://github.com/zirman/robs-rules/"
            connection = "scm:git:git://github.com/zirman/robs-rules.git"
            developerConnection = "scm:git:ssh://git@github.com/zirman/robs-rules.git"
        }
    }
}
