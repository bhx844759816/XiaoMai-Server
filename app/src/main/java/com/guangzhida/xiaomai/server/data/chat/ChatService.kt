package com.guangzhida.xiaomai.server.data.chat

import com.guangzhida.xiaomai.server.base.BaseResult
import com.guangzhida.xiaomai.server.model.ChatUserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ChatService {


    /**
     * 根据用户UserName获取用户对象
     */
    @FormUrlEncoded
    @POST("user/get_user_by_nickorphone")
    suspend fun getChatUserModel(@Field("nickOrPhone") nickOrPhone: String): BaseResult<List<ChatUserModel>>

    @FormUrlEncoded
    @POST("user/get_user_by_nickorphone")
    fun getChatUserModelCall(@Field("nickOrPhone") nickOrPhone: String): Call<BaseResult<List<ChatUserModel>>>

    /**
     *获取好友列表
     */
    @POST("user/get_userfriends_by_userId")
    suspend fun getFriendList(): BaseResult<List<ChatUserModel>>

    /**
     * 同意或拒绝好友
     * @param isAgree 1同意 0不同意
     */
    @FormUrlEncoded
    @POST("user/agreeFriends")
    suspend fun agreeAddFriend(@Field("friendId") friendId: String, @Field("isAgree") isAgree: String): BaseResult<String>

    /**
     * 发送好友验证消息
     */
    @FormUrlEncoded
    @POST("user/sendFriendsMessage")
    suspend fun sendAddFriends(@Field("friendId") friendId: String, @Field("message") message: String): BaseResult<String>


    /**
     * 移除好友
     * /user/remove_friends
     */
    @FormUrlEncoded
    @POST("user/remove_friends")
    suspend fun removeFriend(@Field("friendId") friendId: String): BaseResult<String>


    /**
     * 发送文本消息
     */
    @FormUrlEncoded
    @POST("user/send_msg")
    suspend fun sendTextMsg(@Field("friendId") friendId: String, @Field("context") context: String): BaseResult<String>

//    /**
//     * 发送语音图片消息
//     * friendId 好友ID
//     * fileType 文件类型
//     * file 文件
//     */
//    @Headers("Content-type:application/json")
//    @POST("user/sendPicAndAudioFileMsg")
//    suspend fun sendPicOrVoiceMsg(@Body body: RequestBody): BaseResult<String>
    /**
     * 发送语音图片消息
     * friendId 好友ID
     * fileType 文件类型
     * file 文件
     */
    @Multipart
    @POST("user/sendPicAndAudioFileMsg")
    suspend fun sendPicOrVoiceMsg(
        @Part photo: MultipartBody.Part,
        @Part("friendId") friendId: RequestBody, @Part("fileType") fileType: RequestBody
    ): BaseResult<String>

    /**
     * 根据昵称和手机号获取用户信息
     * @param nickName 用户昵称
     * @param phone
     */
    @FormUrlEncoded
    @POST("user/get_user_by_nickname")
    suspend fun getUserInfoByNickNameOrPhone(@Field("nickName") nickName: String = "", @Field("mobilePhone") phone: String = ""): BaseResult<List<ChatUserModel>>


    @FormUrlEncoded
    @POST("user/get_user_by_nickorphone")
    suspend fun getUserInfoByNickNameOrPhone(@Field("nickOrPhone") nickOrPhone: String): BaseResult<List<ChatUserModel>>


}