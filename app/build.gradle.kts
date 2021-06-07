plugins {
    id("zems") version "1.0-SNAPSHOT"
}

version = parent!!.version

sourceSets {
    main {
        java {
            srcDir("src/main/java")
            srcDir("src/main/ops")
            resources {
                srcDir("src/main/components")
                srcDir("src/main/modules")
                srcDir("src/main/canvas")
            }
        }
    }

    test {
        java {
            srcDir("src/test/java")
        }
    }
}

docker {
    name = "hub.docker.com/repository/docker/zinggengineering/zems-playground:${project.version}"

    setDockerfile(layout.projectDirectory.file("src/main/ops/docker/zems-app/Dockerfile").asFile)
    files(tasks.getByName("bootJar").outputs)

    buildArgs(
      mapOf(
        "APPLICATION_JAR" to "app-${project.version}.jar"
      )
    )
}

tasks.register<Copy>("copyComposeFile") {
    group = "docker"
    from(layout.projectDirectory.file("src/main/ops/docker/docker-compose.yml.template"))
    expand(
      "imageReference" to "hub.docker.com/repository/docker/zinggengineering/zems-playground:${project.version}"
    )
    rename { fileName -> "docker-compose.yml" }
    into(layout.buildDirectory.dir("dist/release-${project.version}"))
}

tasks.register<Copy>("copyEnvFile") {
    group = "docker"
    from(layout.projectDirectory.file("src/main/ops/docker/env.template"))
    rename { fileName -> ".env" }
    into(layout.buildDirectory.dir("dist/release-${project.version}"))
}

tasks.getByName("docker") {
    dependsOn("copyComposeFile")
    dependsOn("copyEnvFile")
}