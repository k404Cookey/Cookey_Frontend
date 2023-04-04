package com.googleplay.cookey.navigation.upload

//import com.skyhope.materialtagview.interfaces.TagItemListener
//import com.skyhope.materialtagview.model.TagModel

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData.Item
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.googleplay.cookey.App
import com.googleplay.cookey.MainActivity
import com.googleplay.cookey.R
import com.googleplay.cookey.base.BaseActivity
import com.googleplay.cookey.data.datasource.remote.api.RecipeDTO
import com.googleplay.cookey.data.repository.Repository
import com.googleplay.cookey.databinding.ActivityUpload2Binding
import com.googleplay.cookey.navigation.upload.UploadActivity3
import com.zhihu.matisse.internal.utils.PathUtils

class UploadActivity2 : BaseActivity(R.layout.activity_upload2) {

    lateinit var binding: ActivityUpload2Binding

    companion object {
        private const val REQUEST_GALLERY_CODE = 100
        private const val PERMISSION_CODE = 100
    }
    private var themes = ArrayList<RecipeDTO.Themes>()
    private var subTitle: String = ""
    private var recipeTitle: String = ""
    private var saveFilterList = ArrayList<String>()
    private var timeList = ArrayList<RecipeDTO.Time>()
    private var timeString: String = ""
    private var thumbnail: String? = null
    private var tempThumb: Uri? = null
    private lateinit var tagModel: MutableList<Item>
    private lateinit var tagModel2: MutableList<Item>
    //    private lateinit var tagModel: MutableList<TagModel>
//    private lateinit var tagModel2: MutableList<TagModel>
    private var mainFoodTagList = ArrayList<String>()
    private var subFoodTagList = ArrayList<String>()
    private var positionMain = -1

    private var saveImages = RecipeDTO.UploadImage("", "", "", "", "", "")
    private val repository = Repository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpload2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        textWatcher()
//        initMainFoodTagView()
//        initSubFoodTagView()
        getItems()
        addTimeFilter()
        callAdapter()

        binding.ivUploadGallery.setOnClickListener {
            checkPermissions()
        }

        binding.btnUploadRecipePrev1.setOnClickListener {
            clickPrevButton()
        }

        binding.btnUploadRecipeNext2.setOnClickListener {
            clickNextButton()
        }

        binding.ivUploadCancel.setOnClickListener {
            clickCancelButton()
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun getItems() {
        if (intent.hasExtra("recipeTitle")) {
            recipeTitle = intent.getStringExtra("recipeTitle")!!
            Log.d("title", recipeTitle.toString())
        }
        if (intent.hasExtra("filter")) {
            saveFilterList = intent.getStringArrayListExtra("filter")!!
            Log.d("savefilterList", saveFilterList.toString())
        }
        if (intent.hasExtra("subtitle")) {
            subTitle = intent.getStringExtra("subtitle")!!
            Log.d("subTitle", subTitle)
        }
        if (intent.hasExtra("themes")) {
            themes = intent.getSerializableExtra("themes") as ArrayList<RecipeDTO.Themes>
            for(i in themes.indices) {
                Log.d("themes", themes[i].id.toString() + " " + themes[i].name.toString())
            }
        }
    }

    private fun clickPrevButton() {
        val intent = Intent(this, UploadActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun clickNextButton() {
        makeTagList()

        if (checkPermissionNextButton()) {
            val intent = Intent(this, UploadActivity3::class.java)
            intent.putExtra("recipeTitle", recipeTitle)
            intent.putExtra("filter", saveFilterList)
            intent.putExtra("thumbnail", thumbnail)
            intent.putExtra("mainfood", mainFoodTagList)
            intent.putExtra("subfood", subFoodTagList)
            intent.putExtra("time", timeString)
            intent.putExtra("subtitle", subTitle)
            intent.putExtra("themes", themes)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            startActivity(intent)
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                100 -> {
                    tempThumb = data?.data
                    imageUploadToServer(PathUtils.getPath(App.instance, tempThumb))
                }
            }
        }
    }

    private fun pickFromGallery() {
        if (Build.VERSION.SDK_INT < 19) {
            val intent = Intent()
            intent.apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(intent, REQUEST_GALLERY_CODE)
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("image/*")
            Log.d("here", "here")
            startActivityForResult(intent, REQUEST_GALLERY_CODE)
        }
    }

    private fun checkPermissions() {
        var permissions = ""
        if(checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED) {
            permissions +=  Manifest.permission.READ_MEDIA_IMAGES + " "
        }
//        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//            // val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
//            permissions += Manifest.permission.READ_EXTERNAL_STORAGE + " "
//        }
//        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//            permissions +=  Manifest.permission.WRITE_EXTERNAL_STORAGE + " "
//        }
        if(TextUtils.isEmpty(permissions) == false) {
            ActivityCompat.requestPermissions(this,
                permissions.trim().split(" ").toTypedArray(), PERMISSION_CODE)
        }
        else {
            pickFromGallery()
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
                    pickFromGallery()
                } else {
                    Toast.makeText(App.instance, "권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
    private fun textWatcher() {

        binding.editTextMainfood.setOnKeyListener { v, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_COMMA && event.action == KeyEvent.ACTION_UP){
                binding.apply {
                    var tagName = editTextMainfood.text.toString()
                    tagName = tagName.substringBefore(",")
                    createChips(tagName)
                    mainFoodTagList.add(tagName)
                    Log.d("mainFoodTagList", mainFoodTagList.toString())
                    if(editTextMainfood.text != null) {
                        editTextMainfood.setText("")
                        editTextMainfood.text.clear()
                    }
                }
                return@setOnKeyListener true
            }
            false
        }

        binding.editTextSubfood.setOnKeyListener { v, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_COMMA && event.action == KeyEvent.ACTION_UP){
                binding.apply {
                    var tagName = editTextSubfood.text.toString()
                    tagName = tagName.substringBefore(",")
                    createChips2(tagName)
                    subFoodTagList.add("$tagName")
                    Log.d("subFoodTagList", subFoodTagList.toString())
                    if(editTextSubfood.text != null) {
                        editTextSubfood.setText("")
                        editTextSubfood.text.clear()
                    }
                }
                return@setOnKeyListener true
            }
            false
        }
    }
    private fun createChips(tagName:String) {
        val chip = Chip(this)
        chip.apply {
            text = tagName
            chipIcon = ContextCompat.getDrawable(
                this@UploadActivity2,
                R.drawable.ic_launcher_background)
            isChipIconVisible = false
            isCloseIconVisible = true
            isCheckable = true
            isClickable = true
            binding.apply {
                tagViewMainfood.addView(chip as View)
                chip.setOnCloseIconClickListener {
                    tagViewMainfood.removeView(chip as View)
                    mainFoodTagList.remove(tagName)
                }
            }
        }
    }
    private fun createChips2(tagName:String) {
        val chip = Chip(this)
        chip.apply {
            text = tagName
            chipIcon = ContextCompat.getDrawable(
                this@UploadActivity2,
                R.drawable.ic_launcher_background)
            isChipIconVisible = false
            isCloseIconVisible = true
            isCheckable = true
            isClickable = true
            binding.apply {
                tagViewSubfood.addView(chip as View)
                chip.setOnCloseIconClickListener {
                    tagViewSubfood.removeView(chip as View)
                    subFoodTagList.remove(tagName)
                }
            }
        }
    }
//    private fun initMainFoodTagView() {
//        var mainFood = binding.editTextMainfood.text.toString()
//        binding.editTextMainfood.addTextChangedListener {        }
//    }

//    private fun initSubFoodTagView() {
//        val listener2: TagItemListener? = null
//        binding.tagViewSubfood.initTagListener(listener2)
//        binding.tagViewSubfood.setTagList()
//        binding.tagViewSubfood.setHint("밀가루4컵, 당근2개")
//        tagModel2 = binding.tagViewSubfood.selectedTags
//    }

    private fun makeTagList() {
//        mainFoodTagList.clear()
//        subFoodTagList.clear()

//        if (tagModel != null) {
//            for (i in tagModel.indices) {
//                if (tagModel[i].tagText.toString() != "") {
//                    mainFoodTagList.add(tagModel[i].tagText.toString().replace(" ", ""))
//                }
//            }
//        }
//
//        if (tagModel2 != null) {
//            for (i in tagModel2.indices) {
//                if (tagModel2[i].tagText.toString() != "") {
//                    subFoodTagList.add(tagModel2[i].tagText.toString().replace(" ", ""))
//                }
//            }
//        }
    }

    private fun callAdapter() {
        binding.rvUploadTime.layoutManager =
            GridLayoutManager(this, 4)
        binding.rvUploadTime.setHasFixedSize(true)

        binding.rvUploadTime.adapter = UploadTimeAdapter(timeList, timeString) { position ->
            positionMain = position

            if (position == timeList.size - 1) {
                binding.tvUploadTimeSetText.visibility = View.VISIBLE
                binding.tvUploadTimeSetValue.visibility = View.VISIBLE
                funTimePicker()
            } else {
                binding.tvUploadTimeSetValue.text = "0분"
                binding.tvUploadTimeSetText.visibility = View.INVISIBLE
                binding.tvUploadTimeSetValue.visibility = View.INVISIBLE
            }

            timeString = timeList[positionMain].timeName.substring(0, 2)
            if (Integer.parseInt(timeString) == 60) {
                timeString = "59"
            }
            Log.d("timeString adapter", timeString)
        }
    }

    private fun addTimeFilter() {
        timeList.add(RecipeDTO.Time("15분 이내"))
        timeList.add(RecipeDTO.Time("30분 이내"))
        timeList.add(RecipeDTO.Time("45분 이내"))
        timeList.add(RecipeDTO.Time("60분 이내"))
        timeList.add(RecipeDTO.Time("60분 이상"))
    }

    private fun funTimePicker() {
        val dialog = AlertDialog.Builder(UploadActivity2@ this).create()
        val edialog: LayoutInflater = LayoutInflater.from(UploadActivity2@ this)
        val mView: View = edialog.inflate(R.layout.dialog_datepicker, null)
        val step: Array<String> = arrayOf(
            "1시간",
            "2시간",
            "3시간",
            "4시간",
            "5시간",
            "6시간",
            "7시간",
            "8시간",
            "9시간",
            "10시간",
            "11시간",
            "12시간",
            "13시간",
            "14시간",
            "15시간",
            "16시간",
            "17시간",
            "18시간",
            "19시간",
            "20시간",
            "21시간",
            "22시간",
            "23시간"
        )
        val step2: Array<String> = arrayOf(
            "0분",
            "1분",
            "2분",
            "3분",
            "4분",
            "5분",
            "6분",
            "7분",
            "8분",
            "9분",
            "10분",
            "11분",
            "12분",
            "13분",
            "14분",
            "15분",
            "16분",
            "17분",
            "18분",
            "19분",
            "20분",
            "21분",
            "22분",
            "23분",
            "24분",
            "25분",
            "26분",
            "27분",
            "28분",
            "29분",
            "30분",
            "31분",
            "32분",
            "33분",
            "34분",
            "35분",
            "36분",
            "37분",
            "38분",
            "39분",
            "40분",
            "41분",
            "42분",
            "43분",
            "44분",
            "45분",
            "46분",
            "47분",
            "48분",
            "49분",
            "50분",
            "51분",
            "52분",
            "53분",
            "54분",
            "55분",
            "56분",
            "57분",
            "58분",
            "59분"
        )

        val minute: NumberPicker = mView.findViewById(R.id.min_picker)
        val hour: NumberPicker = mView.findViewById(R.id.hour_picker)
        val cancel: Button = mView.findViewById(R.id.btn_time_cancel)
        val submit: Button = mView.findViewById(R.id.btn_time_submit)

        hour.apply {
            wrapSelectorWheel = true
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            minValue = 0
            maxValue = 22
            displayedValues = step
        }
        minute.apply {
            wrapSelectorWheel = true
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            minValue = 0
            maxValue = 59
            displayedValues = step2
        }

        cancel.setOnClickListener {
            binding.tvUploadTimeSetValue.text = "0분"
            dialog.dismiss()
            dialog.cancel()
        }

        submit.setOnClickListener {
            Log.d("submit", "submit")
            binding.tvUploadTimeSetValue.text = step[hour.value] + " " + step2[minute.value]

            var hour_time = step[hour.value]
            var min_time = step2[minute.value]
            var hour_len = step[hour.value].length
            var min_len = step2[minute.value].length

            val h = hour_time.substring(0, hour_len - 2)
            val m = min_time.substring(0, min_len - 1)

            Log.d("hour_time", hour_len.toString())
            Log.d("min_time", min_len.toString())


            timeString = ((Integer.parseInt(h) * 60) + Integer.parseInt(m)).toString()
            Log.d("timeString here", timeString)
            dialog.dismiss()
            dialog.cancel()
        }

        dialog.setView(mView)
        dialog.create()
        dialog.show()
    }

    private fun imageUploadToServer(imagePath: String) {
        repository.postImageUpload(imagePath,
            success = {
                saveImages.data = it.data
                thumbnail = it.data
                try {
                    Glide.with(App.instance)
                        .load(saveImages.data)
                        .into(binding.ivUploadGallery)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            },
            fail = {
                Log.d("function fail", "fail")
            }
        )
    }

    private fun checkPermissionNextButton(): Boolean {
        Log.d("timeString", timeString + "여기")
        Log.d("timeString", "mainFoodTagList 사이즈"+mainFoodTagList.size.toString())
        Log.d("timeString", "subFoodTagList 사이즈"+mainFoodTagList.size.toString())
        Log.d("timeString", "timeString"+timeString)
        Log.d("timeString", "thumbnail"+thumbnail)

        if (thumbnail != null && mainFoodTagList.size > 0 && subFoodTagList.size > 0 && timeString != "") {
            return true
        } else if (mainFoodTagList.size > 0 && subFoodTagList.size > 0 && timeString != "") {
            Toast.makeText(this, "레시피 썸네일을 등록해주세요.", Toast.LENGTH_SHORT).show()
            return false
        } else if (thumbnail != null && subFoodTagList.size > 0 && timeString != "") {
            Toast.makeText(this, "요리 필수 재료를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return false
        } else if (thumbnail != null && mainFoodTagList.size > 0 && timeString != "") {
            Toast.makeText(this, "요리 부가 재료를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return false
        } else if (thumbnail != null && mainFoodTagList.size > 0 && subFoodTagList.size > 0) {
            Toast.makeText(this, "얼마나 걸리는지 시간을 선택해주세요.", Toast.LENGTH_SHORT).show()
            return false
        }

        Toast.makeText(this, "항목을 채워주세요", Toast.LENGTH_SHORT).show()

        return false
    }

}