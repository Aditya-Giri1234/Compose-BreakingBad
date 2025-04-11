/*
 * Copyright 2021 shinhyo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.shinhyo.brba.core.data.repository

import io.github.shinhyo.brba.core.data.model.asEntity
import io.github.shinhyo.brba.core.database.dao.CharacterDao
import io.github.shinhyo.brba.core.database.model.CharacterEntity
import io.github.shinhyo.brba.core.database.model.asExternalModel
import io.github.shinhyo.brba.core.domain.repository.CharactersRepository
import io.github.shinhyo.brba.core.model.BrbaCharacter
import io.github.shinhyo.brba.core.network.NetworkDataSource
import io.github.shinhyo.brba.core.network.model.CharacterResponse
import io.github.shinhyo.brba.core.network.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

open class CharactersRepositoryImpl @Inject constructor(
    private val api: NetworkDataSource,
    private val dao: CharacterDao,
) : CharactersRepository {

    val character = BrbaCharacter(
        charId = 0,
        name = "Walter White",
        birthday = "09-07-1958",
        img = "https://images.amcnetworks.com/amc.com/wp-content/uploads/2015/04/cast_bb_700x1000_walter-white-lg.jpg",
        status = "Presumed dead",
        nickname = "Heisenberg",
        portrayed = "",
        category = listOf("Breaking Bad"),
        ratio = 1.2f,
        isFavorite = true,
    )
    private val characters = listOf(
        character,
        character.copy(charId = 1, ratio = 1.8f),
        character.copy(charId = 2, ratio = 1.6f, isFavorite = false , img = "https://as1.ftcdn.net/v2/jpg/09/72/73/48/1000_F_972734871_rz66twiRga8ttVWzZuxC6p2CkAFJQmKB.jpg"),
        character.copy(charId = 3, ratio = 1.4f, isFavorite = false),
        character.copy(charId = 4, ratio = 1.2f, isFavorite = false),
        character.copy(charId = 5, ratio = 1.8f, isFavorite = true),
    )

/*    override fun getCharacterList(): Flow<List<BrbaCharacter>> = flow { emit(api.getCharacter()) }
        .map {
            it.map(CharacterResponse::asExternalModel)
        }*/

    override fun getCharacterList(): Flow<List<BrbaCharacter>> = flow { emit(characters) }

    override fun getCharacterList(id: Long): Flow<BrbaCharacter> =
        flow { emit(api.getCharacter(id)) }
            .map { it.first().asExternalModel() }

    override fun getDatabaseList(isAsc: Boolean): Flow<List<BrbaCharacter>> =
        dao.getCharacter(isAsc = isAsc)
            .map { it.map(CharacterEntity::asExternalModel) }

    override fun getDatabaseList(id: Long): Flow<BrbaCharacter?> = dao.getCharacter(charId = id)
        .map { it?.asExternalModel() }

    override fun updateFavorite(character: BrbaCharacter): Flow<Boolean> = flowOf(character)
        .map { it.asEntity().copy(favorite = !character.isFavorite) }
        .map { dao.insert(it) }
        .map { it != 0L }
}