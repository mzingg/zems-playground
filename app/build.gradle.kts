plugins {
    id("zems") version "1.0-SNAPSHOT"
}

version = parent!!.version

project.ext["dockerImageName"] = "hub.docker.com/repository/docker/zinggengineering/zems-playground:${project.version}"