package lt.saltyjuice.dragas.chatty.v3.discord.api

import lt.saltyjuice.dragas.chatty.v3.discord.enumerated.Parameter
import lt.saltyjuice.dragas.chatty.v3.discord.message.builder.*
import lt.saltyjuice.dragas.chatty.v3.discord.message.event.EventChannelDelete
import lt.saltyjuice.dragas.chatty.v3.discord.message.event.EventGuildEmojisUpdate
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.*
import lt.saltyjuice.dragas.chatty.v3.discord.message.request.GatewayInit
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.Field
import java.io.File

/**
 * An interface for Retrofit to generate calls for DiscordAPI.
 *
 * Note: For any maps, you should use [Parameter] for keys.
 *
 * You really shouldn't call any of these methods directly (even if using retrofit client generated ones). Instead
 * use [Builder] wrappers.
 */
interface DiscordAPI
{
    @GET("gateway/bot")
    fun gatewayInit(): Call<GatewayInit>
//---------------------------------------Invites---------------------------------------------------------
    /**
     * Returns an invite object for the given code.
     */
    @GET("invites/{invite-code}")
    fun getInvite(@Path("invite-code") inviteCode: String): Call<Invite>

    /**
     * Delete an invite. Requires the MANAGE_CHANNELS permission. Returns an [Invite] object on success.
     */
    @PATCH("invites/{invite-code}")
    fun deleteInvite(@Path("invite-code") inviteCode: String): Call<Invite>

    /**
     * Accept an invite. This requires the guilds.join OAuth2 scope to be able to accept invites
     * on behalf of normal users (via an OAuth2 Bearer token).
     * Bot users are disallowed. Returns an [Invite] object on success.
     */
    @POST("invites/{invite-code}")
    fun acceptInvite(@Path("invite-code") inviteCode: String): Call<Invite>
//---------------------------------------Invites---------------------------------------------------------

//------------------------------------Guild audit logs---------------------------------------------------

    /**
     * Returns an [AuditLog] object for the guild. Requires the 'VIEW_AUDIT_LOG' permission.
     */
    @GET("guilds/{guild-id}/audit-logs")
    fun getGuildAuditLog(@Path("guild-id") guildId: String, @QueryMap map: Map<String, String>): Call<AuditLog>

    /**
     * Returns an [AuditLog] object for the guild. Requires the 'VIEW_AUDIT_LOG' permission.
     */
    @GET("guilds/{guild-id}/audit-logs")
    fun getGuildAuditLog(@Path("guild-id") guildId: String): Call<AuditLog>
//------------------------------------Guild audit logs---------------------------------------------------

//----------------------------------------Channel--------------------------------------------------------
    /**
     * Get a channel by ID. Returns a guild channel or dm channel object.
     */
    @GET("channels/{channel-id}")
    fun getChannel(@Path("channel-id") channelId: String): Call<Channel>

    /**
     * Update a channels settings. Requires the 'MANAGE_CHANNELS' permission for the guild. Returns a guild channel
     * on success, and a 400 BAD REQUEST on invalid parameters.
     * Fires a Channel Update Gateway event.
     * All the JSON Params are optional.
     */
    @PATCH("channels/{channel-id}")
    @FormUrlEncoded
    fun modifyChannelPatch(@Path("channel-id") channelId: String, @FieldMap map: Map<String, String>): Call<Channel>

    /**
     * Update a channels settings. Requires the 'MANAGE_CHANNELS' permission for the guild. Returns a guild channel
     * on success, and a 400 BAD REQUEST on invalid parameters.
     *
     * Fires a Channel Update Gateway event.
     */
    @PUT("channels/{channel-id}")
    @FormUrlEncoded
    fun modifyChannelPut(@Path("channel-id") channelId: String, @FieldMap map: Map<String, String>): Call<Channel>

    /**
     *  Delete a guild channel, or close a private message. Requires the 'MANAGE_CHANNELS' permission for the guild.
     *  Returns a guild channel or dm channel object on success.
     *  Fires a [EventChannelDelete] Gateway event.
     *
     *  Deleting a guild channel cannot be undone. Use this with caution, as it is impossible to
     *  undo this action when performed on a guild channel.
     *  In contrast, when used with a private message, it is possible to undo the action
     *  by opening a private message with the recipient again.
     */

    @DELETE("channels/{channel-id}")
    fun deleteChannel(@Path("channel-id") channelId: String): Call<Channel>

    /**
     * Returns the messages for a channel. If operating on a guild channel,
     * this endpoint requires the 'READ_MESSAGES' permission to be present
     * on the current user. Returns an array of message objects on success.
     *
     * The [Parameter.BEFORE], [Parameter.AFTER], and [Parameter.AROUND] keys are mutually exclusive, only one may be passed at a time.
     */
    @GET("channels/{channel-id}/messages")
    fun getChannelMessages(@Path("channel-id") channelId: String, @QueryMap map: Map<String, String>): Call<List<Message>>


    /**
     * Returns a specific message in the channel. If operating on a guild channel, this endpoints requires
     * the 'READ_MESSAGE_HISTORY' permission to be present on the current user. Returns a message object on success.
     */
    @GET("channels/{channel-id}/messages/{message-id}")
    fun getChannelMessage(@Path("channel-id") channelId: String, @Path("message-id") messageId: String): Call<Message>

    /**
     * Post a message to a guild text or DM channel. If operating on a guild channel, this endpoint requires the
     * 'SEND_MESSAGES' permission to be present on the current user.
     * Returns a message object. Fires a Message Create Gateway event. See [message formatting](https://discordapp.com/developers/docs/reference#message-formatting) for more information
     * on how to properly format messages.
     */
    @POST("channels/{channel-id}/messages")
    @Multipart
    fun createMessage(@Path("channel-id") channelId: String, @Part("file") file: File): Call<Message>

    /**
     * Post a message to a guild text or DM channel. If operating on a guild channel, this endpoint requires the
     * 'SEND_MESSAGES' permission to be present on the current user.
     *
     * Returns a message object. Fires a Message Create Gateway event. See [message formatting](https://discordapp.com/developers/docs/reference#message-formatting) for more information
     * on how to properly format messages.
     */
    @POST("channels/{channel-id}/messages")
    fun createMessage(@Path("channel-id") channelId: String, @Body message: MessageBuilder): Call<Message>

    /**
     * Post a message to a guild text or DM channel. If operating on a guild channel, this endpoint requires the
     * 'SEND_MESSAGES' permission to be present on the current user.
     *
     * Returns a message object. Fires a Message Create Gateway event. See [message formatting](https://discordapp.com/developers/docs/reference#message-formatting) for more information
     * on how to properly format messages.
     */
    @POST("channels/{channel-id}/messages")
    fun createMessage(@Path("channel-id") channelId: String, @Body embed: EmbedWrapper): Call<Message>


    /**
     * Post a message to a guild text or DM channel. If operating on a guild channel, this endpoint requires the
     * 'SEND_MESSAGES' permission to be present on the current user.
     *
     * Returns a message object. Fires a Message Create Gateway event. See [message formatting](https://discordapp.com/developers/docs/reference#message-formatting) for more information
     * on how to properly format messages.
     */
    @POST("channels/{channel-id}/messages")
    @FormUrlEncoded
    fun createMessage(@Path("channel-id") channelId: String, @Field("content") message: String): Call<Message>

    /**
     * Create a reaction for the message. This endpoint requires the 'READ_MESSAGE_HISTORY' permission to be present
     * on the current user. Additionally, if nobody else has reacted to the message using this emoji,
     * this endpoint requires the 'ADD_REACTIONS' permission to be present on the current user.
     * Returns a 204 empty response on success.
     */
    @PUT("channels/{channel-id}/messages/{message-id}/reactions/{emoji}/@me")
    fun addReaction(@Path("channel-id") channelId: String, @Path("message-id") messageId: String, @Path("emoji") emojiId: String): Call<Unit>

    /**
     * Delete a reaction the current user has made for the message. Returns a 204 empty response on success.
     */
    @DELETE("channels/{channel-id}/messages/{message-id}/reactions/{emoji}/@me")
    fun deleteOwnReaction(@Path("channel-id") channelId: String, @Path("message-id") messageId: String, @Path("emoji") emojiId: String): Call<Unit>

    /**
     * Deletes another user's reaction. This endpoint requires the 'MANAGE_MESSAGES' permission to be
     * present on the current user. Returns a 204 empty response on success.
     */
    @DELETE("channels/{channel-id}/messages/{message-id}/reactions/{emoji}/{user-id}")
    fun deleteUserReaction(@Path("channel-id") channelId: String, @Path("message-id") messageId: String, @Path("emoji") emojiId: String, @Path("user-id") userId: String): Call<Unit>

    /**
     * Get a list of users that reacted with this emoji. Returns an array of [User] objects on success.
     */
    @GET("channels/{channel-id}/messages/{message-id}/reactions/{emoji}")
    fun getUsersForReaction(@Path("channel-id") channelId: String, @Path("message-id") messageId: String, @Path("emoji") emojiId: String): Call<List<User>>

    /**
     * Deletes all reactions on a message. This endpoint requires the 'MANAGE_MESSAGES'
     * permission to be present on the current user.
     */
    @DELETE("channels/{channel-id}/messages/{message-id}/reactions")
    fun deleteAllReactions(@Path("channel-id") channelId: String, @Path("message-id") messageId: String): Call<Unit>

    /**
     * Edit a previously sent message. You can only edit messages that have been sent by the current user.
     * Returns a message object. Fires a Message Update Gateway event.
     */
    @PATCH("channels/{channel-id}/messages/{message-id}")
    @FormUrlEncoded
    fun editMessage(@Path("channel-id") channelId: String, @Path("message-id") messageId: String, @Field("content") content: String): Call<Message>

    /**
     * Edit a previously sent message. You can only edit messages that have been sent by the current user.
     * Returns a message object. Fires a Message Update Gateway event.
     */
    @PATCH("channels/{channel-id}/messages/{message-id}")
    @FormUrlEncoded
    fun editMessage(@Path("channel-id") channelId: String, @Path("message-id") messageId: String, @Field("embed") embed: Embed): Call<Message>


    /**
     * Delete a message. If operating on a guild channel and trying to delete a message that
     * was not sent by the current user, this endpoint requires the 'MANAGE_MESSAGES' permission.
     * Returns a 204 empty response on success. Fires a Message Delete Gateway event.
     */
    @DELETE("channels/{channel-id}/messages/{message-id}")
    fun deleteMessage(@Path("channel-id") channelId: String, @Path("message-id") messageId: String): Call<Unit>

    /**
     *Delete multiple messages in a single request.
     * This endpoint can only be used on guild channels and requires the 'MANAGE_MESSAGES' permission.
     * Returns a 204 empty response on success. Fires multiple Message Delete Gateway events.
     *
     * The gateway will ignore any individual messages that do not exist or do not belong to this channel,
     * but these will count towards the minimum and maximum message count.
     * Duplicate snowflakes will only be counted once for these limits.
     *
     * This endpoint will not delete messages older than 2 weeks, and will fail if
     * any message provided is older than that. An endpoint will be added in the
     * future to prune messages older than 2 weeks from a channel.
     */
    @POST("channels/{channel-id}/message/bulk-delete")
    @FormUrlEncoded
    fun deleteMessageBulk(@Path("channel-id") channelId: String, @Field("messages") vararg messageId: String): Call<Unit>

    /**
     * Edit the channel permission overwrites for a user or role in a channel.
     * Only usable for guild channels. Requires the 'MANAGE_ROLES' permission.
     * Returns a 204 empty response on success. For more information about permissions, see
     * [permissions](https://discordapp.com/developers/docs/topics/permissions#permissions).
     *
     * Params
     * * allow  integer     the bitwise value of all allowed permissions
     * * deny   integer     the bitwise value of all disallowed permissions
     * * type   string      "member" for a user or "role" for a role
     */
    @PUT("channels/{channel-id}/permissions/{overwrite-id}")
    @FormUrlEncoded
    fun editChannelPermissions(@Path("channel-id") channelId: String, @Path("overwrite-id") overwriteId: String, @FieldMap params: Map<String, String>): Call<Unit>

    /**
     * Returns a list of [Invite] objects (with [InviteMetadata]) for the channel.
     * Only usable for guild channels. Requires the 'MANAGE_CHANNELS' permission.
     */
    @GET("channels/{channel-id}/invites")
    fun getChannelInvites(@Path("channel-id") channelId: String): Call<List<Invite>>

    /**
     * Create a new invite object for the channel. Only usable for guild channels.
     * Requires the CREATE_INSTANT_INVITE permission.
     *
     * All JSON paramaters for this route are optional, however the request body is not.
     * If you are not sending any fields, you still have to send an empty JSON object ({}).
     * Returns an [Invite] object.
     */
    @POST("channels/{channel-id}/invites")
    fun createChannelInvite(@Path("channel-id") channelId: String, @Body creatableInvite: InviteBuilder): Call<Invite>

    /**
     * Delete a channel permission overwrite for a user or role in a channel.
     * Only usable for guild channels.
     *
     * Requires the 'MANAGE_ROLES' permission.
     *
     * Returns a 204 empty response on success. For more information about permissions, see permissions
     */
    @DELETE("channel/{channel-id}/permissions/{overwrite-id}")
    fun deleteChannelPermission(@Path("channel-id") channelId: String, @Path("overwrite-id") overwriteId: String): Call<Unit>

    /**
     * Post a typing indicator for the specified channel. Generally bots should not implement this route.
     * However, if a bot is responding to a command and expects the computation to take a few seconds,
     * this endpoint may be called to let the user know that the bot is processing their message.
     *
     * Returns a 204 empty response on success. Fires a Typing Start Gateway event.
     */
    @POST("channels/{channel-id}/typing")
    fun triggerTypingIndicator(@Path("channel-id") channelId: String): Call<Unit>

    /**
     * Returns all pinned messages in the channel as an array of [Message] objects.
     */
    @GET("channel/{channel-id}/pins")
    fun getPinnedMessages(@Path("channel-id") channelId: String): Call<List<Message>>

    /**
     * Pin a message in a channel. Requires the 'MANAGE_MESSAGES' permission. Returns a 204 empty response on success.
     */
    @PUT("channels/{channel-id}/pins/{message-id}")
    fun addPinnedMessage(@Path("channel-id") channelId: String, @Path("message-id") messageId: String): Call<Unit>

    /**
     * Deletes a message in a channel. Requires the 'MANAGE_MESSAGES' permission. Returns a 204 empty response on success.
     */
    @DELETE("channels/{channel-id}/pins/{message-id}")
    fun deletePinnedMessage(@Path("channel-id") channelId: String, @Path("message-id") messageId: String): Call<Unit>


    /**
     * Adds a recipient to a Group DM using their access token
     *
     * @param accessToken access token of a user that has granted your app the gdm.join scope
     * @param nickname nickname of the user being added
     */
    @PUT("channels/{channel-id}/recipients/{user-id}")
    @FormUrlEncoded
    fun addGroupDMRecipient(@Path("channel-id") channelId: String, @Path("user-id") userId: String, @Field("access_token") accessToken: String, @Field("nick") nickname: String): Call<Unit>

    /**
     * Removes a recipient from a Group DM
     */
    @DELETE("channels/{channel-id}/recipients/{user-id}")
    fun removeGroupDMRecipient(@Path("channel-id") channelId: String, @Path("user-id") userId: String): Call<Unit>
//------------------------------------------------------Channel--------------------------------------------------

//-------------------------------------------------------User----------------------------------------------------
    /**
     * Returns a [User] object for a given user ID.
     */
    @GET("users/{user-id}")
    fun getUser(@Path("user-id") userId: String): Call<User>


    /**
     * Returns the [User] object of the requester's account. For OAuth2, this requires the identify scope,
     * which will return the object without an email, and optionally the email scope,
     * which returns the object with an email.
     */
    @GET("users/@me")
    fun getUser(): Call<User>

    /**
     * Modify the requester's [User] account settings. Returns a [User] object on success.
     *
     * @param imageType image type header which is passed along the base 64 encoded image data.
     * Implementations should handle that the parameter would be either image/gif, image/jpeg or image/png.
     * @param userBuilder builder which contains user data that's going to overwrite the current user's data
     */
    @PATCH("users/@me")
    fun modifyCurrentUser(@Header("Content-Type") imageType: String, @Body userBuilder: UserBuilder): Call<User>


    /**
     * Modify the requester's [User] account settings. Returns a [User] object on success.
     *
     * @param userBuilder builder which contains user data that's going to overwrite the current user's data
     */
    @PATCH("users/@me")
    fun modifyCurrentUser(@Body userBuilder: UserBuilder): Call<User>

    /**
     * Create a new DM channel with a user. Returns a DM channel object.
     */
    @POST("users/@me/channels")
    fun createChannel(@Body channelBuilder: PrivateChannelBuilder): Call<Channel>

    /**
     * Create a new group DM channel with multiple users. Returns a DM channel object.
     */
    @POST("users/@me/channels")
    fun createGroupChannel(@Body channelBuilder: PrivateGroupChannelBuilder): Call<Channel>

    @GET("users/@me/connections")
    fun getUserConnections(): Call<List<Connection>>

//-------------------------------------------------------User----------------------------------------------------


//------------------------------------------------------Webhook--------------------------------------------------

    /**
     * Create a new webhook. Returns a webhook object on success.
     */
    @POST("channels/{channel-id}/webhooks")
    fun createWebHook(@Header("Content-Type") contentType: String, @Path("channel-id") channelId: String, @Body builder: WebHookBuilder): Call<WebHook>

    /**
     * Returns a list of channel [WebHook] objects.
     */
    @GET("channels/{channel-id}/webhooks")
    fun getWebhooksForChannel(@Path("channel-id") channelId: String): Call<List<WebHook>>

    /**
     * Returns a list of guild [WebHook] objects.
     */
    @GET("guilds/{guild-id}/webhooks")
    fun getWebhooksForGuild(@Path("guild-id") guildId: String): Call<List<WebHook>>

    /**
     * Returns a particular [WebHook] object for the given id.
     */
    @GET("webhooks/{webhook-id}")
    fun getWebhook(@Path("webhook-id") webhookId: String): Call<List<WebHook>>

    /**
     * Modifies a [WebHook]. Returns the updated [WebHook] object on success. Note. All parameters are optional in builder.
     */
    @PATCH("webhooks/{webhook-id}")
    fun modifyWebhook(@Header("Content-Type") contentType: String, @Path("webhook-id") webhookId: String, @Body builder: WebHookBuilder): Call<WebHook>

    /**
     * Delete a [WebHook] permanently. User must be owner. Returns a 204 NO CONTENT response on success.
     */
    @DELETE("webhooks/{webhook-id}")
    fun deleteWebhook(@Path("webhook-id") webhookId: String): Call<Unit>

//---------------------------------------------------------------------------------------------------------------

//------------------------------------------------------Emoji----------------------------------------------------

    /**
     * Returns a list of [Emoji] objects for given guild
     */
    @GET("guilds/{guild-id}/emojis")
    fun listGuildEmojis(@Path("guild-id") guildId: String): Call<List<Emoji>>

    /**
     * Returns an [Emoji] object for that [Guild] with given ID.
     */
    @GET("guilds/{guild-id}/emojis/{emoji-id}")
    fun getGuildEmoji(@Path("guild-id") guildId: String, @Path("emoji-id") emojiId: String): Call<Emoji>

    /**
     * Create a new [Emoji] for the guild. Requires the 'MANAGE_EMOJIS' permission.
     *
     * Returns the new [Emoji] object on success. Fires a [EventGuildEmojisUpdate] Gateway event.
     */
    @POST("guilds/{guild-id}/emojis")
    fun createGuildEmoji(@Path("guild-id") guildId: String, @Body emojiBuilder: EmojiBuilder): Call<Emoji>

    /**
     * Modify the given [Emoji]. Requires the 'MANAGE_EMOJIS' permission.
     *
     * Returns the updated [Emoji] object on success. Fires a [EventGuildEmojisUpdate] Gateway event.
     */
    @PATCH("guilds/{guild-id}/emojis/{emoji-id}")
    fun modifyGuildEmoji(@Path("guild-id") guildId: String, @Path("emoji-id") emojiId: String, @Body emojiBuilder: EmojiBuilder): Call<Emoji>

    /**
     * Delete the given [Emoji]. Requires the 'MANAGE_EMOJIS' permission.
     *
     * Returns 204 No Content on success. Fires a [EventGuildEmojisUpdate] Gateway event.
     */
    @DELETE("guilds/{guild-id}/emojis/{emoji-id}")
    fun deleteGuildEmoji(@Path("guild-id") guildId: String, @Path("emoji-id") emojiId: String): Call<Unit>

//-------------------------------------------------------------------------------------------------------------

//------------------------------------------------------Guild--------------------------------------------------

    /**
     * Returns the [Guild] object for the given id.
     */
    @GET("guilds/{guild-id}")
    fun getGuild(@Path("guild-id") guildId: String): Call<Guild>

    /**
     * Returns a list of guild [Channel] objects.
     */
    @GET("guilds/{guild-id}/channels")
    fun getGuildChannels(@Path("guild-id") guildId: String): Call<List<Channel>>

    /**
     * Returns a list of members for particular guild.
     *
     * @param limit an integer between 1 and 1000
     * @param after a snowflake of smallest user id, not included
     */
    @GET("guilds/{guild-id}/members")
    fun getGuildMembers(@Path("guild-id") guildId: String, @Query("limit") limit: Int, @Query("after") after: String): Call<List<Member>>

    /**
     * Returns a member object for the specified user.
     */
    @GET("guilds/{guild-id}/members/{user-id}")
    fun getGuildMember(@Path("guild-id") guildId: String, @Path("user-id") userId: String): Call<Member>

    /*@PATCH("guilds/{guild-id}/members/{user-id}")
    fun modifyMemberRoles(@Path("guild-id") guildId: String, @Path("user-id") userId: String, @Part)*/
}