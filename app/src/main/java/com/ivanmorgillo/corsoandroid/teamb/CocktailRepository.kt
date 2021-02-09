package com.ivanmorgillo.corsoandroid.teamb

import com.ivanmorgillo.corsoandroid.teamb.network.CocktailAPI

/* Interfaccia Repository Pattern
 * tipo suspend --> rappresenta un'operazione di input output --> è un'operazione che può fallire
 * (fare riferimento alla gestione degli errori del workshop) o che richiede molto tempo per recuperare le info
 * bisogna quindi segnalare agli utilizzatori che questa funzione sta facendo IO e che può crashare o durare tanto */
interface CocktailRepository {
    suspend fun loadCocktails(): List<Cocktail>
}

/* Implementazione dell'interfaccia definita prima */
class CocktailRepositoryImpl(private val api: CocktailAPI) : CocktailRepository {
    override suspend fun loadCocktails(): List<Cocktail> {
        return api.loadCocktails()
    }
}

/* rappresenta una lista di ricette: lista di oggetti con un nome, immagine e id */
data class Cocktail(val name: String, val image: String, val idMeal: String)
