package com.googleplay.cookey.navigation.upload

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.googleplay.cookey.App
import com.googleplay.cookey.MainActivity
import com.googleplay.cookey.R
import com.googleplay.cookey.base.BaseActivity
import com.googleplay.cookey.data.datasource.remote.api.RecipeDTO
import com.googleplay.cookey.data.repository.Repository
import com.googleplay.cookey.databinding.ActivityUpload4Binding
import com.googleplay.cookey.databinding.ActivityUploadBinding
import com.googleplay.cookey.navigation.quote.QuoteActivity

class UploadActivity4 : BaseActivity(R.layout.activity_upload4) {

    lateinit var binding: ActivityUpload4Binding

    private var themes = ArrayList<RecipeDTO.Themes>()
    private var select_cut: String? = null
    private var recipeTitle: String? = null
    private var subTitle: String? = null
    private var steps = ArrayList<RecipeDTO.Recipe>()
    private var timeString: String = ""
    private var saveFilterList = ArrayList<String>()
    private var mainFoodTagList = ArrayList<String>()
    private var subFoodTagList = ArrayList<String>()
    private var thumbnail: String? = null
    private lateinit var adapter: UploadPreviewRecipeAdapter
    val recipeResult = RecipeDTO.UploadRecipe(null, null, null, ArrayList<RecipeDTO.MainIngredients>(),ArrayList<RecipeDTO.SubIngredients>(), ArrayList<Int>(), ArrayList<RecipeDTO.Steps>(),null,null,null,null)

    private val repository = Repository()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpload4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        getItems()

        setPageImage()
        binding.btnUploadRecipePrev2.setOnClickListener {
            clickPrevButton()
        }
        binding.btnSubmit.setOnClickListener {
            clickSubmitButton()
        }
        binding.ivUploadCancel.setOnClickListener {
            clickCancelButton()
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun clickPrevButton() {
        val intent = Intent(this, QuoteActivity::class.java)
        intent.putExtra("number", select_cut)
        intent.putExtra("filter", saveFilterList)
        intent.putExtra("thumbnail", thumbnail)
        intent.putExtra("mainfood", mainFoodTagList)
        intent.putExtra("subfood", subFoodTagList)
        intent.putExtra("steps", steps)
        intent.putExtra("recipeTitle", recipeTitle)
        intent.putExtra("time", timeString)
        intent.putExtra("subtitle", subTitle)
        startActivity(intent)
        finish()
    }

    private fun clickSubmitButton() {
        setResult()

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("number", select_cut)
        intent.putExtra("filter", saveFilterList)
        intent.putExtra("thumbnail", thumbnail)
        intent.putExtra("mainfood", mainFoodTagList)
        intent.putExtra("subfood", subFoodTagList)
        intent.putExtra("steps", steps)
        intent.putExtra("recipeTitle", recipeTitle)
        intent.putExtra("time", timeString)
        intent.putExtra("subtitle", subTitle)
        intent.putExtra("cancel", "10001")
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    private fun clickCancelButton() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(
            "나중에 올릴 땐 다시 작성해야해요\n" +
                    "작성을 멈추시겠어요?"
        )
            .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("cancel", "1")
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            })
            .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
            })
        builder.show()
    }

    private fun getItems() {
        steps.clear()

        if (intent.hasExtra("number")) {
            select_cut = intent.getStringExtra("number")
        }
        if (intent.hasExtra("themes")) {
            themes = intent.getSerializableExtra("themes") as ArrayList<RecipeDTO.Themes>
        }
        if (intent.hasExtra("thumbnail")) {
            thumbnail = intent.getStringExtra("thumbnail")
        }
        if (intent.hasExtra("mainfood")) {
            mainFoodTagList = intent.getStringArrayListExtra("mainfood")!!
        }
        if (intent.hasExtra("subfood")) {
            subFoodTagList = intent.getStringArrayListExtra("subfood")!!
        }
        if (intent.hasExtra("steps")) {
            steps = intent.getSerializableExtra("steps") as ArrayList<RecipeDTO.Recipe>
        }
        if (intent.hasExtra("recipeTitle")) {
            recipeTitle = intent.getStringExtra("recipeTitle")
        }
        if (intent.hasExtra("time")) {
            timeString = intent.getStringExtra("time")!!
        }
        if (intent.hasExtra("subtitle")) {
            subTitle = intent.getStringExtra("subtitle")!!
        }
        if (intent.hasExtra("recipeTitle")) {
            recipeTitle = intent.getStringExtra("recipeTitle")
        }
    }

    private fun setResult() {
        recipeResult.title = recipeTitle.toString()
        recipeResult.description = subTitle.toString()
        recipeResult.thumbnail = thumbnail.toString()
        recipeResult.time = timeString
        recipeResult.viewCount = select_cut

        for (i in mainFoodTagList.indices) {
            recipeResult.mainIngredients?.add(RecipeDTO.MainIngredients(mainFoodTagList[i]))
        }

        for (i in subFoodTagList.indices) {
            recipeResult.subIngredients?.add(RecipeDTO.SubIngredients(subFoodTagList[i]))
        }

        for(i in steps.indices) {
            recipeResult.steps?.add(RecipeDTO.Steps(null, steps[i].comment, steps[i].image, null))
        }

        for(i in themes.indices) {
            recipeResult.themeIds?.add(themes[i].id!!)
        }

//        App.sharedPrefs.saveKakaoId("80")
        recipeResult.writerId = Integer.parseInt(App.sharedPrefs.getGoogleId())
/*        if(App.sharedPrefs.getFlag() == "1") {
        } else {
            recipeResult.writerId = Integer.parseInt(App.sharedPrefs.getGoogleId())
        }*/
        recipeResult.pid = 1

        recipeUpload()
    }

    private fun setPageImage() {
        var rv_recipe_list = findViewById(R.id.rv_upload_preview_recipe) as RecyclerView
        adapter = UploadPreviewRecipeAdapter(steps)
        rv_recipe_list.adapter = adapter
        rv_recipe_list.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_recipe_list.setHasFixedSize(true)
    }


    private fun recipeUpload() {
        repository.postRecipeUpload(recipeResult,
            success = {
                Log.d("success", "success")
            }, fail = {
                Log.d("function fail", "fail")
            })
    }
}