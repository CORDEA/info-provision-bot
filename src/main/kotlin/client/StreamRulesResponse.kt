package client

import kotlinx.serialization.Serializable

sealed class StreamRulesResponse {

    @Serializable
    data class Success(
        val data: List<StreamRuleResponse>
    ) : StreamRulesResponse()

    @Serializable
    data class Error(
        val errors: List<StreamRuleErrorResponse>
    ) : StreamRulesResponse()
}

@Serializable
data class StreamRuleResponse(
    val value: String,
    val tag: String = "",
    val id: String
)

@Serializable
data class StreamRuleErrorResponse(
    val value: String,
    val id: String,
    val title: String,
    val type: String
)
