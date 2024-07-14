package com.cmgcode.minimoods.data

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MoodDatabaseTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MoodDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun when_migratingFromVersion1to2_expect_dataInValidFormat() {
        helper.createDatabase(TEST_DB_NAME, 1).apply {
            execSQL("INSERT INTO Mood (date, mood) VALUES (837129600000, 1)")
            close()
        }

        helper.runMigrationsAndValidate(TEST_DB_NAME, 2, true, MIGRATION_1_2)

        val moods = getAllMoodsFromTestDatabase()

        assertThat(moods).hasSize(1)
        assertThat(moods.first().mood).isEqualTo(1)
        assertThat(moods.first().date.time).isEqualTo(837129600000)
        assertThat(moods.first().comment).isNull()
    }

    private fun getAllMoodsFromTestDatabase(): List<Mood> {
        return Room.databaseBuilder(getApplicationContext(), MoodDatabase::class.java, TEST_DB_NAME)
            .addMigrations(MIGRATION_1_2)
            .build()
            .getMoodDao()
            .getAll()
    }

    private companion object {
        const val TEST_DB_NAME = "migration-test"
    }
}