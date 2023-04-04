package com.googleplay.cookey.navigation.quote

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.googleplay.cookey.R
import com.googleplay.cookey.App
import com.googleplay.cookey.MainActivity
import com.googleplay.cookey.base.BaseActivity
import com.googleplay.cookey.data.datasource.local.Glide4Engine
import com.googleplay.cookey.data.datasource.remote.api.RecipeDTO
import com.googleplay.cookey.data.repository.Repository
import com.googleplay.cookey.databinding.ActivityMainBinding
import com.googleplay.cookey.databinding.ActivityQuoteBinding
import com.googleplay.cookey.navigation.upload.UploadSwapDelete
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.utils.PathUtils
import java.lang.Exception

class QuoteActivity : BaseActivity(R.layout.activity_quote) {

    open lateinit var binding: ActivityQuoteBinding

    companion object {
        private const val REQUEST_GALLERY_CODE = 100
        private const val REQUEST_GET_IMAGE = 105
        private const val PERMISSION_CODE = 100
    }
    val recipeResult = RecipeDTO.UploadRecipe(null, null, null, ArrayList<RecipeDTO.MainIngredients>(),ArrayList<RecipeDTO.SubIngredients>(), ArrayList<Int>(), ArrayList<RecipeDTO.Steps>(),null,null,null,null)
    private var saveImages = RecipeDTO.UploadImage("", "", "", "", "", "")
    private var saveCommentList = ArrayList<RecipeDTO.Recipe>()
    private var recipeList = ArrayList<RecipeDTO.Recipe>()
    private var resultCommentList = ArrayList<RecipeDTO.Recipe>()
    private var commentList = ArrayList<RecipeDTO.Recipe>()
    private var filterList = ArrayList<RecipeDTO.Themes>()
    private var select_cut: Int = 0
    private var steps = ArrayList<RecipeDTO.Steps>()
    private var number = 0
    private var quoteThumbnail: Uri? = null
    private var count = 0
    private var recipeId = 0
    private var positionMain = -1
//    private var changeThumbnail: String? = null

    lateinit var parentRecipe: RecipeDTO.APIResponseRecipeData
    private lateinit var itemMain: RecipeDTO.Recipe
    private lateinit var imageAdapter: QuoteImageAdapter
    private lateinit var commentAdapter: QuoteCommentAdapter

    private val repository = Repository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getItems()
        requestRecipeById(recipeId)
        itemTouch()
    }

    private fun getResponse(recipe : RecipeDTO.APIResponseRecipeData) {
        parentRecipe = recipe
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

    private fun addRecyclerView() {
        if (number >= 3) {
            number++
            recipeList.add(RecipeDTO.Recipe(number.toString(), null, null))
        }
    }

    private fun itemTouch() {
        val item = object : UploadSwapDelete(this, 0, ItemTouchHelper.LEFT) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (viewHolder.adapterPosition >= select_cut) {
                    commentAdapter.deleteItem(viewHolder.adapterPosition)
                    imageAdapter.deleteItem(viewHolder.layoutPosition)
                    number--
                    count--
                } else {
                    Toast.makeText(App.instance, "기존 레시피는 변경할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    commentAdapter.notifyDataSetChanged()

                }

            }
        }

        val itemTouchHelper = ItemTouchHelper(item)
        itemTouchHelper.attachToRecyclerView(binding.rvQuoteComment)
    }

    private fun addItem(position: Int, data: RecipeDTO.Recipe) {
        commentList.add(position, data)
        saveCommentList.add(position, data)
        commentAdapter.notifyDataSetChanged()
        count++
    }

    private fun getItems() {
        if (intent.hasExtra("recipeId")) {
            recipeId = intent.getIntExtra("recipeId", 1)
        }
        if(intent.hasExtra("number")) {
            number = intent.getIntExtra("number", 0)
        }
    }

    private fun pickUpGallery() {
        Matisse.from(this)
            .choose(MimeType.ofAll())
            .countable(true)
            .maxSelectable(9)
            .spanCount(3)
            //.imageEngine(GlideEngine())
            .imageEngine(Glide4Engine())
            .forResult(REQUEST_GET_IMAGE)
    }

    private fun pickUpThumbnailGallery() {
        val intent = Intent()
        intent.apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }

        startActivityForResult(intent, REQUEST_GALLERY_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_GALLERY_CODE -> {
                    quoteThumbnail = data?.data
                    try {
                        Glide.with(App.instance)
                            .load(quoteThumbnail)
                            .into(binding.ivQuoteRecipeResult)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                REQUEST_GET_IMAGE ->
                    Matisse.obtainResult(data)?.let {
                        val mSelected: List<Uri> = Matisse.obtainResult(data)
                        val size = mSelected.size
                        Log.d("positionMain", positionMain.toString())
                        Log.d("size", size.toString())
                        Log.d("inner number", number.toString())
                        if (size == 1) {
                            imageUploadToServer(PathUtils.getPath(App.instance, mSelected[0]))
                            if (number >= 3 && positionMain >= number - 1) {
                                addRecyclerView()
                                addItem(
                                    binding.rvQuoteComment.adapter!!.itemCount,
                                    RecipeDTO.Recipe(Integer.toString(count + 1), "", "")
                                )
                            }
                            imageAdapter.notifyDataSetChanged()
                        } else {
                            if (positionMain + size <= number) {
                                for (i in mSelected.indices) {
                                    imageUploadToServer(
                                        PathUtils.getPath(
                                            App.instance,
                                            mSelected[i]
                                        ), i
                                    )
                                }
                            } else if (positionMain + size > number) {
                                for (i in mSelected.indices) {
                                    imageUploadToServer(
                                        PathUtils.getPath(
                                            App.instance,
                                            mSelected[i]
                                        ), i
                                    )
                                    if (positionMain + i >= number - 1) {
                                        addRecyclerView()
                                        addItem(
                                            binding.rvQuoteComment.adapter!!.itemCount,
                                            RecipeDTO.Recipe(
                                                Integer.toString(count + 1),
                                                "",
                                                ""
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                else -> finish()
            }
        }
    }

    private fun checkPermissions() {
        if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED) {
            val permissions = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            requestPermissions(permissions, PERMISSION_CODE)
        } else {
            pickUpGallery()
        }
    }

    private fun checkThumbnailsPermissions() {
        if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED) {
            val permissions = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            requestPermissions(permissions, PERMISSION_CODE)
        } else {
            pickUpThumbnailGallery()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickUpGallery()
                } else {
                    Toast.makeText(App.instance, "권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("사진을 삭제하시겠습니까?")
            .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                if ((positionMain < count) && count > 3) {
                    imageAdapter.deleteItem(positionMain)
                    commentAdapter.deleteItem(positionMain)
                    if (positionMain == 4) {
                        addRecyclerView()
                    }
                    count--
                    number--
                } else {
                    if (count <= 3) {
                        Toast.makeText(
                            this,
                            "사진은 최소 3장 업로드 해야합니다.\n사진을 눌러 변경해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(this, "삭제하실 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }

                }
                Log.d("dialog_number", number.toString())
            })
            .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
            })
        builder.show()
    }


    private fun makeSteps(commentList:ArrayList<RecipeDTO.Recipe>, recipeList:ArrayList<RecipeDTO.Recipe>, steps: ArrayList<RecipeDTO.Recipe>, recipeResult : RecipeDTO.UploadRecipe) {
        steps.clear()
        Log.d("recipeList here!!!!!!","makeSteps속의 ${recipeList.toString()}")
        for (i in commentList.indices) {
            steps.add(
                RecipeDTO.Recipe(
                    commentList[i].number,
                    commentList[i].comment,
                    recipeList[i].image
                )
            )
        }
        Log.d("recipeResult here", recipeResult.toString() + " dsadsadasdasfdsfds" )
        Log.d("recipeResult.step here", recipeResult.steps.toString())
        Log.d( "steps here", steps.toString() + "떠라떠라떠라떠라")
        if (steps.size !=0) {
            if (recipeResult.steps?.size !=0 ) {
                for (i in steps.indices) {
                    recipeResult.steps!![i].sequence = steps[i].number
                    recipeResult.steps!![i].description = steps[i].comment
                    recipeResult.steps!![i].imageUrl = steps[i].image
                }
            }
        }
        recipeResult.title = binding.etQuoteRecipeTitle.text.toString()
//        recipeResult.thumbnail =
        recipeUpload(recipeResult)

        val intent = Intent(App.instance, MainActivity::class.java)
        intent.putExtra("cancel", "10001")
        startActivity(intent)

        Log.d("steps", steps.toString() + " 여기여기여기여")
    }

    private fun requestRecipeById(recipeId: Int){
        repository.getRecipeById(
            recipeId,
            success = {
                parentRecipe = it
                getResponse(parentRecipe)
                val ivParentPoster = findViewById<ImageView>(R.id.iv_quote_recipe_poster)
                val tvParentTitle = findViewById<TextView>(R.id.tv_quote_recipe_title)
                val tvParentFood = findViewById<TextView>(R.id.tv_quote_recipe_food)
                val btn_quote_add_comment = findViewById<Button>(R.id.btn_quote_add_comment)
                val mainfood = it.data!!.mainIngredients
                val subfood = it.data!!.subIngredients
                var food = ""
                val recipeResult = RecipeDTO.UploadRecipe(null, null, null, ArrayList<RecipeDTO.MainIngredients>(),ArrayList<RecipeDTO.SubIngredients>(), ArrayList<Int>(), ArrayList<RecipeDTO.Steps>(),null,null,null,null)
                var saveSteps = ArrayList<RecipeDTO.Recipe>()

                recipeResult.title = it?.data.title
                recipeResult.description = it?.data.description
                recipeResult.thumbnail = it?.data.thumbnail
                recipeResult.mainIngredients = it?.data.mainIngredients
                recipeResult.subIngredients = it?.data.subIngredients

                var theme = it?.data.themes
                for(i in theme.indices) {
                    recipeResult.themeIds?.add(i)
                }

                recipeResult.writerId = it?.data.writer?.id
                recipeResult.time = it?.data.time
                recipeResult.viewCount = it?.data.viewCount
                recipeResult.pid = recipeId
                recipeResult.steps = it.data?.steps
//                recipeResult.themeIds = filterList


                btn_quote_add_comment.setOnClickListener {
                    addItem(
                        binding.rvQuoteComment.adapter!!.itemCount,
                        RecipeDTO.Recipe(Integer.toString(count + 1), "", "")
                    )
                    addRecyclerView()
                    imageAdapter.notifyDataSetChanged()
                }
                binding.ivUploadCancel.setOnClickListener {
                    clickCancelButton()
                }

                binding.ivQuoteRecipeResult.setOnClickListener {
                    checkThumbnailsPermissions()
                }

                filterList = it.data!!.themes
                steps = it.data!!.steps
                Log.d("lsy", "스탭스 데이터를 가져왔니? $steps")
                select_cut = steps.size

                for (i in 0..select_cut - 1) {
                    recipeList.add(
                        RecipeDTO.Recipe(
                            (Integer.parseInt(steps[i].sequence) + 1).toString(),
                            null,
                            steps[i].imageUrl
                        )
                    )
                    commentList.add(
                        RecipeDTO.Recipe(
                            (Integer.parseInt(steps[i].sequence) + 1).toString(),
                            steps[i].description,
                            null
                        )
                    )
                    saveCommentList.add(
                        RecipeDTO.Recipe(
                            (Integer.parseInt(steps[i].sequence) + 1).toString(),
                            steps[i].description,
                            null
                        )
                    )
                    resultCommentList.add(
                        RecipeDTO.Recipe(
                            (Integer.parseInt(steps[i].sequence) + 1).toString(),
                            "",
                            null
                        )
                    )
                    number++
                    count++
                }
                Log.d("lsy", "for문이 실행된 후의 ===========$recipeList")
                if (select_cut < 9) {
                    recipeList.add(RecipeDTO.Recipe((steps.size + 1).toString(), null, null))
                    number++
                }

                for (i in mainfood.indices) {
                    if (i == 0 && i == mainfood.size - 1 && subfood.size == 0) {
                        food += mainfood[i].name
                    } else {
                        food += mainfood[i].name + ", "
                    }
                }

                for (i in subfood.indices) {
                    if (i == subfood.size - 1) {
                        food += subfood[i].name
                    } else {
                        food += subfood[i].name + ", "
                    }
                }

                binding.tvQuoteRecipeFood.text = food

                Glide.with(App.instance)
                    .load(it.data?.thumbnail)
                    .into(ivParentPoster)

                tvParentTitle.text = it.data?.title

                binding.rvQuoteFilter.apply {
                    layoutManager =
                        GridLayoutManager(App.instance, 3)
                    setHasFixedSize(true)

                    adapter = QuoteFilterAdapter(filterList)
                }

                var rv_recipe_list = findViewById(R.id.rv_quote_image) as RecyclerView
                imageAdapter = QuoteImageAdapter(recipeList) { position, item ->
                    positionMain = position
                    itemMain = item

                    if (positionMain >= select_cut) {
                        checkPermissions()
                    } else {
                    }
                }

                imageAdapter.onItemLongClick = { position, item ->
                    positionMain = position
                    itemMain = item

                    if (select_cut <= positionMain) {
                        showDialog()
                    } else {
                    }
                }

                rv_recipe_list.apply {
                    adapter = imageAdapter
                    layoutManager =
                        LinearLayoutManager(App.instance, LinearLayoutManager.HORIZONTAL, false)
                    setHasFixedSize(true)
                }

                commentAdapter = QuoteCommentAdapter(commentList, saveCommentList, select_cut)
                binding.rvQuoteComment.adapter = commentAdapter
                binding.rvQuoteComment.layoutManager =
                    LinearLayoutManager(App.instance, LinearLayoutManager.VERTICAL, false)
                binding.rvQuoteComment.setHasFixedSize(true)

                imageAdapter.notifyDataSetChanged()
                commentAdapter.notifyDataSetChanged()

                binding.btnQuoteSubmit.setOnClickListener {
                    makeSteps(commentList, recipeList, saveSteps, recipeResult)
                    // checkPermissionNextButton()
                }
            },
            fail = {
                Log.d("fail", "fail fail fail")
            }
        )
    }

    private fun imageUploadToServer(imagePath: String, position: Int) {
        repository.postImageUpload(imagePath,
            success = {
                saveImages.data = it.data
                recipeList[positionMain + position].image = it.data
                imageAdapter.notifyDataSetChanged()
//                changeThumbnail.data = it.data
                Log.d("recipeList here2!!", recipeList.toString())
            },
            fail = {
                Log.d("function fail", "fail")
            }
        )
    }

    private fun imageUploadToServer(imagePath: String) {

        repository.postImageUpload(imagePath,
            success = {
                saveImages.data = it.data
                recipeList[positionMain].image = it.data
                imageAdapter.notifyDataSetChanged()
                Log.d("recipeList here1!!", recipeList[positionMain].image.toString())
            },
            fail = {
                Log.d("function fail", "fail")
            }
        )
    }

    private fun recipeUpload(recipeResult: RecipeDTO.UploadRecipe) {
        repository.postRecipeUpload( recipeResult,
            success = {
                Log.d("success", "success")
            }, fail = {
                Log.d("function fail", "fail")
            })
    }

}