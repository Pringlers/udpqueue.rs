plugins {
    `java-library`
    `maven-publish`
}

repositories {
  mavenCentral()
  jcenter()
}

dependencies {
  implementation("com.sedmelluq:lava-common:1.1.0")
  implementation("org.slf4j:slf4j-api:1.7.25")
}


fun getPlatform(triplet: String) = when {
  triplet.startsWith("x86_64")  && "linux"   in triplet -> "linux-x86-64"
  triplet.startsWith("x86")     && "linux"   in triplet -> "linux-x86"
  triplet.startsWith("aarch64") && "linux"   in triplet -> "linux-aarch64"
  triplet.startsWith("arm")     && "linux"   in triplet -> "linux-arm"

  triplet.startsWith("x86_64")  && "windows" in triplet -> "win-x86-64"
  triplet.startsWith("x86")     && "windows" in triplet -> "win-x86"
  triplet.startsWith("aarch64") && "windows" in triplet -> "win-aarch64"
  triplet.startsWith("arm")     && "windows" in triplet -> "win-arm"
  
  triplet.startsWith("x86_64")  && "darwin"  in triplet -> "darwin"
  triplet.startsWith("x86")     && "darwin"  in triplet -> "darwin"
  triplet.startsWith("aarch64") && "darwin"  in triplet -> "darwin"
  triplet.startsWith("arm")     && "darwin"  in triplet -> "darwin"

  triplet.isEmpty() -> "linux-x86-64" // TODO: find current OS instead
  else -> throw IllegalArgumentException("Unknown platform: $triplet")
}

val processResources by tasks
val target = project.properties["target"]?.toString() ?: ""

tasks.create<Copy>("moveResources") {
  group = "build"
  val platform = getPlatform(target)

  from("../target/$target/release/")

  include {
    it.name.endsWith(".so") || it.name.endsWith(".dll") || it.name.endsWith(".dylib")
  }

  into("src/main/resources/natives/$platform")

  processResources.dependsOn(this)
}


tasks.create<Delete>("cleanNatives") {
  group = "build"
  delete(fileTree("src/main/resources/natives"))
  tasks["clean"].dependsOn(this)
}
