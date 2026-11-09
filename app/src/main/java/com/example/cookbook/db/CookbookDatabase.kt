package com.example.cookbook.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cookbook.db.dao.RecipeDao
import com.example.cookbook.db.entities.RecipeEntity

@Database(entities = [RecipeEntity::class], version = 1, exportSchema = false)
abstract class CookbookDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: CookbookDatabase? = null

        fun getDatabase(context: Context): CookbookDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CookbookDatabase::class.java,
                    "cookbook_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}