package com.googleplay.cookey.detail

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.googleplay.cookey.App
import com.googleplay.cookey.R
import com.googleplay.cookey.SharedPreferenceUtil
import com.googleplay.cookey.data.datasource.remote.api.RecipeDTO
import com.googleplay.cookey.data.repository.Repository
import com.googleplay.cookey.databinding.FragmentDetailBinding
import com.googleplay.cookey.navigation.home.HomeMultiViewAdapter
import com.googleplay.cookey.navigation.quote.QuoteActivity
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator


class DetailFragment : Fragment() {
    lateinit var binding: FragmentDetailBinding
    private lateinit var v: View

    internal lateinit var viewPagerPics: ViewPager
    internal lateinit var rvSteps: RecyclerView
    internal lateinit var tabLayout: WormDotsIndicator

    internal lateinit var ibRating: ImageView

    private lateinit var picsAdapter: DetailViewPagerPicsAdapter
    private lateinit var StepDescriptionAdapter: DetailStepsAdapter
    private lateinit var commentAdapter: DetailCommentAdapter
    private lateinit var tagAdapter: DetailTagAdapter
    private lateinit var mainIngredientAdapter: DetailMainIngredientAdapter
    private lateinit var subIngredientAdapter: DetailSubIngredientAdapter

    private var recipeId: Int = 0
    private var thumbnailURL: String = ""
    private var historyWord: String? = ""
    private var floatRatingAvgRound: Float = 0f

    private val repository = Repository()

    private lateinit var btn_follow : Button

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_detail, container, false)
        binding = FragmentDetailBinding.inflate(layoutInflater)


        getRecipeIdFromBeforeFragment()

        setViewPagerPics()
        setStarRatingButton()
        setTagRecyclerView()
        setMainIngredients()
        setSubIngredients()
        setCommentRecyclerView()
        setViewPagerSteps()
        setCommentProfilePic()
        setPostComment()

        requestRecipeById(recipeId)
        requestCommentById(recipeId)
        binding = FragmentDetailBinding.inflate(layoutInflater)
        return v
    }


    /**  API Reciep Data 요청(id로) **/
    private fun requestRecipeById(recipeId: Int) {
        repository.getRecipeById(
            recipeId,
            success = {
                it.run {
                    val userId = it.data?.writer?.id
                    val userProfile = it.data?.writer?.imageUrl
                    callFollow(userId!!)
                    setProfilePic(userProfile!!)
                    val tvNickname = v.findViewById<TextView>(R.id.tv_uploader_name)
                    val tvTitle = v.findViewById<TextView>(R.id.tv_detail_title)
                    val tvDescription = v.findViewById<TextView>(R.id.tv_introduction)
                    val tvStarAverage = v.findViewById<TextView>(R.id.tv_star_rating)
                    val tvViewCount = v.findViewById<TextView>(R.id.tv_viewcount)
                    val tvRating = v.findViewById<TextView>(R.id.tv_star_rating2)

                    val tvTime = v.findViewById<TextView>(R.id.tv_time)


                    val btnQuote = v.findViewById<Button>(R.id.btn_quote)


                    tvNickname.text = it.data?.writer?.name
                    tvTitle.text = it.data?.title
                    tvDescription.text = it.data?.description
                    tvViewCount.text = it.data?.viewCount
                    tvTime.text = it.data?.time

                    setRatingBar(it.data?.starCount!!)
                    tvRating.text = "이 레시피는 ${floatRatingAvgRound}점 이에요!"
                    tvStarAverage.text = floatRatingAvgRound.toString()



                    val num = it.data?.themes?.size
                    btnQuote.setOnClickListener {
                        if (num != null) {
                            clickQuoteButton(num - 1)
                        }
                    }

                    // 어댑터 설정
                    picsAdapter.recipeImages.add(RecipeDTO.Steps(555, "plzplzplz",it.data.thumbnail, "plzplzplzplz"))
                    picsAdapter.recipeImages.addAll(it.data?.steps!!)
                    StepDescriptionAdapter.updateDescription(it.data?.steps!!)
                    tagAdapter.updateThemes(it.data.themes)
                    mainIngredientAdapter.updateMainIngredients(it.data.mainIngredients)
                    subIngredientAdapter.updateSubIngredients(it.data.subIngredients)

                    picsAdapter.notifyDataSetChanged()
                    StepDescriptionAdapter.notifyDataSetChanged()
                    tagAdapter.notifyDataSetChanged()
                    mainIngredientAdapter.notifyDataSetChanged()
                    subIngredientAdapter.notifyDataSetChanged()
                }
            },
            fail = {
                Log.e("getRecipeById", "DetailFragment")
            }
        )
    }

    private fun requestCommentById(recipeId: Int) {
        repository.getCommentsById(
            recipeId,
            success = {
                it.run {
                    commentAdapter.updateComments(it.list!!)

                    commentAdapter.notifyDataSetChanged()
                }
            },
            fail = {
                Log.e("getCommentById", "DetailFragment")
            }

        )
    }

    private fun callFollow(userID : Int) {
        btn_follow = v.findViewById(R.id.btn_follow)
        btn_follow.setOnClickListener {
            repository.userFollow(
                success = {
                    it.run {
                        Log.d("Detial Fragment","${userID}번 유저 팔로우 하셨습니다!")
                    }
                },
                fail = {
                    Log.d("fail", "fail fail fail")
                },
                token = SharedPreferenceUtil(App.instance).getToken().toString(),
                followingId =userID
            )
        }
    }
    private fun setProfilePic(userProfile : String) {

        val imageUrl = SharedPreferenceUtil(App.instance).getImage()
        val userProfile = v.findViewById<ImageView>(R.id.iv_uploader_profile)

        Glide.with(App.instance)
            .load(imageUrl)
            .placeholder(R.drawable.ic_face)
            .into(userProfile)
    }

    private fun setCommentProfilePic() {//TODO - 추후에 User API 배포 후 댓글 프로필 사진 설정
        val commentProfilePic = v.findViewById<ImageView>(R.id.iv_comment_profile)
        Glide.with(App.instance)
            .load(this@DetailFragment)
            .placeholder(R.drawable.ic_face)
            .into(commentProfilePic)

        val profilePic = v.findViewById<ImageView>(R.id.iv_uploader_profile)
        profilePic.setImageURI(Uri.parse(SharedPreferenceUtil(App.instance).getImage()))
    }

    /**  이전 Fragment에서 클릭된 Recipe Id 가져옴**/
    // arguments!!
    // requireArguments()
    private fun getRecipeIdFromBeforeFragment() {
        recipeId = requireArguments().getInt("recipeId") // 전달한 key 값
        historyWord = requireArguments().getString("history")
    }

    private fun setRatingBar(ratingAverage: Double) {
        val floatRatingAvg = ratingAverage.toFloat()
        val ratingBar2 = v.findViewById(R.id.ratingbar2) as RatingBar
        val stars = ratingBar2.progressDrawable as LayerDrawable
        stars.getDrawable(2).setTint(Color.rgb(255,217,81))
        floatRatingAvgRound = Math.round(floatRatingAvg*10)/10f
        ratingBar2.rating= floatRatingAvgRound
    }

    /**  매인 재료 RecyclerView  **/
    private fun setMainIngredients() {
        mainIngredientAdapter = DetailMainIngredientAdapter()

        val rvMainIngredient = v.findViewById<RecyclerView>(R.id.rv_main_ingredient)
        rvMainIngredient.layoutManager = LinearLayoutManager(v.context, LinearLayoutManager.HORIZONTAL,false)
        rvMainIngredient.setHasFixedSize(true)
        rvMainIngredient.adapter = mainIngredientAdapter
    }

    private fun setSubIngredients() {
        subIngredientAdapter = DetailSubIngredientAdapter()

        val rvSubIngredient = v.findViewById<RecyclerView>(R.id.rv_sub_ingredient)
        rvSubIngredient.layoutManager = LinearLayoutManager(v.context, LinearLayoutManager.HORIZONTAL, false)
        rvSubIngredient.setHasFixedSize(true)
        rvSubIngredient.adapter = subIngredientAdapter
    }

    /**  RecyclerView : 요리순서  **/
    private fun setViewPagerSteps() {
        StepDescriptionAdapter = DetailStepsAdapter()

        rvSteps = v.findViewById<RecyclerView>(R.id.rv_steps)
        rvSteps.layoutManager = LinearLayoutManager(v.context, LinearLayoutManager.VERTICAL, false)
        rvSteps.setHasFixedSize(true)
        rvSteps.adapter = StepDescriptionAdapter

    }

    /**  RecyclerView : 댓글  **/
    private fun setCommentRecyclerView() {
        commentAdapter = DetailCommentAdapter()

        val rvComment = v.findViewById<RecyclerView>(R.id.rv_comment)
        rvComment.layoutManager = LinearLayoutManager(v.context, LinearLayoutManager.VERTICAL,false)
        rvComment.setHasFixedSize(true)
        rvComment.adapter = commentAdapter
    }

    /**  RecyclerView : 태그 버튼  **/
    private fun setTagRecyclerView() {
        tagAdapter = DetailTagAdapter()

        val rvTag = v.findViewById<RecyclerView>(R.id.rv_tag)
        rvTag.layoutManager = LinearLayoutManager(v.context, LinearLayoutManager.HORIZONTAL, false)
        rvTag.setHasFixedSize(true)
        rvTag.adapter = tagAdapter
    }


    private fun setStarRatingButton() {
        ibRating = v.findViewById<ImageView>(R.id.iv_like)
        ibRating.setOnClickListener {//평점주기 버튼 클릭 리스너

            val dialog = Dialog(v.context);
            dialog.setCancelable(false);
            // 다이얼로그 화면 설정
            dialog.setContentView(R.layout.dialogue_star_rating)
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

            val rating : RatingBar = dialog.findViewById(R.id.ratingbar_dialogue);
            val btn_ok : Button = dialog.findViewById(R.id.btn_ok);
            val btn_cancel : Button = dialog.findViewById(R.id.btn_cancel)

            rating.setRating(3f) // 레이팅 바에 기본값 채우기
            rating.setIsIndicator(false)// 사용자가 임의로 별점을 바꿀수 있도록 허가하는 메서드
            rating.setStepSize(1f)// 한칸당 1 점으로 할당

            val starsInRatingDialogue = rating.progressDrawable as LayerDrawable
            starsInRatingDialogue.getDrawable(2).setTint(Color.rgb(255, 217, 81))

            btn_ok.setOnClickListener {
                dialog.dismiss()
                ibRating.setBackgroundResource(R.drawable.rating_star_clicked);
                //TODO : 서버 요청
                // 정방향 실행
//                repository.postRating(){
//                    repository.postRatingUpload( RecipeDTO.Rating,
//                        success = {
//                            Log.d("success", "success")
//                        }, fail = {
//                            Log.d("function fail", "fail")
//                        })
//                }

            }
            btn_cancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show();
        }
    }

    private fun setViewPagerPics() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater
                .from(context)
                .inflateTransition(R.transition.shared_image)// 바꿀 수 있음
        }
        viewPagerPics = v.findViewById<ViewPager>(R.id.vp_recipes)
        ViewCompat.setTransitionName(viewPagerPics, "@string/transition_random_to_detail")

        tabLayout = v.findViewById<WormDotsIndicator>(R.id.tab_layout)

        picsAdapter = DetailViewPagerPicsAdapter(v.context)
        viewPagerPics.adapter = picsAdapter

        tabLayout.setViewPager(viewPagerPics)
//        tabLayout.setupWithViewPager(viewPagerPics)//Circle Indicator 추가
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_image)
    }

    private fun clickQuoteButton(number: Int?) {
        val intent = Intent(App.instance, QuoteActivity::class.java)
        intent.putExtra("recipeId", recipeId)
        intent.putExtra("number", number)
        startActivity(intent)
    }

    private fun setPostComment() {
        val btnPostComment = v.findViewById<Button>(R.id.btn_comment_confirm)
        //val editText = v.findViewById<EditText>(R.id.edt_comment)
        val imageUrl = SharedPreferenceUtil(App.instance).getImage()
//        val comment = binding.edtComment.text.toString()

//        val comment = editText.text.toString()
//        if(editText.text.toString().length == 0) {
//            Toast.makeText(this@DetailFragment, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
//        } else {
//            val commentDTO = RecipeDTO.RequestComment(1, recipeId, editText.text.toString(), imageUrl, 1)
//        }
//        val commentDTO = RecipeDTO.RequestComment(1, recipeId, editText.text.toString(), imageUrl, 1)
//        val commentDTO = RecipeDTO.RequestComment(1, recipeId, comment, imageUrl, 1)
//        commentDTO.content = editText.text.toString()
        btnPostComment.setOnClickListener {
            val editText = v.findViewById<EditText>(R.id.edt_comment)
            val comment = editText.text.toString()
            val commentDTO = RecipeDTO.RequestComment(1, recipeId, comment, imageUrl, 1)
            Log.d("lsy", "무엇이 적혔을까요? ${editText.text.toString()}")
            Log.d("lsy", "마찬가지겠지만 comment에는 무엇이 적혔을까요? $comment")
            Log.d("lsy", "commentDto에는 무엇이 들어있나요? ${commentDTO.toString()}")
            repository.postComment(
                token = SharedPreferenceUtil(App.instance).getToken().toString(),
                commentInfo = commentDTO,
                success = {
                    val comment = RecipeDTO.Comment("","",
                        1,
                        it.content,
                        it.imageUrl,
                        RecipeDTO.Writer(
                            Integer.parseInt(SharedPreferenceUtil(App.instance).getGoogleId()),
                            SharedPreferenceUtil(App.instance).getEmail(),
                            SharedPreferenceUtil(App.instance).getImage(),
                            SharedPreferenceUtil(App.instance).getName()
                        ),
                        1
                    )

                    commentAdapter.addComment(comment)
                    commentAdapter.notifyDataSetChanged()
                },
                fail = {
                    Log.d("function fail", "fail")
                }
            )
        }
    }
}