package com.example.a26apigooglemap

import android.app.Application
import android.content.Context
import com.example.a26apigooglemap.Request.ApiClient
import com.example.a26apigooglemap.Request.Repository
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

//    override fun onCreate() {
//        super.onCreate()
//        appContext = applicationContext
//        repository = Repository(client = ApiClient())
//    }
//
//    companion object {
//        lateinit var appContext: Context
//        lateinit var repository: Repository
//    }
}
//Додаток-гід для визначення цікавих та визначних місць в заданому радіусі. Реєстрація
// в Firebase, додавання гугл карт в додаток, формування оптимального маршруту через Google Places API
//
//- Додати логування через  GoogerAuthenticator
//- Якщо юзер залогований перейти до екрану пошуку та вказання радіусу пошуку
//- Здійснити пошук за місцеположенням використовуючи даний ендпойнт
// https://maps.googleapis.com/maps/api/place/nearbysearch/json
//&location=широта, довгота
//&radius=радіус пошуку
//&type=tourist_attraction
//&key=YOUR_API_KEY
//- Відобразити результати у вигляді списку в окремому фрагменті (картинка та назва)
//- При натисканні на FAB відобразити маршрут на карті
//- Додати swipeRefreshLayout для оновлення  списку
//- Так як гугл-апі не завжди коректно повертає визначні місця, як альтернативу,
// можна обрати geoapify.com, зареєструватись там, отримати апі-ключ та здійснити такий запит:
//https://api.geoapify.com/v2/places?categories=tourism.sights&filter=circle:широта,
// довгота,радіус&limit=20&apiKey=ваш_api_key
//
//Для захисту фінального проекту студентам необхідно представити результат роботи у
// форматі короткої вільної презентації, розповісти про результат роботи, із яким
// викликом ви зіштовхнулися у процесі розробки (за наявності такого), які ідеї
// реалізували тощо. Під час презентації платформи ви зможете отримати питання від
// лектора та інших студентів та задати їх іншим, поділитися враженнями, досвідом тощо.