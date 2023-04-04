package com.googleplay.cookey.data.repository

import com.googleplay.cookey.data.datasource.local.LocalDataSource
import com.googleplay.cookey.data.datasource.remote.RemoteDataSource
import com.googleplay.cookey.data.datasource.remote.api.RecipeDTO

class Repository {

    private val localDataSourceImpl = LocalDataSource()
    private val remoteMovieDataSourceImpl = RemoteDataSource()

    fun getRandomRecipes(
        success: (RecipeDTO.APIResponseRecipeList) -> Unit,
        fail: (Throwable) -> Unit
    ) {
        remoteMovieDataSourceImpl.getRandomRecipesInFeed(
            success,
            fail
        )
    }

    fun getRandomRecipesInSearchFragment(
        randomCut: Int,
        success: (RecipeDTO.APIResponseRecipeList) -> Unit,
        fail: (Throwable) -> Unit
    ) {
        remoteMovieDataSourceImpl.getRandomRecipesInSearchFragment(
            randomCut,
            success,
            fail
        )
    }


    fun getResultRecipes(
        queryType: String,
        stepStart: Int?,
        stepEnd: Int?,
        time: Int?,
        startDate: String?,
        endDate: String?,
        order: String?,
        keyword: String?,
        limit: String?,
        offset: String?,
        success: (RecipeDTO.APIResponseRecipeList) -> Unit,
        fail: (Throwable) -> Unit
    ) {
        remoteMovieDataSourceImpl.getResultRecipesLatest(
            queryType,
            stepStart,
            stepEnd,
            time,
            startDate,
            endDate,
            order,
            keyword,
            limit,
            offset,
            success,
            fail
        )
    }

    fun getRecipeById(
        recipeId: Int,
        success: (RecipeDTO.APIResponseRecipeData) -> Unit,
        fail: (Throwable) -> Unit
    ) {
        remoteMovieDataSourceImpl.getRecipeById(
            recipeId,
            success,
            fail
        )
    }

    fun getCommentsById(
        recipeId: Int,
        success: (RecipeDTO.APIResponseCommentList) -> Unit,
        fail: (Throwable) -> Unit
    ) {
        remoteMovieDataSourceImpl.getCommentsById(
            recipeId,
            success,
            fail
        )
    }

    fun getHomeRecipes(
        success: (RecipeDTO.APIResponseRecipeList) -> Unit,
        fail: (Throwable) -> Unit,
        queryType: String,
        order : String
    ) {
        remoteMovieDataSourceImpl.getHomeRecipes(
            success,
            fail,
            queryType,
            order
        )
    }

    fun getMyRecipes(
        success: (RecipeDTO.APIResponseList) -> Unit,
        fail: (Throwable) -> Unit,
        queryType: String,
        token : String
    ) {
        remoteMovieDataSourceImpl.getMyRecipes(
            success,
            fail,
            queryType,
            token
        )
    }

//    fun postTimeline(
//        postInfo: ArrayList<RecipeDTO.PostItem>,
//        success: (RecipeDTO.TimelineResponse) -> Unit,
//        fail: (Throwable) -> Unit
//    ) {
//        remoteMovieDataSourceImpl.postTimeline(
//            postInfo,
//            success,
//            fail
//        )
//    }

    fun saveSearchHistory(recipe: String) {
        localDataSourceImpl.saveSearchWord(recipe)
    }

    fun deleteSearchHistory(selected: String) {
        localDataSourceImpl.deleteSearcWord(selected)
    }

    fun getSavedSearchList(): ArrayList<String> {
        return localDataSourceImpl.getSavedSearchWordList()
    }

    // 이미지 업로드
    fun postImageUpload(
        imagePath: String,
        success: (RecipeDTO.UploadImage) -> Unit,
        fail: (Throwable) -> Unit
    ) {
        remoteMovieDataSourceImpl.postImageUpload(imagePath, success, fail)
    }

    // 레시피 등록
    fun postRecipeUpload(
        recipeInfo: (RecipeDTO.UploadRecipe),
        success: (RecipeDTO.UploadRecipe) -> Unit,
        fail: (Throwable) -> Unit
    ) {
        remoteMovieDataSourceImpl.postRecipeUpload(recipeInfo, success, fail)
    }

    fun postLoginInfo(
        token: String,
        email: RecipeDTO.RequestPostLogin,
        success : (RecipeDTO.RequestPostLogin) -> Unit,
        fail : (Throwable) -> Unit
    ) {
        remoteMovieDataSourceImpl.postLoginInfo(token, email, success, fail)
    }

    fun postLoginInfo2(
        email: RecipeDTO.RequestPostLogin2,
        success : (RecipeDTO.RequestPostLogin2) -> Unit,
        fail : (Throwable) -> Unit
    ) {
        remoteMovieDataSourceImpl.postLoginInfo2(email, success, fail)
    }

    fun postJoinInfo(
        token: String,
        joinInfo : RecipeDTO.RequestJoin,
        success: (RecipeDTO.RequestJoin) -> Unit,
        fail : (Throwable) -> Unit
    ) {
        remoteMovieDataSourceImpl.postJoinInfo(token, joinInfo, success, fail)
    }

    //유저 팔로우하기
    fun userFollow(
        token: String,
        followingId : Int,
        success : (RecipeDTO.userFollow) -> Unit,
        fail : (Throwable) -> Unit
    ){
        remoteMovieDataSourceImpl.userFollow(token,followingId,success,fail)
    }


    //유저 언팔로우하기
    fun userUnFollow(
        token: String,
        followingId : Int,
        success : (RecipeDTO.userFollow) -> Unit,
        fail : (Throwable) -> Unit
    ){
        remoteMovieDataSourceImpl.userUnFollow(token,followingId,success,fail)
    }


    //팔로워 리스트
    fun getFollower(
        token : String,
        success: (RecipeDTO.UserResponse) -> Unit,
        fail : (Throwable) -> Unit
    ){
        remoteMovieDataSourceImpl.getFollower(token,success,fail)
    }

    //팔로잉 리스트
    fun getFollowing(
        token : String,
        success: (RecipeDTO.UserResponse) -> Unit,
        fail : (Throwable) -> Unit
    ){
        remoteMovieDataSourceImpl.getFollowing(token,success,fail)
    }

    //피드 리스트
    fun getFollowingFeeds(
        token : String,
        success: (RecipeDTO.APIResponseList) -> Unit,
        fail : (Throwable) -> Unit
    ){
        remoteMovieDataSourceImpl.getFollowingFeeds(token,success,fail)
    }

    fun deleteRecipes(
        recipeId: Int,
        success: (RecipeDTO.APIResponseData) -> Unit,
        fail: (Throwable) -> Unit
    ){
        remoteMovieDataSourceImpl.deleteRecipe(recipeId,success,fail)
    }

    fun postComment(
        token: String,
        commentInfo: RecipeDTO.RequestComment,
        success: (RecipeDTO.RequestComment) -> Unit,
        fail: (Throwable) -> Unit
    ) {
        remoteMovieDataSourceImpl.postComment(token, commentInfo, success, fail)
    }

//    fun postRatingUpload(
//        token: String,
//        success: (RecipeDTO.Rating) -> Unit,
//        fail: (Throwable) -> Unit
//    ) {
//        remoteMovieDataSourceImpl.postRating(token ,success, fail)
//    }
}