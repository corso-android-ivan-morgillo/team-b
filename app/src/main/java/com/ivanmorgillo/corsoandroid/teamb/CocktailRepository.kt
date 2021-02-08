package com.ivanmorgillo.corsoandroid.teamb

private const val MAXRANGE = 10

/* Interfaccia Repository Pattern
 * tipo suspend --> rappresenta un'operazione di input output --> è un'operazione che può fallire
 * (fare riferimento alla gestione degli errori del workshop) o che richiede molto tempo per recuperare le info
 * bisogna quindi segnalare agli utilizzatori che questa funzione sta facendo IO e che può crashare o durare tanto */
interface CocktailRepository {
    suspend fun loadCocktails(): List<Cocktail>
}

/* Implementazione dell'interfaccia definita prima */
class CocktailRepositoryImpl : CocktailRepository {
    override suspend fun loadCocktails(): List<Cocktail> {
        val cocktailName = "Mojito"
        val imageCocktail = "https://www.thecocktaildb.com/images/media/drink/vwxrsw1478251483.jpg"
        return (1..MAXRANGE).map {
            Cocktail(
                name = cocktailName + it,
                image = imageCocktail,
                idMeal = it.toString(),
            )
        }
    }
}

/* rappresenta una lista di ricette: lista di oggetti con un nome, immagine e id */
data class Cocktail(val name: String, val image: String, val idMeal: String)
