import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import utils.libs

class JvmToolchainConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            extensions.configure<JavaPluginExtension> {
                toolchain {
                    val version = libs.findVersion("java-toolchain").get().displayName
                    languageVersion.set(JavaLanguageVersion.of(version))
                }
            }
        }
}
