package com.example.retrofitproject.repository

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.example.retrofitproject.Country

data class CountryRoomDTO(
    val countryName: String,
    val currency: String
)

@Entity(tableName = "query_country_entity")
data class QueryCountryEntity(
    @ColumnInfo(name = "currency") val currency: String?
) {
    @PrimaryKey (autoGenerate = true) var id: Int = 0
}

@Entity(tableName = "country_entity")
data class CountryEntity (
    @ColumnInfo(name = "countryName") val countryName: String,
    @ColumnInfo(name = "currency") val currency: String,
    @ColumnInfo(name = "idQuery") val idQuery: Long,
) {
    @PrimaryKey (autoGenerate = true) var id: Int = 0
}

@Dao
interface CountryRoomDao {
    @Insert
    suspend fun insertQuery(queryCountry: QueryCountryEntity): Long

    @Insert
    suspend fun insertCountry(country: List<CountryEntity>)

    @Query("SELECT country_entity.countryName, country_entity.currency " +
            "FROM country_entity INNER JOIN query_country_entity ON (query_country_entity.id=idQuery)" +
            "WHERE country_entity.currency=:currencyInput")
    suspend fun getCountry(currencyInput: String?): List<CountryRoomDTO>
}

@Database(entities = [CountryEntity::class, QueryCountryEntity::class], version = 1)
abstract class CountryDB: RoomDatabase() {
    abstract fun countryDao():CountryRoomDao
}

class RoomDecoratorCountryRepository(private val decoratedRepository: ICountryRepository,
                                     countryDB: CountryDB): ICountryRepository {

    private val countryDao = countryDB.countryDao()

    override suspend fun getCountry(currency: String): List<Country> {

        val listCountry = countryDao.getCountry(currency)

        return if(listCountry.isEmpty()) {
            val countryFromDecorator = decoratedRepository.getCountry(currency)
            val queryCountry = QueryCountryEntity(currency)
            val id = countryDao.insertQuery(queryCountry)

            countryDao.insertQuery(queryCountry)
            countryDao.insertCountry(
                countryFromDecorator.map {
                    CountryEntity(it.name, currency, id)
                }
            )
            countryFromDecorator
        } else listCountry.map { Country(it.countryName) }
    }
}