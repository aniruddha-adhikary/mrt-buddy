package net.adhikary.mrtbuddy.arch

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.verify.assertTrue
import kotlin.test.Test

class ArchitectureTest {
    private val scope = Konsist.scopeFromProject()

    @Test
    fun viewModelsResideUnderUiScreens() {
        scope.classes()
            .withNameEndingWith("ViewModel")
            .assertTrue { it.resideInPackage("..ui.screens..") }
    }

    @Test
    fun daosResideInDaoPackage() {
        scope.interfaces()
            .withNameEndingWith("Dao")
            .assertTrue { it.resideInPackage("..dao..") }
    }

    @Test
    fun entitiesResideInDataPackage() {
        scope.classes()
            .withNameEndingWith("Entity")
            .assertTrue {
                it.resideInPackage("..data..") ||
                    it.resideInPackage("..database..")
            }
    }
}
