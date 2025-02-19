package ru.yandex.practicum.sprint11koh25

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.Date

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SPRINT_11"
    }

    private val adapter = NewsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val uri: Uri = Uri.parse("https://myserver.com:5051/api/v1/path?text=android&take=1#last")

        Log.d(TAG, "uri.scheme ${uri.scheme}")
        Log.d(TAG, "uri.host ${uri.host}")
        Log.d(TAG, "uri.authority ${uri.authority}")
        Log.d(TAG, "uri.pathSegments ${uri.pathSegments}")
        Log.d(TAG, "uri.lastPathSegment ${uri.lastPathSegment}")
        Log.d(TAG, "uri.queryParameterNames ${uri.queryParameterNames}")
        Log.d(TAG, "uri.getQueryParameter(\"text\") ${uri.getQueryParameter("text")}")
        Log.d(TAG, "uri.fragment ${uri.fragment}")


        val itemsRv: RecyclerView = findViewById(R.id.items)
        itemsRv.adapter = adapter

        val retrofit = Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/avanisimov/sprint-11-koh-25/")
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(Date::class.java, CustomDateTypeAdapter())
                        .registerTypeAdapter(NewsItem::class.java, NewsItemTypeAdapter())
                        .create()
                )
            )
            .build()
        val serverApi = retrofit.create(Sprint11ServerApi::class.java)

        serverApi.getNews1().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                Log.i(TAG, "onResponse: ${response.body()}")
                val items = response.body()?.data?.items
                    ?.filter { it !is NewsItem.Unknown }
                adapter.items = items ?: emptyList()
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: $t")
            }

        })


        val sp = getSharedPreferences("SOME_NAME_2", Context.MODE_PRIVATE)
        sp.edit().clear().commit()
//        for (i in 10 until 30) {
//            sp.edit()
//                .putString("key" + i, "text" + i)
//                .commit()
//        }
        sp.edit()
            .putString("key", "text")
            .commit()
        sp.edit()
            .putString("a", "b")
            .commit()
    }


//    Интересно, почему при записи в SharedPreferences в цикле последовательность не сохраняется? (см код и результат ниже).
//
//    for (i in 0 until 10) {
//        sharedPrefs.edit()
//            .putString("key" + i, "text" + i)
//            .apply()
//    }
//
//
//    <?xml version='1.0' encoding='utf-8' standalone='yes' ?>
//    <map>
//    <string name="key1">text1</string>
//    <string name="key2">text2</string>
//    <string name="key0">text0</string>
//    <string name="key5">text5</string>
//    <string name="key6">text6</string>
//    <string name="key3">text3</string>
//    <string name="key4">text4</string>
//    <string name="key9">text9</string>
//    <string name="key7">text7</string>
//    <string name="key8">text8</string>
//    </map>


}

// https://raw.githubusercontent.com/avanisimov/sprint-11-koh-24/main/jsons/news_1.json

interface Sprint11ServerApi {


    @GET("main/jsons/news_2.json")
    fun getNews1(): Call<NewsResponse>
}
