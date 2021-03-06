package lt.saltyjuice.dragas.chatty.v3.discord

object Settings
{
    val API_VERSION = 6
    val URL_LINK: String = "https://github.com/Dragas/Chatty"
    val API_ENCODING = "json"
    val BASE_URL = "https://discordapp.com/api/v$API_VERSION/"
    val VERSION = 1
    val DISCORD_KEY_ENV = "DISCORD_KEY"
    val token = System.getenv(DISCORD_KEY_ENV) ?: throw NullPointerException("$DISCORD_KEY_ENV environmental variable must be present")
    val FIELD_LIMIT: Int = 25
    val MAX_MESSAGE_CONTENT_LENGTH: Int = 2000
    val MAX_EMBED_CONTENT_LENGTH: Int = 6000
    val USERNAME_MIN_LENGTH: Int = 2
    val USERNAME_MAX_LENGTH: Int = 32
    val INVALID_USERNAME_CHARACTERS: String = "@#;`"
    val INVALID_USERNAME_REGEX: Regex = Regex("[$INVALID_USERNAME_CHARACTERS]")
    val VALID_IMAGE_TYPES_RAW: String = "(gif|jpeg|png|jpg)"
    val VALID_IMAGE_TYPES: Regex = Regex(VALID_IMAGE_TYPES_RAW)
    val TYPING_DELAY = 10000L
    val MEMBER_THRESHOLD: Int = 250
}